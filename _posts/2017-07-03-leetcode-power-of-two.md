---
layout: post
title: "Leetcode - Algorithm - Power Of Two "
date: 2017-07-03 13:05:10
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation","math"]
level: "easy"
description: >
---

### 题目
Given an integer, write a function to determine if it is a power of two.

### 最直观的位操作，一位一位检查
首先得是大于零的正整数。然后二进制数只有一位是`1`。

#### 代码
```java
public class Solution {
    public boolean isPowerOfTwo(int n) {
        if (n <= 0) { return false; }
        while ((n & 1) == 0) { n >>>= 1; } // eliminate 0s in the end
        n >>>= 1; // remove the first 1
        return n == 0;
    }
}
```

#### 结果
![power-of-two-1](/images/leetcode/power-of-two-1.png)


### 利用`Integer.bitCount()`函数
`Integer.bitCount()`就是用来计算`1`位的个数。

#### 代码
```java
public class Solution {
    public boolean isPowerOfTwo(int n) {
        return (n > 0) && (Integer.bitCount(n) == 1);
    }
}
```

#### 结果
![power-of-two-2](/images/leetcode/power-of-two-2.png)


### 小聪明做法，检查 `n & (n-1)` 是否等于`0`
基于`n & (n-1)`的特性，如下所示，
```
n:      0010 0000
n-1:    0001 1111       &
        -----------------
        0000 0000
```


#### 代码
```java
public class Solution {
    public boolean isPowerOfTwo(int n) {
        return (n > 0) && ( (n & (n-1)) == 0 );
    }
}
```

#### 结果
![power-of-two-3](/images/leetcode/power-of-two-3.png)
