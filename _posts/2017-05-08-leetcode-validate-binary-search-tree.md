---
layout: post
title: "Leetcode - Algorithm - Validate Binary Search Tree "
date: 2017-05-08 16:27:06
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: "medium"
description: >
---

### 题目
Given a binary tree, determine if it is a valid binary search tree (BST).

Assume a BST is defined as follows:

The left subtree of a node contains only nodes with keys less than the node's key.
The right subtree of a node contains only nodes with keys greater than the node's key.
Both the left and right subtrees must also be binary search trees.
Example 1:
```
    2
   / \
  1   3
```
Binary tree `[2,1,3]`, return true.
Example 2:
```
    1
   / \
  2   3
```
Binary tree `[1,2,3]`, return false.

### 分治法递归
对于每个节点，我都比较`left`,`root`,`right`三个部分。
```
    1
   / \
  2   3
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
    public boolean isValidBST(TreeNode root) {
        return isValidBSTRecur(root,Integer.MIN_VALUE,Integer.MAX_VALUE);
    }
    public boolean isValidBSTRecur(TreeNode root, int min, int max) { // range inclusive
        if (root == null) { return true; }
        int value = root.val;
        if (root.left != null) {
            int left = root.left.val;
            if (left < min || left >= value) { return false; }
        }
        if (root.right != null) {
            int right = root.right.val;
            if (right > max || right <= value) { return false; }
        }
        return isValidBSTRecur(root.left,min,value-1) && isValidBSTRecur(root.right,value+1,max);
    }
}
```

#### 结果
![validate-binary-search-tree-1](/images/leetcode/validate-binary-search-tree-1.png)


### 把递归的终结条件往下推一层
把递归的范围缩小到每一个节点。每次递归只看当前节点是否落在合法区间内。`min < root.val < max`。需要注意边界`min`,`max`可能会溢出，需要要`Long`。

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
    public boolean isValidBST(TreeNode root) {
        return isValideBSTRecur(root,Long.MIN_VALUE,Long.MAX_VALUE);
    }
    public boolean isValideBSTRecur(TreeNode root, long min, long max) { // range inclusive
        if (root == null) { return true; }
        if (root.val < min || root.val > max) { return false; }
        return isValideBSTRecur(root.left,min,(long)root.val-1) && isValideBSTRecur(root.right,(long)root.val+1,max);
    }
}
```

#### 结果
![validate-binary-search-tree-2](/images/leetcode/validate-binary-search-tree-2.png)


### 用迭代版的`inorder traversal`方法也可以
思路就是按从小到大的顺序遍历所有节点，一旦中间有数字大于前驱节点，就是错的。

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
    public boolean isValidBST(TreeNode root) {
        if (root == null) { return true; }
        Deque<TreeNode> stack = new LinkedList<TreeNode>();
        TreeNode cur = root;
        long pre = Long.MIN_VALUE;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.offerFirst(cur);
                cur = cur.left;
            }
            cur = stack.pollFirst();
            if (cur.val <= pre) { return false; }
            pre = cur.val;
            cur = cur.right;
        }
        return true;
    }
}
```

#### 结果
![validate-binary-search-tree-3](/images/leetcode/validate-binary-search-tree-3.png)
