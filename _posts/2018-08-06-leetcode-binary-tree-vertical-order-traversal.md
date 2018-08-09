---
layout: post
title: "Leetcode - Algorithm - Binary Tree Vertical Order Traversal "
date: 2018-08-06 21:34:26
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","binary tree","breadth first search"]
level: "medium"
description: >
---

### 题目
Given a binary tree, return the vertical order traversal of its nodes' values. (ie, from top to bottom, column by column).

If two nodes are in the same row and column, the order should be from left to right.

Examples 1:
```
Input: [3,9,20,null,null,15,7]

   3
  /\
 /  \
 9  20
    /\
   /  \
  15   7

Output:

[
  [9],
  [3,15],
  [20],
  [7]
]
```

Examples 2:
```
Input: [3,9,8,4,0,1,7]

     3
    /\
   /  \
   9   8
  /\  /\
 /  \/  \
 4  01   7

Output:

[
  [4],
  [9],
  [3,0,1],
  [8],
  [7]
]
```

Examples 3:
```
Input: [3,9,8,4,0,1,7,null,null,null,2,5] (0's right child is 2 and 1's left child is 5)

     3
    /\
   /  \
   9   8
  /\  /\
 /  \/  \
 4  01   7
    /\
   /  \
   5   2

Output:

[
  [4],
  [9,5],
  [3,0,1],
  [8,2],
  [7]
]
```

### 二叉树广度优先搜索（Breadth First Search - BFS）
BFS遍历数组，没什么说的。另一个关键是：怎么计算节点的“列号(Order)”。我这里的策略是以根节点为基准`0`，往左子节点跑一次`order--`，往右子节点跑一次`order++`。
```
                1 (order=0)
               / \
   (order=-1) 2   3 (order=1)
             / \   \
 (order=-2) 4   5   6 (order=2)
                |
             (order=0)
```


#### 代码

<iframe src="https://leetcode.com/playground/HgxQQsf6/shared" frameBorder="0" width="1000" height="700"></iframe>


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
class Solution {
     public List<List<Integer>> verticalOrder(TreeNode root) {
            List<List<Integer>> result = new ArrayList<>();
            if (root == null) { return result; }
            int[] globalRange = new int[]{0,0};
            Map<Integer,List<Integer>> global = new HashMap<>();
            List<Integer> orders = new ArrayList<>();
            List<TreeNode> nodes = new ArrayList<>();
            orders.add(0);
            nodes.add(root);
            int size = 1;
            while (size > 0) {
                for (int i = 0; i < size; i++) {
                    TreeNode node = nodes.remove(0);
                    int order = orders.remove(0);
                    if (global.containsKey(order)) {
                        global.get(order).add(node.val);
                    } else {
                        global.put(order,new ArrayList<Integer>(Arrays.asList(new Integer[]{node.val})));
                        if (order < globalRange[0]) {
                            globalRange[0] = order;
                        } else if (order > globalRange[1]) {
                            globalRange[1] = order;
                        }
                    }
                    if (node.left != null) { orders.add(order-1); nodes.add(node.left); }
                    if (node.right != null) { orders.add(order+1); nodes.add(node.right); }
                    size = nodes.size();
                }
            }
            for (int i = globalRange[0]; i <= globalRange[1]; i++) {
                result.add(new ArrayList<Integer>(global.get(i)));
            }
            return result;
        }
}
```

#### 结果
![binary-tree-vertical-order-traversal-1](/images/leetcode/binary-tree-vertical-order-traversal-1.png)
