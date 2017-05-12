---
layout: post
title: "Leetcode - Algorithm - Path Sum "
date: 2017-05-12 02:22:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search","tree"]
level: "easy"
description: >
---

### 题目
Given a binary tree and a sum, determine if the tree has a root-to-leaf path such that adding up all the values along the path equals the given sum.

For example:
Given the below binary tree and sum = `22`,
```
              5
             / \
            4   8
           /   / \
          11  13  4
         /  \      \
        7    2      1
```
return true, as there exist a root-to-leaf path `5->4->11->2` which sum is `22`.

### 回溯递归
深度优先地递归向下探索，一旦得到正确结果就返回，否则回退一层继续向下递归。

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
    public boolean hasPathSum(TreeNode root, int sum) {
        if (root == null) { return false; }
        int remain = sum - root.val;
        if (root.left == null && root.right == null && remain == 0) { return true; }
        if (root.left != null && hasPathSum(root.left,remain)) { return true; }
        if (root.right != null && hasPathSum(root.right,remain)) { return true; }
        return false;
    }
}
```

#### 稍微优化代码
```java
public class Solution {
    public boolean hasPathSum(TreeNode root, int sum) {
        if (root == null) { return false; }
        if (root.left == null && root.right == null) { return sum == root.val; }
        int remain = sum - root.val;
        return hasPathSum(root.left,remain) || hasPathSum(root.right,remain);
    }
}
```

#### 结果
银弹！
![path-sum-1](/images/leetcode/path-sum-1.png)
