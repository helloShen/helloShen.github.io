---
layout: post
title: "Leetcode - Algorithm - Spiral Matrix "
date: 2017-04-14 22:41:27
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given a matrix of m x n elements (m rows, n columns), return all elements of the matrix in spiral order.

For example,
Given the following matrix:
```
[
 [ 1, 2, 3 ],
 [ 4, 5, 6 ],
 [ 7, 8, 9 ]
]
```
You should return `[1,2,3,6,9,8,7,4,5]`.

### 还是剥洋葱

一个二维数组，可以一层层剥出多个同心圆，
```
1	2	3	4

5			8               6	7

9			12              10	11

13	14	15	16
```
然后只需要在知道方形圈四个角坐标的情况下，顺时针绕圈遍历数字。

#### 代码
```java
public class Solution {
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> res = new ArrayList<>();
        if (matrix.length == 0 || matrix[0].length == 0) { return res; }
        int high = 0, low = matrix.length-1;
        int left = 0, right = matrix[0].length-1;
        while (low - high >= 0 && right - left >= 0) {
            parseCircle(matrix,high++,low--,left++,right--,res);
        }
        return res;
    }
    public void parseCircle(int[][] matrix, int high, int low, int left, int right, List<Integer> res) {
        for (int i = left; i < right; i++) {
            res.add(matrix[high][i]);
        }
        for (int i = high; i < low; i++) {
            res.add(matrix[i][right]);
        }
        if (low - high > 0) {
            for (int i = right; i > left; i--) {
                res.add(matrix[low][i]);
            }
        } else { // 横一字长条
            res.add(matrix[high][right]);
        }
        if (right - left > 0) {
            for (int i = low; i > high; i--) {
                res.add(matrix[i][left]);
            }
        }  else if (low - high > 0){ // 纵向一字长条，且不是单个点
            res.add(matrix[low][left]);
        }
    }
}
```

#### 结果
还不错。银弹。不需要更多解法了。
![spiral-matrix-1](/images/leetcode/spiral-matrix-1.png)
