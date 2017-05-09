---
layout: post
title: "Leetcode - Algorithm - Binary Tree Level Order Traversal "
date: 2017-05-09 16:36:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","breadth first search"]
level: "medium"
description: >
---

### 题目
Given a binary tree, return the level order traversal of its nodes' values. (ie, from left to right, level by level).

For example:
Given binary tree [3,9,20,null,null,15,7],
```
    3
   / \
  9  20
    /  \
   15   7
```
return its level order traversal as:
```
[
  [3],
  [9,20],
  [15,7]
]
```

### 用`List`缓存每一行的元素
每次迭代都重复一个动作，
1. 读取缓存`List`中的元素
2. 把每个元素的`left`和`right`子节点继续插入缓存`List`

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
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        if (root == null) { return res; }
        List<TreeNode> buffer = new ArrayList<>();
        buffer.add(root);
        while (!buffer.isEmpty()) {
            List<Integer> nums = new ArrayList<>();
            int size = buffer.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = buffer.remove(0);
                if (node != null) {
                    nums.add(node.val);
                    buffer.add(node.left);
                    buffer.add(node.right);
                }
            }
            if (!nums.isEmpty()) { res.add(nums); }
        }
        return res;
    }
}
```

#### 结果
![binary-tree-level-order-traversal-1](/images/leetcode/binary-tree-level-order-traversal-1.png)
