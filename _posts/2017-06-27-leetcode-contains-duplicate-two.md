---
layout: post
title: "Leetcode - Algorithm - Contains Duplicate Two "
date: 2017-06-27 06:00:00
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","hash table"]
level: "easy"
description: >
---

### 题目
Given an array of integers and an integer k, find out whether there are two distinct indices i and j in the array such that `nums[i] = nums[j]` and the absolute difference between i and j is at most k.

### 用`HashSet`记录遇到过的数字空间
* 时间复杂度：$$O(n)$$
* 空间复杂度：$$O(n)$$

#### 代码
```java
public class Solution {
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        if (nums.length == 0) { return false; }
        Set<Integer> checkSet = new HashSet<>();
        for (int i = 0; i <= k && i < nums.length; i++) {
            if (!checkSet.add(nums[i])) { return true; }
        }
        for (int i = 0, j = k + 1; j < nums.length; i++,j++) {
            checkSet.remove(new Integer(nums[i]));
            if (!checkSet.add(nums[j])) { return true; }
        }
        return false;
    }
}
```

#### 结果
![contains-duplicate-two-1](/images/leetcode/contains-duplicate-two-1.png)
