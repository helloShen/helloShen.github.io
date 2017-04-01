---
layout: post
title: "Leetcode - Algorithm - Zig-Zag Conversion"
date: 2017-03-20 17:59:06
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "medium"
description: >
---

### 题目
The string "PAYPALISHIRING" is written in a zigzag pattern on a given number of rows like this: (you may want to display this pattern in a fixed font for better legibility)
```
P   A   H   N
A P L S I I G
Y   I   R
```
And then read line by line: `PAHNAPLSIIGYIR`
Write the code that will take a string and make this conversion given a number of rows:

string convert(string text, int nRows);
convert("PAYPALISHIRING", 3) should return "PAHNAPLSIIGYIR".

### 朴素二维数组法
最简单的方法，就是把所有字符存到一个`length * numRows`大小的二维数组。用一个简单的`boolean`做flag,控制在二维数组中上下移动。当`numRows`为`1`时，可以直接返回原字符串。

#### 代码
```java
public class Solution {
    public String convert(String s, int numRows) {
        if (numRows == 1) { return s; }
        char[] chars = s.toCharArray();
        char[][] matrix = new char[numRows][chars.length];
        int[] cursors = new int[numRows];
        boolean goesUp = false; // 控制掉头，0=down, 1=up
        for (int i = 0, row = 0; i < chars.length; i++){
            matrix[row][cursors[row]++] = chars[i];
            // 掉头
            if (row == 0) { goesUp = false; }
            if (row == numRows-1) { goesUp = true; }
            // 跑
            row = (goesUp)? row-1 : row+1;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numRows; i++) {
            sb.append(matrix[i],0,cursors[i]);
        }
        return sb.toString();
    }
}
```

#### 结果
结果已经不差。这题优化的空间不大。
![zig-zag-1](/images/leetcode/zigzag-conversion-1.png)
