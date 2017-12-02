---
layout: post
title: "Leetcode - Algorithm - Add Strings "
date: 2017-12-02 15:54:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string","two pointers"]
level: "easy"
description: >
---

### 题目
Given two non-negative integers num1 and num2 represented as string, return the sum of num1 and num2.

Note:
1. The length of both num1 and num2 is < 5100.
2. Both num1 and num2 contains only digits 0-9.
3. Both num1 and num2 does not contain any leading zero.
4. You must not use any built-in BigInteger library or convert the inputs to integer directly.

### 用两个指针从右向左遍历字符串

#### 代码
```java
class Solution {
    public String addStrings(String num1, String num2) {
        if (num1.length() == 0 || num2.length() == 0) { return ""; }
        StringBuilder sb = new StringBuilder();
        int cur1 = num1.length() - 1;
        int cur2 = num2.length() - 1;
        int a = 0, b = 0, sum = 0, carry = 0;
        while (cur1 >= 0 || cur2 >= 0) {
            a = 0; b = 0;
            if (cur1 >= 0) { a = num1.charAt(cur1--) - '0'; }
            if (cur2 >= 0) { b = num2.charAt(cur2--) - '0'; }
            sum = a + b + carry;
            carry = sum / 10;
            sb.insert(0,sum % 10);
        }
        if (carry == 1) { sb.insert(0,'1'); }
        return sb.toString();
    }
}
```

#### 结果
![add-strings-1](/images/leetcode/add-strings-1.png)
