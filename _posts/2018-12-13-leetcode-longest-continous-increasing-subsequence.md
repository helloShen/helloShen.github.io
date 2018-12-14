---
layout: post
title: "Leetcode - Algorithm - Longest Continous Increasing Subsequence "
date: 2018-12-13 20:25:07
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given an unsorted array of integers, find the length of longest continuous increasing subsequence (subarray).

Example 1:
```
Input: [1,3,5,4,7]
Output: 3
Explanation: The longest continuous increasing subsequence is [1,3,5], its length is 3.
Even though [1,3,5,7] is also an increasing subsequence, it's not a continuous one where 5 and 7 are separated by 4.
```

Example 2:
```
Input: [2,2,2,2,2]
Output: 1
Explanation: The longest continuous increasing subsequence is [2], its length is 1.
```

Note: Length of the array will not exceed 10,000.

### 开个窗口遍历数组
用两个指针`lo`和`hi`维持一个窗口，遍历数组即可。

#### 代码
```java
class Solution {
    public int findLengthOfLCIS(int[] nums) {
        if (nums.length == 0) return 0;
        int maxCount = 1;
        for (int lo = 0, hi = 1; lo < nums.length; lo = hi, hi = lo + 1) {
            while (hi < nums.length && nums[hi] > nums[hi - 1]) hi++;
            maxCount = Math.max(maxCount, hi - lo);
        }
        return maxCount;
    }
}
```

#### 结果
![longest-continous-increasing-subsequence-1](/images/leetcode/longest-continous-increasing-subsequence-1.png)
