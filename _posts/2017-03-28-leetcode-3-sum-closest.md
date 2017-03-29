---
layout: post
title: "Leetcode - Algorithm - 3 Sum Closest "
date: 2017-03-28 20:35:15
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode"]
level: "medium"
description: >
---

### 题目
Given an array S of n integers, find three integers in S such that the sum is closest to a given number, target. Return the sum of the three integers. You may assume that each input would have exactly one solution.
```
    For example, given array S = {-1 2 1 -4}, and target = 1.

    The sum that is closest to the target is 2. (-1 + 2 + 1 = 2).
```

注意，如果无法获得结果（比如，数组长度小于3），直接返回target。

### 继承3Sum的先排序，用第一个数字，双向查找后两个数字 $$O(n^2)$$
就当成标准的3Sum来做。维护一个和target的最小差和最终结果。每次获得和target更接近的结果，就更新最小差和最终结果。

#### 代码
```java
public class Solution {
    public int threeSumClosest(int[] nums, int target) {
        int result = target;
        if (nums == null || nums.length < 3) { return result; }
        Arrays.sort(nums);
        int main = 0;
        long minDiff = Math.abs((long)Integer.MIN_VALUE - (long)target);
        outerWhile:
        while (main < nums.length-2) {
            int low = main + 1;
            int high = nums.length - 1;
            while (low < high) {
                long sum = (long)nums[main] + (long)nums[low] + (long)nums[high];
                long tempDiff = sum - target;
                if (tempDiff == 0) { result = (int)sum; break outerWhile; }
                if (tempDiff < 0) {
                    while (low+1 < high && nums[low] == nums[low+1]) { low++; }
                    low++;
                }
                if (tempDiff > 0) {
                    while (low < high-1 && nums[high] == nums[high-1]) { high--; }
                    high--;
                }
                long absDiff = Math.abs(tempDiff);
                if (absDiff < minDiff) {
                    minDiff = absDiff;
                    result = (int)sum;
                }
            }
            while (main < nums.length-2 && nums[main] == nums[main+1]) { main++; }
            main++;
        }
        return result;
    }
}
```

#### 结果
已经是银弹！
![3-sum-closest-1](/images/leetcode/3-sum-closest-1.png)
