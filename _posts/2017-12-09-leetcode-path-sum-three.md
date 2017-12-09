---
layout: post
title: "Leetcode - Algorithm - Path Sum Three "
date: 2017-12-09 16:29:26
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree"]
level: "easy"
description: >
---

### 题目
You are given a binary tree in which each node contains an integer value.

Find the number of paths that sum to a given value.

The path does not need to start or end at the root or a leaf, but it must go downwards (traveling only from parent nodes to child nodes).

The tree has no more than 1,000 nodes and the values are in the range -1,000,000 to 1,000,000.

Example:
```
root = [10,5,-3,3,2,null,11,3,-2,null,1], sum = 8

      10
     /  \
    5   -3
   / \    \
  3   2   11
 / \   \
3  -2   1

Return 3. The paths that sum to 8 are:

1.  5 -> 3
2.  5 -> 2 -> 1
3. -3 -> 11
```

### 递归
递归式如下：
> T(root) = T(left) + T(right) + G(root)

其中`T(N)`代表本题的问题，`G(N)`是本问题的一个变种，即：
> 必须以N为根节点的情况下，有多少条path的总和等于sum。

其中问题`G(N)`也可以用递归来完成，而且是比较传统的递归。

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
    public int pathSum(TreeNode root, int sum) {
        if (root == null) { return 0; }
        int left = pathSum(root.left, sum);
        int right = pathSum(root.right,sum);
        int curr = dfs(root,sum);
        return left + right + curr;
    }
    private int dfs(TreeNode root, int remain) {
        if (root == null) { return 0; }
        int left = dfs(root.left,remain - root.val);
        int right = dfs(root.right,remain - root.val);
        int curr = (root.val == remain)? 1 : 0;
        return left + right + curr;
    }
}
```

#### 结果
![path-sum-three-1](/images/leetcode/path-sum-three-1.png)
