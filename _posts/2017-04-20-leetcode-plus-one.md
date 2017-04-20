---
layout: post
title: "Leetcode - Algorithm - Plus One "
date: 2017-04-20 15:29:10
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","array"]
level: "easy"
description: >
---

### 题目
Given a non-negative integer represented as a non-empty array of digits, plus one to the integer.

You may assume the integer do not contain any leading zero, except the number 0 itself.

The digits are stored such that the most significant digit is at the head of the list.

### 老老实实遍历数组
这题就是老老实实一位一位加，进位。唯一需要增加数组长度的情况是`99+1=100`这样。

#### 第一版代码
```java
public class Solution {
    public int[] plusOne(int[] digits) {
        int carry = 1;
        for (int i = digits.length-1; i >= 0 && carry == 1; i--) {
            int sum = digits[i] + carry;
            digits[i] = sum % 10;
            carry = sum / 10;
        }
        if (carry == 1) { // 唯一需要增加数组长度的情况
            digits = new int[digits.length+1];
            digits[0] = 1;
        }
        return digits;
    }
}
```

#### 理清思路，优化版
```java
public int[] plusOne(int[] digits) {
    for (int i = digits.length-1; i >=0; i--) {
        if (digits[i] < 9) {
            digits[i]++; break;
        } else {
            digits[i] = 0;
        }
    }
    if (digits[0] == 0) {
        digits = new int[digits.length+1];
        digits[0] = 1;
    }
    return digits;
}
```


#### 结果
![plus-one-1](/images/leetcode/plus-one-1.png)
