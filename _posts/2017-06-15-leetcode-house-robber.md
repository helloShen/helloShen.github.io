---
layout: post
title: "Leetcode - Algorithm - House Robber "
date: 2017-06-15 19:19:32
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "easy"
description: >
---

### 题目
You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed, the only constraint stopping you from robbing each of them is that adjacent houses have security system connected and it will automatically contact the police if two adjacent houses were broken into on the same night.

Given a list of non-negative integers representing the amount of money of each house, determine the maximum amount of money you can rob tonight without alerting the police.

### 基本思路 - 回溯，分治，动态规划
这题一看，就是可以用`回溯算法`解决的类型。最基本的回溯算法，就是把问题抽象成在面对每一家的时候做一个决策：**偷或者不偷**。

这类可以用回溯算法解决的问题，经常可以把每一步都简化成：**这一步的选择** `+` **下一个子问题的最优解**，的形式。这样就可以用 **自底向上的动态规划** 来解决问题。

### 动态规划。递归版。复杂度 $$O(n^2)$$
这个问题的本质可以归纳成一个递归式：
> T(n) = Math.max(nums[n] + T(n+2), nums[n+1] + T(n+3))

`base case`是：
* 当`n >= nums.length`: `return 0;`
* 当`n == nums.length-1`: `return nums[nums.length-1]`

#### 代码
```java
public class Solution {
    public int rob(int[] nums) {
        int[] memo = new int[nums.length];
        return dp(nums,0,memo);
    }
    public int dp(int[] nums, int cur, int[] memo) {
        // base case
        if (cur >= nums.length) { return 0; }
        if (cur == nums.length - 1) { return nums[nums.length-1]; }
        // dp
        if (memo[cur] > 0) { return memo[cur]; }
        memo[cur] = Math.max(nums[cur] + dp(nums,cur+2,memo), nums[cur+1] + dp(nums,cur+3,memo));
        return memo[cur];
    }
}
```

#### 结果
![house-robber-1](/images/leetcode/house-robber-1.png)


### 动态规划。迭代版。复杂度 $$O(n)$$
因为是自底向上的动态规划，而且有一个备忘录。所以可以直接在备忘录上迭代。

空间复杂度 $$O(n)$$。

#### 代码
```java
public class Solution {
    public int rob(int[] nums) {
        if (nums.length == 0) { return 0; }
        int[] memo = new int[nums.length+3];
        memo[nums.length-1] = nums[nums.length-1];
        for (int i = nums.length-2; i >= 0; i--) {
            memo[i] = Math.max(nums[i] + memo[i+2], nums[i+1] + memo[i+3]);
        }
        return memo[0];
    }
}
```

#### 结果
![house-robber-2](/images/leetcode/house-robber-2.png)


### 时间复杂度 $$O(n)$$，空间复杂度 $$O(1)$$ 的变种
其实不需要用一个数组记录所有之前的结果。我们每一步用到的只有 **如果前一户没有偷的最大值**， 以及 **如果前一户偷了的最大值**。 只要维护着两个变量即可。

#### 代码
```java
public class Solution {
    public int rob(int[] nums) {
        if (nums.length == 0) { return 0; }
        int robPrevious = 0, notRobPrevious = 0;
        int cur = nums.length-1;
        do {
            int notRobCurrent = Math.max(robPrevious,notRobPrevious);
            int robCurrent = nums[cur--] + notRobPrevious;
            notRobPrevious = notRobCurrent;
            robPrevious = robCurrent;
        } while (cur >= 0);
        return Math.max(robPrevious,notRobPrevious);
    }
}
```

#### 结果
![house-robber-3](/images/leetcode/house-robber-3.png)
