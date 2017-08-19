---
layout: post
title: "Leetcode - Algorithm - Missing Number "
date: 2017-08-18 17:20:30
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation"]
level: "easy"
description: >
---

### 题目
Given an array containing n distinct numbers taken from `0, 1, 2, ..., n`, find the one that is missing from the array.

For example,
```
Given nums = [0, 1, 3] return 2.
```

Note:
Your algorithm should run in linear runtime complexity. Could you implement it using only constant extra space complexity?

### 基本思路
遍历数组比较的方法当然可以做，但边角情况就比较多。

把`[0,n]`个数字加起来，再减去`nums`里的数字，可以保证得到那个缺失的数字，而且没有边角情况。

当然缺一个数字这种问题，用`XOR`异或位操作是最好的。

#### 代码
```java
class Solution {
    public int missingNumber(int[] nums) {
        int res = 0;
        for (int i = 0; i < nums.length; i++) {
            res ^= i; res ^= nums[i];
        }
        res ^= nums.length;
        return res;
    }
}
```

#### 结果
![missing-number-1](/images/leetcode/missing-number-1.png)
