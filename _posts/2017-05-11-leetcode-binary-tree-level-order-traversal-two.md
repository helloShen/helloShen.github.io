---
layout: post
title: "Leetcode - Algorithm - Binary Tree Level Order Traversal Two "
date: 2017-05-11 18:58:47
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","breadth first search"]
level: "easy"
description: >
---

### 题目
Given a binary tree, return the bottom-up level order traversal of its nodes' values. (ie, from left to right, level by level from leaf to root).

For example:
Given binary tree [3,9,20,null,null,15,7],
```
    3
   / \
  9  20
    /  \
   15   7
```
return its bottom-up level order traversal as:
```
[
  [15,7],
  [9,20],
  [3]
]
```

### 老办法，用一个`List`缓存每一行的节点
和`BinaryTreeLevelOrderTraversal`的区别是，把每行的结果插在结果列表的最前面。

#### 跌代版
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
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        if (root == null) { return res; }
        List<TreeNode> buffer = new ArrayList<>();
        buffer.add(root);
        while (!buffer.isEmpty()) {
            List<Integer> nums = new ArrayList<>();
            int size = buffer.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = buffer.remove(0);
                if (node != null) {
                    nums.add(node.val);
                    buffer.add(node.left);
                    buffer.add(node.right);
                }
            }
            if (!nums.isEmpty()) { res.add(0,nums); }
        }
        return res;
    }
}
```

#### 递归版
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
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        if (root == null) { return res; }
        List<TreeNode> buffer = new ArrayList<>();
        buffer.add(root);
        levelRecur(buffer,res);
        return res;
    }
    public void levelRecur(List<TreeNode> buffer, List<List<Integer>> res) {
        if (buffer.isEmpty()) { return; }
        List<Integer> nums = new ArrayList<>();
        int size = buffer.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = buffer.remove(0);
            if (node != null) {
                nums.add(node.val);
                buffer.add(node.left);
                buffer.add(node.right);
            }
        }
        if (!nums.isEmpty()) { res.add(0,nums); }
        levelRecur(buffer,res);
    }
}
```

#### 结果
![binary-tree-level-order-traversal-two-1](/images/leetcode/binary-tree-level-order-traversal-two-1.png)


### 分治递归 $$O(n)$$
老老实实分治递归。每次递归都记录当前递归深度（行号），利用行号找到储存当前行数字的那个`List`。这也是一种靠谱的`Level Order`的常规解法。

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
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        recur(root,res,0);
        Collections.reverse(res);
        return res;
    }
    public void recur(TreeNode root, List<List<Integer>> res, int level) {
        if (root == null) { return; }
        if (level == res.size()) {
            List<Integer> newLevel = new ArrayList<>();
            newLevel.add(root.val);
            res.add(newLevel);
        } else {
            List<Integer> list = res.get(level);
            list.add(root.val);
        }
        recur(root.left,res,level+1);
        recur(root.right,res,level+1);
    }
}
```

#### 结果
![binary-tree-level-order-traversal-two-2](/images/leetcode/binary-tree-level-order-traversal-two-2.png)
