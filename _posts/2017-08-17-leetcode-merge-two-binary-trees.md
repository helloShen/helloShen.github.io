---
layout: post
title: "Leetcode - Algorithm - Merge Two Binary Trees "
date: 2017-08-17 17:43:30
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree"]
level: "easy"
description: >
---

### 题目
Given two binary trees and imagine that when you put one of them to cover the other, some nodes of the two trees are overlapped while the others are not.

You need to merge them into a new binary tree. The merge rule is that if two nodes overlap, then sum node values up as the new value of the merged node. Otherwise, the NOT null node will be used as the node of new tree.

Example 1:
```
Input:
	Tree 1                     Tree 2                  
          1                         2                             
         / \                       / \                            
        3   2                     1   3                        
       /                           \   \                      
      5                             4   7                  
Output:
Merged tree:
	     3
	    / \
	   4   5
	  / \   \
	 5   4   7
```
Note: The merging process must start from the root nodes of both trees.

### 基本思路
遇到二叉树，第一个想法肯定是用递归。关键是能不能两棵树一起递归。设置递归条件的时候要考虑到其中一棵树此节点为空的情况。

#### 代码
```java
public class Solution {
        public TreeNode mergeTrees(TreeNode t1, TreeNode t2) {
            if (t1 == null && t2 == null) { return null; }
            TreeNode t1Left = null, t1Right = null;
            TreeNode t2Left = null, t2Right = null;
            int sum = 0;
            if (t1 != null) {
                sum += t1.val;
                t1Left = t1.left; t1Right = t1.right;
            }
            if (t2 != null) {
                sum += t2.val;
                t2Left = t2.left; t2Right = t2.right;
            }
            TreeNode newNode = new TreeNode(sum);
            newNode.left = mergeTrees(t1Left,t2Left);
            newNode.right = mergeTrees(t1Right,t2Right);
            return newNode;
        }
}
```

#### 结果
![merge-two-binary-trees-1](/images/leetcode/merge-two-binary-trees-1.png)
