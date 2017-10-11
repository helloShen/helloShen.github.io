---
layout: post
title: "How Tomcat Works - Chapter 6 - Lifecycle"
date: 2017-10-10 20:59:19
author: "Wei SHEN"
categories: ["java","how tomcat works"]
tags: ["lifecycle"]
description: >
---

### 草稿
* 实现了`Lifecycle`接口的组件中可以注册多个事件监听器来对发生在该组件上的某些事件进行监听。当某个事件发生时，相应的事件监听器会收到通知。
* `LifecycleListener`类的实例（监听器）监听到相关事件发生时，会调用`lifecycleEvent(LifecycleEvent)`方法。监听的事件就通过参数绑定。
* 事件`Event`的基类都是`java.util.EventObject`。构造事件需要绑定`Object`。叫做事件在“某个对象”上发生。
* `LifecycleEvent`类的实例绑定的对象都是`Lifecycle`类实例。所以我们说：在某个生命周期组件上发生的生命周期事件。
* 一共有6种不同的事件。分别是3种启动事件：`BEFORE_START_EVENT`,`START_EVENT`,`AFTER_START_EVENT`，以及3中关闭事件： `BEFORE_STOP_EVENT`,`SSTOP_EVENT`,`AFTER_STOP_EVENT`.
* 具体是由`LifecycleSupport`工具帮助`Lifecycle`组件管理`LifecycleListener`监听器，并由`fireLifecycleEvent(String,Object)`方法触发相应的`LifecycleEvent`事件。
* 书上的例子并没有在多线程的环境中运行连接器和容器，但因为用户可以直接为`Lifecycle`组件注册或注销监听器，在并发条件下，有可能构成竟态资源，所以涉及到监听器的操作都是临界区，需要用`synchronized`互斥锁保护。

### 整理线程头绪
首先假设我们只有一个`HttpConnector`实例。它拥有自己的线程。它用`accept()`方法接受客户端发来的请求，创建一个`Socket`实例的引用，并且通过`HttpProcessor#assign()`方法把这个引用传给`HttpProcessor`对象。
```java
public final class HttpConnector implements Connector, Lifecycle, Runnable {

    // ... 省略

    public void run() {
        // Loop until we receive a shutdown command
        while (!stopped) {
            // Accept the next incoming connection from the server socket
            Socket socket = null;
            try {
                socket = serverSocket.accept();

                // ... 省略

            } catch (Exception ioe) {
                    // ... 省略
            }

            // Hand this socket off to an appropriate processor
            HttpProcessor processor = createProcessor(); // 到HttpProcessor对象池里拿一个HttpProcessor实例的引用

            // ... 省略

            processor.assign(socket);
        }
    }
}
```

`HttpConnector`的`createProcessor()`方法并不会每次都实际创建一个新的`HttpProcessor`实例。`HttpProcessor`对象池。它维护着一个`HttpProcessor`对象池，其中每个`HttpProcessor`也都有自己的线程。

```java
public final class HttpConnector implements Connector, Lifecycle, Runnable {

    /**
     * HttpProcessor对象池
     * The set of processors that have been created but are not currently
     * being used to process a request.
     */
    private Stack processors = new Stack();

    // ... 省略

    private HttpProcessor createProcessor() {

        synchronized (processors) {
            if (processors.size() > 0) {
                return ((HttpProcessor) processors.pop());
            }
            if ((maxProcessors > 0) && (curProcessors < maxProcessors)) {
                return (newProcessor());
            } else {
                if (maxProcessors < 0) {
                    return (newProcessor());
                } else {
                    return (null);
                }
            }
        }

    }
}
```
下面是`HttpProcessor`的`run()`方法，它和`HttpConnector`是通过`await()`和`assign()`互相协作，
```java
final class HttpProcessor implements Lifecycle, Runnable {

    // many omitted code ...

    public void run() {

        // Process requests until we receive a shutdown signal
        while (!stopped) {

            // Wait for the next socket to be assigned
            Socket socket = await();
            if (socket == null)
                continue;

            // Process the request from this socket
            try {
                process(socket);
            } catch (Throwable t) {
                log("process.invoke", t);
            }

            // Finish up this request
            connector.recycle(this);

        }

        // Tell threadStop() we have shut ourselves down successfully
        synchronized (threadSync) {
            threadSync.notifyAll();
        }

    }
}
```
下面`assign()`和`await()`方法的代码清单，他们是一对典型的“生产者-消费者”模式。
```java
final class HttpProcessor implements Lifecycle, Runnable {

    // many omitted code ...

    synchronized void assign(Socket socket) {

        // Wait for the Processor to get the previous Socket
        while (available) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        // Store the newly available Socket and notify our thread
        this.socket = socket;
        available = true;
        notifyAll();

        if ((debug >= 1) && (socket != null))
            log(" An incoming request is being assigned");

    }
    private synchronized Socket await() {

        // Wait for the Connector to provide a new Socket
        while (!available) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        // Notify the Connector that we have received this Socket
        Socket socket = this.socket;
        available = false;
        notifyAll();

        if ((debug >= 1) && (socket != null))
            log("  The incoming request has been awaited");

        return (socket);

    }
}
```

每个`HttpProcessor`线程解析完HTTP请求，构建完`Request`和`Response`，会调用和`Connector`绑定的`Container`。

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
                connector.getContainer().invoke(request, response);
            }
        } catch (ServletException e) {
            // ... omitted code
        }

        // ... omitted code    
    }
}
```

到这里可以看出2个很重要的事实：
> 容器是运行在每个HttpProcessor的线程上的。

> 容器实例的引用是绑定到连接器上的。

所以，可能同时有多个线程访问同一个容器对象，
> 多个HttpProcessor线程共享公用的几个容器。每个容器都构成“竞态资源”。所以容器需要互斥锁保护。
