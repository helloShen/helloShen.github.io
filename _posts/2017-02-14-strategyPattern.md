---
layout: post
title: "Strategy Pattern"
date: 2017-02-14
author: "Wei SHEN"
categories: ["java","design pattern"]
tags: ["strategy pattern"]
description: >
---

### 策略模式(Strategy Pattern)
策略模式用大白话讲就是：**我要做一件事，有很多种不同的做法。然后我就把做这件事的算法，从整个程序的过程中抽象出去。实现解耦。**

所以一个典型的策略模式的本质就是：**无状态对象模拟函数指针。策略接口负责提供某种算法服务**。

Java中最典型的策略模式，是`Comparator`。`Comparator`接口API很简单，只有一个`compare(T t1, T t2)`方法。如果`t1`大于`t2`，函数返回大于零的整数，反之，返回负整数。相等，返回零。
```java
/**
 * return int > 0 if t1 > t2
 * return int < 0 if t1 < t2
 * return int = 0 if t1.equals(t2)
 */
interface Comparator<T> {
    int compare(T t1, T t2);
}
```
Comparator对其他事情都不操心，只负责比较两个传入参数的大小，然后返回一个结果。

如果我要比较两个`Integer`的大小，只要传递一个`Comparator`接口的引用`c`，然后我的函数会调用`c`对象的`compare()`方法，帮我比较大小就这么简单。
```java
int compareTwoInteger(Integer i1, Integer i2, Comparator<Integer> c) {
    return c.compare(i1,i2);
}
```
