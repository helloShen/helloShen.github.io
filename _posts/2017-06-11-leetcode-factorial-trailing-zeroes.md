---
layout: post
title: "Leetcode - Algorithm - Factorial Trailing Zeroes "
date: 2017-06-11 23:45:42
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
Given an integer n, return the number of trailing zeroes in n!.

Note: Your solution should be in logarithmic time complexity.

### 找数学规律，复杂度 $$O(\log_{}{n})$$
首先，`trailing zeroes`就是指 **末尾的连续的`0`**。

其实很简单，因为能产生末尾的`0`的只有相乘为`10`的数。只有`2*5=10`。
> 所以本质上 **遇到一个`5`末尾就有一个`0`**。因为前面的`2`有的是。

但这题的关键点就在于，
```
25   = 5^2    // 能分解出两个5
125  = 5^3    // 能分解出三个5
...
**** = 5^n    // 能分解出n个5
```   

#### 代码
```java
public class Solution {
    public int trailingZeroes(int n) {
        int ret = 0;
        long fivePower = 1l;
        while (true) {
            fivePower = fivePower * 5;
            if (n >= fivePower) {
                ret += n / fivePower;
            } else {
                break;
            }
        }
        return ret;
    }
}
```

以下是简洁版，
```java
public class Solution {
    public int trailingZeroes(int n) {
        int ret = 0;
        while (n >= 5) {
            ret += n / 5;
            n /= 5;
        }
        return ret;
    }
}
```

#### 结果
![factorial-trailing-zeroes-1](/images/leetcode/factorial-trailing-zeroes-1.png)


### 递归版
递归版只有一行，你怕不怕？

#### 代码
```java
public class Solution {
    public int trailingZeroes(int n) {
        return (n == 0)? 0 : n / 5 + trailingZeroes(n/5);
    }
}
```

#### 结果
![factorial-trailing-zeroes-2](/images/leetcode/factorial-trailing-zeroes-2.png)
