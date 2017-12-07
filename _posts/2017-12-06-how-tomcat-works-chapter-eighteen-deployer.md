---
layout: post
title: "How Tomcat Works - Chapter 18 - Deployer"
date: 2017-12-06 18:14:55
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["deployer"]
description: >
---

### 部署器是做什么的？
部署器和一个Host实例关联。用来为Host实例安装Context子容器实例，并添加到Host容器中，并随Host容器启动Context子容器。

完成这些工作的代码，在前几章的应用中并不陌生，在`Bootstrap.java`文件中，总能看到下面的代码，
```java
Context context = new StandardContext();
// StandardContext's start method adds a default mapper
context.setPath("/app14");
context.setDocBase("app14");

context.addChild(wrapper1);
context.addChild(wrapper2);

LifecycleListener listener = new SimpleContextConfig();
((Lifecycle) context).addLifecycleListener(listener);

Host host = new StandardHost();
host.addChild(context);
host.setName("localhost");
host.setAppBase("webapps");

// ... code omitted
// ... code omitted
```

但很显然Tomcat作者McClanahan又用一个几千行代码的大模块比较健壮地把这几十行代码的工作给做了。

### HostConfig-Deployer架构
部署器主要由两部分组成一起工作。简单讲就是`HostConfig`实现了`LifecycleListener`生命周期监听器接口，`StandardHost`容器通过`START`事件触发`HostConfig`监听器运行，转而再调用`Deployer`类的`install()`函数执行实地部署动作。具体调用链如下，
```
org.apache.catalina.core.StandardHost#start()
                    |
                    | 触发
                    |
            START生命周期事件
                    |
                    | 激活
                    |
org.apache.catalina.startup.HostConfig生命周期监听器
                    |
                    | 启动
                    |
org.apache.catalina.startup.HostConfig#lifecycleEvent()
                    |
                    | 调用
                    |
org.apache.catalina.startup.HostConfig#start()
                    |
                    | 调用
                    |
org.apache.catalina.startup.HostConfig#deployApps()
                    |
                    | 调用
                    |
org.apache.catalina.startup.HostConfig#deployDescriptors()  // 负责部署XML描述符
org.apache.catalina.startup.HostConfig#deployWARs()         // 负责部署WAR文档
org.apache.catalina.startup.HostConfig#deployDirectories()  // 负责部署直接目录
                    |
                    | 调用
                    |
org.apache.catalina.core.StandardHost#install() // org.apache.catalina.Deployer接口
                    |
                    | 代理模式
                    |
org.apache.catalina.core.StandardHostDeployer#install() // 也实现了org.apache.catalina.Deployer接口
```
其中，`StandardHost`类本身实现了`Deployer`接口，但它自己没有包含实际部署的逻辑代码，而是继续代理（代理模式）给了另一个辅助类`StandardHostDeployer`类，后者同样实现了`Deployer`接口。


### 一些重要的默认路径
Tomcat 4中的部署描述符XML文件（比如admin.xml和manager.xml)归档在，
> CATALINA_HOME/webapps

Tomcat 5中部署描述符XML文件（比如admin.xml和manager.xml）归档在，
> CATALINA_HOME/server/webapps

WAR文件归档在，
> CATALINA_HOME/webapps

也可以直接将Web应用程序的整个目录复制到，
> CATALINA_HOME/webapps


### 关于Manager和Admin
本章提到了会通过解析XML部署描述符`CATALINA_HOME/server/webapps/manager.xml`和`CATALINA_HOME/server/webapps/admin.xml`的方式部署Manager和Admin应用程序。关于这两个应用程序的具体作用会在第19章介绍。
