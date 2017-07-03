---
layout: post
title: "Leetcode - Algorithm - Kth Smallest Element In Bst "
date: 2017-07-03 11:44:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","tree"]
level: "medium"
description: >
---

### 题目
Given a binary search tree, write a function `kthSmallest` to find the kth smallest element in it.

Note:
You may assume k is always valid, `1 < k < size`.

Follow up:
What if the BST is modified (insert/delete operations) often and you need to find the kth smallest frequently? How would you optimize the kthSmallest routine?

### 遍历二叉树，迭代版
缺点是需要一个额外的`Stack`缓存节点。
* time: $$O(n)$$
* space: $$O(n)$$

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
    public int kthSmallest(TreeNode root, int k) {
        int count = 0;
        Deque<TreeNode> stack = new LinkedList<>(); //已找到的待处理的左倾线上的节点
        TreeNode cur = root; // 当前需要检查左倾线的节点（通常是右节点，除非是根节点）
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.offerFirst(cur);
                cur = cur.left;
            }
            TreeNode node = stack.pollFirst(); count++; // count number
            if (count == k) { return node.val; }
            cur = node.right;
        }
        return 0; // never reached
    }
}
```

#### 结果
![kth-smallest-element-in-bst-1](/images/leetcode/kth-smallest-element-in-bst-1.png)


### 遍历二叉树，递归版
不需要额外容器。
* time: $$O(n)$$
* space: $$O(1)$$

#### 代码
```java
public class Solution {
    public int kthSmallest(TreeNode root, int k) {
        int[] result = new int[2];
        result = kth(root,result,k);
        return result[1];
    }
    /** Return a [order,value] pair, where,
     *  "order" is the order of the node,
     *  "value" is the return value
     *  If don't want to return array, we can declare these two params as member fields.
     */
    public int[] kth(TreeNode root, int[] parentOrder, int k) {
        if (root == null) { return parentOrder; }
        int[] order = kth(root.left, parentOrder, k);
        if (order[0] < k) {
            order[0]++;
            if (order[0] < k) {
                return kth(root.right,order,k);
            } else {
                order[1] = root.val; // this is the kth smallest element
                return order;
            }
        } else {
            return order;
        }
    }
}
```

#### 结果
![kth-smallest-element-in-bst-2](/images/leetcode/kth-smallest-element-in-bst-2.png)


### $$O(\log_{}{n})$$ 的二分查找无法实现，需要二叉树维护一个`size`参数
根据这题给定的`TreeNode`数据结构，是无法实现 $$O(\log_{}{n})$$ 的二分查找的。因为要决定是否丢弃某棵子树，先要计算它的大小。如果二叉树没有维护一个`size`参数，就无法在 $$O(1)$$ 时间内得到子树的大小。
```
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int x) { val = x; }
}
```

如果应用场景是更新数据多，查找少，就不维护`size`。如果数据不频繁更新，但频繁查找，就可以增加一个`size`参数，每次更新数据的时候维护这个`size`。
