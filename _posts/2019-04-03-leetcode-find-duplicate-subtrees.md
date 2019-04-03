---
layout: post
title: "Leetcode - Algorithm - Find Duplicate Subtrees "
date: 2019-04-03 14:10:10
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["tree", "preorder"]
level: "medium"
description: >
---

### 题目
Given a binary tree, return all duplicate subtrees. For each kind of duplicate subtrees, you only need to return the root node of any one of them.

Two trees are duplicate if they have the same structure with same node values.

Example 1:
```
        1
       / \
      2   3
     /   / \
    4   2   4
       /
      4
```

The following are two duplicate subtrees:
```
      2
     /
    4
```
and
```
    4
```

Therefore, you need to return above trees' root in the form of a list.

### 树串行化
关键问题在于：怎么记录一棵树的信息？ 因为涉及到两棵树的比较。如果每次都同时完整遍历两棵树，复杂度太高。

记录一棵树的信息，最直观的思路是把一棵树串行化。串行化的过程中，必须把空节点也标出来，否则会出现歧义，

比如例子中的树，
```
        1
       / \
      2   3
     /   / \
    4   2   4
       /
      4
```

串行化之后变成，
```
124###324###4##
```

遍历整棵树的过程中，串行化每个节点。然后用一个`Map<String, Integer>`对每个不同的子树出现的次数计数。当第二次遇到时，将结果存入将要输出的列表。

#### 代码
```java
class Solution {
    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        List<TreeNode> res = new LinkedList<>();
        Map<String, Integer> memo = new HashMap<>();
        preorder(root, memo, res);
        return res;
    }

    private String preorder(TreeNode root, Map<String, Integer> memo, List<TreeNode> res) {
        if (root == null) return "#";
        String serial = root.val
                        + preorder(root.left, memo, res)
                        + preorder(root.right, memo, res);
        Integer history = memo.getOrDefault(serial, 0);
        if (history == 1) res.add(root);
        memo.put(serial, history + 1);
        return serial;
    }
}
```

#### 结果
![find-duplicate-subtrees-1](/images/leetcode/find-duplicate-subtrees-1.png)
