---
layout: post
title: "Leetcode - Algorithm - Permutation "
date: 2017-04-11 15:37:59
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["backtracking"]
level: "medium"
description: >
---

### 题目
Given a collection of distinct numbers, return all possible permutations.

For example,
[1,2,3] have the following permutations:
```
[
  [1,2,3],
  [1,3,2],
  [2,1,3],
  [2,3,1],
  [3,1,2],
  [3,2,1]
]
```

### 标准回溯算法 $$O(n^n)$$
这里唯一的的小技巧，是`candidates`的容器，选了`LinkedList`。为的是随机插入，删除操作快$$O(1)$$。

除了把`candidates`用`List`容器装之外，还可以保留原来的数组，但额外再维护一个数组，来表示哪些数字用过了。但效率上没有本质的区别。

#### 代码
```java
public class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> buff = new ArrayList<>();
        List<Integer> candidates = new LinkedList<>(); //选LinkedList为了插入，删除操作快
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
        }
    }
}
```

#### 结果
银弹！
![permutation-1](/images/leetcode/permutation-1.png)
