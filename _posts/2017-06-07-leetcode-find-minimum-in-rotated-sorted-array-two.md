---
layout: post
title: "Leetcode - Algorithm - Find Minimum In Rotated Sorted Array Two "
date: 2017-06-07 17:20:22
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","binary search"]
level: "hard"
description: >
---

### 题目
Follow up for "Find Minimum in Rotated Sorted Array":
> What if duplicates are allowed?

Would this affect the run-time complexity? How and why?
Suppose an array sorted in ascending order is rotated at some pivot unknown to you beforehand.

(i.e., `0 1 2 4 5 6 7` might become `4 5 6 7 0 1 2`).

Find the minimum element.

The array may contain duplicates.

### 二分查找，最坏情况复杂度 $$O(n)$$
* 假设`4,4,4,4,4,1,3,3,3,3`的情况，`4 > 3`，说明 **最小数字在右半边**。
* 假设`4,4,4,4,4,1,3,3,3,3,3`的情况，`1 < 3`，说明 **最小数字在左半边**。
* 假设`4,4,4,4,4,1,4,4,4,4`的情况，`4 == 4`，这时候，**最小数有可能在左边，也可能在右边。** 唯一可以做的是：排除最高位的那个`4`，因为如果`4`是最小数，至少`mid`也等于`4`。

#### 代码
```java
public class Solution {
    public int findMin(int[] nums) {
        if (nums.length == 0) { return 0; }
        int lo = 0, hi = nums.length-1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] < nums[hi]) { hi = mid; continue; } // 最小值在左半边
            if (nums[mid] > nums[hi]) { lo = mid+1; continue; } // 最小值在右半边
            if (nums[mid] == nums[hi]) { hi--; } // 有可能在左半边，也可能在右半边。只能放心丢弃最高位的元素，因为至少中位数的值和他相等。
        }
        return nums[lo];
    }
}
```

#### 结果
银弹！
![find-minimum-in-rotated-sorted-array-two-1](/images/leetcode/find-minimum-in-rotated-sorted-array-two-1.png)
