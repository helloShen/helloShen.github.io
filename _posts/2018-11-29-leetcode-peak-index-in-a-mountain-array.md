---
layout: post
title: "Leetcode - Algorithm - Peak Index In A Mountain Array "
date: 2018-11-29 17:34:50
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Let's call an array A a mountain if the following properties hold:

* A.length >= 3
* There exists some 0 < i < A.length - 1 such that A[0] < A[1] < ... A[i-1] < A[i] > A[i+1] > ... > A[A.length - 1]

Given an array that is definitely a mountain, return any i such that A[0] < A[1] < ... A[i-1] < A[i] > A[i+1] > ... > A[A.length - 1].

Example 1:
```
Input: [0,1,0]
Output: 1
```

Example 2:
```
Input: [0,2,1,0]
Output: 1
```

Note:
* 3 <= A.length <= 10000
* 0 <= A[i] <= 10^6
* A is a mountain, as defined above.

### 解法1

#### 代码
```java
class Solution {
    public int peakIndexInMountainArray(int[] A) {
        for (int i = 1; i < A.length; i++) {
            if (A[i] < A[i - 1]) return i - 1;
        }
        return 0;
    }
}
```

#### 结果
![peak-index-in-a-mountain-array-1](/images/leetcode/peak-index-in-a-mountain-array-1.png)
