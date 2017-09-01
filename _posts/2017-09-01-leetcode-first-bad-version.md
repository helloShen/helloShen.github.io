---
layout: post
title: "Leetcode - Algorithm - First Bad Version "
date: 2017-09-01 13:00:36
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search"]
level: "easy"
description: >
---

### 题目
You are a product manager and currently leading a team to develop a new product. Unfortunately, the latest version of your product fails the quality check. Since each version is developed based on the previous version, all the versions after a bad version are also bad.

Suppose you have n versions `[1, 2, ..., n]` and you want to find out the first bad one, which causes all the following ones to be bad.

You are given an API bool isBadVersion(version) which will return whether version is bad. Implement a function to find the first bad version. You should minimize the number of calls to the API.


### 就是标准二分查找

#### 代码
```java
/* The isBadVersion API is defined in the parent class VersionControl.
      boolean isBadVersion(int version); */
public class Solution extends VersionControl {
    public int firstBadVersion(int n) {
        return binarySearch(1,n);
    }
    private int binarySearch(int lo, int hi) {
        if (lo > hi) { return lo; }
        int mid = lo + (hi - lo) / 2;
        boolean midIsBadVersion = isBadVersion(mid);
        if (midIsBadVersion) {
            return binarySearch(lo,mid-1);
        } else {
            return binarySearch(mid+1,hi);
        }
    }
}
```

#### 结果
![first-bad-version-1](/images/leetcode/first-bad-version-1.png)
