---
layout: post
title: "Leetcode - Algorithm - Isomorphic Strings "
date: 2017-06-19 04:17:54
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","hash table"]
level: "easy"
description: >
---

### 题目
Given two strings s and t, determine if they are isomorphic.

Two strings are isomorphic if the characters in s can be replaced to get t.

All occurrences of a character must be replaced with another character while preserving the order of characters. No two characters may map to the same character but a character may map to itself.

For example,
Given `egg`, `add`, return true.
Given `foo`, `bar`, return false.
Given `paper`, `title`, return true.

Note:
You may assume both s and t have the same length.

### 基本思路
首先这题的本质是一个 **映射** 关系，这种映射必须是一一对应。不但要记录，还要查询映射信息。

所以用`HashMap`记录映射关系，可以在 $$O(1)$$ 时间内查询。 所以整体 $$O(n)$$ 的时间复杂度， $$O(1)$$ 的空间复杂度已经是极限。

如果还要优化，可以从数据容器的角度出发，看能不能不使用额外空间，或者用数组完成，可以省掉创建容器的开销。

结果应为无论如何是要记录映射信息，不可能不使用额外空间。但用数组储存映射信息是可能的。

### 用`HashMap`记录映射表
相当于每个字母都有对应的编码，而且都是一一对应的关系。
```
title & paper
t <=> p
i <=> a
l <=> e
e <=> r
```
所以需要两个`HashMap`，一个记录`t`映射到`p`：`t => p`。一个记录`p`映射到`t`：`t <= p`。

#### 代码
```java
public class Solution {
    public boolean isIsomorphic(String s, String t) {
        Map<Character,Character> ts = new HashMap<>(); // key = t, value = s
        Map<Character,Character> st = new HashMap<>(); // key = s, value = t
        for (int i = 0; i < s.length(); i++) {
            Character cs = s.charAt(i);
            Character ct = t.charAt(i);
            Character csShouldBe = ts.get(ct);
            Character ctShouldBe = st.get(cs);
            if (csShouldBe == null && ctShouldBe == null) {
                ts.put(ct,cs);
                st.put(cs,ct);
            } else if (csShouldBe == null || ctShouldBe == null || csShouldBe != cs || ctShouldBe != ct) {
                return false;
            }
        }
        return true;
    }
}
```

#### 结果
![isomorphic-strings-1](/images/leetcode/isomorphic-strings-1.png)


### 用数组记录映射表
String中的所有char都属于ASCII字符集，对应`[0,255]`。这样就可以用一个`int[256]`来记录映射，键值`key`相当于数组下标。

#### 代码
```java
public class Solution {
    public boolean isIsomorphic(String s, String t) {
        char[] ts = new char[256];
        char[] st = new char[256];
        for (int i = 0; i < s.length(); i++) {
            char cs = s.charAt(i);
            char ct = t.charAt(i);
            char csShouldBe = ts[ct];
            char ctShouldBe = st[cs];
            if (csShouldBe == '\u0000' && ctShouldBe == '\u0000') {
                ts[ct] = cs;
                st[cs] = ct;
            } else if (csShouldBe == '\u0000' || ctShouldBe == '\u0000' || csShouldBe != cs || ctShouldBe != ct) {
                return false;
            }
        }
        return true;
    }
}
```

#### 结果
![isomorphic-strings-2](/images/leetcode/isomorphic-strings-2.png)
