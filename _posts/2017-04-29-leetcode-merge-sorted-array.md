---
layout: post
title: "Leetcode - Algorithm - Merge Sorted Array "
date: 2017-04-29 16:19:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","two pointers"]
level: "easy"
description: >
---

### 题目
Given two sorted integer arrays nums1 and nums2, merge nums2 into nums1 as one sorted array.

Note:
You may assume that nums1 has enough space (size that is greater or equal to m + n) to hold additional elements from nums2. The number of elements initialized in nums1 and nums2 are m and n respectively.

### 标准的双指针合并
两个指针分别指向两个数组的末尾，选比较大的填充到`nums1`的尾部。

#### 代码
```java
public class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int tail1 = m - 1, tail2 = n - 1;
        int cursor = m + n - 1;
        while (tail1 >= 0 || tail2 >= 0) {
            int first = Integer.MIN_VALUE;
            int second = Integer.MIN_VALUE;
            if (tail1 >= 0) { first = nums1[tail1]; }
            if (tail2 >= 0) { second = nums2[tail2]; }
            if (first >= second) {
                nums1[cursor--] = nums1[tail1--];
            } else {
                nums1[cursor--] = nums2[tail2--];
            }
        }
    }
}
```

#### 结果
![merge-sorted-array-1](/images/leetcode/merge-sorted-array-1.png)

### 稍微优化
因为这里是`nums2`合并入`nums1`。所以一定是`num2`的数字先用完。

#### 代码
因为如果`nums1`还没用完，那么开头剩下的元素不需要拷贝了，已经在`nums1`里了。只有当`nums2`没用完的情况，才需要拷贝剩下的。

```java
public class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int tail1 = m - 1, tail2 = n - 1, cur = m + n - 1;
        while (tail1 >= 0 && tail2 >= 0) {
            if (nums1[tail1] >= nums2[tail2]) {
                nums1[cur--] = nums1[tail1--];
            } else {
                nums1[cur--] = nums2[tail2--];
            }
        }
        while (tail2 >= 0) { nums1[cur--] = nums2[tail2--]; }
    }
}
```

#### 结果
![merge-sorted-array-2](/images/leetcode/merge-sorted-array-2.png)
