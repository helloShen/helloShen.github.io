---
layout: post
title: "Leetcode - Algorithm - Super Ugly Number "
date: 2018-08-04 20:38:04
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","math","heap"]
level: "meidum"
description: >
---

### 题目
Write a program to find the nth super ugly number.

Super ugly numbers are positive numbers whose all prime factors are in the given prime list primes of size k.

Example:
```
Input: n = 12, primes = [2,7,13,19]
Output: 32
Explanation: [1,2,4,7,8,13,14,16,19,26,28,32] is the sequence of the first 12 super ugly numbers given primes = [2,7,13,19] of size 4.
```
Note:
* 1 is a super ugly number for any given primes.
* The given numbers in primes are in ascending order.
* 0 < k ≤ 100, 0 < n ≤ 106, 0 < primes[i] < 1000.
* The nth super ugly number is guaranteed to fit in a 32-bit signed integer.

### 暴力除法
比较笨的办法是逐个数字去判断它是不是丑陋数。

对丑陋数的判断很简单，拿列表中的素数挨个除，如果最后能除尽，就是丑陋数，否则就不是。

#### 代码
```java
public int nthSuperUglyNumber(int n, int[] primes) {
    if (n == 1) { return 1; }
    n--;
    int next = 2;
    for( ; n > 0; next++) {
        if (isUglyNumber(next,primes)) {
            n--;
        }
    }
    return next-1;
}

private boolean isUglyNumber(int n, int[] primes) {
    for (int prime : primes) {
        while (n % prime == 0) { n /= prime;}
    }
    return n == 1;    // 最后除干净得到1，就肯定是。
}
```

#### 结果
![super-ugly-number-1](/images/leetcode/super-ugly-number-1.png)


### 自己构造每个丑陋数
以`[2,7,13,19]`为例，首先`1`肯定是丑陋数，
```
丑陋数： 1
```
然后用`1`去乘以`[2,7,13,19]`里的每一个数，得到4个候选数。这时候，四个候选数的乘数都是`1`，
```
原始素数：   2,      7,      13,     19
候选乘数：   1,      1,      1,      1
候选数：    2*1,    7*1,   13*1,    19*1
丑陋数： 1,
```
取其中最小的数`2`作为下一个丑陋数，同时拿走的那个`2`的位置更新下一个候选数：`2*2`。这个`*2`就是指`1`后面的一个丑陋数。
```
原始素数：   2,      7,      13,     19
候选乘数：   2,      1,      1,      1
候选数：    2*2,    7*1,   13*1,    19*1
丑陋数： 1,2
```
第三轮，还是在4个候选数中选一个最小的，`4`，更新为下一个丑陋数，
```
原始素数：   2,      7,      13,     19
候选乘数：   2,      1,      1,      1
候选数：    2*4,    7*1,   13*1,    19*1
丑陋数： 1,2,4
```
以此类推。

原理就是丑陋数本身就么有不合法的质因数，所以它就是最天然的乘数。需要注意可能会发生重复的情况比如`2*7 = 7*2`。如果本轮最小候选数和上一个丑陋数重复是，本轮不添加新丑陋数。

#### 代码
```java
class Solution {
     public int nthSuperUglyNumber(int n, int[] primes) {
        int[] uglyNumbers = new int[n]; // 储存结果
        int uglyNumbersP = 0;           // 结果列表的指针
        uglyNumbers[uglyNumbersP++] = 1;
        int[] pointers = new int[primes.length];    // 标识每个素数基元都倍化到哪儿了
        int[] candidates = new int[primes.length];  // 本轮参加竞选的倍化好的基元
        for (int i = 0; i < primes.length; i++) {
            candidates[i] = primes[i];
        }
        while (uglyNumbersP < n) {
            // 取本轮最小值
            int minP = 0; // 指示本轮是第几个候选数当选
            for (int j = 1; j < candidates.length; j++) {
                if (candidates[j] < candidates[minP]) {
                    minP = j;
                }
            }
            // 最小候选数成功当选。注意候选数和上一个丑陋数重复是，本轮不添加新丑陋数。
            if (candidates[minP] > uglyNumbers[uglyNumbersP-1]) {
                uglyNumbers[uglyNumbersP++] = candidates[minP];
            }
            // 更新候选数列表
            candidates[minP] = primes[minP] * (uglyNumbers[++pointers[minP]]);
        }
        return uglyNumbers[n-1];
    }
}
```

#### 结果
![super-ugly-number-2](/images/leetcode/super-ugly-number-2.png)


### 使用`Heap`维护最小候选数

#### 代码

#### 结果
![super-ugly-number-3](/images/leetcode/super-ugly-number-3.png)
