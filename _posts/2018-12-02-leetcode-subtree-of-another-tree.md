---
layout: post
title: "Leetcode - Algorithm - Subtree_of_another_tree "
date: 2018-12-02 18:58:21
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree", "binary tree"]
level: "easy"
description: >
---

### 题目
Given two non-empty binary trees s and t, check whether tree t has exactly the same structure and node values with a subtree of s. A subtree of s is a tree consists of a node in s and all of this node's descendants. The tree s could also be considered as a subtree of itself.

Example 1:
```
Given tree s:

     3
    / \
   4   5
  / \
 1   2
Given tree t:
   4
  / \
 1   2
Return true, because t has the same structure and node values with a subtree of s.
```

Example 2:
```
Given tree s:

     3
    / \
   4   5
  / \
 1   2
    /
   0
Given tree t:
   4
  / \
 1   2
```

### 先找到相同的根节点，然后比较两棵子树
例子1中的`s`和`t`如下
```
Given tree s:

     3
    / \
   4   5
  / \
 1   2
Given tree t:
   4
  / \
 1   2
```

先在`s`中找到`4`节点，然后比较以`4`节点为根节点的子树和`t`树。

#### 代码，用一个`List`储存所有节点的引用
```java
class Solution {

    public boolean isSubtree(TreeNode s, TreeNode t) {
        if (t == null) return true;
        int val = t.val;
        List<TreeNode> nodes = getNodes(s);
        for (TreeNode node : nodes) {
            if (node.val == val && compare(node, t)) return true;
        }
        return false;
    }

    private List<TreeNode> getNodes(TreeNode root) {
        List<TreeNode> list = new LinkedList<>();
        List<TreeNode> level = new LinkedList<>();
        level.add(root);
        while (!level.isEmpty()) {
            int size = level.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = level.remove(0);
                if (node != null) {
                    list.add(node);
                    level.add(node.left);
                    level.add(node.right);
                }
            }
        }
        return list;
    }

    private boolean compare(TreeNode s, TreeNode t) {
        if (s == null && t == null) return true;
        if (s == null || t == null) return false;
        if (s.val != t.val) return false;
        if (!compare(s.left, t.left) || !compare(s.right, t.right)) return false;
        return true;
    }

}
```

#### 用迭代器遍历一棵二叉树
```java
class Solution {

    public boolean isSubtree(TreeNode s, TreeNode t) {
        if (t == null) return true;
        int val = t.val;
        Iterator ite = iterator(s);
        while (ite.hasNext()) {
            TreeNode node = ite.next();
            if (node.val == val && compare(node, t)) return true;
        }
        return false;
    }

    private Iterator iterator(TreeNode root) {
        return new Iterator(root);
    }

    private static class Iterator {
        private List<TreeNode> level;
        public Iterator (TreeNode root) {
            level = new LinkedList<TreeNode>();
            level.add(root);
        }
        public boolean hasNext() {
            while (!level.isEmpty() && level.get(0) == null) level.remove(0); // trim leading null nodes
            return !level.isEmpty();
        }
        public TreeNode next() {
            while (!level.isEmpty() && level.get(0) == null) level.remove(0); // trim leading null nodes
            if (!level.isEmpty()) {
                TreeNode next = level.remove(0);
                level.add(next.left);
                level.add(next.right);
                return next;
            }
            return null;
        }
    }

    private List<TreeNode> getNodes(TreeNode root) {
        List<TreeNode> list = new LinkedList<>();
        List<TreeNode> level = new LinkedList<>();
        level.add(root);
        while (!level.isEmpty()) {
            int size = level.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = level.remove(0);
                if (node != null) {
                    list.add(node);
                    level.add(node.left);
                    level.add(node.right);
                }
            }
        }
        return list;
    }

    private boolean compare(TreeNode s, TreeNode t) {
        if (s == null && t == null) return true;
        if (s == null || t == null) return false;
        if (s.val != t.val) return false;
        if (!compare(s.left, t.left) || !compare(s.right, t.right)) return false;
        return true;
    }
}
```

#### 结果
![subtree-of-another-tree-1](/images/leetcode/subtree-of-another-tree-1.png)
