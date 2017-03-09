---
layout: post
title: "The usage of Generics in Java Collection Framework"
date: 2017-02-02
author: "Wei SHEN"
categories: ["java"]
tags: ["generics","container"]
description: >
  Java的泛型可以为类提供编译器类型检查。同时有界和无界通配符，又可以根据PECP原则适当放宽类型的接收范围。这里以Map为例，对什么时候该用什么尺度的泛型约束，做一个案例分析。
---

### 分析HashMap的泛型设计
Java的泛型可以为类提供编译器类型检查。同时有界和无界通配符，又可以根据PECP原则适当放宽类型的接收范围。这里以Map为例，对什么时候该用什么尺度的泛型约束，做一个案例分析。

```java
/**
 * 只声明了“键-值”对的泛型参数，只需要保持一致性，并没有做什么类型约束。
 */
public class HashMap<K,V> implements Map<K,V> {
    /**
     * 构造器
     */
    HashMap() {}
    HashMap(int initialCapacity) {}
    HashMap(int initialCapacity, float loadFactor) {}
    //下面的参数m, 因为是数据的producer, 根据PECP原则，用上界通配符放宽条件，可以接受以K,V的子类型为参数的Map
    HashMap(Map<? extends K,? extends V> m) {}

    /**
     * 静态内部套嵌接口Entry。因为是静态成员，所以这里的K,V和外部类类型参数的K,V不是一回事。
     */
    Entry<K,V> {}
    // entrySet是一个工厂方法，这里的K,V和整体泛型类型参数保持一致
    Set<Map.Entry<K,V>>	entrySet() {}

    void clear() {}
    /** 这里的contains, equals, get以及后面的remove都直接接受Object，并没有用泛型(K key)或者(V value)是有原因的。
     * 因为这里判断是否是同一个元素的标准不要求必须是同一类型。而是要求”值相等”：（o == null？e == null：o.equals（e））
     * 就算类型不等，也可以值相等。例如， List.equals（）的规范说，如果两个 List 对象都是 List s并且具有相同的内容，即使它们是List的不同实现。
     */
    boolean	containsKey(Object key) {}
    boolean	containsValue(Object value) {}
    boolean	equals(Object o) {}
    V get(Object key) {}
    int	hashCode() {}
    boolean	isEmpty() {}
    Set<K> keySet() {}
    // 最简单的泛型类型一致性检查
    V put(K key, V value) {}
    // 因为m是数据提供者Producer，根据PECP原则，使用上界通配符。
    void putAll(Map<? extends K,? extends V> m) {}
    V remove(Object key) {}
    int	size() {}
    Collection<V> values() {}
}
```
