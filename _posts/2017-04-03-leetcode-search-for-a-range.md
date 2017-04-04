---
layout: post
title: "Leetcode - Algorithm - Search For A Range "
date: 2017-04-03 22:19:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","array"]
level: "medium"
description: >
---

### 题目
Given an array of integers sorted in ascending order, find the starting and ending position of a given target value.

Your algorithm's runtime complexity must be in the order of O(log n).

If the target is not found in the array, return [-1, -1].

For example,
```
Given [5, 7, 7, 8, 8, 10] and target value 8,
return [3, 4].
```

### 二分查找分别找出目标数字的上界和下界 $$O(\log_{}{n})$$
分两步走。首先，没有找到目标数的时候，沿用普通的二分查找，丢弃一半元素。比如，`[5, 7, 7, 8, 8, 10]`里找`8`。
```
median = (5-0)/2 = 2

[5, 7, >7<, 8, 8, 10]

中位数 7 < 8, 丢弃前半部分，

[8, 8, 10]
```
如果始终没有找到`8`，就返回`[-1,-1]`，不进入第二步。

第二步，当找到`8`以后，分为两个递归，`searchLowBound()`找`8的下界`，`searchHighBound()`找`8的上界`。
```
找到一个`8`，
[8, >8<, 10]

分成两个递归：
[8,8]: 找第一个8。
[8,10]: 找最后一个8。
```

#### 代码
```java
public class Solution {
    public int[] searchRange(int[] nums, int target) {
        return search(nums,target,0,nums.length-1);
    }
    public int[] search(int[] nums, int target, int low, int high) {
        if (low > high) { return new int[]{-1,-1}; }
        int median = low + (high - low) / 2;
        if (nums[median] < target) {
            return search(nums,target,median+1,high);
        } else if (nums[median] > target) {
            return search(nums,target,low,median-1);
        } else {
            int lowBound = searchLowBound(nums,target,low,median);
            int highBound = searchHighBound(nums,target,median,high);
            return new int[]{lowBound,highBound};
        }
    }
    // 找目标数下界
    public int searchLowBound(int[] nums, int target, int lowBound, int lowCertain) {
        if (lowBound == lowCertain) { return lowCertain; }
        int median = (lowBound + lowCertain) / 2;
        if (nums[median] < target) {
            return searchLowBound(nums,target,median+1,lowCertain);
        } else { // nums[median] == target
            return searchLowBound(nums,target,lowBound,median);
        }
    }
    // 找目标数上界
    public int searchHighBound(int[] nums, int target, int highCertain, int highBound) {
        if (highBound == highCertain) { return highCertain; }
        int median = (highCertain + highBound + 1) / 2;
        if (nums[median] > target) {
            return searchHighBound(nums,target,highCertain,median-1);
        } else { // nums[median] == target
            return searchHighBound(nums,target,median,highBound);
        }
    }
}
```

#### 结果
![search-for-a-range-1](/images/leetcode/search-for-a-range-1.png)


### 用`lower_bound()`函数 $$O(\log_{}{n})$$
上面这个方法虽然可行，但逻辑过于复杂。

同样是用二分查找，但可以把问题抽象成执行两次同一个`lower_bound()`函数操作。`lower_bound()`是`c++`STL中的函数。
1. 返回数组中第一个大于或等于目标数的元素的位置。
2. 如果所有元素都小于目标数，返回数组的长度。

考虑数组`[5, 7, 7, 8, 8, 10]`，查找`8`。第一次查找第一个`>=8`的数字，
```
[5, 7, 7, >8<, 8, 10]  // 结果：3
```
第一次查找第一个`>=8+1`的数字，
```
[5, 7, 7, 8, 8, >10<]  // 结果：5
```
这两个动作，就夹逼出了所有8的范围`[3,4]`。

Java没有`lower_bound()`函数。自己用二分法写。


#### 代码
```java
public class Solution {
    public int[] searchRange(int[] nums, int target) {
        if (nums.length == 0) { return new int[]{-1,-1}; }
        int start = firstGreaterEqual(nums,target,0,nums.length-1);
        if (start == nums.length || nums[start] != target) { return new int[]{-1,-1}; }
        int end = firstGreaterEqual(nums,target+1,start,nums.length-1);
        return new int[]{start,end-1};
    }

    public int firstGreaterEqual(int[] nums, int target, int low, int high) {
        if (low == high) { return (nums[low] >= target)? low : nums.length; }
        int median = (low + high) >> 1;
        if (nums[median] < target) {
            return firstGreaterEqual(nums,target,median+1,high);
        } else {
            return firstGreaterEqual(nums,target,low,median);
        }
    }
}
```

#### 结果
银弹！这就是为什么SLT库为什么提供`lower_bound()`函数。
![search-for-a-range-2](/images/leetcode/search-for-a-range-2.png)
