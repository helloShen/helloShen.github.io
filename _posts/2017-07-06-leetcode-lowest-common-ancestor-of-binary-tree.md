---
layout: post
title: "Leetcode - Algorithm - Lowest Common Ancestor Of Binary Tree "
date: 2017-07-06 15:38:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree"]
level: "medium"
description: >
---

### 题目
Given a binary tree, find the lowest common ancestor (LCA) of two given nodes in the tree.

According to the definition of LCA on Wikipedia: “The lowest common ancestor is defined between two nodes v and w as the lowest node in T that has both v and w as descendants (where we allow a node to be a descendant of itself).”
```
        _______3______
       /              \
    ___5__          ___1__
   /      \        /      \
   6      _2       0       8
         /  \
         7   4
```
For example, the lowest common ancestor (LCA) of nodes 5 and 1 is 3. Another example is LCA of nodes 5 and 4 is 5, since a node can be a descendant of itself according to the LCA definition.

### 递归分别找出通向`p`和`q`的路径

#### 代码
```java
/** 比较健壮的版本，适合扩展。完整找完Path，再比较。*/
public class SolutionV1 {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || p == null || q == null) { return null; } // defense
        Deque<TreeNode> pathP = getPath(root,p);
        Deque<TreeNode> pathQ = getPath(root,q);
        System.out.println("Path of P is: " + pathP);
        System.out.println("Path of Q is: " + pathQ);
        TreeNode commonAncestor = null;
        while (!pathP.isEmpty() && !pathQ.isEmpty()) {
            TreeNode currP = pathP.pollFirst();
            TreeNode currQ = pathQ.pollFirst();
            if (currP == currQ) {
                commonAncestor = currP;
            } else { break; }
        }
        return commonAncestor;
    }
    /**
     * root can be null, but target CANNOT
     * Return an empty Stack if nothing found.
     */
    public Deque<TreeNode> getPath(TreeNode root, TreeNode target) {
        Deque<TreeNode> path = new LinkedList<>();
        if (root == null) { return path; }
        if (root == target) { path.offerFirst(root); return path; }
        path = getPath(root.left,target);
        if (!path.isEmpty()) { path.offerFirst(root); return path; }
        path = getPath(root.right,target);
        if (!path.isEmpty()) { path.offerFirst(root); return path; }
        return path;
    }
}
```

#### 结果
![lowest-common-ancestor-of-binary-tree-1](/images/leetcode/lowest-common-ancestor-of-binary-tree-1.png)


### 用`Bitmap`在递归中判断状态

#### 代码
```java
public class Solution {
    private TreeNode lowestCommonAncestor = null;
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || p == null || q == null) { return null; }
        lowestCommonAncestor = null;
        recur(root,p,q);
        return lowestCommonAncestor;
    }
    /**
     * Using BITMAP
     *  1. nothing found in tree:           00 -> 0
     *  2. p found in tree:                 01 -> 1
     *  3. q found in tree:                 10 -> 2
     *  4. both p & q are found in tree:    11 -> 3
     */
    public int recur(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) { return 0; }
        int left = recur(root.left,p,q);
        if (left == 3) { return left; } // LCA found in left sub tree
        int right = recur(root.right,p,q);
        if (right == 3) { return right; } // LCA found in right sub tree
        int sub = left | right;
        if (sub == 3) { lowestCommonAncestor = root; return sub; }
        int curr = 0;
        if (root == p) { curr |= 1; }
        if (root == q) { curr |= 2; }
        curr |= sub;
        if (curr == 3) { lowestCommonAncestor = root; }
        return curr;
    }
}
```

#### 结果
![lowest-common-ancestor-of-binary-tree-2](/images/leetcode/lowest-common-ancestor-of-binary-tree-2.png)


### 耍小聪明的做法，淘汰空分支
本来比较分散的逻辑分支，现在集中起来。

#### 代码
```java
/**
 * 换个角度看问题：不用盯住哪些子树有p,q，而是盯住哪些子树什么也没有。
 * 只要在root树中，既没有p也没有q，就返回null。
 */
public class SolutionV3 {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) { return null; }
        if (root == p || root == q) { return root; }
        TreeNode left = lowestCommonAncestor(root.left,p,q);
        TreeNode right = lowestCommonAncestor(root.right,p,q);
        if (left == null) { // 淘汰左分支
            return right;
        } else if (right == null) { // 淘汰右分支
            return left;
        } else {
            return root;
        }
    }
}
```

#### 结果
![lowest-common-ancestor-of-binary-tree-3](/images/leetcode/lowest-common-ancestor-of-binary-tree-3.png)
