---
layout: post
title: "Leetcode - Algorithm - The Maze "
date: 2019-01-19 22:01:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search", "breadth first search"]
level: "medium"
description: >
---

### 题目
There is a ball in a maze with empty spaces and walls. The ball can go through empty spaces by rolling up, down, left or right, but it won't stop rolling until hitting a wall. When the ball stops, it could choose the next direction.

Given the ball's start position, the destination and the maze, determine whether the ball could stop at the destination.

The maze is represented by a binary 2D array. 1 means the wall and 0 means the empty space. You may assume that the borders of the maze are all walls. The start and destination coordinates are represented by row and column indexes.

Example 1:
```
Input 1: a maze represented by a 2D array

0 0 1 0 0
0 0 0 0 0
0 0 0 1 0
1 1 0 1 1
0 0 0 0 0

Input 2: start coordinate (rowStart, colStart) = (0, 4)
Input 3: destination coordinate (rowDest, colDest) = (4, 4)

Output: true

Explanation: One possible way is : left -> down -> left -> down -> right -> down -> right.
```

Example 2:
```
Input 1: a maze represented by a 2D array

0 0 1 0 0
0 0 0 0 0
0 0 0 1 0
1 1 0 1 1
0 0 0 0 0

Input 2: start coordinate (rowStart, colStart) = (0, 4)
Input 3: destination coordinate (rowDest, colDest) = (3, 2)

Output: false

Explanation: There is no way for the ball to stop at the destination.
```

Note:
* There is only one ball and one destination in the maze.
* Both the ball and the destination exist on an empty space, and they will not be at the same position initially.
* The given maze does not contain border (like the red rectangle in the example pictures), but you could assume the border of the maze are all walls.
* The maze contains at least 2 empty spaces, and both the width and height of the maze won't exceed 100.

### DFS && BFS
既然是弹球游戏，那就让球自由地弹。看他最终能探索到哪些区域。因此DFS和BFS都可以。

#### 代码
```java
class Solution {
    public boolean hasPath(int[][] maze, int[] start, int[] destination) {
        height = maze.length;
        width = maze[0].length;
        localMaze = maze;
        visited = new int[height][width];
        return dfs(start, destination);
    }

    private int height, width;
    private int[][] localMaze;
    private int[][] visited;

    private boolean dfs(int[] start, int[] destination) {
        if (Arrays.equals(start, destination)) return true;
        if (visited[start[0]][start[1]] == 1) return false;
        visited[start[0]][start[1]] = 1;
        return dfs(rollLeft(start), destination) ||
               dfs(rollRight(start), destination) ||
               dfs(rollUp(start), destination) ||
               dfs(rollDown(start), destination);
    }

    private int[] rollLeft(int[] start) {
        int row = start[0], col = start[1];
        for (int i = col - 1; i >= 0; i--) {
            if (localMaze[row][i] == 1) return new int[]{row, i + 1};
        }
        return new int[]{row, 0};
    }

    private int[] rollRight(int[] start) {
        int row = start[0], col = start[1];
        for (int i = col + 1; i < width; i++) {
            if (localMaze[row][i] == 1) return new int[]{row, i - 1};
        }
        return new int[]{row, width - 1};
    }

    private int[] rollUp(int[] start) {
        int row = start[0], col = start[1];
        for (int i = row - 1; i >= 0; i--) {
            if (localMaze[i][col] == 1) return new int[]{i + 1, col};
        }
        return new int[]{0, col};
    }
    private int[] rollDown(int[] start) {
        int row = start[0], col = start[1];
        for (int i = row + 1; i < height; i++) {
            if (localMaze[i][col] == 1) return new int[]{i - 1, col};
        }
        return new int[]{height - 1, col};
    }
}
```

#### 结果
![the-maze-1](/images/leetcode/the-maze-1.png)
