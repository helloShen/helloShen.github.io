---
layout: post
title: "Leetcode - Algorithm - Count Univalue Subtrees "
date: 2017-07-22 15:12:09
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","recursion"]
level: "medium"
description: >
---

### 题目
Given a binary tree, count the number of uni-value subtrees.

A Uni-value subtree means all nodes of the subtree have the same value.

For example:
Given binary tree,
```
              5
             / \
            1   5
           / \   \
          5   5   5
```
return `4`.

### 基本思路
这种题是最典型的递归。需要从 **叶节点** 一步步递归上来。

### 递归

#### 不进入`root == null`这一层递归（不推荐）
```java
public int countUnivalSubtrees(TreeNode root) {
    count = 0;
    if (root == null) { return count; }
    recursion(root);
    return count;
}
private int count;
public boolean recursion(TreeNode root) {
    // base case
    if (root.left == null && root.right == null) { count++; return true; }
    // recursion
    boolean l = false, r = false;
    if (root.left != null) { l = recursion(root.left) && root.val == root.left.val; }
    if (root.right != null) { r = recursion(root.right) && root.val == root.right.val; }
    if (root.left == null) { if (r) { count++; } return r; }
    if (root.right == null) { if (l) { count++; } return l; }
    if (l & r) { count++; }
    return (l & r);
}
```

#### 更标准的以`root == null`作为`base case`（推荐）
这个代码的思路很清晰。层层推进，逻辑层次很分明。
```java
public int countUnivalSubtrees(TreeNode root) {
    count = 0;
    recursion(root);
    return count;
}
private int count = 0;
private boolean recursion(TreeNode root) {
    // base case
    if (root == null) { return true; }
    // recursion
    boolean l = recursion(root.left);
    boolean r = recursion(root.right);
    if (l && r) {
        if (root.left != null && root.val != root.left.val) { return false; }
        if (root.right != null && root.val != root.right.val) { return false; }
        count++; return true;
    } else {
        return false;
    }
}
```

#### 结果
![count-univalue-subtrees-1](/images/leetcode/count-univalue-subtrees-1.png)
