---
layout: post
title: "Leetcode - Algorithm - Range Sum Query Mutable "
date: 2018-08-02 01:48:26
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["segment tree","tree"]
level: "medium"
description: >
---

### 题目
Given an integer array nums, find the sum of the elements between indices i and j (i ≤ j), inclusive.

The update(i, val) function modifies nums by updating the element at index i to val.

Example:
```
Given nums = [1, 3, 5]

sumRange(0, 2) -> 9
update(1, 2)
sumRange(0, 2) -> 8
```

Note:
1. The array is only modifiable by the update function.
2. You may assume the number of calls to update and sumRange function is distributed evenly.

### 常用手段：提前做好加法
老套路：提前计算累加和，
```
[   1, 2,  3,  4,  5   ]
[   1, 3,  6,  10, 15  ]    // 每一位都是前面所有数字的累加和
```
这样的好处是，每次计算`sum[i,j]`(j >= i)，只需要`O(1)`的时间。
> sum[i,j] = sum[j] - sum[i-1]

缺点是：每次`update()`，需要花`O(n)`的时间更新所有位的累加和。

#### 代码
```java
class NumArray {

        private static int[] numbers = new int[0];
        private static int[] sum = new int[0];
        private static Map<Integer,Integer> diff = new HashMap<>();

        public NumArray(int[] nums) {
            numbers = new int[nums.length];
            sum = new int[nums.length];
            diff.clear();
            if (nums.length == 0) { return; }
            numbers[0] = nums[0];
            sum[0] = nums[0];
            for (int i = 1; i < nums.length; i++) {
                numbers[i] = nums[i];
                sum[i] = sum[i-1] + nums[i];
            }
        }

        public void update(int i, int val) {
            int d = val - numbers[i];
            numbers[i] = val;
            diff.put(i,(diff.containsKey(i))? diff.get(i) + d : d);
        }

        public int sumRange(int i, int j) {
            int res = (i == 0)? sum[j] : sum[j] - sum[i-1];
            for (int k = i; k <= j; k++) {
                if (diff.containsKey(k)) {
                    res += diff.get(k);
                }
            }
            return res;
        }
}

/**
 * Your NumArray object will be instantiated and called as such:
 * NumArray obj = new NumArray(nums);
 * obj.update(i,val);
 * int param_2 = obj.sumRange(i,j);
 */
```

#### 结果
![range-sum-query-mutable-1](/images/leetcode/range-sum-query-mutable-1.png)


### 线段树
考虑到`sum()`和`update()`函数调用次数比较接近，所以用线段树可以避免`update()`函数复杂度过高的问题。以求在`sum()`和`update()`之间达到最佳平衡。

线段树代码明天写。
