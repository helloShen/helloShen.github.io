---
layout: post
title: "How Tomcat Works - Chapter 7 - Logger"
date: 2017-10-18 20:05:58
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["log"]
description: >
---

### 前言
日志系统就是把日志消息写入一个本地文本文件。简单地用到了`java.lang.io`。Tomcat到处都是设计模式。`Logger`的设计上用到了接口加“骨架实现”的模式。

### Logger接口
`public void log(String msg)`方法是这套接口的核心。其他更复杂的`log()`方法重载版本都需要调用这个最基本的版本。

`getVerbosity()`和`setVerbosity()`方法表明`Logger`接口支持日志等级设置。

### `LoggerBase`是`Logger`接口的骨架实现
本章讨论的是Tomcat 4的LoggerBase的实现。它实现了除`log(String msg)`方法外的全部方法。继承它的子类需要实现自己的`log(String msg)`方法。“骨架实现”模式中，最基本的留空方法叫做 **"Primitive Method"**。其他上层方法都可以通过调用"Primitive Methode"的方式预先写好。最后继承骨架实现抽象类的子类，只需要实现留空的"Primitive Method"就可以实现所有功能。

骨架实现在`Collections`框架里用的很多，`Map#entrySet()`就是留空的"Primitive Methode"。

#### `SystemOutLogger`，`SystemErrLogger`和`FileLogger`补全骨架实现
`SystemOutLogger`和`SystemErrLogger`分别把日志发送到标准输出和标准错误。`FileLogger`把日志记录在一个本地文件，每天都会创建一个当前专属的日志文件。

下面是`FileLogger`的`log(String msg)`函数。往日志文件里写入之前要做两件事，首先获取时间戳，然后根据时间戳检查是否需要创建一个新的日志文件。
```java
public void log(String msg) {

    // Construct the timestamp we will use, if requested
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    String tsString = ts.toString().substring(0, 19);
    String tsDate = tsString.substring(0, 10);

    // If the date has changed, switch log files
    if (!date.equals(tsDate)) {
        synchronized (this) {
            if (!date.equals(tsDate)) {
                close();
                date = tsDate;
                open();
            }
        }
    }

    // Log this message, timestamped if necessary
    if (writer != null) {
        if (timestamp) {
            writer.println(tsString + " " + msg);
        } else {
            writer.println(msg);
        }
    }

}
```
`FileLogger`的`open()`函数的写法也值得借鉴，创建文件之前先用`isAbsolute()`函数检查所在文件夹是否已经创建，
```java
private void open() {

    // Create the directory if necessary
    File dir = new File(directory);
    if (!dir.isAbsolute())
        dir = new File(System.getProperty("catalina.base"), directory);
    dir.mkdirs();

    // Open the current log file
    try {
        String pathname = dir.getAbsolutePath() + File.separator +
            prefix + date + suffix;
        writer = new PrintWriter(new FileWriter(pathname, true), true);
    } catch (IOException e) {
        writer = null;
    }

}
```

### 使用`Logger`
`Logger`使用起来也很简单那，`SimpleContext`里写了一个`log()`函数封装使用`Logger`的过程，直接创建`Logger`实例，调用`log()`函数就好，
```java
private void log(String message) {
  Logger logger = this.getLogger();
  if (logger!=null)
    logger.log(message);
}
```
在需要记录日志的地方调用`log()`函数即可。比如在`SimpleContext#start()`函数里，正常的处理逻辑开始之前和之后，都写下日志。
```java
public synchronized void start() throws LifecycleException {
  log("starting Context");

  // code in start() method ...

  log("Context started");
}
```
