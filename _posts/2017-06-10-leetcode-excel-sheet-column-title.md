---
layout: post
title: "Leetcode - Algorithm - Excel Sheet Column Title "
date: 2017-06-10 21:27:30
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["easy"]
level: "math"
description: >
---

### 题目
Given a positive integer, return its corresponding column title as appear in an Excel sheet.

For example:
```
    1 -> A
    2 -> B
    3 -> C
    ...
    26 -> Z
    27 -> AA
    28 -> AB
```

### 找到进位的阈值
首先这不能当做传统的`26`进制处理。因为这里没有`0`的概念。
```
A   =                              26^0  =   1
AA  =                   26^1     + 26^0  =   27
AAA =           26^2 +  26^1     + 26^0  =   703
AAAA= 26^3  +   26^2 +  26^1     + 26^0  =   18279
...
```

这个方法先算出总共有多少位，比如，`18000 < 18279`，所以最多3位数。然后依次算出每一位的数字。


#### 代码
```java
public class Solution {
    public String convertToTitle(int n) {
        StringBuilder res = new StringBuilder();
        if (n < 1) { return res.toString(); }
        // find the highest level
        long nl = (long)n;
        int lev = 0;
        long thredshold = 0;
        while (true) {
            long preThreshold = thredshold;
            thredshold += (long)Math.pow(26,lev);
            if (nl > thredshold) {
                lev++;
            } else if (nl < thredshold) {
                lev--; thredshold = preThreshold; break;
            } else { // nl == thredshold
                break;
            }
        }
        // fill each level
        int remainder = n - (int)thredshold; // here thredshold must < Integer.MAX_VALUE;
        for (int i = lev; i >=0; i--) {
            int denominator = (int)Math.pow(26,i);
            char letter = (char)(remainder / denominator + 1 + 64); // 'A'=65
            remainder = remainder % denominator;
            res.append(letter);
        }
        return res.toString();
    }
}
```

#### 结果
![excel-sheet-column-title-1](/images/leetcode/excel-sheet-column-title-1.png)


### 向前借位法
这个过程和普通26进制的区别就是每位不可能是`0`，至少是`1`。所以就每位先向前借`1`，在求`%26`的余数。

#### 代码

##### 迭代版
```java
public class Solution {
    public String convertToTitle(int n) {
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            char letter = (char)((n - 1) % 26 + 'A');
            sb.insert(0,letter);
            n = (n - 1) / 26;
        }
        return sb.toString();
    }
}
```

##### 递归版
递归版异常非常简洁。只有一行。
```java
public class Solution {
    public String convertToTitle(int n) {
        return (n == 0)? "" : convertToTitle((n-1)/26) + (char)((n-1)%26 + 'A');
    }
}
```

#### 结果
![excel-sheet-column-title-2](/images/leetcode/excel-sheet-column-title-2.png)
