---
layout: post
title: "Leetcode - Algorithm - Flatten Binary Tree To Linked List "
date: 2017-05-12 18:54:19
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","depth first search"]
level: "medium"
description: >
---

### 题目
Given a binary tree, flatten it to a linked list in-place.

For example,
Given
```
         1
        / \
       2   5
      / \   \
     3   4   6
```
The flattened tree should look like:
```
   1
    \
     2
      \
       3
        \
         4
          \
           5
            \
             6
```
click to show hints.

Hints:
If you notice carefully in the flattened tree, each node's right child points to the next node of a pre-order traversal.

### 迭代遍历，用`Stack`缓存暂时剪下来的右子树
基本思想就是：先把右子树缓存在`Stack`里，然后把左子树嫁接在右子树的位置。跑到叶节点后，再从`Stack`里读取之前保留的右子树，接着往下嫁接。比如，
```
         1
        / \
       2   5
      / \   \
     3   4   6
```
先把右子树`5->6`缓存在`Stack`里。把左子树`2->3->4`嫁接到右子树，变成下面这样，
```
         1                  | |
          \                 |5|->6->null
           2        stack:  |_|
          / \   
         3   4
```
再把右子树`4`缓存起来，把左子树`3`嫁接到右子树。
```
         1                  |4|->null
          \                 |5|->6->null
           2        stack:  |_|
            \   
             3
```
然后再从`Stack`里取之前缓存的元素`4`,嫁接到右子树，
```
         1                  | |
          \                 |5|->6->null
           2        stack:  |_|
            \   
             3
              \
               4
```
最后再把`5->6`嫁接过来，
```
         1                  | |
          \                 | |
           2        stack:  |_|
            \   
             3
              \
               4
                \
                 5
                  \
                   6
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
    public void flatten(TreeNode root) {
        TreeNode cur = root;
        Deque<TreeNode> stack = new LinkedList<>();
        while (cur != null || !stack.isEmpty()) {
            while (cur.left != null || cur.right != null) {
                if (cur.left != null) {
                    if (cur.right != null) {
                        stack.offerFirst(cur.right);
                    }
                    cur.right = cur.left;
                    cur.left = null;
                }
                cur = cur.right;
            }
            cur.right = stack.pollFirst();
            cur = cur.right;
        }
    }
}
```

#### 结果
![flatten-binary-tree-to-linked-list-1](/images/leetcode/flatten-binary-tree-to-linked-list-1.png)


### 分治法，深度优先递归
对任何一个点来说，操作可以归纳成两步走：
1. 把左子树铺平，嫁接到右子树。
2. 把右子树铺平，嫁接到已经铺平的左子树的右子树。

为了方便嫁接，`flattenRecur()`函数返回铺平之后首元素和尾元素。

#### 代码
```java
public void flatten(TreeNode root) {
    flattenRecur(root);
}
// return TreeNode[2]: [head,tail]
public TreeNode[] flattenRecur(TreeNode root) {
    TreeNode[] headTail = new TreeNode[2];
    if (root == null) { return headTail; }
    TreeNode[] leftSub = flattenRecur(root.left);
    TreeNode[] rightSub = flattenRecur(root.right);
    TreeNode cur = root;
    if (leftSub[0] != null) { cur.left = null; cur = grafting(cur,leftSub); }
    if (rightSub[0] != null) { cur = grafting(cur,rightSub); }
    headTail[0] = root; headTail[1] = cur;
    return headTail;
}
// graft target as the right subtree of root.
// return the tail of the target
public TreeNode grafting(TreeNode root, TreeNode[] target) {
    TreeNode cur = root;
    cur.right = target[0];
    return target[1];
}
```

#### 结果
![flatten-binary-tree-to-linked-list-2](/images/leetcode/flatten-binary-tree-to-linked-list-2.png)


### 简化上面的分治递归
与其每次都返回铺平之后链表的首元素和尾元素，其实可以只返回首元素。把上一次返回链表的首元素嫁接到目前链表的尾部即可。

非常聪明的一种做法。但不是每一道题都能找到这么整齐的代码。

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
    public void flatten(TreeNode root) {
        flattenRecur(root,null);
    }
    public TreeNode flattenRecur(TreeNode root, TreeNode pre) {
        if (root == null) { return pre; }
        TreeNode cur = root;
        pre = flattenRecur(root.right,pre);
        pre = flattenRecur(root.left,pre);
        root.left = null;
        root.right = pre;
        return root;
    }
}
```

#### 结果
![flatten-binary-tree-to-linked-list-3](/images/leetcode/flatten-binary-tree-to-linked-list-3.png)
