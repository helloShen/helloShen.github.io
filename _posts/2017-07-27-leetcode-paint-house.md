---
layout: post
title: "Leetcode - Algorithm - Paint House "
date: 2017-07-27 13:53:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search","dynamic programming"]
level: "medium"
description: >
---

### 题目
There are a row of n houses, each house can be painted with one of the three colors: red, blue or green. The cost of painting each house with a certain color is different. You have to paint all the houses such that no two adjacent houses have the same color.

The cost of painting each house with a certain color is represented by a n x 3 cost matrix. For example, `costs[0][0]` is the cost of painting house 0 with color red; `costs[1][2]` is the cost of painting house 1 with color green, and so on... Find the minimum cost to paint all houses.

Note:
All costs are positive integers.

### 基本思路
基本判断DFS回溯是肯定可以的。但自底向上的动态规划（分治法）是不行的。因为每一层上来保留的当前最优解不保证是全局最优解。

但这种情况下，每一层不保留唯一的最优解，而是同时保留多个候选解，这样动态规划（分治法）就是可能的。

### DFS(回溯)
最简单的一个DFS尾递归推出去，每次到达最后一层，更新一下`minCost`。

#### 代码
```java
public class Solution {
    public int minCost(int[][] costs) {
        int[] minCost = new int[]{-1};
        dfs(costs,0,-1,0,minCost);
        return minCost[0];
    }
    public void dfs(int[][] costs, int row, int col, int costSoFar, int[] minCost) {
        if (row == costs.length) { minCost[0] = (minCost[0] < 0)? costSoFar : Math.min(minCost[0],costSoFar); return; }
        for (int i = 0; i < costs[0].length; i++) {
            if (col >= 0 && i == col) {  continue; }
            dfs(costs,row+1,i,costSoFar+costs[row][i],minCost);
        }
    }
}
```

#### 结果
这都超时，这题给的资源非常严格，看来非要动态规划才能过了。
![paint-house-1](/images/leetcode/paint-house-1.png)


### 自底向上的动态规划
既然只保留最优解的标准动态规划不行，那保留多个候选解行不行？ 行！
> 我们就保留到某一间房子为止，如果它刷红，黄，蓝三种油漆分别的最低价格。

假设只有两间房子，
```
              红  黄   蓝
房子2：        16, 7,  20
房子1：        11, 19, 12
```

则房子2刷红漆的最低花费取决于： **房子1刷黄漆或者刷蓝漆的最低价格中较小的那一个**

```
              红  黄   蓝
房子2：        16, 7,  20
房子1：        11, 19, 12

房子2红：16 + min(19,12) = 28
房子2黄：7 + min(11,12) = 18
房子2蓝：20 + min(11,19) = 31
```

#### 代码
```java
public class Solution {
    public int minCost(int[][] costs) {
        if (costs.length == 0) { return 0; }
        dp(costs,0);
        return Math.min(Math.min(costs[0][0],costs[0][1]),costs[0][2]);
    }
    public void dp(int[][] costs, int row) {
        if (row == costs.length-1) { return; }
        dp(costs,row+1);
        costs[row][0] += Math.min(costs[row+1][1],costs[row+1][2]);
        costs[row][1] += Math.min(costs[row+1][0],costs[row+1][2]);
        costs[row][2] += Math.min(costs[row+1][0],costs[row+1][1]);
    }
}
```

#### 结果
![paint-house-2](/images/leetcode/paint-house-2.png)
