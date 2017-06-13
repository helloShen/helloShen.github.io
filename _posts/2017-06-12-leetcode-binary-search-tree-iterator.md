---
layout: post
title: "Leetcode - Algorithm - Binary Search Tree Iterator "
date: 2017-06-12 17:13:28
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search tree","tree","stack","design"]
level: "medium"
description: >
---

### 题目
Implement an iterator over a binary search tree (BST). Your iterator will be initialized with the root node of a BST.

Calling `next()` will return the next smallest number in the BST.

Note: `next()` and `hasNext()` should run in average `O(1)` time and uses `O(h)` memory, where h is the height of the tree.

### 传统的迭代`inorder`遍历二叉树
用一个`Stack`缓存一整条左分支。注意是 **左分支**，不是整颗 **左子树**。如下图所示，
```
             6              左分支1：6-4-2-1
          /     \           左分支2：3
         4       8          左分支3：5
       /   \   /   \        左分支4：8-7
      2     5 7     9       左分支5：9
    /   \
   1     3
```

关键在于，怎么把整个过程拆分成`hasNext()`和`next()`两个函数。

#### 代码
```java
/**
 * Definition for binary tree
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */

public class BSTIterator {
        private Deque<TreeNode> stack = new LinkedList<>();
        public BSTIterator(TreeNode root) {
            TreeNode cur = root;
            while (cur != null) { // 缓存整条左分支
                stack.offerFirst(cur);
                cur = cur.left;
            }
        }

        /** @return whether we have a next smallest number */
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        /** @return the next smallest number.
         *  return 0, if the iterator reach the end of the tree.
         */
        public int next() {
            if (hasNext()) {
                TreeNode curr = stack.pollFirst();
                int ret = curr.val;
                if  (curr.right != null) {
                    TreeNode cur = curr.right;
                    while (cur != null) { // 缓存整条左分支
                        stack.offerFirst(cur);
                        cur = cur.left;
                    }
                }
                return ret;
            } else {
                return 0;
            }
        }
}
```

#### 结果
![binary-search-tree-iterator-1](/images/leetcode/binary-search-tree-iterator-1.png)
