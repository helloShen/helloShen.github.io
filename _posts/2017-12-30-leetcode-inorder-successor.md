---
layout: post
title: "Leetcode - Algorithm - Inorder Successor "
date: 2017-12-30 21:12:24
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree","tree"]
level: "medium"
description: >
---

### 题目
Given a binary search tree and a node in it, find the in-order successor of that node in the BST.

Note: If the given node has no in-order successor in the tree, return null.

### 用`Stack`迭代遍历二叉树

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
    public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode cur = root;
        boolean found = false;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            if (found) { return cur; }
            if (cur == p) { found = true; }
            cur = (cur.right == null)? null : cur.right;
        }
        return null;
    }
}
```

#### 结果
![inorder-successor-1](/images/leetcode/inorder-successor-1.png)


### 递归遍历二叉树

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
        public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
            TreeNode[] res = new TreeNode[1];
            helper(root,p,res);
            return res[0];
        }
        private boolean helper(TreeNode root, TreeNode p, TreeNode[] res) {
            if (root == null) { return false; }
            boolean foundInLeft = helper(root.left,p,res);
            if (foundInLeft) {
                if (res[0] == null) { res[0] = root; }
                return true;
            }
            if (root == p) {
                if (root.right != null) {
                    res[0] = root.right;
                    while (res[0].left != null) { res[0] = res[0].left; }
                }
                return true;
            }
            if (helper(root.right,p,res)) { return true; }
            return false;
        }
}
```

#### 结果
![inorder-successor-2](/images/leetcode/inorder-successor-2.png)


### 更简洁的递归遍历

#### 代码
```java
class Solution {
    public TreeNode inorderSuccessor(TreeNode root, TreeNode p) {
        if (root == null) { return null; }
        if (root.val <= p.val) {
            return inorderSuccessor(root.right,p);
        } else {
            TreeNode left = inorderSuccessor(root.left,p);
            return (left != null)? left : root;
        }
    }
}
```

#### 结果
![inorder-successor-3](/images/leetcode/inorder-successor-3.png)
