---
layout: post
title: "Leetcode - Algorithm - Construct Binary Tree From Inorder And Postorder Traversal "
date: 2017-05-11 16:37:12
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","array","depth first search"]
level: "medium"
description: >
---

### 题目
Given inorder and postorder traversal of a tree, construct the binary tree.

Note:
You may assume that duplicates do not exist in the tree.

### 分治递归 $$O(n)$$
和`ConstructBinaryTreeFromPreorderAndInorder`一样，假设二叉树是：
```
BFS: [[14], [7, 15], [1, 11, null, 17], [null, 6, 10, 13, 16, 18], [4, null, 8, null, 12, null, null, null, null, 20], [null, null, null, null, null, null, null, null]]
```
写成`inorder`和`postorder`形式就是，
```
Inorder: [1][4][6][7][8][10][11][12][13][14][15][16][17][18][20]
Postorder: [4][6][1][8][10][12][13][11][7][16][20][18][17][15][14]
```
每次都取`postorder`末尾元素，比如`14`，作为根节点。 然后在`inorder`中找到`14`元素的位置，并且据此把`inorder`分成3部分：
```
                                inorder
                                [14]
                              /      \
                             /        \
[1][4][6][7][8][10][11][12][13]      [15][16][17][18][20]
     左子树：size=9                         右子树：size=5
```
然后根据左子树和右子树的长度，把`postorder`中的左子树和右子树切开。原理就是虽然顺序不同，但`postorder`的左子树和`inorder`的左子树是完全相同的数字集合构成的。右子树也是。
```
postorder:
[4][6][1][8][10][12][13][11][7]    +    [16][20][18][17][15]    +    [14]
     左子树：size=9                         右子树：size=5              根
```

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
    public TreeNode buildTree(int[] inorder, int[] postorder) {
        if (inorder.length == 0 || postorder.length == 0) { return null; }
        Map<Integer,Integer> inorderIndexMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) { inorderIndexMap.put(inorder[i],i); }
        TreeNode res = buildTreeRecur(postorder,postorder.length-1,0,inorder.length-1,inorderIndexMap);
        return res;
    }
    public TreeNode buildTreeRecur(int[] postorder, int cur, int lo, int hi, Map<Integer,Integer> inorderIndexMap) {
        if (lo > hi) { return null; }
        TreeNode root = new TreeNode(postorder[cur]);
        int indexInInorder = inorderIndexMap.get(postorder[cur]);
        int rightSize = hi - indexInInorder;
        root.left = buildTreeRecur(postorder,cur-rightSize-1,lo,indexInInorder-1,inorderIndexMap);
        root.right = buildTreeRecur(postorder,cur-1,indexInInorder+1,hi,inorderIndexMap);
        return root;
    }
}
```

#### 结果
直接银弹！
![construct-binary-tree-from-inorder-and-postorder-traversal-1](/images/leetcode/construct-binary-tree-from-inorder-and-postorder-traversal-1.png)
