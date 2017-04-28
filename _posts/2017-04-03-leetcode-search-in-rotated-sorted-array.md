---
layout: post
title: "Leetcode - Algorithm - Search In Rotated Sorted Array "
date: 2017-04-03 17:06:07
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","binary search"]
level: "medium"
description: >
---

### 题目
Suppose an array sorted in ascending order is rotated at some pivot unknown to you beforehand.
```
0 1 2 4 5 6 7 -> 4 5 6 7 0 1 2
```
You are given a target value to search. If found in the array return its index, otherwise return -1.

You may assume no duplicate exists in the array.

### 单指针遍历 $$O(n)$$
最简单的单指针遍历数组。两个小区别，
1. 数组越界要循环回到另一个端点。
2. 每次都需要检测`pivot`点。越过`pivot`点，结束程序。

#### 代码
```java
public class Solution {
    public int search(int[] nums, int target) {
        if (nums.length == 0) { return -1; }
        if (nums.length == 1) { return (target == nums[0])? 0 : -1; }
        int cursor = 0, next = 0;
        while (true) {
            if (nums[cursor] == target) { return cursor; }
            if (nums[cursor] < target) {
                next = (cursor + 1) % nums.length;
                if (nums[next] < nums[cursor] || nums[next] > target) { return -1; }
            }
            if (nums[cursor] > target) {
                next = (cursor - 1 < 0)? cursor - 1 + nums.length : cursor - 1;
                if (nums[next] > nums[cursor] || nums[next] < target) { return -1; }
            }
            cursor = next;
        }
    }
}
```

#### 结果
![search-in-rotated-sorted-array-1](/images/leetcode/search-in-rotated-sorted-array-1.png)

### Binary Search $$O(\log_{}{n})$$
两步走：
1. 用Binary Search找到那个`pivot`点，就是最小的那个数字。
2. 把`pivot`看做是当前数组在标准数组上的一个偏移值。做一个在标准数组上做一个Binary Search。

考虑数组`[10,12,2,4,6,8]`，首先找到`pivot`点，`2`是最小的那个数字。
```
2是最小的数字。
[10,12,>2<,4,6,8]
```
所以，标准数组就是：
```java
[2,4,6,8,10,12]  // 标准数组
```
我们的`[10,12,2,4,6,8]`可以看做是在标准数组向右偏移了`2`个位置，这个`2`就是`pivot`值。

所以整个二分查找都是在假想的标准数组上进行，到`nums`上查找数字的时候，再把下标加上`pivot`偏移值，翻译成在`nums`上的下标。


#### 代码
```java
public class Solution {
    public int search(int[] nums, int target) {
        if (nums.length == 0) { return -1; }
        if (nums.length == 1) { return (target == nums[0])? 0 : -1; }
        int pivot = pivot(nums,0,nums.length-1);
        return searchRecur(nums,0,nums.length-1,target,pivot);
    }
    public int pivot(int[] nums, int low, int high) { // 找最小数(转动的那个点)
        if (high == low) { return low; }
        int median = low + (high - low) / 2;
        if (nums[median] < nums[high]) {
            return pivot(nums,low,median);
        } else {
            return pivot(nums,median+1,high);
        }
    }
    public int searchRecur(int[] nums, int low, int high, int target, int rotate) { //正常的二分查找
        if (low > high) { return -1; }
        int median = low + (high - low) / 2;
        int medianRotated = (median + rotate) % nums.length; // 根据偏移值找到点在数组上的实际位置
        if (nums[medianRotated] < target) {
            return searchRecur(nums,median+1,high,target,rotate);
        } else if (nums[medianRotated] > target) {
            return searchRecur(nums,low,median-1,target,rotate);
        } else {
            return medianRotated;
        }
    }
}
```

#### 结果
银弹！
![search-in-rotated-sorted-array-2](/images/leetcode/search-in-rotated-sorted-array-2.png)

### 也可以直接在数组上二分查找 $$O(\log_{}{n})$$
每次迭代还是找中位数，然后和最高位比较，如果`nums[mid] == target`，就找到了，如果不相等，分为两种情况，
1. 如果像`[7,0,1,3,5,6]`这样，`nums[mid] < nums[hi]`，说明右半边`1,3,5,6`是升序的。
    * 如果`1 < target <= 6`，说明目标在`3,5,6`里能找到，所以放弃左半边`7,0,1`。
    * 否则，说明目标不在`1,3,5,6`，只需要到`7,0,1`里找。
2. 如果像`[1,3,5,6,0]`这样，`nums[mid] > nums[hi]`，说明左半边`1,3,5`是升序的。
    * 如果`1 <= target < 5`，目标在`1,3`里，放弃右半边`5,6,0`。
    * 否则，目标不在`1,3,5`里，在剩下的`6,0`里找。

#### 代码
```java
public class Solution {
    public int search(int[] nums, int target) {
        if (nums.length == 0) { return -1; }
        int lo = 0, hi = nums.length-1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) { return mid; }
            if (nums[mid] < nums[hi]) { // right half is sorted, ex:[7,0,1,3,5,6]
                if (nums[mid] < target && target <= nums[hi]) { // target can only be in right half
                    lo = mid + 1;
                } else { // target is not in right half
                    hi = mid - 1;
                }
            } else { // left half is sorted, ex:[1,3,5,6,0]
                if (nums[lo] <= target && target < nums[mid]) { // target can only be in left half
                    hi = mid - 1;
                } else { // target is not in left half
                    lo = mid + 1;
                }
            }
        }
        return -1;
    }
}
```

#### 结果
![search-in-rotated-sorted-array-3](/images/leetcode/search-in-rotated-sorted-array-3.png)
