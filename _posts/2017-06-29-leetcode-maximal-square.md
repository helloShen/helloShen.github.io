---
layout: post
title: "Leetcode - Algorithm - Maximal Square "
date: 2017-06-29 14:44:49
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","dynamic programming"]
level: "medium"
description: >
---

### 主要收获
之前考虑动态规划，分解子问题的时候，子问题和总体的问题总是保持一致。其实这不是必须的。
> 动态规划维护的内容可以是任何信息。哪怕只是得到最终结果的辅助信息。

一路维护推广所需要的辅助信息，也能帮助得到最终结果。

### 主要思路
首先，暴力能解决问题吗？能！

然后看，暴力解里，有没有重复操作？有没有很多问题重复检查很多遍？ 有！

遇到矩阵的问题，别忘了从动态规划这条路上去想想。往往有奇效。

### 题目
Given a 2D binary matrix filled with 0's and 1's, find the largest square containing only 1's and return its area.

For example, given the following matrix:
```
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0
```
Return `4`.

### 暴力解法
检查以矩阵中的每个点为起点，最大的全`1`方块有多大。检查全`1`方块的时候，一层层往外推，遇到`0`就终止。复杂度 $$O(n^2 * m^2)$$。

比如，先从最左上角的`1`开始，
```
1 0 1 0 0
1 0 1 1 1
1 1 1 1 1
1 0 0 1 0
```
检查的时候，先看
```
1
```

再看
```
1 0
1 0
```
遇到`0`就终止。

当剩下的区域不可能产生大于最大方块的时候，也终止迭代。

看上去复杂度很高，但因为有各种终止条件，实际运行效率挺高的。

#### 代码
```java
/** Brute Force */
public class Solution {
    public int maximalSquare(char[][] matrix) {
        int result = 0;
        if (matrix.length == 0) { return result; }
        int height = matrix.length, width = matrix[0].length;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int maxPossibleHeight = height -i;
                if (maxPossibleHeight <= result) { return result * result; } // 剪枝
                int maxPossibleWidth = width - j;
                if (maxPossibleWidth <= result) { break; } // 剪枝
                int range = Math.min(maxPossibleHeight,maxPossibleWidth);
                result = Math.max(result,checkSquare(matrix,i,j,range));
            }
        }
        return result * result;
    }
    /** Check the max size of Square start from a certain point (left-upper corner) [x,y]  */
    public int checkSquare(char[][] matrix, int x, int y, int range) {
        int result = 0;
        for (int i = 0; i < range; i++) {
            for (int j = x + i, k = y; k <= y + i; k++) { if (matrix[j][k] == '0') { return result; } }
            for (int j = y + i, k = x; k <= x + i; k++) { if (matrix[k][j] == '0') { return result; } }
            result++;
        }
        return result;
    }
}
```

#### 结果
![maximal-square-1](/images/leetcode/maximal-square-1.png)


### 动态规划
标准的动态规划问题。只不过分解子问题的时候，需要给问题换一个角度。
> 分解子问题的时候，记录的不仅仅是当前的最大深度，而是所有`1`方块的深度。方便上层问题的扩展。

比如，如下矩阵。**对每个`1`点，取它“楼上点”，“左边点”，“对角点”深度的最小值，再加1。**
```
0	0	0	1
1	1	0	1
1	1	1	1
0	1	1	1
0	1	1	1
```

1层
```
0
```
2层
```
0	0
1	1
```
3层
```
0	0	0
1	1	0
1	2	1
```
4层
```
0	0	0	1
1	1	0	1
1	2	1	1
0	1	2	2
```
5层
```
0	0	0	1
1	1	0	1
1	2	1	1
0	1	2	2
0	1	2	3
```

实际操作的时候，为了简化判断条件，可以预先在顶上和左边各增加一行`0`。
```
0|  0   0   0   0
-----------------
0|  0   0	0	1
0|  1	1	0	1
0|  1	2	1	1
0|  0	1	2	2
0|  0	1	2	3
```

#### 代码
```java
public class Solution {
    public int maximalSquare(char[][] matrix) {
        int size = 0;
        if (matrix.length == 0 || matrix[0].length == 0) { return size * size; }
        int height = matrix.length, width = matrix[0].length;
        int[][] dpTable = new int[height+1][width+1];
        for (int i = 1; i < dpTable.length; i++) {
            for (int j = 1; j < dpTable[0].length; j++) {
                if (matrix[i-1][j-1] == '1') {
                    int dpVal = Math.min(Math.min(dpTable[i-1][j],dpTable[i][j-1]),dpTable[i-1][j-1]) + 1;
                    dpTable[i][j] = dpVal;
                    size = Math.max(size,dpVal);
                }
            }
        }
        return size * size;
    }
}
```

#### 结果
![maximal-square-2](/images/leetcode/maximal-square-2.png)


### 动态规划，空间缩小至 $$O(n)$$
不用始终维护整个表。只用得到最近的两行。

#### 代码
```java
public class Solution {
    public int maximalSquare(char[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0) { return 0; }
        int max = 0, height = matrix.length, width = matrix[0].length;
        int[] dpHelper = new int[width+1];
        for (int i = 0; i < height; i++) {
            int[] localHelper = new int[width+1];
            for (int j = 0; j < width; j++) {
                if (matrix[i][j] == '1') {
                    int dpVal = Math.min(localHelper[j],Math.min(dpHelper[j],dpHelper[j+1])) + 1;
                    localHelper[j+1] = dpVal;
                    max = Math.max(max,dpVal);
                }
            }
            dpHelper = localHelper;
        }
        return max * max;
    }
}
```

#### 结果
![maximal-square-3](/images/leetcode/maximal-square-3.png)
