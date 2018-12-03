---
layout: post
title: "Leetcode - Algorithm - Next Greater Element Three "
date: 2018-12-03 18:22:54
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "math"]
level: "medium"
description: >
---

### 题目
Given a positive 32-bit integer n, you need to find the smallest 32-bit integer which has exactly the same digits existing in the integer n and is greater in value than n. If no such positive 32-bit integer exists, you need to return -1.

Example 1:
```
Input: 12
Output: 21
```

Example 2:
```
Input: 21
Output: -1
```

### 数学性质
看一个例子`12443322`，
```
首先需要把这个3提到2之前。为什么是这个3？因为它是2之后最接近2，但大于2的数字，这样能保证变换之后的数字大于原始数字。
 |   |
12443322

得到：
13443222

然后需要把3后面的部分按升序排序。这为了确保得到的数字是大于原始数字中最小的一个。
  |    |
13443222

变成：
13222344
```

#### 代码
```java
class Solution {
    public int nextGreaterElement(int n) {
        char[] arr = String.valueOf(n).toCharArray();
        if (findGreater(arr)) {
            try {
                return Integer.parseInt(new String(arr));
            } catch (NumberFormatException nfe) {
                return -1;
            }
        }
        return -1;
    }

    private boolean findGreater(char[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            if (arr[i] > arr[i - 1]) {
                int j = i;
                while (j + 1 < arr.length && (arr[j + 1] > arr[i - 1])) j++;
                swap(arr, i - 1, j);
                Arrays.sort(arr, i, arr.length);
                return true;
            }
        }
        return false;
    }

    /** swap ath & bth element in arr */
    private void swap(char[] arr, int a, int b) {
        char temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }
}
```

#### 结果
![next-greater-element-three-1](/images/leetcode/next-greater-element-three-1.png)
