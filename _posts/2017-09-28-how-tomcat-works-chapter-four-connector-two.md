---
layout: post
title: "How Tomcat Works - Chapter 4 - Connector 2"
date: 2017-09-28 23:34:32
author: "Wei SHEN"
categories: ["web","java","how tomcat works"]
tags: ["socket","io","tcp","http","connector"]
description: >
---

### 前言
本章介绍的连接器是老版Tomcat的默认连接器。后被Coyote替代。

默认连接器的职责分工更加明确。连接器`Connector`主要负责，
* 响应客户端TCP连接请求，创建`Socket`实例。
* 解析HTTP请求
* 创建`Request`和`Response`对象，传递给`Container`。

本章的重点不在容器，因此容器`Container`的功能被简化，但也很明确，就是第三章的`ServletProcessor`的职责：
* 根据`Request`对象中的信息，动态加载响应Servlet的.class文件，并执行Servlet。

`Container`的功能在第5章会得到扩展。但本章主要集中在连接器`Connector`。相比第三章的连接器，本章的连接器主要有两点强化，
1. 升级成并发。给每个`HttpProcessor`独立的线程。维护一个`HttpProcessor`对象池。让原先只可以顺序处理单个客户连接请求的连接器，现在可以同时相应多个客户的请求，并为每个连接分配一个独立的`HttpProcessor`对象解析HTTP消息。
2. 增加了对HTTP/1.1版本多个新特性的支持。

剩下的HTTP消息解析的细节，还是由`SocketInputStream`类实际负责，和第三章保持一致。对`StringManager`也延续第三章的内容。最后为了更好地管理线程的生命周期，默认连接器实现了`org.apache.catalina.Lifecycle`接口。这部分内容会在第6章的时候详细介绍。

本章Demo全部代码，在我的Github可以找到 ->
<https://github.com/helloShen/HowTomcatWorks/tree/master/solutions/src/com/ciaoshen/howtomcatworks/ex04>

### 本章重点一：`HttpProcessor`线程池
`HttpConnector`负责将一个新`Socket`对象的引用交给`HttpProcessor`，`HttpProcessor`实际负责解析HTTP消息，并填充`Request`对象。第三章的`HttpConnector`和`HttpProcessor`是一对一的关系。因此`HttpConnector`无法同时响应多个HTTP请求。本章的`HttpConnector`对象维护着一个`HttpProcessor`线程池。每个`HttpProcessor`都拥有自己独立的线程。因此就算解析HTTP消息是一个耗时的操作，`HttpConnector`仍然能够保持对用户连接请求的快速响应。这是根本目标。 因此`HttpConnector`和`HttpProcessor`间协作的基本原则是：
> 让`HttpConnector`和`HttpProcessor`间的协作仅限于“存取”`Socket`引用。然后把比较耗时的解析HTTP消息的过程完全独立出去。

所以实际的设计就是：
* `HttpProcessor#assign()`函数负责传入`Socket`引用。
* `HttpProcessor#await()`函数负责取出`Socket`引用。
* `HttpProcessor#run()`函数统筹解析HTTP请求的过程。

而`assign()`和`await()`函数，构成了一对“生产者-消费者”模式。

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

这里变量不是指所有的变量。而主要是指对象的成员字段。尤其是对象公有的成员字段，当多个线程可以同时读写这个字段，就构成了 **竞态条件**。 这时公有的成员字段就成为了 **竟态资源**。那么所有访问竟态资源的代码就构成了 **临界区**。 临界区内的代码原则上都必须用`synchronized`互斥锁保护，确保同一时刻只有一个线程进入临界区代码。

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

另外这部分代码显示了5个并发编程的良好实践，
* 生产者-消费者模式是 **自旋锁(spin lock)** 的进化版，优点是用释放互斥锁的`wait()`替代了`while()`轮询忙等。
* 因为无论是`assign()`方法还是`await()`方法都是在某个`HttpProcessor`对象的锁上等待，因此将他们封装进`HttpProcessor`类是非常正确的做法。
* 将`wait()`方法包裹在一个`while`轮询中，并且使用`notifyAll`方法唤醒，就像之前说的，已经成为一种惯用法。
* 在`wait()`方法外面包裹一个`try{}catch{}`代码块，让`HttpProcessor`在阻塞过程中依旧能够响应系统中断。
* `await()`方法里`Socket socket = this.socket;`把`Socket`对象的引用复制一份返回，是一个很好的 **栈封闭** 实践。这样比较耗时的HTTP消息解析过程就完全从并发过程中完全独立出去。整个`HttpProcessor#run()`函数包括`process()`函数依赖的`Socket`引用都是一个函数内局部变量，而不是`HttpProcessor`的公有竟态资源`socket`域。

关于线程，并发更详细的内容，可以阅读--> <http://www.ciaoshen.com/operating%20system/2017/08/09/race-condition-and-mutual-exclusion.html>

关于生产者-消费者模式更详细的内容，可以阅读--> <http://www.ciaoshen.com/java/design%20pattern/2017/10/03/producer-and-consumer.html>

### 重点二：对HTTP/1.1协议新特性的支持
在前两章曾经出现默认使用`HTTP/0.9`浏览器不支持的问题。也出现过因为没有计算消息正文的总长度，并在消息头中给出`content-length`的属性，而导致图片完成传输的问题。这章的默认连接器因为对HTTP/1.1细节特性的支持，不会出现这方面问题。

总体HTTP消息的解析任务还是由`SocketInputStream`类的实例实际负责。但默认连接器的`process()`方法中做了下面这些对HTTP/1.1特性的支持。

#### 多了`parseConnection()`函数
本章在第三章的`parseRequest()`和`parseHeaders()`方法之前加了一个`parseConnection()`函数获取请求所使用的协议，支持`HTTP/0.9`,`HTTP1.0`和`HTTP/1.1`。

一下列举了`HTTP/1.1`开始的新特性，
* `HTTP/1.1`必须在请求头中加入`content-length`属性。
* `HTTP/1.1`支持字节流分块发送。如果使用需要用`transfer-encoding`消息头标明。对每一个块，块的长度(16进制)后面会有一个回车/换行符（CR/LF）。然后是具体的数据。最后用一个`0\r\n`表明事务已完成。

```
假如要发送下面38个字节的内容，
I'm as helpless as a kitten up a tree.

实际发送内容可以如下：
1D/r/n
I'm as helpless as a kitten u
9/r/n
p a tree
0/r/n
```

* `HTTP/1.1`支持持久链接。需要在请求头中加入`connection: keep-alive`属性。
* `HTTP/1.1`客户端在发送请求体之前发送`Expect: 100-continue`请求头，等待服务器确认。服务器可以发送`HTTP/1.1 100 continue`响应，表示接受。

相应的`HttpProcessor`在`parseConnection()`，`parseRequest()`和`parseHeaders()`方法中都加入了部分代码支持以上新特性。比如`boolean keepAlive`标明是否支持持久链接。如果在请求头中发现了`Expect: 100-continue`，就把`boolean sendAck`域设置为`true`。另外`isChunkingAllowed()`函数可以判断是否支持分块传输。`boolean finishResponse`域在解析过程出现异常的情况下会被设置成`true`。



### 其他主题
本章的例子运行起来没有什么困难。前几章容易出问题的地方，`lib/servlet.jar`包解决地很好。

#### StringManager
`lib/servlet.jar/javax/servlet/http`包下已经有了`LocalStrings.properties`，`LocalStrings_es.properties`和`LocalStrings_jp.properties`文件。关于StringManager的详细内容，参见第三章。

#### `org.apache.catalina.Lifecycle`接口
`org.apache.catalina.connector.http.HttpConnector`类实现了`org.apache.catalina.Lifecycle`接口。Lifecycle接口用于维护每个实现了该接口的Canalina组件的生命周期（主要是线程）。这部分内容会在第6章详细介绍。本章的内容主要关注多线程之间怎么协作，把线程的创建和回收的工作交给Lifecycle接口。但实际上这是一个非常重要的主题。
