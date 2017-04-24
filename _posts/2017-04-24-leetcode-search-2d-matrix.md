---
layout: post
title: "Leetcode - Algorithm - Search 2d Matrix "
date: 2017-04-24 00:12:38
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search"]
level: "medium"
description: >
---

### 题目
Write an efficient algorithm that searches for a value in an m x n matrix. This matrix has the following properties:

* Integers in each row are sorted from left to right.
* The first integer of each row is greater than the last integer of the previous row.
For example,

Consider the following matrix:
```
[
  [1,   3,  5,  7],
  [10, 11, 16, 20],
  [23, 30, 34, 50]
]
```
Given target = `3`, return `true`.

### 虚拟二分查找
本质上这是一个标准的二分查找。 特殊的地方在于需要在数组上做。

比较笨的办法是真的把上界，下界，中位数全用二维坐标表示。缺点是二维坐标的前进，后退，取中位数，判断交不交叉的动作都比较复杂。需要判断比较复杂的边界约束。

比较聪明的办法是 **把整个过程都当成在虚拟的数轴上操作**。只是在真的要取数字来比较的时候，再把数轴中的虚拟位置，转换成二维数组中的绝对位置。

#### 代码
```java
public class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix.length == 0) { return false; }
        int rowSize = matrix.length, colSize = matrix[0].length;
        int lo = 0, hi = (rowSize * colSize) - 1; // 转换成虚拟数轴中的位置
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int num = matrix[mid/colSize][mid%colSize]; // 虚拟数轴转换成矩阵中的绝对位置，取数字
            if (target < num) {
                hi = mid - 1;
            } else if (target > num) {
                lo = mid + 1;
            } else { // target == num
                return true;
            }
        }
        return false;
    }
}
```

#### 结果
![search-2d-matrix-1](/images/leetcode/search-2d-matrix-1.png)
