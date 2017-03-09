---
layout: post
title: "[Effective Java] Note: - Chapter-2-3：Destroy the Objects"
date: 2017-02-03
author: "Wei SHEN"
categories: ["java","effective java"]
tags: ["instance control"]
description: >
  虽然Java提供了无偿的垃圾回收机制。但这不代表我们从此完全不用操心对象的销毁。
---

### 消除过期对象的引用
Java虽然提供了自动垃圾回收，但还是可能因为过期对象导致内存泄漏。下面的`Stack`类中就隐藏着一个内存泄漏的隐患。因为`pop()`方法在返回对象，往回移动指针之后没有清除`elements`数组中的引用。
```java
public class Stack {
    private Object[] elements;
    private int size =  0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }

    private void ensureCapacity() {
        if (element.length = size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

所以正确的做法是，**当一些对象过期之后，需要手动清空引用**。尤其是 **容器这种需要自己管理内存的类，尤其要注意**。

#### 缓存是内存泄漏的另一常见来源
如果缓存中的对象不太重要，只有外部还在使用它（保持着对对象的引用），它才是有意义的。那么可以使用`WeakHashMap`来储存缓存。

`WeakHashMap`使用`WeakReference`来引用它的`Key`。`WeakReference`不能保护对象被垃圾回收清理。一旦某个`Key`被清理，`WeakHashMap`中这个`Key`值对应的`Entry`也被清理掉。

`WeakReference`和`SoftReference`的区别是，`SoftReference`只在系统内存吃紧的时候才被释放。而`WeakReference`只要垃圾回收，就会被释放。

另外一种方案是利用一个`Timer`或者`ScheduledThreadPoolExecutor`后台线程来按时间清理过期对象。或者在每次给缓存添加新条目的时候清理一下缓存。

#### 缓存的第三个常见来源是监听器和回调

### 避免使用finalize()方法
关于finalize()方法的定义是这样：**它会在对象被垃圾回收之前被执行。** 问题出在，JVM并不能保证垃圾收集执行的时间，我们也没办法控制。**`System.gc()`** 和 **`System.runFinalization()`** 两个方法都只是 **建议** 虚拟机进行垃圾回收，但没有任何保障。

而且 **finalize()** 会导致性能的损耗。所以 **一般情况不要用finalize()方法**。只有在两种情况下，需要使用`finalize()`方法：
1. finalize()方法在有其他显式终止方法的情况下，充当"Safety Net"的第二层保险。
2. 用来终止本地资源。因为虚拟机无法回收本地资源。

#### 用显式终止方法代替finalize()
需要正确关闭的资源可以自己写一个`close()`方法。比如说`InputStream`和`OutputStream`和`java.sql.Connection`上的`close()`方法。一个惯用法是放在一个`try-finally`代码块中，以确保无论使用对象的时候是否抛出异常，`close()`始终能够执行。

```java
Foo foo = new Foo();
try {
    // do something with foo
} finally {
    foo.close();
}
```
