---
layout: post
title: "How Tomcat Works - Chapter 16 - Shutdown Hook"
date: 2017-12-03 16:24:56
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["hook","thread"]
description: >
---

### 关闭钩子怎么工作？
> 关闭钩子实际上就是一个由虚拟机保证在特定情况下执行的“守护线程”。

一个关闭钩子类必须继承自`Thread`类，拥有自己的`run()`函数，比如下面的代码，定义了一个最简单的关闭钩子，
```java
class ShutdownHook extends Thread {
    public void run() {
        System.out.println("Shutting down!");
    }
}
```
要让关闭钩子能够工作，最关键的是要：
> 使用当前Runtime类的addShutdownHook()方法注册关闭钩子。

```java
Runtime.addShutdownHook(new ShutdownHook());
```
这样，当程序意外终止的时候，虚拟机也会逐一执行在`Runtime`类中注册过的所有关闭钩子。

#### 背后的虚拟机规范
能这样使用钩子，是建立在虚拟机的下列行为规范上的，

##### 虚拟机什么时候执行关闭操作？
首先，Java虚拟机会对两类事件进行响应，然后执行关闭操作：
* 当调用`System.exit()`方法或程序的最后一个非守护进程线程退出时，应用程序正常退出。
* 用户突然强制虚拟机中断运行，例如用户按CTRL-C快捷键或在未关闭Java程序的情况下，从系统中退出。

##### 虚拟机的关闭操作包含哪些动作？
* 虚拟机启动所有已经注册的关闭钩子，如果有的话。关闭钩子是先前已经通过Runtime类注册的线程，所有关闭购置会并发执行，知道任务完成。
* 虚拟机根据情况调用所有没有被调用过的终结器（finalizer）。


### Tomcat中的关闭钩子
在`org.apache.catalina.startup.Catalina`类中有一个继承自`java.lang.Thread`类的`CatalinaShutdownHook`内部类，它提供了`run()`方法的实现，它会调用Server对象的`stop()`方法。
```java
/**
 * Shutdown hook which will perform a clean shutdown of Catalina if needed.
 */
protected class CatalinaShutdownHook extends Thread {

    public void run() {

        if (server != null) {
            try {
                ((Lifecycle) server).stop();
            } catch (LifecycleException e) {
                System.out.println("Catalina.stop: " + e);
                e.printStackTrace(System.out);
                if (e.getThrowable() != null) {
                    System.out.println("----- Root Cause -----");
                    e.getThrowable().printStackTrace(System.out);
                }
            }
        }

    }
}
```

这就是关于关闭钩子我们需要知道的全部。关于`org.apache.catalina.startup.Catalina`类的详细信息，将在下一章（17章）详细介绍。

### 应用程序
原有的`MySwingApp`类的源码里没有关闭钩子。下面这个版本加上了关闭钩子，
```java
package com.ciaoshen.howtomcatworks.ex16.shutdownhook;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class MySwingApp extends JFrame {
  JButton exitButton = new JButton();
  JTextArea jTextArea1 = new JTextArea();
  String dir = System.getProperty("user.dir");
  String filename = "temp.txt";

  public MySwingApp() {
    exitButton.setText("Exit");
    exitButton.setBounds(new Rectangle(304, 248, 76, 37));
    exitButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exitButton_actionPerformed(e);
      }
    });
    this.getContentPane().setLayout(null);
    jTextArea1.setText("Click the Exit button to quit");
    jTextArea1.setBounds(new Rectangle(9, 7, 371, 235));
    this.getContentPane().add(exitButton, null);
    this.getContentPane().add(jTextArea1, null);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setBounds(0,0, 400, 330);
    this.setVisible(true);
    initialize();
  }

  private void initialize() {
    // create a temp file
    File file = new File(dir, filename);
    try {
      System.out.println("Creating temporary file");
      file.createNewFile();
      /** 注册关闭钩子 */
      Runtime.getRuntime().addShutdownHook(new MyShutdownHook());
    }
    catch (IOException e) {
      System.out.println("Failed creating temporary file.");
    }
  }

  private void shutdown() {
    // delete the temp file
    File file = new File(dir, filename);
    if (file.exists()) {
      System.out.println("Deleting temporary file.");
      file.delete();
    } else {
      System.out.println("File has been closed correctly!");
    }
  }

  void exitButton_actionPerformed(ActionEvent e) {
    shutdown();
    System.exit(0);
  }

  /** 关闭钩子 */
  private class MyShutdownHook extends Thread {
      public void run() {
          shutdown();
          System.out.println("Shutdown Hook has been called to make sure the file is closed!");
      }
  }

  public static void main(String[] args) {
    MySwingApp mySwingApp = new MySwingApp();
  }
}
```
