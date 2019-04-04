---
layout: post
title: "Leetcode - Algorithm - Valid Tic Tac Toe State "
date: 2019-04-04 12:50:57
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string", "array"]
level: "medium"
description: >
---

### 题目
A Tic-Tac-Toe board is given as a string array board. Return True if and only if it is possible to reach this board position during the course of a valid tic-tac-toe game.

The board is a `3 x 3` array, and consists of characters " ", "X", and "O".  The " " character represents an empty square.

Here are the rules of Tic-Tac-Toe:
* Players take turns placing characters into empty squares (" ").
* The first player always places "X" characters, while the second player always places "O" characters.
* "X" and "O" characters are always placed into empty squares, never filled ones.
* The game ends when there are 3 of the same (non-empty) character filling any row, column, or diagonal.
* The game also ends if all squares are non-empty.
* No more moves can be played if the game is over.

Example 1:
```
Input: board = ["O  ", "   ", "   "]
Output: false
Explanation: The first player always plays "X".
```

Example 2:
```
Input: board = ["XOX", " X ", "   "]
Output: false
Explanation: Players take turns making moves.
```

Example 3:
```
Input: board = ["XXX", "   ", "OOO"]
Output: false
```

Example 4:
```
Input: board = ["XOX", "O O", "XOX"]
Output: true
```

Note:
* board is a length-3 array of strings, where each string `board[i]` has length `3`.
* Each `board[i][j]` is a character in the set `{" ", "X", "O"}`.


### 一次遍历
首先逻辑是这样：
1. `X`因为先行，数量要么和`O`相等，要么比`O`多一个
2. 如果`X`赢了，那么`X`的数量必须比`O`多一个（因为最后一步是`X`走）
3. 如果`O`赢了，那么`O`的数量和`X`必须相等（因为最后一步是`O`走）
4. `X`和`O`不可能同时赢（因为一方获胜，游戏终止）

一次遍历的做法，需要额外空间统计每一列，以及斜向的`X`和`O`的数量。`int[3][2] columnCount`统计每一列，`int[2][2] diagonalCount`统计斜向。

#### 代码
下面代码是系统性地处理问题，如果`board`扩展到`4*4`或者`n*n`同样适用。如果觉得题目规定了`3*3`，直接放到`char[9]`里检查，也行。
```java
class Solution {
    public boolean validTicTacToe(String[] board) {
        boolean xWin = false, oWin = false;
        int[][] columnCount = new int[3][2];
        int[][] diagonalCount = new int[2][2];
        int xCount = 0, oCount = 0;
        for (int i = 0; i < 3; i++) {
            String row = board[i];
            if (row.equals("XXX")) {
                xWin = true;
            } else if (row.equals("OOO")) {
                oWin = true;
            }
            for (int j = 0; j < 3; j++) {
                char c = row.charAt(j);
                if (c == 'X') {
                    xCount++;
                    columnCount[j][0]++;
                    if (i == j) diagonalCount[0][0]++;
                    if ((i + j) == 2) diagonalCount[1][0]++;
                } else if (c == 'O') {
                    oCount++;
                    columnCount[j][1]++;
                    if (i == j) diagonalCount[0][1]++;
                    if ((i + j) == 2) diagonalCount[1][1]++;
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            if (columnCount[i][0] == 3) xWin = true;
            if (columnCount[i][1] == 3) oWin = true;
        }
        for (int i = 0; i < 2; i++) {
            if (diagonalCount[i][0] == 3) xWin = true;
            if (diagonalCount[i][1] == 3) oWin = true;
        }
        if (xWin && oWin) return false;
        if (xCount < oCount || (xCount - oCount) > 1) return false;
        if (xWin && xCount == oCount) return false;
        if (oWin && (xCount - oCount) == 1) return false;
        return true;
    }
}
```

#### 结果
![valid-tic-tac-toe-state-1](/images/leetcode/valid-tic-tac-toe-state-1.png)
