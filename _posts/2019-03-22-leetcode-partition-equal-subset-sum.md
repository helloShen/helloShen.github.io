---
layout: post
title: "Leetcode - Algorithm - Partition Equal Subset Sum "
date: 2019-03-22 16:50:11
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming", "array"]
level: "meidum"
description: >
---

### 题目
Given a non-empty array containing only positive integers, find if the array can be partitioned into two subsets such that the sum of elements in both subsets is equal.

Note:
* Each of the array element will not exceed 100.
* The array size will not exceed 200.

Example 1:
```
Input: [1, 5, 11, 5]

Output: true

Explanation: The array can be partitioned as [1, 5, 5] and [11].
```

Example 2:
```
Input: [1, 2, 3, 5]

Output: false

Explanation: The array cannot be partitioned into equal sum subsets.
```

### 目标和问题
这题可以转化为检验一组数能否得到指定和的问题。这个和就是`nums`数组所有数字总和的一半。

### 动态规划
这样的问题，可以用动态规划解决。我们假设下面一组数`[1,5,11,6,...]`，

累加到`1`的情况，
```
要么取1，要么不取1.
  [0,1]     
    |
    1,5,11,6,...
```

再累加到`5`的情况，
```
要么不取5:  [0,1]
要么取5:   [0+5,1+5]
    [0,1]     
      |
    1,5,11,6,...
```

假设到第`i`个数的累加可能和是`[a,b,c,...]`，那么累加到第`i+1`个数，先保留之前所有的可能，然后在每个数的基础上再加上`nums[i+1]`。

#### 代码
```java
class Solution {
    public boolean canPartition(int[] nums){
        int sum = 0;
        for (int n : nums) sum += n;
        if (sum % 2 == 1) return false;
        int target = sum / 2;
        Set<Integer> sumSet = new HashSet<>();
        sumSet.add(0);
        for (int n : nums) {
            int[] newSumSet = new int[sumSet.size()];
            int p = 0;
            for (int s : sumSet) {
                int newSum = n + s;
                if (newSum == target) return true;
                newSumSet[p++] = newSum;
            }
            for (int newSum : newSumSet) sumSet.add(newSum);
        }
        return false;
    }
}
```

#### 结果
![partition-equal-subset-sum-1](/images/leetcode/partition-equal-subset-sum-1.png)


### 另外一种动态规划视角
如果数字的总和不会很大的情况下，我们可以用`boolean[sum + 1] dp`记录每一个`[0,sum]`的可能性，还是分2中情况，
1. 不取第`i`个数：`dp[x] = dp[x]`，即每个记录都不变。
2. 取第`i`个数：如果`dp[x - nums[i]] == true`，则`dp[x] = true`。

所以归纳成一行代码就是：
```
dp[i] = dp[i] || dp[i - num];
```

#### 代码
```java
class Solution {
    public boolean canPartition(int[] nums){
        int sum = 0;
        for (int n : nums) sum += n;
        if (sum % 2 == 1) return false;
        int target = sum / 2;
        boolean[] dp = new boolean[sum + 1];
        dp[0] = true;
        for (int num : nums) {
            for (int i = sum; i > 0; i--) {
                if (i >= num) dp[i] = dp[i] || dp[i - num];
            }
        }
        return dp[target];
    }
}
```

#### 结果
![partition-equal-subset-sum-2](/images/leetcode/partition-equal-subset-sum-2.png)
