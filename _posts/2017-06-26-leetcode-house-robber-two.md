---
layout: post
title: "Leetcode - Algorithm - House Robber Two "
date: 2017-06-26 21:25:25
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
Note: This is an extension of `House Robber`.

After robbing those houses on that street, the thief has found himself a new place for his thievery so that he will not get too much attention. This time, all houses at this place are arranged in a circle. That means the first house is the neighbor of the last one. Meanwhile, the security system for these houses remain the same as for those in the previous street.

Given a list of non-negative integers representing the amount of money of each house, determine the maximum amount of money you can rob tonight without alerting the police.

### 基本思路
典型的动态规划问题。问题的解决，可以抽象成在前一步子问题的最优解的基础上，做出当前的策略选择，得到这一步的最优解。

### 动态规划
根据`House Robber`的标准动态规划解法，
1. 分支一：偷了前面隔壁房(n+1)，这户就不能偷，所以 `max(n) = max(n+1)`
2. 分支二：没偷前面隔壁房(n+1)，这户可以偷，`max(n) = max(n+2)+nums[n]`

最终这一步就是在两种可能中做一个决策，取获利最多的那个。
> T(n) = max( T(n+1), T(n+2) + nums[n] )

代码如下，
```java
public int noCircleRob(int[] nums, int lo, int hi) {
    if (lo > hi) { return 0; }
    if (lo == hi) { return nums[lo]; }
    // assertion: hi - lo > 0
    int maxPre = Math.max(nums[hi],nums[hi-1]), maxBeforePre = nums[hi];
    for (int i = hi-2; i >= lo; i--) {
        int maxCurr = Math.max(maxPre,maxBeforePre + nums[i]);
        maxBeforePre = maxPre;
        maxPre = maxCurr;
    }
    return maxPre;
}
```

这题的变化在于，小偷偷了一圈，可能最优解既要偷第一户，又要偷最后一户，如果住户排成圈，就会报警。
```
   rob  rob
    |   |
    2,1,2       // 报警：第一户和最后一户是挨着的
```

解决这个问题很简单，搜索两遍就可以了。第一遍范围为`max1 = [0,len-2]`，第二遍范围`max2 = [1,len-1]`。 最后在两者间选出一个较大值，`max = max(max1,max2)`。
```
   lo1           hi1
    |             |
    1,2,3,4,5,6,7,8,9
      |             |
     lo2           hi2
```

#### 代码
```java
public class Solution {
    public int rob(int[] nums) {
        if (nums.length == 0) { return 0; }
        if (nums.length == 1) { return nums[0]; }
        return Math.max(noCircleRob(nums,0,nums.length-2), noCircleRob(nums,1,nums.length-1));
    }
    public int noCircleRob(int[] nums, int lo, int hi) {
        if (lo > hi) { return 0; }
        if (lo == hi) { return nums[lo]; }
        // assertion: hi - lo > 0
        int maxPre = Math.max(nums[hi],nums[hi-1]), maxBeforePre = nums[hi];
        for (int i = hi-2; i >= lo; i--) {
            int maxCurr = Math.max(maxPre,maxBeforePre + nums[i]);
            maxBeforePre = maxPre;
            maxPre = maxCurr;
        }
        return maxPre;
    }
}
```

#### 结果
![house-robber-two-1](/images/leetcode/house-robber-two-1.png)


### 两次循环的操作可以合并在一次循环中完成

#### 代码
```java
public class Solution {
    public int rob(int[] nums) {
        if (nums.length == 0) { return 0; }
        if (nums.length == 1) { return nums[0]; }
        // assertion: nums.length >= 2
        int preTakeLast = nums[nums.length-1], preNotTakeLast = nums[nums.length-2];
        int bPreTakeLast = nums[nums.length-1], bPreNotTakeLast = 0;
        for (int i = nums.length-3; i >= 0; i--) {
            int currTakeLast = Math.max(preTakeLast, bPreTakeLast + nums[i]);
            int currNotTakeLast = Math.max(preNotTakeLast, bPreNotTakeLast + nums[i]);
            bPreTakeLast = preTakeLast; preTakeLast = currTakeLast;
            bPreNotTakeLast = preNotTakeLast; preNotTakeLast = currNotTakeLast;
        }
        return Math.max(preNotTakeLast,bPreTakeLast);
    }
}
```

#### 结果
![house-robber-two-2](/images/leetcode/house-robber-two-2.png)
