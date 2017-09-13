---
layout: post
title: "Berkeley Socket API"
date: 2017-09-12 21:25:18
author: "Wei SHEN"
categories: ["operating system","web"]
tags: ["socket","tcp","ip"]
description: >
---

### 前言
`Socket`是向程序员提供`TCP/IP`服务的一个抽象。标准伯克利套接字`Berkeley Socket API`提供的函数如下，

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

这篇文章主要记录一下UNIX环境下，伯克利套接字这些函数需要注意的一些点。


### socket()函数
`socket()`函数的函数签名如下，
```c
#include <sys/socket.h>

// 若成功，返回非负描述符。若出错，则返回-1.
int socket(int family, int type, int protocol);
```
其中，
* `family`参数：指明协议族，比如IPv4(`AF_INET`),IPv6(`AF_INET6`),等。
* `type`参数：指明套接字类型。
* `protocol`参数：标明使用的协议类型。（和family指的IP协议不同，protocol指的是TCP,UDP或者SCTP协议中的一个）

![socket-family](/images/berkeley-socket-api/socket-family.png)
![socket-type-protocol](/images/berkeley-socket-api/socket-type-protocol.png)
![socket-family-type](/images/berkeley-socket-api/socket-family-type.png)

比如，一个基于`TCP/IPv4`协议的套接字，初始化参数设置如下，
```c
int sockfd = socket(AF_INET,SOCK_RAW,IPPROTO_TCP);
```

### accept()函数
```c
// 若成功，返回非负描述符。若出错则返回-1.
int accept(int sockfd, struct sockaddr *cliaddr, socklen_t *addrlen);
```
* 参数`cliaddr`: 用来返回客户端的IP地址。
* 参数`addrlen`: 标明cliaddr套接字地址结构的长度。


```c
struct sockaddr {
    unsigned short    sa_family;    // address family, AF_xxx
    char              sa_data[14];  // 14 bytes of protocol address
};
```
