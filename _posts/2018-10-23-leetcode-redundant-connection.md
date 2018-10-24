---
layout: post
title: "Leetcode - Algorithm - Redundant Connection "
date: 2018-10-23 17:27:24
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["graph", "union find"]
level: "medium"
description: >
---

### 题目
In this problem, a tree is an undirected graph that is connected and has no cycles.

The given input is a graph that started as a tree with N nodes (with distinct values 1, 2, ..., N), with one additional edge added. The added edge has two different vertices chosen from 1 to N, and was not an edge that already existed.

The resulting graph is given as a 2D-array of edges. Each element of edges is a pair [u, v] with u < v, that represents an undirected edge connecting nodes u and v.

Return an edge that can be removed so that the resulting graph is a tree of N nodes. If there are multiple answers, return the answer that occurs last in the given 2D-array. The answer edge [u, v] should be in the same format, with u < v.

Example 1:
```
Input: [[1,2], [1,3], [2,3]]
Output: [2,3]
Explanation: The given undirected graph will be like this:
  1
 / \
2 - 3
```

Example 2:
```
Input: [[1,2], [2,3], [3,4], [1,4], [1,5]]
Output: [1,4]
Explanation: The given undirected graph will be like this:
5 - 1 - 2
    |   |
    4 - 3
```

Note:
* The size of the input 2D-array will be between 3 and 1000.
* Every integer represented in the 2D-array will be between 1 and N, where N is the size of the input array.

Update (2017-09-26):
* We have overhauled the problem description + test cases and specified clearly the graph is an undirected graph. For the directed graph follow up please see Redundant Connection II). We apologize for any inconvenience caused.


### 抽象成数学模型
题目中已经提到了：
> 树是一个无向无环图。

多出来的一条边一定构成一个“环”。考虑例子：`[[1,2], [1,3], [2,3]]`，
```
[[1,2], [1,3]]

只有两条边的时候是一棵树：
  1
 / \
2   3

加入第三条边[2,3]的时候，构成了一个环。所以[2,3]是多余的边。
  1
 / \
2 - 3
```

同样，`[[1,2], [2,3], [3,4], [1,4], [1,5]]`的例子，
```
[[1,2], [2,3], [3,4]]

前3条边没有构成环，
5   1 - 2
        |
    4 - 3

第四条边[1,4]，构成了环，所以它是多余的。   
5   1 - 2
    |   |
    4 - 3
```

### Union Find
无向图的连通问题，可以考虑Union Find。无向图中“环”的问题，转换到Union Find模型，就是 **“连通两个已经是同组的节点”**。比如，
```
此时4个节点已经连通。都属于1个小组，根节点只有一个。
    1 - 2
        |
    4 - 3

此时连通[1,4]，就是把属于同一小组的两个节点再次连通，就构成一个环。
    1 - 2
    |   |
    4 - 3
```

#### 代码
```java
class Solution {
    public int[] findRedundantConnection(int[][] edges) {
        int size = 1000;
        board = new int[size + 1];
        for (int i = 1; i <= size; i++) board[i] = i;
        for (int[] edge : edges) {
            if (!union(edge[0], edge[1])) return edge;
        }
        return null;
    }
    private int[] board;
    private boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) return false;
        board[rootB] = rootA;
        return true;
    }
    private int find(int a) {
        if (board[a] == a) return a;
        int prev = find(board[a]);
        board[a] = prev; // path compression
        return prev;
    }
}
````

#### 结果
![redundant-connection-1](/images/leetcode/redundant-connection-1.png)
