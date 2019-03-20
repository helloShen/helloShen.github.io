---
layout: post
title: "Leetcode - Algorithm - All Paths From Source To Target "
date: 2019-03-20 19:32:24
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search", "breadth first search", "backtracking"]
level: "medium"
description: >
---

### 题目
Given a directed, acyclic graph of N nodes.  Find all possible paths from node 0 to node N-1, and return them in any order.

The graph is given as follows:  the nodes are 0, 1, ..., graph.length - 1.  graph[i] is a list of all nodes j for which the edge (i, j) exists.

Example:
```
Input: [[1,2], [3], [3], []]
Output: [[0,1,3],[0,2,3]]
Explanation: The graph looks like this:
0--->1
|    |
v    v
2--->3
There are two paths: 0 -> 1 -> 3 and 0 -> 2 -> 3.
```

Note:
* The number of nodes in the graph will be in the range [2, 15].
* You can print different paths in any order, but you should keep the order of nodes inside one path.

### 广度优先（BFS）
首先很明确，这是一个搜索问题。搜索的顺序应该是从目的地开始倒推。

思路比较清晰的解法是广度优先（BFS）。从目的地`3`找到所有上一层`[1,2]`，
```
        3
      /   \
     1     2
```

再推到更上一层`[0]`，
```
        3
      /   \
     1     2
     |     |
     0     0
```

但这里BFS的问题是，需要重新统计所有的edges。因为给出的edges是以起点聚类的，`graph[0] = [1,2]`表示从`[0]`点出发，可以到`[1]`和`[2]`点。但倒推需要知道的是要到`[1]`点，可以从`[0]`点来。这就需要额外的一个统计过程，以及额外的空间储存统计数据。所以BFS效率稍慢。

#### 代码
```java
class Solution {
    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> bfsQueue = new LinkedList<>(), outPut = new LinkedList<>();
        int[][] edgesTable = statistic(graph);
        bfsQueue.add(new LinkedList<Integer>(Arrays.asList(new Integer[]{graph.length - 1})));
        while (!bfsQueue.isEmpty()) {
            int size = bfsQueue.size();
            for (int i = 0; i < size; i++) {
                List<Integer> path = bfsQueue.remove(0);
                int head = path.get(0);
                int[] pres = edgesTable[head];
                for (int pre : pres) {
                    if (pre == -1) break;
                    List<Integer> newPath = new LinkedList<>(path);
                    newPath.add(0, pre);
                    if (pre == 0) {
                        outPut.add(newPath);
                    } else {
                        bfsQueue.add(newPath);
                    }
                }
            }
        }
        return outPut;
    }

    private int[][] statistic(int[][] graph) {
        int[][] edgesTable = new int[graph.length][15];
        for (int[] edge : edgesTable) Arrays.fill(edge, -1);
        int[] ps = new int[graph.length];
        for (int i = 0; i < graph.length; i++) {
            int[] arr = graph[i];
            for (int j = 0; j < arr.length; j++) {
                int to = arr[j];
                edgesTable[to][ps[to]++] = i;
            }
        }
        return edgesTable;
    }
}
```

#### 结果
![all-paths-from-source-to-target-1](/images/leetcode/all-paths-from-source-to-target-1.png)


### 深度优先（DFS）
深度优先（DFS）则不需要重新统计edges。直接从`[0]`点出发，利用回溯算法，暴力遍历所有可能的路径，只要最后能走到目的地，就把路径储存到结果中。

#### 代码
```java
class Solution {
    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        localGraph = graph;
        res = new LinkedList<List<Integer>>();
        dfs(new LinkedList<Integer>(Arrays.asList(new Integer[]{0})), 0, graph.length - 1);
        return res;
    }

    int[][] localGraph;
    List<List<Integer>> res;

    private void dfs(List<Integer> path, int node, int target) {
        if (node == target) {
            res.add(new ArrayList<Integer>(path));
        } else {
            for (int next : localGraph[node]) {
                path.add(next);
                dfs(path, next, target);
                path.remove(path.size() - 1);
            }
        }
    }
}
```

#### 结果
![all-paths-from-source-to-target-2](/images/leetcode/all-paths-from-source-to-target-2.png)
