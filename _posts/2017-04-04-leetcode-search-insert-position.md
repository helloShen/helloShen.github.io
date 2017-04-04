---
layout: post
title: "Leetcode - Algorithm - Search Insert Position "
date: 2017-04-04 15:14:00
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","binary search"]
level: "easy"
description: >
---

### 题目
Given a sorted array and a target value, return the index if the target is found. If not, return the index where it would be if it were inserted in order.

You may assume no duplicates in the array.

Here are few examples.
```
[1,3,5,6], 5 → 2
[1,3,5,6], 2 → 1
[1,3,5,6], 7 → 4
[1,3,5,6], 0 → 0
```

### 递归二分查找 $$O(\log_{}{n})$$
典型的二分查找问题。

#### 代码
```java
public class Solution {
    public int searchInsert(int[] nums, int target) {
        if (nums.length == 0) { return 0; }
        return searchInsertRecur(nums,target,0,nums.length-1);
    }
    public int searchInsertRecur(int[] nums, int target, int low, int high) {
        if (low >= high) {
            return (nums[low] >= target)? low : ++low;
        }
        int median = (high + low) >> 1;
        if (nums[median] < target) {
            return searchInsertRecur(nums,target,median+1,high);
        } else if (nums[median] > target) {
            return searchInsertRecur(nums,target,low,median-1);
        } else {
            return median;
        }
    }
}
```

#### 结果
银弹！
![search-insert-position-1](/images/leetcode/search-insert-position-1.png)


### 迭代版二分查找 $$O(\log_{}{n})$$
不用递归，迭代的代码看上去更简洁一点。

#### 代码
```java
public class Solution {
    public int searchInsert(int[] nums, int target) {
        if (nums.length == 0) { return 0; }
        int low = 0, high = nums.length-1;
        while (low < high) {
            int median = (low + high) >> 1;
            if (nums[median] < target) { low = median + 1; }
            if (nums[median] > target) { high = median - 1; }
            if (nums[median] == target) { return median; }
        }
        return (nums[low] >= target)? low : ++low;
    }
}
```

#### 结果
和递归一样！
![search-insert-position-2](/images/leetcode/search-insert-position-2.png)


### 更简洁的 base case
不把`low == high`作为`base case`。继续往下走一层。把`low > high`作为终结条件。
![search-insert-position-0](/images/leetcode/search-insert-position-0.png)

任何一种情况，都只要返回`low`下标即可。这是对终结条件的一种高度归纳。但也未必是每次二分查找都能归纳到这个程度的。

#### 代码
```java
public class Solution {
    public int searchInsert(int[] nums, int target) {
        int low = 0, high = nums.length-1;
        while (low <= high) {
            int median = (low + high) >> 1;
            if (nums[median] < target) { low = median + 1; }
            if (nums[median] > target) { high = median - 1; }
            if (nums[median] == target) { return median; }
        }
        return low;
    }
}
```

#### 结果
![search-insert-position-3](/images/leetcode/search-insert-position-3.png)
