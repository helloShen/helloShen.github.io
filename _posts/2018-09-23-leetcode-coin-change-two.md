---
layout: post
title: "Leetcode - Algorithm - Coin Change Two "
date: 2018-09-23 18:19:51
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
You are given coins of different denominations and a total amount of money. Write a function to compute the number of combinations that make up that amount. You may assume that you have infinite number of each kind of coin.

Note: You can assume that
* 0 <= amount <= 5000
* 1 <= coin <= 5000
* the number of coins is less than 500
* the answer is guaranteed to fit into signed 32-bit integer

Example 1:
```
Input: amount = 5, coins = [1, 2, 5]
Output: 4
Explanation: there are four ways to make up the amount:
5=5
5=2+2+1
5=2+1+1+1
5=1+1+1+1+1
```

Example 2:
```
Input: amount = 3, coins = [2]
Output: 0
Explanation: the amount of 3 cannot be made up just with coins of 2.
```

Example 3:
```
Input: amount = 10, coins = [10]
Output: 1
```


### 比较直观的动态规划
考虑有`[1,2,5]`三种硬币，假设目标总额为`10`，记作`f(10)`，则存在以下递归，
> f(10) = f(10 - 1) + f(10 - 2) + f(10 - 5)

转化成代码（自底向上）如下所示。

#### 代码
```java
class Solution {
    public int change(int amount, int[] coins) {
        localCoins = coins;
        dpTable = new HashMap<Integer, Set<List<Integer>>>();
        dp(amount);
        return dpTable.get(amount).size();
    }

    private int[] localCoins;
    private Map<Integer, Set<List<Integer>>> dpTable;

    /** from bottom to top */
    private void dp(int amount) {
        List<Integer> emptySolution = new ArrayList<>();
        Set<List<Integer>> zeroSet = new HashSet<>();
        zeroSet.add(emptySolution);
        dpTable.put(0, zeroSet);
        for (int i = 1; i <= amount; i++) {
            Set<List<Integer>> set = new HashSet<>();
            dpTable.put(i, set);
            for (int coin : localCoins) {
                int subcase = i - coin;
                if (dpTable.containsKey(subcase)) {
                    Set<List<Integer>> subSet = dpTable.get(subcase);
                    for (List<Integer> subSolution : subSet) {
                        List<Integer> newSolution = new ArrayList<>(subSolution);
                        newSolution.add(firstEqualGreater(coin, newSolution), coin);
                        set.add(newSolution);
                    }
                }
            }
        }
    }
    /** return the index of first element equal to or greater than target value */
    private int firstEqualGreater(int target, List<Integer> list) {
        int lo = 0, hi = list.size() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (list.get(mid) < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }
}
```

#### 结果
![coin-change-two-1](/images/leetcode/coin-change-two-1.png)


### 不太直观的二维动态规划
还是以`[1,2,5]`三种硬币，目标总额为`10`为例，记作`f(3,10)`，念作使用前`3`种硬币（1,2,5），构成总额为`10`的方法。那么递归式如下，
> f(3, 10) = f(2, 10) + f(3, 10 - 5)

前半部分`f(2, 10)`表示不使用`5`分硬币，仅用`1,2`分硬币凑成`10`分的所有可能。后半部分`f(3, 10 - 5)`表示使用了`5`分硬币，可能的组合就和用`1,2,5`三种硬币凑总和为`5`的子问题解一样（因为都是在此基础上增加一枚`5`分硬币）。

所以可以用一个二维数组`int[][] dp`来记录所有可能，其中`dp[i][j]`表示：**使用前`i`种硬币，凑成总数`j`。** 这里的"basecase"是：不管用多少种硬币，总数为`0`的凑法只有一种，即`dp[i][0] = 1`。

#### 代码
```java
class Solution {
    public int change(int amount, int[] coins) {
        // dpMatrix[i][j]: number of combination to make up amount of "j" with the first "i" types of coin
        int[][] dpMatrix = new int[coins.length + 1][amount + 1];
        // basecase: always 1 combination to make up amount of "0"
        for (int i = 0; i < dpMatrix.length; i++) {
            dpMatrix[i][0] = 1;
        }
        // dp probagation
        for (int i = 1; i < dpMatrix.length; i++) {
            for (int j = 1; j < dpMatrix[0].length; j++) {
                int doNotUseCoinI = dpMatrix[i - 1][j];
                int coin = coins[i - 1];
                int useCoinI = (j - coin >= 0)? dpMatrix[i][j - coin] : 0;
                dpMatrix[i][j] = doNotUseCoinI + useCoinI;
            }
        }
        return dpMatrix[coins.length][amount];
    }
}
```

#### 结果
![coin-change-two-2](/images/leetcode/coin-change-two-2.png)


### 只用一维数组做动态规划表
考虑到动态规划表是一行一行更新的，每个节点的更新仅仅取决于上一行的节点，以及本行的前驱节点。所以只需要保留一行记录即可。

#### 代码
```java
class Solution {
    public int change(int amount, int[] coins) {
        // dp[i]: number of combination to make up amount of "i" with current bag of coins
        int[] dp = new int[amount + 1];
        // basecase: always 1 combination to make up amount of "0"
        dp[0] = 1;
        // dp probagation
        for (int coinIdx = 0; coinIdx < coins.length; coinIdx++) {
            for (int i = 1; i < dp.length; i++) {
                int doNotUseCoin = dp[i];
                int coin = coins[coinIdx];
                int useCoin = (i - coin >= 0)? dp[i - coin] : 0;
                dp[i] = doNotUseCoin + useCoin;
            }
        }
        return dp[amount];
    }
}
```

#### 结果
![coin-change-two-3](/images/leetcode/coin-change-two-3.png)

#### 继续简化代码
这个代码真的很漂亮了。
```java
class Solution {
    public int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;
        for (int coin : coins) {
            for (int i = coin; i < dp.length; i++) {
                dp[i] = dp[i] + dp[i - coin];
            }
        }
        return dp[amount];
    }
}
```

#### 结果
![coin-change-two-4](/images/leetcode/coin-change-two-4.png)
