---
layout: post
title: "Leetcode - Algorithm - Binary Tree Right Side View "
date: 2017-06-15 21:41:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","depth first search","breadth first search"]
level: "medium"
description: >
---

### 题目
Given a binary tree, imagine yourself standing on the right side of it, return the values of the nodes you can see ordered from top to bottom.

For example:
Given the following binary tree,
```
   1            <---
 /   \
2     3         <---
 \     \
  5     4       <---
```
You should return `[1, 3, 4]`.

### 总体思路
关于树的题。根据题目要求，基本就要要遍历整棵树才能得到答案。遍历二叉树，递归法是主流。

### DFS. 先右后左的`inorder`遍历。递归版。
关键是树的深度`depth`。以`inorder`（但先右后左）的顺序遍历整棵树，每次探索到一个新的深度的时候，就找到了一个从右侧能看到的节点，因为它露头了。
```
    1       // 顺序：1-3-2
   / \
  2   3
```

#### 代码
```java
public class Solution {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> ret = new ArrayList<>();
        recursion(root,1,new int[]{0},ret);
        return ret;
    }
    public void recursion(TreeNode root, int depth, int[] maxDepth, List<Integer> ret) {
        if (root == null) { return; }
        if (depth > maxDepth[0]) {
            ret.add(root.val);
            maxDepth[0] = depth;
        }
        recursion(root.right, depth+1,maxDepth,ret);
        recursion(root.left, depth+1,maxDepth,ret);
    }
}
```

也可以不维护`maxDepth`。改为用`ret.size()`来获得当前最大深度。

```java
public class Solution {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> ret = new ArrayList<>();
        recursion(root,1,ret);
        return ret;
    }
    public void recursion(TreeNode root, int depth, List<Integer> ret) {
        if (root == null) { return; }
        if (depth > ret.size()) { ret.add(root.val); }
        recursion(root.right,depth+1,ret);
        recursion(root.left,depth+1,ret);
    }
}
```

#### 结果
![binary-tree-right-side-view-1](/images/leetcode/binary-tree-right-side-view-1.png)


### DFS. 先右后左的`inorder`遍历。迭代版。
能用递归遍历，就一定能用迭代遍历。缺点是要承受`Stack`容器的开销。

#### 代码
```java
public class Solution {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> ret = new ArrayList<>();
        Deque<TreeNode> nodeStack = new LinkedList<>();
        Deque<Integer> depthStack = new LinkedList<>();
        int depth = 0, maxDepth = 0;
        TreeNode cur = root;
        while (cur != null || !nodeStack.isEmpty()) {
            while (cur != null) {
                nodeStack.offerFirst(cur);
                depthStack.offerFirst(++depth);
                if (depth > maxDepth) {
                    ret.add(cur.val);
                    maxDepth = depth;
                }
                cur = cur.right;
            }
            cur = nodeStack.pollFirst().left;
            depth = depthStack.pollFirst();
        }
        return ret;
    }
}
```

#### 结果
![binary-tree-right-side-view-2](/images/leetcode/binary-tree-right-side-view-2.png)


### BFS. 记录每层第一个元素
用BFS就不用记录深度，反正每层都取第一个元素记录。

#### 代码
```java
public class Solution {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> ret = new ArrayList<>();
        List<TreeNode> level = new LinkedList<>();
        level.add(root);
        while (!level.isEmpty()) {
            int size = level.size();
            boolean findFirst = false;
            for (int i = 0; i < size; i++) {
                TreeNode node = level.remove(0);
                if (node != null) {
                    if (!findFirst) { ret.add(node.val); findFirst = true; }
                    level.add(node.right);
                    level.add(node.left);
                }
            }
        }
        return ret;
    }
}
```

#### 结果
![binary-tree-right-side-view-3](/images/leetcode/binary-tree-right-side-view-3.png)
