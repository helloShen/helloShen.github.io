---
layout: post
title: "Leetcode - Algorithm - Count And Say "
date: 2017-04-08 17:11:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "easy"
description: >
---

### 题目
The count-and-say sequence is the sequence of integers beginning as follows:
```
1, 11, 21, 1211, 111221, ...
```

`1` is read off as "one 1" or `11`.
`11` is read off as "two 1s" or `21`.
`21` is read off as "one 2, then one 1" or `1211`.
Given an integer n, generate the nth sequence.

Note: The sequence of integers will be represented as a string.

### 递归版
`base case`是`1`。写一个`read()`函数，负责把之前的一个数字朗读出来。然后递归调用`read()`函数，朗读之前朗读出来的结果。

#### 代码
```java
public class Solution {
    public String countAndSay(int n) {
        if (n < 1) { return ""; }
        if (n == 1) { return "1"; }
        return read(countAndSay(n-1));
    }
    public String read(String s) {
        int cursor = 0, count = 1;
        char reg = s.charAt(cursor);
        StringBuilder sb = new StringBuilder();
        while (++cursor < s.length()) {
            char c = s.charAt(cursor);
            if (c != reg ) {
                sb.append(Integer.toString(count)).append(reg);
                reg = c;
                count = 1;
            } else {
                count++;
            }
        }
        sb.append(Integer.toString(count)).append(reg);
        return sb.toString();
    }
}
```

#### 结果
不错。应该没有更高级别银弹了。
![count-and-say-1](/images/leetcode/count-and-say-1.png)


### 迭代版

#### 代码
```java
public class Solution {
    public String countAndSay(int n) {
        if (n < 1) { return ""; }
        String bootstrap = "1";
        while (--n > 0) {
            bootstrap = read(bootstrap);
        }
        return bootstrap;
    }
    public String read(String s) {
        int cursor = 0, count = 1;
        char reg = s.charAt(cursor);
        StringBuilder sb = new StringBuilder();
        while (++cursor < s.length()) {
            char c = s.charAt(cursor);
            if (c != reg) {
                sb.append(Integer.toString(count)).append(reg);
                reg = c;
                count = 1;
            } else {
                count++;
            }
        }
        sb.append(Integer.toString(count)).append(reg);
        return sb.toString();
    }
}
```

#### 结果
nice!
![count-and-say-2](/images/leetcode/count-and-say-2.png)
