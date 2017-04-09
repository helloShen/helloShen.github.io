---
layout: post
title: "Leetcode - Algorithm - Combination Sum 2 "
date: 2017-04-09 13:43:18
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","backtracking"]
level: "medium"
description: >
---

### 主要收获
ROCK the Backtracking!

### 题目
Given a collection of candidate numbers (C) and a target number (T), find all unique combinations in C where the candidate numbers sums to T.

Each number in C may only be used once in the combination.

Note:
All numbers (including target) will be positive integers.
The solution set must not contain duplicate combinations.
For example, given candidate set [10, 1, 2, 7, 6, 1, 5] and target 8,
A solution set is:
```
[
  [1, 7],
  [1, 2, 5],
  [2, 6],
  [1, 1, 6]
]
```

### 直接标准回溯算法 $$O(2^n)$$
这题来得非常及时！昨天睡前写了一遍`CombinationSum`的标准回溯算法，今天早上一起来，正好复习一遍。

主要有两个不同的地方。第一，数字不可以重复使用，所以像`[3,4,7,8]`，第一个数字取了`3`，接下去递归就不是从`3`开始，而是从`4`开始。具体体现到代码，就是下面这一行，注意是`i+1`，不是`i`。
```java
backtracking(temp, preRemain, candidates, i+1, target, result); // 注意这里的i+1
```

第二个不同，要注意重复。考虑`[1, 1, 2, 5, 6, 7, 10]`，目标数`8`，这个例子，遍历第一个`1`的时候，能找出`[1,2,5]`这个组合。然后从第二个`1`开始，也能找出`[1,2,5]`的组合。所以需要那第二个`1`。具体到代码，就是下面做这一行，
```
if (i != start && candidates[i] == candidates[i-1]) { continue; } // eliminates duplicates
```

#### 代码
```java
public class Solution {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> result = new ArrayList<>();
        backtracking(new ArrayList<Integer>(), target, candidates, 0, target, result);
        return result;
    }
    public void backtracking(List<Integer> temp, int remain, int[] candidates, int start, int target, List<List<Integer>> result) {
        if (remain == 0) {
            result.add(new ArrayList<Integer>(temp));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (i != start && candidates[i] == candidates[i-1]) { continue; } // eliminates duplicates
            int preRemain = remain - candidates[i];
            if (preRemain < 0) { break; } // 剪枝
            temp.add(candidates[i]);
            backtracking(temp, preRemain, candidates, i+1, target, result);
            temp.remove(temp.size()-1);
        }
    }
}
```

#### 结果
直接银弹！不啰嗦！
![combination-sum-2-1](/images/leetcode/combination-sum-2-1.png)
