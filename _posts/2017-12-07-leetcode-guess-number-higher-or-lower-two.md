---
layout: post
title: "Leetcode - Algorithm - Guess Number Higher Or Lower Two "
date: 2017-12-07 19:58:19
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
We are playing the Guess Game. The game is as follows:

I pick a number from 1 to n. You have to guess which number I picked.

Every time you guess wrong, I'll tell you whether the number I picked is higher or lower.

However, when you guess a particular number x, and you guess wrong, you pay $x. You win the game when you guess the number I picked.

Example:
```
n = 10, I pick 8.

First round:  You guess 5, I tell you that it's higher. You pay $5.
Second round: You guess 7, I tell you that it's higher. You pay $7.
Third round:  You guess 9, I tell you that it's lower. You pay $9.

Game over. 8 is the number I picked.
```

You end up paying $5 + $7 + $9 = $21.
Given a particular n ≥ 1, find out how much money you need to have to guarantee a win.


### 动态规划
本题思路其实是个最笨的办法：递归考虑每一种猜的可能。然后假设每次猜测的人都是最不走运的，也就是每次都猜到只剩最后一个数字才猜对，而且每一轮正确答案都在需要更贵成本一边（淘汰较少的数字，需要更多的后续猜测，猜测成本更高）。

然后取所有最终结果最少的一个。

过程中可以用一个二维数组记录所有解过的子问题，就是动态规划，

#### 代码
```java
class Solution {
    private int[][] memo = new int[1][1];

    public int getMoneyAmount(int n) {
        memo = new int[n+1][n+1];
        return helper(1,n);
    }
    private int helper(int lo, int hi) {
        if (lo >= hi) { return 0; }
        if (memo[lo][hi] != 0) { return memo[lo][hi]; }
        int res = Integer.MAX_VALUE;
        for (int i = lo; i <= hi; i++) {
            int local = i + Math.max(helper(lo,i - 1), helper(i + 1,hi));
            res = Math.min(res,local);
        }
        memo[lo][hi] = res;
        return res;
    }
}
```

#### 结果
![guess-number-higher-or-lower-two-1](/images/leetcode/guess-number-higher-or-lower-two-1.png)
