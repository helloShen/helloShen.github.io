---
layout: post
title: "Leetcode - Algorithm - Range Sum Array Immutable"
date: 2017-08-29 16:32:01
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given an integer array nums, find the sum of the elements between indices i and j (i ≤ j), inclusive.

Example:
```
Given nums = [-2, 0, 3, -5, 2, -1]

sumRange(0, 2) -> 1
sumRange(2, 5) -> -1
sumRange(0, 5) -> -3
```
Note:
You may assume that the array does not change.
There are many calls to sumRange function.

### 维护一个累加的数组
逐个把原数组中的数字累加起来，比如原数组为`[-2,0,3,-5,2,-1]`。累加之后变成`[-2,-2,1,-4,-2,-3]`。可以在数组头部安插一个哨兵元素`[0,-2,-2,1,-4,-2,-3]`防止越界。

#### 代码
```java
class NumArray {
    private int[] sumArray;
    public NumArray(int[] nums) {
        sumArray = new int[nums.length+1];
        for (int i = 0; i < nums.length; i++) {
            sumArray[i+1] = sumArray[i] + nums[i];
        }
    }

    public int sumRange(int i, int j) {
        if (i < 0 || j >= sumArray.length-1) { return 0; }
        return sumArray[j+1] - sumArray[i];
    }
}
```

#### 结果
已经是最好的了。前面更快的提交，基本都是没有开新数组，修改了原数组。题目明确这是不允许的。
![range-sum-array-immutable-1](/images/leetcode/range-sum-array-immutable-1.png)
