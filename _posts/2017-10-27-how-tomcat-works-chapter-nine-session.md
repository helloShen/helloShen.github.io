---
layout: post
title: "How Tomcat Works - Chaper 9 - Session"
date: 2017-10-27 18:56:28
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["session"]
description: >
---

### 思路
1. Http是无状态的。不面向连接。
2. 但有保存用户连接信息的需求。主要有3种方法：
    * cookie
    * URL重写
    * 隐藏表单域
3. cookie是最常用的
4. session和cookie配套使用。在客户端浏览器保存一个cookie（session id）信息，服务器用HashMap保存所有session id
5. URL重写为了应对cookie被人为禁止的情况
6. session持久化是为了应对服务器可能重启，而丢失所有当前session信息
7. 集群也同样为了高可用性
8. 应用程序间的session应该是被隔离的。所以session对象和context容器绑定。但也有办法能做到应用间共享session信息

### HTTP是无状态的，Session的本质是为HTTP协议模拟“连接”
HTTP是无状态的。我们把一次完整的HTTP请求和HTTP响应叫做一个 **“HTTP事务”**。说HTTP无状态，就是说，两个独立的HTTP事务之间是没有关联的。说得直白一点就是，HTTP协议不关心服务器是否能知道两次HTTP请求是否来自同一个用户。

但现在的网站用户都可以登录自己的账户，之后服务器可以为他提供个性化的服务。但是无状态的HTTP做不到这些。所以就有了Session这个东西来帮HTTP做。用大白话讲，
> Session的本质就是服务器为每个客户分配一个短时间内有效的用来表示本次“会话”的ID。

所以，Session最重要的两个属性，一个是会话的序列号`id`，一个是最长存活时间`maxInactiveInterval`。

### Session和“持久连接”的区别
Session和持久连接是两个不同层面的东西。Session是在应用的层面，表示多次“事务”属于同一次“会话”。持久连接是在传输层面，指TCP连接的持久。

#### 持久连接的持久是指的TCP连接的持久
`HTTP/1.0`的时候，每次HTTP事务都要建立一个新的TCP连接。服务器将本次请求的资源返回后就会断开于客户浏览器的TCP连接。虽然通过显式加`Connection: Keep-Alive`也可以支持持久连接，但这是工程上的补充支持，本身`HTTP/1.0`协议官方不支持。

1个包含了3个嵌入图片的Web页面，浏览器需要发起4个HTTP事务来显示此页面。1个用于顶层的HTTP页面，3个用户嵌入图片。串行的条件下（必须完整完成上一个事务，才开始下一个事务。也就是必须接收到上一个请求的响应消息，才开始传输下一个请求），显示这个页面需要创建和关闭4个TCP连接。

![http-sequence-0](/images/how-tomcat-works-chapter-nine-session/http-sequence-0.png)

在第3章`HttpProcessor`类的`process()`函数中，在解析完一个HTTP请求，把事务发送给`processor`之后，主动调用`socket.close()`关闭TCP连接。
```java
/** in HttpProcessor.java of Chapter 3 */
public void process(Socket socket) {

    // code omitted ...

    parseRequest(input, output);
    parseHeaders(input);

    if (request.getRequestURI().startsWith("/servlet/")) {
      ServletProcessor processor = new ServletProcessor();
      processor.process(request, response);
    }
    else {
      StaticResourceProcessor processor = new StaticResourceProcessor();
      processor.process(request, response);
    }

    // Close the socket
    socket.close();

  }
  catch (Exception e) {
    e.printStackTrace();
  }
}
```

考虑到创建/关闭TCP连接是一个开销比较大的操作。两次串行事务之间会有一定的时延。
> 持久连接的目的就是重复利用一个TCP连接，消除多次创建/关闭TCP连接的开销。


![http-sequence-1](/images/how-tomcat-works-chapter-nine-session/http-sequence-1.png)

`HTTP/1.1`默认支持持久连接。具体操作上就是在每次服务器返回请求资源以后，根据具体参数决定是否断开当前TCP连接，回收套接字。

```java
/** in org.apache.catalina.connector.HttpProcessor */
private void process(Socket socket) {

    // code omitted ... ...
    // code omitted ... ...
    // code omitted ... ...

    /** 如果response的头信息"Connection"在servlet中被设置成close，或者协议是HTTP/1.0，则keepAlive将为false，TCP连接将被关闭 */
    if ( "close".equals(response.getHeader("Connection")) ) {
        keepAlive = false;
    }

    // End of request processing
    status = Constants.PROCESSOR_IDLE;

    // Recycling the request and the response objects
    request.recycle();
    response.recycle();

    }

    try {
        shutdownInput(input);
        socket.close();
    } catch (IOException e) {
        ;
    } catch (Throwable e) {
        log("process.invoke", e);
    }
    socket = null;
}
```

#### 不要和HTTP的串行和并行搞混
HTTP并行是指为了传输一个网页，同一个客户可以同时发起多个连接。不用等待前一个事务完成后再发送下一个事务。

![http-sequence-2](/images/how-tomcat-works-chapter-nine-session/http-sequence-2.png)


#### `HTTP/1.1`的`content-length`和代码块新特性都是为“持久连接”服务
既然说到持久连接，在扯出去讲一下`HTTP/1.1`中的另外两项新特性：
* 响应头中必须写`content-length`项
* 块编码

HTTP/1.0，服务器不用在响应消息里写`content-length`项，也就不用计算消息正文的长度。这正是因为`HTTP/1.0`不支持持久连接，服务器发送完响应信息就直接关闭连接。所以客户端可以一直放心读取内容，直到读方法返回-1，表明读到了文件尾。

有了持久连接，客户端就不能用读方法返回-1判断消息结尾了。所以服务器必须精确计算每个响应消息正文长度，告诉哪里是本次事务的结尾。

块编码更进一步，不仅可以区分事务和事务之间的边界，在同一事务的响应消息也可以分块发送。一个十六进制数字加上`\r\n`提示下一个分块的长度，然后用`0\r\n`标记一个事务的结尾。
```
1D\r\n
I'm as helpless as a kitten u
9\r\n
p a tree.
0\r\n
```

所以"持久连接"，"content-length"和"块编码"三个新特性，需要了解他们的内在逻辑。

### Session的核心信息：Session ID
要标识一个Session的唯一性，最简单有效的就是设置一个Session ID。Servlet默认的:
> `SessionCookieName = JSESSIONID`。

理想的状态是服务器和客户端双方都知晓并且记录了这个ID（用cookie）。如果客户端禁用了cookie，可以用URL重写，在客户端没有记录Session ID的情况下，服务器把供客户点击的URL连接预先附带上Session ID信息。

#### Cookie

#### URL重写

#### 隐藏表单

### 什么时候向下转型会被允许？
From Stackoverflow -> <https://stackoverflow.com/questions/380813/downcasting-in-java>

当某个向下转型在运行时 **“有可能成功”** 的时候，编译器会允许这个转型语句存在，
```java
Object o = getSomeObject();
String s = (String) o; // this is allowed because o could reference a String
```
哪怕有很大概率失败，但只要可能成功，编译器就放行。如果真的不行，运行时会报错，比如，
```java
Object o = new Object();
String s = (String) o; // this will fail at runtime, because o doesn't reference a String
```
当然，如果编译器能判定某些向下转型肯定能成功，当然更好，坑定放行，比如，
```java
Object o = "a String";
String s = (String) o; // this will work, since o references a String
```
编译器如果不允许某个向下转型，那么就是说编译器已经通过一些规则已经能确定它 **“不可能成功”**，比如，
```java
Integer i = getSomeInteger();
String s = (String) i; // the compiler will not allow this, since i can never reference a String.
```

### 为什么`StandardSessionFacade`类能防止错误的向下转型？
向`Servlet`实例传递一个`Session`对象的时候，尽管`StandardSession`类已经实现了`javax.servlet.http.HttpSession`接口（一下简称`HttpSession`接口），还是要先封装成`StandardSessionFacade`类型（也实现了`HttpSession`接口）。

为什么呢？

因为`StandardSession`类实现了很多`HttpSession`接口以外的方法。直接传给`Servlet`实例的话，`Servlet`程序员就可以向下转型成`StandardSession`类型，然后访问`HttpSession`接口以外的方法。所以`StandardSessionFacade`实现且只实现了`HttpSession`接口，是用来防止程序员恶意（或无意）向下转型的。

为什么封装成`StandardSessionFacade`就能防止向下转型？根据上面的规则，`Servlet`拿到的是一个`StandardSessionFacade`型实例。而`StandardSessionFacade`和`StandardSession`唯一的关系是他们都实现了`HttpSession`接口，但他们互相之间没有直接或间接的继承关系，所以编译器能直接禁止将`StandardSessionFacade`转型为`StandardSession`型。


### 持久化

#### `PersistentManagerBase`和`Store`的协作

### 集群
