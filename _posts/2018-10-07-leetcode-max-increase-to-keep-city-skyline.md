---
layout: post
title: "Leetcode - Algorithm - Max Increase To Keep City Skyline "
date: 2018-10-07 23:51:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: ""
description: >
---

### 题目
In a 2 dimensional array grid, each value `grid[i][j]` represents the height of a building located there. We are allowed to increase the height of any number of buildings, by any amount (the amounts can be different for different buildings). Height 0 is considered to be a building as well.

At the end, the "skyline" when viewed from all four directions of the grid, i.e. top, bottom, left, and right, must be the same as the skyline of the original grid. A city's skyline is the outer contour of the rectangles formed by all the buildings when viewed from a distance. See the following example.

What is the maximum total sum that the height of the buildings can be increased?

Example:
```
Input: grid = [[3,0,8,4],[2,4,5,7],[9,2,6,3],[0,3,1,0]]
Output: 35
Explanation:
The grid is:
[ [3, 0, 8, 4],
  [2, 4, 5, 7],
  [9, 2, 6, 3],
  [0, 3, 1, 0] ]

The skyline viewed from top or bottom is: [9, 4, 8, 7]
The skyline viewed from left or right is: [8, 7, 9, 3]

The grid after increasing the height of buildings without affecting skylines is:

gridNew = [ [8, 4, 8, 7],
            [7, 4, 7, 7],
            [9, 4, 8, 7],
            [3, 3, 3, 3] ]
```

Notes:
* 1 < grid.length = grid[0].length <= 50.
* All heights grid[i][j] are in the range [0, 100].
* All buildings in grid[i][j] occupy the entire grid cell: that is, they are a 1 x 1 x grid[i][j] rectangular prism.


### 先找出横向和纵向的skyline
skyline其实就是每行，每列最大值构成的，因为最高的建筑挡住了其他建筑。所以如果要保持skyline，对于任意一点`grid[i][j]`它的值即不能超过`horizon[j]`也不能超过`vertical[i]`。比如`grid[0][0] = 3`，它不能超过`horizon[0] = 9`，也不能超过`vertical[0] = 8`，取更小的那一个。

```
        |  9  4  8  7   <-- 横向skyline: horizon[j]
        | <= <= <= <=
----------------------
        | [3, 0, 8, 4]   <= 8
        | [2, 4, 5, 7]   <= 7
        | [9, 2, 6, 3]   <= 9
        | [0, 3, 1, 0]   <= 3
                            ^
                            |
                            纵向skyline: vertical[i]

```

#### 代码
```java
class Solution {
    public int maxIncreaseKeepingSkyline(int[][] grid) {
        int height = grid.length, width = grid[0].length;
        int[] horizon = new int[width];
        int[] vertical = new int[height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                horizon[j] = Math.max(horizon[j], grid[i][j]);
                vertical[i] = Math.max(vertical[i], grid[i][j]);
            }
        }
        int diff = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                diff += (Math.min(horizon[j], vertical[i]) - grid[i][j]);
            }
        }
        return diff;
    }
}
```

#### 结果
![max-increase-to-keep-city-skyline-1](/images/leetcode/max-increase-to-keep-city-skyline-1.png)
