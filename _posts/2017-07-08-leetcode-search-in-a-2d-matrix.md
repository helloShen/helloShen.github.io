---
layout: post
title: "Leetcode - Algorithm - Search In A 2d Matrix "
date: 2017-07-08 14:45:59
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","divide and conquer","depth first search"]
level: "medium"
description: >
---

### 题目
Write an efficient algorithm that searches for a value in an m x n matrix. This matrix has the following properties:

Integers in each row are sorted in ascending from left to right.
Integers in each column are sorted in ascending from top to bottom.
For example,

Consider the following matrix:
```
[
  [1,   4,  7, 11, 15],
  [2,   5,  8, 12, 19],
  [3,   6,  9, 16, 22],
  [10, 13, 14, 17, 24],
  [18, 21, 23, 26, 30]
]
```
Given target = `5`, return `true`.

Given target = `20`, return `false`.

### DFS
从任何一个点`x`开始，和`target`比较，
* 如果`x > target`，就要么往 **“左”** 走，要么往 **“上”** 走。
* 如果`x < target`，就要么往 **“右”** 走，要么往 **“下”** 走。
* 如果`x = target`，就找到目标数。

按这么走，我们总是在慢慢靠近目标数。相当于做一个 **DFS**，深度优先的探索。

当然，中间需要用一个额外数组，记录走过的路线，已经访问过的点就不必再往下递归了。

复杂度，
* time: $$O(n\log_{}{n})$$
* space: $$O(n)$$

#### 代码
```java
public class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix.length == 0) { return false; }
        int height = matrix.length, width = matrix[0].length;
        int halfHeight = (height - 1) / 2;
        int halfWidth = (width - 1) / 2;
        boolean[][] visited = new boolean[height][width];
        return dfs(matrix,target,halfHeight,halfWidth,visited);
    }
    private boolean dfs(int[][] matrix, int target, int x, int y, boolean[][] visited) {
        if (x < 0 || x == matrix.length || y < 0 || y == matrix[0].length || visited[x][y]) { return false; }
        int val = matrix[x][y];
        visited[x][y] = true;
        if (val > target) {
            return dfs(matrix,target,x-1,y,visited) || dfs(matrix,target,x,y-1,visited);
        } else if (val < target) {
            return dfs(matrix,target,x+1,y,visited) || dfs(matrix,target,x,y+1,visited);
        } else {
            return true;
        }
    }
}
```

#### 结果
![search-in-a-2d-matrix-1](/images/leetcode/search-in-a-2d-matrix-1.png)


### 二分查找
先找到中位行。
```
target = 5

[1,   4,  7, 11, 15]
[2,   5,  8, 12, 19]
[3,   6,  9, 16, 22] <- 中位行
[10, 13, 14, 17, 24]
[18, 21, 23, 26, 30]
```

然后找目标数`5`插入中位行的位置，
```
target = 5

[1,   4,  7, 11, 15]
[2,   5,  8, 12, 19]

 "5" insert here
    |
[3,   6,  9, 16, 22] <- 中位行



[10, 13, 14, 17, 24]
[18, 21, 23, 26, 30]
```

然后以找到的这个点可以把矩阵划为4块。其中有`左上角`和`右下角`的两块可以淘汰。
```
target = 5

  淘汰（都太小）                   保留递归
            [1, |  4,  7, 11, 15]
            [2, |  5,  8, 12, 19]
            ----|-----------------
            [3, |  6,  9, 16, 22]
            [10,| 13, 14, 17, 24]
            [18,| 21, 23, 26, 30]
      保留递归                    淘汰（都太大）
```
保留剩下两块，继续递归。

复杂度，
* time: $$O(\log_{}{n})$$
* space: $$O(1)$$

#### 代码
```java
/** Binary Search */
public class Solution {
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix.length == 0) { return false; }
        return binarySearch(matrix,target,0,matrix.length-1,0,matrix[0].length-1);
    }
    private boolean binarySearch(int[][] matrix, int target, int up, int down, int left, int right) {
        if (up > down || left > right) { return false; }
        int mid = up + (down - up) / 2;
        int index = index(matrix,target,mid,left,right);
        if (index != matrix[0].length && (matrix[mid][index] == target)) {
            return true;
        } else {
            return binarySearch(matrix,target,mid+1,down,left,index-1) || binarySearch(matrix,target,up,mid-1,index,right);
        }
    }
    /** find the index to insert the target number */
    private int index(int[][] matrix, int target, int row, int lo, int hi) {
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int val = matrix[row][mid];
            if (val > target) {
                hi = mid - 1;
            } else if (val < target) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        return lo;
    }
}
```

#### 结果
![search-in-a-2d-matrix-2](/images/leetcode/search-in-a-2d-matrix-2.png)
