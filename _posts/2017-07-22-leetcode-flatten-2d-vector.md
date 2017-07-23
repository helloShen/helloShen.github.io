---
layout: post
title: "Leetcode - Algorithm - Flatten 2d Vector "
date: 2017-07-22 19:07:04
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["design"]
level: "medium"
description: >
---

### 主要收获
> 好的代码就像说话一样。好的接口设计，让用户程序员能写出像说话一样的代码。好的`while()`循环语句就像我们在用最简明最自然的方式在提一个条件，把事情的轻重缓急娓娓道来。

### 题目
Implement an iterator to flatten a 2d vector.

For example,
Given 2d vector =
```
[
  [1,2],
  [3],
  [4,5,6]
]
```
By calling next repeatedly until hasNext returns false, the order of elements returned by next should be: `[1,2,3,4,5,6]`.

Follow up:
As an added challenge, try to code it using only iterators in C++ or iterators in Java.

### 主要思路
内部维护两个`Iterator`。一个是大`List`的迭代器`Iterator<List<Integer>>`，一个是内部小的`Integer`的迭代器`Iterator<Integer>`。利用这两个迭代器遍历所有元素。但这两个迭代器的细节不向用户暴露。

### 把维护内部`Iterator`的职责放在`next()`方法里（不推荐）
考虑到为了确保安全，每次调用`next()`之前都要调用一次`hasNext()`，其实这样的设计非常别扭，所以不推荐。

#### 非常丑的Java代码
```java
public class Vector2D implements Iterator<Integer> {
        private List<List<Integer>> list;
        private int index = 0, size = 0;
        private Iterator<Integer> ite;

        public Vector2D(List<List<Integer>> vec2d) {
            list = vec2d;
            size = list.size();
            nextIte();
        }
        // maintains the ite, if there are no more list, return null
        private void nextIte() {
            ite = null;
            while (index < size) {
                List<Integer> curr = list.get(index++);
                if (!curr.isEmpty()) { ite = curr.iterator(); return; }
            }
        }
        // next() method should keep ite.hasNext() always be true
        // if there are no more list, ite should be null.
        public Integer next() {
            if (!hasNext()) { return null; }
            Integer result = ite.next();
            if (!ite.hasNext()) { nextIte(); }
            return result;
        }

        public boolean hasNext() {
            return ite != null;
        }
}
```

#### 结果
![flatten-2d-vector-1](/images/leetcode/flatten-2d-vector-1.png)


### 把维护内部`Iterator`的职责放在`hasNext()`里（推荐）
每次调用`next()`之前，都先调用一次`hasNext()`，更新一下内部两个`Iterator`，以确保`intIte.next()`能正确返回下一个元素。这样的设计比较合理。

#### Java代码
```java
public class Vector2D implements Iterator<Integer> {
        private Iterator<List<Integer>> listIte;
        private Iterator<Integer> intIte;

        public Vector2D(List<List<Integer>> vec2d) {
            listIte = vec2d.iterator();
            if (listIte.hasNext()) { intIte = listIte.next().iterator(); }
        }
        /** only call intIte.next() */
        public Integer next() {
            return (hasNext())? intIte.next() : null;
        }
        /** keep intIte.hasNext() always be true */
        public boolean hasNext() {
            if (intIte == null) { return false; }
            if (intIte.hasNext()) { return true; }
            intIte = null;
            while (listIte.hasNext()) {
                intIte = listIte.next().iterator();
                if (intIte.hasNext()) { return true; }
            }
            return false;
        }
}
```

#### 更简洁的Java代码（喜欢的版本）
好的代码就像说话一样。好的接口设计，让用户程序员能写出像说话一样的代码。好的`while()`循环语句就像我们在用最简明最自然的方式在提一个条件一样，把事情的轻重缓急娓娓道来。
```java
public class Vector2D implements Iterator<Integer> {
        private Iterator<List<Integer>> listIte;
        private Iterator<Integer> intIte;

        public Vector2D(List<List<Integer>> vec2d) {
            listIte = vec2d.iterator();
        }
        public Integer next() {
            return (hasNext())? intIte.next() : null;
        }
        public boolean hasNext() {
            while ((intIte == null || !intIte.hasNext()) && listIte.hasNext()) {
                intIte = listIte.next().iterator();
            }
            return intIte != null && intIte.hasNext();
        }
}
```

#### 结果
![flatten-2d-vector-2](/images/leetcode/flatten-2d-vector-2.png)
