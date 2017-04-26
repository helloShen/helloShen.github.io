---
layout: post
title: "Leetcode - Algorithm - Combinations "
date: 2017-04-25 17:00:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["backtracking","dynamic programming"]
level: "medium"
description: >
---

### 题目
Given two integers n and k, return all possible combinations of k numbers out of 1 ... n.

For example,
If n = 4 and k = 2, a solution is:
```
[
  [2,4],
  [3,4],
  [2,3],
  [1,2],
  [1,3],
  [1,4],
]
```

### 回溯算法 $$O(n^k)$$
完全可以用`[from,to)`来表示范围。不用真的`1,2,3,4,5,6,7,8,9,10,....`都写出来。

#### 代码
```java
public class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        backtracking(res,temp,k,1,n+1);
        return res;
    }
    // [from,to):  from=inclusive,  to=exclusive
    public void backtracking(List<List<Integer>> res, List<Integer> temp, int remain, int from, int to) {
        if (remain == 0) { res.add(new ArrayList<Integer>(temp)); return; }
        for (int i = from; i < to; i++) {
            temp.add(i);
            backtracking(res,temp,remain-1,i+1,to);
            temp.remove(temp.size()-1);
        }
    }
}
```

#### 结果
前面还有一个peek。还有银弹没找到。
![combinations-2](/images/leetcode/combinations-2.png)


### 基于排列组合公式的递归法
基于排列组合公式：
> C(n,k) = C(n-1, k-1) U n + C(n-1,k)

很容易写出递归。

#### 代码
```java
public class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> res = new ArrayList<>();
        if (k == n) {
            List<Integer> list = new ArrayList<>();
            for (int i = 1; i <= n; i++) { list.add(i); }
            res.add(list);
            return res;
        }
        if (k == 1) {
            for (int i = 1; i <= n; i++) {
                List<Integer> temp = new ArrayList<>();
                temp.add(i);
                res.add(temp);
            }
            return res;
        }
        res = combine(n-1,k-1);
        for (List<Integer> list : res) { list.add(n); }
        res.addAll(combine(n-1,k));
        return res;
    }
}
```

#### 简洁版代码
把终结条件再往下推一层。像`k=1`和`k=n`这样最基本的迭代都省了。
```java
public class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> res = new ArrayList<>();
        if (k > n) { return res; }
        if (k == 0) {
            res.add(new ArrayList<Integer>());
            return res;
        }
        res = combine(n-1,k-1);
        for (List<Integer> list : res) { list.add(n); }
        res.addAll(combine(n-1,k));
        return res;
    }
}
```

#### 结果
已经很不错了。
![combinations-3](/images/leetcode/combinations-3.png)


### 动态规划
递归版在一些底层像`C00`,`C01`这样的子问题的重复计算很严重。用一个备忘录把所有计算过的结果都记下来。
```java
public class Solution {
    public List<List<Integer>> combine(int n, int k) {
        Map<String,List<List<Integer>>> memo = new HashMap<>();
        return dp(n,k,memo);
    }
    public List<List<Integer>> dp(int n, int k, Map<String,List<List<Integer>>> memo) {
        String key = "" + n + k;
        if (memo.containsKey(key)) { return memo.get(key); }
        List<List<Integer>> res = new ArrayList<>();
        if (k > n) {
            memo.put(key,res);
            return res;
        }
        if (k == 0) {
            res.add(new ArrayList<Integer>());
            memo.put(key,res);
            return res;
        }
        res = combine(n-1,k-1);
        for (List<Integer> list : res) { list.add(n); }
        res.addAll(combine(n-1,k));
        memo.put(key,res);
        return res;
    }
}
```

#### 结果
这里子问题本身的复杂度不高，很多`base case`本身都是`O(1)`直接就返回了。反而是记录表，查表的动作开销更大。
![combinations-4](/images/leetcode/combinations-4.png)
