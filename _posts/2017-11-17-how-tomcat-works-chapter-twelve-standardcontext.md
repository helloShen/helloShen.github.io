---
layout: post
title: "How Tomcat Works - Chapter 12 - StandardContext"
date: 2017-11-17 22:46:35
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["container"]
description: >
---

### `ContextConfig`监听器为StandardContext实例配置
StandardContext实例的`start()`方法中，会触发一个`BEFORE_START_EVENT`事件，然后监听器`org.apache.catalina.startup.ContextConfig`实例（实现了`LifecycleListener`接口），开始对StandardContext实例进行配置。
```java
// Notify our interested LifecycleListeners
lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
```

配置过程中会用`Digester`库解析`web.xml`配置文件，文件的具体路径如下，
> CATALINA_HOME/conf/web.xml

关于`Digester`库的细节，详见第15章。

### 默认Mapper
StandardContext定义的默认`mapperClass`字段是硬编码的，但也可以自己设置。
```java
private String mapperClass = "org.apache.catalina.core.StandardContextMapper";
```

### 重载
当`web.xml`文件发生变化，或者`WEB-INF/classes`目录下的其中一个文件被重新编译后，应用程序会重载。

Tomcat 4中`WebappLoader`用另一个线程周期性地检查`WEB-INF`目录中的所有类和JAR文件的时间戳。需要`StandardContext`和`WebappLoader`是双向绑定的。这样加载器也能找到容器。

所以Tomcat 4运行Context容器的加载器和Session管理器这些组件都需要自己的后台线程。这就会导致资源浪费。

Tomcat 5用`ContainerBackgroundProcessor`类用一个后台线程统一检查`WEB-INF`目录中类的时间戳，还要帮助Session管理器检查会话有效期。一个后台线程大管家。

它通过`ContainerBase`的`start()`方法调用`threadStart()`方法启动。

它的`processChildren()`方法会调用自身容器的`backgroundProgress()`方法，然后递归调用每个子容器的`processChildren()`。这样可以确保每个子容器的`backgroundProgress()`方法都被调用。
