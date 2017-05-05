---
layout: post
title: "Leetcode - Algorithm - Binary Tree Inorder Traversal "
date: 2017-05-03 20:31:58
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","hashtable","stack"]
level: "medium"
description: >
---

### 主要收获 - 1: 关于 **迭代** 和 **递归**
怎么看待迭代？迭代的关键因素有两个，首先要明确一个监听对象，迭代过程就是站在这个对象的主视角讲故事。第二个关键因素才是过程，就是这到底是个什么样的故事。

怎么看待递归？递归是面向过程的。递归中对象是变化的，不变的一套动作。递归通过不断地在不同的对象上重复一套动作，来完成某项工作。

### 主要收获 - 2: 关于 **`Stack`**
怎么看待`Stack`。迭代过程，需要在不同状态之间来回切换。`Stack`的`FIFO`的特性非常有利于保存每一层迭代的状态信息。

### 题目
Given a binary tree, return the inorder traversal of its nodes' values.

For example:
Given binary tree `[1,null,2,3]`,
```
   1
    \
     2
    /
   3
```
return `[1,3,2]`.

### 递归版
递归版非常简单。只要注意顺序，
* 获取整颗以左节点为根的子树。
* 获取当前节点的值。
* 获取整颗以右节点为根的子树。

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
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) { return res; }
        res.addAll(inorderTraversal(root.left));
        res.add(root.val);
        res.addAll(inorderTraversal(root.right));
        return res;
    }
}
```

#### 结果
![binary-tree-inorder-traversal-1](/images/leetcode/binary-tree-inorder-traversal-1.png)


### 迭代版
关键是 **站在一个节点的主视角，提炼出一整套重复的动作**。
* 如果有左节点，去左节点。
* 如果没有左节点，或者从左节点回来，往结果列表添加val。
* 如果有右节点，去右节点。
* 回到父节点，并且，从哪个节点回溯回来，就删除哪个节点。

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
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Deque<TreeNode> parentStack = new LinkedList<TreeNode>();
        Deque<Boolean> fromLeft = new LinkedList<Boolean>();
        boolean leftVisted = false, rightVisited = false;
        boolean backFromRight = false;
        TreeNode cur = root;
        /*
         * 一个节点的主视角。
         * 如果有左节点，去左节点。
         * 如果没有左节点，或者从左节点回来，往结果列表添加val。
         * 如果有右节点，去右节点。
         * 回到父节点，并且，从哪个节点回溯回来，就删除哪个节点。
         */
        while (cur != null) {
            if (cur.left != null) {
                parentStack.addFirst(cur);
                fromLeft.addFirst(true);
                cur = cur.left;
                continue;
            }
            if (!backFromRight) { // 从右节点回溯回来，不重复添加
                res.add(cur.val);
            } else {
                backFromRight = false;
            }
            if (cur.right != null) {
                parentStack.offerFirst(cur);
                fromLeft.addFirst(false);
                cur = cur.right;
                continue;
            }
            cur = parentStack.pollFirst(); // 回溯至父节点
            if (cur != null) {
                Boolean isFromLeft = fromLeft.pollFirst();
                if (isFromLeft) { // 从哪个节点回来，就删除哪个节点。
                    cur.left = null;
                } else {
                    cur.right = null;
                    backFromRight = true; // 从右节点回溯回来，不需要再重复添加父节点
                }
            }
        }
        return res;
    }
}
```

#### 结果
![binary-tree-inorder-traversal-2](/images/leetcode/binary-tree-inorder-traversal-2.png)


### 更好的一个迭代版
总体思路是：既然迭代不能同时处理两条分支。就把二叉树全部分解成一根根左倾分支。
![binary-tree-inorder-traversal-4](/images/leetcode/binary-tree-inorder-traversal-4.png)

步骤如下：
1. 一直往左分支走到底，经过的节点全部存入`Stack`。
2. 从`Stack`里取最后一个出来，写入结果。
3. 跑到这个节点的右节点。
4. 重复步骤1~3。

很完美，但是每次遇到这样的问题，都想要想出这么完美算法不太容易。先记住。

#### 代码
```java
public class Solution {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Deque<TreeNode> stack = new LinkedList<TreeNode>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.offerFirst(cur);
                cur = cur.left;
            }
            cur = stack.pollFirst();
            res.add(cur.val);
            cur = cur.right();
        }    
        return res;
    }
}
```

#### 结果
![binary-tree-inorder-traversal-3](/images/leetcode/binary-tree-inorder-traversal-3.png)
