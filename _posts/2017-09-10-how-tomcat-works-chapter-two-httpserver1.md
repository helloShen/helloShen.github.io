---
layout: post
title: "How Tomcat Works - Chapter 2 - HttpServer1"
date: 2017-09-10 00:53:20
author: "Wei SHEN"
categories: ["java","web","how tomcat works"]
tags: ["socket","http","io","url","uri"]
description: >
---


### 加载Servlet类
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

### 资源路径
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
