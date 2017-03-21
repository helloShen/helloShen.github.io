---
layout: post
title: "Leetcode - Algorithm - Reverse Integer"
date: 2017-03-21 01:40:56
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode","primitive type"]
level: "easy"
description: >
---

### 题目
Reverse digits of an integer.
```
Example1: x = 123, return 321
Example2: x = -123, return -321
```
click to show spoilers.

Note:
The input is assumed to be a 32-bit signed integer. Your function should **return 0 when the reversed integer overflows.**

### 回顾一下int
Java中`int`基本型，使用 **`32位带符号2的补码`** 来表示正负数。Oracle官方手册上的定义如下：
> By default, the int data type is a 32-bit signed two's complement integer, which has a minimum value of $$-2^{31}$$ and a maximum value of $$2^{31}-1$$.

`2的补码`表示负数的机制如下图，
![two-complement](/images/int-princeple/int-1.gif)

把一个正数，转换成对应的负数，分两步走，
1. 每一个二进制位都取相反值，0变成1，1变成0。
2. 将上一步得到的值加1。

2的补码的本质是： **一对正负数，二进制加起来，正好进位**。
```
１００００００００
－００００１０００   // +8
－－－－－－－－－
　１１１１１０００   // -8
```

### 利用String转码
`Integer.toString(int i)`可以帮我们做这个`int -> String`的转换。为什么不用呢？ 然后，`String`本身没有`reverse()`方法。但`StringBuilder`有。倒序之后，再用`Integer.parseInt(String s)`，从`String`转回`int`。这里用`long`是为了避免溢出。

#### 代码
```java
public class Solution {
    // int -> String -> StringBuilder -> reverse StringBuilder -> long -> int
    public int reverse(int x) {
        String num = Integer.toString(x); // decode
        StringBuilder sb = new StringBuilder(num).reverse(); // reverse
        if (sb.charAt(sb.length()-1) == '-') { // negative
            sb.deleteCharAt(sb.length()-1);
            sb.insert(0,'-');
        }
        long longX = Long.parseLong(sb.toString()); // encode
        return (longX < (long)Integer.MIN_VALUE || longX > (long)Integer.MAX_VALUE)? 0:(int)longX; // overflow
    }
}
```

#### 结果
还不错。
![reverse-integer-1](/images/leetcode/reverse-integer-1.png)

### 通过计算
通过`x % 10`这样一个简单的计算，可以取出十进制的每一位。代码只有6行，很Q。

#### 代码
```java
public class Solution {
    public int reverse(int x) {
        long result = 0l;
        while(x != 0) {
            result = result * 10 + (x % 10);
            x = x/10;
        }
        return (result < (long)Integer.MIN_VALUE || result > (long)Integer.MAX_VALUE)? 0:(int)result; // overflow
    }
}
```

#### 结果
比用String解码稍微快一点。
![reverse-integer-2](/images/leetcode/reverse-integer-2.png)
