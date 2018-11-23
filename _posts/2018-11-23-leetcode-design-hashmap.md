---
layout: post
title: "Leetcode - Algorithm - Design Hashmap "
date: 2018-11-23 00:11:27
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table"]
level: "easy"
description: >
---

### 题目
Design a HashMap without using any built-in hash table libraries.

To be specific, your design should include these functions:

* `put(key, value)` : Insert a (key, value) pair into the HashMap. If the value already exists in the HashMap, update the value.
* `get(key)`: Returns the value to which the specified key is mapped, or -1 if this map contains no mapping for the key.
* `remove(key)` : Remove the mapping for the value key if this map contains the mapping for the key.

Example:
```
MyHashMap hashMap = new MyHashMap();
hashMap.put(1, 1);          
hashMap.put(2, 2);         
hashMap.get(1);            // returns 1
hashMap.get(3);            // returns -1 (not found)
hashMap.put(2, 1);          // update the existing value
hashMap.get(2);            // returns 1
hashMap.remove(2);          // remove the mapping for 2
hashMap.get(2);            // returns -1 (not found)
```

Note:
* All keys and values will be in the range of [0, 1000000].
* The number of operations will be in the range of [1, 10000].
* Please do not use the built-in HashMap library.

### 用一个数组实现
考虑到最多只有10000次操作，使用一个固定大小为10000的数组。数组的每个槽位为了应付碰撞都是一个`List`，其中的元素为一个`int[]`数组表示的键值对。所以数组的构建方式如下，

```java
List[] table = (LinkedList<int[]>[]) new LinkedList[SIZE];
```

这里涉及到使用泛型数组的问题。不能直接用泛型数组，而是先创建原生类型`ArrayList[]`数组，然后在强制转型。只在编译期对类型进行检查。运行时虚拟机不知道具体类型。

第二个要注意的点是因为涉及从中间删除元素，所以用`LinkedList`效率比`ArrayList`要高。

#### 代码
```java
class MyHashMap {

    private static final int SIZE = 10000;
    private List[] table;

    public MyHashMap() {
         table = (LinkedList<int[]>[]) new LinkedList[SIZE];
    }

    public void put(int key, int value) {
        int hash = hash(key);
        if (table[hash] == null) table[hash] = new LinkedList<int[]>();
        List list = table[hash];
        for (Object o : list) {
            int[] entry = (int[]) o;
            if (entry[0] == key) {
                entry[1] = value;
                return;
            }
        }
        list.add(new int[]{key, value});
    }

    public int get(int key) {
        int hash = hash(key);
        List list = table[hash];
        if (list == null) return -1;
        for (Object o : list) {
            int[] entry = (int[]) o;
            if (entry[0] == key) return entry[1];
        }
        return -1;
    }

    public void remove(int key) {
        int hash = hash(key);
        List list = table[hash];
        if (list == null) return;
        Iterator<Object> ite = list.iterator();
        while (ite.hasNext()) {
            int[] entry = (int[]) ite.next();
            if (entry[0] == key) {
                ite.remove();
                return;
            }
        }
        if (table[hash].isEmpty()) table[hash] = null;
    }

    private int hash(int n) {
        return (51 + 31 * n) % SIZE;
    }
}
```

#### 结果
![design-hashmap-1](/images/leetcode/design-hashmap-1.png)
