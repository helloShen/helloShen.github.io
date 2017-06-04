---
layout: post
title: "Leetcode - Algorithm - Binary Tree Preorder Traversal "
date: 2017-06-03 20:36:11
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree","tree","stack"]
level: "medium"
description: >
---

### 题目
Given a binary tree, return the preorder traversal of its nodes' values.

For example:
Given binary tree `{1,#,2,3}`,
```
   1
    \
     2
    /
   3
```
return `[1,2,3]`.

Note: Recursive solution is trivial, could you do it iteratively?

### 递归版
`Binary Tree`的问题，递归法是最简单的。

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
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) { return res; }
        res.add(root.val);
        res.addAll(preorderTraversal(root.left));
        res.addAll(preorderTraversal(root.right));
        return res;
    }
}
```

#### 结果
![binary-tree-preorder-traversal-1](/images/leetcode/binary-tree-preorder-traversal-1.png)


### 迭代版
用一个`Stack`缓存接下来需要处理的节点。

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
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Deque<TreeNode> stack = new LinkedList<TreeNode>();
        stack.add(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pollFirst();
            if (node != null) {
                res.add(node.val);
                stack.offerFirst(node.right);
                stack.offerFirst(node.left);
            }
        }
        return res;
    }
}
```

#### 结果
![binary-tree-preorder-traversal-2](/images/leetcode/binary-tree-preorder-traversal-2.png)
