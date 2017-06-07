---
layout: post
title: "Leetcode - Algorithm - Find Minimum In Rotated Sorted Array "
date: 2017-06-07 17:01:13
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","binary search"]
level: "medium"
description: >
---

### 题目
Suppose an array sorted in ascending order is rotated at some pivot unknown to you beforehand.

(i.e., `0 1 2 4 5 6 7` might become `4 5 6 7 0 1 2`).

Find the minimum element.

You may assume no duplicate exists in the array.

### 遍历整个数组肯定能找到，复杂度 $$O(n)$$
代码省略。

### 标准二分查找，复杂度 $$O(\log{n})$$
标准的二分查找问题。取 **中位数**，将数组分为左右两个部分。比较 **中位数** 和最高位的数字。
* 假设`0 1 2 4 5 6 7`的情况，`4 < 7`，说明 **最小数字在左半边**。
* 假设`4 5 6 7 0 1 2`的情况，`7 > 2`，说明 **最小数字在右半边**。

#### 代码
```java
public class Solution {
    public int findMin(int[] nums) {
        if (nums.length == 0) { return 0; }
        int lo = 0, hi = nums.length-1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] > nums[hi]) { lo = mid+1; continue; } // 在右半部分
            if (nums[mid] < nums[hi]) { hi = mid; } // 在左半部分
        }
        return nums[lo];
    }
}
```

#### 结果
![find-minimum-in-rotated-sorted-array-1](/images/leetcode/find-minimum-in-rotated-sorted-array-1.png)
