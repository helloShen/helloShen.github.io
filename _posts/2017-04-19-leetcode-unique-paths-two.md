---
layout: post
title: "Leetcode - Algorithm - Unique Paths Two "
date: 2017-04-19 20:04:53
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","dynamic programming"]
level: "medium"
description: >
---

### 主要收获
#### 被17亿次调用`dp()`方法打脸
本来以为只要方法本身操作不复杂（比如我$$O(1)$$的`dp()`），多调用几次方法没关系。其实因为动态绑定的因素，Java调用`非final`方法的开销很可观。

#### 练习写`自底向上`的动态规划
> `自顶向下`的带备忘录的动态规划虽然通过查表避免了重复计算子问题，但并没有避免子问题被提出。子问题的数量还是`指数级`的，方法的调用也是`指数级`的。

> 而`自底向上`的动态规划，每个子问题都只被提出一次，并计算一次。所以是`多项式复杂度`。


### 题目
Follow up for "Unique Paths":

Now consider if some obstacles are added to the grids. How many unique paths would there be?

An obstacle and empty space is marked as `1` and `0` respectively in the grid.

For example,
There is one obstacle in the middle of a `3x3` grid as illustrated below.
```
[
  [0,0,0],
  [0,1,0],
  [0,0,0]
]
```
The total number of unique paths is `2`.

Note: m and n will be at most `100`.

### 带备忘录的动态规划 指数级复杂度

#### 代码
```java
public class Solution {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int lineSize = obstacleGrid.length;
        if (lineSize == 0) { return 0; }
        int columnSize = obstacleGrid[0].length;
        if (columnSize == 0) { return 0; }
        int[][] memo = new int[lineSize][columnSize];
        return dp(obstacleGrid,memo,lineSize-1,columnSize-1,0,0);
    }
    public int dp(int[][] obs, int[][] memo, int lineSize, int columnSize, int m, int n) {
        if (obs[m][n] == 1) { return 0; }
        if (m == lineSize && n == columnSize) { return 1; }
        if (memo[m][n] != 0) { return memo[m][n]; }
        int rightCount = 0, downCount = 0;
        if (m < lineSize) { rightCount = dp(obs,memo,lineSize,columnSize,m+1,n); }
        if (n < columnSize) { downCount = dp(obs,memo,lineSize,columnSize,m,n+1); }
        int res = rightCount + downCount;
        memo[m][n] = res;
        return res;
    }
}
```

#### 结果
很奇怪为什么超时？已经用了备忘录了，每个点的可行路线只会计算一次。把测试用例在自己机器上运行，一共花了 **`103秒`**。 炸了。
![unique-paths-two-1](/images/leetcode/unique-paths-two-1.png)

用`Visual VM`检查了一下，发现`103秒`中有`81秒`花在了`1704611784次`（17亿次）对`dp()`方法的调用。
![unique-paths-two-vm](/images/leetcode/unique-paths-two-vm.png)

所以虽然每个点的路线数量我们都只计算了一次，从第二次开始都是直接通过查表获得数据。但17亿次查表操作也是一笔客观的开销。而且还要考虑到JVM调用方法时动态绑定的开销，单纯查表的动态规划也不能满足需求。

关于动态绑定造成的开销，下面这篇文章用一个`benchmarks`证实了因为多态的特性，在调用方法的过程中虚拟机需要到`vtable`表里查询具体是哪个类型实例的方法应该被调用。这会导致一定程度的开销。
[**《哪些因素影响Java调用的性能？》**](http://www.importnew.com/16202.html)



### 自底向上的动态规划 $$O(mn)$$
动态规划除了充满青春期躁动的`自顶向下`的指数级展开每条可能的路径之外，也可以真正脚踏实地的`自底向上`的计算每一个子问题的结果。这种`自底向上`的方法确保了 **每个子问题都只计算一次，而且只被访问一次。** 每个子问题的结果都直接由前一个子问题的结果推算出来。对一个`m x n`规模的二维数组，对每个格子都做一次计算，一共$$O(mn)$$的复杂度就能得到结果。

#### 代码
```java
public class Solution {
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int lineSize = obstacleGrid.length;
        if (lineSize == 0) { return 0; }
        int columnSize = obstacleGrid[0].length;
        if (columnSize == 0) { return 0; }
        int[][] memo = new int[lineSize][columnSize];
        for (int i = lineSize-1; i >= 0; i--) {
            for (int j = columnSize-1; j >= 0; j--) {
                if (obstacleGrid[i][j] == 1) { memo[i][j] = 0; continue; }
                if ( (i == lineSize-1) && (j == columnSize-1) ) { memo[i][j] = 1; continue; }
                if (i == lineSize-1) { memo[i][j] = memo[i][j+1]; continue; }
                if (j == columnSize-1) { memo[i][j] = memo[i+1][j]; continue; }
                memo[i][j] = memo[i+1][j] + memo[i][j+1];
            }
        }
        return memo[0][0];
    }
}
```

#### 结果
![unique-paths-two-2](/images/leetcode/unique-paths-two-2.png)
