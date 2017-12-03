---
layout: post
title: "Leetcode - Algorithm - Zigzag Iterator "
date: 2017-12-03 15:29:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["list","design"]
level: "medium"
description: >
---

### 题目
Given two 1d vectors, implement an iterator to return their elements alternately.

For example, given two 1d vectors:
```
v1 = [1, 2]
v2 = [3, 4, 5, 6]
```
By calling next repeatedly until hasNext returns `false`, the order of elements returned by next should be: `[1, 3, 2, 4, 5, 6]`.

Follow up: What if you are given `k` 1d vectors? How well can your code be extended to such cases?

Clarification for the follow up question - Update (2015-09-18):
* The "Zigzag" order is not clearly defined and is ambiguous for k > 2 cases. If "Zigzag" does not look right to you, replace "Zigzag" with "Cyclic". For example, given the following input:
```
[1,2,3]
[4,5,6,7]
[8,9]
```
It should return [1,4,8,2,5,9,3,6,7].

### 用两个`Iterator<Integer>`

#### 代码
```java
public class ZigzagIterator {

    private static Iterator<Integer> ite1 = null, ite2 = null;
    private static boolean turnIte1 = false;

    public ZigzagIterator(List<Integer> list1, List<Integer> list2) {
        ite1 = list1.iterator();
        ite2 = list2.iterator();
        turnIte1 = true;
    }
    public boolean hasNext() {
        return ite1.hasNext() || ite2.hasNext();
    }
    public int next() {
        Iterator<Integer> localIte = (turnIte1)? ite1 : ite2;
        if (hasNext()) {
            if ((localIte == ite1) && (!ite1.hasNext())) { localIte = ite2; }
            if ((localIte == ite2) && (!ite2.hasNext())) { localIte = ite1; }
        }
        turnIte1 = !turnIte1;
        return localIte.next();
    }
}

/**
 * Your ZigzagIterator object will be instantiated and called as such:
 * ZigzagIterator i = new ZigzagIterator(v1, v2);
 * while (i.hasNext()) v[f()] = i.next();
 */
```

#### 结果
![zigzag-iterator-1](/images/leetcode/zigzag-iterator-1.png)


### 为了适应多个数列，可以用一个`List<Iterator<Integer>>`

#### 代码
```java
public class ZigzagIterator {

    private List<Iterator<Integer>> ites = new ArrayList<>();

    public ZigzagIterator(List<Integer> list1, List<Integer> list2) {
        Iterator<Integer> ite = list1.iterator();
        if (ite.hasNext()) { ites.add(ite); }
        ite = list2.iterator();
        if (ite.hasNext()) { ites.add(ite); }
    }
    public boolean hasNext() {
        return !ites.isEmpty();
    }
    public int next() {
        if (hasNext()) {
            Iterator<Integer> localIte = ites.remove(0);
            Integer res = localIte.next();
            if (localIte.hasNext()) {
                ites.add(ites.size(),localIte);
            }
            return res;
        }
        return 0;
    }

}

/**
 * Your ZigzagIterator object will be instantiated and called as such:
 * ZigzagIterator i = new ZigzagIterator(v1, v2);
 * while (i.hasNext()) v[f()] = i.next();
 */
```

#### 结果
![zigzag-iterator-2](/images/leetcode/zigzag-iterator-2.png)
