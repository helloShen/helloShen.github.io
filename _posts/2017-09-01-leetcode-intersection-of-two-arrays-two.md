---
layout: post
title: "Leetcode - Algorithm - Intersection Of Two Arrays Two "
date: 2017-09-01 13:51:38
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","two pointers"]
level: "easy"
description: >
---

### 题目
Given two arrays, write a function to compute their intersection.

Example:
Given nums1 = `[1, 2, 2, 1]`, nums2 = `[2, 2]`, return `[2, 2]`.

Note:
Each element in the result should appear as many times as it shows in both arrays.
The result can be in any order.
Follow up:
What if the given array is already sorted? How would you optimize your algorithm?
What if nums1's size is small compared to nums2's size? Which algorithm is better?
What if elements of nums2 are stored on disk, and the memory is limited such that you cannot load all elements into the memory at once?

### 未排序的情况，用`HashMap`，复杂度 $$O(m+n)$$
用`HashMap`统计`nums1`中所有元素的频率。然后再遍历`nums2`，到`HashMap`里查找元素。

#### 代码
```java
class Solution {
    public int[] intersect(int[] nums1, int[] nums2) {
        Map<Integer,Integer> map = new HashMap<>();
        for (Integer n : nums1) {
            Integer freq = map.get(n);
            map.put(n,(freq == null)? 1 : freq+1);
        }
        int[] inter = new int[Math.min(nums1.length,nums2.length)];
        int cur = 0;
        for (Integer n : nums2) {
            Integer freq = map.get(n);
            if (freq != null) {
                inter[cur++] = n;
                if (freq == 1) {
                    map.remove(n);
                } else {
                    map.put(n,freq-1);
                }
            }
        }
        return Arrays.copyOfRange(inter,0,cur);
    }
}
```

#### 结果
![intersection-of-two-arrays-two-1](/images/leetcode/intersection-of-two-arrays-two-1.png)


### 有序数组，可以用`Two Pointers`算法
和合并排序一样的，用两个指针分别指向两个数组。代码省略。

### 如果`nums1`长度大大小于`nums2`的长度？
这就意味着有序数组的`Two Pointers`算法效率可能高于无序数组的`HashMap`的方法。假设`nums1.length = n`, `nums2.length = m`。无序数组的`HashMap`方法的复杂度是 $$O(m+n)$$，有序数组的`Two Pointers`方法的复杂度是 $$O(n\log_{}{m})$$。当`m`很大，而`n`很小的时候，`\log_{}{m}`就很重要了。

### 如果`nums1`和`nums2`都很大，内存放不下，怎么办？
可以分块读取一部分的`nums1`和`nums2`，最后可以把所有小块交集合并起来。
