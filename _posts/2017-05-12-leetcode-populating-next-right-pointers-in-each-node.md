---
layout: post
title: "Leetcode - Algorithm - Populating Next Right Pointers In Each Node "
date: 2017-05-12 21:30:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","breadth first search"]
level: "medium"
description: >
---

### 题目
Given a binary tree
```
    struct TreeLinkNode {
      TreeLinkNode *left;
      TreeLinkNode *right;
      TreeLinkNode *next;
    }
```
Populate each next pointer to point to its next right node. If there is no next right node, the next pointer should be set to NULL.

Initially, all next pointers are set to NULL.

Note:

You may only use constant extra space.
You may assume that it is a perfect binary tree (ie, all leaves are at the same level, and every parent has two children).
For example,
Given the following perfect binary tree,
```
         1
       /  \
      2    3
     / \  / \
    4  5  6  7
```
After calling your function, the tree should look like:
```
         1 -> NULL
       /  \
      2 -> 3 -> NULL
     / \  / \
    4->5->6->7 -> NULL
```

### 传统二叉树广度优先遍历
对于树中的每个节点，以`2`为例，
```
        1
       /  \
      2 -> 3
     /\    /\
    4->5-> 6  7
```
1. 先把左子节点`4`和右子节点`5`连起来。
2. 然后找到`2`的兄弟节点`3`，把`2`的右子节点`5`和`3`的左子节点`6`连起来。

所以从根节点开始，展开传统二叉树的广度优先（Level Order）的遍历。遍历必须是自顶向下的，处理每一层子节点的时候，它自己必须和自己的兄弟节点连好。

#### 代码
```java
/**
 * Definition for binary tree with next pointer.
 * public class TreeLinkNode {
 *     int val;
 *     TreeLinkNode left, right, next;
 *     TreeLinkNode(int x) { val = x; }
 * }
 */
public class Solution {
    public void connect(TreeLinkNode root) {
        if (root == null) { return; }
        List<TreeLinkNode> buffer = new ArrayList<>();
        buffer.add(root);
        while (!buffer.isEmpty()) {
            int size = buffer.size();
            for (int i = 0; i < size; i++) {
                TreeLinkNode node = buffer.remove(0);
                if (node.left != null && node.right != null) {
                    node.left.next = node.right; // 右子节点链接左子节点
                    if (node.next != null) {
                        node.right.next = node.next.left; // 右子节点链接兄弟节点的左子节点
                    }
                    buffer.add(node.left);
                    buffer.add(node.right);
                }
            }
        }
    }
}
```

#### 结果
还不是银弹！
![populating-next-right-pointers-in-each-node-1](/images/leetcode/populating-next-right-pointers-in-each-node-1.png)


### 利用`next`指针，横向递归
因为`TreeLinkNode`多了一个指向同一层下一个节点的指针，进行广度优先（Level Order）遍历就不需要像传统二叉树这么麻烦了。直接通过`next`跳转到同一层下一个节点就行，注意需要提前预备下一行的头一个节点。

#### 代码
```java
/**
 * Definition for binary tree with next pointer.
 * public class TreeLinkNode {
 *     int val;
 *     TreeLinkNode left, right, next;
 *     TreeLinkNode(int x) { val = x; }
 * }
 */
public class Solution {
    public void connect(TreeLinkNode root) {
        if (root == null) { return; }
        recursion(root,null);
    }
    public void recursion(TreeLinkNode root, TreeLinkNode nextLevel) {
        if (root.left == null && root.right == null) { return; }
        if (nextLevel == null) { nextLevel = root.left; } // 提前预备下一行的头节点
        root.left.next = root.right; // 右子节点链接左子节点
        if (root.next != null) {
            root.right.next = root.next.left; // 右子节点链接兄弟节点的左子节点
        }
        if (root.next != null) {
            recursion(root.next,nextLevel); // 跳转到同一行的next
        } else {
            recursion(nextLevel,null); // 跳转到下一行的第一个节点
        }
    }
}
```

#### 结果
银弹！
![populating-next-right-pointers-in-each-node-2](/images/leetcode/populating-next-right-pointers-in-each-node-2.png)


### 迭代版，利用`next`横向遍历

#### 代码
```java
/**
 * Definition for binary tree with next pointer.
 * public class TreeLinkNode {
 *     int val;
 *     TreeLinkNode left, right, next;
 *     TreeLinkNode(int x) { val = x; }
 * }
 */
public class Solution {
    public void connect(TreeLinkNode root) {
        if (root == null) { return; }
        TreeLinkNode nextLevel = null, cur = root;
        while (cur.left != null && cur.right != null) {
            nextLevel = cur.left;
            while (cur != null) {
                cur.left.next = cur.right;
                if (cur.next != null) {
                    cur.right.next = cur.next.left;
                }
                cur = cur.next;
            }
            cur = nextLevel;
        }
    }
}
```

#### 结果
更好了！
![populating-next-right-pointers-in-each-node-3](/images/leetcode/populating-next-right-pointers-in-each-node-3.png)
