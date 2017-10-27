---
layout: post
title: "How Tomcat Works - Chapter 5 - Container"
date: 2017-10-16 22:21:10
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["container","wrapper","context"]
description: >
---

### 摘要
![overview](/images/how-tomcat-works-chapter-five/overview.png)

第3，4章详细讲了连接器，这章主要讲另一个重要模块“容器”。连接器的任务就是接受一个TCP连接请求，然后把通过各种协议传输进来的消息解析成`Request`，然后再创建一个`Response`，把这一对I/O传递给“容器”，连接器的使命结束。剩下的任务就由“容器”完成: 根据`Request`中的信息，加载对应的`Servlet`程序，然后把处理的结果通过传进来的`Response`返回给客户端。

Tomcat用了很多设计模式。理解容器要抓住3条线索，
1. 第一条主线就是`Container -> Pipeline -> ValveContext -> Valve`这个装饰器（或者说过滤器）模式。每个容器都会把要完成的几个步骤分别封装在一系列“阀”中，并最终串联成一个“管道”。
2. Catalina中有4种规模的Servlet容器，从小到大依次为：`Wrapper`,`Context`,`Host`,`Engine`。每一层对应到上一层都是一对多的包含关系。本章只介绍了`Wrapper`和`Context`。
3. `Loader`这条线。因为不管封装怎么复杂，“容器”最终的任务还是要去加载Servlet程序，也就是".class"类文件。`Loader`就是实际用类加载器加载类文件的组件。

Tomcat把容器封装地这么复杂是有它正当理由的：为了实现让管理员通过编辑配置文件`server.xml`来决定使用哪些容器，容器需要完成哪些任务。封装好的各级容器，以及阀门就可以实现像搭积木一样的不同功能间的自由组合。

### “管道”和“阀”
“管道”就是一个容器要完成的一系列任务的集合。一个“阀”就代表其中一个具体的任务。容器的任务就是要逐一执行每一个任务，就像水要通过管道，需要逐一通过中间的每一个阀门。
![pipeline-valve-1](/images/how-tomcat-works-chapter-five/pipeline-valve-1.png)
一个Servlet容器可以有一条管道。每条管道中必须有一个 **"基础阀"**，然后可以添加任意数量的额外阀。基础阀总是最后一个执行。一般在基础阀中执行容器最重要的任务，比如加载Servlet程序，额外的阀作为前置增强。

Catalina有一个`Valve`类，和一个`Pipeline`类。阀门集合是`Pipeline`类型中的一个`Valve[]`数组字段。基础阀`basic`是单独列出来的。
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
但Tomcat用了一个迭代器`org.apache.catalina.ValveContext`来遍历阀。
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

这是一个典型的 **装饰器模式**。
> 容器是管道的装饰器。

容器包装了管道，添加了像映射器，加载器这样的组件。但包装后的容器还是一个管道。还可以被其他的管道装饰器继续包装。这就是装饰器模式特有的结构。

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

#### `Wrapper`用到的`Loader`绑定在`Context`上
`loadServlet()`函数是实际加载Servlet类文件的地方。它要用到`Loader`。但这个`Loader`不是绑定在`Wrapper`上，而是它的上层容器`Context`。这样`Context`里的多个`Wrapper`就可以共享这个加载器。

```java
public Loader getLoader() {
  if (loader != null)
    return (loader);
  if (parent != null)
    return (parent.getLoader()); // Wrapper的parent字段代表它所在的Context
  return (null);
}
```

### `Context`用映射器`Mapper`查找目标`Wrapper`
`Context`包含多个`Wrapper`，每个Wrapper专门负责加载一个特定Servlet类文件。演示代码里，每个Wrapper都有一个`name`字段，和一个`servletClass`字段。`Bootstrp2`中给Context配置了两个Wrapper，分别叫`Primitive`和`Modern`。对应的Servlet类文件分别是：`com.ciaoshen.howtomcatworks.ex06.webroot.PrimitiveServlet`和`com.ciaoshen.howtomcatworks.ex06.webroot.ModernServlet`。

```java
Wrapper wrapper1 = new SimpleWrapper();
wrapper1.setName("Primitive");
// 必须是类的全具名
wrapper1.setServletClass("§");
Wrapper wrapper2 = new SimpleWrapper();
wrapper2.setName("Modern");
// 必须是类的全具名
wrapper2.setServletClass("com.ciaoshen.howtomcatworks.ex06.webroot.ModernServlet");

Context context = new SimpleContext();
context.addChild(wrapper1);
context.addChild(wrapper2);
```

映射的信息分别储存在`SimpleContext`的`servletMapping`和`children`两个字段里。比如说Request里传进来的URI是`127.0.0.1:8080/Primitive`，`Connector`解析以后找到指定Servlet的有效信息是`/Primitive`，通过`servletMapping`找到对应的Serlet名字叫`Primitive`。
```bash
                    Info from Request   Servlet Name
servletMapping -+-> "/Primitive"    -> "Primitive"
                |
                +-> "/Modern"       -> "Modern"
```
然后再到`children`映射里，通过`Primitive`这个名字，找到`com.ciaoshen.howtomcatworks.ex06.webroot.PrimitiveServlet`目标类文件。
```bash
                    Servlet Name        Servlet Class
children       -+-> "Primitive"    -> "com.ciaoshen.howtomcatworks.ex06.webroot.PrimitiveServlete"
                |
                +-> "Modern"       -> "com.ciaoshen.howtomcatworks.ex06.webroot.ModernServlet"
```
下面是`SimpleContext`中的相关代码片段。注意！映射器在Tomcat 5被其他映射方案所替代。
```java
public class SimpleContext implements Context, Pipeline {

  public SimpleContext() {
    pipeline.setBasic(new SimpleContextValve());
  }
  // 存放Servlet Name到Servlet程序的映射。
  // 比如拿到"Primitive"这个名字，
  // 找到叫这个名字的"com.ciaoshen.howtomcatworks.ex05.webroot.PrimitiveServlet"的类文件
  protected HashMap children = new HashMap();
  protected Loader loader = null;
  protected SimplePipeline pipeline = new SimplePipeline(this);
  // 存放URI最后指定servlet的部分，和Servlet Name之间的映射
  // 比如["/Primitive","Primitive"]
  // 从"127.0.0.1:8080/Primitive"切下"/Primitive"，映射到叫"Primitive"的Servlet程序
  protected HashMap servletMappings = new HashMap();
  protected Mapper mapper = null;
  protected HashMap mappers = new HashMap();
  private Container parent = null;

  // remainder omitted ...
}
```

### 关于`Loader`到第8章详细介绍
本章的`Loader`非常简单，没有实现自定义类加载器，直接用了`URLClassLoader`。关于类加载器的细节，留到第8章的时候详细介绍。
```java
public SimpleLoader() {
  try {
    URL[] urls = new URL[1];
    URLStreamHandler streamHandler = null;
    File classPath = new File(WEB_ROOT);
    String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString() ;
    urls[0] = new URL(null, repository, streamHandler);
    classLoader = new URLClassLoader(urls); // 直接用URLClassLoader
  }
  catch (IOException e) {
    System.out.println(e.toString() );
  }
  // remainder omitted ...
}
```
### 总览
总的来说每个容器都有一条管道，配上一些其他组件（比如映射器，加载器），每个管道里有几个前置阀和一个基础阀。每个`Context`容器里可以有多个`Wrapper`容器。`Wrapper`容器是最底层的容器，是实际调用Servlet类文件的地方。本章的例子里映射器和加载器都是和`Context`容器绑定的，`Wrapper`要用的时候，它所在的`Context`父级容器要。
![overview](/images/how-tomcat-works-chapter-five/overview.png)

### 追踪连接器调用容器`invoke()`以后的调用链
现在“容器”概念上的模型已经有了，下面梳理一下`Bootstrap2`中当连接器调用了servlet容器的`invoke()`方法后，直到一个真正的Servlet的类文件被加载，具体的调用链。
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

装饰器（或者说过滤器）模式有它的好处，但弊端就是调用链太长。

### 容器运行在每个`HttpProcessor`的线程上
通过第3章和第4章已经知道每个连接器是在自己独立的线程上工作。同时每个连接器都维护着一个`HttpProcessor`对象池，帮助它完成`Request`的解析。每个`HttpProcessor`都实现了`Runnable`接口，在自己的独立线程上工作。这样连接器把`Request`交到`HttpProcessor`手里以后就可以腾出手继续处理其他客户的连接请求。

现在把目光聚焦到连接器最终调用容器`invoke()`方法的地方，实际的调用者不是`Connector`，而是`HttpProcessor`的`process()`方法，
```java
final class HttpProcessor implements Lifecycle, Runnable {

    // ... many code omitted

    private void process(Socket socket) {

        // ... omitted code

        // Ask our Container to process this request
        try {
            ((HttpServletResponse) response).setHeader
                ("Date", FastHttpDateFormat.getCurrentDate());
            if (ok) {
                // 实际调用容器的代码
                connector.getContainer().invoke(request, response);
            }
        } catch (ServletException e) {
            // ... omitted code
        }

        // ... omitted code    
    }
}
```

这行调用代码，暴露出两个信息，
1. 容器是在`HttpProcessor`的线程上运行的。
2. 容器实例是和`Connector`关联的。

这就说明“容器”暴露在危险的环境当中，
> “容器”属于竞态资源。可能出现多个HttpProcessor线程同时访问某个容器的情况。

所以涉及到修改容器状态的方法，大多都加了`synchronized`互斥锁保护起来。
```java
public synchronized void addValve(Valve valve) {
  pipeline.addValve(valve);
}
```
```java
public void addServletMapping(String pattern, String name) {
  synchronized (servletMappings) {
    servletMappings.put(pattern, name);
  }
}
```
```java
public String findServletMapping(String pattern) {
  synchronized (servletMappings) {
    return ((String) servletMappings.get(pattern));
  }
}
```
```java
public void addMapper(Mapper mapper) {
  // this method is adopted from addMapper in ContainerBase
  // the first mapper added becomes the default mapper
  mapper.setContainer((Container) this);      // May throw IAE
  this.mapper = mapper;
  // 每个Context都有自己独立的线程，但Mapper可以多个容器共享。所以Mapper是竞态资源，Context不是。
  synchronized(mappers) {
    if (mappers.get(mapper.getProtocol()) != null)
      throw new IllegalArgumentException("addMapper:  Protocol '" +
        mapper.getProtocol() + "' is not unique");
    mapper.setContainer((Container) this);      // May throw IAE
    mappers.put(mapper.getProtocol(), mapper);
    if (mappers.size() == 1)
      this.mapper = mapper;
    else
      this.mapper = null;
  }
}
```
本身操作具备原子性的方法就没有加互斥锁，
```java
public void setLoader(Loader loader) {
  this.loader = loader;
}
```
