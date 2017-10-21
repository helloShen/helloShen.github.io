---
layout: post
title: "How Tomcat Works - Chapter 5 - Container"
date: 2017-10-16 22:21:10
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["container"]
description: >
---

### 摘要
第3，4章详细讲了连接器，这章主要讲另一个重要模块“容器”。连接器的任务就是接受一个TCP连接请求，然后把通过各种协议传输进来的消息解析成`Request`，然后再创建一个`Response`，把这一对I/O传递给“容器”，连接器的使命结束。剩下的任务就由“容器”完成: 根据`Request`中的信息，加载对应的`Servlet`程序，然后把处理的结果通过传进来的`Response`返回给客户端。

Tomcat用了很多设计模式，容器写得很复杂。理解的时候要抓住3条线索，事情就会变得很简单。
1. 第一条主线就是`Container -> Pipeline -> ValveContext -> Valve`这个装饰器（或者说过滤器）模式。每个容器都会把要完成的几个步骤分别封装在一系列“阀”中，并最终串联成一个“管道”。
2. Catalina中有4种规模的Servlet容器，从小到大依次为：`Wrapper`,`Context`,`Host`,`Engine`。每一层对应到上一层都是一对多的包含关系。本章只介绍了`Wrapper`和`Context`。
3. `Loader`这条线。因为不管封装怎么复杂，“容器”最终的任务还是要去加载Servlet程序，也就是".class"类文件。`Loader`就是实际用类加载器加载类文件的组件。

Tomcat把容器封装地这么复杂是有它正当理由的：就是为了实现让管理员通过编辑配置文件`server.xml`来决定使用哪些容器，容器需要完成哪些任务。封装好的各级容器，以及阀门就可以实现像搭积木一样的不同功能间的自由组合。

### “管道”和“阀”
用大白话说，“管道”就是一个容器要完成的一系列任务的集合。一个“阀”就代表其中一个具体的任务。总得来说容器的任务就是要逐一执行每一个任务，就像水要通过管道，需要逐一通过中间的每一个阀门。
![pipeline-valve-1](/images/how-tomcat-works-chapter-five/pipeline-valve-1.png)
一个Servlet容器可以有一条管道。每条管道中必须有一个 **"基础阀"**，然后可以添加任意数量的额外阀。基础阀总是最后一个执行。一般在基础阀中执行容器最重要的任务，比如加载Servlet程序，额外的阀作为前置增强。

Catalina有一个`Valve`类，和一个`Pipeline`类。阀门集合是`Pipeline`类型中的一个`Valve[]`数组字段。这很形象，很容易理解。基础阀`basic`是单独列出来的。
```java
public class SimplePipeline implements Pipeline {

  // The basic Valve (if any) associated with this Pipeline.
  protected Valve basic = null;
  // The Container with which this Pipeline is associated.
  protected Container container = null;
  // the array of Valves
  protected Valve valves[] = new Valve[0];

  // rest of the code omitted

}
```
所以依次执行阀的代码可以写得很简单，
```java
// invoke each valve in the pipeline
for (Valve valve : valves) {
    valve.invoke(...);
}
// invoke basic pipeline in the end
basic.invoke(...);
```
但Tomcat用了一个迭代器`org.apache.catalina.ValveContext`来遍历阀，把框架又复杂化了。
![pipeline-valve-2](/images/how-tomcat-works-chapter-five/pipeline-valve-2.png)

具体的实现上和`Collections`框架的`Iterator`很像，`SimplePipelineValveContext`是`Pipeline`的一个内部类，实现了`ValveContext`接口。

内部有一个`stage`字段记录了具体执行到了哪一个阀。`invokeNext()`函数每次都执行下一个阀，然后将`stage`字段自增1.
```java
protected class SimplePipelineValveContext implements ValveContext {

  protected int stage = 0;

  public String getInfo() {
    return null;
  }

  public void invokeNext(Request request, Response response)
    throws IOException, ServletException {
    int subscript = stage;
    stage = stage + 1; // 迭代器自增1
    // Invoke the requested Valve for the current request thread
    if (subscript < valves.length) {
      valves[subscript].invoke(request, response, this);
    }
    else if ((subscript == valves.length) && (basic != null)) {
      basic.invoke(request, response, this);
    }
    else {
      throw new ServletException("No valve");
    }
  }
}
```
设计的关键在于每个`Valve`都保留了`ValveContext`实例的引用，只有在`Pipeline`类里第一次调用`invokeNext()`，执行第一个`Valve`，之后都是在`Valve`里继续调用`ValveContext#invokeNext()`。
```java
public class ClientIPLoggerValve implements Valve, Contained {

  // omitted ...

  public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {

    // Pass this request on to the next valve in our pipeline
    valveContext.invokeNext(request, response);

    // remainder omitted ...
  }
}
```

### “容器”和“管道”的关系
`Container`接口和`Pipeline`接口的关系有点“别扭”。首先本章的`SimpleContext`类同时实现了`Context`(`Context`继承自`Container`接口)和`Pipeline`两个接口。这样容器`SimpleContext`本身也是也是`Pipeline`实例。但同时`SimpleContext`类中又包含了一个`Pipeline`成员字段，在`SimpleContext`实例构造之前，`this`引用已经被用来构造`Pipeline`接口实例。
```java
public class SimpleContext implements Context, Pipeline { // 本身实现了Pipeline接口
    public SimpleContext() {
      pipeline.setBasic(new SimpleContextValve());
    }
    protected HashMap children = new HashMap();
    protected Loader loader = null;
    protected SimplePipeline pipeline = new SimplePipeline(this); // 又有个Pipeline成员字段，用this去构造
    protected HashMap servletMappings = new HashMap();
    protected Mapper mapper = null;
    protected HashMap mappers = new HashMap();
    private Container parent = null;
}
```
至于为什么要这样设计，先放一放。至少有一点很清楚，`Container`和`Pipeline`是一对一的关系，并且`Pipeline`是`Container`最重要的一个组件。

### `Engine`,`Host`,`Context`,`Wrapper`
Catalina中有4种不同规模的Servlet容器接口，每个层次和它的上层都是一对多的包含关系，他们都继承自`Container`接口，
1. Engine: 表示整个Catalina Servlet引擎
2. Host: 表示包含一个或多个Context的虚拟主机
3. Context: 表示一个Web应用程序，可以有多个Wrapper
4. Wrapper: 表示一个独立的Servlet

![engine-host-context-wrapper-1](/images/how-tomcat-works-chapter-five/engine-host-context-wrapper-1.png)

他们的实现都继承自抽象类`ContainerBase`。本章的演示代码`SimpleContext`和`SimpleWrapper`是一个简化版，部分代码就取自`ContainerBase`。并且，本章只讨论了`Context`和`Wrapper`，没有涉及`Engine`和`Host`，这部分内容留到以后的章节。

### `Wrapper`包装一个独立Servlet
`Wrapper`已经是最底层的容器，它专门用来加载某一个特定的Servlet程序。所以它内部有一个`Servlet instance`字段表示它绑定的Servlet程序。如果Servlet程序没有初始化，可以用`load()`函数初始化Servlet实例（初始化的工作由`loadServlet()`函数完成）。如果已经初始化了，调用`allocate()`函数会返回这个实例的引用。所以说起来，`Wrapper`是一个惰性加载的单例器。

### `Context`用映射器`Mapper`查找目标`Wrapper`
未完待续

### 追踪连接器调用容器`invoke()`以后的调用链
现在“容器”概念上的模型已经有了，每个“容器”都有一个条“管道”，管道里有多个阀。下面梳理一下`Bootstrap2`中当连接器调用了servlet容器的`invoke()`方法后，直到一个真正的Servlet的类文件被加载，具体的调用链。
```bash
SimpleContext#invoke()
|
+-> SimplePipeline#invoke()
    |
    +-> SimplePipeline.SimplePipelineValveContext#invokeNext()
        |
        +-> ClientIPLoggerValve#invoke()
            |
            +-> SimplePipeline.SimplePipelineValveContext#invokeNext()
                |
                +-> HeaderLoggerValve#invoke()
                    |
                    +-> SimplePipeline.SimplePipelineValveContext#invokeNext()
                        |
                        +-> SimpleContextValve.invoke()
                            |
                            +-> SimpleWrapper.invoke()
                                |
                                +-> SimplePipeline#invoke()
                                    |
                                    +-> SimplePipeline.SimplePipelineValveContext#invokeNext()
                                        |
                                        +-> SimpleWrapperValve#invoke()
                                            |
                                            +-> SimpleWrapper#allocate()
                                                |
                                                +-> SimpleWrapper#loadServlet()
                                                    |
                                                    +-> PrimitiveServlet.class loaded!
```
调用链很复杂，为了加载一个Servlet程序类文件，调用了十几次函数。简单描述这个过程就是：
1. 我有一个`Context`实例，调用它的`invoke()`
2. 它调用它内部`Pipeline`成员的`invoke()`
3. `Pipeline`调用迭代器`SimplePipelineValveContext`的`invokeNext()`
4. 我的`Context`容器实例里绑定了两个前置阀，迭代器调用1号阀的`invoke()`
5. 1号阀的`invoke()`完成以后回调用迭代器的`invokeNext()`
6. 迭代器的`invokeNext()`调用2号阀的`invoke()`
7. 2号阀的`invoke()`再回调迭代器的`invokeNext()`
8. 迭代器发现前置阀门执行完毕，最后调用基础阀`SimpleContextValve`的`invoke()`
9. 我的`Context`绑定了2个`Wrapper`实例，基础阀的`invoke()`调用映射器找到1号`Wrapper`容器，调用它的`invoke()`
10. 整个故事重来一遍，1号`Wrapper`继续调用它内部`Pipeline`的`invoke()`
11. `Pipeline`的`invoke()`调用迭代器的`invokeNext()`
12. 迭代器发现1号`Wrapper`没有前置阀，直接执行基础阀的`invoke()`
13. 1号`Wrapper`的基础阀`SimpleWrapperValve`的`invoke()`
14. 1号`Wrapper`的`allocate()`函数被调用
15. `allocate()`接着调用自己的`loadServlet()`函数加载`PrimitiveServlet.class`文件
