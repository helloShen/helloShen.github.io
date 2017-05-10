---
layout: post
title: "Leetcode - Algorithm - Maximum Depth Of Binary Tree "
date: 2017-05-09 22:34:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","depth first search"]
level: "easy"
description: >
---

### 题目
Given a binary tree, find its maximum depth.

The maximum depth is the number of nodes along the longest path from the root node down to the farthest leaf node.

### 递归遍历整棵树，用一个`int[]`记录深度

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
    public int maxDepth(TreeNode root) {
        int[] max = new int[]{ 0 };
        dfs(root,1,max);
        return max[0];
    }
    public void dfs(TreeNode root, int depth, int[] max) {
        if (root == null) { return; }
        max[0] = Math.max(depth,max[0]);
        if (root.left != null) { dfs(root.left,depth+1,max); }
        if (root.right != null) { dfs(root.right,depth+1,max); }
    }
}
```

#### 优化代码
```java
public class Solution {
    public int maxDepth(TreeNode root) {
        if (root == null) { return 0; }
        return Math.max(maxDepth(root.left),maxDepth(root.right)) + 1;
    }
}
```

或者再简短一点，
```java
public class Solution {
    public int maxDepth(TreeNode root) {
        return (root == null)? 0 : Math.max(maxDepth(root.left),maxDepth(root.right)) + 1;
    }
}
```

#### 结果
![maximum-depth-of-binary-tree-1](/images/leetcode/maximum-depth-of-binary-tree-1.png)


### dfs迭代遍历整棵树，同时记录深度

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
    public int maxDepth(TreeNode root) {
        int maxDepth = 0, depth = 0;
        Deque<TreeNode> stack = new LinkedList<>();
        Deque<Integer> depthMemo = new LinkedList<>(); // 恢复到前驱节点，也需要恢复深度
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                depth++;
                maxDepth = Math.max(depth,maxDepth);
                stack.offerFirst(cur);
                depthMemo.offerFirst(depth);
                cur = cur.left;
            }
            cur = stack.pollFirst();
            depth = depthMemo.pollFirst();
            cur = cur.right;
        }
        return maxDepth;
    }
}
```

#### 结果
![maximum-depth-of-binary-tree-2](/images/leetcode/maximum-depth-of-binary-tree-2.png)


### bfs迭代遍历整棵树

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
    public int maxDepth(TreeNode root) {
        int depth = 0;
        List<TreeNode> buffer = new ArrayList<>();
        buffer.add(root);
        while (!buffer.isEmpty()) {
            int size = buffer.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = buffer.remove(0);
                if (node != null) {
                    buffer.add(node.left);
                    buffer.add(node.right);
                }
            }
            if (!buffer.isEmpty()) { depth++; } // 这时候才能确定上一层至少有一个非null的节点
        }
        return depth;
    }
}
```

#### 结果
![maximum-depth-of-binary-tree-3](/images/leetcode/maximum-depth-of-binary-tree-3.png)
