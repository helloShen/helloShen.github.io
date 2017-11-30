---
layout: post
title: "Leetcode - Algorithm - Next Greater Element One "
date: 2017-11-29 19:22:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
You are given two arrays (without duplicates) nums1 and nums2 where nums1’s elements are subset of nums2. Find all the next greater numbers for nums1's elements in the corresponding places of nums2.

The Next Greater Number of a number x in nums1 is the first greater number to its right in nums2. If it does not exist, output -1 for this number.

Example 1:
```
Input: nums1 = [4,1,2], nums2 = [1,3,4,2].
Output: [-1,3,-1]
Explanation:
    For number 4 in the first array, you cannot find the next greater number for it in the second array, so output -1.
    For number 1 in the first array, the next greater number for it in the second array is 3.
    For number 2 in the first array, there is no next greater number for it in the second array, so output -1.
```

Example 2:
```
Input: nums1 = [2,4], nums2 = [1,2,3,4].
Output: [3,-1]
Explanation:
    For number 2 in the first array, the next greater number for it in the second array is 3.
    For number 4 in the first array, there is no next greater number for it in the second array, so output -1.
```

Note:
* All elements in nums1 and nums2 are unique.
* The length of both nums1 and nums2 would not exceed 1000.

### 老老实实从右往左遍历，$$O(n^2)$$

#### 代码
```java
class Solution {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        int len1 = nums1.length, len2 = nums2.length;
        int[] res = new int[len1];
        for (int i = 0; i < len1; i++) {
            int lastGreater = -1;
            int n1 = nums1[i];
            for (int j = len2 - 1; j >= 0; j--) {
                int n2 = nums2[j];
                if (n2 > n1) {
                    lastGreater = n2;
                } else if (n2 == n1) {
                    res[i] = lastGreater;
                    break;
                }
            }
        }
        return res;
    }
}
```

#### 结果
![next-greater-element-one-1](/images/leetcode/next-greater-element-one-1.png)


### 用Stack，$$O(n)$$
考虑下面这个例子，一串递减数列，当它打破递减的规律，出现一个稍大的数字，比如说`6`之后，对这个递减数列中所有小于`6`的数字来说，`nextGreater`数都是`6`。
```
    递减数列      开始递增
 |            |  |
[10, 8, 5, 3, 1, 6, ..., ...]
        |     |
        这3个数，nextGreater都是6

对于递减数列里所有 <6 的数字来说，第一个更大的数字都是6.
```

根据这个规律，可以用一个Stack一直`push()`这个递减数列，当遇到递增数的时候，开始`pop()`所有小于这个递增数的数字。这几个弹出的数字对应的`nextGreater`都等于那个递增数。对于这些确定的`nextGreater`用一个Map记录就好了。

#### 代码
```java
class Solution {
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        int[] res = new int[nums1.length];
        if (nums1.length == 0) { return res; }
        Map<Integer,Integer> map = new HashMap<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(nums2[0]);
        for (int i = 1; i < nums2.length; i++) {
            if (nums2[i] > nums2[i-1]) {
                while (!stack.isEmpty() && stack.peek() < nums2[i]) {
                    map.put(stack.pop(),nums2[i]);
                }
            }
            stack.push(nums2[i]);
        }
        Integer lastGreater = null;
        for (int i = 0; i < nums1.length; i++) {
            lastGreater = map.get(nums1[i]);
            res[i] = (lastGreater == null)? -1 : lastGreater;
        }
        return res;
    }
}
```

#### 结果
![next-greater-element-one-2](/images/leetcode/next-greater-element-one-2.png)
