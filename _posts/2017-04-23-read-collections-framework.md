---
layout: post
title: "Read Java Source Code - Collections Framework"
date: 2017-04-23 02:06:32
author: "Wei SHEN"
categories: ["java","source code"]
tags: ["collections framework"]
description: >
---

### JDK源代码在哪里？
```bash
/Library/Java/JavaVirtualMachines/jdk1.8.0_73.jdk/Contents/Home/src.zip
```

### 用什么工具读？
> **IntelliJ IDEA**

平时写代码可以不用IDE，但读源码要查看调用链，用IDE事半功倍。

在IntelliJ里创建一个新项目叫`read-jdk-source-code`。
运行下面命令，将源码手动导入IntelliJ。
```
cd /Library/Java/JavaVirtualMachines/jdk1.8.0_73.jdk/Contents/Home
cp src.zip ~/IdeaProjects/read-jdk-source-code/src
cd ~/IdeaProjects/read-jdk-source-code/src
unzip src.zip
rm src.zip
```

项目文件目录树如下，开始读。
```
.
├── read-jdk-source-code.iml
└── src
    ├── com
    │   └── sun
    ├── java
    │   └── util    # 我要看的Collections Framework在这里
    ├── javax
    ├── launcher
    └── org
```

### Collections Framework官网
信官网，得永生！ --> <http://docs.oracle.com/javase/7/docs/technotes/guides/collections/overview.html>

### 鸟览
先有个总览，看不明白没关系，下面一点点捋。
![collection-structure](/images/read-jdk-source-code/collection-structure.jpg)
![map-structure](/images/read-jdk-source-code/map-structure.jpg)

### 有哪些接口？
主要有两个接口族：
1. java.util.Collection
2. java.util.Map

**`Collection`** 族接口的主要拓扑如下（不保证全，但最常用的都在这里了），
```bash
java.lang.Iterable<T>
    └──java.util.Collection<E>
        ├──java.util.List<E>
        ├──java.util.Queue<E>
        │    └──java.util.Deque<E>
        │    │  └──java.util.concurrent.BlockingDeque
        │    ├──java.util.concurrent.BlockingQueue
        │    └──java.util.concurrent.TransferQueue
        └──java.util.Set<E>
            ├──java.util.SortedSet<E>
            └──java.util.NavigableSet<E>
```
**`Map`** 族的拓扑如下，
```bash
java.util.Map
    ├──java.util.SortedMap
    │   └──java.util.NavigableMap
    ├──java.util.concurrent.ConcurrentMap
    └──java.util.concurrent.ConcurrentNavigableMap
```

> 胖胖，上面这两个拓扑是要记在心里的！

如果要为了好记，按功能分就是 **Java容器四大天王**，
* Set: 元素不重复。
* List: 最普通意义上的列表。
* Deque: `Stack`和`Queue`的结合体，即可`FIFO`，又可`LIFO`。
* Map: `key-value`映射。

### `Collection`和`Map`两大接口族的主要实现类有哪些？
实现太多，记不住怎么办？记住下面这张图就行！这张表里的几个API个个重要地不得了，每天都用。
![set-list-deque-map](/images/read-jdk-source-code/set-list-deque-map.png)

> 胖胖，上面这个表是要背下来的！

并发最常用的几个如下，
* LinkedBlockingQueue
* ArrayBlockingQueue
* PriorityBlockingQueue
* DelayQueue
* SynchronousQueue
* LinkedBlockingDeque
* LinkedTransferQueue
* CopyOnWriteArrayList
* CopyOnWriteArraySet
* ConcurrentSkipListSet
* ConcurrentHashMap
* ConcurrentSkipListMap

### 读代码草稿 （待续 ... ...）

### 好的代码读起来像说话一样通顺
用非常有限的设计模式，达到代码读起来像平时说话的句子一样。而且耦合低，易维护，易扩展。下面这段代码，这几点都做到了。
```java
/**
 * 写代码像说话一样。能像句子一样念出来。
 */
default boolean removeIf(Predicate<? super E> filter) {
    Objects.requireNonNull(filter);
    boolean removed = false;
    final Iterator<E> each = iterator();
    while (each.hasNext()) {
        if (filter.test(each.next())) { // 策略模式
            each.remove();
            removed = true;
        }
    }
    return removed;
}
```

### 结构化编程是对的
不要耍小聪明，老是用`break`,`continue`。老老实实`if-else`简单明了。除非套嵌太深，用`break`,`continue`来减少套嵌。
```java
public boolean contains(Object o) {
    Iterator<E> it = iterator();
    if (o==null) {
        while (it.hasNext())
            if (it.next()==null)
                return true;
    } else {
        while (it.hasNext()) // 稍微重复一点代码也没关系，关键可读性最重要。
            if (o.equals(it.next()))
                return true;
    }
    return false;
}
```

### 看看这防御！
`Iterator`是线程安全的，竟态条件下，遇到冲突会抛出`ConcurrentModificationException`。但`AbstractCollection#toArray()`还是预防了长度被改变的情况。
```java
public Object[] toArray() {
    // Estimate size of array; be prepared to see more or fewer elements
    Object[] r = new Object[size()];
    Iterator<E> it = iterator();
    for (int i = 0; i < r.length; i++) {
        if (! it.hasNext()) // fewer elements than expected    //看看这防御！
            return Arrays.copyOf(r, i);
        r[i] = it.next();
    }
    return it.hasNext() ? finishToArray(r, it) : r;
}
```

### 看看人家是怎么用反射创建数组的
用`Class`对象的`getComponentType()`方法，获取数组的类型。然后用`java.lang.reflect.Array
.newInstance()`动态创建新数组实例。
```java
public <T> T[] toArray(T[] a) {
    // Estimate size of array; be prepared to see more or fewer elements
    int size = size();
    T[] r = a.length >= size ? a :  
              (T[])java.lang.reflect.Array
              .newInstance(a.getClass().getComponentType(), size); // 数组长度不够，就用反射创建新数组实例。
    Iterator<E> it = iterator();

    for (int i = 0; i < r.length; i++) {
        if (! it.hasNext()) { // fewer elements than expected
            if (a == r) {
                r[i] = null; // null-terminate
            } else if (a.length < i) {
                return Arrays.copyOf(r, i);
            } else {
                System.arraycopy(r, 0, a, 0, i);
                if (a.length > i) {
                    a[i] = null;
                }
            }
            return a;
        }
        r[i] = (T)it.next();
    }
    // more elements than expected
    return it.hasNext() ? finishToArray(r, it) : r;
}
```
