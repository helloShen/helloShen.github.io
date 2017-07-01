---
layout: post
title: "Leetcode - Algorithm - Invert Binary Tree "
date: 2017-07-01 12:05:41
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree"]
level: "easy"
description: >
---

### 题目
Invert a binary tree.
```
     4
   /   \
  2     7
 / \   / \
1   3 6   9
```
to
```
     4
   /   \
  7     2
 / \   / \
9   6 3   1
```

Trivia:
This problem was inspired by this original tweet by Max Howell:
> Google: 90% of our engineers use the software you wrote (Homebrew), but you can’t invert a binary tree on a whiteboard so fuck off.

### 主要思路
遇到二叉树，递归就没错啦。

### 递归交换左右子树

#### 代码
```java
public class Solution {
    public TreeNode invertTree(TreeNode root) {
        recur(root);
        return root;
    }
    private void recur(TreeNode root) {
        if (root == null) { return; }
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;
        recur(root.left);
        recur(root.right);
    }
}
```

再简单点，可以这样写，
```java
public class Solution {
    public TreeNode invertTree(TreeNode root) {
        if (root == null) { return null; }
        TreeNode temp = root.left;
        root.left = invertTree(root.right);
        root.right = invertTree(temp);
        return root;
    }
}
```

#### 结果
![invert-binary-tree-1](/images/leetcode/invert-binary-tree-1.png)
