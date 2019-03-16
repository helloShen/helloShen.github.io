---
layout: post
title: "Leetcode - Algorithm - Longest Consecutive One In Matrix "
date: 2019-03-16 12:59:02
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "dynamic programming"]
level: "medium"
description: >
---

### 题目
Given a 01 matrix `M`, find the longest line of consecutive one in the matrix. The line could be horizontal, vertical, diagonal or anti-diagonal.
Example:
```
Input:
[[0,1,1,0],
 [0,1,1,0],
 [0,0,0,1]]
Output: 3
```

Hint: The number of elements in the given matrix will not exceed 10,000.

### 遍历整个矩阵
最朴素的想法就是，从矩阵中的每个点出发，计算“横向”，“纵向”，“斜向”，“反斜向”，四个方向的连续的1。

但这样可能涉及重复计算，比如下面的例子，从首行第一个`1`出发，计算横向有`2`个连续的`1`之后，再从首行第二个`1`出发计算横向连续的1，就会重复计算。
```
从这点出发计算横向连续的1
   |
[0,1,1,0],
[0,1,1,0],
[0,0,0,1]
```

最简单的解决方法是用一个表格，记录之前每个点上，四个方向的连续1是否已经计算过。

#### 代码
```java
public int longestLine(int[][] M) {
    int max = 0;
    int height = M.length;
    if (height == 0) return max;
    int width = M[0].length;
    int[][][] record = new int[height][width][4];
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (M[i][j] == 1) {
                if (record[i][j][0] == 0) { // horizontal
                    int end = j + 1;
                    while (end < width && M[i][end] == 1) {
                        record[i][end][0] = 1;
                        end++;
                    }
                    max = Math.max(max, end - j);
                }
                if (record[i][j][1] == 0) { // vertical
                    int end = i + 1;
                    while (end < height && M[end][j] == 1) {
                        record[end][j][1] = 1;
                        end++;
                    }
                    max = Math.max(max, end - i);
                }
                if (record[i][j][2] == 0) { // diagonal
                    int rowEnd = i + 1, colEnd = j + 1;
                    while (colEnd < width && rowEnd < height && M[rowEnd][colEnd] == 1) {
                        record[rowEnd][colEnd][2] = 1;
                        rowEnd++; colEnd++;
                    }
                    max = Math.max(max, rowEnd - i);
                }
                if (record[i][j][3] == 0) { // anti-diagonal
                    int rowEnd = i + 1, colEnd = j - 1;
                    while (colEnd >= 0 && rowEnd < height && M[rowEnd][colEnd] == 1) {
                        record[rowEnd][colEnd][3] = 1;
                        rowEnd++; colEnd--;
                    }
                    max = Math.max(max, rowEnd - i);
                }
            }
        }
    }
    return max;
}
```

#### 结果
![longest-consecutive-one-in-matrix-1](/images/leetcode/longest-consecutive-one-in-matrix-1.png)


### 动态规划思想
如果使用动态规划的思想，代码变动很小。

#### 代码
```java
public int longestLine(int[][] M) {
    int max = 0;
    int height = M.length;
    if (height == 0) return max;
    int width = M[0].length;
    int[][][] dp = new int[height][width][4];
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            if (M[i][j] == 1) {
                dp[i][j][0] = (j > 0)? dp[i][j - 1][0] + 1 : 1;    // horizontal
                max = Math.max(max, dp[i][j][0]);
                dp[i][j][1] = (i > 0)? dp[i - 1][j][1] + 1 : 1;    // vertical
                max = Math.max(max, dp[i][j][1]);
                dp[i][j][2] = (i > 0 && j > 0)? dp[i - 1][j - 1][2] + 1 : 1;    // diagonal
                max = Math.max(max, dp[i][j][2]);
                dp[i][j][3] = (i > 0 && j + 1 < width)? dp[i - 1][j + 1][3] + 1 : 1;    // anti-diagonal
                max = Math.max(max, dp[i][j][3]);
            } else {
                dp[i][j][0] = 0;
                dp[i][j][1] = 0;
                dp[i][j][2] = 0;
                dp[i][j][3] = 0;
            }
        }
    }
    return max;
}
```

只记录前一行数据，
```java
public int longestLine(int[][] M) {
    int max = 0;
    int height = M.length;
    if (height == 0) return max;
    int width = M[0].length;
    int[][] pre = new int[width][4];
    for (int i = 0; i < height; i++) {
        int[][] curr = new int[width][4];
        for (int j = 0; j < width; j++) {
            if (M[i][j] == 1) {
                curr[j][0] = (j > 0)? curr[j - 1][0] + 1 : 1;    // horizontal
                max = Math.max(max, curr[j][0]);
                curr[j][1] = (i > 0)? pre[j][1] + 1 : 1;    // vertical
                max = Math.max(max, curr[j][1]);
                curr[j][2] = (i > 0 && j > 0)? pre[j - 1][2] + 1 : 1;    // diagonal
                max = Math.max(max, curr[j][2]);
                curr[j][3] = (i > 0 && j + 1 < width)? pre[j + 1][3] + 1 : 1;    // anti-diagonal
                max = Math.max(max, curr[j][3]);
            } else {
                curr[j][0] = 0;
                curr[j][1] = 0;
                curr[j][2] = 0;
                curr[j][3] = 0;
            }
        }
        pre = curr;
    }
    return max;
}
```

#### 结果
![longest-consecutive-one-in-matrix-2](/images/leetcode/longest-consecutive-one-in-matrix-2.png)
