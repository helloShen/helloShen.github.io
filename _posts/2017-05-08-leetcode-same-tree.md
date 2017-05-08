---
layout: post
title: "Leetcode - Algorithm - Same Tree "
date: 2017-05-08 19:01:17
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: "easy"
description: >
---

### 题目
Given two binary trees, write a function to check if they are equal or not.

Two binary trees are considered equal if they are structurally identical and the nodes have the same value.

### 分治法递归
`BST`就用分治递归。

#### 代码
```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
public class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) { return true; }
        if (p != null && q != null && p.val == q.val) {
            return isSameTree(p.left,q.left) && isSameTree(p.right,q.right);
        }
        return false;
    }
}
```

#### 结果
![same-tree-1](/images/leetcode/same-tree-1.png)
