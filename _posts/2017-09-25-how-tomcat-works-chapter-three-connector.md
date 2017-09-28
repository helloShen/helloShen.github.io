---
layout: post
title: "How Tomcat Works - Chapter 3 - Connector"
date: 2017-09-25 16:08:37
author: "Wei SHEN"
categories: ["web","java","how tomcat works"]
tags: ["socket","io","tcp","http"]
description: >
---

### 摘要
第三章主要演示了 **连接器**。连接器的主要职责有3：
1. 监听客户端的TCP连接请求。创建 **已连接套接字** `Socket`实例
2. 解析HTTP消息
3. 构造好一对I/O流`HttpServletRequest`和`HttpServletResponse`实例

这一章，这3个任务分别由三个类完成，
1. `HttpConnector`监听客户端的TCP连接请求
2. `HttpProcessor`解析HTTP消息
3. `HttpRequest`和`HttpResponse`类分别实现了`HttpServletRequest`和`HttpServletResponse`接口

其中有2个比较重要的点：
1. 怎么解析HTTP消息？实际由现成的轮子`org.appache.catalina.connector.http.SocketInputStream`完成
2. 怎么把一个`PrintWriter`封装到`HttpResponse`里，并且让这个`PrintWriter`的`write()`方法能接收`char[]`字符流，然后输入`byte[]`字节流。

一个连接器的职责边界就在于构造好一对I/O流`HttpServletRequest`和`HttpServletResponse`。再往后调用`Servlet#service(ServletRequest request, ServletResponse response)`之后就进入了 **Servlet容器** 的职责范围。

### `HttpConnector`监听客户端的TCP连接请求

### `HttpProcessor`解析HTTP消息

#### 回顾HTTP消息
一个HTTP消息如下，
```bash
### 请求行
GET /servlet/ModernServlet?userName=tarzan&password=pwd HTTP/1.1

### 消息头
Server: Microsoft-IIS/4.0
Date: Mon, 5 Jan 2004 13:13:12 GMT
Content-Type: text/html
Last-Modified: Mon, 5 Jan 2004 13:13:12 GMT
Content-Length: 11

### 消息正文
... ...
```

主要分为3部分，
1. 请求行：请求消息的第一行。
    * method: `GET`
    * URI: `/servlet/ModernServlet?userName=tarzan&password=pwd`
    * protocol: `HTTP/1.1`
2. 消息头：一系列表示元信息的“键-值”对
3. 消息正文：在一个空行之后，是消息内容正文

#### 现成的轮子 `org.appache.catalina.connector.http.SocketInputStream`
`HttpProcessor`没有自己造轮，而是利用现成的轮子`org.appache.catalina.connector.http.SocketInputStream`解析HTTP消息。

本地的`com.ciaoshen.howtomcatworks.ex03.connector.SocketInputStream`是`org.appache.catalina.connector.http.SocketInputStream`的副本。

首先调用`SocketInputStream#readRequestLine()`把第一行 “请求行” 切成3段。完整的请求行如下，
```bash
GET /servlet/ModernServlet?userName=tarzan&password=pwd HTTP/1.1
```
切成3段后，
* method:              GET
* URI(包含查询字符串):   servlet/ModernServlet?userName=tarzan&password=pwd
* protocol:            HTTP/1.1

解析后的数据被封装在一个`HttpRequestLine`类实例中。
```java
private HttpRequestLine requestLine = new HttpRequestLine();

// ... ... omitted code

input.readRequestLine(requestLine);
```

然后调用`SocketInputStream#readHeader()`解析第二部分 “消息头”。消息头的本质就是一系列以冒号`:`分割的属性键-值对。每一个键值对都储存在`HttpHeader`类的实例里。
```bash
### 键-值对1
Server: Microsoft-IIS/4.0
### 键-值对2
Date: Mon, 5 Jan 2004 13:13:12 GMT
### 键-值对3
Content-Type: text/html
### 键-值对4
Last-Modified: Mon, 5 Jan 2004 13:13:12 GMT
### 键-值对5
Content-Length: 11
```

#### `SocketInputStream`解析后的数据赋值给`HttpRequest`实例
解析出来的数据，无论是`HttpRequestLine`还是`HttpHeader`，最后都被逐一赋值到`HttpRequest`实例里。 在`HttpRequest`类里有专门的字段来存放请求行的`method`,`uri`和`protocol`，还有一个`HashMap`专门用来储存消息头里的键值对。
```java
public class HttpRequest implements HttpServletRequest {

    private String contentType;
    private int contentLength;
    private InetAddress inetAddress;
    private InputStream input;
    private String method;              // 请求行的method
    private String protocol;            // 请求行的protocol
    private String queryString;         // 请求行的uri的后半部分
    private String requestURI;          // 请求行uri的前半部分
    private String serverName;
    private int serverPort;
    private Socket socket;
    private boolean requestedSessionCookie;
    private String requestedSessionId;
    private boolean requestedSessionURL;

    // ... ... omitted code

    protected HashMap headers = new HashMap();  // 请求头的键-值对

    // ... ... omitted code
}
```

HTTP消息的数据解析完赋值给`HttpRequest`，当`ModernServlet`拿到`HttpRequest`实例的引用之后，就是很简单地把各部分数据打印出来。

### 在`HttpResponse`里封装`PrintWriter`
前面的工作`HttpProcessor`成功解析了Http请求消息，把解析后的数据赋值给了`HttpRequest`实例。接下来要构造`HttpResponse`实例。


#### `HttpResponse`只能通过`getWriter()`方法拿到`PrintWriter`实例引用
`HttpProcessor`类在构造`HttpResponse`的时候就传入了`Socket#OutputStream`，
```java
public class HttpProcessor {

        // omitted code ... ...
    public void process(Socket socket) {
        SocketInputStream input = null;
        OutputStream output = null;
        try {
          input = new SocketInputStream(socket.getInputStream(), 2048);
          output = socket.getOutputStream();

          // create HttpRequest object and parse
          request = new HttpRequest(input);

          // create HttpResponse object
          response = new HttpResponse(output);

          // omitted code ... ...

        } catch (Exception e) {
            e.printStackTrace();
        }
    }      

    // omitted code ... ...

}
```

但Servlet程序是不能直接访问这个`OutputStream`类型字段的。`HttpResponse`实现`HttpServletResponse`接口。`HttpServletResponse`接口继承自`ServletResponse`接口。所以只能通过`getWriter()`方法可以拿到内部`PrintWriter`实例的引用，作为想客户端传送输出流的出口。

```java
public class PrimitiveServlet implements Servlet {

  // omitted code ... ...

  public void service(ServletRequest request, ServletResponse response)
    throws ServletException, IOException {
    /** 通过getWriter()方法拿大PrintWriter实例引用 */
    PrintWriter out = response.getWriter();

    /** 拿到PrintWriter以后，直接向客户端浏览器输出 */
    out.println("HTTP/1.1 200 OK");
    out.println("");
    out.println("Hello. Roses are red.");
    out.print("Violets are blue.");
  }

  // omitted code ... ...

}
```

#### 让`PrintWriter`输出字节流
`PrintWriter`继承自`Writer`，是面向字符流的输出。但HTTP协议需要传输的数据大部分是像图片这样的纯字节流。要让`PrintWriter`能传输字节流，需要利用利用`OutputStreamWriter`为中介，先将面向字节流的`OutputStream`封装到一个`OutputStreamWriter`实例里，然后再把这个`OutputStreamWriter`实例封装到一个`PrintWriter`实例里返回。这样，`PrintWriter#write()`方法实际调用的会是`OutputStream#write()`方法。

最简单的做法是像第二章`Response#getWriter()`方法这样，用直接用一个`OutputStream`实例构造`PrintWriter`。

```java
class Response implements ServletResponse {

    private static final int BUFFER_SIZE = 1024;
    Request request;
    OutputStream output;
    PrintWriter writer;

    // omitted code ... ...

    public PrintWriter getWriter() throws IOException {
        /**
         * autoflush is true, println() will flush,
         * but print() will not.
         * output is an instance of OutputStream
         */
        writer = new PrintWriter(output, true);
        return writer;
    }

    // omitted code ... ...
}
```
因为`PrintWriter(OutputStream out)`构造器会再调用另一个有两个参数的构造器，在构造`PrintWriter`之前，会先用`OutputStreamWriter`和`BufferedWriter`装饰，
```java
public PrintWriter(OutputStream out, boolean autoFlush) {
    /** out is an instance of OutputStream */
    this(new BufferedWriter(new OutputStreamWriter(out)), autoFlush);
    if (out instanceof java.io.PrintStream) {
        psOut = (PrintStream) out;
        // omitted code ... ...
    }
    // omitted code ... ...
}
```
这样`getWriter()`函数拿到的`PrintWriter`实例就可以输出字节流了。

但第三章的`getWriter()`方法要更复杂一点。先用`HttpResponse`构造一个`ResponseStream`实例。`ResponseStream`实际继承自`OutputStream`类，它的作用是强制在调用`OutputStream#write()`方法之前检查`HttpResponse`消息长度。

然后`ResponseStream`再通过`OutputStreamWriter`做为过度，构造出`ResponseWriter`实例。`ResponseWriter`就继承自`PrintWriter`类。装饰一层`ResponseWriter`的目的是为了让每个`print()`和`println()`方法都自动调用`flush()`方法。
```java
/**
 * 利用OutputStreamWriter为中介，将面向字节流的OutputStream封装在PrintWriter实例里返回
 */
public PrintWriter getWriter() throws IOException {
  /** "this" is an instance of HttpResponse */
  ResponseStream newStream = new ResponseStream(this);
  newStream.setCommit(false);
  OutputStreamWriter osr = new OutputStreamWriter(newStream, getCharacterEncoding());
  writer = new ResponseWriter(osr);
  return writer;
}
```

### Servlet容器
本章有两个`Servlet`容器实现类。一个和第二章一样相对简单的`PrimitiveServlet`，一个是这一章的`ModernServlet`。`PrimitiveServlet`直接实现了`Servlet`接口。直接在`service()`方法里提供服务逻辑。`ModernServlet`继承了`HttpServlet`骨架实现类，`service()`方法被架空，实际服务在`doGet()`方法里提供。

#### 加载Servlet容器
`ServletProcessor`类还是用`URLClassLoader`在运行时动态加载`PrimitiveServlet`和`ModernServlet`类，用`Class#newInstance()`方法创建实例，并调用此实例的`service()`入口方法。

#### `HttpServlet.service()`架空的入口
基本的`Servlet`的服务可以像`PrimitiveServlet`这样，直接在主入口`service()`方法里提供服务。
```java
public class PrimitiveServlet implements Servlet {

  // omitted code ... ...

  public void service(ServletRequest request, ServletResponse response)
    throws ServletException, IOException {
    System.out.println("Call PrimitiveServlet Service!");
    PrintWriter out = response.getWriter();
    /** HttpResponse里什么也没有，输出信息全靠硬编码打印 */
    out.println("HTTP/1.1 200 OK");         // 不加回应头浏览器会默认HTTP/0.9而报错
    out.println("");
    out.println("Hello. Roses are red.");
    out.print("Violets are blue.");
  }

  // omitted code ... ...
}
```

需要注意！`PrimitiveServlet`里不能直接向浏览器输出文本，至少要加上一个回应头，标明HTTP协议版本`HTTP/1.1`，否则多数浏览器会默认使用的是`HTTP/0.9`而报错。
```java
out.println("HTTP/1.1 200 OK");
```

但`HttpServlet`类比较特别。它是一个骨架实现，它的主入口`service()`函数，会根据传入的客户请求类型调用不同的方法。 具体就是看传入的`ServletRequest.method`字段，如果是`GET`，就调用`doGet()`函数，如果是`POST`就调用`doPost()`函数，等等。所以继承`HttpServlet`类，不需要重写`service()`函数，而是相应的重写`doGet()`或者`doPost()`等方法。

`ModernServlet`继承的就是`HttpServlet`。
```java
public class ModernServlet extends HttpServlet {

    // omitted code ... ...

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // some code here ... ...
    }

    // omitted code ... ...
}
```


### 关于`StringManager`
Tomcat用`StringManager`实例来处理错误消息。方法是将错误消息储存在多个扩展名为`.properties`文件中。具体是每个包有一个`.properties`文件储存这个包内的类产生的错误消息。每个`.properties`文件由一个`StringManager`实例管理。

一共有3个地方用到了`StringManager`，分别是：
* `com.ciaoshen.howtomcatworks.ex03.connector.RequestStream`
* `com.ciaoshen.howtomcatworks.ex03.connector.http.SocketInputStream`
* `com.ciaoshen.howtomcatworks.ex03.connector.http.HttpProcessor`

通过`getManager()`方法获得`StringManager`实例的引用。传进去的参数`Constants.PACK_HTTP`是下面例子里调用StringManager的`HttpProcessor`的完整包名：`com.ciaoshen.howtomcatworks.ex03.connector.http`。
```java
public class HttpProcessor {

    // omitted code ... ...

    /** The string manager for this package. */
    protected StringManager sm = StringManager.getManager(Constants.PACK_HTTP);
}
```

构造`StringManager`需要3个东西，
1. 包名(baseName): `getManager()`的参数提供。
2. 语言/地域(Local): `getManager()`会调用`java.util.Locale#getDefault()` `Locale.getDefault()`方法获得。
3. 加载器(loader): `java.lang.ClassLoader#getResource`这里不是加载一个类，只是加载文件。

`.properties`文件的路径和文件名就主要由“包名”加上"地域"加上后缀组成，
> `[classpath]/[basename]/LocalStrings_[local].properties`

比如我`com.ciaoshen.howtomcatworks.ex03.connector.http`包里的的`.properties`文件绝对路径和文件名就是：
> /Users/Wei/github/HowTomcatWorks/solutions/bin/com/ciaoshen/howtomcatworks/ex03/connector/http/LocalStrings.properties

其中：
* `/Users/Wei/github/HowTomcatWorks/solutions/bin/`是ClassPath
* `com/ciaoshen/howtomcatworks/ex03/connector/http`是包名的`.`替换成`/`
* `LocalStrings.properties`是规定的文件名加后缀

实际我`http`包下有3个`.properties`文件，
* `LocalStrings.properties`
* `LocalStrings_es.properties`
* `LocalStrings_jp.properties`

后两个分别是西班牙语和日语的版本。但最基本的只需要有`LocalStrings.properties`就可以保证正常工作，因为`StringManager`加载`.properties`的工作是由`java.util.ResourceBundle`完成，它会调用`java.util.Locale#getDefault()`和`Locale.getDefault()`方法找到我系统的国家和语言参数。比如我在加拿大魁北克，使用法语，系统的`Local`参数就是`fr_CA`。但这不是说我就必须有`LocalStrings_fr_CA.properties`. 因为`ResourceBundle`的算法是列出一系列的候选文件名单，如果能找到其中的一个，就正常加载。如果一个都没找到，最后保底会去找默认的`LocalStrings.properties`。如果连`LocalStrings.properties`都没有，才会抛出`java.util.MissingResourceException`，

```
Exception in thread "Thread-0" java.util.MissingResourceException: Can't find bundle for base name com.ciaoshen.howtomcatworks.connector.http.LocalStrings, locale fr_CA
	at java.util.ResourceBundle.throwMissingResourceException(ResourceBundle.java:1564)
	at java.util.ResourceBundle.getBundleImpl(ResourceBundle.java:1387)
	at java.util.ResourceBundle.getBundle(ResourceBundle.java:773)
	at org.apache.catalina.util.StringManager.<init>(StringManager.java:107)
	at org.apache.catalina.util.StringManager.getManager(StringManager.java:252)
	at com.ciaoshen.howtomcatworks.ex03.connector.http.HttpProcessor.<init>(HttpProcessor.java:50)
	at com.ciaoshen.howtomcatworks.ex03.connector.http.HttpConnector.run(HttpConnector.java:45)
	at java.lang.Thread.run(Thread.java:745)
```

上面的错误就是我在`http`包下没有`LocalStrings.properties`文件。创建这个文件以后就正常运行。

### 关于传输图片
传输图片的时候，需要准确计算好图片的大小，把长度放在HTTP Header里传给客户端浏览器。否则很可能传输不全，图片无法正常显示。

下面`sendStaticResource()`函数传输静态网页，只加请求行`HTTP/1.1 200 OK`，没有消息头，导致网页文本可以正常显示，图片无法显示，
```java
public void sendStaticResource() throws IOException {
  byte[] bytes = new byte[BUFFER_SIZE];
  FileInputStream fis = null;
  try {
    // 请求行
    output.write("HTTP/1.1 200 OK\r\n".getBytes());
    File file = new File(Constants.WEB_ROOT, request.getRequestURI());
    fis = new FileInputStream(file);
    System.out.println("Static Resource File: " + file.getPath());
    // 没有消息头

    // 空一行直接消息正文
    output.write("\n\r".getBytes());
    int ch = fis.read(bytes, 0, BUFFER_SIZE);
    while (ch!=-1) {
      output.write(bytes, 0, ch);
      System.out.println("\nLength = " + ch + "\n");
      System.out.write(bytes,0,ch);
      ch = fis.read(bytes, 0, BUFFER_SIZE);
    }
  } catch (FileNotFoundException e) {
    System.out.println(e);    
  }
  // omitted code ... ...
}
```

下面计算好图片大小，并且在消息头里告诉浏览器，就可以正常显示图片，
```java
public void sendStaticResource() throws IOException {
  byte[] bytes = new byte[BUFFER_SIZE];
  FileInputStream fis = null;
  try {
    /*********************************************************
     * 读取文件
     ********************************************************/
    File file = new File(Constants.WEB_ROOT, request.getRequestURI());
    fis = new FileInputStream(file);
    /*********************************************************
     * 先缓存所有正文，并计算长度长度。
     ********************************************************/
    int MAX_INFO = 4096;
    int totalLen = 0; // 资源长度
    byte[] info = new byte[MAX_INFO]; // 缓存
    int count = fis.read(bytes, 0, BUFFER_SIZE);
    while (count != -1) {
        for (int i = 0; i < count; i++) {
            if (totalLen == info.length) { info = Arrays.copyOf(info,info.length * 2); }
            info[totalLen++] = bytes[i];
        }
        count = fis.read(bytes, 0, BUFFER_SIZE);
    }
    /*********************************************************
     * 编辑带有"状态行"和"响应头"的HTTP头
     ********************************************************/
    String httpHeader = "HTTP/1.1 200 OK\r\n" +
        "Server: SHEN's First Java HttpServer\r\n" +
        "Content-Type: text/html\r\n" +
        "Content-Length: " + String.valueOf(totalLen) + "\r\n" +
        "\r\n"; // 将消息正文长度告知浏览器

    /*********************************************************
     * 写HTTP头
     ********************************************************/
    output.write(httpHeader.getBytes());
    output.write(info, 0, totalLen);
  } catch (FileNotFoundException e) {
    System.out.println(e);    
  }
// omitted code ... ...
}
```

### 项目中已经弃用的类
最初版本的Tomcat时间已经有点久远，例子中的很多类现在已经被弃用。这些类包括：
* RequestStream
* HttpHeader
* HttpRequestLine
* SocketInputStream
* ServletRequest
* HttpServletRequest#isRequestedSessionIdFromUrl()

另外编译的时候如果用`javac -Xlint`会有很多泛型的`rawtypes`警告。选择忽略即可。
