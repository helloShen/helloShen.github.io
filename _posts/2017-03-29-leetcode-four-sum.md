---
layout: post
title: "Leetcode - Algorithm - Four Sum "
date: 2017-03-29 17:05:07
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode"]
level: "medium"
description: >
---

### 题目
Given an array S of n integers, are there elements a, b, c, and d in S such that a + b + c + d = target? Find all unique quadruplets in the array which gives the sum of target.

Note: The solution set must not contain duplicate quadruplets.

```
For example, given array S = [1, 0, -1, 0, -2, 2], and target = 0.

A solution set is:
[
  [-1,  0, 0, 1],
  [-2, -1, 1, 2],
  [-2,  0, 0, 2]
]
```
### 双向查找 $$O(n^3)$$
和3Sum一样，设置两个常规指针，两个双向指针。

#### 代码
```java
public class Solution {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 4) { return result; }
        Arrays.sort(nums);
        for (int first = 0; first < nums.length - 3; first++) {
            for (int second = first+1; second < nums.length -2; second++) {
                int low = second + 1;
                int high = nums.length - 1;
                while (low < high) {
                    int sum = nums[first] + nums[second] + nums[low] + nums[high];
                    if (sum == target) {
                        result.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{nums[first],nums[second],nums[low],nums[high]})));
                    }
                    if (sum <= target) {
                        while(low+1 < high && nums[low] == nums[low+1]) { low++; }
                        low++;
                    }
                    if (sum >= target) {
                        while(low < high-1 && nums[high] == nums[high-1]) { high--; }
                        high--;
                    }
                }
                while(second+1 < nums.length-2 && nums[second] == nums[second+1]) { second++; }
            }
            while(first+1 < nums.length-3 && nums[first] == nums[first+1]) { first++; }
        }
        return result;
    }
}
```

#### 结果
可以做地更好，但代码会变得比较复杂。
![four-sum-1](/images/leetcode/four-sum-1.png)
