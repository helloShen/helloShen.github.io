---
layout: post
title: "Internals of Java Class Loading"
date: 2016-08-07 08:07:33
author: "Wei SHEN"
categories: ["Java"]
tags: ["Reflection","OOP"]
description: >
---

### 类的加载过程
解释完鸡蛋问题，最后复习一下JVM里一个类的加载过程（正常运行的时候，Object和Class类都早已经正常加载完毕，系统已经可以自动运行下去了）。
一个类第一次被用到的时候，才被动态加载到JVM。一个类完整的过程分为如下三步：
1. 加载：先找并**加载**.class文件里以字节码形式存在的**Class类的对象**。(类的元信息)
2. 链接：为变量分配内存空间。
	2.1 准备：在方法区把类的静态变量的初始值设成零值（static final可以在这个时候赋值）。给类或接口，字段，类方法，接口方法四种元数据分配内存（也是在方法区的常量池）。具体方法是分配一个没有实际内容的符号引用（Symbolic References）。
	2.2 解析：然后对类或接口，字段，类方法，接口方法四种符号开始解析，添加引用（也都是在方法区的常量池里）。到这一步都完全没有对象这回事。
3. 初始化：这一步才真正开始赋值。
	3.1 <clinit>()方法。静态语句块static{}和成员变量的"默认"赋值是一起执行的。具体谁先谁后按照在文件中出现的先后顺序。
	3.2 <init>()方法（类构造器）。最后调用类的构造器来构造对象实例。


### java.lang.reflect.ClassLoader
关于java反射类库java.lang.reflect里的ClassLoader类，看到两篇比较深入浅出的科普文，转过来留存。
原文链接如下，感谢作者。
* [**《深入浅出ClassLoader》**](https://yq.aliyun.com/articles/2890)
* [**《深入探讨Java类加载器》**](http://www.ibm.com/developerworks/cn/java/j-lo-classloader/index.html?ca=drs-cn-0301)
