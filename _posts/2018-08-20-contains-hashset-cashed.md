---
layout: post
title: "HashSet.contains() Method is Cached"
date: 2018-08-20 16:36:31
author: "Wei SHEN"
categories: ["data structure"]
tags: ["hash table"]
description: >
---

### HashSet的`contains()`函数先调用`hashCode()`比较散列值，再调用`equals()`函数
HashSet的`contains()`函数调用的是`HashMap`的`containsKey()`函数， **后者比较元素是先调用`hashCode()`比较两者的散列值，再调用`equals()`函数进行比较。**

所以，如果`hashCode()`是惰性更新（只在第一次调用`hashCode()`时计算一次），那之后即是对象的成员字段被修改，这个改动也不会反映在散列值上。

所以另一种意义上说，
> **除非是不可变对象，否则谨慎使用惰性更新的散列值。**

源码如下，
```java
public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }
}
/**
 * Implements Map.get and related methods
 *
 * @param hash hash for key
 * @param key the key
 * @return the node, or null if none
 */
final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            do {
                if (e.hash == hash &&  //先比较散列值
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```


### 错误范例
下面的`containsAllPoints()`函数试图检查一个点集合`testCollection`中的所有点是否都包含在另一个点集合`points`中。它把`points`集合中的所有点都加入`HashSet`里，然后对`testCollection`中的每个点逐一检查。

为了节省空间，只创建了一个`Point`对象。每次将要比较的点坐标信息赋值给这个对象，然后调用`HashSet`的`contains()`函数检查，
```java
public boolean containsAllPoints(int[][] points, int[][] testCollection) {
    Set<Point> pointsSet = new HashSet<>();
    for (int[] point : points) {
        pointsSet.add(new Point(point[0],point[1]);
    }
    Point expected = new Point(); //为了节省空间，只创建一个点对象
    for (int[] point : testCollection) {
        expected.x = point[0]; //每次都更新这个点对象的数据
        expected.y = point[1];
        if (!pointsSet.contains(expected)) {
            System.out.println("点：" + expected + "没找到！");
        }
        System.out.println("点：" + expected + "找到了！");
    }
}
```
Point类重写了`equals()`函数，它同时比较x轴和y轴坐标。
```java
private class Point {
    private int x; //点的x轴坐标
    private int y; //点的y轴坐标
    private Point() {
        x = 0;
        y = 0;
    }
    private Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    @Override
    public boolean equals(Object obj) { //equals()函数同时比较点的x轴和y轴坐标
        Point anotherPoint = (Point)obj;
        return (anotherPoint.x == x) && (anotherPoint.y == y);
    }
    private int hash = 0;
    @Override
    public int hashCode() {
        if (hash == 0) { //惰性更新是罪魁祸首
            hash = x * 31 + y;
        }
        return hash;
    }
}
```

**但这个代码无法判断集合中是否包含某个点。** 就是因为`hashCode()`函数惰性更新`Point`对象的散列值。每个对象只计算一次散列值。所以后面尽管对象成员字段修改了，`contains()`函数因为先调用`hashCode()`比较散列值，散列值不同，就不会调用`equals()`函数。
