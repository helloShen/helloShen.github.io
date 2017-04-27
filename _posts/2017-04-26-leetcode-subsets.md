---
layout: post
title: "Leetcode - Algorithm - Subsets "
date: 2017-04-26 18:33:21
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","backtracking","bit manipulation"]
level: "medium"
description: >
---

### 思考
设计算法，最关键的在于怎么 **抽象问题**。简单的观察规律，得以得出最后第3种迭代的解法。把过程抽象成一系列`添加`和`不添加`的决策，很快就能写出回溯算法。再把`添加`和`不添加`的决策往上抽象一个层次，利用`位图`的数据结构来模拟策略组合的信息。最后`组合`问题完全被抽象成`信息流`。

### 题目
Given a set of distinct integers, nums, return all possible subsets.

Note: The solution set must not contain duplicate subsets.

For example,
If nums = `[1,2,3]`, a solution is:
```
[
  [3],
  [1],
  [2],
  [1,2,3],
  [1,3],
  [2,3],
  [1,2],
  []
]
```

### 回溯算法
这种题，第一个想到的一定是回溯算法，把问题抽象成：对数组中的每一个数字，都有两种选择`选择添加`和`选择不添加`。对`[1,2,3,4]`来说，其中一种组合`[1,3]`就是一种策略`[添加，不添加，添加，不添加]`的结果。

#### 代码
优先`添加`。
```java
public class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        if (nums.length == 0) { return res; }
        backtracking(res,new ArrayList<Integer>(),nums,0);
        return res;
    }
    public void backtracking(List<List<Integer>> res, List<Integer> temp, int[] nums, int cursor) {
        if (cursor == nums.length) { res.add(new ArrayList<Integer>(temp)); return; }
        temp.add(nums[cursor]); // 优先添加
        backtracking(res,temp,nums,cursor+1);
        temp.remove(temp.size()-1);
        backtracking(res,temp,nums,cursor+1);
    }
}
```

反过来优先`不添加`。一回事。
```java
public class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        if (nums.length == 0) { return res; }
        backtracking(res,new ArrayList<Integer>(),nums,0);
        return res;
    }
    public void backtrackingReverse(List<List<Integer>> res, List<Integer> temp, int[] nums, int cursor) {
        if (cursor == nums.length) { res.add(new ArrayList<Integer>(temp)); return; }
        backtracking(res,temp,nums,cursor+1); //优先不添加
        temp.add(nums[cursor]);
        backtracking(res,temp,nums,cursor+1);
    }
}
```

#### 结果
![subsets-1](/images/leetcode/subsets-1.png)

### 抽象成位操作
像`[添加，不添加，添加，不添加]`这样的策略组合，可以用一个`bit map`位图来表示，就是`1010`。那么对于四个数字`[1,2,3,4]`，可能的策略组合表示成位图就是在,
```
0000 ~ 1111 之间
```

所以完全可以用一个`int`来表示这个位图。当有`4`个数字时，这个`int`至少需要能表示`1111`，需要用$$2^4$$，`16`，换算成二进制是`10000`来表示。所以对于`n`个数，需要$$2^n$$来做`位图`。

然后用`bitmap & 1`来切下最末一位。用`>>`把`mask`往右退一位。
```
1234    nums
0101    bitmap
0001    & mask = 1
-------------------
0001    位图3位为：1

1234
0101    bitmap
0010    & mask << 1
-------------------
0000    位图2位为：0
```

#### 代码
```java
public class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        if (nums.length == 0) { return res; }
        int numLen = nums.length;
        int maxMap = (int)Math.pow((double)2,(double)numLen);
        List<Integer> temp = new ArrayList<>();
        for (int bitmap = 0; bitmap < maxMap; bitmap++) { // 从00...00到11...11
            temp.clear();
            for (int pos = numLen-1, mask = 1; pos >= 0; mask = mask << 1, pos--) { // pos和mask都从最后一位开始前移
                if ((bitmap & mask) == mask) { // 这一位为1
                    temp.add(nums[pos]);
                }
            }
            res.add(new ArrayList<Integer>(temp));
        }
        return res;
    }
}
```

#### 结果
![subsets-2](/images/leetcode/subsets-2.png)

### 解法3 笨办法
不光只有`回溯算法`和`位图`可以解。
```
起始subset集为：[]
添加S0后为：[], [S0]
添加S1后为：[], [S0], [S1], [S0, S1]
添加S2后为：[], [S0], [S1], [S0, S1], [S2], [S0, S2], [S1, S2], [S0, S1, S2]
```
观察上面的过程，显然规律为添加`Si`后，新增的subset为克隆现有的所有subset，并在它们后面都加上Si。

#### 迭代版
```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    if (nums.length == 0) { return res; }
    res.add(new ArrayList<Integer>());
    for (int num : nums) {
        int size = res.size();
        for (int i = 0; i < size; i++) {
            List<Integer> temp = new ArrayList<Integer>(res.get(i));
            temp.add(num);
            res.add(temp);
        }
    }
    return res;
}
```

#### 递归版
```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    if (nums.length == 0) { return res; }
    res.add(new ArrayList<Integer>());
    recursive(res,nums,0);
    return res;
}
public void recursive(List<List<Integer>> res, int[] nums, int cursor) {
    if (cursor == nums.length) { return; }
    List<List<Integer>> newComponent = new ArrayList<>();
    for (List<Integer> ele: res) {
        List<Integer> temp = new ArrayList<>(ele);
        temp.add(nums[cursor]);
        newComponent.add(temp);
    }
    res.addAll(newComponent);
    recursive(res,nums,++cursor);
}
```

#### 结果
![subsets-3](/images/leetcode/subsets-3.png)
