---
layout: post
title: "Leetcode - Algorithm - Android Unlock Patterns "
date: 2018-08-13 19:54:54
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","backtracking"]
level: "medium"
description: >
---

### 题目
Given an Android 3x3 key lock screen and two integers m and n, where 1 ≤ m ≤ n ≤ 9, count the total number of unlock patterns of the Android lock screen, which consist of minimum of m keys and maximum n keys.
![android-unlock-patterns-a](/images/leetcode/android-unlock-patterns-a.png)

Rules for a valid pattern:
1. Each pattern must connect at least m keys and at most n keys.
2. All the keys must be distinct.
3. If the line connecting two consecutive keys in the pattern passes through any other keys, the other keys must have previously selected in the pattern. No jumps through non selected key is allowed.
4. The order of keys used matters.

Explanation:
```
| 1 | 2 | 3 |
| 4 | 5 | 6 |
| 7 | 8 | 9 |
```
Invalid move: `4 - 1 - 3 - 6`
Line 1 - 3 passes through key 2 which had not been selected in the pattern.

Invalid move: `4 - 1 - 9 - 2`
Line 1 - 9 passes through key 5 which had not been selected in the pattern.

Valid move: `2 - 4 - 1 - 3 - 6`
Line 1 - 3 is valid because it passes through key 2, which had been selected in the pattern

Valid move: `6 - 5 - 4 - 1 - 9 - 2`
Line 1 - 9 is valid because it passes through key 5, which had been selected in the pattern.

Example:
```
Input: m = 1, n = 1
Output: 9
```


### 直接按照题意
下一步的新节点必须满足一下两个条件之一，
1. 要么直接相连
2. 要么通过已有节点间接相连

这种探索问题用回溯DFS解决最直观。

#### 代码
```java
public int numberOfPatterns(int m, int n) {
    if (notInit()) { init(); }
    return memo[n] - memo[m-1];
}

private int[] memo;
private int[][] board;
private final int SIZE = 3;

//预先算好所有结果
private void init() {
    memo = new int[SIZE*SIZE+1];
    board = new int[SIZE][SIZE];
    backtracking(1);
    for (int i = 1; i < memo.length; i++) {
        memo[i] += memo[i-1];
    }
    // System.out.println(Arrays.toString(memo));
}
//惰性初始化
private boolean notInit() {
    return memo == null;
}
//暴力回溯遍历所有可能
private void backtracking(int num) {
    if (num > SIZE*SIZE) { return; }
    for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
            if (board[i][j] == 0 && (num == 1 || notJump(i,j))) {
                board[i][j] = 1;
                memo[num]++;
                backtracking(num+1);
                board[i][j] = 0;
            }
        }
    }
}
//判定某一点是否是孤立的（因为不能飞）
private boolean notJump(int i, int j) {
    for (int x = i-1; x <= i+1; x++) {
        for (int y = j-1; y <= j+1; y++) {
            if (x >= 0 && x < SIZE && y >= 0 && y < SIZE &&
               (x != i || y != j) && board[x][y] == 1) {
                return true;
            }
        }
    }
    return false;
}
```

### 隐含条件：Android手机`1 -> 8`不算触碰`4`和`5`
这个隐含条件题目里没有说清楚。如果像`1->8`这样的斜线不算跳跃的话，剩下跳跃仅剩下16组，
![android-unlock-patterns-b](/images/leetcode/android-unlock-patterns-b.png)

#### 代码
```java
class Solution {

    public int numberOfPatterns(int m, int n) {
        count = new int[SIZE+1];
        board = new int[SIZE+1];
        banned = new int[SIZE+1][SIZE+1];
        initBanned();
        backtracking(1,0,n);
        int res = 0;
        for (int i = m ; i <=n; i++) {
            res += count[i];
        }
        return res;
    }

    private int[] count;
    private int[] board;
    private int[][] banned;
    private final int SIZE = 9;

    //枚举所有跳跃的情况（真的只有16种，因为类似1->8这类在Android里不算跳跃）
    private void initBanned() {
        //横
        banned[1][3] = 2;
        banned[3][1] = 2;
        banned[7][9] = 8;
        banned[9][7] = 8;
        //竖
        banned[1][7] = 4;
        banned[7][1] = 4;
        banned[3][9] = 6;
        banned[9][3] = 6;
        //中
        banned[2][8] = 5;
        banned[8][2] = 5;
        banned[4][6] = 5;
        banned[6][4] = 5;
        //斜
        banned[3][7] = 5;
        banned[7][3] = 5;
        banned[1][9] = 5;
        banned[9][1] = 5;
    }
    //暴力回溯遍历所有可能
    private void backtracking(int len, int last, int max) {
        if (len > max) { return; }
        for (int i = 1; i <= SIZE; i++) {
            int obstacle = banned[last][i];
            if (board[i] == 0 &&
                (obstacle == 0 || board[obstacle] == 1)) {
                board[i] = 1;
                count[len]++;
                backtracking(len+1,i,max);
                board[i] = 0;
            }
        }
    }
}
```

#### 结果
![android-unlock-patterns-1](/images/leetcode/android-unlock-patterns-1.png)
