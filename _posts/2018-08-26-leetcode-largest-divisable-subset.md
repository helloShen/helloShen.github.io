---
layout: post
title: "Leetcode - Algorithm - Largest Divisable Subset "
date: 2018-08-26 17:40:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
iven a set of distinct positive integers, find the largest subset such that every pair (Si, Sj) of elements in this subset satisfies:
```
Si % Sj = 0 or Sj % Si = 0.
```

If there are multiple solutions, return any subset is fine.

Example 1:
```
Input: [1,2,3]
Output: [1,2] (of course, [1,3] will also be ok)
```
Example 2:
```
Input: [1,2,4,8]
Output: [1,2,4,8]
```

### 动态规划
这题是标准的动态规划。每一步的解都是 **当前格 + 子问题的最优解**。这里我用了一个`Map<Integer, List<Interger>`，

#### 代码
```java
// assert: all numbers are positive
public List<Integer> largestDivisibleSubset(int[] nums) {
    List<Integer> res = new ArrayList<>();
    if (nums == null || nums.length == 0) {
        return res;
    }
    Arrays.sort(nums);
    int[] len = new int[nums.length];
    int[] pre = new int[nums.length];
    int globalMaxIndex = nums.length - 1; // 标记最长链的下标
    for (int i = nums.length - 1; i >= 0; i--) {
        int maxLen = 0;
        pre[i] = -1;
        for (int j = i + 1; j < nums.length; j++) {
            if (nums[j] % nums[i] == 0) {
                if (len[j] > maxLen) {
                    maxLen = len[j];
                    pre[i] = j;
                }
            }
        }
        len[i] = maxLen + 1;
        if (len[i] > len[globalMaxIndex]) {
            globalMaxIndex = i;
        }
    }
    while (globalMaxIndex != -1) {
        res.add(nums[globalMaxIndex]);
        globalMaxIndex = pre[globalMaxIndex];
    }
    return res;
}
```

#### 结果
![largest-divisable-subset-1](/images/leetcode/largest-divisable-subset-1.png)


### 用两个数组
1. 长度数组: 记录每一格最长两条的长度
2. 跳跃数组：记录每一格前驱节点的下标

就是说不需要用一个`Map<Integer, List<Integer>)`记录完整的最优解。而是以一个链表的形式，每个节点记住它的前驱节点即可。

比如，`[3,4,8,16]`中最长路径为`[4,6,8]`，它的`pre[]`列表如下，
```
[3,  4, 8, 16] --> 原数组
[-1, 2, 3, -1] --> 下标
     |  +--- 能整除8的是16
     |
     +------ 能整除4的是8
```

#### 代码
```java
class Solution {
    public List<Integer> largestDivisibleSubset(int[] nums) {
        List<Integer> res = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return res;
        }
        Arrays.sort(nums);
        int[] len = new int[nums.length]; // 当前节点为首节点的链子长度
        int[] pre = new int[nums.length]; // 记录跳跃的前驱节点下标
        int globalMaxIndex = nums.length - 1; // 标记最长链的下标
        for (int i = nums.length - 1; i >= 0; i--) {
            int maxLen = 0;
            pre[i] = -1;
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[j] % nums[i] == 0) {
                    if (len[j] > maxLen) {
                        maxLen = len[j];
                        pre[i] = j;
                    }
                }
            }
            len[i] = maxLen + 1;
            if (len[i] > len[globalMaxIndex]) {
                globalMaxIndex = i;
            }
        }
        while (globalMaxIndex != -1) {
            res.add(nums[globalMaxIndex]);
            globalMaxIndex = pre[globalMaxIndex];
        }
        return res;
    }
}
```

#### 结果
![largest-divisable-subset-2](/images/leetcode/largest-divisable-subset-2.png)


### 一种神奇的算法，至今不太理解

#### 代码
```java
class Solution {
    private List<Integer> result;
    private int max;
    public List<Integer> largestDivisibleSubset(int[] nums) {
        result = new ArrayList<>();
        if (nums == null || nums.length < 1) return result;
        Arrays.sort(nums);
        max = nums[0];
        for (int num : nums)
            if (num > max)
                max = num;
        int i = 0, limit = max;
        while (i < nums.length && nums[i] <= limit) {
            List<Integer> temp = new ArrayList<>();
            temp.add(nums[i]);
            insert(nums, i, temp);
            limit = max/(int)Math.pow(2, result.size());
            i++;
        }
        return result;
    }

    private void insert(int[] nums, int i, List<Integer> list) {
        if (list.size() > result.size()) {
            result = new ArrayList<>(list);
        }
        int pow = result.size() - list.size();
        if (pow < 1) pow = 0;
        int limit = max/(int)Math.pow(2, pow);
        for (int j = i + 1; j < nums.length; j ++) {
            if (nums[j] > limit) break;
            if (nums[j] % nums[i] == 0) {
                list.add(nums[j]);
                insert(nums, j, list);
                list.remove(list.size()-1);
            }
        }
    }
}
```

#### 结果
![largest-divisable-subset-3](/images/leetcode/largest-divisable-subset-3.png)
