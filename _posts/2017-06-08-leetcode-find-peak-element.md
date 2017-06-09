---
layout: post
title: "Leetcode - Algorithm - Find Peak Element "
date: 2017-06-08 22:07:02
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","binary search"]
level: "medium"
description: >
---

### 题目
A peak element is an element that is greater than its neighbors.

Given an input array where num[i] ≠ num[i+1], find a peak element and return its index.

The array may contain multiple peaks, in that case return the index to any one of the peaks is fine.

You may imagine that num[-1] = num[n] = -∞.

For example, in array `[1, 2, 3, 1]`, 3 is a peak element and your function should return the index number 2.

click to show spoilers.

Note:
Your solution should be in logarithmic complexity.

### $$O(n)$$ 老老实实遍历数组
肯定能找到。

#### 代码
```java
public class Solution {
    public int findPeakElement(int[] nums) {
        if (nums.length < 2) { return 0; }
        for (int i = 0; i < nums.length; i++) {
            if (i == 0) {
                if (nums[i] > nums[i+1]) { return 0; }
            } else if (i == nums.length-1) {
                if (nums[i-1] < nums[i]) { return i; }
            } else {
                if (nums[i-1] < nums[i] && nums[i] > nums[i+1]) { return i; }
            }
        }
        return -1; // never reached. because we can always find a peak when length > 1
    }
}
```

#### 结果
![find-peak-element-1](/images/leetcode/find-peak-element-1.png)


### 二分查找，$$O(\log{n})$$
这题能二分查找，基于下面两个重要推论，
1. 因为`num[i] ≠ num[i+1]`，以及`num[-1] = num[n] = -∞`，所有数组都至少有一个peak。
2. 只有两种最坏的情况只有一个peak:
    * 数组单调递增：则最右边末尾元素为peak。
    * 数组单调递减：则最左边起始元素为peak。

根据上面两个推论，构成了这题能使用二分查找丢弃一半分区的逻辑基础：
> 当`nums[mid] < nums[high]`，至少可以肯定右半边肯定有peak。

> 当`nums[mid] < nums[low]`，至少可以肯定左半边肯定有peak。

#### 代码
```java
public class Solution {
    public int findPeakElement(int[] nums) {
        if (nums.length < 2) { return 0; }
        int lo = 0, hi = nums.length-1;
        while (hi - lo > 1) {
            int mid = lo + (hi - lo) / 2;
            int val = nums[mid];
            if (val < nums[hi]){
                lo = mid;
            } else if (val < nums[lo]) {
                hi = mid;
            } else { // val >= nums[lo] && val >= nums[hi]
                lo++; hi--;
            }
        }
        return (nums[lo] > nums[hi])? lo : hi;
    }
}
```

#### 结果
![find-peak-element-2](/images/leetcode/find-peak-element-2.png)


### 上面的二分查找有一个思路更清晰的版本
比较中位数`nums[mid]`和它的下一个数`nums[mid+1]`。较小的那个半区可以丢弃，因为更大的那个半区肯定有peak。

图画出来，如下,
![find-peak-element-demo](/images/leetcode/find-peak-element-demo.png)

#### 代码
```java
public class Solution {
    public int findPeakElement(int[] nums) {
        if (nums.length < 2) { return 0; }
        int lo = 0, hi = nums.length-1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] < nums[mid+1]) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;    
    }
}
```

#### 结果
![find-peak-element-3](/images/leetcode/find-peak-element-3.png)
