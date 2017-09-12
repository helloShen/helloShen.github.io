---
layout: post
title: "URI & URL"
date: 2017-09-12 14:08:08
author: "Wei SHEN"
categories: ["web","how tomcat works"]
tags: ["uri","url","urn"]
description: >
---

![uri-and-url](/images/uri-url/uri-and-url.png)

* URI (Uniform Resource Identifier): 统一资源标识符
* URL (Uniform Resource Locator): 统一资源定位符
* URN (Uniform Resource Name): 统一资源名称

三者之间的关系是：
> URI可以进一步被分为：URL和URN。URL是URI的子集。

URL提供资源的访问机制，就好比 **“地址”**，URN就好比 **“名字”**。

网络环境下，资源的访问机制比较复杂，大部分的URI同时也是URL，因为TCP/IP以及应用层协议的信息占了大部分。

下图是URI的标准语法，
![syntax-uri](/images/uri-url/uri-syntax.png)

但URI也可以不是URL，比如一个电话号码`tel:+1-816-555-1212`，或者国际标准书号`ISBN: 9787807682035`。他们都可以准确标识一个资源，但他们并不包括访问机制。

下面的这些例子都是URI，其中的大部分同时又都是URL，
* ftp://ftp.is.co.za/rfc/rfc1808.txt (also a URL because of the protocol)
* http://www.ietf.org/rfc/rfc2396.txt (also a URL because of the protocol)
* ldap://[2001:db8::7]/c=GB?objectClass?one (also a URL because of the protocol)
* mailto:John.Doe@example.com (also a URL because of the protocol)
* news:comp.infosystems.www.servers.unix (also a URL because of the protocol)
* tel:+1-816-555-1212
* telnet://192.0.2.16:80/ (also a URL because of the protocol)
* urn:oasis:names:specification:docbook:dtd:xml:4.1.2

某个资源既是URI，又是URL，不知道该叫什么的情况下，叫URI是不会错的。
