---
layout: post
title: "Leetcode - Algorithm - Binary Tree Zigzag Level Order Traversal "
date: 2017-05-09 18:19:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","breadth first search","stack"]
level: "medium"
description: >
---

### 题目
Given a binary tree, return the zigzag level order traversal of its nodes' values. (ie, from left to right, then right to left for the next level and alternate between).

For example:
Given binary tree `[3,9,20,null,null,15,7]`,
```
    3
   / \
  9  20
    /  \
   15   7
```
return its zigzag level order traversal as:
```
[
  [3],
  [20,9],
  [15,7]
]
```

### 还是用`List`缓冲元素
但设置一个开关，每次控制结果列表的顺序。

#### 代码（迭代版）
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
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        if (root == null) { return res; }
        List<TreeNode> buffer = new ArrayList<>();
        buffer.add(root);
        boolean leftToRight = true;
        while (!buffer.isEmpty()) {
            List<Integer> nums = new ArrayList<>();
            int size = buffer.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = buffer.remove(0);
                if (node != null) {
                    if (leftToRight) { // 根据方向开关插入结果
                        nums.add(node.val);
                    } else {
                        nums.add(0,node.val);
                    }
                    buffer.add(node.left);
                    buffer.add(node.right);
                }
            }
            if (!nums.isEmpty()) { res.add(nums); }
            leftToRight = !leftToRight;
        }
        return res;
    }
}
```

#### 递归版
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
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        if (root == null) { return res; }
        List<TreeNode> buffer = new ArrayList<>();
        buffer.add(root);
        zigzagLevelOrderRecur(res,buffer,new Boolean(true));
        return res;
    }
    public void zigzagLevelOrderRecur(List<List<Integer>> res, List<TreeNode> buffer, boolean fromLeftToRight) {
        if (buffer.isEmpty()) { return; }
        List<Integer> nums = new ArrayList<>();
        int size = buffer.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = buffer.remove(0);
            if (node != null) {
                if (fromLeftToRight) {
                    nums.add(node.val);
                } else {
                    nums.add(0,node.val);
                }
                buffer.add(node.left);
                buffer.add(node.right);
            }
        }
        if (!nums.isEmpty()) { res.add(nums); }
        zigzagLevelOrderRecur(res,buffer,!fromLeftToRight);
    }
}
```

#### 结果
![binary-tree-zigzag-level-order-traversal-1](/images/leetcode/binary-tree-zigzag-level-order-traversal-1.png)
