---
layout: post
title: "Leetcode - Algorithm - Convert Sorted Array To Binary Search Tree "
date: 2017-05-11 20:18:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","depth first search"]
level: "easy"
description: >
---

### 题目
Given an array where elements are sorted in ascending order, convert it to a height balanced BST.

### 分治递归 $$O(n)$$
注意这里需要的是`balanced BST`。平衡二叉搜索树就是说：**每个叶节点到根节点的距离差最多为1.**
![balanced-binary-search-tree](/images/leetcode/balanced-binary-search-tree.png)

关键策略就是：**每次都取中位数作为根元素的值**。


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
    public TreeNode sortedArrayToBST(int[] nums) {
        return recursion(nums,0,nums.length-1);
    }
    public TreeNode recursion(int[] nums, int lo, int hi) {
        if (lo > hi) { return null; }
        int mid = lo + (hi - lo) / 2; // 下位中位数
        TreeNode root = new TreeNode(nums[mid]);
        root.left = recursion(nums,lo,mid-1);
        root.right = recursion(nums,mid+1,hi);
        return root;
    }
}
```

#### 结果
![convert-sorted-array-to-binary-search-tree-1](/images/leetcode/convert-sorted-array-to-binary-search-tree-1.png)
