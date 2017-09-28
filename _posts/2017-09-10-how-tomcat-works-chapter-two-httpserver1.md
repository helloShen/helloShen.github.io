---
layout: post
title: "How Tomcat Works - Chapter 2 - HttpServer1"
date: 2017-09-10 00:53:20
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["socket","http","io","url","uri"]
description: >
---


### `HttpServer1.java`主入口
主要工作都在`await()`函数里，
* await()函数构造ServerSocket实例
* 利用ServerSocket#accept()函数和客户端建立连接。拿到Socket类实例的的引用。
* 从Socket实例再拿到一对InputStream和OutputStream
* 分别封装到Request类和Response类里。
* 根据任务类型分发任务，是请求静态资源，还是请求Servlet服务？


```java
package com.ciaoshen.howtomcatworks.ex02;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;


/*************************************************************************
 * 主入口
 * await()函数构造ServerSocket实例
 * 利用ServerSocket#accept()函数和客户端建立连接。拿到Socket类实例的的引用。
 * 从Socket实例再拿到一对InputStream和OutputStream
 * 分别封装到Request类和Response类里。
 * 根据任务类型分发任务，是请求静态资源，还是请求Servlet服务？
 ************************************************************************/
public class HttpServer1 {

  /**
   * WEB_ROOT常量被收拢到了Constants类。
   */

  // shutdown command
  private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

  // the shutdown command received
  private boolean shutdown = false;

  public static void main(String[] args) {
    HttpServer1 server = new HttpServer1();
    server.await();
  }

  public void await() {
    /** await()函数构造ServletSocket实例 */
    ServerSocket serverSocket = null;
    int port = 8080;
    try {
      serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
    }
    catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
    }

    // Loop waiting for a request
    while (!shutdown) {
      /** 利用ServerSocket#accept()函数和客户端建立连接。拿到Socket类实例的的引用。*/
      Socket socket = null;

      InputStream input = null;
      OutputStream output = null;

      try {
        System.out.println("HttpServer1 is waiting for client requests ...");
        socket = serverSocket.accept();

        /** 从Socket实例再拿到一对InputStream和OutputStream。*/
        input = socket.getInputStream();
        output = socket.getOutputStream();

        // create Request object and parse
        /* 分别封装到Request类和Response类里。*/
        Request request = new Request(input);
        request.parse();
        // create Response object
        Response response = new Response(output);
        response.setRequest(request);

        // check if this is a request for a servlet or a static resource
        /**
         * 根据任务类型分发任务，是请求静态资源，还是请求Servlet服务？
         * 我搜索“百里玄策”，收到的http请求如下，
         *      GET /index.html?king-of-glory-hero=%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96
         */
        if (request.getUri().contains("?king-of-glory-hero=")) {
          ServletProcessor1 processor = new ServletProcessor1();
          processor.process(request, response);
        } else { // 和第一章一样，请求静态资源
          StaticResourceProcessor processor = new StaticResourceProcessor();
          processor.process(request, response);
        }

        // Close the socket
        socket.close();
        //check if the previous URI is a shutdown command
        shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
      }
      catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
```

### 静态资源服务器`StaticResourceProcessor.java`
和第一章的任务一样， 参见第一章的Demo: <http://www.ciaoshen.com/java/web/how%20tomcat%20works/2017/07/05/how-tomcat-works-chapter-one-socket.html>

```java
package com.ciaoshen.howtomcatworks.ex02;

import java.io.IOException;

public class StaticResourceProcessor {

  public void process(Request request, Response response) {
    try {
      response.sendStaticResource();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}
```

### Servlet服务器：`ServletProcessor1.java`
Servlet服务器和普通静态资源服务器的区别就是它不是简单地提供静态资源，而是包含一套较为复杂的处理过程。而且代码被封装到一个独立的文件中。

`ServletProcessor1`的任务就是，
1. 解析收到的客户端请求
2. 识别它请求的是静态资源，还是Servlet服务？
3. 加载静态资源服务程序，或者Servlet服务程序。

解析URL要用到URL类，以及通过URLClassLoader类用一个URL定位并加载类文件。 最后用Class#newInstance()方法，创建Servlet实例。  

```java
package com.ciaoshen.howtomcatworks.ex02;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.File;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/******************************************************************************
 * 一个Servlet服务器类，需要实现`Servlet`接口，
 *`ServletProcessor1`的任务就是，
 *  1. 解析收到的客户端请求
 *  2. 识别它请求的是静态资源，还是Servlet服务？
 *  3. 加载静态资源服务程序，或者Servlet服务程序。
 *
 * 解析URL要用到URL类，以及通过URLClassLoader类用一个URL定位并加载类文件。
 * 最后用Class#newInstance()方法，创建Servlet实例。
 *****************************************************************************/
public class ServletProcessor1 {

  public void process(Request request, Response response) {
    /**************************************************************************
     * 记住：servelet使用的URI格式如下：
     *      /servlet/servletName
     * servletName是请求的servlet资源的类名
     *
     * 一般URL的通用格式如下：
     *      scheme:[//[user[:password]@]host[:port]][/path][?query][#fragment]
     *
     * 所以，我们我们用的URL中的
     * user域，password域，host域，port域，query域，fragment域都为空。
     *
     * 第一个域scheme域指的是protocol。代码里选用的是file协议。
     *************************************************************************/
    String uri = request.getUri();
    /**
     * 我搜索“百里玄策”，收到的http请求如下，
     *      GET /index.html?king-of-glory-hero=%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96
     *
     * 传进来的信息Request.getUri()是,
     *      index.html?king-of-glory-hero=%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96
     *
     * 要确定用哪个servlet，需要把 "?xxxxxxxx=" 这段抠出来，比如，
     *      king-of-glory-hero 对应的是 KingOfGloryServlet
     */
    // String servletName = uri.substring(uri.lastIndexOf("/") + 1);
    String servletName = "";
    String servletId = uri.substring(uri.indexOf("?")+1,uri.indexOf("="));
    System.out.println("Servlet Id = " + servletId);
    switch (servletId) {
        case "king-of-glory-hero": servletName = "com.ciaoshen.howtomcatworks.ex02.KingOfGloryServlet"; break;
        default: break;
    }
    URLClassLoader loader = null;

    try {
      // create a URLClassLoader
      URL[] urls = new URL[1];
      URLStreamHandler streamHandler = null;
      File classPath = new File(Constants.CLASS_PATH);
      System.out.println(">>>>>> Class Path = \"" + classPath.getPath() + "\"");
      // the forming of repository is taken from the createClassLoader method in
      // org.apache.catalina.startup.ClassLoaderFactory
      /***********************************************************************
       * 注意！这里调用的是URL的下面这个构造器：
       *        public URL(String protocol, String host, String file)
       * 其中第一个参数"file"是指：file协议。
       * 第二个参数host被置空：null
       * 主要工作的是三个参数：file。它被赋值成classPath，即之前定义的WEB_ROOT，
       * 它指明了我们要到哪里去找Class。
       *
       * 上面这个三个参数的URL构造器最终调用的是一个有5个参数的构造器，
       *        public URL(String protocol, String host, int port, String file,
               URLStreamHandler handler)
       * 其中，port域被默认设为-1，handler域为null。
       **********************************************************************/
      String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
      System.out.println("Repository = " + repository);
      // the code for forming the URL is taken from the addRepository method in
      // org.apache.catalina.loader.StandardClassLoader class.
      /***********************************************************************
       * 注意！这里调用的是URL的下面这个构造器：
       *        public URL(URL context, String spec, URLStreamHandler handler)
       * 其中，第一个参数context和第三个参数handler都可以被设为null。
       * 第二个参数spec，就是之前构造的repository。就是告诉URLClassLoader到哪里去找Class.
       **********************************************************************/
      urls[0] = new URL(null, repository, streamHandler);
      loader = new URLClassLoader(urls);
    }
    catch (IOException e) {
      System.out.println(e.toString() );
    }
    Class<?> myClass = null;
    try {
      /******************************************************************
       * 所以，Servlet的本质就是用ClassLoader加载Class。
       *****************************************************************/
      System.out.println("Servlet Name = " + servletName);
      myClass = loader.loadClass(servletName);
    }
    catch (ClassNotFoundException e) {
      System.out.println(e.toString());
    }

    Servlet servlet = null;

    try {
      /******************************************************************
       * 创建已载入的servlet类的一个实例，并将其向下转型为javax.servlet.servlet
       * 并调用其service()方法。
       *****************************************************************/
      servlet = (Servlet) myClass.newInstance();
      servlet.service((ServletRequest) request, (ServletResponse) response);
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
    catch (Throwable e) {
      System.out.println(e.toString());
    }

  }
}
```

#### 关键点1：怎么构造Servlet类文件的路径？
首先Servlet资源的统一根目录是一个`File`，其实相当于我的Servlet类文件的`CLASSPATH`。
```java
File classPath = new File(Constants.CLASS_PATH);
```
用`classPath.getPath()`方法，显示根目录在我系统的这个位置，
```bash
"/Users/Wei/github/HowTomcatWorks/solutions/bin"
```
然后给这个路径前面加上一个 **协议**，封装成一个`URL`。一个标准URL的结构如下，这里的 “协议”就是最开头的`scheme`，

> **scheme:[//[user[:password]@]host[:port]][/path][?query][#fragment]**

直接用`java.net.URL`类构造URL。因为这个URL是用来定位我的`com.ciaoshen.howtomcatworks.ex02.KingOfGloryServlet`编译好的.class文件，所以可以用`file`协议。这里为什么要用URL，而不是URI，其实都可以，只不过因为类加载器是`URLClassLoader`。
```java
String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
```
封装好的URL打印出来如下，
```bash
file:/Users/Wei/github/HowTomcatWorks/solutions/bin/
```
把上面这个路径传递给`URLClassLoader`，调用`loadClass()`函数就可以运行时动态加载编译好的.class文件。
```java
URLClassLoader loader = new URLClassLoader(urls)
Class<?> myClass = loader.loadClass("com.ciaoshen.howtomcatworks.ex02.KingOfGloryServlet");
```

### Servlet程序：`KingOfGloryServlet.java`
小Demo里不放太复杂的业务逻辑，只是简单地解析HTML表单通过GET方法传递过来的请求消息，
并返回响应的资源。

假设我们服务器提供 《王者荣耀》游戏英雄属性资料的查询，通过解析GET方法传过来的英雄名字，
返回相应英雄资料的图片。

这里有两个关键点：
* 第一，浏览器发过来的消息用UTF-8编码过了，需要用URLDecoder.decode()方法解码
* 第二，因为ServletRequest和ServletResponse拿不到面向字节流的I/O 所以又转型回Request和Response类。不提倡这样做。用两个代理类， RequestFacade和ResponseFacade类可以禁止这样的操作。

**注意！** 这里的第二点当初认为`Response#getWriter()`拿到的是面向字符流的`PrintWriter`实例，是不对的。这个`PrintWriter`内部实际封装的是一个`OutputStream`实例。所以最终的输出还是面向字节流的。关键就是在构造`PrintWriter`的过程中用了一个`OutputStreamWriter`对象作为`PrintWriter`和`OutputStream`的桥梁。

```java
package com.ciaoshen.howtomcatworks.ex02;

import javax.servlet.*;
// import java.io.IOException;
// import java.io.PrintWriter;
import com.ciaoshen.howtomcatworks.ex02.Request;
import com.ciaoshen.howtomcatworks.ex02.Response;
import java.io.*;
import java.net.URLDecoder;

/*****************************************************************************
 * 小Demo里不放太复杂的业务逻辑，只是简单地解析HTML表单通过GET方法传递过来的请求消息，
 * 并返回响应的资源。
 *
 * 假设我们服务器提供 《王者荣耀》游戏英雄属性资料的查询，通过解析GET方法传过来的英雄名字，
 * 返回相应英雄资料的图片。
 *
 * 这里有两个关键点：
 *      第一，浏览器发过来的消息用UTF-8编码过了，需要用URLDecoder.decode()方法解码
 *      第二，因为ServletRequest和ServletResponse拿不到面向字节流的I/O
 *          所以又转型回Resquest和Response类。不提倡这样做。用两个代理类，
 *          RequestFacade和ResponseFacade类可以禁止这样的操作。
 ****************************************************************************/

class KingOfGloryServlet implements Servlet {

  public void init(ServletConfig config) throws ServletException {
    System.out.println("init");
  }

  private static final int BUFFER_SIZE = 1024;
  private static byte[] bytes = new byte[BUFFER_SIZE];

  public void service(ServletRequest request, ServletResponse response)
    throws ServletException, IOException {
    FileInputStream fis = null;
    try {
        // 为了拿到uri，必须转型回Request
        String uri = ((Request)request).getUri();
        String fileNameInUtf8 = uri.substring(uri.indexOf("=")+1);
        /**
         * 把浏览器传过来的 %E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96 转换回 “百里玄策”
         * 浏览器用的是UTF-8编码，每个中文3个字节，
         *   百 = E7 99 BE
         *   里 = E9 87 8C
         *   玄 = E7 8E 84
         *   策 = E7 AD 96
         */
        String fileName = URLDecoder.decode(fileNameInUtf8,"UTF-8") + ".jpg";
        File file = new File(Constants.WEB_ROOT + File.separator + "images",fileName);
        fis = new FileInputStream(file);

        // 回应消息加HTTP头
        String httpHeader = "HTTP/1.1 200 OK\r\n\r\n"; // 最简单的HTTP消息头，只有状态行

        /*
         * 注意！ServletResponse的getWriter()函数拿到的PrintWriter实例是面向字节留的
         *     public PrintWriter getWriter() {
         *         ... ...
         *     }
         *
         * 因为实际用来构造这个PrintWriter的是一个OutputStreamWriter实例，
         * 这个OutputStreamWriter实例内部封装的是一个OutputStream实例。
         * 所以PrintWriter#write(char[])方法，实际调用的是OutputStream#print(byte[])
         */
        ((Response)response).output.write(httpHeader.getBytes());
        int ch = fis.read(bytes,0,BUFFER_SIZE);
        while (ch != -1) {
            ((Response)response).output.write(bytes,0,ch);
            ch = fis.read(bytes,0,BUFFER_SIZE);
        }
    } catch (FileNotFoundException e) {
      String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
        "Content-Type: text/html\r\n" +
        "Content-Length: 23\r\n" +
        "\r\n" +
        "<h1>File Not Found</h1>";
      ((Response)response).output.write(errorMessage.getBytes());
    } finally {
      if (fis!=null)
        fis.close();
    }
  }

  public void destroy() {
    System.out.println("destroy");
  }

  public String getServletInfo() {
    return null;
  }
  public ServletConfig getServletConfig() {
    return null;
  }

}
```

#### 第二个关键：UTF-8解码
已连接客户端发来的请求，从一个字节流`InputStream`接收，
```java
InputStream input = socket.getInputStream();
```
这个进入的字节流进一步被包装到`Request`类，
```java
Request request = new Request(input);
```
浏览器页面填写下面表单，向服务器发送HTTP请求，
```html
<form method = "get">
    请输入英雄名字:<br>
        <input type="text" name="king-of-glory-hero" value="">
    <br>
    <br><br>
    <input type="submit" value="">
</form>
```
对应浏览器中的地址如下，
```bash
http://127.0.0.1:8080/index.html?king-of-glory-hero=百里玄策
```
用`InputStream.read()`方法读取浏览器发过来的HTTP请求消息如下，用`GET`方法请求资源，
```bash
GET /index.html?king-of-glory-hero=%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96 HTTP/1.1
Host: 127.0.0.1:8080
Upgrade-Insecure-Requests: 1
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/603.3.8 (KHTML, like Gecko) Version/10.1.2 Safari/603.3.8
Accept-Language: fr-ca
Accept-Encoding: gzip, deflate
Connection: keep-alive
```
经过`Request#parseUri()`函数的解析以后，得到如下资源标识符`URI`，
```bash
/index.html?king-of-glory-hero=%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96
```
这里比较麻烦的是请求消息里有中文字符`百里玄策`。"百里玄策"的的Unicode是`767e 91cc 7384 7b56`，用UTF-8编码成了`E799BE E9878C E78E84 E7AD96`（UTF-8每个中文用3个字节编码），然后每个字节前面加上了`%`，变成`%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96`，

> URL的编码规范 RFC3986 可知浏览器编码 URL 是将非 ASCII 字符按照某种编码格式编码成 16 进制数字然后将每个 16 进制表示的字节前加上“%”，所以最终的 URL 就成了上图的格式了。

拿到`%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96`，去掉`%`，然后把十六进制转换成二进制，虽然可以得到实际的UTF-8编码，但要解码UTF-8（去掉UTF-8的头）这一步，不可能手动做，需要用现成的工具。现在的情况是，比如`百`这个字，
```bash
"百" = "E7 99 BE"      // UTF-8编码成3个字节以后，写成16进制

"E7" = "1110 0111"    // 3个字节分别转换成二进制
"99" = "1001 1001"
"BE" = "1011 1110"
```
3个字节分别是带着UTF-8头的，关键就是怎么去掉这个头，解码成原始Unicode字节流。
```bash
3个字节
"11100111 10011001 10111110"
需要分别去掉UTF-8头，解析成原始Unicode字节流：
"01110110 01111110"
也就是:
"767e"
```
这个转码工作可以用`URLDecoder.decode()`函数来做，而且连`%`都不用预处理，直接一步到位转换成`String`。
```java
String fileNameInUtf8 = "%E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96";
/**
 * 把浏览器传过来的 %E7%99%BE%E9%87%8C%E7%8E%84%E7%AD%96 转换回 “百里玄策”
 * 浏览器用的是UTF-8编码，每个中文3个字节，
 *   百 = E7 99 BE
 *   里 = E9 87 8C
 *   玄 = E7 8E 84
 *   策 = E7 AD 96
 */
String fileName = URLDecoder.decode(fileNameInUtf8,"UTF-8"); // 百里玄策
```

### `Request.java`伪实现`ServletRequest`接口

```java
package com.ciaoshen.howtomcatworks.ex02;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;


class Request implements ServletRequest { // 仅包内可见，不要影响其他章节的练习

  private InputStream input;
  private String uri;

  public Request(InputStream input) {
    this.input = input;
  }

  public String getUri() {
    return uri;
  }

  private String parseUri(String requestString) {
    int index1, index2;
    index1 = requestString.indexOf(' ');
    if (index1 != -1) {
      index2 = requestString.indexOf(' ', index1 + 1);
      if (index2 > index1)
        return requestString.substring(index1 + 1, index2);
    }
    return null;
  }

  public void parse() {
    // Read a set of characters from the socket
    StringBuffer request = new StringBuffer(2048);
    int i;
    byte[] buffer = new byte[2048];
    try {
      i = input.read(buffer);
    }
    catch (IOException e) {
      e.printStackTrace();
      i = -1;
    }
    for (int j=0; j<i; j++) {
      request.append((char) buffer[j]);
    }
    System.out.print(request.toString());
    uri = parseUri(request.toString());
  }

  /* implementation of the ServletRequest*/
  public Object getAttribute(String attribute) {
    return null;
  }

  @SuppressWarnings("rawtypes")
  public Enumeration getAttributeNames() {
    return null;
  }

  @SuppressWarnings("deprecation")
  public String getRealPath(String path) {
    return null;
  }

  public RequestDispatcher getRequestDispatcher(String path) {
    return null;
  }

  public boolean isSecure() {
    return false;
  }

  public String getCharacterEncoding() {
    return null;
  }

  public int getContentLength() {
    return 0;
  }

  public String getContentType() {
    return null;
  }

  public ServletInputStream getInputStream() throws IOException {
    return null;
  }

  public Locale getLocale() {
    return null;
  }

  @SuppressWarnings("rawtypes")
  public Enumeration getLocales() {
    return null;
  }

  public String getParameter(String name) {
    return null;
  }

  @SuppressWarnings("rawtypes")
  public Map getParameterMap() {
    return null;
  }

  @SuppressWarnings("rawtypes")
  public Enumeration getParameterNames() {
    return null;
  }

  public String[] getParameterValues(String parameter) {
    return null;
  }

  public String getProtocol() {
    return null;
  }

  public BufferedReader getReader() throws IOException {
    return null;
  }

  public String getRemoteAddr() {
    return null;
  }

  public String getRemoteHost() {
    return null;
  }

  public String getScheme() {
   return null;
  }

  public String getServerName() {
    return null;
  }

  public int getServerPort() {
    return 0;
  }

  public void removeAttribute(String attribute) {
  }

  public void setAttribute(String key, Object value) {
  }

  public void setCharacterEncoding(String encoding)
    throws UnsupportedEncodingException {
  }

}

```

### `Response.java`伪实现`ServletResponse`接口

```java
package com.ciaoshen.howtomcatworks.ex02;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;

class Response implements ServletResponse { // 仅包内可见，不要影响其他章节的练习

  private static final int BUFFER_SIZE = 1024;
  Request request;
  OutputStream output;
  PrintWriter writer;

  public Response(OutputStream output) {
    this.output = output;
  }

  public void setRequest(Request request) {
    this.request = request;
  }

  /* This method is used to serve a static page */
  public void sendStaticResource() throws IOException {
    byte[] bytes = new byte[BUFFER_SIZE];
    FileInputStream fis = null;
    try {
      /* request.getUri has been replaced by request.getRequestURI */
      File file = new File(Constants.WEB_ROOT, request.getUri());
      System.out.println("Web Root = " + Constants.WEB_ROOT);
      System.out.println("File Path = " + file.getName());
      fis = new FileInputStream(file);
      /*
         HTTP Response = Status-Line
           *(( general-header | response-header | entity-header ) CRLF)
           CRLF
           [ message-body ]
         Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
      */
      String httpHeader = "HTTP/1.1 200 OK\r\n\r\n"; // 最简单的HTTP消息头，只有状态行
      output.write(httpHeader.getBytes());
      System.out.println(httpHeader);
      int ch = fis.read(bytes, 0, BUFFER_SIZE);
      while (ch!=-1) {
        output.write(bytes, 0, ch);
        System.out.println(new String(bytes));
        ch = fis.read(bytes, 0, BUFFER_SIZE);
      }
    }
    catch (FileNotFoundException e) {
      String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
        "Content-Type: text/html\r\n" +
        "Content-Length: 23\r\n" +
        "\r\n" +
        "<h1>File Not Found</h1>";
      output.write(errorMessage.getBytes());
    }
    finally {
      if (fis!=null)
        fis.close();
    }
  }


  /** implementation of ServletResponse  */
  public void flushBuffer() throws IOException {
  }

  public int getBufferSize() {
    return 0;
  }

  public String getCharacterEncoding() {
    return null;
  }

  public Locale getLocale() {
    return null;
  }

  public ServletOutputStream getOutputStream() throws IOException {
      return null;
  }

  /**
   * 为了传输图片，不需要字符流，要的是字节流。
   */
  public PrintWriter getWriter() throws IOException {
    // autoflush is true, println() will flush,
    // but print() will not.

    // writer = new PrintWriter(output, true);
    // return writer;
    return null;
  }

  public boolean isCommitted() {
    return false;
  }

  public void reset() {
  }

  public void resetBuffer() {
  }

  public void setBufferSize(int size) {
  }

  public void setContentLength(int length) {
  }

  public void setContentType(String type) {
  }

  public void setLocale(Locale locale) {
  }
}
```

### 代理类`RequestFacade`和`ResponseFacade`可以阻止用户转型回`Request`和`Response`
原理就是实际工作是由内部的自由字段`Request`和`Response`来做，但用`RequestFacade`和`ResponseFacade`包装过以后，不能转型成`Request`和`Response`了。而且内部字段也是私有的，用户拿不到它的引用。

#### `RequestFacade.java`
```java
package com.ciaoshen.howtomcatworks.ex02;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

public class RequestFacade implements ServletRequest {

  private ServletRequest request = null;

  public RequestFacade(Request request) {
    this.request = request;
  }

  /* implementation of the ServletRequest*/
  public Object getAttribute(String attribute) {
    return request.getAttribute(attribute);
  }

  public Enumeration getAttributeNames() {
    return request.getAttributeNames();
  }

  public String getRealPath(String path) {
    return request.getRealPath(path);
  }

  public RequestDispatcher getRequestDispatcher(String path) {
    return request.getRequestDispatcher(path);
  }

  public boolean isSecure() {
    return request.isSecure();
  }

  public String getCharacterEncoding() {
    return request.getCharacterEncoding();
  }

  public int getContentLength() {
    return request.getContentLength();
  }

  public String getContentType() {
    return request.getContentType();
  }

  public ServletInputStream getInputStream() throws IOException {
    return request.getInputStream();
  }

  public Locale getLocale() {
    return request.getLocale();
  }

  public Enumeration getLocales() {
    return request.getLocales();
  }

  public String getParameter(String name) {
    return request.getParameter(name);
  }

  public Map getParameterMap() {
    return request.getParameterMap();
  }

  public Enumeration getParameterNames() {
    return request.getParameterNames();
  }

  public String[] getParameterValues(String parameter) {
    return request.getParameterValues(parameter);
  }

  public String getProtocol() {
    return request.getProtocol();
  }

  public BufferedReader getReader() throws IOException {
    return request.getReader();
  }

  public String getRemoteAddr() {
    return request.getRemoteAddr();
  }

  public String getRemoteHost() {
    return request.getRemoteHost();
  }

  public String getScheme() {
   return request.getScheme();
  }

  public String getServerName() {
    return request.getServerName();
  }

  public int getServerPort() {
    return request.getServerPort();
  }

  public void removeAttribute(String attribute) {
    request.removeAttribute(attribute);
  }

  public void setAttribute(String key, Object value) {
    request.setAttribute(key, value);
  }

  public void setCharacterEncoding(String encoding)
    throws UnsupportedEncodingException {
    request.setCharacterEncoding(encoding);
  }

}
```

#### `ResponseFacade.java`
```java
package com.ciaoshen.howtomcatworks.ex02;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;

public class ResponseFacade implements ServletResponse {

  private ServletResponse response;
  public ResponseFacade(Response response) {
    this.response = response;
  }

  public void flushBuffer() throws IOException {
    response.flushBuffer();
  }

  public int getBufferSize() {
    return response.getBufferSize();
  }

  public String getCharacterEncoding() {
    return response.getCharacterEncoding();
  }

  public Locale getLocale() {
    return response.getLocale();
  }

  public ServletOutputStream getOutputStream() throws IOException {
    return response.getOutputStream();
  }

  public PrintWriter getWriter() throws IOException {
    return response.getWriter();
  }

  public boolean isCommitted() {
    return response.isCommitted();
  }

  public void reset() {
    response.reset();
  }

  public void resetBuffer() {
    response.resetBuffer();
  }

  public void setBufferSize(int size) {
    response.setBufferSize(size);
  }

  public void setContentLength(int length) {
    response.setContentLength(length);
  }

  public void setContentType(String type) {
    response.setContentType(type);
  }

  public void setLocale(Locale locale) {
    response.setLocale(locale);
  }

}
```

### HTML表单
就是一个简单的`<form>`让用户填写需要查询的英雄名字。
```HTML
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>王者荣耀 - 英雄技能</title>
</head>
<body>
    <form method = "get">
        请输入英雄名字:<br>
            <input type="text" name="king-of-glory-hero" value="">
        <br>
        <br><br>
        <input type="submit" value="">
    </form>
</body>
</html>
```
