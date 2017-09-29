---
layout: post
title: "How Tomcat Works - Chapter 4 - Connector 2"
date: 2017-09-28 23:34:32
author: "Wei SHEN"
categories: ["web","java","how tomcat works"]
tags: ["socket","io","tcp","http","connector"]
description: >
---

### 回顾Java并发特性

#### `synchronized`同步锁
《Thinking in Java》对`synchronized`关键字的描述如下，
> 如果一个方法处于对某个标记为`synchronized`的方法的调用中，那么在这个线程从这个方法返回之前，所有要调用这个对象中任何标记有`synchronized`的方法的线程都会被阻塞。

假设`A`类有两个加了`synchronized`关键字的方法`f()`和`g()`，
```java
public class A {
    public synchronized void f() {
        // some code here
    }
    public synchronized void g() {
        // some code here
    }
}
```
当某个线程正在调用`A`类对象`a`的`f()`函数的时候，`a`对象将被锁住，另一个线程如果尝试调用`a.g()`或`a.f()`都将被阻塞。
```java
public void run() {
    A a = new A();
    a.f();  // 此线程调用f()方法返回前，其他任何线程调用f()或g()都将被阻塞
}
```

什么时候需要用同步锁？Brian Goetz说，
> 当你正在写一个变量，它接下来可能被另一个线程读取，或者正在读取一个上一次已经被另一个线程写过的变量，那么你必须使用同步锁。并且读写线程都必须使用相同的监视器锁同步。

#### `wait()`和`notify()`，`notifyAll()`
> wait()方法会是当前线程进入阻塞状态，直到其他线程调用了这个对象的notify()方法或notifyAll()方法。

`wait()`,`notify()`和`notifyAll()`是基类`Object`的接口方法，不是`Thread`类的一部分。所以只有通过某个对象调用它的`wait()`,`notify()`或者`notifyAll()`方法，并且调用这三个方法之前，调用线程必须获得目标对象的锁。所以只能在同步控制方法，或者同步控制块中调用它们。
