---
layout: post
title: "Leetcode - Algorithm - Permutation 2 "
date: 2017-04-11 15:42:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["backtracking"]
level: "medium"
description: >
---

### 题目
Given a collection of numbers that might contain duplicates, return all possible unique permutations.

For example,
[1,1,2] have the following unique permutations:
```
[
  [1,1,2],
  [1,2,1],
  [2,1,1]
]
```

### 标准回溯算法 $$O(n^n)$$
和无重复数字`Permutations`的区别在于，得先排序，然后跳过重复数字。比如`[1,2,1]`，先排序，得到`[1,1,2]`。然后递归遍历的时候处理过第一个`1`以后，跳过第二个`1`，直接处理`2`。

#### 代码
```java
public class Solution {
    public List<List<Integer>> permuteUnique(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> buff = new ArrayList<>();
        List<Integer> candidates = new LinkedList<>();
        for (int i = 0; i < nums.length; i++) {
            candidates.add(nums[i]);
        }
        backtracking(buff,candidates,result);
        return result;
    }
    public void backtracking(List<Integer> buff, List<Integer> candidates, List<List<Integer>> result) {
        if (candidates.size() == 0) { result.add(new ArrayList<Integer>(buff)); return; }
        for (int i = 0; i < candidates.size(); i++) {
            int temp = candidates.get(i);
            buff.add(temp);
            candidates.remove(i);
            backtracking(buff,candidates,result);
            candidates.add(i,temp);
            buff.remove(buff.size()-1);
            while (i+1 < candidates.size() && candidates.get(i+1).equals(candidates.get(i))) { i++; } // skip duplicates
        }
    }
}
```

#### 结果
银弹！
![permutation-2-1](/images/leetcode/permutation-2-1.png)
