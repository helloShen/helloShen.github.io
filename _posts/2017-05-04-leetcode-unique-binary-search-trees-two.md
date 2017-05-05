---
layout: post
title: "Leetcode - Algorithm - Unique Binary Search Trees Two "
date: 2017-05-04 16:02:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: "medium"
description: >
---

### 思考问题的范式（下面的`BST`指代“二叉搜索树”）
1. 笨办法该怎么做？ 根据数组，把`BTS`构造出来，然后和现有的所有`BTS`比较。
2. 痛点在哪儿？构造`BTS`，比较`BTS`开销很大。
3. 有没有可能解决这个痛点？原本的过程是：输入数组 -> `BTS` -> 在标准空间内比较。有没有可能绕过`BTS`，直接把数组映射到标准空间？

### 题目
Given an integer n, generate all structurally unique BST's (binary search trees) that store values 1...n.

For example,
Given `n = 3`, your program should return all 5 unique BST's shown below.
```
   1         3     3      2      1
    \       /     /      / \      \
     3     2     1      1   3      2
    /     /       \                 \
   2     1         2                 3
```

### 笨办法，一颗颗树构造出来，然后比较
因为给出的`TreeNode`数据结构太简陋，什么功能都没有。需要自己用函数实现所有 **构造**， **插入**， **比较** 的功能函数。还附带了一个用来生成排列组合数组的函数`permutation()`。

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
    public List<TreeNode> generateTrees(int n) {
        List<TreeNode> res = new ArrayList<>();
        List<List<Integer>> numsStream = permutation(n);
        for (List<Integer> nums : numsStream) {
            TreeNode tree = buildTree(nums);
            if (isNewTree(res,tree)) { res.add(tree); }
        }
        return res;
    }
    public boolean isNewTree(List<TreeNode> res, TreeNode tree) {
        for (TreeNode member : res) {
            if (equals(member,tree)) { return false; }
        }
        return true;
    }
    public List<List<Integer>> permutation(int n) {
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= n; i++) { nums.add(i); }
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        permutationRecur(nums,new ArrayList<Integer>(),res);
        return res;
    }
    public void permutationRecur(List<Integer> nums, List<Integer> temp, List<List<Integer>> res) {
        if (nums.isEmpty() && !temp.isEmpty()) { res.add(new ArrayList<Integer>(temp)); return; }
        for (int i = 0; i < nums.size(); i++) {
            temp.add(nums.remove(i));
            permutationRecur(nums,temp,res);
            nums.add(i,temp.remove(temp.size()-1));
        }
    }
    public TreeNode buildTree(List<Integer> nums) {
        if (nums.isEmpty()) { return null; }
        TreeNode res = new TreeNode(nums.get(0));
        for (int i = 1; i < nums.size(); i++) {
            res = insert(res,nums.get(i));
        }
        return res;
    }
    public TreeNode insert(TreeNode tree, int num) {
        TreeNode newNode = new TreeNode(num);
        if (tree == null) { return newNode; }
        TreeNode cur = tree, pre = tree;
        boolean goLeft = num < tree.val;
        while (cur != null) {
            pre = cur;
            if (num < cur.val) {
                cur = cur.left;
                goLeft = true;
            } else { // num > cur.val  (num == cur.val is unreachable)
                cur = cur.right;
                goLeft = false;
            }
        }
        if (goLeft) {
            pre.left = newNode;
        } else {
            pre.right = newNode;
        }
        return tree;
    }
    public boolean equals(TreeNode first, TreeNode second) {
        if (first == null && second == null) { return true; }
        if (!nodeEquals(first,second)) { return false; } // 数值不等，或者只有其中一个为空
        // assert: first != null && second != null && first.val == second.val
        return (equals(first.left,second.left) && equals(first.right,second.right));
    }
    public boolean nodeEquals(TreeNode first, TreeNode second) {
        return (first == null)? second == null : first.val == second.val;
    }
}
```

#### 结果
结果肯定非常慢。但实现笨办法，为后面的优化铺平了道路。
![unique-binary-search-trees-two-1](/images/leetcode/unique-binary-search-trees-two-1.png)


### 解法2

#### 代码
```java

```

#### 结果
![unique-binary-search-trees-two-2](/images/leetcode/unique-binary-search-trees-two-2.png)


### 解法3

#### 代码
```java

```

#### 结果
![unique-binary-search-trees-two-3](/images/leetcode/unique-binary-search-trees-two-3.png)
