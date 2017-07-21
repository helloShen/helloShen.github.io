---
layout: post
title: "Leetcode - Algorithm - Strobogrammatic Number "
date: 2017-07-20 22:58:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string","math","hash table"]
level: "easy"
description: >
---

### 题目
A strobogrammatic number is a number that looks the same when rotated 180 degrees (looked at upside down).

Write a function to determine if a number is strobogrammatic. The number is represented as a string.

For example, the numbers `69`, `88`, and `818` are all strobogrammatic.

### 用指针
能组成对称数字的阿拉伯数字只有`0`,`1`,`8`，以及比较特殊的`6`和`9`。如果是`0`,`1`,`8`组成只需要是对称的回文就可以，如果出现`6`则对称位置必须是`9`，反之亦然。

所以就用两个指针指向字符串的首尾。用判断回文的方法，将两个指针向中心靠拢。



#### Java代码
```java
public class Solution {
    public boolean isStrobogrammatic(String num) {
        int len = num.length();
        if (len == 0) { return false; }
        int lo = 0, hi = num.length()-1;
        while (lo <= hi) {
            char l = num.charAt(lo++);
            char r = num.charAt(hi--);
            if (
                !(l == '0' && r == '0') &&
                !(l == '1' && r == '1') &&
                !(l == '8' && r == '8') &&
                !(l == '6' && r == '9') &&
                !(l == '9' && r == '6')
            ) { return false; }
        }
        return true;
    }
}
```

#### 再耍个小聪明
还是用刚才的两个指针的方法。只不过判断的条件被简化在了一个字符串里`00 11 88 696`。

#### Java代码
```java
public boolean isStrobogrammatic(String num) {
    for (int i=0, j=num.length()-1; i <= j; i++, j--)
        if (!"00 11 88 696".contains(num.charAt(i) + "" + num.charAt(j)))
            return false;
    return true;
}
```

#### 结果
![strobogrammatic-number-1](/images/leetcode/strobogrammatic-number-1.png)


### 用`stack`
当然也可以用`stack`来做，把历史记录压入`stack`，但有容器的开销，效率就不高。代码很简单就不写了。
