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

### 本章应用

#### `StandardEngineMapper`查找Host子容器
`org.apache.catalina.core.StandardEngineMapper`的`map()`函数在查找Host子容器的时候，先查找URL里的"Server Name"。
```java
// code omitted ...

// Extract the requested server name
String server = request.getRequest().getServerName();

// code omitted ...

Host host = (Host) engine.findChild(server);
```

比如我拿到的URL如下，
> localhost:8080/app13/Primitive

解析出的"Server Name"就是`:`冒号前的"localhost"。因为我们的Host容器的名字就是叫"localhost"，所以服务器正确查找到了目标服务器，
```java
host.setName("localhost");
```

但如果"Server Name"匹配失败，比如拿到URL的是，
> 127.0.0.1:8080/app13/Primitive

因为这里解析出来的"Server Name"变成了`127.0.0.1`。应该没有对应的Host容器。但因为我们把"localhost"这个Host容器设置成了Engine默认的子容器，服务器也能正确返回页面。
```java
engine.setDefaultHost("localhost");
```
`org.apache.catalina.core.StandardEngineMapper`的`map()`函数的查找策略的下一条就是如果"Server Name"匹配失败，就启用默认Host子容器。
```java
// Trying the "default" host if any
if (host == null) {
    if (debug >= 2)
        engine.log(" Trying the default host");
    host = (Host) engine.findChild(engine.getDefaultHost());
}
```

对应于`127.0.0.1:8080/app13/Primitive`的URL，实际收到的消息头如下，
```bash
host:: 127.0.0.1:8080
connection:: keep-alive
accept:: */*
user-agent:: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/604.3.5 (KHTML, like Gecko) Version/11.0.1 Safari/604.3.5
accept-language:: en-us
referer:: http://127.0.0.1:8080/app13/Primitive
accept-encoding:: gzip, deflate
```
