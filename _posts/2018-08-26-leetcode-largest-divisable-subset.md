---
layout: post
title: "Leetcode - Algorithm - Largest Divisable Subset "
date: 2018-08-26 17:40:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming", "backtracking"]
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
这题是标准的动态规划。先排序，
```
[3, 4, 6, 8, 9, 16] ->   3 + 子问题[6,8,9,16]的最优解 -+
                         3 + 子问题[9,16]的最优解      |
                         4 + 子问题[8,9,16]的最优解    +--> 中取最优解
                         4 + 子问题[16]的最优解        |
                         8 + 子问题[16]的最优解       -+
```

自底向上的动态规划从数组末尾往前遍历，用一个`Map<Integer, List<Interger>`可以记录计算过的每个位置的最优解。

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


### 给动态规划剪枝
因为所有数都是正整数，且不重复。所有就有最小的乘数是`2`，

> if a > b && a % b == 0，than a >= b * 2

考虑我们有`[3,4,5,8,10,16,20,21]`，当我们找到一个长度为3的串`[4,8,16]`，接下来从数字`5`开始遍历，我们至少需要找到一个长度为4的串，
```
Next smallest possible chain of length 4 = 【5, 10, 20, 40】
     |
[3,4,5,8,10,16,20,21]
   |-->|---->|
Current longest chain
```
很明显这是不可能的，因为哪怕连续三次都用最小乘数`2`，最后一个数也至少是`40`，远大于数组最后一个数字`21`。所以当我们已经找到长度为3的串的时候，最大的可能形成长度为4的串的首元素是`2`，
```
limit = 21 / 2 / 2 / 2 = 2
```
所有大于2的数字都不要再遍历了。

#### 代码
```java
class Solution4 implements Solution {
    // assert: all numbers are positive and distinct
    public List<Integer> largestDivisibleSubset(int[] nums) {
        List<Integer> res = new ArrayList<>();
        if (nums == null || nums.length == 0) {
            return res;
        }
        Arrays.sort(nums);
        sorted = nums;
        maxNum = nums[nums.length-1];       // 全局最大元素
        len = new int[nums.length];         // 以每个节点为头节点的链长度
        next = new int[nums.length];        // 指向全局最优子问题
        longestChainHead = -1;              // 标记当前最长链头元素下标
        maxLen = 0;                         // 当前最长链的长度
        int limit = maxNum;                 // 当前还可能最长链头节点的最大可能值
        for (int i = 0; i < nums.length && nums[i] <= limit; i++) {
            dp(i, 0);
            limit = maxNum >> maxLen;
        }
        while (longestChainHead != -1) {
            res.add(nums[longestChainHead]);
            longestChainHead = next[longestChainHead];
        }
        return res;
    }

    private int[] sorted;
    private int[] len;
    private int[] next;
    private int longestChainHead;
    private int maxLen;
    private int maxNum;

    private void dp(int start, int preLen) {
        if (len[start] == 0) {
            len[start] = 1;
            next[start] = -1;
        }
        if (len[start] > maxLen) {
            maxLen = len[start];
            longestChainHead = start;
        }
        int limit = maxNum >> Math.max((maxLen - preLen - 1), 0);
        int max = 0;
        for (int i = start + 1; i < sorted.length && sorted[i] <= limit; i++) {
            if (sorted[i] % sorted[start] == 0) {
                if (len[i] == 0) {
                    dp(i, preLen + 1);
                }
                if (len[i] > max) {
                    max = len[i];
                    len[start] = len[i] + 1;
                    next[start] = i;
                    if (len[start] > maxLen) {
                        maxLen = len[start];
                        longestChainHead = start;
                        limit = maxNum >> Math.max((maxLen - preLen - 1), 0);
                    }           
                }
            }
        }
    }
}
```

#### 结果
![largest-divisable-subset-3](/images/leetcode/largest-divisable-subset-3.png)
