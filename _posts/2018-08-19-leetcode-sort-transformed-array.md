---
layout: post
title: "Leetcode - Algorithm - Sort Transformed Array "
date: 2018-08-19 14:46:51
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","two pointers"]
level: "medium"
description: >
---

### 题目
Given a sorted array of integers nums and integer values a, b and c. Apply a quadratic function of the form f(x) = ax2 + bx + c to each element x in the array.

The returned array must be in sorted order.

Expected time complexity: O(n)

Example 1:
```
Input: nums = [-4,-2,2,4], a = 1, b = 3, c = 5
Output: [3,9,15,33]
```

Example 2:
```
Input: nums = [-4,-2,2,4], a = -1, b = 3, c = 5
Output: [-23,-5,1,7]
```

### 二次函数的极值问题
首先二次函数的形状像个漏斗，漏斗的底部导数为0的位置为最小值。函数沿着最小值为中轴对称。
![sort-transformed-array-a](/images/leetcode/sort-transformed-array-a.png)

随着`a > 0`的时候是个正漏斗，`a < 0` 的时候是个倒漏斗。`a = 0`的时候，`b`起主导作用，退化成一次函数，斜率取决于`b`是正是负。
![sort-transformed-array-b](/images/leetcode/sort-transformed-array-b.png)

用两个指针指向数组首尾两端，逐渐向中心极值点靠拢就可以完成排序。
```
left            right
 |->           <-|
[9,    3,  15,  33]
     |
     | 极值x=-1.5
     |
[-4,  -2,   2,   4]
```

#### 代码
```java
class Solution {
    public int[] sortTransformedArray(int[] nums, int a, int b, int c) {
        if (nums == null) { return null; }
        int[] result = new int[nums.length];
        //calculate
        for (int i = 0; i < nums.length; i++) {
            result[i] = function(nums[i],a,b,c);
        }
        //sort
        int[] sorted = new int[nums.length];
        int p = (a <= 0)? 0 : nums.length - 1; //a > 0，正漏斗。 a <= 0，倒漏斗。
        for (int left = 0, right = nums.length-1; left <= right; ) {
            if (a <= 0) {
                sorted[p++] = (result[left] <= result[right])? result[left++] : result[right--];
            } else {
                sorted[p--] = (result[left] >= result[right])? result[left++] : result[right--];
            }
        }
        return sorted;
    }
    private int function(int x, int a, int b, int c) {
        return (a*x*x + b*x + c);
    }
}
```

#### 结果
![sort-transformed-array-1](/images/leetcode/sort-transformed-array-1.png)
