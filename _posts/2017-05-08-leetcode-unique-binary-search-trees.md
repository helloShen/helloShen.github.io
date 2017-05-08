---
layout: post
title: "Leetcode - Algorithm - Unique Binary Search Trees "
date: 2017-05-08 00:12:33
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","dynamic programming","divid and conquer"]
level: "medium"
description: >
---

### 题目
Given n, how many structurally unique BST's (binary search trees) that store values 1...n?

For example,
Given n = 3, there are a total of 5 unique BST's.
```
   1         3     3      2      1
    \       /     /      / \      \
     3     2     1      1   3      2
    /     /       \                 \
   2     1         2                 3
```

### 分治法
根据`Unique Binary Search Trees Two`最后一种解法，分治法，
![unique-binary-search-trees-two-bst](/images/leetcode/unique-binary-search-trees-two-bst.png)

所有的节点包含`1~n`的连续数字。所以每一棵树都分为`1 ~ k-1`,`k`,`k+1 ~ n`，三个部分。用分治法可以很简单地得到结果。终结条件是当`[1,k-1]`和`[k+1,n]`空间为空时，返回只包含一个`null`元素的`List`。

我们当然不需要真的画出树结构，只需要知道总结出一条递归公式：

> $$num(n) = \sum_{k=1}^{n}num(1,k-1) * num(k+1,n)$$

#### 代码
```java
public class Solution {
    public int numTrees(int n) {
        if (n <= 0) { return 0; }
        return dac(1,n);
    }
    public int dac(int start, int end) {
        if (start > end) { return 1; }
        int res = 0;
        for (int root = start; root <=end; root++) {
            res += (dac(start,root-1) * dac(root+1,end));
        }
        return res;
    }
}
```

#### 结果
超时很正常，因为没有用动态规划。
![unique-binary-search-trees-1](/images/leetcode/unique-binary-search-trees-1.png)


### 动态规划
用一个备忘录`int[][]`记录所有计算过的子问题的结果。

#### 代码
```java
public class Solution {
    public int numTrees(int n) {
        if (n <= 0) { return 0; }
        int[][] memo = new int[n+1][n+1];
        return dac(1,n,memo);
    }
    public int dac(int start, int end, int[][] memo) {
        if (start > end) { return 1; }
        int log = memo[start][end];
        if (log > 0) { return log; }
        int res = 0;
        for (int root = start; root <=end; root++) {
            res += (dac(start,root-1,memo) * dac(root+1,end,memo));
        }
        memo[start][end] = res;
        return res;
    }
}
```

#### 结果
已经非常不错。
![unique-binary-search-trees-2](/images/leetcode/unique-binary-search-trees-2.png)


### 进一步优化： $$num(n) = \sum_{k=1}^{n}num(k-1) * num(n-k)$$
这里有一个小`trick`：如果只需要计算不同结构的数量，`1,2,3`和`2,3,4`和`3,4,5`都是一样的，因为数字个数相等。所以只要用`num(3)`就可以表示所有连续的3个数字的二叉树结构数。所以只需要一个以为数组`int[]`就可以储存所有历史记录。

#### 代码
```java
public class Solution {
    public int numTrees(int n) {
        if (n <= 0) { return 0; }
        int[] memo = new int[n+1];
        memo[0] = 1; memo[1] = 1;
        for (int i = 2; i <=n; i++) {
            for (int j = 1; j <= i; j++) {
                memo[i] += memo[j-1] * memo[i-j];
            }
        }
        return memo[n];
    }
}
```

#### 结果
银弹！
![unique-binary-search-trees-3](/images/leetcode/unique-binary-search-trees-3.png)
