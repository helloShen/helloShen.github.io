---
layout: post
title: "doubleCapacity() Method of HashMap in Android"
date: 2016-09-28 19:24:04
author: "Wei SHEN"
categories: ["java","android","data structure"]
tags: ["container","hashmap"]
description: >
  这个做法是出于效率的考虑。优点在于，他是把整棵树的引用一起搬过去的。在碰撞啊比较严重的情况下（这棵树比较大），再以next子节点高位highBit来判断，next指向的子树需不需要搬家。 50%的概率是不需要搬的。理论上省了一半的搬家时间。实际未必能达到这个效果。
---

### 问题
Android环境下的HashMap类中，扩容后重新哈希的代码中，计算index不是像put中的那样使用hash & newCapacity - 1，而是使用hash & oldCapacity得到hightBit,再用hightBit | j得到index。二者的结果相同，但是计算步骤上反而是后者多了一步或运算。这样写的好处在哪里？

```java
private HashMapEntry<K, V>[] doubleCapacity() {
        HashMapEntry<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            return oldTable;
        }
        int newCapacity = oldCapacity * 2;
        HashMapEntry<K, V>[] newTable = makeTable(newCapacity);
        if (size == 0) {
            return newTable;
        }

        for (int j = 0; j < oldCapacity; j++) {
            /*
             * Rehash the bucket using the minimum number of field writes.
             * This is the most subtle and delicate code in the class.
             */
            HashMapEntry<K, V> e = oldTable[j];
            if (e == null) {
                continue;
            }
            int highBit = e.hash & oldCapacity;
            HashMapEntry<K, V> broken = null;
            newTable[j | highBit] = e;
            for (HashMapEntry<K, V> n = e.next; n != null; e = n, n = n.next) {
                int nextHighBit = n.hash & oldCapacity;
                if (nextHighBit != highBit) {
                    if (broken == null)
                        newTable[j | nextHighBit] = n;
                    else
                        broken.next = n;
                    broken = e;
                    highBit = nextHighBit;
                }
            }
            if (broken != null)
                broken.next = null;
        }
        return newTable;
    }
```

### 解答
Android这么玩，是基于一个事实：HashMap的长度是2的整数次幂。

HashMap每次扩容，也是长度翻倍：
* **int newCapacity = oldCapacity * 2;**

这样的好处是，可以非常简单地用作为掩码获得数组下标。（具体参见另一个回答：JDK 源码中 HashMap 的 hash 方法原理是什么？ - 胖胖的回答）。以原始长度16为例：
```bash
        10100101 11000100 00110101
&	00000000 00000000 00001111    //掩码=16-1
----------------------------------
	00000000 00000000 00000101    //高位全部归零，只保留末四位
```

所以，每次扩容，数组的下标的变化其实很微妙：
只有前面加了一位，后面几位保持不变。
比如长度16扩容到了32：
```bash
        10100101 11000100 00110101
&	00000000 00000000 00011111    //掩码=32-1
----------------------------------
	00000000 00000000 00010101    //只是第5位变化了
```

只有低位第5位是可能变化的。
所以真实情况是：**只有一半的元素需要搬家。**


这个前提下，再来看JDK里HashMap扩容，是不是有点太老实了。transfer( )方法两层迭代，把Entry一个个搬家。这里重新取下标indexFor( )方法就是题主说的：hash & (newCapacity - 1)。
```java
    /**
     * Transfers all entries from current table to newTable.
     */
    void transfer(Entry[] newTable, boolean rehash) {
        int newCapacity = newTable.length;
        for (Entry<K,V> e : table) {
            while(null != e) {
                Entry<K,V> next = e.next;
                if (rehash) {
                    e.hash = null == e.key ? 0 : hash(e.key);
                }
                int i = indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            }
        }
    }

    /**
     * Returns index for hash code h.
     */
    static int indexFor(int h, int length) {
        return h & (length-1);
    }
```

Android搞了一个嫁接：
```java
int highBit = e.hash & oldCapacity;
```
以oldCapacity为掩码，highBit单独把原先散列值里会变化的那一位切出来。还是以16位到32位扩容为例：
```bash
        10100101 11000100 00110101
&	00000000 00000000 00010000    //掩码=16
----------------------------------
	00000000 00000000 00010000    //highBit只保留第5位
```

highBit把第5位单独切出来，而代表原来的后4位散列值。嫁接到一起（用“|”或操作完成），正好是新的5位散列值。
```java
newTable[j | highBit] = e;
```

这个做法的优点在于，他是把整棵树的引用一起搬过去的。在碰撞啊比较严重的情况下（这棵树比较大），再以next子节点高位highBit来判断，next指向的子树需不需要搬家。

**50%的概率是不需要搬的。**
```java
                if (nextHighBit != highBit) {
                    if (broken == null)
                        newTable[j | nextHighBit] = n;
                    else
                        broken.next = n;
                    broken = e;
                    highBit = nextHighBit;
                }
```

像HashMap这种每天要被用无数次的基础设施，微小的优化都会为世界节省很多时间。
