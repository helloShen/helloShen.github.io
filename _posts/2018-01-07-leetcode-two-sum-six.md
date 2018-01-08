---
layout: post
title: "Leetcode - Algorithm - Two Sum Six "
date: 2018-01-07 20:45:27
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","binary search tree"]
level: "easy"
description: >
---

### 题目
Given a Binary Search Tree and a target number, return true if there exist two elements in the BST such that their sum is equal to the given target.

Example 1:
```
Input:
    5
   / \
  3   6
 / \   \
2   4   7

Target = 9

Output: True
```

Example 2:
```
Input:
    5
   / \
  3   6
 / \   \
2   4   7

Target = 28

Output: False
```

### 朴素递归遍历二叉树，用一个`HashSet`记录遍历过的值
基本思路就是用一个`HashSet`记录下所有已经遍历过的值，这样每当遇到一个新节点，就可以在`O(1)`的时间里查出之前遇到的所有节点有没有能和他匹配的。

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

    private static final Set<Integer> set = new HashSet<>();
    private static int target = 0;

    public boolean findTarget(TreeNode root, int k) {
        set.clear();
        target = k;
        return preOrder(root);
    }
    private boolean preOrder(TreeNode root) {
        if (root == null) { return false; }
        if (preOrder(root.left)) { return true; }
        if (set.contains(target - root.val)) { return true; }
        set.add(root.val);
        if (preOrder(root.right)) { return true; }
        return false;
    }
}
```

#### 结果
![two-sum-six-1](/images/leetcode/two-sum-six-1.png)
