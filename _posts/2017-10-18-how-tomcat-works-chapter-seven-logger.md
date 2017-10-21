---
layout: post
title: "How Tomcat Works - Chapter 7 - Logger"
date: 2017-10-18 20:05:58
author: "Wei SHEN"
categories: ["java","how tomcat works"]
tags: ["log"]
description: >
---

### 前言
日志系统不是很复杂。说得高级一点是数据固化。大白话讲就是把日志消息写入一个本地文本文件。简单地用到了`java.lang.io`。

### Logger接口
最简单的`public void log(String msg)`方法是这套接口的核心。其他更复杂的`log()`方法重载版本都需要调用这个最基本的版本。

`getVerbosity()`和`setVerbosity()`方法表明`Logger`接口支持日志等级设置。

### LoggerBase是Logger接口的骨架实现
本章讨论的是Tomcat 4的LoggerBase的实现。它实现了除`log(String msg)`方法外的全部方法。继承它的子类需要实现自己的`log(String msg)`方法。

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
`FileLogger`的`open()`函数的写法也值得借鉴，创建文件之前先检查所在文件夹是否已经创建，
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

### 记录Log的时机
1. `SimpleContext`的`start()`和`stop()`方法里记录容器的启动和关闭。
