---
layout: post
title: "Leetcode - Algorithm - Diameter Of Binary Tree "
date: 2017-11-30 19:21:36
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree"]
level: "easy"
description: >
---

### 题目
Given a binary tree, you need to compute the length of the diameter of the tree. The diameter of a binary tree is the length of the longest path between any two nodes in a tree. This path may or may not pass through the root.

Example:
Given a binary tree
```
          1
         / \
        2   3
       / \     
      4   5    
```

Return 3, which is the length of the path [4,2,1,3] or [5,2,1,3].

Note: The length of path between two nodes is represented by the number of edges between them.


### 递归
问题可以转化成：
> 任意一棵子树的周长（可以经过根节点）中最长的一个。

一棵树经过根节点的周长，更好算，就是右子树到某个叶节点的最长路径，加上左子树到某个叶节点的最长路径。

这样就可以用递归解决。

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
class Solution {

    private static int diameter = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        diameter = 0;
        depth(root);
        return diameter;
    }
    private int depth(TreeNode root) {
        if (root == null) { return -1; }
        int left = depth(root.left) + 1;
        int right = depth(root.right) + 1;
        diameter = Math.max(diameter, (left + right)); // 左，右子树的最大深度相加
        return Math.max(left,right);
    }
}
```

#### 结果
![diameter-of-binary-tree-1](/images/leetcode/diameter-of-binary-tree-1.png)
