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

### 用一个监听器作为配置器
`ApplicationFilterConfig`实现了`Listener`接口。

### Wrapper里是一个`ApplicationFilterChain`，Context里是`Pipeline`

### Tomcat 5用`ContainerBackgroundProcessor`类用一个后台线程帮助载入器和Session管理器执行任务
它的`processChildren()`方法会调用自身容器的`backgroundProgress()`方法，然后递归调用每个子容器的`processChildren()`。这样可以确保每个子容器的`backgroundProgress()`方法都被调用。
