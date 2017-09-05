---
layout: post
title: "Leetcode - Algorithm - Number Complement "
date: 2017-09-05 17:01:46
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation"]
level: "easy"
description: >
---

### 题目
Given a positive integer, output its complement number. The complement strategy is to flip the bits of its binary representation.

Note:
The given integer is guaranteed to fit within the range of a 32-bit signed integer.
You could assume no leading zero bit in the integer’s binary representation.
Example 1:
```
Input: 5
Output: 2
```
Explanation: The binary representation of 5 is 101 (no leading zero bits), and its complement is 010. So you need to output 2.

Example 2:
```
Input: 1
Output: 0
```
Explanation: The binary representation of 1 is 1 (no leading zero bits), and its complement is 0. So you need to output 0.

### 位操作
首先需要知道，
> 和`1`做XOR异或操作可以取反。

```
0011 0101
0011 1111 // 全1掩码
-------------------
0000 1010
```
所以要做两件事，
1. 找到数字的最高`1`位。
2. 构造这个掩码。


#### 代码
```java
class Solution {
    private final int INT_SIZE = 32;
    public int findComplement(int num) {
        int len = 0, copy = num;
        while (copy != 0) { copy >>>= 1; ++len; }   // 找num的最高位在哪儿
        int mask = (~0) >>> (INT_SIZE - len);       // 构造掩码00000111，右边直到num的最高位的位置，全是1.
        return (num ^ mask);                        // XOR异或取反
    }
}
```

#### 用`Integer#highestOneBit()`构造掩码
当然方法有很多，比如`Integer#highestOneBit()`可以找到最高位，
```java
class Solution {
    public int findComplement(int num) {
        int mask = (Integer.highestOneBit(num) << 1) - 1; // 构造00000111掩码
        return num ^ mask;
    }
}
```

#### 结果
![number-complement-1](/images/leetcode/number-complement-1.png)
