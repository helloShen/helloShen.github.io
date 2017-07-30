---
layout: post
title: "Leetcode - Algorithm - Graph Valid Tree "
date: 2017-07-29 20:39:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["graph","union find"]
level: "medium"
description: >
---

### 学习算法的一点心得
> 想不出brillian的算法不要紧，世界上本来就没有几个人能创造新算法，更没有人能天天创造新算法。可以学别人的算法，但学了之后，遇到类似问题要会用。

`union find`算法很漂亮，不是我想出来的。但曾经用它解过一些题。现在又遇到`图`的题，自然而然就想到了它。

### 题目
Given n nodes labeled from 0 to n - 1 and a list of undirected edges (each edge is a pair of nodes), write a function to check whether these edges make up a valid tree.

For example:
```
Given n = 5 and edges = [[0, 1], [0, 2], [0, 3], [1, 4]], return true.
Given n = 5 and edges = [[0, 1], [1, 2], [2, 3], [1, 3], [1, 4]], return false.
```

Note: you can assume that no duplicate edges will appear in edges. Since all edges are undirected, [0, 1] is the same as [1, 0] and thus will not appear together in edges.

### `树（Tree）`的定义
`树`最简单的定义如下，
> 树中的任意两个节点都有且只有一条边连接。

换句话说，有两个重点，
* 所有节点都是可到达（连通）的
* 没有环

### 用`Union Find`数据结构
用`Union Find`的理由是，
> `Union Find`是这样一张表，它是维护一个图中节点相互连通状态信息最简单的数据结构。

#### 代码
```java
public class Solution {
        /** union find */
        private int[] board = new int[0];
        public boolean validTree(int n, int[][] edges) {
            if (n < 0) { return false; }
            if (n < 2) { return true; } // n == 0 || n == 1
            init(n);
            // connect each vertices by their edge
            for (int[] edge : edges) {
                if (!connect(edge[0],edge[1])) { return false; } // circle found
            }
            // check if each vertices belong to exactly one tree.
            int id = find(0);
            for (int i = 1; i < n; i++) {
                if (find(i) != id) { return false; }
            }
            return true;
        }
        /** initialize board elements by their index. */
        private void init(int size) {
            board = new int[size];
            for (int i = 0; i < size; i++) { board[i] = i; }
        }
        /**
         * connect two vertices if they don't belongs to a single tree, and return true,
         * return false if two given vertices are already connected (circle).
         */
        private boolean connect(int a, int b) {
            int idA = find(a);
            int idB = find(b);
            if (idA == idB) {
                return false;
            } else {
                board[idB] = idA; // add tree B to tree A
                return true;
            }
        }
        /** return the root of the target value */
        private int find(int n) {
            if (board[n] == n) { return n; } // n is the root
            int idN = find(board[n]);
            board[n] = idN; // path compress
            return idN;
        }
}
```

#### 结果
![graph-valid-tree-1](/images/leetcode/graph-valid-tree-1.png)
