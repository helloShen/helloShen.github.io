---
layout: post
title: "Leetcode - Algorithm - Excel Sheet Column Number "
date: 2017-06-11 22:53:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
Related to question Excel Sheet Column Title

Given a column title as appear in an Excel sheet, return its corresponding column number.

For example:
```
    A -> 1
    B -> 2
    C -> 3
    ...
    Z -> 26
    AA -> 27
    AB -> 28
```

### 迭代版
这题很简单，按照数学公式来就可以了，
> AAA = 1 * 26^2 + 1 * 26^1 + 1 * 26^0

#### 代码
```java
public class Solution {
    public int titleToNumber(String s) {
        int num = 0, length = s.length();
        for (int i = 0; i < length; i++) {
            num += (s.charAt(i)-64) * (int)Math.pow(26,length-1-i);
        }
        return num;
    }
}
```

#### 结果
![excel-sheet-column-number-1](/images/leetcode/excel-sheet-column-number-1.png)


### 递归版

#### 代码
```java
public class Solution {
    public int titleToNumber(String s) {
        return recursion(s,0);
    }
    public int recursion(String s, int pos) {
        if (pos == s.length()) { return 0; }
        return (s.charAt(pos)-64) * (int)Math.pow(26,s.length()-1-pos) + recursion(s,pos+1);
    }
}
```

#### 结果
![excel-sheet-column-number-2](/images/leetcode/excel-sheet-column-number-2.png)
