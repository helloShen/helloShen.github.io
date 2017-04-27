---
layout: post
title: "Leetcode - Algorithm - Word Search "
date: 2017-04-26 20:51:29
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","backtracking"]
level: "medium"
description: >
---

### 主要收获 - 1: 大胆推进递归`base case`
递归胆子要大，可以把`base case`推进到`出错`的情况，把错误处理交给`base case`，可以简化逻辑。

### 主要收获 - 2: 尽量不要用额外空间
额外空间的开销有的时候比想象的要大，尤其是递归的时候。

### 题目
Given a 2D board and a word, find if the word exists in the grid.

The word can be constructed from letters of sequentially adjacent cell, where "adjacent" cells are those horizontally or vertically neighboring. The same letter cell may not be used more than once.

For example,
Given board =
```
[
  ['A','B','C','E'],
  ['S','F','C','S'],
  ['A','D','E','E']
]
```
word = "ABCCED", -> returns true,
word = "SEE", -> returns true,
word = "ABCB", -> returns false.

### 一位一位核对, 使用额外 $$O(m*n)$$ 空间，复杂度$$O(4^k)$$
遍历数组，对每个字符都调用`scan()`函数。`scan()`函数的责任就是核对当前字符，如果字符符合，就递归对`前后左右`四个字符递归调用`scan()`函数。直至匹配成功或匹配失败。

另外为了避免使用重复字符，维护一个额外的`row * col`二维数组。每个元素指明`board`中的对应元素有没有被使用过。（需要注意匹配失败之后的，备忘录使用记录的回退。）

这题的`tag`是`backtracking`，这里所谓的 **回溯** 主要就是指的使用记录回退。

#### 代码
```java
public class Solution {
    public boolean exist(char[][] board, String word) {
        if (board.length == 0 || word.length() == 0) { return false; } // 0 x 0 array
        if (board.length == 1 && board[0].length == 1) { // 1 x 1 array
            return (word.length() == 1 && word.charAt(0) == board[0][0]);
        }
        char[] letters = word.toCharArray();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boolean[][] used = new boolean[board.length][board[0].length];
                if (scan(board,used,letters,0,i,j)) { return true; }
            }
        }
        return false;
    }
    public boolean scan(char[][] board, boolean[][] used, char[] letters, int cursor, int row, int col) {
        if (cursor == letters.length) { return true; } // end ?
        if (board[row][col] != letters[cursor] || used[row][col]) { return false; } // verify current ?
        used[row][col] = true; // note the used element
        /* look around */
        if (row > 0) { // not first row
            if (scan(board,used,letters,cursor+1,row-1,col)) { return true; } // check higher row
        }
        if (row < board.length-1) { // not last row
            if (scan(board,used,letters,cursor+1,row+1,col)) { return true; } // check lower row
        }
        if (col > 0) { // not first colum
            if (scan(board,used,letters,cursor+1,row,col-1)) { return true; } // check left column
        }
        if (col < board[0].length-1) { // not last column
            if (scan(board,used,letters,cursor+1,row,col+1)) { return true; } // check right column
        }
        used[row][col] = false; // 注意回溯，一条路径一旦失败，所有标记used的全部改回来。
        return false;
    }
}
```

#### 结果
问题在哪里？
![word-search-1](/images/leetcode/word-search-1.png)


### 一位一位核对，不使用额外空间，复杂度还是$$O(4^k)$$，k表示word长度
用来记录字符使用情况的`m x n`列表不是必要的。因为`ascii`表中，`a~z`和`A~Z`的字符集中在`0~127`的上半区。所以用过的字符用`256`做掩码，用`^`位操作，`board[i][j] ^ 256`取得任意字符的补码，不属于任何英语字符，表示已经用过，避免和其他字符匹配。而且不丢失信息，匹配失败后，再做一次补码即可恢复字符。

直接在`board`数组上标记导致并发安全方面的隐患。

#### 代码
```java
public class Solution {
    public boolean exist(char[][] board, String word) {
        if (board.length == 0 || word.length() == 0) { return false; } // 0 x 0 array
        if (board.length == 1 && board[0].length == 1) { // 1 x 1 array
            return (word.length() == 1 && word.charAt(0) == board[0][0]);
        }
        char[] letters = word.toCharArray();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (scan(board,letters,0,i,j)) { return true; }
            }
        }
        return false;
    }
    public boolean scan(char[][] board, char[] letters, int cursor, int row, int col) {
        if (cursor == letters.length) { return true; } // end ?
        if (board[row][col] != letters[cursor]) { return false; } // verify current ?
        board[row][col] = (char)((int)board[row][col] ^ 256); // 取补码，标记已用
        /* look around */
        if (row > 0) { // not first row
            if (scan(board,letters,cursor+1,row-1,col)) { return true; } // check higher row
        }
        if (row < board.length-1) { // not last row
            if (scan(board,letters,cursor+1,row+1,col)) { return true; } // check lower row
        }
        if (col > 0) { // not first colum
            if (scan(board,letters,cursor+1,row,col-1)) { return true; } // check left column
        }
        if (col < board[0].length-1) { // not last column
            if (scan(board,letters,cursor+1,row,col+1)) { return true; } // check right column
        }
        board[row][col] = (char)((int)board[row][col] ^ 256); // 注意回溯，一条路径一旦失败，所有标记used的全部改回来。
        return false;
    }
}
```

#### 结果
少用一个额外`m x n`的空间，真的会快20倍吗？
![word-search-2](/images/leetcode/word-search-2.png)


### 整理代码，DFS(Depth First Search)深度优先
可以将递归再往下推进一层，我不用判断点是不是出界，把这个判断交个`base case`. 一行判断出界的代码，替代了原先四个方向上的四次判断。另一个好处是不用特别探讨`1 x 1`规模数组的特殊情况。因为原先的代码，四个方向上都不会尝试扩展，只能用代码拿出来特殊讨论。现在就算四个方向都是错的，还是会出去尝试，并得出错误的结果。

#### 不剪枝
有的时候，一不注意，可能觉得等四个方向都得出结论，再统一判断没什么关系。实际上效率可能就差了`100`倍。
```java
public boolean exist(char[][] board, String word) {
    if (board.length == 0) { return false; }
    char[] letters = word.toCharArray();
    for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[i].length; j++) {
            if (scan(board,letters,0,i,j)) { return true; }
        }
    }
    return false;
}
public boolean scan(char[][] board, char[] letters, int cursor, int row, int col) {
    if (cursor == letters.length) { return true; }
    if (row < 0 || row == board.length || col < 0 || col == board[row].length) { return false; } // 出界
    if (board[row][col] != letters[cursor]) { return false; }
    board[row][col] = (char)((int)board[row][col] ^ 256);
    boolean left = scan(board,letters,cursor+1,row,col-1);
    boolean right = scan(board,letters,cursor+1,row,col+1);
    boolean high = scan(board,letters,cursor+1,row-1,col);
    boolean low = scan(board,letters,cursor+1,row+1,col);
    boolean res = left || right || high || low;
    board[row][col] = (char)((int)board[row][col] ^ 256); // 四个方向都得出结果，再得出这一点的结论。
    return res;
}
```

#### 结果
这里没有剪枝，超时。也就是`350ms`开外。
![word-search-3](/images/leetcode/word-search-3.png)

#### 剪枝
四个方向只要有一个方向走通，就宣布成功。

```java
public class Solution {
    public boolean exist(char[][] board, String word) {
        if (board.length == 0) { return false; }
        char[] letters = word.toCharArray();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (scan(board,letters,0,i,j)) { return true; }
            }
        }
        return false;
    }
    public boolean scan(char[][] board, char[] letters, int cursor, int row, int col) {
        if (cursor == letters.length) { return true; }
        if (row < 0 || row == board.length || col < 0 || col == board[row].length) { return false; } // 出界
        if (board[row][col] != letters[cursor]) { return false; }
        board[row][col] = (char)((int)board[row][col] ^ 256);
        if (scan(board,letters,cursor+1,row,col-1)) { return true; }
        if (scan(board,letters,cursor+1,row,col+1)) { return true; }
        if (scan(board,letters,cursor+1,row-1,col)) { return true; }
        if (scan(board,letters,cursor+1,row+1,col)) { return true; }
        board[row][col] = (char)((int)board[row][col] ^ 256);
        return false;
    }
}
```

#### 结果
`9ms`。比不剪枝差了整整`40`倍。
![word-search-4](/images/leetcode/word-search-4.png)
