---
layout: post
title: "Leetcode - Algorithm - Minimum Moves To Equal Array Element "
date: 2017-09-28 18:20:45
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
Given a non-empty integer array of size n, find the minimum number of moves required to make all array elements equal, where a move is incrementing n - 1 elements by 1.

Example:
```
Input:
[1,2,3]

Output:
3
```
Explanation:
Only three moves are needed (remember each move increments two elements):
```
[1,2,3]  =>  [2,3,3]  =>  [3,4,3]  =>  [4,4,4]
```

### 这是一个数学问题
假设我们有`[4,5,8,10]`，已经知道最后达到平衡的结果是`[15,15,15,15]`。观察最后的结果，我们会发现一些规律，
```
15, 15, 15, 15
 4,  5,  8, 10  -
-----------------
11, 10,  7,  5

11 + 10 + 7 + 5 = 33

每轮4个数里选3个递增
最小的数4，递增了11轮

3 * 11 = 33
```
所以如果最初`n`个数字，`sum`代表它们的总和，假设递增`m`轮以后达到平衡，`min`表示最初数组中最小的数字，则下面这个等式成立，
```
sum + m * (n - 1) = (min + m) * n
```
简单整理一下，就推导出，
```
m = sum - min * n
```

#### 代码
```java
class Solution {
    public int minMoves(int[] nums) {
        if (nums.length < 2) { return 0; }
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int num : nums) {
            min = Math.min(min,num);
            max = Math.max(max,num);
        }
        int diffInit = 0;
        for (int num : nums) {
            diffInit += (max - num);
        }
        return (max - min) * nums.length - diffInit;
    }
}
```

#### 结果
![minimum-moves-to-equal-array-element-1](/images/leetcode/minimum-moves-to-equal-array-element-1.png)
