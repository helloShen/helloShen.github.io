---
layout: post
title: "Leetcode - Algorithm - Max Area Of Island "
date: 2017-11-15 22:21:17
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["union find"]
level: "easy"
description: >
---

### 题目
Given a non-empty 2D array grid of 0's and 1's, an island is a group of 1's (representing land) connected 4-directionally (horizontal or vertical.) You may assume all four edges of the grid are surrounded by water.

Find the maximum area of an island in the given 2D array. (If there is no island, the maximum area is 0.)

Example 1:
```
[[0,0,1,0,0,0,0,1,0,0,0,0,0],
 [0,0,0,0,0,0,0,1,1,1,0,0,0],
 [0,1,1,0,1,0,0,0,0,0,0,0,0],
 [0,1,0,0,1,1,0,0,1,0,1,0,0],
 [0,1,0,0,1,1,0,0,1,1,1,0,0],
 [0,0,0,0,0,0,0,0,0,0,1,0,0],
 [0,0,0,0,0,0,0,1,1,1,0,0,0],
 [0,0,0,0,0,0,0,1,1,0,0,0,0]]
```

Given the above grid, return 6. Note the answer is not 11, because the island must be connected 4-directionally.

Example 2:
```
[[0,0,0,0,0,0,0,0]]
```
Given the above grid, return 0.
Note: The length of each dimension in the given grid does not exceed 50.


### Union Find
这是一个最基本的连通性问题。最优方案就是Union Find。为了统计所有连通小组的成员数量，可以额外维护一个数组。

#### 代码
```java
class Solution {
    private int[] count = new int[0]; // 分组成员数量表（下标为小组ID）
    private int[] board = new int[0]; // 分组ID表（下标为小组ID，值于下标相等的点为根节点）

    private void init(int[][] grid) {
        int height = grid.length;
        int width = grid[0].length;
        count = new int[height * width + 1];
        board = new int[height * width + 1];
    }
    public int maxAreaOfIsland(int[][] grid) {
        int height = grid.length;
        if (height == 0) { return 0; }
        int width = grid[0].length;
        init(grid);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grid[i][j] == 1) {
                    int posCurr = j+1 + width * i, posLeft = -1, posUpper = -1;
                    create(posCurr);
                    if (j > 0 && grid[i][j-1] == 1) {
                        posLeft = j + width * i;
                        merge(posCurr,posLeft); // 当前树嫁接到左树
                    }
                    if (i > 0 && grid[i-1][j] == 1) {
                        posUpper = j+1 + width * (i-1);
                        merge(posCurr,posUpper); // 当前树嫁接到上树
                    }
                }
            }
        }
        int max = 0;
        for (int i = 1; i < count.length; i++) {
            max = Math.max(max,count[i]);
        }
        return max;
    }
    private void create(int pos) {
        board[pos] = pos;
        count[pos] = 1;
    }
    private int find(int pos) {
        if (board[pos] == pos) {
            return pos;
        } else {
            int root = find(board[pos]);
            board[pos] = root; // path compression
            return root;
        }
    }
    // root1嫁接到root2上
    private void merge(int pos1, int pos2) {
        int root1 = find(pos1);
        int root2 = find(pos2);
        if (root1 != root2) {
            board[root1] = board[root2];
            count[root2] += count[root1];
            count[root1] = 0;
        }
    }
}
```

#### 结果
![max-area-of-island-1](/images/leetcode/max-area-of-island-1.png)
