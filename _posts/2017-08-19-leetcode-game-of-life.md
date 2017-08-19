---
layout: post
title: "Leetcode - Algorithm - Game Of Life "
date: 2017-08-19 14:37:34
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
According to the Wikipedia's article: "The Game of Life, also known simply as Life, is a cellular automaton devised by the British mathematician John Horton Conway in 1970."

Given a board with m by n cells, each cell has an initial state live (1) or dead (0). Each cell interacts with its eight neighbors (horizontal, vertical, diagonal) using the following four rules (taken from the above Wikipedia article):

Any live cell with fewer than two live neighbors dies, as if caused by under-population.
Any live cell with two or three live neighbors lives on to the next generation.
Any live cell with more than three live neighbors dies, as if by over-population..
Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.
Write a function to compute the next state (after one update) of the board given its current state.

Follow up:
Could you solve it in-place? Remember that the board needs to be updated at the same time: You cannot update some cells first and then use their updated values to update other cells.
In this question, we represent the board using a 2D array. In principle, the board is infinite, which would cause problems when the active area encroaches the border of the array. How would you address these problems?

### 要`in place`就不能马上更新生死状态
需要一组临时标记同时表示 **这一代的生死信息** 以及 **下一代的生死信息**。比如，这一代还活着，下一代就要死了，标记为`2`。这一代是死的，下一代马上要复活，标记为`3`。

#### 代码
```java
class Solution {
    public void gameOfLife(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int nbs = countNeighbors(board,i,j);
                if (board[i][j] == 1) {
                    if (nbs < 2 || nbs > 3) {
                        board[i][j] = 2; // 将死亡，先标记上
                    }
                } else if (nbs == 3) {
                    board[i][j] = 3;     // 将复活，先标记上
                }
            }
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 2) { board[i][j] = 0; }  // 应死的终将死去
                if (board[i][j] == 3) { board[i][j] = 1; }  // 应活的也将得生
            }
        }
    }
    // return the number of neighbors for the given point
    private int countNeighbors(int[][] board, int x, int y) {
        int count = 0;
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (i < 0) { continue; }
                if (i >= board.length) { continue; }
                if (j < 0) { continue; }
                if (j >= board[0].length) { continue; }
                if (i == x && j == y) { continue; }
                if (board[i][j] == 0) { continue; }
                if (board[i][j] == 3) { continue; }
                ++count;
            }
        }
        return count;
    }
}
```

#### 结果
![game-of-life-1](/images/leetcode/game-of-life-1.png)
