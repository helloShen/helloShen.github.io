---
layout: post
title: "Leetcode - Algorithm - Pacific Atlantic Water Flow "
date: 2019-03-17 11:16:51
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search", "breadth first search", "array"]
level: "medium"
description: >
---

### 题目
Given an m x n matrix of non-negative integers representing the height of each unit cell in a continent, the "Pacific ocean" touches the left and top edges of the matrix and the "Atlantic ocean" touches the right and bottom edges.

Water can only flow in four directions (up, down, left, or right) from a cell to another one with height equal or lower.

Find the list of grid coordinates where water can flow to both the Pacific and Atlantic ocean.

Note:
* The order of returned grid coordinates does not matter.
* Both m and n are less than 150.

Example:
```
Given the following 5x5 matrix:

  Pacific ~   ~   ~   ~   ~
       ~  1   2   2   3  (5) *
       ~  3   2   3  (4) (4) *
       ~  2   4  (5)  3   1  *
       ~ (6) (7)  1   4   5  *
       ~ (5)  1   1   2   4  *
          *   *   *   *   * Atlantic

Return:

[[0, 4], [1, 3], [1, 4], [2, 2], [3, 0], [3, 1], [4, 0]] (positions with parentheses in above matrix).
```

### DFS或BFS
基本思路是逐个点去检查，这个点是否可以连通太平洋和大西洋。可以用一个`boolean[2]`数组记录这个数据。可以同时连通太平洋和大西洋的点就是需要返回的符合条件的点。

下图中，蓝色点表示能连通太平洋，黄色点能连通大西洋。黄蓝重叠的点就是同时能连通两大洋的点。
![pacific-atlantic-water-flow-figure-1](/images/leetcode/pacific-atlantic-water-flow-figure-1.png)

#### 代码
```java
class Solution {
    public List<int[]> pacificAtlantic(int[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0) return new LinkedList<int[]>();
        init(matrix);
        parsePacific();
        parseAtlantic();
        return check();
    }

    private int[][] localMatrix;
    private boolean[][][] memo;
    private int height, width;

    private void init(int[][] matrix) {
        localMatrix = matrix;
        height = matrix.length;
        width = matrix[0].length;
        memo = new boolean[height][width][2];
    }

    private void parsePacific() {
        for (int i = 0; i < width; i++) canFlow(0, i, 0);
        for (int i = 0; i < height; i++) canFlow(i, 0, 0);
    }

    private void parseAtlantic() {
        for (int i = 0; i < width; i++) canFlow(height - 1, i, 1);
        for (int i = 0; i < height; i++) canFlow(i, width - 1, 1);
    }

    /**
     * @param  ocean: 0 = pacific, 1 = atlantic
     */
    private void canFlow(int row, int col, int ocean) {
        memo[row][col][ocean] = true;
        if (row < height - 1 && !memo[row + 1][col][ocean] && localMatrix[row + 1][col] >= localMatrix[row][col]) canFlow(row + 1, col, ocean);
        if (row > 0 && !memo[row - 1][col][ocean] && localMatrix[row - 1][col] >= localMatrix[row][col]) canFlow(row - 1, col, ocean);
        if (col < width - 1 && !memo[row][col + 1][ocean] && localMatrix[row][col + 1] >= localMatrix[row][col]) canFlow(row, col + 1, ocean);
        if (col > 0 && !memo[row][col - 1][ocean] && localMatrix[row][col - 1] >= localMatrix[row][col]) canFlow(row, col - 1, ocean);
    }

    private List<int[]> check() {
        List<int[]> res = new LinkedList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (memo[i][j][0] && memo[i][j][1]) res.add(new int[]{i, j});
            }
        }
        return res;
    }
}
```

#### 结果
![pacific-atlantic-water-flow-1](/images/leetcode/pacific-atlantic-water-flow-1.png)
