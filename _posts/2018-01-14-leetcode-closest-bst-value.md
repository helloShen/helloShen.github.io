---
layout: post
title: "Leetcode - Algorithm - Closest Bst Value "
date: 2018-01-14 18:50:53
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","binary search tree"]
level: "easy"
description: >
---

### 题目
Given a non-empty binary search tree and a target value, find the value in the BST that is closest to the target.

Note:
* Given target value is a floating point.
* You are guaranteed to have only one unique value in the BST that is closest to the target.


### 思路
其实就相当于往二叉树里插入这个目标数。需要根据它路过的节点，维护两个变量：
1. 最近一个大于目标数的数
2. 最近一个小于目标数的数

最后比较这一对上界和下界，取更接近的那个。

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

    private Integer gt = null;
    private Integer ls = null;

    public int closestValue(TreeNode root, double target) {
        gt = null; ls = null;
        preOrder(root,target);
        if (gt == null) { return ls; }
        if (ls == null) { return gt; }
        return ((target - ls) >= (gt - target))? gt : ls;
    }
    private void preOrder(TreeNode root, double target) {
        if (root == null) { return; }
        double val = (double)root.val;
        if (val == target) {
            gt = root.val;
        } else if (val > target) {
            gt = root.val;
            preOrder(root.left, target);
        } else {
            ls = root.val;
            preOrder(root.right, target);
        }
    }

}
```

#### 结果
![closest-bst-value-1](/images/leetcode/closest-bst-value-1.png)
