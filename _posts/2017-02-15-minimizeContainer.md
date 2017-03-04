---
layout: post
title: "The Easiest Way to Implement Container"
date: 2017-02-21
author: "Wei SHEN"
categories: ["Java","Data_Structure"]
tags: ["Container","Template_Pattern","Encapsulation"]
description: >
  List要写size()和get()。Set要写iterator()和size()。Map要写entrySet()。
---

### List
继承`AbstractList`类，实现 **`size()`** , **`get()`** 方法，就能获得一个“只读”的List。最主要的是一个迭代器`Iterator`，这在`AbstractList`里已经包括了。是一个叫`Itr`的内部类。它的`hasNext()`是基于`size()`写的，`next()`是基于`get()`方法写的，`remove()`调用`AbstractList`的`remove(int)`方法。所以`AbstractList`的primitive方法是`size()` , `get()`。

```java
public class SimplestList<E> extends AbstractList<E> {
    List<E> list = new ArrayList<>();
    @Override
    public E get(int index) {
        return list.get(index);
    }
    @Override
    public int size() {
        return list.size();
    }
}
```

### Set
继承`AbstractSet`类，实现 **`size()`** , **`iterator()`** 方法。因为`Set`接口不接受随机访问，所以没有`get()`方法。但是`AbstractSet`又没有给一个`Iterator`的默认实现，所以还要写一下`iterator()`方法。

下面代码没有重新写一个`Iterator`，而是简单代理了一下成员域中的set。一般情况下，还是需要写一个`Iterator`的，

```java
public class SimplestSet<E> extends AbstractSet<E> {
    Set<E> set = new HashSet<>();
    @Override
    public int size() {
        return set.size();
    }
    public Iterator<E> iterator() { // 这里偷懒没有写Iterator，只是代理了一下
        return set.iterator();
    }
}
```

### Map
继承`AbstractMap`类，实现 **`entrySet()`** 方法。因为`size()`用的是`entrySet()`返回的`Set`的`size()`方法。而`put()`方法是`optional`的。

不想重写`Map.Entry`类的话，可以使用`AbstractMap.SimpleEntry`。

```java
public class SimplestMap<K,V> extends AbstractMap<K,V> {
    Map<K,V> map = new HashMap<>();
    @Override
    public Set<Map.Entry<K,V>> entrySet() { // 自己写entrySet的话，可以利用AbstractMap.SimpleEntry
        return map.entrySet();
    }
}
```
