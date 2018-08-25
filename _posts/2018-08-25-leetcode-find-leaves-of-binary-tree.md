---
layout: post
title: "Leetcode - Algorithm - Find Leaves Of Binary Tree "
date: 2018-08-25 13:57:12
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree","binary tree"]
level: "medium"
description: >
---

### 题目
Given a binary tree, collect a tree's nodes as if you were doing this: Collect and remove all leaves, repeat until the tree is empty.

Example:
```
Input: [1,2,3,4,5]

          1
         / \
        2   3
       / \     
      4   5    
Output: [[4,5,3],[2],[1]]
```

Explanation:

1. Removing the leaves [4,5,3] would result in this tree:
```
          1
         /
        2          
```

2. Now removing the leaf [2] would result in this tree:
```
          1          
```

3. Now removing the leaf [1] would result in the empty tree:
```
          []         
```

### post-order DFS遍历二叉树
核心思想就是给每个节点表层级，层级的规则如下，
1. NULL节点层级为-1
2. 叶节点层级为0
3. 每个节点的层级等于它左右两个节点层级较高的那一个+1

```
Input: [1,2,3,4,5,6]

          1[3]
         / \
      2[2]  3[0]
       / \     
    4[0] 5[1]
           \
            6[0]
Output: [[4,6,3],[5],[2],[1]]
```

#### 代码
```java
class Solution {
    public List<List<Integer>> findLeaves(TreeNode root) {
        List<List<Integer>> list = new ArrayList<>();
        map = new HashMap<Integer, List<Integer>>();
        dfs(root);
        for (int i = 0; i < map.size(); i++) {
            list.add(map.get(i));
        }
        return list;
    }

    /**=================== 【以下为私有】 ========================= */
    private Map<Integer,List<Integer>> map;
    // 把节点按层次归类
    // 叶节点层次为0，越往父节点，层次越高。 NULL节点层次为-1。
    private int dfs(TreeNode node) {
        if (node == null) {
            return -1;
        }
        int left = dfs(node.left);
        int right = dfs(node.right);
        int level = Math.max(left, right) + 1;        
        if (!map.containsKey(level)) {
            map.put(level, new ArrayList<Integer>());
        }
        map.get(level).add(node.val);
        return level;
    }
}
```

#### 结果
![find-leaves-of-binary-tree-1](/images/leetcode/find-leaves-of-binary-tree-1.png)
