---
layout: post
title: "Leetcode - Algorithm - Knight Probability In Chessboard "
date: 2018-12-05 20:09:11
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
On an `NxN` chessboard, a knight starts at the `r-th` row and `c-th` column and attempts to make exactly `K` moves. The rows and columns are 0 indexed, so the top-left square is `(0, 0)`, and the bottom-right square is `(N-1, N-1)`.

A chess knight has 8 possible moves it can make, as illustrated below. Each move is two squares in a cardinal direction, then one square in an orthogonal direction.
![knight](/images/leetcode/knight.png)

Each time the knight is to move, it chooses one of eight possible moves uniformly at random (even if the piece would go off the chessboard) and moves there.

The knight continues moving until it has made exactly K moves or has moved off the chessboard. Return the probability that the knight remains on the board after it has stopped moving.

Example:
```
Input: 3, 2, 0, 0
Output: 0.0625
Explanation: There are two moves (to (1,2), (2,1)) that will keep the knight on the board.
From each of those positions, there are also two moves that will keep the knight on the board.
The total probability the knight stays on the board is 0.0625.
```

Note:
* N will be between 1 and 25.
* K will be between 0 and 100.
* The knight always initially starts on the board.


### 先用DFS熟悉一下问题
DFS无非就是最暴力地让马一步一步地走。`K`步以后看还在不在棋盘上。中间如果有任何一步走出棋盘，整条支线之后的所有分支全部算走出棋盘。

这样因为每一步都有`8`种可能，所以复杂度`O(8 ^ K)`。当`K`很大时， 这是一个天文数字。

#### 代码
```java
class Solution {
    public double knightProbability(int N, int K, int r, int c) {
        init(N);
        moveTo(c, r, K);
        return (off == 0)? 1.0 : (double) on / (on + off);
    }

    private int size;
    private int on, off;

    private void init(int size) {
        this.size = size;
        on = 0;
        off = 0;
    }

    private void moveTo(int x, int y, int remain) {
        if (onTheBoard(x, y)) {
            if (remain == 0) {
                on++;
            } else {
                remain--;
                moveTo(x + 1, y + 2, remain);
                moveTo(x + 2, y + 1, remain);
                moveTo(x + 2, y - 1, remain);
                moveTo(x + 1, y - 2, remain);
                moveTo(x - 1, y - 2, remain);
                moveTo(x - 2, y - 1, remain);
                moveTo(x - 2, y + 1, remain);
                moveTo(x - 1, y + 2, remain);
            }
        } else {
            off += (int) Math.pow(8, remain);
        }
    }

    private boolean onTheBoard(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }
}
```

#### 结果
![knight-probability-in-chessboard-2](/images/leetcode/knight-probability-in-chessboard-2.png)

### 动态规划
仔细观察可以发现，对于任意一个棋盘，从每一格，走一步以后还留在棋盘上的概率是可以算出来的，如下图所示，
![knight-probability-in-chessboard-figure-1](/images/leetcode/knight-probability-in-chessboard-figure-1.png)

然后以这个数据为基础，又可以计算出从任意一格，走两步以后还留在棋盘的概率。然后依次类推，走`K`步的话需要计算`8 * K * N * N`次。也就是复杂度`O(KNN)`。

#### 代码
```java
class Solution {
    private int size;
    private double[][] oldBoard, newBoard;

    private void init(int size) {
        this.size = size;
        oldBoard = new double[size][size];
        newBoard = new double[size][size];
    }
    public double knightProbability(int N, int K, int r, int c) {
        if (K == 0) return 1.0;
        init(N);
        for (int i = 0; i < N; i++) Arrays.fill(oldBoard[i], 1.0);
        for (int i = 0; i < K - 1; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    newBoard[j][k] = sumProb(j, k, oldBoard);
                }
            }
            double[][] temp = oldBoard;
            oldBoard = newBoard;
            newBoard = temp;
        }
        return sumProb(r, c, oldBoard);
    }

    private double sumProb(int r, int c, double[][] board) {
        double sum = 0;
        sum  += (onTheBoard(r + 1, c + 2))? board[r + 1][c + 2] : 0;
        sum  += (onTheBoard(r + 2, c + 1))? board[r + 2][c + 1] : 0;
        sum  += (onTheBoard(r + 2, c - 1))? board[r + 2][c - 1] : 0;
        sum  += (onTheBoard(r + 1, c - 2))? board[r + 1][c - 2] : 0;
        sum  += (onTheBoard(r - 1, c - 2))? board[r - 1][c - 2] : 0;
        sum  += (onTheBoard(r - 2, c - 1))? board[r - 2][c - 1] : 0;
        sum  += (onTheBoard(r - 2, c + 1))? board[r - 2][c + 1] : 0;
        sum  += (onTheBoard(r - 1, c + 2))? board[r - 1][c + 2] : 0;
        return sum / 8;
    }

    private boolean onTheBoard(int r, int c) {
        return r >= 0 && r < size && c >= 0 && c < size;
    }
}
```

#### 结果
![knight-probability-in-chessboard-1](/images/leetcode/knight-probability-in-chessboard-1.png)
