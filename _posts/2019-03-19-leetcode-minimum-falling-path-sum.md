---
layout: post
title: "Leetcode - Algorithm - Minimum Falling Path Sum "
date: 2019-03-19 13:08:43
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
Given a square array of integers A, we want the minimum sum of a falling path through A.

A falling path starts at any element in the first row, and chooses one element from each row.  The next row's choice must be in a column that is different from the previous row's column by at most one.

Example 1:
```
Input: [[1,2,3],[4,5,6],[7,8,9]]
Output: 12
Explanation:
The possible falling paths are:
[1,4,7], [1,4,8], [1,5,7], [1,5,8], [1,5,9]
[2,4,7], [2,4,8], [2,5,7], [2,5,8], [2,5,9], [2,6,8], [2,6,9]
[3,5,7], [3,5,8], [3,5,9], [3,6,8], [3,6,9]
The falling path with the smallest sum is [1,4,7], so the answer is 12.
```

Note:
* 1 <= A.length == A[0].length <= 100
* -100 <= A[i][j] <= 100

### 典型的动态规划问题
考虑题目给出的例子，
```
1,  2,  3
4,  5,  6
7,  8,  9
```

对于`5`这个点，有且只有3种可能的路径，
```
1,  2,  3
  \ | /
    5
```

所以，到达`5`这个点，最短路径是确定的`1 + 5 = 6`。而到达`8`这个点，又取决于到达`4`,`5`,`6`这3个点的最短路径，
```
f(4),   f(5),   f(6)
    \    |    /
         8
```

所以这是一个典型的动态规划问题，我们可以逐行累计出一个到达每一点的最短路径。
```
1,  2,  3
5,  6,  8
12, 12, 15
```

#### 代码
```java
class Solution {
    public int minFallingPathSum(int[][] A) {
        int height = A.length, width = A[0].length;
        int[] dpRow = Arrays.copyOf(A[0], width);
        for (int i = 1; i < height; i++) {
            int[] nextRow = new int[width];
            for (int j = 0; j < width; j++) {
                nextRow[j] = dpRow[j] + A[i][j];
                if (j > 0) nextRow[j] = Math.min(nextRow[j], dpRow[j - 1] + A[i][j]);
                if (j < width - 1) nextRow[j] = Math.min(nextRow[j], dpRow[j + 1] + A[i][j]);
            }
            dpRow = nextRow;
        }
        int res = 10001;
        for (int n : dpRow) res = Math.min(res, n);
        return res;
    }
}
```

#### 结果
![minimum-falling-path-sum-1](/images/leetcode/minimum-falling-path-sum-1.png)
