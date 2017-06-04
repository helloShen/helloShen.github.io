---
layout: post
title: "Leetcode - Algorithm - Binary Tree Postorder Traversal "
date: 2017-06-03 21:31:49
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree","tree","stack"]
level: "hard"
description: >
---

### 题目
Given a binary tree, return the postorder traversal of its nodes' values.

For example:
Given binary tree `{1,#,2,3}`,
```
   1
    \
     2
    /
   3
```
return `[3,2,1]`.

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
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) { return res; }
        res.addAll(postorderTraversal(root.left));
        res.addAll(postorderTraversal(root.right));
        res.add(root.val);
        return res;
    }
}
```

#### 结果
![binary-tree-postorder-traversal-1](/images/leetcode/binary-tree-postorder-traversal-1.png)


### 迭代版
`Post Order`的迭代不像`Pre Order`的迭代这么直观地从右往左遍历。而是 **相反地从左往右遍历**。还是要用一个`Stack`缓存节点。以下面的树为例，
```
     1
   /   \
  2     3
 / \   / \
4   5 6   7
```
1. `Stack`开头压入`1`
2. `Stack`读取`1`， `res`开头压入`1`
3. `Stack`开头压入`2`， 开头压入`3`
4. `Stack`读取`3`，`res`开头压入`3`
5. `Stack`开头压入`6`，开头压入`7`
6. ...
7. ...


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
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> res = new LinkedList<>();
        Deque<TreeNode> buffer = new LinkedList<TreeNode>();
        buffer.offerFirst(root);
        while (!buffer.isEmpty()) {
            TreeNode node = buffer.pollFirst();
            if (node != null) {
                buffer.offerFirst(node.left);
                buffer.offerFirst(node.right);
                res.add(0,node.val);
            }
        }
        return res;
    }
}
```

#### 结果
![binary-tree-postorder-traversal-2](/images/leetcode/binary-tree-postorder-traversal-2.png)
