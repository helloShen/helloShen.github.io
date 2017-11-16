---
layout: post
title: "How Tomcat Works - Chapter 11 & 12 - Standard Wrapper & Context"
date: 2017-11-12 17:02:32
author: "Wei SHEN"
categories: ["web","java","how tomcat works"]
tags: ["container"]
description: >
---

### `SingleThreadModel`接口
> 实现`SingleThreadModel`接口的Servlet不一定就是线程安全的。只是保证绝不会有两个线程同时执行该servlet实例的service()方法。

实现这一点有两个途径，
1. 要么所有请求共享一个此Servlet的受互斥锁保护的单例。
2. 要么维护一个此Servlet的实例池，然后为每个新请求分配一个空闲的实例。

Catalina采用了第2种实例池的方案，原因是因为第一种方案效率太低。当某个线程占用这个Servlet单例的时候，其他线程必须阻塞等待。线程池的方案就可以同时响应多个请求。代价只是一些分配实例的策略代码。

实际分配Servlet实例的过程封装在`StandardWrapper#allocate()`函数里。具体策略如下，
1. 如果此Servlet没有实现`SingleThreadModel`接口，只维护一个此Servlet的单例(`instance`字段)。只有在第一次请求该Servlet实例是才会调用类加载器加载。
2. 如果此Servlet实现了`SingleThreadModel`接口，则维护一个有数量上限(`maxInstances`字段)的实例池。

没有实现`SingleThreadModel`接口的Servlet为什么可以用单例模式呢？因为在这个唯一的实例上没有互斥锁。它可以同时响应多个线程的请求。因此没有实现`SingleThreadModel`的Servlet类的程序员必须表征该Servlet类的`service()`方法在多线程环境中是线程安全的。所以尽管Servlet实例本身没有加互斥锁，但它访问的共享资源上应该要加上。

也正是因为对实现了`SingleThreadModel`接口的Servlet采用实例池的方案。尽管单个Servlet实例的`service()`函数同一时刻只有一个线程在执行，但同一时刻可能有多个此Servlet类的实例的`service()`函数在运行。如果此Servlet实例要访问一些诸如静态变量之类的共享资源，如果程序员没有注意保护，仍旧是线程不安全的。

```java
/**
 * Allocate an initialized instance of this Servlet that is ready to have
 * its <code>service()</code> method called.  If the servlet class does
 * not implement <code>SingleThreadModel</code>, the (only) initialized
 * instance may be returned immediately.  If the servlet class implements
 * <code>SingleThreadModel</code>, the Wrapper implementation must ensure
 * that this instance is not allocated again until it is deallocated by a
 * call to <code>deallocate()</code>.
 *
 * @exception ServletException if the servlet init() method threw
 *  an exception
 * @exception ServletException if a loading error occurs
 */
public Servlet allocate() throws ServletException {

    if (debug >= 1)
        log("Allocating an instance");

    // If we are currently unloading this servlet, throw an exception
    if (unloading)
        throw new ServletException
          (sm.getString("standardWrapper.unloading", getName()));

    // If not SingleThreadedModel, return the same instance every time
    if (!singleThreadModel) {

        // Load and initialize our instance if necessary
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    try {
                        instance = loadServlet();
                    } catch (ServletException e) {
                        throw e;
                    } catch (Throwable e) {
                        throw new ServletException
                            (sm.getString("standardWrapper.allocate"), e);
                    }
                }
            }
        }

        if (!singleThreadModel) {
            if (debug >= 2)
                log("  Returning non-STM instance");
            countAllocated++;
            return (instance);
        }

    }

    // if implements SingleThreadModel interface, maintains an instancePool
    synchronized (instancePool) {

        while (countAllocated >= nInstances) {
            // Allocate a new instance if possible, or else wait
            if (nInstances < maxInstances) {
                try {
                    instancePool.push(loadServlet());
                    nInstances++;
                } catch (ServletException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new ServletException
                        (sm.getString("standardWrapper.allocate"), e);
                }
            } else {
                try {
                    instancePool.wait();
                } catch (InterruptedException e) {
                    ;
                }
            }
        }
        if (debug >= 2)
            log("  Returning allocated STM instance");
        countAllocated++;
        return (Servlet) instancePool.pop();

    }

}
```

### Servlet的构造（构造函数）和初始化（`init()`函数）的区别？

![init](/images/how-tomcat-works-chapter-eleven-standardwrapper/init.png)

`StandardWrapper`容器的`loadServlet()`函数动态载入Servlet类后，会调用该Servlet实例的`init()`函数初始化。那么既然有构造函数了，有什么事不能在构造函数里做，非要再加一个`init()`函数来初始化呢？

答案就是，
> 构造器主要用来设置和Servlet自身的属性。而init()函数主要用来配置这个Servlet所部署容器环境的属性。因为同一个Servlet类可能被多个不同的容器加载，这些环境变量不应该属于构造函数的一部分。

比如供用户查找Servlet的`servletName`是在`WEB-INF/web.xml`文件里的`servlet-name`项配置。同一个Servlet类在不同的容器中，可以配置为不同的名字。
```xml
<servlet>
  <servlet-name>Modern</servlet-name>
  <servlet-class>ModernServlet</servlet-class>
</servlet>
<servlet>
  <servlet-name>Primitive</servlet-name>
  <servlet-class>PrimitiveServlet</servlet-class>
</servlet>
```

这也就正好解释了为什么`init()`方法需要传入一个`javax.servlet.ServletConfig`实例作为参数。而`StandardWrapper`类本身就实现了`ServletConfig`接口。其实就是在`StandardWrapper`容器中添加了几个提供环境容器属性的方法，就不需要另外定义一个类了。当Servlet需要配置容器环境信息的时候，将容器的`ServletConfig`接口展示给Servlet就可以了。为了避免程序员把拿到的`ServletConfig`实例的引用强制转型回`StandardWrapper`而滥用其他方法，传给`init()`方法的是一个外观类`org.apache.catalina.core.StandardWrapperFacade`外观类的实例。


### Valve和Filter的区别？
既然Tomcat在实际执行某个Servlet实例（基础阀中）之前，会先运行几个前置阀，那为什么还需要和Servlet关联的过滤器呢？

Tomcat官方文档对`Valve`的定义如下，
> A Valve element represents a component that will be inserted into the request processing pipeline for the associated Catalina container.

实际上`Filter`的定义和`Valve`可以说如出一辙。他们真正的区别在于：
> `org.apache.catalina.Valve`只是在Tomcat框架下的特殊概念。但Servlet框架被广泛运用在除了Tomcat之外的其他类似服务器或者容器框架中。比如说Jetty，如果用Valve接口为Servlet做前置增强，就无法和Jetty兼容，因为Jetty框架不兼容Valve接口。但`javax.servlet.Filter`是Servlet框架的一部分，所有支持Servlet框架的服务器和容器都必须支持Filter，所以Filter普适性更好。

所以一般Servlet的前置增强都用Filter，因为可以兼容所有支持Servlet框架的服务器和容器。Valve只有Tomcat框架本身的代码才会用到。

### 用一个监听器作为配置器
`ApplicationFilterConfig`实现了`Listener`接口。

### Wrapper里是一个`ApplicationFilterChain`，Context里是`Pipeline`

### Tomcat 5用`ContainerBackgroundProcessor`类用一个后台线程帮助载入器和Session管理器执行任务
它的`processChildren()`方法会调用自身容器的`backgroundProgress()`方法，然后递归调用每个子容器的`processChildren()`。这样可以确保每个子容器的`backgroundProgress()`方法都被调用。
