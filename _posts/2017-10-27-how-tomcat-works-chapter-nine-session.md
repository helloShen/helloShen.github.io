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

### Session和“持久连接”的区别
Session和持久连接是两个不同层面的东西。


#### 持久连接的持久是指的TCP连接的持久
**“HTTP事务”** 由一次完整的HTTP请求和HTTP响应构成。

`HTTP/1.0`的时候，每次HTTP事务都要建立一个新的TCP连接。当服务器将本次请求的资源返回后就会断开于客户浏览器的TCP连接。虽然通过显式加`Connection: Keep-Alive`也可以支持持久连接，但这是工程上的补充支持，本身`HTTP/1.0`协议官方不支持。

代码细节在P38页内容。

`HTTP/1.1`默认支持持久连接。具体操作上就是在每次服务器返回请求资源以后，根据具体参数决定是否断开当前TCP连接，回收套接字。

代码细节在P64页内容。

#### HTTP连接和事务的关系
这里要弄清楚一个细节，
TCP连接和Socket是对应的概念。一个TCP连接用一个Socket文件描述符标识。
事务和[Request/Response]是对应的概念。

#### 不要和HTTP的串行和并行搞混
HTTP并行是指为了传输一个网页，同一个客户可以同时发起多个连接。持久连接都是在一个连接里发生的事情。

#### `HTTP/1.1`的`content-length`和代码块新特性都是为“持久连接”服务
细节在P54页内容

### Session是比HTTP更高的应用程序
Session是为了在横跨多个HTTP事务的过程中，让服务器知道这是服务于同一个客户。而TCP连接不能标识客户的唯一性，因为存在HTTP并行，一个客户同时发起多个TCP连接。

关键点：Session用来标识一个独立的客户。不是HTTP事务，也不是TCP连接。

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
