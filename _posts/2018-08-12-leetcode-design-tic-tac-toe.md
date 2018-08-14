---
layout: post
title: "Leetcode - Algorithm - Design Tic Tac Toe "
date: 2018-08-12 13:39:42
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Design a Tic-tac-toe game that is played between two players on a n x n grid.

You may assume the following rules:
1. A move is guaranteed to be valid and is placed on an empty block.
2. Once a winning condition is reached, no more moves is allowed.
3. A player who succeeds in placing n of their marks in a horizontal, vertical, or diagonal row wins the game.

Example:
```
Given n = 3, assume that player 1 is "X" and player 2 is "O" in the board.

TicTacToe toe = new TicTacToe(3);

toe.move(0, 0, 1); -> Returns 0 (no one wins)
|X| | |
| | | |    // Player 1 makes a move at (0, 0).
| | | |

toe.move(0, 2, 2); -> Returns 0 (no one wins)
|X| |O|
| | | |    // Player 2 makes a move at (0, 2).
| | | |

toe.move(2, 2, 1); -> Returns 0 (no one wins)
|X| |O|
| | | |    // Player 1 makes a move at (2, 2).
| | |X|

toe.move(1, 1, 2); -> Returns 0 (no one wins)
|X| |O|
| |O| |    // Player 2 makes a move at (1, 1).
| | |X|

toe.move(2, 0, 1); -> Returns 0 (no one wins)
|X| |O|
| |O| |    // Player 1 makes a move at (2, 0).
|X| |X|

toe.move(1, 0, 2); -> Returns 0 (no one wins)
|X| |O|
|O|O| |    // Player 2 makes a move at (1, 0).
|X| |X|

toe.move(2, 1, 1); -> Returns 1 (player 1 wins)
|X| |O|
|O|O| |    // Player 1 makes a move at (2, 1).
|X|X|X|
```

Follow up:
Could you do better than `O(n2)` per move() operation?

### 遍历整个棋盘`O(n^2)`
最笨的办法就是用一个二维数组`int[][]`模拟整个棋盘。然后每次`move()`都遍历整个棋盘，来确定有没有一方获胜。

这方法太蠢了，实在不想写。

### 只检查当前落子的这一行，列或对角线，`O(n)`
还是用二维数组`int[][]`模拟整个棋盘。但每次落子，只需要检查落子所在的行，列和对角线有没有可能获胜即可。复杂度降为`O(n)`。

![design-tic-tac-toe-a](/images/leetcode/design-tic-tac-toe-a.png)

#### 代码
```java
class TicTacToe {

    /** Initialize your data structure here. */
    public TicTacToe(int n) {
        board = new int[n][n];
        size = n;
        win = 0;
    }

    /** Player {player} makes a move at ({row}, {col}).
        @param row The row of the board.
        @param col The column of the board.
        @param player The player, can be either 1 or 2.
        @return The current winning condition, can be either:
                0: No one wins.
                1: Player 1 wins.
                2: Player 2 wins. */
    public int move(int row, int col, int player) {
        if (win > 0) { return win; } //有人赢了就不能再下了
        board[row][col] = player;
        if (win(row,col,player)) {
            win = player;
            return player;
        } else {
            return 0;
        }
    }


    /**================================以下内容为私有================================*/

    //棋盘
    private int[][] board;
    //棋盘大小
    private int size;
    //胜利标志(只要有一方赢了，这个标志就不改了)
    private int win;

    private boolean win(int row, int col, int player) {
        return rowWin(row,col,player) ||
               colWin(row,col,player) ||
               ((row == col) && diagonalWin(player)) ||
               ((row + col == size-1) && antiDiagonalWin(player));
    }
    private boolean rowWin(int row, int col, int player) {
        for (int i = 0; i < size; i++) {
            if (board[row][i] != player) {
                return false;
            }
        }
        return true;
    }
    private boolean colWin(int row, int col, int player) {
        for (int i = 0; i < size; i++) {
            if (board[i][col] != player) {
                return false;
            }
        }
        return true;
    }
    private boolean diagonalWin(int player) {
        for (int i = 0; i < size; i++) {
            if (board[i][i] != player) {
                return false;
            }
        }
        return true;
    }
    private boolean antiDiagonalWin(int player) {
        for (int i = 0; i < size; i++) {
            if (board[i][size-1-i] != player) {
                return false;
            }
        }
        return true;
    }
}

/**
 * Your TicTacToe object will be instantiated and called as such:
 * TicTacToe obj = new TicTacToe(n);
 * int param_1 = obj.move(row,col,player);
 */
```

#### 结果
![design-tic-tac-toe-1](/images/leetcode/design-tic-tac-toe-1.png)


### 不模拟棋盘，把行列占用信息抽象出来，`O(1)`
如果我把每一行，每一列，以及斜线被占用的信息单独统计出来，如下图,
![design-tic-tac-toe-b](/images/leetcode/design-tic-tac-toe-b.png)

对于每一行，我需要知道的只有2个信息，
1. 这行有几个玩家的棋子
2. 如果所有棋子都属于同一个玩家，那么有几颗棋子

对于每一列也是如此，以及两个对角线也一样。这时会发现，仅仅依赖`row[]`,`col[]`以及`diagonal[]`就可以完成每一步棋对棋盘信息的更新。原来的棋盘已经不重要了。

所以就有下面代码，`rows[height][2]`是一个二维数组，对于第`k`行`rows[k]`有两项信息，
1. rows[k][0]: 这行的归属
    * 0: 这一行还没有棋子
    * 1: 这一行只有玩家1的棋子
    * 2: 这一行只有玩家2的棋子
    * 3: 这一行已经同时有2个玩家的棋子，死局
2. rows[k][1]: 如果此行只有一个玩家的棋子，则它统计棋子个数

#### 代码
```java
class TicTacToe {

    //模拟构造函数
    public TicTacToe(int n) {
        row = new int[n][2];
        col = new int[n][2];
        diagonal = new int[2][2];
        size = n;
        win = 0;
    }
    //下棋
    public int move(int row, int col, int player) {
        if (win > 0) { return win; } //有人赢了就不能再下了
        if (win(row,col,player)) {
            win = player;
            return player;
        } else {
            return 0;
        }
    }


    /**================================以下内容为私有================================*/


    /**
     * 棋盘（真实棋盘被抽象统计表代替）
     * row[x][0]标记x行归属
     * row[x][1]标记x行霸主占了多少格
     * col[y][1]标记y列归属
     * col[y][1]标记y列霸主占了多少格
     * diagonal[0][0]标记正斜线"\"归属
     * diagonal[0][1]标记正斜线"\"霸主占了多少格
     * diagonal[1][0]标记反斜线"\"归属
     * diagonal[1][1]标记反斜线"\"霸主占了多少格
     *

     */
    private int[][] row, col, diagonal;
    //棋盘大小
    private int size;
    //胜利标志(只要有一方赢了，这个标志就不改了)
    private int win;
    /**
     * 常数：
     *      0: 还没有人涉足
     *      3: 1，2都有布局，谁都不是霸主，死局
     */
    private final int EMPTY = 0;
    private final int CAN_NOT_WIN = 3;

    private boolean win(int rowNum, int colNum, int player) {
        return rowColWin(row,rowNum,player) ||
               rowColWin(col,colNum,player) ||
               ((rowNum == colNum) && diagonalWin(diagonal[0], player)) ||
               ((rowNum + colNum == size-1) && diagonalWin(diagonal[1], player));
    }
    //行或列获胜判定方法一致，所以合并
    private boolean rowColWin(int[][] rowOrCol, int numRowCol, int player) {
        if (rowOrCol[numRowCol][0] == EMPTY || rowOrCol[numRowCol][0] == player) {
            if (rowOrCol[numRowCol][0] == EMPTY) {
                rowOrCol[numRowCol][0] = player;
            }
            if (++rowOrCol[numRowCol][1] == size) {
                win = player;
                return true;
            } else {
                return false;
            }
        } else { //要么对手是霸主，要么已经是死局
            if (rowOrCol[numRowCol][0] != CAN_NOT_WIN) { //对手是霸主
                rowOrCol[numRowCol][0] = CAN_NOT_WIN;
            }
            return false;
        }
    }
    private boolean diagonalWin(int[] diagonal, int player) {
        if (diagonal[0] == EMPTY || diagonal[0] == player) {
            if (diagonal[0] == EMPTY) {
                diagonal[0] = player;
            }
            if (++diagonal[1] == size) {
                win = player;
                return true;
            } else {
                return false;
            }
        } else { //要么对手是霸主，要么已经是死局
            if (diagonal[0] != CAN_NOT_WIN) { //对手是霸主
                diagonal[0] = CAN_NOT_WIN;
            }
            return false;
        }
    }
}

/**
 * Your TicTacToe object will be instantiated and called as such:
 * TicTacToe obj = new TicTacToe(n);
 * int param_1 = obj.move(row,col,player);
 */
```

#### 结果
![design-tic-tac-toe-2](/images/leetcode/design-tic-tac-toe-2.png)


### 再简化一下数据结构，用一维数组
之前我们用`int[n][2]`统计每一行，每一列。其中`row[x][0]`表示谁占领了这一格，`row[x][1]`记录占领了多少格。

考虑到只有两个人玩游戏，一个人用`1`，另一个用`-1`，最终只需要统计每一行每一列的综合即可。

#### 代码
```java
class TicTacToe {

    public TicTacToe(int n) {
        size = n;
        rows = new int[n];
        cols = new int[n];
        diagonal = 0;
        antiDiagonal = 0;
        win = 0;
    }
    public int move(int row, int col, int player) {
        //alreay finish
        if (win > 0) { return win; }
        //move
        int move = (player == 1)? 1 : -1;
        rows[row] += move;
        cols[col] += move;
        if (row == col) { diagonal += move; }
        if (row + col == size - 1) { antiDiagonal += move; }
        //win?
        if (win(row,col,player)) {
            win = player;
            return player;
        } else {
            return 0;
        }
    }

    private int size;
    private int[] rows;
    private int[] cols;
    private int diagonal;
    private int antiDiagonal;
    private int win;

    private boolean win(int row, int col, int player) {
        int target = (player == 1)? size : -size;
        return rows[row] == target ||
               cols[col] == target ||
               diagonal == target ||
               antiDiagonal == target;
    }

}
```

#### 结果
![design-tic-tac-toe-3](/images/leetcode/design-tic-tac-toe-3.png)
