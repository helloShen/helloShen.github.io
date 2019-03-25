---
layout: post
title: "Leetcode - Algorithm - Maximum Width Of Binary Tree "
date: 2019-03-25 17:34:40
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree", "binary tree"]
level: "medium"
description: >
---

### 题目
Given a binary tree, write a function to get the maximum width of the given tree. The width of a tree is the maximum width among all levels. The binary tree has the same structure as a full binary tree, but some nodes are null.

The width of one level is defined as the length between the end-nodes (the leftmost and right most non-null nodes in the level, where the null nodes between the end-nodes are also counted into the length calculation.

Example 1:
```
Input:

           1
         /   \
        3     2
       / \     \  
      5   3     9

Output: 4
Explanation: The maximum width existing in the third level with the length 4 (5,3,null,9).
```

Example 2:
```
Input:

          1
         /  
        3    
       / \       
      5   3     

Output: 2
Explanation: The maximum width existing in the third level with the length 2 (5,3).
```

Example 3:
```
Input:

          1
         / \
        3   2
       /        
      5      

Output: 2
Explanation: The maximum width existing in the second level with the length 2 (3,2).
```

Example 4:
```
Input:

          1
         / \
        3   2
       /     \  
      5       9
     /         \
    6           7
Output: 8
Explanation:The maximum width existing in the fourth level with the length 8 (6,null,null,null,null,null,null,7).
```

Note:
* Answer will in the range of 32-bit signed integer.

### 计算二叉树每个节点的偏移值
给二叉树每一层的每一点在本层的绝对偏移值。
```
         1                      |1|                     d = 0, size = 2^0 = 1
        / \
       3   2                    |3|2|                   d = 1, size = 2^1 = 2
      /     \  
     5       9                  |5| | |9|               d = 2, size = 2^2 = 4
    /         \
   6           7                |6| | | | | | |7|       d = 3, size = 2^3 = 8
-------------------------------------------------
                                 0 1 2 3 4 5 6 7
```

左右两个子节点的绝对偏移值取决于父节点的偏移值，
> if offset(parent) = n, offset(left_child) = n * 2, offset(right_child) = n * 2 + 1

每层最大宽度为：`maxOffset - minOffset + 1`。

#### 代码
```java
class Solution {
    public int widthOfBinaryTree(TreeNode root) {
        Map<Integer, int[]> map = new HashMap<>();
        parseTree(root, 0, 0, map);
        int maxScope = 0;
        for (Map.Entry<Integer, int[]> entry : map.entrySet()) {
            int[] scope = entry.getValue();
            maxScope = Math.max(maxScope, scope[1] - scope[0] + 1);
        }
        return maxScope;
    }

    private void parseTree(TreeNode node, int depth, int offset, Map<Integer, int[]> map) {
        if (node != null) {
            if (!map.containsKey(depth)) map.put(depth, new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE});
            int[] scope = map.get(depth);
            scope[0] = Math.min(scope[0], offset);
            scope[1] = Math.max(scope[1], offset);
            parseTree(node.left, depth + 1, offset * 2, map);
            parseTree(node.right, depth + 1, offset * 2 + 1, map);
        }
    }
}
```

#### 结果
![maximum-width-of-binary-tree-1](/images/leetcode/maximum-width-of-binary-tree-1.png)
