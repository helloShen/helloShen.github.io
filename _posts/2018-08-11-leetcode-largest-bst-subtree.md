---
layout: post
title: "Leetcode - Algorithm - Largest Bst Subtree "
date: 2018-08-11 16:33:46
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree"]
level: "medium"
description: >
---

### 题目
Given a binary tree, find the largest subtree which is a Binary Search Tree (BST), where largest means subtree with largest number of nodes in it.

Note:
A subtree must include all of its descendants.

Example:
```
Input: [10,5,15,1,8,null,7]

   10
   / \
  5  15
 / \   \
1   8   7

Output: 3
Explanation:
  1. The Largest BST Subtree in this case is the highlighted one.
  2. The return value is the subtree's size, which is 3.
```
Follow up:
Can you figure out ways to solve it with O(n) time complexity?

### 递归遍历二叉树
首先自上往下判断一棵树是不是BST是不行的，因为在高层根节点是看不出底层的错误的。

![largest-bst-subtree-a](/images/leetcode/largest-bst-subtree-a.png)

所以必须先找到每个叶节点，然后再自底向上归纳，
![largest-bst-subtree-b](/images/leetcode/largest-bst-subtree-b.png)

然后判断一个根节点是否符合BST的要求，必须知道它左右两个子树的值域。即，
> 根节点不能大于左子树中的最大值，也不能小于右子树中的最小值。

这就要求递归函数`recursion()`不但要返回子树BST的规模数值，还要告诉调用者它的值域。所以返回值需要是一个`int[3]`。

![largest-bst-subtree-c](/images/leetcode/largest-bst-subtree-c.png)

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
        public int largestBSTSubtree(TreeNode root) {
            if (root == null) { return 0; }
            return recursion(root)[0];
        }
        /**
         * 函数返回值是一个数组int[3]:
         *     res[0]: 当前子树中最大BST的规模
         *     res[1]: 当前子树值域左边界（最小值）
         *     res[2]: 当前子树值域右边界（最大值）
         *
         * 如果当前子树不是合法BST，我设置res[1]=1, res[2]=-1，即值域为空，
         * 即告诉上层递归调用这棵子树已经死了
         */
        private int[] recursion(TreeNode root) {
            //基
            if (root == null){ return null; }
            //递归
            int[] res = new int[3];
            res[0] = 1; res[1] = root.val; res[2] = root.val;
            int[] left = recursion(root.left);
            int[] right = recursion(root.right);
            int numLeft = (left == null)? 1 : left[0];
            int numRight = (right == null)? 1 : right[0];
            //死子树，不用处理边界，也不用更新统计规模
            if (notBST(root,left,right)) {
                return new int[]{Math.max(numLeft,numRight),1,-1};
            }
            //活子树，更新子树规模，更新边界
            if (left != null) {
                res[0] += left[0];
                res[1] = left[1];
            }
            if (right != null) {
                res[0] += right[0];
                res[2] = right[2];
            }
            return res;
        }
        //判断当前是否是一棵死子树（不是BST）
        private boolean notBST(TreeNode root, int[] left, int[] right) {
            //左右子树只要有一棵死了，整棵树都死
            boolean childrenDead = (left != null && left[1] > left[2]) || (right != null && right[1] > right[2]);
            //当前节点不合法，树被杀死
            boolean killRoot = (root.left != null && root.val <= left[2]) || (root.right != null && root.val >= right[1]);
            return childrenDead || killRoot;
        }
}
```

#### 结果
![largest-bst-subtree-1](/images/leetcode/largest-bst-subtree-1.png)
