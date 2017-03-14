---
layout: post
title: "Leetcode - Algorithm - Median of Two Sorted Arrays"
date: 2017-03-14 16:47:48
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode"]
description: >
---

### 题目
There are two sorted arrays nums1 and nums2 of size m and n respectively.

Find the median of the two sorted arrays. The overall run time complexity should be O(log (m+n)).

Example 1:
```
nums1 = [1, 3]
nums2 = [2]
```
The median is 2.0

Example 2:
```
nums1 = [1, 2]
nums2 = [3, 4]
```
The median is (2 + 3)/2 = 2.5

### 朴素解法
朴素解法直接遍历，复杂度是线性的`O(n)`，不是`O(log(m+n))`。

两个指针分别指着两个数组。直接比较两个指针指向的两个数字，数字比较小的那个指针前移一位。如果一个数组比较短提前用完，就用`Integer.MAX_VALUE`临时填充。有一个全局计数器，只比较总长度一半的次数，就肯定能拿到我们要的计算中位数的数字了。

有奇数个数字，直接取中间那个数字。有偶数个数字，返回中间两个数字的平均值。

```java
public class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int totalLength = nums1.length + nums2.length;
        if (totalLength == 0) {
            return 0.0d;
        }
        if (totalLength == 1) {
            if (nums1.length == 1) {
                return (double)nums1[0];
            } else {
                return (double)nums2[0];
            }
        }
        // 下面开始两个数组长度总和 >= 2
        int index1 = 0;
        int index2 = 0;
        int num1 = 0;
        int num2 = 0;
        int cursor = 0; // 当前窗口
        int[] candidate = new int[2]; // 不管长度是奇数还是偶数，先把两个候选数取出来
        for (int i = 0; i <= totalLength/2; i++) {
            if (index1 < nums1.length) {
                num1 = nums1[index1];
            } else {
                num1 = Integer.MAX_VALUE;
            }
            if (index2 < nums2.length) {
                num2 = nums2[index2];
            } else {
                num2 = Integer.MAX_VALUE;
            }
            if (num1 < num2) {
                cursor = num1;
                index1++;
            } else {
                cursor = num2;
                index2++;
            }
            if (i == (totalLength/2 -1)) {
                candidate[0] = cursor;
            }
            if (i == (totalLength/2)) {
                candidate[1] = cursor;
            }
        }
        // 根据总长度是奇数还是偶数，用候选数计算出结果。
        if (totalLength%2 == 0) { // 长度是偶数：取两个候选数的平均数
            return ((double)candidate[0] + (double)candidate[1]) / 2;
        } else { // 长度是奇数：取两个候选数中的后者
            return (double)candidate[1];
        }
    }
}
```

#### 结果
虽然复杂度是`O(n)`，不是`O(log(m+n))`，但已经通过了。

![median-two-array-1](/images/leetcode/median-two-array-1.png)
