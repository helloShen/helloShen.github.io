---
layout: post
title: "[Note] How Tomcat Works - Chapter 1 - Test HttpSniffer"
date: 2017-07-03 19:10:03
author: "Wei SHEN"
categories: ["java","web","how tom cat works"]
tags: ["http","socket"]
description: >
---

### Macbook Localhost Server - Apache
Macbook Mac OS X 自带了 Apache 服务器。下面`httpd`可以查看版本信息。
```bash
httpd -v
Server version: Apache/2.4.25 (Unix)
Server built:   Feb  6 2017 20:02:10
```

如果`Apache`服务器没有启用，用以下命令启用，
```
sudo apachectl start
```

编辑`/etc/apache2/httpd.conf`文件，可以修改Apache配置。

### 运行`HttpSniffer`程序

运行`HttpSniffer`程序的shell脚本如下，
```
# Active Apache Server
sudo apachectl start
# Call HttpSniffer
java -cp ../.. com.brainysoftware.pyrmont.util.HttpSniffer
```

**！注意**：默认的`8080`端口，连接失败，
```
java.net.ConnectException: Connection refused
```
![8080-port-request-refused](/images/how-tomcat-works-chapter-one/8080-port-request-refused.png)

这表示，apache服务器没有监听`127.0.0.1:8080`端口。打开`/etc/apache2/httpd.conf`配置文件，发现apache的默认监听端口是`80`，
```
Listen 80
```

要么，向`80`端口发送消息，要么把apache的默认监听端口设为`8080`。改为向`80`端口发送消息后，收到了回复，
![80-port-request-accepted](/images/how-tomcat-works-chapter-one/80-port-request-accepted.png)

### 不用`HttpSniffer`，在Shell向Apache发送Request
`HttpSniffer`实际上只是一个图形化窗口。真正发挥作用的是 **`java.net.Socket`** 类。
