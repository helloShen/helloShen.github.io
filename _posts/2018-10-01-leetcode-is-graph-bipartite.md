---
layout: post
title: "Leetcode - Algorithm - Is Graph Bipartite "
date: 2018-10-01 18:08:19
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search", "breadth first search", "graph"]
level: "medium"
description: >
---

### 题目
Given an undirected graph, return true if and only if it is bipartite.

Recall that a graph is bipartite if we can split it's set of nodes into two independent subsets A and B such that every edge in the graph has one node in A and another node in B.

The graph is given in the following form: `graph[i]` is a list of indexes `j` for which the edge between nodes `i` and `j` exists.  Each node is an integer between `0` and `graph.length - 1`.  There are no self edges or parallel edges: `graph[i]` does not contain `i`, and it doesn't contain any element twice.

Example 1:
```
Input: [[1,3], [0,2], [1,3], [0,2]]
Output: true
Explanation:
The graph looks like this:
0----1
|    |
|    |
3----2
We can divide the vertices into two groups: {0, 2} and {1, 3}.
```

Example 2:
```
Input: [[1,2,3], [0,2], [0,1,3], [0,2]]
Output: false
Explanation:
The graph looks like this:
0----1
| \  |
|  \ |
3----2
We cannot find a way to divide the set of nodes into two independent subsets.
```

Note:
* graph will have length in range [1, 100].
* graph[i] will contain integers in range [0, graph.length - 1].
* graph[i] will not contain i or duplicate values.
* The graph is undirected: if any element j is in graph[i], then i will be in graph[j].


### 分析问题
以下面这个图为例，
```
0----1
| \  |
|  \ |
3----2
```
假设`0`属于`a组`，因为`0-1`相连，所以点`1`只能属于`b组`。同理点`2`，`3`也必须属于`b组`。
```
a组   b组
0 --> 1
  +-> 2
  +-> 3

点0属于a组，推出点[1,2,3]必须属于b组。
```
但是，如果点`1`属于`b组`，因为`1-2`相连，点`2`又必须属于`a组`。点`2`既只能属于`a组`，又只能属于`b组`，矛盾。所以此图不是`bipartite`。
```
b组   a组
1 --> 0
  +-> 2

点1输入b组，推出点[0,2]必须属于a组。

点2同时既只能属于a组，又只能属于b组。矛盾。
```

### BFS
根据上面的分析，遍历整个图，如果能推出矛盾，就可排除是`bipartite`的可能。

假设有图，
```
0----1
|    |
|    |
3----2
```

下图演示了以广度优先（BFS）遍历图是的过程，
![is-graph-bipartite-a](/images/leetcode/is-graph-bipartite-a.png)

注意题目并没有保证`graph`没有子图，是一个完整的图。所以要考虑有多个独立子图的情况。
![is-graph-bipartite-b](/images/leetcode/is-graph-bipartite-b.png)

#### 代码
用一个数组`int[]`标记每个点的着色情况。
* `1`代表`a组（黄色）`
* `-1`代表`b组（蓝色）`
* `0`代表未着色

```java
class Solution {
    public boolean isBipartite(int[][] graph) {
        int[] groups = new int[graph.length]; // [0]: no group,  [1]: group a,  [-1]: group b
        List<Integer> list = new ArrayList<>();
        int group = 1; // [1]: group a,  [-1]: group b
        for (int k = 0; k < graph.length; k++) { // graph can be composed of different isolated sub-graphs
            if (groups[k] != 0) continue;
            list.add(k);
            while (!list.isEmpty()) {
                int size = list.size();
                for (int i = 0; i < size; i++) {
                    int num = list.remove(0);
                    groups[num] = group;
                    for (int j = 0; j < graph[num].length; j++) {
                        int neighbor = graph[num][j];
                        if (groups[neighbor] == group) return false;
                        if (groups[neighbor] == 0) {
                            groups[neighbor] = -group;
                            list.add(neighbor);
                        }
                    }
                }
                group = -group;
            }
        }
        return true;
    }
}
```

#### 结果
![is-graph-bipartite-1](/images/leetcode/is-graph-bipartite-1.png)


### DFS
能用广度优先，就能用深度优先（DFS）。

#### 代码
```java
class Solution {
    public boolean isBipartite(int[][] graph) {
        localGraph = graph;
        groups = new int[graph.length]; // [0]: no group, [1]: group a, [-1]: group b
        int group = 1; // group a is default value
        for (int i = 0; i < graph.length; i++) {
            if (groups[i] != 0) continue;
            if (!dfs(i, group)) return false;
        }
        return true;
    }

    private int[][] localGraph;
    private int[] groups;

    private boolean dfs(int node, int group) {
        if (groups[node] != 0) return groups[node] == group;
        groups[node] = group;
        for (int i = 0; i < localGraph[node].length; i++) {
            if (!dfs(localGraph[node][i], -group)) return false;
        }
        return true;
    }
}
```

#### 结果
![is-graph-bipartite-2](/images/leetcode/is-graph-bipartite-2.png)
