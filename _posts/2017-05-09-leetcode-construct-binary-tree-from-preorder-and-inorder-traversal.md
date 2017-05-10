---
layout: post
title: "Leetcode - Algorithm - Construct Binary Tree From Preorder And Inorder Traversal (draft)"
date: 2017-05-09 23:55:14
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: "medium"
description: >
---

### 题目
Given preorder and inorder traversal of a tree, construct the binary tree.

Note:
You may assume that duplicates do not exist in the tree.

### 顺序遍历`preorder` $$O(n^2)$$
遍历`preorder`，用根据节点在`inorder`中的顺序，决定插在哪里。假设现有`preorder=[8,2,10,11]`，`inorder=[2,8,10,11]`。其中`2-8-10`已经放好，最后一个`11`。插入的时候从根节点开始比较在`inorder`中的顺序，
```
  8
 / \
2  10
```
`inorder`中`11`在`8`的后面，`11`就应该插在`8`的右节点，如果`8`的右节点不为空，则和右节点的`10`比较，`11`在`inorder`中比`10`还要靠后，因此，插入作为`10`的右节点。
```
      8
     / \
    2  10
         \
         11
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
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length == 0 || inorder.length == 0 || preorder.length != inorder.length) { return null; }
        Map<Integer,Integer> inorderMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) { inorderMap.put(inorder[i],i); }
        TreeNode root = new TreeNode(preorder[0]);
        for (int i = 1; i < preorder.length; i++) {
            int index = inorderMap.get(preorder[i]);
            if (index == -1) { return null; } // ERROR: find node in preorder but not inorder
            TreeNode cur = root;
            while (cur != null) {
                if (inorderMap.get(cur.val) < index) { // 新节点应当在这个节点的右子树
                    if (cur.right == null) {
                        cur.right = new TreeNode(preorder[i]); break;
                    } else {
                        cur = cur.right;
                    }
                } else { // 新节点应当在这个节点的左子树
                    if (cur.left == null) {
                        if (cur.right != null) { // ERROR: find conflit of preorder & inorder
                            return null;
                        } else {
                            cur.left = new TreeNode(preorder[i]); break;
                        }
                    } else {
                        cur = cur.left;
                    }
                }
            }
        }
        return root;
    }
}
```

#### 结果
算法是对的。但超时了。
![construct-binary-tree-from-preorder-and-inorder-traversal-1](/images/leetcode/construct-binary-tree-from-preorder-and-inorder-traversal-1.png)


### 解法2
估计是把`inorder`转换成`List`开销太大。现在通过在`inorder`上加个指针试试。

#### 代码
```java

```

#### 结果
![construct-binary-tree-from-preorder-and-inorder-traversal-2](/images/leetcode/construct-binary-tree-from-preorder-and-inorder-traversal-2.png)


### 解法3

#### 代码
```java

```

#### 结果
![construct-binary-tree-from-preorder-and-inorder-traversal-3](/images/leetcode/construct-binary-tree-from-preorder-and-inorder-traversal-3.png)
