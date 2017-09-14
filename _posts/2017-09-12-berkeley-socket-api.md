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
要进行网络I/O，首先要创建一个 **套接字（Socket）**。 套接字向程序员提供`TCP/IP`服务。

这篇文章主要记录一下UNIX环境下，伯克利套接字这些函数需要注意的一些点。

### 总览
标准伯克利套接字`Berkeley Socket API`提供的函数如下，
![socket-api](/images/berkeley-socket-api/socket-api.png)

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


### `socket()`函数
`socket()`函数用来创建一个套接字，告诉操作系统期望的 **协议类型**。它的函数签名如下，若成功返回的是一个非负 **套接字描述符**，若出错，返回-1。系统内核会为这个套接字分配资源，比如I/O的缓存区，已连接和等待连接的队列。但这些细节内核不向用户暴露。
```c
#include <sys/socket.h>

// 若成功，返回非负描述符。若出错，则返回-1.
int socket(int family, int type, int protocol);
```
其中，
* `family`参数：指明协议族，比如IPv4(`AF_INET`),IPv6(`AF_INET6`),等。
* `type`参数：指明套接字类型。TCP是一个字节流协议，只支持`SOCK_STREAM`。
* `protocol`参数：标明使用的协议类型。（和family指的IP协议不同，protocol指的是TCP,UDP或者SCTP协议中的一个）

![socket-family](/images/berkeley-socket-api/socket-family.png)
![socket-type-protocol](/images/berkeley-socket-api/socket-type-protocol.png)
![socket-family-type](/images/berkeley-socket-api/socket-family-type.png)

比如，一个基于`TCP/IPv4`协议的套接字，初始化参数设置如下，
```c
int sockfd = socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
```
首先，因为是IPv4，所以`family`参数选`AF_INET`，然后TCP是字节流协议，所以`type`参数只支持`SOCK_STREAM`，最后`protocol`因为是TCP，就选`IPPROTO_TCP`。

### `connect()`函数
TCP客户端用`connect()`函数来建立于TCP服务器的连接。
```c
#include <sys/socket.h>

/** 若成功返回0，若出错则返回-1. */
int connect(int sockfd, const struct sockaddr *servaddr, socklen_t addrlen);
```
三个参数分别是，
* `sockfd`是由`socket()`函数返回的套接字描述符。
* `servaddr`是指向 **套接字地址结构** 的指针。
* `addrlen`是套接字地址结构的大小。

这里的关键就是一个 **套接字地址结构**。用大白话说： **就是客户端要告诉系统，它请求连接的服务器的套接字地址。**

其中有两个字段，
* 第一个`sa_family`和`accept()`函数里的`family`参数一样，指明地址所使用的协议族。如果是IPv4就是`AF_INET`，如果是IPv6就是`AF_INET6`.
* 第二个14 bytes的`sa_data`实际是一个 **IP地址** 加上 **端口**。14个字节足够了。

#### 第一个关键：`sockaddr`地址结构
前面说了`sockaddr`地址结构实际就是一个 **IP地址** 加 **端口** 的组合，用来定位一个远程服务器的地址。
```c
struct sockaddr {
    unsigned short    sa_family;    // address family, AF_xxx
    char              sa_data[14];  // 14 bytes of protocol address
};
```

> 注意！`sockaddr`类型的变量可以安全地转型为`sockaddr_in`和`sockaddr_in6`。

从`sockaddr_in`里就可以看出来，`sin_port`是占2个字节的`short`型。`sin_addr`是一个占4个字节的`unsigned long`。
```c
// IPv4 AF_INET sockets:

struct sockaddr_in {
    short            sin_family;   // e.g. AF_INET, AF_INET6
    unsigned short   sin_port;     // e.g. htons(3490)
    struct in_addr   sin_addr;     // see struct in_addr, below
    char             sin_zero[8];  // zero this if you want to
};

struct in_addr {
    unsigned long s_addr;          // load with inet_pton()
};
```
因为IPv6和IPv4是兼容的，虽然`sin6_addr`更长，但可以通过在IPv4的地址补零对齐。
```c
// IPv6 AF_INET6 sockets:

struct sockaddr_in6 {
    u_int16_t       sin6_family;   // address family, AF_INET6
    u_int16_t       sin6_port;     // port number, Network Byte Order
    u_int32_t       sin6_flowinfo; // IPv6 flow information
    struct in6_addr sin6_addr;     // IPv6 address
    u_int32_t       sin6_scope_id; // Scope ID
};

struct in6_addr {
    unsigned char   s6_addr[16];   // load with inet_pton()
};
```

#### 第二个关键：如果是TCP协议，`connect()`函数触发三次握手
三次握手的细节这里不展开。只需要知道系统内核为我们完成了三次握手的过程即可。
![tcp-three-way-handshake](/images/berkeley-socket-api/tcp-three-way-handshake.png)

提一下，
> 如果TCP客户没有收到SYN分节的响应，则返回`ETIMEOUT`错误。例如4.4BSD内核发送一个SYN，若无响应则等待6秒再发送一个。若仍无响应，过24秒再发送一个。总共等到75秒还没有响应，则返回错误。

按照TCP状态转换图，`connect()`函数导致当前套接字从`CLOSED`状态（该套接字从由`socket()`函数创建以来一直处在的状态）转移到`SYN_SENT`状态，若成功则再转移到`ESTABLISHED`状态。若失败，则该套接字不可再用，必须关闭。不可再次调用`connect()`函数。

### `bind()`函数
`bind()`函数为套接字绑定一个 **本地地址**。 通常对于TCP/IP的网际协议，协议地址是32位（4个字节）的IPv4地址或128位（16个字节）的IPv6地址，与16位（2个字节）的TCP或UDP端口号组成。
```c
#include <sys/socket.h>

/** 若成功返回0，若出错则返回-1. */
int bind(int sockfd, const struct sockaddr *servaddr, socklen_t addrlen);
````
调用`bind()`函数的时候，可以指定一个端口号，一个IP地址，也可以两个都指定，也可以两个都不指定。知名应用服务器在启动的时候绑定它们知名端口号，比如HTTP服务器绑定`80`或`8080`端口。否则如果指定端口号为0,内核就选择一个临时端口。

至于它的三个参数，参照`connect()`函数。

### `listen()`函数
`listen()`函数很关键，它决定套接字是公（客户端）是母（服务器）。 `listen()`函数只由TCP服务器调用。
```c
#include <sys/socket.h>

/** 若成功返回0，若出错则返回-1. */
int listen(int sockfd, int backlog);
```

> 当`socket()`函数创建一个套接字是，它被设置为一个 **主动套接字**，就是将调用`connect()`函数发起连接的客户端套接字。`listen()`函数把一个未连接的套接字转换成一个 **被动套接字**。

根据TCP状态转换图，调用`listen()`函数，导致套接字从`CLOSED`状态转换到`LISTEN`状态。

两个参数，
* `sockfd`是套接字的非负描述符。
* `backlog`规定了内核应该为该套接字排队的最大连接个数。

这里的 **“最大连接个数”** 是指：**未完成连接队列（incomplete connection queue）** 以及 **已完成连接队列（completed connection queue）** 的总和不超过`backlog`。

![listen-backlog](/images/berkeley-socket-api/listen-backlog.jpg)

### `accept()`函数
`accept()`函数由TCP服务器调用，用于从 **已完成连接队列** 头返回下一个已完成连接。如果已完成连接队列为空，那么进程被投入 **睡眠（阻塞）**。
```c
// 若成功，返回非负描述符。若出错则返回-1.
int accept(int sockfd, struct sockaddr *cliaddr, socklen_t *addrlen);
```
* 参数`sockfd`: 由`socket()`函数返回的 **监听套接字（listening socket）** 描述符。
* 参数`cliaddr`: 用来返回客户端的IP地址。
* 参数`addrlen`: 标明`cliaddr`套接字地址结构的长度。

关于`accept()`函数，关键点在于：**它返回的是一个由内核自动生成的全新的已连接套接字（connected socket）**。 要和它的第一个参数`sockfd` **监听套接字（listening socket）** 描述符区分开来。

对一个服务器来说，通常仅仅创建一个监听套接字，它在该服务器的生命周期内一直存在，但内核为每一个由服务器进程接受的客户连接创建一个已连接套接字（TCP三路握手已完成）。当服务器完成对该客户的服务时，响应的已连接套接字就被关闭。

### 附录：TCP状态转换图
![tcp-status](/images/berkeley-socket-api/tcp-status.jpg)
