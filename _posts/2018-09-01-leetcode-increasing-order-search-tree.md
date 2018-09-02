---
layout: post
title: "Leetcode - Algorithm - Increasing Order Search Tree "
date: 2018-09-01 23:04:30
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search tree","tree","binary tree"]
level: "easy"
description: >
---

### 题目
Given a tree, rearrange the tree in in-order so that the leftmost node in the tree is now the root of the tree, and every node has no left child and only 1 right child.

Example 1:
```
Input: [5,3,6,2,4,null,8,1,null,null,null,7,9]

       5
      / \
    3    6
   / \    \
  2   4    8
 /        / \
1        7   9

Output: [1,null,2,null,3,null,4,null,5,null,6,null,7,null,8,null,9]

 1
  \
   2
    \
     3
      \
       4
        \
         5
          \
           6
            \
             7
              \
               8
                \
                 9
```                 

### `in-order`遍历二叉树
然后根据拿到的数值，重新构造一棵新的二叉树。

#### 代码
```java
class Solution {
    public TreeNode increasingBST(TreeNode root) {
        dummy = new TreeNode(0);
        TreeNode head = dummy;
        inOrder(root);
        return head.right;   
    }
    private TreeNode dummy;
    private void inOrder(TreeNode root) {
        if (root == null) {
            return;
        }
        inOrder(root.left);
        dummy.right = new TreeNode(root.val);
        dummy = dummy.right;
        inOrder(root.right);
    }
}
```
