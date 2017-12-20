---
layout: post
title: "Leetcode - Algorithm - Subarray Sum Equals K "
date: 2017-12-19 19:11:57
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","map"]
level: "medium"
description: >
---

### 题目
Given an array of integers and an integer k, you need to find the total number of continuous subarrays whose sum equals to k.

Example 1:
```
Input:nums = [1,1,1], k = 2
Output: 2
```
Note:
* The length of the array is in range [1, 20,000].
* The range of numbers in the array is [-1000, 1000] and the range of the integer k is [-1e7, 1e7].


### 朴素预求和，$$O(n^2)$$
先把到每一位为止的总和预先算出来，比如我有`[1,1,1,1,1]`，预求和的结果就是`[1,2,3,4,5]`。我要求`[2,4]`区间的和，只需要做一次减法`sum[4] - sum[2-1]`。

#### 代码
```java
class Solution {
    public int subarraySum(int[] nums, int k) {
        for (int i = 1; i < nums.length; i++) {
            nums[i] = nums[i-1] + nums[i];
        }
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i; j < nums.length; j++) {
                int sum = nums[j] - ((i == 0)? 0 : nums[i-1]);
                if (sum == k) { count++; }
            }
        }
        return count;
    }
}
```

#### 结果
![subarray-sum-equals-k-1](/images/leetcode/subarray-sum-equals-k-1.png)


### 用一个`Map`储存预求和的结果，$$O(n)$$
用一个`Map`把每一位为止的预求和的结果出现的频率统计在一个`HashMap`里。

然后只需要遍历数组一次，复杂度是$$O(n)$$。

#### 代码
```java
class Solution {
    public int subarraySum(int[] nums, int k) {
        int res = 0;
        int sum = 0;
        Map<Integer,Integer> map = new HashMap<>();
        map.put(0,1);
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            res += map.getOrDefault(sum - k,0);
            map.put(sum,map.getOrDefault(sum,0)+1);
        }
        return res;
    }
}
```

#### 结果
![subarray-sum-equals-k-2](/images/leetcode/subarray-sum-equals-k-2.png)
