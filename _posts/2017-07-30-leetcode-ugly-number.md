---
layout: post
title: "Leetcode - Algorithm - Ugly Number "
date: 2017-07-30 15:06:40
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
Write a program to check whether a given number is an ugly number.

Ugly numbers are positive numbers whose prime factors only include `2`, `3`, `5`. For example, `6`, `8` are ugly while 14 is not ugly since it includes another prime factor `7`.

Note that `1` is typically treated as an ugly number.

### 直接用除法
如果一个数本身是素数，只要它不是`2`或`3`或`5`，都是不是丑陋数。
`[1，2，3，4，5，6，8，9，10，12，15]`是前10个丑陋数。

#### Java代码
```java
public class Solution {
    public boolean isUgly(int num) {
        if (num <= 0) { return false; }
        while (true) {
            if (num % 2 == 0) { num /= 2; continue; }
            if (num % 3 == 0) { num /= 3; continue; }
            if (num % 5 == 0) { num /= 5; continue; }
            break;
        }
        return num == 1;
    }
}
```

#### 结果
![ugly-number-1](/images/leetcode/ugly-number-1.png)

#### C代码
没有用官方的`bool`型。只是用`typedef`给`int`型换了一个别名。
```c
#include <stdio.h>

typedef int bool;
#define T 1
#define F 0

bool isUgly(int num);

bool isUgly(int num) {
    bool res = 0;
    if (num <= 0) { return res; }
    while (T) {
        if (num % 2 == 0) { num /= 2; continue; }
        if (num % 3 == 0) { num /= 3; continue; }
        if (num % 5 == 0) { num /= 5; continue; }
        break;
    }
    if (num == 1) { res = 1; }
    return res;
}

int main() {
    for (int i = 0; i < 20; i++) {
        char *answer = "false";
        if (isUgly(i) == T) { answer = "true"; }
        printf("%2d is ugly number? %5s\n",i,answer);
    }
}
```
