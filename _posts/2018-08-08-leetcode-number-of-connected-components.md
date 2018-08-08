---
layout: post
title: "Leetcode - Algorithm - Number Of Connected Components "
date: 2018-08-08 15:29:50
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["union find","graph"]
level: "medium"
description: >
---

### 题目
Given n nodes labeled from 0 to n - 1 and a list of undirected edges (each edge is a pair of nodes), write a function to find the number of connected components in an undirected graph.

Example 1:
```
Input: n = 5 and edges = [[0, 1], [1, 2], [3, 4]]

     0          3
     |          |
     1 --- 2    4

Output: 2
```

Example 2:
```
Input: n = 5 and edges = [[0, 1], [1, 2], [2, 3], [3, 4]]

     0           4
     |           |
     1 --- 2 --- 3

Output:  1
```

Note:
You can assume that no duplicate edges will appear in edges. Since all edges are undirected, [0, 1] is the same as [1, 0] and thus will not appear together in edges.



### union-find
图的连通树问题肯定是`Union Find`算法。

`union-find`的核心原理就是：
> 除了一棵子树的根节点的值等于它的下标，其他所有节点值都等于它前驱节点的下标。

再强调一下，
![number-of-connected-components-a](/images/leetcode/number-of-connected-components-a.png)

另外一种最直观的做法，像下面这样， **将所有节点的值都改成它根节点的值**，在合并两棵子树的时候，你得修改其中一棵树所有节点的值。

![number-of-connected-components-b](/images/leetcode/number-of-connected-components-b.png)

#### 代码
```java
class Solution {
    public int countComponents(int n, int[][] edges) {
        if (n <= 0 || edges == null) { return 0; }
        broad = new int[n];
        for (int i = 0; i < n; i++) { broad[i] = i; }
        for (int[] edge : edges) {
            union(edge[0],edge[1]);
        }
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (broad[i] == i) { count++; }
        }
        return count;
    }
    //union-find
    private int[] broad;
    private void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        broad[rootB] = rootA;
    }
    private int find(int a) {
        if (broad[a] == a) {
            return a;
        }
        broad[a] = find(broad[a]); //路径压缩
        return broad[a];
    }
}
```

#### 结果
![number-of-connected-components-1](/images/leetcode/number-of-connected-components-1.png)
