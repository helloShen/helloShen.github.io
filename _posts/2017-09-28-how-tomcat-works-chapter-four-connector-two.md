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

`notifyAll()`和`notify()`的区别是，`notifyAll()`会唤醒在这个对象的锁上等待的所有线程。而`notify()`只是由调度器选择在这个锁上等待的某一个线程唤醒。但具体唤醒的是哪一个线程，是程序员不可控的。

对于使用`wait()`和`notify()`，`notifyAll()`的一个良好实践是：
> 总是在一个`while`轮询中使用`wait()`。并且在一般情况下`notifyAll()`的优先级高于`notify()`。

`notifyAll()`优于`notify()`是因为：
> `notifyAll()`可以避免来自不相干线程在对象锁上的意外或恶意等待。

因为，如果一个恶意线程在对象的锁上等待，调度器在`notify()`后不幸将锁分配给了这个恶意线程，那这个重要的 “唤醒通知” 将被 “吞掉”。那个真正需要被唤醒的线程将陷入无限的等待。

如果所有程序员普遍使用`notifyAll()`替代`notify()`，那么`wait()`方法的阻塞必然会经常被在条件不满足的情况下被唤醒，因此，
> 为了配合`notifyAll()`非常 “大方” 的唤醒策略，每个`wait()`必须在每次被唤醒的时候都谨慎测试它的阻塞条件是否被跳过了，否则应该继续阻塞。

这样带来的另一个好处是：
> `while`轮询中的`wait()`同时能抵御来自不相干线程的 “恶意唤醒”。

时刻记住，每个在公有可访问对象上等待的线程都是将自己暴露在一个非常危险的环境里。任何拿到对象互斥锁的线程都能错误或者恶意将这个等待线程唤醒，不管条件是否满足。

#### `HttpProcessor`类的`assign()`和`await()`方法是典型的“生产者-消费者”模式
“生产者”`assign()`方法负责将`HttpConnector`产生的`Socket`对象赋值给`HttpProcessor`类的`socket`域。
```java
synchronized void assign(Socket socket) {

    // Wait for the Processor to get the previous Socket
    while (available) {
        try {
            wait();
        } catch (InterruptedException e) { }
    }

    // Store the newly available Socket and notify our thread
    this.socket = socket;
    available = true;
    notifyAll();

    // remainder omitted
}
```

“消费者”`await()`方法从`socket`域拷贝并返回`Socket`对象的引用。
```java
private synchronized Socket await() {

    // Wait for the Connector to provide a new Socket
    while (!available) {
        try {
            wait();
        } catch (InterruptedException e) { }
    }

    // Notify the Connector that we have received this Socket
    Socket socket = this.socket;
    available = false;
    notifyAll();

    // remainder omitted

    return (socket);

}
```
`await()`和`assign()`方法都用`synchronized`关键词修饰，使得两个方法内部临界区的过程都是原子性的。进入这两个方法之前，都必须先获得这个`HttpProcessor`对象上的互斥锁。然后`wait()`和`notifyAll()`方法保证了`await()`和`assign()`间的协作，当条件不满足的时候，它们会释放它们占用的锁。

`boolean available`域用来标识`socket`域当前是否有可用(available名字的来由)但未处理的新引用传递进来。当`available`域的值被设置为`true`表明这个`HttpProcessor`对象还没有着手处理上一个传进来的`Socket`对象。因此`HttpConnector`线程需要等一等。当`available`域被设置成`false`以后，说明`HttpProcessor`的`run()`方法已经拷贝了上一个`Socket`对象的引用，并且已经着手调用`process()`方法进行解析，这时`HttpConnector`线程被允许将一个新的`Socket`对象引用传进`HttpProcessor`。

另外这部分代码显示了3个并发编程的良好实践，
* 因为无论是`assign()`方法还是`await()`方法都是在某个`HttpProcessor`对象的锁上等待，因此将他们封装进`HttpProcessor`类是非常正确的做法。
* 将`wait()`方法包裹在一个`while`轮询中，并且使用`notifyAll`方法唤醒，就像之前说的，已经成为一种惯用法。
* 在`wait()`方法外面包裹一个`try{}catch{}`代码块，让`HttpProcessor`在阻塞过程中依旧能够响应系统中断。
