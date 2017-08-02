---
layout: post
title: "Leetcode - Algorithm - Ugly Number Two "
date: 2017-08-01 19:57:22
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","dynamic programming","heap"]
level: "medium"
description: >
---

### 题目
Write a program to find the n-th ugly number.

Ugly numbers are positive numbers whose prime factors only include `2`, `3`, `5`. For example, `[1, 2, 3, 4, 5, 6, 8, 9, 10, 12]` is the sequence of the first 10 ugly numbers.

Note that 1 is typically treated as an ugly number, and n does not exceed `1690`.

### 利用`isUglyNumber()`函数判断每个自增的自然数
这是一种笨办法。

#### 代码
```java
public class Solution {
    public int nthUglyNumber(int num) {
        if (num < 0) { return 0; }
        int count = 0;
        int val = 1;
        while (true) {
            if (isUglyNumber(val)) { ++count; }
            if (count == num) { return val; }
            ++val;
        }
    }
    /* 判断参数num是不是ugly number */
    private boolean isUglyNumber(int num) {
        if (num <= 0) { return false; }
        while (num > 1) {
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
![ugly-number-two-1](/images/leetcode/ugly-number-two-1.png)


### 用`Map`记录所有找到过的 Ugly Number，相当于一个`Heap`
Ugly Number的分布到后来是非常稀疏的。在带符号`int`取值范围内也只有差不多`1690`个。这也是这题限定到`1690`的原因。所以可以用一个`Map`把找到过的所有ugly number和他们的序号存起来。

因为是自然数自增顺序调用`isUglyNumber()`函数检查，可以用一个`maxTest`标记检查到哪里，在`[0,maxTest]`范围内的数字，只要没有记录在`Map`里的都确定不是ugly number. 因为标记出最大元素和检查过的元素范围，相当于一个`Max Heap`。

整个过程用递归来写非常清楚。

#### 代码
```java
public class Solution{

    private Map<Integer,Integer> uglyNums = new HashMap<>(); // 记录目前找到的所有ugly number
    { uglyNums.put(1,1); } // base case
    private int maxIndex = 1; // 目前map里记录的最大的ugly number
    private int maxTest = 1; // 目前为止检测过的最大的数字（之前的数字都检查过）

    /* 以记录中最大的ugly number为起点，接着往下找 */
    public int nthUglyNumber(int num) {
        if (num <= 0) { return 0; }
        if (num <= maxIndex) {
            return uglyNums.get(num);
        } else {
            return mthToNth(maxIndex,uglyNums.get(maxIndex),num);
        }
    }
    /* 从第m个ugly number开始，找到第n个ugly number. m < n */
    public int mthToNth(int m, int mth, int n) {
        while (true) {
            if (isUglyNumber(++mth)) {
                uglyNums.put(++m,mth); // 由mthToNth()函数维护map
                maxIndex = m; //由mthToNth()函数维护maxIndex
            }
            maxTest = mth;
            if (m == n) { return mth; }
        }
    }
    /* 判断参数num是不是ugly number, 也可以利用过去的记录 */
    private boolean isUglyNumber(int num) {
        if (num <= 0) { return false; }
        if (num <= maxTest) { return uglyNums.values().contains(num); } // uglyNums里没记的就都不是
        if (num % 2 == 0) { return isUglyNumber(num/2); }
        if (num % 3 == 0) { return isUglyNumber(num/3); }
        if (num % 5 == 0) { return isUglyNumber(num/5); }
        return false;
    }
}
```

#### 结果
![ugly-number-two-2](/images/leetcode/ugly-number-two-2.png)


### 用一个超大数组，$$O(n)$$
内存容不下`Integer.MAX_VALUE`大小的数组。

#### 代码
```java
/* 用一个超大数组记录ugly number信息 */
public class Solution {

    private int[] memo = new int[Integer.MAX_VALUE-8]; // 如果记录>0，说明是ugly number, 而且记录数字就代表是第几个ugly number。
    private int max = 0; // 标记当前检查到哪个数

    public int nthUglyNumber(int num) {
        int val = 0;
        int count = 0;
        while (true) {
            if (isUglyNumber(++val)) { memo[val] = ++count; }
            max = val;
            if (count == num) { return val; }
        }
    }
    /* return if num is an ugly number */
    private boolean isUglyNumber(int num) {
        if (num <= max) { return memo[num] > 0; }
        if (num == 1) { memo[1] = 1; return true; } // base case
        if (num % 2 == 0) { return isUglyNumber(num/2); }
        if (num % 3 == 0) { return isUglyNumber(num/3); }
        if (num % 5 == 0) { return isUglyNumber(num/5); }
        return false;
    }
}
```

### 最优解法：动态规划。$$O(n)$$
所有Ugly Number都包含在下面三个序列里。
```
(1) 1×2, 2×2, 3×2, 4×2, 5×2, …
(2) 1×3, 2×3, 3×3, 4×3, 5×3, …
(3) 1×5, 2×5, 3×5, 4×5, 5×5, …
```
这有种自包含的意思，每个序列都是`[2 or 3 or 5] * (ugly number sequence)`。然后利用类似`merge sort`的三个指针，指向三个序列，取指针指向的三个元素中最小的一个，被选中的序列指针后移。

#### Java代码
```java
public class Solution {
        public int nthUglyNumber(int num) {
            if (num <= 0) { return 0; }
            int[] uglyNums = new int[num+1];
            uglyNums[1] = 1;
            int index2 = 1, index3 = 1, index5 = 1;
            int factor2 = 2, factor3 = 3, factor5 = 5;
            for (int i = 2; i <= num; i++) {
                int min = Math.min(Math.min(factor2,factor3),factor5);
                uglyNums[i] = min;
                if (min == factor2) { factor2 = 2 * uglyNums[++index2]; }
                if (min == factor3) { factor3 = 3 * uglyNums[++index3]; }
                if (min == factor5) { factor5 = 5 * uglyNums[++index5]; }
            }
            return uglyNums[num];
        }
}
```

#### 结果
![ugly-number-two-3](/images/leetcode/ugly-number-two-3.png)
