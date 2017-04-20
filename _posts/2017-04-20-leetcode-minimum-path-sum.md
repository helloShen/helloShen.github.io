---
layout: post
title: "Leetcode - Algorithm - Minimum Path Sum "
date: 2017-04-20 15:13:39
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","array"]
level: "medium"
description: >
---

### 题目
Given a m x n grid filled with non-negative numbers, find a path from top left to bottom right which minimizes the sum of all numbers along its path.

Note: You can only move either down or right at any point in time.

### 自底向上的动态规划 $$O(mn)$$
有过`Unique Path Two`的经验，就知道这种题用`自底向上`的动态规划最合适。每个点到终点的最短路线，都取决于自己的长度，以及它的`右前置点`和`下前置点`的最短距离。取`右前置点`和`下前置点`中较短者作为它的前置点。

这里用到一个小技巧，就是预先在数组多加一行和一列，起到哨兵的作用。这样可以不用判断边界点做特殊处理，所有点都一般化处理。
```
7   22  9   13  3   16  MAX
21  19  1   10  5   6   MAX
18  2   24  15  14  17  MAX
8   23  4   11  12  20  0       # 注意，这里必须是0。
MAX MAX MAX MAX MAX 0   MAX

                    # 这里也必须是0
```

#### 代码
```java
public class Solution {
    public int minPathSum(int[][] grid) {
        int lineSize = grid.length;
        if (lineSize == 0) { return 0; }
        int columnSize = grid[0].length;
        if (columnSize == 0) { return 0; }
        int[][] memo = new int[lineSize+1][columnSize+1]; // 最后多加一行一列哨兵。
        for (int i = 0; i < lineSize+1; i++) {
            memo[i][columnSize] = Integer.MAX_VALUE;
        }
        for (int i = 0; i < columnSize+1; i++) {
            memo[lineSize][i] = Integer.MAX_VALUE;
        }
        memo[lineSize-1][columnSize] = 0; // 为了不影响点[i][j]
        memo[lineSize][columnSize-1] = 0; // 为了不影响点[i][j]
        for (int i = lineSize-1; i >= 0; i--) {
            for (int j = columnSize-1; j >= 0; j--) {
                memo[i][j] = grid[i][j] + Math.min(memo[i+1][j],memo[i][j+1]); // 哨兵的作用显现出来，所有点都一般化处理。
            }
        }
        return memo[0][0];
    }
}
```

#### 结果
银弹！
![minimum-path-sum-1](/images/leetcode/minimum-path-sum-1.png)
