---
layout: post
title: "Leetcode - Algorithm - Reverse String "
date: 2017-08-03 15:01:21
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "easy"
description: >
---

### 题目
Write a function that takes a string as input and returns the string reversed.

Example:
Given s = `hello`, return `olleh`.

### 变成数组，然后交换首尾元素
只需要额外一位寄存器。

#### 代码
```java
public class Solution {
    public String reverseString(String s) {
        char[] chars = s.toCharArray();
        char register = '\0';
        int lo = 0, hi = chars.length-1;
        while (lo < hi) {
            register = chars[lo];
            chars[lo++] = chars[hi];
            chars[hi--] = register;
        }
        return new String(chars);
    }
}
```

#### 结果
![reverse-string-1](/images/leetcode/reverse-string-1.png)
