---
layout: post
title: "Leetcode - Algorithm - Balanced Binary Tree "
date: 2017-05-12 00:40:31
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search","tree"]
level: "easy"
description: >
---

### 题目
Given a binary tree, determine if it is height-balanced.

For this problem, a height-balanced binary tree is defined as a binary tree in which the depth of the two subtrees of every node never differ by more than 1.

### 分治法递归，$$O(n)$$
对二叉树的题目，递归分治总是奏效。对每个节点，都检查它两个节点的深度。如果深度差`>1`，则返回`-1`就代表不是平衡二叉树。

递归是自底向上的，终结条件是：`null`的子节点深度为`0`。每往上推一层，深度`+1`。

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
    public boolean isBalanced(TreeNode root) {
        int depth = depth(root);
        return (depth != -1);
    }
    // return -1, if find some node is not balanced
    public int depth(TreeNode root) {
        if (root == null) { return 0; }
        int leftDepth = depth(root.left);
        int rightDepth = depth(root.right);
        if (leftDepth == -1 || rightDepth == -1) {
            return -1;
        } else if (Math.abs(leftDepth-rightDepth) > 1) {
            return -1;
        } else {
            return Math.max(leftDepth,rightDepth) + 1;
        }
    }
}
```

#### 结果
银弹！
![balanced-binary-tree-1](/images/leetcode/balanced-binary-tree-1.png)


### 自顶向下，分治递归 （坏解法，不可取）$$O(n^2)$$
如果递归是自顶向下的，会重复解决很多次相同的子问题。计算第`n`层的深度，需要跑一遍`1~n`层。计算`n-1`层的深度，又需要跑一遍`1~n-1`层。复杂度是$$O(n^2)$$。

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
    public boolean isBalanced(TreeNode root) {
        if (root == null) { return true; }
        int leftDepth = depth(root.left);
        int rightDepth = depth(root.right);
        if (Math.abs(leftDepth-rightDepth) > 1) { return false; }
        return isBalanced(root.left) && isBalanced(root.right);
    }
    public int depth(TreeNode root) {
        if (root == null) { return 0; }
        return Math.max(depth(root.left),depth(root.right)) + 1;
    }
}
```

#### 结果
复杂度上升了几个量级。
![balanced-binary-tree-2](/images/leetcode/balanced-binary-tree-2.png)
