---
layout: post
title: "Skeletal Implementation in Java Collection Framework"
date: 2017-02-14
author: "Wei SHEN"
categories: ["Java","Design_Pattern"]
tags: ["Template_Pattern","Container"]
description: >
---

### 骨架实现 （Skeletal Implementation）
骨架实现的架构是一个三明治结构：**接口 - 骨架实现类 - 实际类**。

它结合了接口和抽象类的优点。接口的优点是可以 **多继承实现mixin**。但接口不能实现部分功能。抽象类可以。

骨架实现先找出接口所有操作中的 **基本操作**。 基本操作就是指，如果有了基本操作的结果，其他方法都可以依赖这个结果完成操作。

典型的骨架实现是Collections Framework。 每个容器接口都有一个对应的骨架实现。以`Map`和`AbstractMap`为例。

`AbstractMap`中的 **基本操作** 是`entrySet()`方法，返回的 **Set视图**。然后`Map`接口定义的大部分方法，都可以通过调用Set视图的`Iterator`来实现。

下面代码是`Map`通过继承`AbstractMap`骨架实现，最快速实现`Map`接口的代码，只要实现了`entrySet()`抽象方法，就基本实现了一个 **只读不写** 的Map。

```java
public class SimplestMap<K,V> extends AbstractMap<K,V> {
    Map<K,V> map = new HashMap<>();
    @Override
    public Set<Map.Entry<K,V>> entrySet() { // 自己写entrySet的话，可以利用AbstractMap.SimpleEntry
        return map.entrySet();
    }
}
```
