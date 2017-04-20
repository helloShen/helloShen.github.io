---
layout: post
title: "Leetcode - Algorithm - Add Binary "
date: 2017-04-20 17:56:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","string"]
level: "easy"
description: >
---

### 题目

### 老老实实一位一位做加法 $$O(n)$$
不敢用`StringBuilder`，肯定慢。只好用`char[]`。

#### 代码
```java
public class Solution {
    public static String addBinary(String a, String b) {
        char[] charA = a.toCharArray();
        char[] charB = b.toCharArray();
        char[] res = new char[Math.max(charA.length,charB.length)+1];
        int carry = 0;
        for (int i = charA.length-1, j = charB.length-1, cursor = res.length-1; i >=0 || j >= 0; ) {
            int numA = (i >= 0)? (charA[i--]-'0'):0;
            int numB = (j >= 0)? (charB[j--]-'0'):0;
            int sum = numA + numB + carry;
            res[cursor--] = (char)((sum % 2) + '0');
            carry = sum / 2;
        }
        res[0] = (char)(carry + '0');
        int firstOne = -1;
        for (int i = 0; i < res.length; i++) {
            if (res[i] == '1') { firstOne = i; break; }
        }
        if (firstOne == -1) { firstOne = res.length-1; }
        return new String(Arrays.copyOfRange(res,firstOne,res.length));
    }
}
```

#### 结果
![add-binary-1](/images/leetcode/add-binary-1.png)
