---
layout: post
title: "Why doesn't Java allow for the creation of generic arrays?"
date: 2016-08-21 23:21:51
author: "Wei SHEN"
categories: ["java"]
tags: ["generics","array"]
description: >
---

根本的原因是：数组在创建的时候必须知道内部元素的类型，而且一直都会记得这个类型信息，每次往数组里添加元素，都会做类型检查。

但因为Java泛型是用擦除（Erasure）实现的，运行时类型参数会被擦掉。比如：
```java
List<String> l = new ArrayList<String>();
l.add("hello");
String str=l.get(0);
```

运行时，类型参数<String>都被擦掉，只有在最后读取内部元素的时候，才插入一个类型转换。看起来就像下面这样。（下面这段是伪代码，只是为了解释效果。）
```java
List l = new ArrayList();
l.add("hello");
String str=(String)List.get(0);
```

所以，如果像下面这样初始化泛型数组的话，
```java
List<String>[] l = new ArrayList<String>[10];    //Error
```

运行时编译器只能看到ArrayList，而看不到泛型的String类型参数。数组由于无法确定所持有元素的类型，所以不允许初始化。

Java Language Specification明确规定：数组内的元素必须是“物化”的。
> **It is a compile-time error if the component type of the array being initialized is not reifiable.**

对“物化”的第一条定义就是不能是泛型：
> A type is reifiable if and only if one of the following holds:
> It refers to a non-generic class or interface type declaration.
> ... ...

因为Array的具体实现是在虚拟机层面，嵌地非常深，也查不到源码。只好用javap反编译看看具体初始化数组的字节码。我们反编译下面一段代码：初始化一个String数组和一个int数组。
```java
String[] s=new String[]{"hello"};
int[] i=new int[]{1,2,3};
```

反编译的片段如下：
```java
    Code:
       0: iconst_1
       1: anewarray     #2                  // class java/lang/String
       4: dup
       5: iconst_0
       6: ldc           #3                  // String hello
       8: aastore
       9: astore_1
      10: iconst_3
      11: newarray       int
      13: dup
      14: iconst_0
      15: iconst_1
      ... ...
```

其中：
* "1: anewarray #2"：创建String数组
* "11: newarray int"：创建int数组

anewarray和newarray都是虚拟机内部用来创建数组的命令。最多只能有2的8次方256个操作码，光创建数组就占了不止一个，可见数组的地位有多特殊。
![title](/images/tij4-15/title.png)
![newArray](/images/tij4-15/newarray.png)
![aNewArray](/images/tij4-15/anewarray.png)

其中newarray用atype来标记数组类型。anewarray用index来标记。从描述里可以看到，数组除了元素类型，还有一个必须确定的是长度，因为数组是一段连续内存。

查一下 Java Virtual Machine 对anewarray命令的描述，
> **anewarray <type>**
> <type> indicates what types of object references are to be stored in the array. It is either the name of a class or interface, e.g. java/lang/String, or, to create the first dimension of a multidimensional array, <type> can be an array type descriptor, e.g.[Ljava/lang/String;

比如anewarray字节码命令的格式就是anewarray后面跟一个具体的元素类型。所以不能确定<type>的确切类型，就无法创建数组。

最初在还没有泛型的时候，数组就一直是以**“The Special One”**的形象出现。那个时候所有容器都还只是持有Object。在其他容器都不太关心类型安全的年代，数组就特立独行地坚持类型检查，它的使命就是提供一个“类型安全”和效率更高的容器。所以类型检查和长度限制都被写到了字节码的规范里。至于到了支持泛型的年代，泛型“泛化”的本质就和数组“精确高效”的主旨根本上是相违背的。而且要改的话就要在字节码里动刀了。还是历史包袱的问题。
