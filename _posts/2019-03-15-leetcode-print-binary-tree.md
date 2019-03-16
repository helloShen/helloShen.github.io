---
layout: post
title: "Leetcode - Algorithm - Print Binary Tree "
date: 2019-03-15 22:54:43
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary tree", "tree"]
level: "medium"
description: >
---

### 题目
Print a binary tree in an m*n 2D string array following these rules:

* The row number m should be equal to the height of the given binary tree.
* The column number n should always be an odd number.
* The root node's value (in string format) should be put in the exactly middle of the first row it can be put. The column and the row where the root node belongs will separate the rest space into two parts (left-bottom part and right-bottom part). You should print the left subtree in the left-bottom part and print the right subtree in the right-bottom part. The left-bottom part and the right-bottom part should have the same size. Even if one subtree is none while the other is not, you don't need to print anything for the none subtree but still need to leave the space as large as that for the other subtree. However, if two subtrees are none, then you don't need to leave space for both of them.
* Each unused space should contain an empty string "".
* Print the subtrees following the same rules.

Example 1:
```
Input:
     1
    /
   2
Output:
[["", "1", ""],
 ["2", "", ""]]
```

Example 2:
```
Input:
     1
    / \
   2   3
    \
     4
Output:
[["", "", "", "1", "", "", ""],
 ["", "2", "", "", "", "3", ""],
 ["", "", "4", "", "", "", ""]]
```

Example 3:
```
Input:
      1
     / \
    2   5
   /
  3
 /
4
Output:

[["",  "",  "", "",  "", "", "", "1", "",  "",  "",  "",  "", "", ""]
 ["",  "",  "", "2", "", "", "", "",  "",  "",  "",  "5", "", "", ""]
 ["",  "3", "", "",  "", "", "", "",  "",  "",  "",  "",  "", "", ""]
 ["4", "",  "", "",  "", "", "", "",  "",  "",  "",  "",  "", "", ""]]
```

* Note: The height of binary tree is in the range of [1, 10].


### 计划经济（BFS整体规划）
先找出整棵树的最大深度，就可以得知整棵树的规模（包括深度和宽度）。然后以`BFS`的顺序，递归遍历整棵树，然后填写整棵树的内容。比如`Example 3`，
```
      1
     / \
    2   5
   /
  3
 /
4
```
知道树的最大深度（树根的深度为`1`）为`4`后，可以计算出，树的规模为`4 * 15`。然后第一行找到中位数`7`的位置填入`1`，下一行在`3`的位置填入`2`，在`11`的位置填入`5`。每一行数字的偏移值可以通过父节点数字的偏移值和当前深度决定。g

```
      1         <- 7号位填入1
     / \
    2   5       <- 3号位填入2，11号位填入5
   /
  3             <- 以此类推
 /
4               <- ...
```

#### 代码
```java
class Solution {
    public List<List<String>> printTree(TreeNode root) {
        int depth = depth(root, 1);
        int width = (int)(Math.pow(2, depth) - 1);
        int aline = (width - 1) / 2;
        List<List<String>> res = new ArrayList<>();
        for (int i = 0; i < depth; i++) {
            String[] arr = new String[width];
            Arrays.fill(arr, "");
            List<String> newLine = new ArrayList<String>(Arrays.asList(arr));
            res.add(newLine);
        }
        helper(1, 0, aline, root, res);
        return res;
    }

    private void helper(int depth, int offset, int aline, TreeNode root, List<List<String>> res) {
        if (root == null) return;
        fill(depth, offset, aline, root.val, res);
        int newAline = (aline - 1 ) / 2;
        helper(depth + 1, offset, newAline, root.left, res);
        helper(depth + 1, offset + aline + 1, newAline, root.right, res);
    }

    private void fill(int depth, int offset, int aline, int val, List<List<String>> res) {
        List<String> list = res.get(depth - 1);
        int idx = offset + aline;
        String old = list.remove(idx);
        list.add(idx, String.valueOf(val));
    }

    private int depth(TreeNode root, int currentDepth) {
        if (root == null) return 0;
        int nextLev = Math.max(depth(root.left, currentDepth + 1), depth(root.right, currentDepth + 1));
        return (nextLev == 0)? currentDepth : nextLev;
    }
}
```

#### 结果
![print-binary-tree-1](/images/leetcode/print-binary-tree-1.png)
