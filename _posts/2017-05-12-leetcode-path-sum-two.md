---
layout: post
title: "Leetcode - Algorithm - Path Sum Two "
date: 2017-05-12 17:34:43
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","depth first search"]
level: "medium"
description: >
---

### 题目
Given a binary tree and a sum, find all root-to-leaf paths where each path's sum equals the given sum.

For example:
Given the below binary tree and sum = 22,
```
              5
             / \
            4   8
           /   / \
          11  13  4
         /  \    / \
        7    2  5   1
```
return
```
[
   [5,4,11,2],
   [5,8,4,5]
]
```

### 自顶向下，递归回溯
每往下递归一层，就扣去当前节点中的数值。直到遇到叶节点，并且剩下的值为0，就找到了一条路线。

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
    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        List<List<Integer>> res = new ArrayList<List<Integer>>();
        if (root == null) { return res; }
        backtracking(root,sum,new ArrayList<Integer>(),res);
        return res;
    }
    public void backtracking(TreeNode root, int remain, List<Integer> path, List<List<Integer>> res) {
        if (root == null) { return; }
        path.add(root.val);
        int nowRemain = remain - root.val;
        if (root.left == null && root.right == null && nowRemain == 0) {
            res.add(new ArrayList<Integer>(path));
        } else {
            backtracking(root.left,nowRemain,path,res);
            backtracking(root.right,nowRemain,path,res);
        }
        path.remove(path.size()-1);
        return;
    }
}
```

#### 结果
![path-sum-two-1](/images/leetcode/path-sum-two-1.png)
