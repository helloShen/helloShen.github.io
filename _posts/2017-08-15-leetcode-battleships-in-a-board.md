---
layout: post
title: "Leetcode - Algorithm - Battleships In A Board "
date: 2017-08-15 20:16:17
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["graph","union find","array"]
level: "medium"
description: >
---

### 题目
Given an 2D board, count how many battleships are in it. The battleships are represented with `X`s, empty slots are represented with `.`s. You may assume the following rules:

You receive a valid board, made of only battleships or empty slots.
Battleships can only be placed horizontally or vertically. In other words, they can only be made of the shape 1xN (1 row, N columns) or Nx1 (N rows, 1 column), where N can be of any size.
At least one horizontal or vertical cell separates between two battleships - there are no adjacent battleships.
Example:
```
X..X
...X
...X
```
In the above board there are 2 battleships.
Invalid Example:
```
...X
XXXX
...X
```
This is an invalid board that you will not receive - as battleships will always have a cell separating between them.

**Follow up** :
Could you do it in one-pass, using only O(1) extra memory and without modifying the value of the board?

### 基本思路
首先这是一个 **图连通性问题**。标准解法是`Union Find`，复杂度是$$O(n\log_{}{n})$$.

但这题有一个特殊条件：
> At least one horizontal or vertical cell separates between two battleships - there are no adjacent battleships.

这就是说，在连通图的时候，**不存在把两棵合并成一棵树的情况**。合并两棵树是朴素的给每组元素同一个编号的方法行不通的原因。比如下面这个例子,

遍历到`[3,3]`位置时，存在两棵树，
```
      树1
       |
    ...1
    ...1
    222     --树2
```
当遍历到`[3,4]`位置，发现2号树需要和1号树合并。这时候 **需要改变2号树所有元素的编号**。这就是朴素做法复杂度高的原因。最坏情况复杂度 $$O(n^2)$$。所以`Union Find`的$$O(n\log_{}{n})$$才显得有价值。
```
      树1
       |
    ...1
    ...1
    222X    --现在树2合并入树1
```

现在这题不存在这样合并两棵树的情况。所以这种朴素的给元素编号的 **连通图** 解法成为可能。

### 检查上一点和左边一点
实际做的时候不需要给每个数编号。从左往右，自上而下遍历的时候，如果当前点是`X`，只需要检查上面一点和左边一点，如果都不是`X`，计数器加一。

#### 代码
```java
public class Solution {
    private final char CROSS = 'X';
    public int countBattleships(char[][] board) {
        if (board.length == 0) { return 0; }
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == CROSS) {
                    if (!(i > 0 && board[i-1][j] == CROSS) && !(j > 0 && board[i][j-1] == CROSS)) { ++count; }
                }
            }
        }
        return count;
    }
}
```

#### 结果
![battleships-in-a-board-1](/images/leetcode/battleships-in-a-board-1.png)


#### 代码
也可以把比较复杂的判断逻辑分成几部分，用`continue`逐步淘汰。这样效率比较高。
```java
public class Solution {
    private final char POINT = '.';
    private final char CROSS = 'X';
    public int countBattleships(char[][] board) {
        if (board.length == 0) { return 0; }
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == POINT) { continue; }
                if (i > 0 && board[i-1][j] == CROSS) { continue; }
                if (j > 0 && board[i][j-1] == CROSS) { continue; }
                ++count;
            }
        }
        return count;
    }
}
```

#### 结果
![battleships-in-a-board-2](/images/leetcode/battleships-in-a-board-2.png)
