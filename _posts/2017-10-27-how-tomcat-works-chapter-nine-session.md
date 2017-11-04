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

理想的状态是：**服务器和客户端双方都知晓并且记录了这个ID**。在这个Session ID的有效期内，客户每次访问这个特定服务器，都会出示这个Session ID，以表明身份。要让客户端记住Session ID，最好的办法就是用Cookie。如果客户端禁用了cookie，可以用URL重写，在客户端没有记录Session ID的情况下，服务器把供客户点击的URL连接预先附带上Session ID信息。

### 什么是Cookie
Cookie是Web服务器端用来识别Web用户的小块数据。有一整套通行的数据标准。而且光有数据格式还不行，关键是现在主流的浏览器都有一个`Cookie罐子`储存成百上千个来自不同服务器的Cookies。Cookie的重要性在于，
> Cookie是目前在客户端（浏览器）储存服务器分配的用户信息的最佳解决方案。

#### Cookie版本0（网景）格式
最初由网景公司定义的“版本0”Cookie规范格式，分为两部分：
1. `Set-Cookie`响应首部：服务器端告诉客户端它给客户分配的Cookie
2. `Cookie`请求部：客户端每次向特定服务器发起请求附带的Cookie信息标签

他们的格式如下，
![set-cookie](/images/how-tomcat-works-chapter-nine-session/set-cookie-1.png)

各部分属性的解释如下，
![set-cookie](/images/how-tomcat-works-chapter-nine-session/set-cookie-2.png)
![set-cookie](/images/how-tomcat-works-chapter-nine-session/set-cookie-3.png)

之后又有了一个版本1的Cookie，比网景公司的版本0多了更多的属性，给首部后面加了一个`2`，
1. `Set-Cookie2`响应首部
2. `Cookie2`请求部

#### Cookie罐子：存储在客户端的状态集（Client-Side States）和Cookie过滤
浏览器Cookie罐里会储存成百上千个来自不同服务器的Cookies。但是浏览器只向服务器发送特定的一部分Cookies。决定Cookie发送目标的是`domain`,`path`属性。
* `domain`对应的是`www.joes-hardware.com`这样的域名
* `path`对应的是`www.joes-hardware.com/tools`这样域名下的自路径`/tools`

比如，假设客户端曾收到过以下5条来自`www.joes-hardware.com`的`Set-Cookie2`响应，
![cookie-filter-1](/images/how-tomcat-works-chapter-nine-session/cookie-filter-1.png)
其中第一条`Domain=".joes-hardware.com"`，表示只有当向诸如`XXX.joes-hardware.com`这样的域名的时候才发送`ID=29046`这个键值对，这才是Cookie的主要有效信息。注意，用来过滤的`domain`和`path`也会随着Cookie一起发送。如果客户端又对路径`/tools/cordless/specials.html`发起一次请求，就会发送下面这条很长的`Cookie`请求首部。5条Cookie里只有第3条的信息不符合要求，所以1,2,4,5条Cookie的信息都会加入请求头，而且会做一定程度的缩减，
![cookie-filter-2](/images/how-tomcat-works-chapter-nine-session/cookie-filter-2.png)

### 用Cookie实现Session的例子
下面的例子演示了一次`www.amazon.com`网站访问中的事务序列，
![amazon-1](/images/how-tomcat-works-chapter-nine-session/amazon-1.png)
![amazon-2](/images/how-tomcat-works-chapter-nine-session/amazon-2.png)
![amazon-3](/images/how-tomcat-works-chapter-nine-session/amazon-3.png)

### URL重写
Cookie因为安全的原因很有可能被客户禁用。这时候，URL重写技术也可以实现Session。

URL重写就是当用户浏览站点时，Web服务器动态生成超链接，把用户特定的信息加入到URL中，修改后的URL被称为 **"胖URL"**。

比如下面的例子，用户在浏览`www.amazon.com`的时候，网站为用户分配了一个专用标识`002-1145265-8016838`，然后在所有商品链接的后面都插入这个标识，这样用户点击任何一个链接，发出的请求行URL中都包含自己的身份标识，
![fat-url](/images/how-tomcat-works-chapter-nine-session/fat-url.png)

URL重写技术要求网站所有页面都是动态生成的，因此每个客户访问的都是自己独有的一个副本。但胖URL有以下几个缺点，
1. 链接逃逸：用户如果跳转到其他网站，再回来，或者直接输入网址访问某个资源，就无意中“逃出”了胖URL的会话。
2. 会话是非持久的：除非客户收藏了特定的网址作为固定入口，否则用户关闭全部网页就逃脱了胖URL的控制，会话就结束了。
3. 破坏缓存：因为所有页面都是动态生成的，提前缓存一些资源提高效率变得不可能。

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
Java支持对象的序列化。只要实现了`Serializable`接口的对象都可以转换成一个字节序列。和字节码的`.class`类文件一样，可以在需要的时候将这个字节序列完全恢复为原来的对象。

要序列化一个对象，首先要创建一个`OutputStream`对象，然后把它封装在一个`ObjectOutputStream`对象里，然后调用`writeObject()`即可将对象序列化。
```java
String s = "Hello World!";
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("mystr.out")); // 储存到本地文件 "./mystr.out" 中
out.writeObject(s);
```
反向的过程是调用`ObjectInputStream`的`readObject()`函数。
```java
ObjectInputStream in = new ObjectInputStream(new FileInputStream("mystr.out")); // 从本地文件 "./mystr.out" 中读取对象
String s = in.readObject();
```

对象序列化不仅保存了“对象”本身，而且能追踪对象内所包含的所有引用，并保存那些对象。这种情况被称为 **“对象网”**。因此如果想写一套自己的对象序列化机制是很麻烦的。尽量不要自己动手，用Java提供的库方法即可。

### `Session`，`Manager`和`Store`的架构
Session是一个会话的抽象。Manager封装并管理着存放多个Session的容器：HashMap。Store是Session对象持久化储存介质的抽象。部分长时间不活动的Session会被从内存换出到持久化介质储存。

### 集群

### 应用
访问应用，
```
http://localhost:8080/app1/Session
```
我在输入框设置了一个value:`HelloWei`，然后退出Tomcat服务器。产生一个Session持久化文件`/Users/Wei/github/HowTomcatWorks/webapps/work/_/_/app1/SESSIONS.ser`。然后，马上（之前的Session没有过期之前）重新访问应用，显示服务器记住了之前的Session，
![app1-1](/images/how-tomcat-works-chapter-nine-session/app1-1.png)

在控制台打印出我的浏览器发给服务器的Cookie，看到`name = JSESSIONID`，`value = EF50019923BE6051B2EDB47DB1C01A22`，说明我的浏览器是通过Cookie记录Session ID的。
```
SessionServlet -- service
Session ID = EF50019923BE6051B2EDB47DB1C01A22
Session ID to String = EF50019923BE6051B2EDB47DB1C01A22
Recieve Cookie:
	[JSESSIONID,EF50019923BE6051B2EDB47DB1C01A22,null,null]
```
