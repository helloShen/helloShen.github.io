---
layout: post
title: "How Tomcat Works - Chapter 11 & 12 - Standard Wrapper & Context"
date: 2017-11-12 17:02:32
author: "Wei SHEN"
categories: ["web","java","how tomcat works"]
tags: ["container"]
description: >
---

### 用一个监听器作为配置器
`ApplicationFilterConfig`实现了`Listener`接口。

### Wrapper里是一个`ApplicationFilterChain`，Context里是`Pipeline`

### Tomcat 5用`ContainerBackgroundProcessor`类用一个后台线程帮助载入器和Session管理器执行任务
它的`processChildren()`方法会调用自身容器的`backgroundProgress()`方法，然后递归调用每个子容器的`processChildren()`。这样可以确保每个子容器的`backgroundProgress()`方法都被调用。
