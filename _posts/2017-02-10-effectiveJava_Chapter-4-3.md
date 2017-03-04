---
layout: post
title: "[Effective Java] Note: - Chapter-4-3: Define the Type and Behavior with Interface"
date: 2017-02-10
author: "Wei SHEN"
categories: ["Java","Effective_Java"]
tags: ["Interface"]
description: >
  接口可以模拟多继承，优于抽象类的单继承。但抽象类可以提供部分实现，所以“接口 + 骨架实现”的模式就结合了两者的有点。可以定义一个只定义了行为的类模拟“函数指针”和“策略模式”。但不要定义一个没有定义行为，只有静态常量的常量接口。常量接口不是一个良好实践。
---

### 接口定义类型和行为
抽象类和接口最主要的区别在于：抽象类允许包含某些方法的实现，但接口却不允许。

### 接口可以多继承，比抽象类的单继承要好
接口可以多继承，抽象类只有单继承。

#### 接口多继承允许构造非层次结构的类型框架
考虑下面两个接口，一个表示“歌唱家”,另一个表示“作曲家”。接口很容易就定义了一个本身也是作曲家的歌唱家。
```java
public interface Singer {
    AudioClip sin(Song s);
}
public interface Songwriter {
    Song compose(boolean hit);
}
public interface SingerSongwriter extends Singer, Songwriter {
    Song compose(boolean hit);
    AudioClip sin(Song s);
}
```

#### 接口是定义`mixin(混合类型)`的理想选择
比如`Comparable`就是一个`mixin`类型。它表明某类型除了它本来的特性外，还提供了互相比较，进行排序的行为。只要添加了约定的`compareTo()`方法，现有类很容易就扩展了`Comparable`接口的功能。而如果是需要更新一个抽象类，就必须仔细考虑把它放在类型层次的结构的哪里。

### 接口不能有具体实现，使用“骨架实现”
解决的办法是：
> **导出的每个重要接口都提供一个抽象的骨架实现（skeletal implementation)**

Java的`Collection`框架是“骨架实现”的好例子。每个重要容器接口，背后都对应了一个“骨架实现”。`Collection`有`AbstractCollection`，`Set`有`AbstractSet`，`Map`有`AbstractMap`，`List`有`AbstractList`。下面以`Collection`为例，“骨架实现”是个三明治的结构，`AbstractCollection`实现了`Collection`接口。
```java
public abstract class AbstractCollection<E> implements Collection<E> {
    // code
}
```
当我想要实现一个符合`Collection`接口的容器，继承`AbstractCollection`可以让工作变得很简单。
```java
public final class SimpleCollection<E> extends AbstractCollection<E> {
    // code
}
```
对内，可以用`SimpleCollection`实例化对象，对外可以把`SimpleCollection`引用赋予一个`Collection`型。外部代码只需要知道这个容器提供标准的`Collection`接口定义的服务。

#### “骨架实现”的精妙在于要找对“基本方法”
牵牛要牵牛鼻子。实现骨架实现最重要的是要找对“基本方法”。所有其他方法都要依赖于它。然后把这个基本方法设为抽象方法。其他方法都可以根据假定的这个抽象方法返回的对象引用来工作。

最好的例子就是`Map#entrySet()`方法是`AbstractMap`中的那个"基本方法"。其他所有的方法都在`entrySet()`方法返回的`Set<Map.Entry<K,V>>`“集合视图”上工作。所以继承了`AbstractMap`之后，只要实现了`entrySet()`方法之后，大部分`Map`接口约定的方法就都能正常工作。

### 不要用常量接口
把几个静态常量集中放在一个接口中，不是一个好的做法。需要静态常量，就把他们放到具体类里。或者放到一个不能实例化的工具类里。就像`String.CASE_INSENSITIVE_ORDER`那样。

### 用无状态类模拟函数指针
`Comparator`是 **策略模式** 的一个好例子。`compare()`定义了比较两个元素的具体算法。
```java
public interface Comparator<T> {
    public int compare(T first, T second);
}
```
`Collections.sort()`方法可以给传入的参数`list`元素排序，比较两个元素的大小就调用`Comparator`参数的`compare()`方法。
```java
public void sort(List<T> list, Comparator<? super T> c) {}
```
因为`Comparator`具体类只有一个`compare()`行为，没有状态，所以也叫 **“无状态类”**。传入一个无状态类等待调用，模拟了 **“函数指针”** 的功能。
