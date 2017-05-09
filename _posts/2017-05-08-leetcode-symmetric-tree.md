---
layout: post
title: "Leetcode - Algorithm - Symmetric Tree "
date: 2017-05-08 21:42:14
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search","tree","breadth first search"]
level: "easy"
description: >
---

### 题目
Given a binary tree, check whether it is a mirror of itself (ie, symmetric around its center).

For example, this binary tree `[1,2,2,3,4,4,3]` is symmetric:
```
    1
   / \
  2   2
 / \ / \
3  4 4  3
```
But the following `[1,2,2,null,3,null,3]` is not:
```
    1
   / \
  2   2
   \   \
   3    3
```
Note:
Bonus points if you could solve it both recursively and iteratively.

### 老办法，分治递归

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
    public boolean isSymmetric(TreeNode root) {
        return (root == null)? true : areSymmetric(root.left,root.right);
    }
    public boolean areSymmetric(TreeNode one, TreeNode two) {
        if (one == null || two == null) { return one == two; }
        return (one.val == two.val) && areSymmetric(one.left,two.right) && areSymmetric(one.right,two.left);
    }
}
```

#### 结果
![symmetric-tree-1](/images/leetcode/symmetric-tree-1.png)


### 迭代DFS遍历
利用迭代的`inorder traversal`方法。遍历左半边的时候，把经过的节点全部压入`memo`栈中，节点的朝向信息全部压入`directionMemo`栈中。 等遍历到了右半边，就把`memo`和`directionMemo`中的历史记录推出来比较。

关键在于判断是否对称，不但节点中的值要相等，朝向还要相反（就是来自于父节点的左节点还是右节点）。光比较值的话，下面这个例子，会被误判为对称的。
```
    1
   / \
  2   3
 /   /
3   2   
```

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
    public boolean isSymmetric(TreeNode root) {
        if (root == null) { return true; }
        Deque<TreeNode> stack = new LinkedList<TreeNode>();
        Deque<Boolean> direction = new LinkedList<Boolean>();
        Deque<TreeNode> memo = new LinkedList<TreeNode>(); // 左半边节点历史
        Deque<Boolean> directionMemo = new LinkedList<Boolean>(); // 左半边节点朝向历史
        TreeNode cur = root;
        boolean reachMid = false;
        boolean fromLeft = false;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.offerFirst(cur);
                if (fromLeft) { // note direction
                    direction.offerFirst(true);
                } else {
                    direction.offerFirst(false);
                }
                cur = cur.left;
                fromLeft = true;
            }
            cur = stack.pollFirst();
            boolean isFromLeft = direction.pollFirst();
            if (!reachMid) { // 没到达中点之前，积累memo
                if (cur != root) {
                    memo.offerFirst(cur);
                    directionMemo.offerFirst(isFromLeft);
                } else {
                    reachMid = true;
                }
            } else { // 过了中点以后，开始和memo里的内容比较
                TreeNode sym = memo.pollFirst();
                Boolean dirInMemo = directionMemo.pollFirst();
                if (sym == null || sym.val != cur.val || dirInMemo == isFromLeft) { return false; }
            }
            cur = cur.right;
            fromLeft = false;
        }
        return memo.isEmpty();
    }
}
```

#### 结果
![symmetric-tree-2](/images/leetcode/symmetric-tree-2.png)


### 迭代BFS遍历
每层收集所有要处理的节点，用一个`Queue`收集起来，特点是逐层处理。不处理完一层不会进入下一层。

#### 代码
```java
public boolean isSymmetric(TreeNode root) {
    if (root == null) { return true; }
    Deque<TreeNode> stack = new LinkedList<TreeNode>();
    TreeNode cur = root;
    stack.offerLast(root.left); stack.offerLast(root.right);
    while (!stack.isEmpty()) {
        TreeNode n1 = stack.pollFirst(), n2 = stack.pollFirst();
        if (n1 == null && n2 == null) { continue; } // 一对null不算完，让stack吐干净
        if ((n1 != null && n2 == null) || (n1 == null && n2 != null) || (n1.val != n2.val)) { return false; }
        stack.offerLast(n1.left); stack.offerLast(n2.right);
        stack.offerLast(n1.right); stack.offerLast(n2.left);
    }
    return true;
}
```

#### 结果
![symmetric-tree-3](/images/leetcode/symmetric-tree-3.png)
