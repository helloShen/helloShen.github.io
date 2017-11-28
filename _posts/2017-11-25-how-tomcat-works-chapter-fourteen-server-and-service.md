---
layout: post
title: "How Tomcat Works - Chapter 14 - Server & Service"
date: 2017-11-25 22:52:42
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["server","service"]
description: >
---

### ”服务器-服务” 架构
`com.ciaoshen.howtomcatworks.ex14.startup.Bootstrap`的下面几行代码很好地概括了”服务器-服务” 架构的特性，
1. 一个服务器可以添加多项服务
2. 一项服务包含一个容器组件和多个连接器组件
```java
// 前面若干代码省略。。。主要构造一系列子容器

// 最顶级的容器
Engine engine = new StandardEngine();
engine.addChild(host);
engine.setDefaultHost("localhost");

// 一项服务
Service service = new StandardService();
service.setName("Stand-alone Service");

// 一个服务器
Server server = new StandardServer();

// 往服务器里注册一项服务
server.addService(service);

// 往服务里绑定一个容器和多个连接器（也可以是1个）
service.addConnector(connector);
service.setContainer(engine);
```

`Server`类实现了`Lifecycle`生命周期接口的4个主要方法：
* initialize()
* start()
* await()
* stop()

`com.ciaoshen.howtomcatworks.ex14.startup.Bootstrap`的下面几行代码很好地体现了这个过程：
```java
// Start the new server
if (server instanceof Lifecycle) {
  try {
    server.initialize();            // 初始化
    ((Lifecycle) server).start();   // 运行
    server.await();                 // 挂起
    // the program waits until the await method returns,
    // i.e. until a shutdown command is received.
  }
  catch (LifecycleException e) {
    e.printStackTrace(System.out);
  }
}
// Shut down the server
if (server instanceof Lifecycle) {
  try {
    ((Lifecycle) server).stop();    // 关闭
  }
  catch (LifecycleException e) {
    e.printStackTrace(System.out);
  }
}
```

注意，
> 服务器线程只是起到一个引导的作用。顺利启动连接器线程之后立即被挂起，在8005(默认)端口等待关闭信号。

主线程在启动（`start()`方法）了某项服务之后，连接器线程被启动，在`8080`端口（默认端口）上等待HTTP请求。此时服务器的使命已经完成，调用`await()`方法在`8005`(默认)端口等待关闭信号。一旦收到关闭信号，立即调用`stop()`方法关闭服务器。
