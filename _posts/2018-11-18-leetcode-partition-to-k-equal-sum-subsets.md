---
layout: post
title: "Leetcode - Algorithm - Partition To K Equal Sum Subsets "
date: 2018-11-18 21:54:58
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search", "backtracking"]
level: "medium"
description: >
---

### 题目
Given an array of integers nums and a positive integer k, find whether it's possible to divide this array into k non-empty subsets whose sums are all equal.

Example 1:
```
Input: nums = [4, 3, 2, 3, 5, 2, 1], k = 4
Output: True
Explanation: It's possible to divide it into 4 subsets (5), (1, 4), (2,3), (2,3) with equal sums.
```

Note:
* 1 <= k <= len(nums) <= 16.
* 0 < nums[i] < 10000.

### 先排序，然后DFS回溯遍历所有组合
拿到`[4, 3, 2, 3, 5, 2, 1]`数列，要分成`4`组和相等。可以先验证和能否被`4`整除。否则直接淘汰。通过计算，发现可行，且个子串目标总和为`5`。
```
sum = 20, k = 4

sum of each subset = 20 / 4 = 5
```

问题转化成要在`[4, 3, 2, 3, 5, 2, 1]`里找所有和为`5`的组合，如果先排个序，会简单很多。
```
排序后：
[1, 2, 2, 3, 3, 4, 5]
```

一个基本思想是：
> 越大的数字灵活性越差，应该优先匹配。

比如留着一个`2`和一个`3`，比留一个`5`好得多。因为大不了`2 + 3`可以当`5`用，但`5`不能拆开当`2`,`3`用。

所以，匹配从数组的最右边最大数字开始。优先用掉最大的数字。遍历遵循DFS顺序，利用回溯算法可以做到。

#### 代码
```java
class Solution {
    public boolean canPartitionKSubsets(int[] nums, int k) {
        int sum = 0;
        for (int n : nums) sum += n;
        if (sum % k != 0) return false;
        int targetSum = sum / k;
        localNums = nums;
        taken = new boolean[nums.length];
        Arrays.sort(localNums);
        int idx = nums.length - 1;
        while (true) {
            while (idx >= 0 && taken[idx]) idx--;
            if (idx < 0) break;
            if (!backtracking(targetSum, idx)) return false;
        }
        return true;
    }

    private int[] localNums;
    private boolean[] taken;

    private boolean backtracking(int sum, int idx) {
        if (sum == 0) return true;
        if (idx < 0 || sum < 0 || taken[idx]) return false;
        int remain = sum - localNums[idx];
        taken[idx] = true;
        if (remain == 0 && idx == 0) return true;
        for (int i = idx - 1; i >= 0; i--) {
            if (backtracking(remain, i)) return true;
        }
        taken[idx] = false;
        return false;
    }
}
```

#### 结果
![partition-to-k-equal-sum-subsets-1](/images/leetcode/partition-to-k-equal-sum-subsets-1.png)
