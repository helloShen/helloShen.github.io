---
layout: post
title: "The Class/Object system in Java”？"
date: 2016-08-08 15:57:46
author: "Wei SHEN"
categories: ["Java"]
tags: ["Reflection","OOP"]
description: >
---

### “对象”/“类型”系统
作为面向对象的编程范式OOP(Object-Oriented Programming)的典型代表，而且是是强类型语言，(strongly typed language)，Java有一对幽灵：类型和对象（class/object），：

> 类（class）代表一种数据的类型。
> 对象（object）是某个类的一个实例。
> Object类是所有类型的基类（superclass）。
> 每个类型都对应一个Class对象。

以我的智商，我已经被搞晕了。其实这东西要这么理解：

什么是类(class)？就指**“某一类东西”**，比如说有种东西叫**“快递”**：
```java
class Delivery {}
```
![delivery1](/images/oo/delivery1.jpg)

但世界上有无数个“快递”，他们每个的大小尺寸形状都不一样。每个包裹都是“快递”类的一个**“实例对象”**。
```java
Delivery d1 new Delivery();
Delivery d2 new Delivery();
Delivery d3 new Delivery();
...
```
![delivery2](/images/oo/delivery2.jpg)

所有类型都有一个共同的基类：Object类。这个Object就好比说是**“东西”**这个概念。快递是一种东西，西瓜是一种东西，所有东西都是一种东西。

![delivery4](/images/oo/delivery4.png)

而且每个快递上都有一张“快递单”，用来描述这个快递的信息。这张快递单，在Java里是**"Class类"**的一个实例对象。Class类就是用来描述每个类型（class）自身的**元信息**，就是描述一个东西本身的说明书。
```java
Delivery d new Delivery();
//获得类型元信息
Class c = new d=getClass();
c=Delivery.class;
```
![delivery3](/images/oo/delivery3.jpg)

但Class类本身也是一种类型。也就是用来说明一个东西的说明书本身也是一种“东西”。

#### Object/Class，“鸡生蛋，蛋生鸡”？
用来描述一个类型元信息的对象都属于java.lang.Class类。而它又是所有类型的超类java.lang.Object类的派生类。Java类型对象系统的这个设计，导致了在系统初始加载时候“鸡生蛋，蛋生鸡”问题。

背后的逻辑死局在于：java.lang.Object类作为一切类的祖先，必须是第一个加载的类。而java.lang.Object它的元数据是它自身的一部分也必须在这个时候被初始化，这个元数据应该是一个java.lang.Class的实例。"正常人类的逻辑"会认为，java.lang.Object这个类都还没有初始化完成，就不能有java.lang.Object的实例，所以java.lang.Class的实例就无法存在。而且java.lang.Class类本身也有它的元数据，又是java.lang.Class类它本身的实例，又是一个死循环。再简单点说这个逻辑悖论就在于：java.lang.Object有一个它的派生类的实例作为自身的一部分。打个很污的比方说就好比：**为了生出你的女儿，你得先娶你的女儿。你的女儿和你的孩子就是你娶的女儿。**

R大RednaxelaFX是这样描述这个问题的：
> **在一个已经启动完毕、可以使用的Java对象系统里，必须要有一个java.lang.Class实例对应java.lang.Object这个类；而java.lang.Class是java.lang.Object的派生类，按“一般思维”前者应该要在后者完成初始化之后才可以初始化**

但这个问题实际是不存在的。因为Java的Object和Class是C++实现的。也就是在用C++初始化java.lang.Ojbect类的时候，我同时也在用C++初始化它的派生类java.lang.Class和它的实例。因为原则上说，类和它的实例的本质都是一个数据结构。只要是数据，我就可以直接用C++来构造。根本不存在任何逻辑控制流。java.lang.Object和java.lang.Class可以同时初始化，然后同时完成，不存在一个先后关系。

#### 鸡蛋问题的答案：引导类加载器（Bootstrap Class Loader）
先推荐一篇启蒙读物 - [**《深入探讨 Java 类加载器》**](https://www.ibm.com/developerworks/cn/java/j-lo-classloader/)

下面这部分引自R神RednaxelaFX在知乎在 - [**《先有Class还是先有Object？》**](https://www.zhihu.com/question/30301819/answer/47539163) - 这个问题下的回答。
> 像Object和Class这种对象系统核心类，是交给引导类加载器（Bootstrap Class Loader）来完成的。目的是搭建起能够自动沿着主线程加载类（字节码），初始化类，垃圾回收这一系列操作流程的对象系统。其间具体的过程简单用大白话描述就是：
> 1. JVM可以为对象系统中最重要的一些核心类型先分配好内存空间，让它们进入[已分配空间]但[尚未完全初始化]状态。此时这些对象虽然已经分配了空间，但因为状态还不完整所以尚不可使用。
> 2. 然后，通过这些分配好的空间把这些核心类型之间的引用关系串好。到此为止所有动作都由JVM完成，尚未执行任何Java字节码。
> 3. 然后这些核心类型就进入了[完全初始化]状态，对象系统就可以开始自我运行下去，也就是可以开始执行Java字节码来进一步完成Java系统的初始化了。


> 在HotSpot VM里，有一个叫做“Universe”的C++类用于记录对象系统的总体状态。它有这么两个有趣的字段记录当前是处于bootstrapping阶段还是已经完全初始化好：
> 然后Universe::genesis()函数会在bootstrap阶段中创建核心类型的对象模型：
> （“genesis”是创世纪的意思，多么形象）
> 其中会调用SystemDictionary::initialize()来初始化对象系统的核心类型：
> 其中会进一步跑到SystemDictionary::initialize_preloaded_classes()来创建java.lang.Object、java.lang.Class等核心类型：
> 这个函数在加载了java.lang.Object、java.lang.Class等核心类型后会调用Universe::fixup_mirrors()来完成前面说的“把引用关系串起来”的动作：

Hotspot虚拟机的整个Bootstrap引导过程，是用C++写的。根本不在Java语法的框架里。并且Bootstrap Class Loader本身并不继承java.lang.ClassLoader。

下面是轮子哥在这个问题下的回答，值得细细品味一下：
> JVM里面只要先把Class和Object搞出来就好了，并没有任何证据表明所有的class都要用java来定义，也没有任何证据表明所有的实例都必须用java的代码使用new这个关键字来创建，再说也没有任何证据表明一个类的metadata跟代表这个类的类型的实例必须被同时创建。
> 你完全可以先搞出Object的metadata，然后搞出Class的metadata，然后创造出Class的两个实例（分别是Object和Class），然后把Object的实例的metadata指向Object的matadata，把Class的实例的metadata指向Class的metadata。
