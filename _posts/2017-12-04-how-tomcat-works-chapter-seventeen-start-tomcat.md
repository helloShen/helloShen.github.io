---
layout: post
title: "How Tomcat Works - Chapter 17 - Start Tomcat"
date: 2017-12-04 18:53:39
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["bootstrap"]
description: >
---

### 摘要
启动总体架构靠2个类`Catalina`和`Bootstrap`，以及2个批处理sh脚本`catalina.sh`和`startup.sh`。
```
            startup.sh                  // 负责做一些小检查然后运行catalina.sh
                |
                |
            catalina.sh                 // 主要配置选项和环境变量，比如CLASSPATH
                |
                |
org.apache.catalina.startup.Bootstrap   // 主要负责创建不同的ClassLoader
                |
                |
org.apache.catalina.startup.Catalina    // 主要负责创建Digester解析xml配置文件
                |
                |
                ...
                ...
                ...
                |
                |
            shutdown.sh
```

### 关于`org.apache.catalina.startup.Catalina`类里的Digester
因为Digester类是一定的:`org.apache.commons.digester.Digester`类。然后解析`CATALINA_HOME/conf/server.xml`文件的规则在`Catalina#createStartDigester()`函数里写死了，所以：
> 我们编写的server.xml配置文件要去适应Catalina.createStartDigester()函数里的规则。

`server.xml`文件的结构要让`createStartDigester()`函数能够正确解析。所以需要很清楚地了解`createStartDigester()`函数中的规则做了什么。

### 关于`org.apache.catalina.startup.Bootstrap`类中创建的ClassLoader
`org.apache.catalina.startup.Bootstrap`类里做的最重要的一件事就是：(Tomcat 5)创建了3个不同的类加载器。使用多个类加载器的目的在“类加载器”这一章已经说了：
> 为了防止应用程序中的类（包括servlet类和Web应用程序中的其他辅助类）使用WEB-INF/classes目录和WEB-INF/lib目录之外的类。

![tomcat-5-classloader](/images/how-tomcat-works-chapter-eight-loader/tomcat-5-class-loader.png)

Tomcat 5的这三个类加载和他们对应的管辖范围如下，
* commonLoader:
    * CATALINA_HOME/common/classes
    * CATALINA_HOME/common/endorsed
    * CATALINA_HOME/common/lib
* catalinaLoader:
    * CATALINA_HOME/server/classes
    * CATALINA_HOME/server/lib
* sharedLoader:
    * CATALINA_HOME/shared/classes
    * CATALINA_HOME/shared/lib

在之后的版本ClassLoader的结构做了简化， 没有了`catalinaLoader`和`sharedLoader`，只剩下`commonLoader`和`webAppLoader`。
