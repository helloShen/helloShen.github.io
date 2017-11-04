---
layout: post
title: "Leetcode - Algorithm - Maximum Binary Tree "
date: 2017-11-04 18:34:00
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree","tree"]
level: "medium"
description: >
---

### 题目
Given an integer array with no duplicates. A maximum tree building on this array is defined as follow:

1. The root is the maximum number in the array.
2. The left subtree is the maximum tree constructed from left part subarray divided by the maximum number.
3. The right subtree is the maximum tree constructed from right part subarray divided by the maximum number.

Construct the maximum tree by the given array and output the root node of this tree.

```
Example 1:
Input: [3,2,1,6,0,5]
Output: return the tree root node representing the following tree:
      6
    /   \
   3     5
    \    /
     2  0   
       \
        1
```

Note:
The size of the given array will be in the range [1,1000].

### 递归
从左往右遍历整个数组，可以逐步构建出符合要求的二叉树，以`[3,2,1,6,0,5]`为例，从`3`开始，
```
3

如果 2 < 3，插入为右子元素
3
 \
  2


如果 1 < 2，插入为右子元素
  3
   \
    2
     \
      1


如果 6 > 3，3成为6的左子元素
        6
       /
      3
       \
        2
         \
          1

如果 0 < 6，0成为6的右子元素
        6
       / \
      3   0
       \
        2
         \
          1

如果 5 > 0，5替代0成为6的右子元素，然后0成为5的左子元素
         6
       /   \
      3     5
       \   /
        2 0
         \
          1
```

二叉树和递归非常契合。整个过程用一个递归`insert()`函数写出来很简单。

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
class Solution {
    public TreeNode constructMaximumBinaryTree(int[] nums) {
        if (nums.length == 0) { return null; }
        TreeNode root = null;
        for (int num : nums) {
            root = insert(root,num);
        }
        return root;
    }
    private TreeNode insert(TreeNode root, int num) {
        TreeNode newNode = new TreeNode(num);
        if (root == null) {
            return newNode;
        } else {
            int val = root.val;
            if (val < num) {
                newNode.left = root;
                return newNode;
            } else {
                root.right = insert(root.right,num);
                return root;
            }
        }
    }
}
```

#### 结果
![maximum-binary-tree-1](/images/leetcode/maximum-binary-tree-1.png)
