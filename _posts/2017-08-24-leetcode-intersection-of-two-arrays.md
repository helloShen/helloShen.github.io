---
layout: post
title: "Leetcode - Algorithm - Intersection Of Two Arrays "
date: 2017-08-24 17:21:36
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","binary search","two pointers","sort"]
level: "easy"
description: >
---

### 题目
Given two arrays, write a function to compute their intersection.

Example:
```
Given nums1 = [1, 2, 2, 1], nums2 = [2, 2], return [2].
```

Note:
Each element in the result must be unique.
The result can be in any order.


### 主要思路
这题主要有两种思路，
* 一种是先给数组排序。然后用Two Pointers的办法遍历数组。复杂度取决于排序的 $$O(n\log_{}{n})$$
* 第二种是用`HashSet`，统计有哪些数字。复杂度是 $$O(m+n)$$。`m`是`nums1[]`的大小，`n`是`nums2[]`的大小。

### 用两个`HashSet`的方法
第一个`HashSet`用来统计`nums1`里有哪些数字。第二个用来存放重合数字的集合。

#### 代码
```java
class Solution {
    public int[] intersection(int[] nums1, int[] nums2) {
        Set<Integer> numbers = new HashSet<>();
        for (int n : nums1) { numbers.add(n); }
        Set<Integer> resSet = new HashSet<>();
        for (int n : nums2) {
            if (numbers.contains(n)) { resSet.add(n); }
        }
        int[] res = new int[resSet.size()];
        int cursor = 0;
        for (int n : resSet) {
            res[cursor++] = n;
        }
        return res;
    }
}
```

#### 结果
![intersection-of-two-arrays-1](/images/leetcode/intersection-of-two-arrays-1.png)


### 聪明一点，用一个`HashSet`就可以完成
第一遍遍历数组`nums1`往`Set`里添加新数字。第二遍遍历数组`nums2`从`Set`里删除元素。删除的这部分元素就是两个数组重复的元素。

#### 代码
```java
class Solution {
    public int[] intersection(int[] nums1, int[] nums2) {
        Set<Integer> numbers = new HashSet<>();
        for (int n : nums1) { numbers.add(n); }
        int[] res = new int[numbers.size()];
        int cursor = 0;
        for (int n : nums2) {
            if (numbers.remove(n)) {
                res[cursor++] = n;
            }
        }
        return Arrays.copyOfRange(res,0,cursor);
    }
}
```

#### 结果
这种做法效率更高。
![intersection-of-two-arrays-2](/images/leetcode/intersection-of-two-arrays-2.png)
