---
layout: post
title: "Leetcode - Algorithm - Projection Area Of 3d Shapes "
date: 2018-08-04 23:25:42
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
On a N * N grid, we place some 1 * 1 * 1 cubes that are axis-aligned with the x, y, and z axes.

Each value v = grid[i][j] represents a tower of v cubes placed on top of grid cell (i, j).

Now we view the projection of these cubes onto the xy, yz, and zx planes.

A projection is like a shadow, that maps our 3 dimensional figure to a 2 dimensional plane.

Here, we are viewing the "shadow" when looking at the cubes from the top, the front, and the side.

Return the total area of all three projections.


Example 1:
```
Input: [[2]]
Output: 5
```

Example 2:
```
Input: [[1,2],[3,4]]
Output: 17
Explanation:
Here are the three projections ("shadows") of the shape made with each axis-aligned plane.
```
![projection-area-of-3d-shapes-1](/images/leetcode/projection-area-of-3d-shapes.png)

Example 3:
```
Input: [[1,0],[0,2]]
Output: 8
```

Example 4:
```
Input: [[1,1,1],[1,0,1],[1,1,1]]
Output: 14
```

Example 5:
```
Input: [[2,2,2],[2,1,2],[2,2,2]]
Output: 21
```

Note:
* 1 <= grid.length = grid[0].length <= 50
* 0 <= grid[i][j] <= 50

### 问题的本质：求每行每列的最大值
考虑图示中`2*2`的矩阵`[[1,2],[4,5]]`，我们把二维数组写成矩阵的形式更容易看，
```
1,2
3,4
```
![projection-area-of-3d-shapes-1](/images/leetcode/projection-area-of-3d-shapes.png)
第一张图，只要矩阵对应位置的值不为`0`，就占住这一格位置。

第二张图，相当于求 **矩阵每一行的最大值**，因为从侧面看，只有最高的塔的高度是有意义的，其他都会被挡住，
```
1,2  -> max = 2
3,4  -> max = 3
```

同理，第三张图，相当于 **矩阵每一列的最大值**，
```
1,2
3,4
| |
3 4
```


#### 代码
```java
public int projectionArea(int[][] grid) {
    int x = 0, y = 0, z = 0;
    int[] maxY = new int[grid[0].length];   //记录每一列的最大值
    for (int i = 0; i < grid.length; i++) {
        int maxX = 0;                       //记录每一行的最大值
        for (int j = 0; j < grid[0].length; j++) {
           if (grid[i][j] != 0) {
                maxX = Math.max(grid[i][j],maxX);
                maxY[j] = Math.max(grid[i][j],maxY[j]);
                z++;
           }
        }
        x += maxX;
    }
    for (int i = 0; i < grid[0].length; i++) {
        y += maxY[i];
    }
    return x + y + z;
}
```
