---
layout: post
title: "Leetcode - Algorithm - Monotonic Array "
date: 2018-09-01 23:04:07
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
An array is monotonic if it is either monotone increasing or monotone decreasing.

An array A is monotone increasing if for all `i <= j, A[i] <= A[j]`.  An array A is monotone decreasing if for all `i <= j, A[i] >= A[j]`.

Return true if and only if the given array A is monotonic.

Example 1:
```
Input: [1,2,2,3]
Output: true
Example 2:

Input: [6,5,4,4]
Output: true
Example 3:

Input: [1,3,2]
Output: false
Example 4:

Input: [1,2,4,5]
Output: true
Example 5:

Input: [1,1,1]
Output: true
```

Note:
* 1 <= A.length <= 50000
* -100000 <= A[i] <= 100000

### 朴素遍历数组

#### 代码
```java
class Solution {
    public boolean isMonotonic(int[] A) {
        if (A == null || A.length == 0) {
            return false;
        }
        int sign = 0;
        for (int i = 1; i < A.length; i++) {
            int diff = A[i] - A[i-1];
            if (sign == 0) {
                sign = diff;
            } else if ((sign > 0 && diff < 0) || (sign < 0 && diff > 0)) {
                return false;
            }
        }
        return true;    
    }
}
```
