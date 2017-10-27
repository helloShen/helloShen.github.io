---
layout: post
title: "Leetcode - Algorithm - Trim A Binary Search Tree "
date: 2017-10-27 18:38:02
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search tree"]
level: "easy"
description: >
---

### 题目
Given a binary search tree and the lowest and highest boundaries as L and R, trim the tree so that all its elements lies in [L, R] (R >= L). You might need to change the root of the tree, so the result should return the new root of the trimmed binary search tree.

Example 1:
```
Input:
    1
   / \
  0   2

  L = 1
  R = 2

Output:
    1
      \
       2
```

Example 2:
```
Input:
    3
   / \
  0   4
   \
    2
   /
  1

  L = 1
  R = 3

Output:
      3
     /
   2   
  /
 1
```

### 基本思路
二叉树的基本性质就是：
> 左子树的所有元素都小于当前节点的值，右子树的所有元素都大于当前节点值。

所以当发现某个节点值小于最小阈值，则此节点及其所有左子树可以删除。反之当节点值大于最大阈值，此节点及其所有右子树可以删除。

### 两次迭代遍历
第一次遍历删除所有小于最小阈值`L`的节点，第二次遍历删除所有大于最大阈值`R`的节点。

#### 代码
```java
class Solution {
    public TreeNode trimBST(TreeNode root, int L, int R) {
        TreeNode dummy = new TreeNode(0);
        dummy.left = root;
        TreeNode cur = root, pre = dummy;
        while (cur != null) {
            if (cur.val < L) {
                pre.left = cur.right;
                cur = cur.right;
            } else {
                pre = cur;
                cur = cur.left;
            }
        }
        dummy.right = dummy.left;
        dummy.left = null;
        cur = dummy.right; pre = dummy;
        while (cur != null) {
            if (cur.val > R) {
                pre.right = cur.left;
                cur = cur.left;
            } else {
                pre = cur;
                cur = cur.right;
            }
        }
        return dummy.right;
    }
}
```

#### 结果
![trim-a-binary-search-tree-1](/images/leetcode/trim-a-binary-search-tree-1.png)


### 两次遍历（递归版）

#### 代码
```java
class Solution {
        public TreeNode trimBST(TreeNode root, int L, int R) {
            TreeNode dummy = new TreeNode(0);
            dummy.left = root;
            trimSmaller(dummy,L);
            dummy.right = dummy.left;
            trimGreater(dummy,R);
            return dummy.right;
        }
        private void trimSmaller(TreeNode root, int L) {
            if (root.left == null) { return; }
            if (root.left.val < L) {
                root.left = root.left.right;
                trimSmaller(root,L);
            } else {
                trimSmaller(root.left,L);
            }
        }
        private void trimGreater(TreeNode root, int R) {
            if (root.right == null) { return; }
            if (root.right.val > R) {
                root.right = root.right.left;
                trimGreater(root,R);
            } else {
                trimGreater(root.right,R);
            }
        }
}
```

#### 结果
![trim-a-binary-search-tree-2](/images/leetcode/trim-a-binary-search-tree-2.png)


### 一次遍历（递归版）
这个是最优解法。

#### 代码
```java
class Solution {
    public TreeNode trimBST(TreeNode root, int L, int R) {
        if (root == null) { return null; }

        if (root.val < L) { return trimBST(root.right,L,R); }
        if (root.val > R) { return trimBST(root.left,L,R); }

        root.left = trimBST(root.left,L,R); root.right = trimBST(root.right,L,R);
        return root;
    }
}
```

#### 结果
![trim-a-binary-search-tree-3](/images/leetcode/trim-a-binary-search-tree-3.png)
