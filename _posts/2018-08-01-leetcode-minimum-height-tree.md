---
layout: post
title: "Leetcode - Algorithm - Minimum Height Tree "
date: 2018-08-01 23:42:41
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["backtracking","tree"]
level: "medium"
description: >
---

### 题目
For a undirected graph with tree characteristics, we can choose any node as the root. The result graph is then a rooted tree. Among all possible rooted trees, those with minimum height are called minimum height trees (MHTs). Given such a graph, write a function to find all the MHTs and return a list of their root labels.

##### Format
The graph contains n nodes which are labeled from 0 to n - 1. You will be given the number n and a list of undirected edges (each edge is a pair of labels).

You can assume that no duplicate edges will appear in edges. Since all edges are undirected, [0, 1] is the same as [1, 0] and thus will not appear together in edges.

Example 1 :
```
Input: n = 4, edges = [[1, 0], [1, 2], [1, 3]]

        0
        |
        1
       / \
      2   3

Output: [1]
```

Example 2 :
```
Input: n = 6, edges = [[0, 3], [1, 3], [2, 3], [4, 3], [5, 4]]

     0  1  2
      \ | /
        3
        |
        4
        |
        5

Output: [3, 4]
```

Note:
* According to the definition of tree on Wikipedia: “a tree is an undirected graph in which any two vertices are connected by exactly one path. In other words, any connected graph without simple cycles is a tree.”
* The height of a rooted tree is the number of edges on the longest downward path between the root and a leaf.

### 暴力计算所有节点两两间距
大不了把所有节点两两间距都算一遍，肯定能找到解。
![minimum-height-tree-a](/images/leetcode/minimum-height-tree-a.png)

#### 代码
```java
class Solution {
        public List<Integer> findMinHeightTrees(int n, int[][] edges) {
            List<Integer> res = new ArrayList<>();
            localEdges = edges;
            table = new int[n][n];
            for (int i = 0; i < n; i++) {
                walkthrough(i,-1,i,0);
            }
            int minHeight = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                int height = 0;
                for (int j = 0; j < n; j++) {
                    height = Math.max(height,table[i][j]);
                }
                if (height <= minHeight) {
                    if (height < minHeight) {
                        minHeight = height;
                        res.clear();
                    }
                    res.add(i);
                }
            }
            return res;
        }
        private int[][] table = new int[0][0];
        private int[][] localEdges = new int[0][0];
        private void walkthrough(int root, int from, int curr, int dis) {
            table[root][curr] = dis;
            table[curr][root] = dis;
            for (int[] edge : localEdges) {
                if (edge[0] == curr && edge[1] != from) {
                    walkthrough(root,curr,edge[1],dis+1);
                } else if (edge[1] == curr && edge[0] != from) {
                    walkthrough(root,curr,edge[0],dis+1);
                }
            }
        }
}
```

#### 结果
![minimum-height-tree-1](/images/leetcode/minimum-height-tree-1.png)


### 找最长线段
这个问题可以转化成，我以哪一个节点为根，把这棵树提起来，树的高度最小。
![minimum-height-tree-b](/images/leetcode/minimum-height-tree-b.png)
这样，一个最直观的解法，就是：
> 以最长线段的中位节点为根，提起来高度最短。

#### 代码：回溯算法遍历整棵树
```java
class Solution {
        public List<Integer> findMinHeightTrees(int n, int[][] edges) {
            if (n == 1) { return new ArrayList<Integer>(Arrays.asList(new Integer[]{0})); }
            init(n,edges);
            for (int i = 0; i < n; i++) {
                backtracking(i,-1,i);
            }
            return new ArrayList<Integer>(res);
        }

        private Set<Integer> res = new HashSet<>();
        private Map<Integer,List<Integer>> localEdges = new HashMap<>();
        private List<Integer> internals = new ArrayList<>();
        private List<Integer> leaves = new ArrayList<>();
        private int longestSegment = 0;
        private int[] path = new int[0];
        private int cursor = 0;

        private void init(int n, int[][] edges) {
            res.clear();
            initEdges(edges);
            initInternalsLeaves();
            initBacktracking(n);
        }
        // 列出和每个节点直接连接（edge）的所有节点列表
        private void initEdges(int[][] edges) {
            localEdges.clear();
            if (edges.length == 0) { return; }
            for (int[] edge : edges) {
                if (localEdges.containsKey(edge[0])) {
                    localEdges.get(edge[0]).add(edge[1]);
                } else {
                    localEdges.put(edge[0],new ArrayList<Integer>(Arrays.asList(new Integer[]{edge[1]})));
                }
                if (localEdges.containsKey(edge[1])) {
                    localEdges.get(edge[1]).add(edge[0]);
                } else {
                    localEdges.put(edge[1],new ArrayList<Integer>(Arrays.asList(new Integer[]{edge[0]})));
                }
            }
        }
        // 把所有节点分为【叶节点】和【内部节点】
        // 所谓一条【路径】就是两个【叶节点】之间的距离
        private void initInternalsLeaves() {
            internals.clear();
            leaves.clear();
            for (Map.Entry<Integer,List<Integer>> list : localEdges.entrySet()) {
                if (list.getValue().size() == 1) {
                    leaves.add(list.getKey());
                } else {
                    internals.add(list.getKey());
                }
            }
        }
        private void initBacktracking(int n) {
            longestSegment = 0;
            path = new int[n+1];
            cursor = 0;
        }
        /**
         * 回溯算法
         * 找出一个叶节点（root）到所有其他叶节点的路径长度
         *      curr: 是当前节点
         *      pre: 是上一个经过的节点
         */
        private void backtracking(int root, int pre, int curr) {
            path[cursor++] = curr;
            // base case: 到了另一个叶节点
            if (leaves.contains(curr) && curr != root) {
                if (cursor >= longestSegment) {
                    // 如果找到更长的路径，首先清空之前的结果列表
                    if (cursor > longestSegment) {
                        longestSegment = cursor;
                        res.clear();
                    }
                    // 更新结果列表: 把中位节点加入结果列表
                    int mid = (cursor - 1) / 2;
                    res.add(path[mid]);
                    if (cursor % 2 == 0) {
                        res.add(path[mid+1]);
                    }
                }
            } else {
                // 遇到非叶节点就递归出去（除了来的这条路）
                for (int next : localEdges.get(curr)) {
                    if (next != pre) {
                        backtracking(root,curr,next);
                    }
                }
            }
            --cursor;
        }
}
```

#### 结果
![minimum-height-tree-2](/images/leetcode/minimum-height-tree-2.png)


### 剥洋葱法：一层层剥掉最外层的叶节点
一棵树的叶节点就是“只和一条边相关”的最外层节点。
![minimum-height-tree-c](/images/leetcode/minimum-height-tree-c.png)

核心思想是：
> 一层层剥离外层叶节点，最后剩下的1，或2个节点，即是我们要找的节点（最核心）。

#### 代码: 类似广度优先迭代遍历二叉树的方法
先遍历所有`edge`信息，将每个节点的直接相邻节点统计出来。用一个`List`维护所有的叶节点，删掉一批再添加一批新的叶节点，直至剩下1~2个节点，那就是我们要找的节点。
![minimum-height-tree-d](/images/leetcode/minimum-height-tree-d.png)

代码主要做了下面几个优化，对结果影响比较大：
1. 像上面这样的统计节点的所有直接相邻节点，可以在O(1)的时间里找到一个叶节点的唯一相邻节点。
2. 原先是用`HashMap<Integer,List<Integer>>`储存上面的统计结果。但实际试下来还是用“泛型数组”：`ArrayList<Integer>[]`更好。范型数组虽然规定不可用，但可以先构造原生类`ArrayList[]`，然后在向下转型。
3. 不需要用一个额外的数组储存所有节点，一轮轮删叶节点，最后返回剩下的。因为最后一轮得到的叶节点（不多于2个节点）即是我们要的答案。
4. 不需要每次删完一批叶节点再重新统计新的叶节点。新的叶节点只会在老叶节点的直接相邻节点集合中产生。

```java
public class Solution {
        @SuppressWarnings({"unchecked","rawtypes"})
        public List<Integer> findMinHeightTrees(int n, int[][] edges) {
            if (n == 0) { return new ArrayList<Integer>(); }
            if (n == 1) { return new ArrayList<Integer>(Arrays.asList(new Integer[]{0})); }
            // 计算所有节点的相关边数
            List<Integer>[] nodes = new ArrayList[n];
            for (int i = 0; i < n; i++) {
                nodes[i] = (ArrayList<Integer>) new ArrayList();
            }
            for (int[] edge : edges) {
                nodes[edge[0]].add(edge[1]);
                nodes[edge[1]].add(edge[0]);
            }
            // 取出所有叶节点，并删掉所有叶节点和相关边
            List<Integer> leaves = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (nodes[i].size() == 1) {
                    leaves.add(i);
                }
            }
            // 在最后还剩少于等于2个节点之前一直循环
            int size = n;
            while (size > 2) {
                List<Integer> newLeafCandidates = new ArrayList<>();
                for (int leaf : leaves) {
                    int neighbour = nodes[leaf].remove(0);
                    nodes[neighbour].remove(new Integer(leaf));
                    if (nodes[neighbour].size() == 1) { newLeafCandidates.add(neighbour); }
                    size--;
                }
                leaves = newLeafCandidates;
            }
            return leaves;
        }
}
```

#### 结果
![minimum-height-tree-3](/images/leetcode/minimum-height-tree-3.png)
