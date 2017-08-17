---
layout: post
title: "Leetcode - Algorithm - Island Perimeter "
date: 2017-08-17 18:14:07
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
You are given a map in form of a two-dimensional integer grid where 1 represents land and 0 represents water. Grid cells are connected horizontally/vertically (not diagonally). The grid is completely surrounded by water, and there is exactly one island (i.e., one or more connected land cells). The island doesn't have "lakes" (water inside that isn't connected to the water around the island). One cell is a square with side length 1. The grid is rectangular, width and height don't exceed 100. Determine the perimeter of the island.

Example:
```
[[0,1,0,0],
 [1,1,1,0],
 [0,1,0,0],
 [1,1,0,0]]

Answer: 16
```
Explanation: The perimeter is the 16 yellow stripes in the image below:
![island-perimeter](/images/leetcode/island-perimeter.png)

### 基本思路
这个问题可以看做是找临接海洋的边加上处在最外圈的边的数量。

也可以换个思路，做减法。每两个临接的方块相邻的两条边不能成为边界。

#### 代码
```java
public class Solution {
    public int islandPerimeter(int[][] grid) {
        if (grid.length == 0) { return 0; }
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 1) {
                    count += 4;
                    if (i > 0 && grid[i-1][j] == 1) { count -= 2; }
                    if (j > 0 && grid[i][j-1] == 1) { count -= 2; }
                }
            }
        }
        return count;
    }
}
```

#### 结果
![island-perimeter-1](/images/leetcode/island-perimeter-1.png)
