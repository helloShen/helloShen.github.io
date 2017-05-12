---
layout: post
title: "Leetcode - Algorithm - Minimum Depth Of Binary Tree "
date: 2017-05-12 01:36:11
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search","tree"]
level: "easy"
description: >
---

### 题目

### 自底向上的分治递归，$$O(n)$$
每层都是`min(leftDepth,rightDepth) + 1`。

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
    public int minDepth(TreeNode root) {
        if (root == null) { return 0; }
        if (root.left == null && root.right == null) { return 1; }
        if (root.left == null) { return minDepth(root.right) + 1; }
        if (root.right == null) { return minDepth(root.left) + 1; }
        return Math.min(minDepth(root.left),minDepth(root.right)) + 1;
    }
}
```

#### 结果
银弹！
![minimum-depth-of-binary-tree-1](/images/leetcode/minimum-depth-of-binary-tree-1.png)


### 解法2

#### 代码
```java

```

#### 结果
![minimum-depth-of-binary-tree-2](/images/leetcode/minimum-depth-of-binary-tree-2.png)


### 解法3

#### 代码
```java

```

#### 结果
![minimum-depth-of-binary-tree-3](/images/leetcode/minimum-depth-of-binary-tree-3.png)
