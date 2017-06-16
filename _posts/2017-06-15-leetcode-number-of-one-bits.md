---
layout: post
title: "Leetcode - Algorithm - Number Of One Bits "
date: 2017-06-15 18:06:07
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation"]
level: "easy"
description: >
---

### 题目
Write a function that takes an unsigned integer and returns the number of `1` bits it has (also known as the Hamming weight).

For example, the 32-bit integer `11` has binary representation `00000000000000000000000000001011`, so the function should return `3`.

### 用`1`掩码切割最低位
时间复杂度 $$O(n)$$，`n` is the length of int.

#### 代码
```java
public class Solution {
    // you need to treat n as an unsigned value
    public int hammingWeight(int n) {
        int count = 0;
        for (int i = 0; i < 32; i++) {
            count += (n & 1);
            n >>= 1;
        }
        return count;
    }
}
```

#### 结果
![number-of-one-bits-1](/images/leetcode/number-of-one-bits-1.png)
