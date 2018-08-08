---
layout: post
title: "IP Routing Algorithm"
date: 2017-12-29 18:30:49
author: "Wei SHEN"
categories: ["web"]
tags: ["ip","routing","cidr","nat","localhost"]
description: >
---

### 摘要
主要复习了关于IP路由的4个事儿，参考Tanenbaum的《计算机网络 - 第4版》。都用大白话讲清楚，省的以后重复查阅资料：
* 老的分类IP地址是怎么路由的？
* 然后新的无类别路由CIDR是怎么回事？
* NAT网络地址转换是什么？
* 为什么localhost是`127.0.0.1`?

### 老式的基于IP分类的路由算法
IP地址被分为5大类（如下图所示），
![ipclasses](/images/ip-routing-algorithm/ipclasses.png)

* A类地址可以有128个网络，每个网络1600万台主机。
* B类地址可以有16384个网络，每个网络65536台主机。
* C类地址可以有200万个网络，每个网络256台主机。

老式路由算法就是基于这种分类。比如有一个IP地址，
```
点分十进制表示法:
        194.24.17.4

二进制表示法:
        11000010 00011000 00010001 00000100
```
路由器拿到这个地址，先右移28位，产生一个前4位的类别号`1100`，说明是一个C类地址。
```
11000010 00011000 00010001 00000100
|  |
前4位 = 1100

说明是一个C类地址
```
知道是C类地址以后，C类地址前24位是网络号，后8位是主机号。所以就拿一个24位的 **掩码**，过滤出网络号`194.24.17.0`，
```
|          网络号         |  主机号 |
11000010 00011000 00010001 00000100
11111111111111111111111111 00000000   <- 掩码
-----------------------------------   &操作
11000010 00011000 00010001 00000000   <- 过滤掉主机号，留下网络号

网络号用点分十进制表示：
194.24.17.0
```
然后路由器内部维护着一个[键-值]表，通过`194.24.17.0`这个网络号，可以查找到对应的输出线路。对于A类和B类的网络号，因为数量少，可以用直接索引的方式。而C类网络数量太多，需要用散列表储存这些表项。


### CIDR(Classless Inter-Domain Routing,无类别域间路由)
但上面A,B,C,D,E网络分类法的问题是：
> 对一般规模的公司或机构来说，一个B类网络65536台主机台太多，一个C类网络256台主机又太少。大家都不想申请C类，都申请B类，但浪费严重。

CIDR的应对思路也很简单：
> 放弃IP分类。把IP地址小块小块地授权。要用多少给多少。

还是刚才`194.24.17.4`的例子。原来它是`1100`开头，是C类。网络号是`194.24.17.0`，网络内可以有256台主机，也就是`194.24.17.0 ~ 194.24.17.255`。现在不是这样了。

现在ICANN(Internet Corporation for Assigned Names and Numbers)分配IP地址的全危机构，决定给剑桥大学分配2048个地址，给牛津大学4096个地址，给爱丁堡大学1024个地址，然后他们的地址空间如下所示，
![university-ip](/images/ip-routing-algorithm/university-ip.jpg)

注意！如上图所示，现在全世界的路由器中的路由表中
都需要添加3条新纪录。每条纪录由一个 **基地址** 和一个 **子网掩码** 构成。
```
剑桥大学(2048个地址):
    First Address:  11000010 00011000 00000000 00000000
    Mask:           11111111 11111111 11111000 00000000
    Template:       11000010 00011000 00000XXX XXXXXXXX

爱丁堡大学(1024个地址):
    First Address:  11000010 00011000 00001000 00000000
    Mask:           11111111 11111111 11111100 00000000
    Template:       11000010 00011000 000010XX XXXXXXXX

牛津大学(4096个地址):
    First Address:  11000010 00011000 00010000 00000000
    Mask:           11111111 11111111 11110000 00000000
    Template:       11000010 00011000 0001XXXX XXXXXXXX
```

以剑桥大学为例，基地址和子网掩码简写成`194.24.0.0/21`，大白话就是说：
> 所有前21位是：“11000010 00011000 00000XXX XXXXXXXX” 这个样子的IP地址都分配给剑桥大学。

所以，当路由器拿到一个IP地址，比如`194.24.17.4`，要和表中所有的掩码逐一做`&`与操作，然后看切下来的头部是否和基地址一致。
```
11000010 00011000 00010001 00000100  // 194.24.17.4
11111111 11111111 11111000 00000000  // 剑桥大学掩码
-----------------------------------
11000010 00011000 00010000 00000000  // &操作结果
11000010 00011000 00000000 0000000   // 剑桥大学基地址
-----------------------------------
不一致
```
然后和爱丁堡大学的掩码做`&`与操作，得到的前22位也和基地址不一致。最后和牛津大学的掩码做`&`操作，得到的地址与基地址一致，所以确定将这个分包发给牛津大学所属的出口。

### NAT(Network Address Translation,网络地址转换)
尽管有CIDR的IP地址分配法，IP地址还是非常紧缺。尤其是对互联网服务供应商来说。就算它有一个完整的B类地址空间（/16），可以有65534个主机号。但当它的客户超过这个数字，怎么办？

一个临时解决办法是给客户动态分配IP地址。每次用户登录，就获得一个随机IP地址，用户下线的时候再收回。理论上这样100个IP地址，同时服务500个客户不成问题（他们不会同时在线）。

但这个方案对希望保持永久在线的公司用户来说不可行。他们希望获得稳定的永久IP。这时候就要用到NAT网络地址转换技术了。

NAT的基本思想很简单：
> 为每个公司分配一个IP（或者最多少量IP）。但是在公司内部，用一些世界通用的保留地址，为每一台计算机分配仅仅是公司内部唯一的IP地址。重点的约定是，这些保留地址不允许出现在Internet上。

Internet Assigned Numbers Authority (IANA) 规定全世界范围内不允许出现在Internet上的保留地址有3段：
* 10.0.0.0 ~ 10.255.255.255       (1600万台主机地址)
* 172.16.0.0 ~ 172.31.255.255     (100万台主机地址)
* 192.168.0.0 ~ 192.168.255.255   (65536台主机地址)

大部分公司用不掉1600万个地址，通常也会选第一段。

这里的关键就需要有一个NAT盒子（NAT Box）做一个 **“地址转换”**。内部机器向外发送分包的时候，将公司内部的`10.x.y.z`这样的地址，转换成公司实际占用的那个IP地址（比如`198.60.42.12`）。
![nat-box](/images/ip-routing-algorithm/nat-box.jpg)

粗略地看NAT的原理就是这么简单。但实际技术上有一个难点在于：
> 一个分组从公司外部发向公司内部怎么定位公司内部的机器？

因为本质上公司只有一个IP地址，比如`198.60.42.12`，怎么定位到`10.x.y.z`这台机器？这里就要用到 **端口（Port）** 了。

端口本来是用来标记分包是传递给哪个进程的。这里可以被拿来借用。比如`10.11.12.13:5000`这台公司内部机向外发送包，中间NAT地址转换服务器就为他重新分配一个`0~65536`之间的端口比如`12345`，然后NAT服务器需要用一个表记录这个映射：
```
10.11.12.13:5000   对应   12345
```
然后NAT服务器把报头改成`198.60.42.12:12345`就发出去了。然后Internet外部服务器也是照着`198.60.42.12:12345`这个地址发回复消息。NAT服务器拿到这个地址，一查表格，根据`12345`端口找到`10.11.12.13:5000`这组内部IP和端口组合。

### localhost(127.0.0.1)是怎么回事？
Linux系统下`/etc/hosts`文件，专门记录IP地址的别名，用VIM打开，其中的默认设置就是，
```
127.0.0.1 localhost
```
为什么是`127.0.0.1`？因为`127.x.x.x`理论上属于A类地址。但它被设定为保留段，专门用作 **回送地址（Loopback Address）**。无论什么程序，一旦使用回送地址发送数据，协议软件立即返回，不进行任何网络传输。

### 参考资料
* 《计算机网络 - 第4版》，作者：Andrew S.Tanenbaum，P370-P380