---
layout: post
title: "Leetcode - Algorithm - Range Sum Query 2d "
date: 2017-11-25 20:48:24
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","math"]
level: "medium"
description: >
---

### 题目
Given a 2D matrix matrix, find the sum of the elements inside the rectangle defined by its upper left corner (row1, col1) and lower right corner (row2, col2).

![range-sum-query-2d](/images/leetcode/range-sum-query-2d.png)

The above rectangle (with the red border) is defined by (row1, col1) = (2, 1) and (row2, col2) = (4, 3), which contains sum = 8.

Example:
```
Given matrix = [
  [3, 0, 1, 4, 2],
  [5, 6, 3, 2, 1],
  [1, 2, 0, 1, 5],
  [4, 1, 0, 1, 7],
  [1, 0, 3, 0, 5]
]

sumRegion(2, 1, 4, 3) -> 8
sumRegion(1, 1, 2, 2) -> 11
sumRegion(1, 2, 2, 4) -> 12
```

Note:
* You may assume that the matrix does not change.
* There are many calls to sumRegion function.
* You may assume that row1 ≤ row2 and col1 ≤ col2.

### 缓存所有求和的结果
拿到一个矩阵，
```
[3, 0, 1, 4, 2]
[5, 6, 3, 2, 1]
[1, 2, 0, 1, 5]
[4, 1, 0, 1, 7]
[1, 0, 3, 0, 5]
```

预先把从`[0,0]`到`[x,y]`区域的和求出来，存在

#### 代码
```java
class NumMatrix {

    private static int[][] sumTable;

    public NumMatrix(int[][] matrix) {
        if (matrix.length == 0) { sumTable = new int[1][1]; return; }
        sumTable = new int[matrix.length+1][matrix[0].length+1];
        for (int i = 1; i < sumTable.length; i++) {
            for (int j = 1; j < sumTable[0].length; j++) {
                sumTable[i][j] = sumTable[i-1][j] + sumTable[i][j-1] - sumTable[i-1][j-1] + matrix[i-1][j-1];
            }
        }
    }

    public int sumRegion(int row1, int col1, int row2, int col2) {
        return sumTable[row2+1][col2+1] - sumTable[row1][col2+1] - sumTable[row2+1][col1] + sumTable[row1][col1];
    }
}

/**
 * Your NumMatrix object will be instantiated and called as such:
 * NumMatrix obj = new NumMatrix(matrix);
 * int param_1 = obj.sumRegion(row1,col1,row2,col2);
 */
```

#### 结果
![range-sum-query-2d-1](/images/leetcode/range-sum-query-2d-1.png)
