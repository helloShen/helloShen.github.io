---
layout: post
title: "Leetcode - Algorithm - Stone Game "
date: 2018-08-02 00:29:34
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
Alex and Lee play a game with piles of stones.  There are an even number of piles arranged in a row, and each pile has a positive integer number of stones piles[i].

The objective of the game is to end with the most stones.  The total number of stones is odd, so there are no ties.

Alex and Lee take turns, with Alex starting first.  Each turn, a player takes the entire pile of stones from either the beginning or the end of the row.  This continues until there are no more piles left, at which point the person with the most stones wins.

Assuming Alex and Lee play optimally, return True if and only if Alex wins the game.



Example 1:
```
Input: [5,3,4,5]
Output: true
Explanation:
Alex starts first, and can only take the first 5 or the last 5.
Say he takes the first 5, so that the row becomes [3, 4, 5].
If Lee takes 3, then the board is [4, 5], and Alex takes 5 to win with 10 points.
If Lee takes the last 5, then the board is [3, 4], and Alex takes 4 to win with 9 points.
This demonstrated that taking the first 5 was a winning move for Alex, so we return true.
```

Note:
* 2 <= piles.length <= 500
* piles.length is even.
* 1 <= piles[i] <= 500
* sum(piles) is odd.

### 贪心的动态规划
既然每轮每个人都有两种选择，要么取首元素，要么取尾元素，那就可以递归出两个子问题。然后这个问题适用于 **贪心算法**，即两个子问题的解，我只要保留对我更有利（更大）的那个解即可，另外一个可以忽略。为了避免重复计算的子问题，可以用一个表记录已经解决过的子问题解（动态规划）。

#### 代码
```java
class Solution {

    public boolean stoneGame(int[] piles) {
        p = piles;
        int len = piles.length;
        dp = new int[len][len];
        return dp(0,piles.length-1) > 0;
    }

    private int[] p;        //copy of piles
    private int[][] dp;     //solved subproblems

    private int dp(int lo, int hi) {
        if (lo == hi) { return 0; }
        if (dp[lo][hi] != 0) { return dp[lo][hi]; }
        int res = 0;
        if ((hi - lo + 1) % 2 == 0) {
            res = Math.max(dp(lo+1,hi) + p[lo], dp(lo,hi-1) + p[hi]);
        } else {
            res = Math.min(dp(lo+1,hi) - p[lo], dp(lo,hi-1) - p[hi]);
        }
        dp[lo][hi] = res;
        return res;
    }

}
```

自底向上的动态规划
```java
class Solution {
    public boolean stoneGame(int[] piles) {
        int len = piles.length;
        int[][] dp = new int[len][len];
        for (int i = 0; i < len; i++) dp[i][i] = -piles[i];
        for (int dis = 1, isAlex = 1; dis < len; dis++, isAlex = -isAlex) {
            for (int lo = 0, hi = lo + dis; lo < len - dis; lo++) {
                int takeHead = piles[lo] * isAlex + dp[lo + 1][hi];
                int takeTail = piles[hi] * isAlex + dp[lo][hi - 1];
                dp[lo][hi] = Math.max(takeHead, takeTail);
            }
        }
        return dp[0][len - 1] > 0;
    }
}
```

#### 结果
![stone-game-1](/images/leetcode/stone-game-1.png)


### Alex必胜
递归到最简单的子问题，“只有2个数字”，Alex只要取更大的那个可以必胜。回归到上一个子问题“只有4个数字”的情况，Alex既然知道了2个数时他保证能拿到更大的数，他也能保证必胜。
![stone-game-a](/images/leetcode/stone-game-a.png)

#### 代码
```java
public boolean stoneGame(int[] piles) {
    return true;
}    
```

#### 结果
![stone-game-2](/images/leetcode/stone-game-2.png)
