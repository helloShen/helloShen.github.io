---
layout: post
title: "Leetcode - Algorithm - Combination Sum Four "
date: 2018-09-02 16:22:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
Given an integer array with all positive numbers and no duplicates, find the number of possible combinations that add up to a positive integer target.

Example:
```
nums = [1, 2, 3]
target = 4

The possible combination ways are:
(1, 1, 1, 1)
(1, 1, 2)
(1, 2, 1)
(1, 3)
(2, 1, 1)
(2, 2)
(3, 1)

Note that different sequences are counted as different combinations.

Therefore the output is 7.
```

Follow up:
* What if negative numbers are allowed in the given array?
* How does it change the problem?
* What limitation we need to add to the question to allow negative numbers?



### 直观解
递归遍历所有可能组合，最后如果`remain == 0`，计数器加1. 因为数字可以重复使用，所以不存在一个`start`指针。每次递归都是从`nums`数组的首元素开始遍历。

假设数组长度为`l`，目标数是`t`，复杂度为`O(l^t)`。对于下面这个测试用例，
```
nums = [2,1,3]
target = 35

复杂度 = 3 ^ 35
```

#### 代码
```java
class Solution {
    public int combinationSum4(int[] nums, int target) {
        if (nums == null && nums.length == 0) {
            return 0;
        }
        count = 0;
        localNums = nums;
        helper(target);
        return count;
    }
    private int[] localNums;
    private int count;
    private void helper(int remain) {
        if (remain == 0) {
            count++;
            return;
        } else if (remain > 0) {
            for (int i = 0; i < localNums.length; i++) {
                helper(remain - localNums[i]);
            }
        }
    }
}
```

#### 结果
![combination-sum-four-1](/images/leetcode/combination-sum-four-1.png)


### 动态规划，O(numLen * target)
写这题动态规划，必须抓住一点：
> 数字可以重复使用，表明动态递归的时候，我们不会从原始`nums[]`数组中拿走数字。随着递归逐渐减小的只有`target`。

所以用来储存所有子问题解的内存结构就是一个简单的数组`int dp = new int[nums.length + 1]`。

#### 代码
```java
class Solution {
    public int combinationSum4(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        localNums = nums;
        dp = new int[target + 1];
        Arrays.fill(dp, -1);
        dp[0] = 1;
        return dp(target);
    }
    private int[] dp;
    private int[] localNums;
    private int dp(int remain) {
        if (remain < 0) {
            return 0;
        }
        if (dp[remain] >= 0) {
            return dp[remain];
        }
        int sum = 0;
        for (int i = 0; i < localNums.length; i++) {
            sum += dp(remain - localNums[i]);
        }
        dp[remain] = sum;
        return sum;
    }
}
```

#### 结果
![combination-sum-four-2](/images/leetcode/combination-sum-four-2.png)


### 自底向上的动态规划
理论上自底向上的动态规划更漂亮，代码更简洁。

#### 代码
```java
class Solution {
    public int combinationSum4(int[] nums, int target) {
        int[] dp = new int[target + 1];
        dp[0] = 1;
        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                int sub = i - num;
                dp[i] += (sub >= 0)? dp[sub] : 0;
            }
        }
        return dp[target];
    }
}
```

#### 结果
![combination-sum-four-3](/images/leetcode/combination-sum-four-3.png)
