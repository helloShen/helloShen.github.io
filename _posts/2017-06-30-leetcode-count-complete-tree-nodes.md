---
layout: post
title: "Leetcode - Algorithm - Count Complete Tree Nodes "
date: 2017-06-30 14:43:37
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","tree"]
level: "medium"
description: >
---

### 题目
Given a complete binary tree, count the number of nodes.

Definition of a complete binary tree from Wikipedia:
In a complete binary tree every level, except possibly the last, is completely filled, and all nodes in the last level are as far left as possible. It can have between 1 and 2h nodes inclusive at the last level h.

### Full Binary Tree & Complete Binary Tree
先搞清楚两个概念：

1. 满二叉树（Full Binary Tree）：就是除了最后一排叶节点，所有节点都有完整的两个子节点。
![full-binary-search-tree](/images/leetcode/full-binary-search-tree)

2. 完全二叉树（Complete Binary Tree)：最后一排叶节点不需要全，但必须全部靠左。
![complete-binary-search-tree](/images/leetcode/complete-binary-search-tree)


### $$O(n)$$ 的遍历每个节点的做法都不是最优解
遍历二叉树很简单。

#### 代码
```java
public class Solution {
    public int countNodes(TreeNode root) {
        if (root == null) { return 0; }
        return 1 + countNodes(root.left) + countNodes(root.right);
    }
}
```

#### 结果
![count-complete-tree-nodes-1](/images/leetcode/count-complete-tree-nodes-1.png)


### 只计算最后一排叶节点的数量，复杂度也是 $$O(n)$$
想法是，只计算叶节点的数量。之前的树是一颗满二叉树，节点数可以算出来。

因为要计算最后一排叶节点，也得遍历整棵二叉树，才能到达每个叶节点。所以没有节省时间。反而多出来了做计算的开销。

#### 代码
```java
public class Solution {
    public int countNodes(TreeNode root) {
        if (root == null) { return 0; }
        int maxDepth = -1;
        TreeNode cur = root;
        while (cur != null) { cur = cur.left; maxDepth++; }
        return (1 << maxDepth) - 1 + countLeaves(root,0,maxDepth);
    }
    public int countLeaves(TreeNode root, int depth, int maxDepth) {
        if (root == null) { return 0; }
        if (depth == maxDepth) { return 1; }
        return countLeaves(root.left,depth+1,maxDepth) + countLeaves(root.right,depth+1,maxDepth);
    }
}
```

#### 结果
![count-complete-tree-nodes-2](/images/leetcode/count-complete-tree-nodes-2.png)


### 最优解，每次只进入左，右子树中的一棵，复杂度 $$O(\log_{}{n}^2)$$
有没有办法，不遍历所有节点呢？能不能靠计算？答案是可以！依据的一个事实就是：
> 满二叉树的节点数是可以算出来的：$$2^{depth+1} - 1$$

进入下一层之前，先计算右子树的深度。如果等于当前深度减`1`，说明，**左子树是一棵满二叉树**，因此只递归进入右子树。
```
        1
       / \
      2   3
     / \ /
    4  5 6  
```

否则，说明 **右子树是一颗满二叉树**，因此递归只进入左子树。
```
        1
       / \
      2   3
     /
    4
```

处理深度的时候，需要小心。深度最好从`0`开始计量。也就是说`root`节点的深度为`0`。
```
```
        1           depth = 0
       / \
      2   3         depth = 1
     / \ / \
    4  5 6  7       depth = 2
```


#### 代码
```java
public class Solution {
    public int countNodes(TreeNode root) {
        if (root == null) { return 0; }
        int height = height(root);
        int rightHeight = height(root.right);
        if (rightHeight == height-1) { // 左子树是full tree
            return (1 << height) + countNodes(root.right);
        } else { // 右子树是full tree，但少一层
            return (1 << (rightHeight+1)) + countNodes(root.left);
        }
    }
    private int height(TreeNode root) {
        int count = -1;
        TreeNode cur = root;
        while (cur != null) { count++; cur = cur.left; }
        return count;
    }
}
```

#### 结果
![count-complete-tree-nodes-3](/images/leetcode/count-complete-tree-nodes-3.png)
