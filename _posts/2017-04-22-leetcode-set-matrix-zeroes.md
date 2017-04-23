---
layout: post
title: "Leetcode - Algorithm - Set Matrix Zeroes "
date: 2017-04-22 20:06:35
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given a m x n matrix, if an element is 0, set its entire row and column to 0. Do it in place.

click to show follow up.

Follow up:
Did you use extra space?
A straight forward solution using O(mn) space is probably a bad idea.
A simple improvement uses O(m + n) space, but still not the best solution.
Could you devise a constant space solution?

### 先记下要变零的行号，列号，$$O(m+n)$$ space


#### 代码
```java
public class Solution {
    public void setZeroes(int[][] matrix) {
        int lineSize = matrix.length;
        if (lineSize == 0) { return; }
        int columnSize = matrix[0].length;
        List<Integer> zeroLine = new ArrayList<>();
        List<Integer> zeroColumn = new ArrayList<>();
        for (int i = 0; i < lineSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                if (matrix[i][j] == 0) {
                    zeroLine.add(i);
                    zeroColumn.add(j);
                }
            }
        }
        for (int num : zeroLine) {
            for (int i = 0; i < columnSize; i++) {
                matrix[num][i] = 0;
            }
        }
        for (int num : zeroColumn) {
            for (int i = 0; i < lineSize; i++) {
                matrix[i][num] = 0;
            }
        }
    }
}
```

#### 结果
![set-matrix-zeroes-1](/images/leetcode/set-matrix-zeroes-1.png)


### 用第一行和第一列记录需要变0的行列信息. $$O(1)$$ space
一下面矩阵为例，
```
1	0	3	4	5

1	2	3	4	5

6	7	8	9	0

1	3	5	7	9
```
遍历每个数，如果遇到0，就在第一行和第一列留下`0`的记号。标记完，变成下面这样。注意，第一行遇到`0`不在`[0,0]`的位置留下标记，因为有歧义，会被认为是第一列应该全变0.
```
// 注意，第一行有0，不在第一列标记。用一个boolean firstLineShouldFillZero标记

1	0	3	4	0  // firstLineShouldFillZero = true

1	2	3	4	5

0	7	8	9	0

1	3	5	7	9
```
完了之后，再根据第一行，第一列的信息把该全变0的行和列全改写成0. 最后再根据`boolean firstLineShouldFillZero`的值，来决定第一行要不要全变成0.

#### 代码
```java
public void setZeroes(int[][] matrix) {
    int lineSize = matrix.length;
    if (lineSize == 0) { return; }
    int columnSize = matrix[0].length;
    boolean firstLineShouldFillZero = false;
    for (int i = 0; i < lineSize; i++) {
        for (int j = 0; j < columnSize; j++) {
            if (matrix[i][j] == 0) { // 标记，此行回头应该整行变0.
                if (i == 0) {
                    firstLineShouldFillZero = true; // 第一行比较特殊，用firstLineShouldFillZero来标记
                } else {
                    matrix[i][0] = 0;
                }
                matrix[0][j] = 0; // 标记，当全部遍历完，回过头把整个这一列变0.
            }
        }
    }
    for (int i = 1; i < lineSize; i++) { // 根据第一列，把需要变0的行全变0。跳过第一行
        if (matrix[i][0] == 0) {
            for (int j = 0; j < columnSize; j++) {
                matrix[i][j] = 0;
            }
        }
    }
    for (int i = 0; i < columnSize; i++) { // 根据第一行，把需要变0的列，全变0.
        if (matrix[0][i] == 0) {
            for (int j = 0; j < lineSize; j++) {
                matrix[j][i] = 0;
            }
        }
    }
    if (firstLineShouldFillZero) { // 根据firstLineShouldFillZero决定第一行要不要全变0
        for (int i = 0; i < columnSize; i++) {
            matrix[0][i] = 0;
        }
    }
}
```

#### 代码整理
索性用两个`boolean`。`firstLineShouldFillZero`标记第一行是否为0. `firstColumnShouldFillZero`标记第一列是否为0. 代码也经过合并优化。

```java
public class Solution {
    public void setZeroes(int[][] matrix) {
        if (matrix.length == 0) { return; }
        int lineSize = matrix.length, columnSize = matrix[0].length;
        boolean firstLineShouldFillZero = false, firstColumnShouldFillZero = false;
        for (int i = 0; i < lineSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                if (matrix[i][j] == 0) {
                    if (i == 0) { firstLineShouldFillZero = true; }
                    if (j == 0) { firstColumnShouldFillZero = true; }
                    matrix[0][j] = 0;
                    matrix[i][0] = 0;
                }
            }
        }
        for (int i = 1; i < lineSize; i++) { // 根据第一行，第一列，把需要变0的行全变0。跳过第一行，第一列
            for (int j = 1; j < columnSize; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }
        if (firstLineShouldFillZero) { // 根据firstLineShouldFillZero决定第一行要不要全变0
            for (int i = 0; i < columnSize; i++) {
                matrix[0][i] = 0;
            }
        }
        if (firstColumnShouldFillZero) { // 根据firstColumnShouldFillZero决定第一列要不要全变0
            for (int i = 0; i < lineSize; i++) {
                matrix[i][0] = 0;
            }
        }
    }
}
```

#### 结果
![set-matrix-zeroes-2](/images/leetcode/set-matrix-zeroes-2.png)
