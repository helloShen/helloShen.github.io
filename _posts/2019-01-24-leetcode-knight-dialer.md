---
layout: post
title: "Leetcode - Algorithm - Knight Dialer "
date: 2019-01-24 21:01:42
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
A chess knight can move as indicated in the chess diagram below:

![knight-chess-board](/images/leetcode/knight-chess-board.png)
![knight-chess-board-keypad](/images/leetcode/knight-chess-board-keypad.png)

This time, we place our chess knight on any numbered key of a phone pad (indicated above), and the knight makes N-1 hops.  Each hop must be from one key to another numbered key.

Each time it lands on a key (including the initial placement of the knight), it presses the number of that key, pressing N digits total.

How many distinct numbers can you dial in this manner?

Since the answer may be large, output the answer modulo `10^9 + 7`.

Example 1:
```
Input: 1
Output: 10
```

Example 2:
```
Input: 2
Output: 20
```

Example 3:
```
Input: 3
Output: 46
```

Note:
* 1 <= N <= 5000

### 动态规划
这题关键点在于从任何一个数字键，跳一步的跳法都是固定的。如下表，
```
0 -> {4, 6}
1 -> {6, 8}
2 -> {7, 9}
3 -> {4, 8}
4 -> {3, 9, 0}
5 -> {}
6 -> {1, 7, 0}
7 -> {2, 6}
8 -> {1, 3}
9 -> {2, 4}
```

所以，以从`1`出发为例，从`1`出发跳`3`下记做`1[3]`，那一定是跳到`6`和`8`，然后再往后跳2下，所以可以分裂成两个子问题，
```
1[3] = 6[2] + 8[2]

则，
1[N] = 6[N - 1] + 8[N - 1]
```

所以只要先计算出所有子问题`X[N - 1]`，就可以以此为根据，算出所有下一层`X[N]`。

#### 代码
```java
class Solution {
    private int max = 1000000007;
    private int[][] hopsTable = new int[][]{
        {4, 6},
        {6, 8},
        {7, 9},
        {4, 8},
        {3, 9, 0},
        {},
        {1, 7, 0},
        {2, 6},
        {1, 3},
        {2, 4}
    };

    public int knightDialer(int N) {
        int [] curr = new int[10];
        Arrays.fill(curr, 1);
        for (int i = 1; i < N; i++) {
            long[] next = new long[10];
            for (int n = 0; n < hopsTable.length; n++) {
                for (int p = 0; p < hopsTable[n].length; p++) {
                    if (hopsTable[n][p] >= 0) {
                        next[n] += curr[hopsTable[n][p]];
                    }
                }
            }
            curr = mod(next);
        }
        return sum(curr);
    }

    private int[] mod(long[] arr) {
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = (int) (arr[i] % max);
        }
        return res;
    }
    private int sum(int[] arr) {
        long sum = 0;
        for (int n : arr) sum += n;
        return (int) (sum % max);
    }
}
```

#### 结果
![knight-dialer-1](/images/leetcode/knight-dialer-1.png)
