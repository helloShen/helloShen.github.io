---
layout: post
title: "Leetcode - Algorithm - Coin Change "
date: 2017-09-21 20:17:53
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","depth first search"]
level: "medium"
description: >
---

### 题目
You are given coins of different denominations and a total amount of money amount. Write a function to compute the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return -1.

Example 1:
```
coins = [1, 2, 5], amount = 11
return 3 (11 = 5 + 5 + 1)
```

Example 2:
```
coins = [2], amount = 3
return -1.
```

Note:
You may assume that you have an infinite number of each kind of coin.

### DFS暴力递归肯定能解决问题
这是一个典型的 **贪婪算法** 不适用的问题。每次总是从价值最大的硬币开始拿不确保能找到最优解。所以退而求其次，用DFS遍历每一种可能的组合总是嫩解决问题的。

#### 代码
```java
class Solution {
    private static int[] coins = new int[0];
    private static int minCount = -1;
    public int coinChange(int[] coins, int amount) {
        this.coins = coins;
        minCount = -1;
        dfs(0,amount);
        return minCount;
    }
    private void dfs(int count, int amount) {
        if (amount < 0) { return; }
        if (amount == 0) { minCount = (minCount == -1)? count : Math.min(minCount,count); return; }
        ++count;
        for (int coin : coins) {
            dfs(count,amount-coin);
        }
    }
}
```

#### 结果
![coin-change-1](/images/leetcode/coin-change-1.png)


### 暴力DFS配合剪枝
如果我们有`1`分钱硬币，而总数`amount`又很大，这样就会得到由很多`1`分钱硬币凑起来的解，递归深度会很大。所以在递归的同时，随时监测当前解和全局临时最优解，如果已经不可能比全局临时最优解更好的时候，可以及时剪枝。

#### 代码
```java
class Solution {
    private static int[] coins = new int[0];
    private static int minCount = -1;
    public int coinChange(int[] coins, int amount) {
        Arrays.sort(coins);
        this.coins = coins;
        minCount = -1;
        dfs(0,amount);
        return minCount;
    }
    private void dfs(int count, int amount) {
        if (minCount != -1 && count >= minCount) { return; }    // 及时剪枝
        if (amount < 0) { return; }
        if (amount == 0) { minCount = (minCount == -1)? count : Math.min(minCount,count); return; }
        ++count;
        for (int i = coins.length-1; i >= 0; i--) {             // 为了配合剪枝，从大到小遍历
            dfs(count,amount-coins[i]);
        }
    }
}
```

#### 结果
虽然剪枝了，但暴力遍历的复杂度还是太高了。
![coin-change-2](/images/leetcode/coin-change-2.png)


### 动态规划
拿`[1,2,5]`为例，如果目标数是`100`的话，
> T(100) = Math.min(T(100-1), T(100-2), T(100-5)) + 1

所以很快地通过子问题的解，得到更高层次的当前问题的解。当然还需要处理一点细节，就是当子问题的索引小于零的时候，说明无解。当某个子问题无解，通过这个子问题引申来的解也无意义。

#### 代码
```java
class Solution {
    private static int[] coins = new int[0];
    private static int[] memo = new int[0];
    public int coinChange(int[] coins, int amount) {
        this.coins = coins;
        memo = new int[amount+1];
        dp(amount);
        return memo[amount];
    }
    private void dp(int amount) {
        for (int i = 1; i <= amount; i++) {
            int localMin = -1;
            for (int coin : coins) {
                int subid = i - coin;
                if (subid >= 0) {
                    int sub = memo[subid];
                    int newVal = (sub == -1)? -1 : sub + 1;
                    if (newVal != -1) { localMin = (localMin == -1)? newVal : Math.min(localMin,newVal); }
                }
            }
            memo[i] = localMin;
        }
    }
}
```

#### 结果
![coin-change-3](/images/leetcode/coin-change-3.png)
