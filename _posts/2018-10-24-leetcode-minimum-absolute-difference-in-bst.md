---
layout: post
title: "Leetcode - Algorithm - Minimum Absolute Difference In Bst "
date: 2018-10-24 19:48:07
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search tree", "tree"]
level: "easy"
description: >
---

### 题目
Given a binary search tree with non-negative values, find the minimum absolute difference between values of any two nodes.

Example:
```
Input:

   1
    \
     3
    /
   2

Output:
1
```

Explanation:
The minimum absolute difference is 1, which is the difference between 2 and 1 (or between 2 and 3).

### in-order遍历二叉树
`in-order`的顺序遍历二叉搜索树，是按照升序的有序方式遍历节点（比如下面二叉树）。所以只需要计算相邻两元素的差值，再找到最小差值即可。
```
            4
           / \
          1  10
               \
               12

in-order遍历的顺序是升序的：1->4->10->12
                         |--|--|---|
                  只需要计算他们两两之间的差值即可
```

时间复杂度：`O(N)`，`N`为节点数。

#### 代码
```java
class Solution {
    public int getMinimumDifference(TreeNode root) {
        minDiff = Integer.MAX_VALUE;
        prev = -1;
        inorder(root);
        return minDiff;
    }
    int minDiff, prev;
    private void inorder(TreeNode root) {
        if (root == null) return;
        inorder(root.left);
        if (prev >= 0) minDiff = Math.min(minDiff, Math.abs(root.val - prev));
        prev = root.val;
        inorder(root.right);
    }
}
```

#### 结果
![minimum-absolute-difference-in-bst-1](/images/leetcode/minimum-absolute-difference-in-bst-1.png)
