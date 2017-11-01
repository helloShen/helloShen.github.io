---
layout: post
title: "Leetcode - Algorithm - Count Numbers With Unique Digits "
date: 2017-11-01 19:28:36
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","dynamic programming"]
level: "medium"
description: >
---

### 题目
Given a non-negative integer n, count all numbers with unique digits, x, where 0 ≤ x < $$10^n$$.

Example:
Given n = 2, return 91. (The answer should be the total numbers in the range of 0 ≤ x < 100, excluding `[11,22,33,44,55,66,77,88,99]`)


### 基本思路
首先，最朴素的思路，拿到一个数字比如`2013`，逐个数字检查是否有重复数字。肯定能解决问题。缺点是效率低，需要检查每一个数字，复杂度是 $$O(10^n * n)$$。

然后考虑，阿拉伯数字一共就10个，这是一个排列组合问题。比如`n = 6`，一个五位数，没用重复数字，第一个数字有`9`种选择（不能以0开头），第二个数字有`9`个选择，第三个数字有`8`个选择，第四个数有`7`种选择，最后一个数有`6`中选择，
```bash
  10-1    9      8      7      6     ...
   |      |      |      |      |
| pos1 | pos2 | pos3 | pos4 | pos5 | ...
```

最后这还是一个套嵌问题，当`n = 6`，不但要考虑所有5位数，还要考虑包括所有可能的4位数组合，3位数组合，2位数组合，1位数组合。这样的问题可以很简单的地用一个递归的动态规划解决。
> T(n) = T(n-1) + f(n)

#### 代码1
简单地把刚才的思路转化成代码，代码如下，
```java
class Solution {

    private static final int[] base = new int[]{9,9,8,7,6,5,4,3,2,1};

    public int countNumbersWithUniqueDigits(int n) {
        if (n < 0) { return 0; }
        int count = 1;
        for (int i = 0; i < n; i++) {
            int product = 1;
            for (int j = 0; j <= i; j++) {
                product *= base[j];
            }
            count += product;
        }
        return count;
    }
}
```

#### 结果
![count-numbers-with-unique-digits-1](/images/leetcode/count-numbers-with-unique-digits-1.png)


#### 代码
一个可能的小优化是，`product`可以不用每次从新开始计算，而是在前一次计算的基础上累乘，
```java
class Solution {
    public int countNumbersWithUniqueDigits(int n) {
        if (n < 0) { return 0; }
        int availableNumber = 10, product = 1;
        int count = 1; // special case: only 0 can start with 0
        for (int i = 0; i < n; i++) {
            product *= (i == 0)? (availableNumber - 1 ) : availableNumber; // avoid numbers start with 0
            count += product;
            availableNumber--;
        }
        return count;
    }
}
```

#### 结果
![count-numbers-with-unique-digits-1](/images/leetcode/count-numbers-with-unique-digits-1.png)
