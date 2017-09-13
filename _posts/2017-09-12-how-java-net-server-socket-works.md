---
layout: post
title: "How java.net.ServerSocket works?"
date: 2017-09-12 20:54:11
author: "Wei SHEN"
categories: ["java","web"]
tags: ["socket","tcp","ip"]
description: >
---

### 前言
我们已经知道`Socket`是向程序员提供`TCP/IP`服务的一个抽象。经典的`Berkeley Socket API`提供的函数如下，

* `socket()` 创建一个新的确定类型的套接字，类型用一个整型数值标识（文件描述符），并为它分配系统资源。
* `bind()` 一般用于服务器端，将一个套接字与一个套接字地址结构相关联，比如，一个指定的本地端口和IP地址。
* `listen()` 用于服务器端，使一个绑定的TCP套接字进入监听状态。
* `connect()` 用于客户端，为一个套接字分配一个自由的本地端口号。 如果是TCP套接字的话，它会试图获得一个新的TCP连接。
* `accept()` 用于服务器端。 它接受一个从远端客户端发出的创建一个新的TCP连接的接入请求，创建一个新的套接字，与该连接相应的套接字地址相关联。
* `send()`和`recv()`,或者`write()`和`read()`,或者`recvfrom()`和`sendto()`, 用于往/从远程套接字发送和接受数据。
* `close()` 用于系统释放分配给一个套接字的资源。 如果是TCP，连接会被中断。
* `gethostbyname()`和`gethostbyaddr()` 用于解析主机名和地址。
* `select()` 用于修整有如下情况的套接字列表： 准备读，准备写或者是有错误。
* `poll()` 用于检查套接字的状态。 套接字可以被测试，看是否可以写入、读取或是有错误。
* `getsockopt()` 用于查询指定的套接字一个特定的套接字选项的当前值。
* `setsockopt()` 用于为指定的套接字设定一个特定的套接字选项。

具体参考这篇文章 -> <http://www.ciaoshen.com/algorithm/leetcode/2017/09/12/berkeley-socket-api.html>

Java有`java.net.Socket`和`java.net.ServerSocket`两个类库，向程序员提供`TCP/IP`网络传输服务。

另外一篇文章用一个Demo演示了怎么用`ServerSocket`搭建一个简易的HTTP服务器。 -> <http://www.ciaoshen.com/algorithm/leetcode/2017-09-10-how-tomcat-works-chapter-two-httpserver1.html>

这篇文章主要分析`java.net.ServerSockt`的源码，看它内部具体是怎么工作的。以`ServerSocket#accept()`函数为例，它最终调用的是`PlainSocketImpl#socketAccept()`本地方法(Native Method)。

所以，理解Java的套接字接口，主要有两个层面，
> 首先，程序员日常面对的是`java.net.Socket`和`java.net.ServerSocket`两个类库提供的接口。
> 再进一步，这两个类库本身面向的是伯克利套接字`Socket API`定义的一组行为。可以把这组接口理解为操作系统向其他应用或者编程语言提供的服务。


### `ServerSocket#accept()`函数
`ServerSocket#accept()`函数调动`ServerSocket#implAccept(Socket)`函数。需要传递一个空的`Socket`实例进去，最后返回的 **已连接套接字（connected socket）** 就是这个实例的引用。

```java
public class ServerSocket implements java.io.Closeable {

    // ... ... 省略其他代码

    public Socket accept() throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isBound())
            throw new SocketException("Socket is not bound yet");
        Socket s = new Socket((SocketImpl) null); // 构造一个空的套接字实例
        implAccept(s); // 把空的套接字实例传递给implAccept(Socket s)函数继续处理
        return s;
    }
}
```

### `ServerSocket#implAccept(Socket)`函数
`ServerSocket#implAccept(Socket s)`函数通过`ServerSocket#getImpl()`函数找到`ServerSocket#impl`字段的引用，调用它的`accept(SocketImpl si)`方法。

```java
public class ServerSocket implements java.io.Closeable {

    // ... ... 省略其他代码

    /**
     * The implementation of this Socket.
     */
    private SocketImpl impl; // impl暴露的是SocketImpl接口

    protected final void implAccept(Socket s) throws IOException {
        SocketImpl si = null;
        try {
            if (s.impl == null)
              s.setImpl();
            else {
                s.impl.reset();
            }
            si = s.impl;
            s.impl = null;
            si.address = new InetAddress();
            si.fd = new FileDescriptor();
            getImpl().accept(si); // 关键调用在这里！

            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkAccept(si.getInetAddress().getHostAddress(),
                                     si.getPort());
            }
        } catch (IOException e) {
            if (si != null)
                si.reset();
            s.impl = si;
            throw e;
        } catch (SecurityException e) {
            if (si != null)
                si.reset();
            s.impl = si;
            throw e;
        }
        s.impl = si;
        s.postAccept();
    }
}
```

### `ServerSocket#impl`字段对外暴露的是`SocketImpl`接口
`ServerSocket#impl`字段是一个`SocketImpl`实例，所以调用的是`SocketImpl#accept(SocketImpl)`函数。
```java
public class ServerSocket implements java.io.Closeable {

    // ... ... 省略其他代码

    /**
     * The implementation of this Socket.
     */
    private SocketImpl impl;

    SocketImpl getImpl() throws SocketException {
        if (!created)
            createImpl();
        return impl;    // 返回的是impl字段
    }
}
```

这个`ServerSocket#impl`字段在`ServerSocket()`构造函数里就初始化了。但实际上`SocketImpl`是一个抽象类（相当于一个接口）。

```java
public abstract class SocketImpl implements SocketOptions {
    // ... ... 省略代码
}
```

继续看`ServerSocket()`的构造函数，调用`setImpl()`函数初始化`impl`字段。
```java
public class ServerSocket implements java.io.Closeable {

    // ... ... 省略其他代码

    public ServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException {
        setImpl(); // 初始化impl字段
        if (port < 0 || port > 0xFFFF)
            throw new IllegalArgumentException(
                       "Port value out of range: " + port);
        if (backlog < 1)
          backlog = 50;
        try {
            bind(new InetSocketAddress(bindAddr, port), backlog);
        } catch(SecurityException e) {
            close();
            throw e;
        } catch(IOException e) {
            close();
            throw e;
        }
    }
}
```

### `ServerSocket#impl`字段实际是`SocksSocketImpl`类的实例
`ServerSocket#setImpl()`函数里实际构造的是`SocksSocketImpl()`类实例。这里的`factory`默认是`null`，用户可以通过`ServerSocket#setSocketFactory(java.net.SocketImplFactory)`方法设置工厂，但默认的是不使用工厂。
```java
public class ServerSocket implements java.io.Closeable {

    // ... ... 省略其他代码

    /**
     * The factory for all server sockets.
     */
    private static SocketImplFactory factory = null;

    private void setImpl() {
        if (factory != null) { // 工厂默认为null，不走这个分支
            impl = factory.createSocketImpl();
            checkOldImpl();
        } else {
            // No need to do a checkOldImpl() here, we know it's an up to date
            // SocketImpl!
            impl = new SocksSocketImpl(); // impl实际是SocksSocketImpl类的实例
        }
        if (impl != null)
            impl.setServerSocket(this);
    }
}
```

### `SocksSocketImpl#accept(SocketImpl)`函数是怎么实现的？

#### `SocksSocketImpl`类继承自`PlainSocketImpl`类
```java
class SocksSocketImpl extends PlainSocketImpl implements SocksConsts {

    // ... ... 省略代码

}
```

#### `PlainSocketImpl`类继承自`AbstractPlainSocketImpl`抽象类
```java
class PlainSocketImpl extends AbstractPlainSocketImpl {

    // ... ... 省略代码

}
```

#### `SocksSocketImpl#accept(Socket)`在`AbstractPlainSocketImpl#accept(Socket)`中定义
`AbstractPlainSocketImpl#accept(Socket)`函数调用`AbstractPlainSocketImpl#socketAccept(Socket)`函数。
```java
class PlainSocketImpl extends AbstractPlainSocketImpl {

    // ... ... 省略代码

    protected void accept(SocketImpl s) throws IOException {
        acquireFD();
        try {
            socketAccept(s);
        } finally {
            releaseFD();
        }
    }
}

```

#### `AbstractPlainSocketImpl#socketAccept(SocketImpl)`函数在`PlainSocketImpl`类里实现
`AbstractPlainSocketImpl`类只是一个骨架实现，
```java
abstract class AbstractPlainSocketImpl extends SocketImpl {

    // ... ... 省略代码

    abstract void socketAccept(SocketImpl s) throws IOException; // 骨架实现类没有实现该方法
}
```
`AbstractPlainSocketImpl#socketAccept(SocketImpl)`具体在`PlainSocketImpl`类里实现。实际调用的是一个叫`socketAccept(SocketImpl)`的本地方法。不是用Java写的代码。

```java
class PlainSocketImpl extends AbstractPlainSocketImpl {

    // ... ... 省略代码

    native void socketAccept(SocketImpl s) throws IOException; // 调用本地方法，不是用Java代码实现的，追踪结束

}
```

### 总结
可以理解为，
> `ServerSocket#accept()`函数中最终实际工作的代码在一个系统调用中。不是Java代码。

所以，
> 程序员面向的是`Socket`和`ServerSocket`两个类库提供的服务，而这两个类库面向的是基于伯克利套接字`Berkeley Socket API`的一组系统调用提供的服务。
\`
