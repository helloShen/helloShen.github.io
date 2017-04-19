---
layout: post
title: "Leetcode - Algorithm - Unique Paths "
date: 2017-04-18 17:47:58
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","dynamic programming"]
level: "medium"
description: >
---

### 题目
A robot is located at the top-left corner of a m x n grid (marked 'Start' in the diagram below).

The robot can only move either down or right at any point in time. The robot is trying to reach the bottom-right corner of the grid (marked 'Finish' in the diagram below).

How many possible unique paths are there?

![robot-maze](/images/leetcode/robot-maze.png)

### 数学解决问题，排列组合公式
先把问题抽象成`一系列的方向选择`。比如在`6x4`的格子中，我一共要面临`(6-1)+(4-1)=8`次方向选择，要么向右，要么向下。限制条件是，总共要选择`5`次向右，`3`次向下。问一共有多少种选择方法。

就变成了`C(n,m)`的n选m组合问题。 **有`n`个球，从中任选`m`个，有多少种不同的组合。** 根据组合公式，
> C(n,m) = n! / (m! * (n-m)!)

因为阶乘的值很大，用`BigDecimal`来做。

#### 代码
```java
import java.math.BigDecimal;

public class Solution {
    public int uniquePaths(int m, int n) {
        if (m <= 0 || n <= 0) { return 0; }
        if (m == 1 || n == 1) { return 1; }
        int big = Math.max(m-1,n-1);
        int small = Math.min(m-1,n-1);
        BigDecimal numerator = fromTo(big+1,big+small);
        BigDecimal denominator = factorial(small);
        return numerator.divide(denominator).intValue();
    }
    //左右闭区间，例如from=8, to=8. 就等于8.
    public BigDecimal fromTo(int from, int to) {
        BigDecimal small = new BigDecimal(from);
        BigDecimal big = new BigDecimal(to);
        BigDecimal res = new BigDecimal(1);
        for (int i = from; i <= to; i++) {
            res = res.multiply(new BigDecimal(i));
        }
        return res;
    }
    // long范围太小，差不多20!的阶乘就超过long的取值范围
    public BigDecimal factorial(int n) {
        BigDecimal fac = new BigDecimal(1);
        for (int i = 1; i <= n; i++) {
            fac = fac.multiply(new BigDecimal(i));
        }
        return fac;
    }
}
```

#### 结果
`BigDecimal`的效率有限。
![unique-paths-1](/images/leetcode/unique-paths-1.png)


### 数学上做了点简化
以`C(8,5)`为例，分母上下的部分数字可以抵消，减小了计算量。
```
1 * 2 * 3 * 4 * 5 * 6 * 7 * 8
------------------------------------------
1 * 2 * 3 * 4 * 5                   #除法（可抵消）
1 * 2 * 3                           #除法
------------------------------------------
6 * 7 * 8 / 1 / 2 / 3
```

因为还是超出int的范围还是用`BigDedcimal`来做。

#### 代码
```java
import java.math.BigDecimal;

public class Solution {
    public int uniquePaths(int m, int n) {
        if (m <= 0 || n <= 0) { return 0; }
        if (m == 1 || n == 1) { return 1; }
        int big = Math.max(m-1,n-1);
        int small = Math.min(m-1,n-1);
        BigDecimal res = new BigDecimal(1);
        for (int i = 1; i <= small; i++) {
            res = res.multiply(new BigDecimal(big+i)).divide(new BigDecimal(i));
        }
        return res.intValue();
    }
}
```

#### 结果
还是因为`BigDecimal`拖后腿。
![unique-paths-2](/images/leetcode/unique-paths-2.png)


### 直接用`int`
因为发现就算用`BigDecimal`，最后转换成`int`还是要溢出。所以看来测试是接受溢出结果的。直接用`int`效果就好多了。

#### 代码
```java
public class Solution {
    public int uniquePaths(int m, int n) {
        if (m <= 0 || n <= 0) { return 0; }
        if (m == 1 || n == 1) { return 1; }
        int big = Math.max(m-1,n-1);
        int small = Math.min(m-1,n-1);
        long res = 1;
        for (int i = 1; i <= small; i++) {
            res = res * (big+i) / i;
        }
        return (int)res;
    }
}
```

#### 结果
![unique-paths-3](/images/leetcode/unique-paths-3.png)

### 普通递归展开，指数级复杂度
不用数学计算，普通回溯递归展开也是可以得到结果的。但肯定相当慢。相当于每条路线都要亲自走一遍。

#### 代码
```java
public class Solution {
    public int uniquePaths(int m, int n) {
        if (m <= 0 || n <= 0) { return 0; }
        int[] count = new int[]{ 0 };
        recursive(m,n,count);
        return count[0];
    }
    public void recursive(int m, int n, int[] count) {
        if (m == 1 && n == 1) { count[0]++; } // reach the end
        if (m > 1) { recursive(m-1, n, count); }
        if (n > 1) { recursive(m,n-1,count); }
    }
}
```

### 动态规划
典型的适合动态规划的例子。普通回溯递归之所以复杂度高，因为以每个点为起点的路线数量都重复计算了很多次。用个备忘录把结果记下来，就快了。

#### 代码
动态规划原理和代码结构都是相似的。
```java
public int uniquePaths(int m, int n) {
    if (m <= 0 || n <= 0) { return 0; }
    int[][] memo = new int[m+1][n+1];
    return dp(m,n,memo);
}
public int dp(int m, int n, int[][] memo) {
    if (memo[m][n] != 0) { return memo[m][n]; }
    if (m == 1 && n == 1) {
        memo[1][1] = 1; return 1;
    }
    int rightCount = 0, downCount = 0;
    if (m > 1) { rightCount = dp(m-1,n,memo); }
    if (n > 1) { downCount = dp(m,n-1,memo); }
    memo[m][n] = rightCount + downCount;
    return memo[m][n];
}
```

#### 结果
相当不错。动态规划速度和纯数学计算差不多了，而且代码可读性还高，还好写，逻辑简单。
![unique-paths-4](/images/leetcode/unique-paths-4.png)
