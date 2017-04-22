---
layout: post
title: "Leetcode - Algorithm - Climbing Stairs "
date: 2017-04-21 17:17:12
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","dynamic programming"]
level: "easy"
description: >
---

### 题目

### 自底向上的动态规划求`Fibonacci` $$O(n)$$
实际上是一个标准`Fibonacci`数列。每格台阶路线数，等于上一阶台阶和上两阶台阶路线之和。创建一个数组，用自底向上的动态规划计算数组中每个元素的值。

用了一个小技巧，比如当台阶数为`5`的时候。申请一个长度为`8`的数组，最高以为装答案，最低两位是哨兵，分别初始化为`0`和`1`，作为引导值。这样就算第一个，第二个数字的产生也可以一般化为`f(n)=f(n+1)+f(n+2)`，而不用每次都在循环里判断是不是`base case`。
```
[0,0,0,0,0,0,0,1]
```
填完之后的数组像下面这样，`13`就是最后的答案。
```
[13,8,5,3,2,1,1,0,1]
```

#### 代码
```java
public class Solution {
    public int climbStairs(int n) { // 实际上是个Fibonicci数列
        if (n <= 0) { return 0; } // 防御
        int[] res = new int[n+3];
        res[n+1] = 0; res[n+2] = 1; // 哨兵
        for (int i = n; i >= 0; i--) {
            res[i] = res[i+1] + res[i+2];
        }
        return res[0];
    }
}
```

#### 结果
![climbing-stairs-1](/images/leetcode/climbing-stairs-1.png)
