---
layout: post
title: "Leetcode - Algorithm - All Nodes Distance K In Binary Tree "
date: 2019-01-25 19:16:28
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack", "tree", "binary tree"]
level: "medium"
description: >
---

### 题目
We are given a binary tree (with root node root), a target node, and an integer value K.

Return a list of the values of all nodes that have a distance K from the target node.  The answer can be returned in any order.

Example 1:
```
Input: root = [3,5,1,6,2,0,8,null,null,7,4], target = 5, K = 2

Output: [7,4,1]

Explanation:
The nodes that are a distance 2 from the target node (with value 5)
have values 7, 4, and 1.
```

![all-nodes-distance-k-in-binary-tree-figure](/images/leetcode/all-nodes-distance-k-in-binary-tree-figure.png)

Note that the inputs "root" and "target" are actually TreeNodes.
The descriptions of the inputs above are just serializations of these objects.

Note:
* The given tree is non-empty.
* Each node in the tree has unique values 0 <= node.val <= 500.
* The target node is a node in the tree.
* 0 <= K <= 1000.

### DFS递归回溯
还是以图中的树为例子，目标是`5`节点，
1. 首先`5`节点的所有距离为`2`的子节点都符合要求
2. 其次距离`5`节点的父节点为`3`节点距离为1的节点都符合要求（`5`节点本身除外）
3. 递归往父节点推第2条规则

所以先要找到目标节点，用一个`Stack`储存沿途的所有父节点。然后以这些父节点为起点，搜索特定距离的节点。

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
    public List<Integer> distanceK(TreeNode root, TreeNode target, int K) {
        List<Integer> res = new ArrayList<>();
        Deque<TreeNode> stack = new LinkedList<TreeNode>();
        stack.push(root);
        if (!find(stack, target)) return res;
        TreeNode curr = stack.pop();
        sub(res, curr, null, K);
        while (!stack.isEmpty() && --K >= 0) {
            TreeNode not = curr;
            curr = stack.pop();
            sub(res, curr, not, K);
        }
        return res;
    }

    private boolean find(Deque<TreeNode> stack, TreeNode target) {
        TreeNode root = stack.peek();
        if (root == null) return false;
        if (root == target) return true;
        stack.push(root.left);
        if (find(stack, target)) return true;
        stack.pop();
        stack.push(root.right);
        if (find(stack, target)) return true;
        stack.pop();
        return false;
    }

    private void sub(List<Integer> res, TreeNode root, TreeNode not, int k) {
        if (root == null) return;
        if (k == 0) {
            res.add(root.val);
            return;
        }
        if (root.left != not) sub(res, root.left, null, k - 1);
        if (root.right != not) sub(res, root.right, null, k - 1);
    }
}
```

#### 结果
![all-nodes-distance-k-in-binary-tree-1](/images/leetcode/all-nodes-distance-k-in-binary-tree-1.png)
