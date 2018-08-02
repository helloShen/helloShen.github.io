---
layout: post
title: "Leetcode - Algorithm - Binary Tree Longest Consecutive Sequence "
date: 2018-07-27 16:45:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree","tree"]
level: "medium"
description: >
---

### 题目
Given a binary tree, find the length of the longest consecutive sequence path.

The path refers to any sequence of nodes from some starting node to any node in the tree along the parent-child connections. The longest consecutive path need to be from parent to child (cannot be the reverse).

Example 1:
```
Input:

   1
    \
     3
    / \
   2   4
        \
         5

Output: 3

Explanation: Longest consecutive sequence path is 3-4-5, so return 3.
```

Example 2:
```
Input:
   2
    \
     3
    /
   2    
  /
 1

Output: 2

Explanation: Longest consecutive sequence path is 2-3, not 3-2-1, so return 2.
```

### 典型的尾递归遍历二叉树

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
        private int max = 0;
        public int longestConsecutive(TreeNode root) {
            max = 0;
            helper(null,root,0);
            return max;
        }
        private void helper(TreeNode father, TreeNode root, int len) {
            if (root == null) { return; }
            len = (father != null && (root.val - father.val == 1))? len + 1 : 1;
            max = Math.max(max,len);
            helper(root,root.left,len);
            helper(root,root.right,len);
        }

}
```

#### 结果
![binary-tree-longest-consecutive-sequence-1](/images/leetcode/binary-tree-longest-consecutive-sequence-1.png)
