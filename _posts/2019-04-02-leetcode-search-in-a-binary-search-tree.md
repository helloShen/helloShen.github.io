---
layout: post
title: "Leetcode - Algorithm - Search In A Binary Search Tree "
date: 2019-04-02 12:39:56
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search tree", "tree"]
level: "easy"
description: >
---

### 题目
Given the root node of a binary search tree (BST) and a value. You need to find the node in the BST that the node's value equals the given value. Return the subtree rooted with that node. If such node doesn't exist, you should return NULL.

For example,
```
Given the tree:
        4
       / \
      2   7
     / \
    1   3
```
And the value to search: 2
You should return this subtree:
```
      2     
     / \   
    1   3
```

In the example above, if we want to search the value 5, since there is no node with value 5, we should return NULL.

Note that an empty tree is represented by NULL, therefore you would see the expected output (serialized tree format) as `[]`, not `null`.

### 二叉搜索树的基本`search()`操作
最基本的二叉树递归检索。

#### 代码
```java
class Solution {
    public TreeNode searchBST(TreeNode root, int val) {
        if (root == null || root.val == val) return root;
        return (root.val < val)? searchBST(root.right, val) : searchBST(root.left, val);
    }
}
```

#### 结果
![search-in-a-binary-search-tree-1](/images/leetcode/search-in-a-binary-search-tree-1.png)
