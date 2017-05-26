---
layout: post
title: "Leetcode - Algorithm - Clone Graph "
date: 2017-05-26 02:07:56
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search","breadth first search","graph"]
level: "medium"
description: >
---

### 题目
Clone an undirected graph. Each node in the graph contains a label and a list of its neighbors.


OJ's undirected graph serialization:
Nodes are labeled uniquely.

We use `#` as a separator for each node, and , as a separator for node label and each neighbor of the node.
As an example, consider the serialized graph `{0,1,2#1,2#2,2}`.

The graph has a total of three nodes, and therefore contains three parts as separated by `#`.

* First node is labeled as 0. Connect node 0 to both nodes 1 and 2.
* Second node is labeled as 1. Connect node 1 to node 2.
* Third node is labeled as 2. Connect node 2 to node 2 (itself), thus forming a self-cycle.

Visually, the graph looks like the following:
```
       1
      / \
     /   \
    0 --- 2
         / \
         \_/
```

两点注释：
1. 根据解释，应该是有向图，而不是无向图。但不影响解题。
2. 这里每个节点的编号是唯一的。

### 深度优先dfs遍历，使用额外空间$$O(n)$$
本质上，就是考验怎么遍历整个图。基本思路，就是用两个指针，一个指向原版，一个指向克隆版。一边遍历原版图的过程中，一边生成克隆图。

这里采用深度优先的遍历策略，有`neighbors`就往下一层探索，回来以后再考虑其他兄弟节点。

需要用一个`Map`储存已有节点的引用，避免重复创建`label`值相同的点。

#### 代码
```java
/**
 * Definition for undirected graph.
 * class UndirectedGraphNode {
 *     int label;
 *     List<UndirectedGraphNode> neighbors;
 *     UndirectedGraphNode(int x) { label = x; neighbors = new ArrayList<UndirectedGraphNode>(); }
 * };
 */
public class Solution {
    public UndirectedGraphNode cloneGraph(UndirectedGraphNode node) {
        if (node == null) { return null; }
        UndirectedGraphNode dummyOrigin = new UndirectedGraphNode(0);
        dummyOrigin.neighbors.add(node);
        UndirectedGraphNode dummyCopy = new UndirectedGraphNode(0);
        dfs(dummyOrigin,dummyCopy,new HashMap<Integer,UndirectedGraphNode>());
        return dummyCopy.neighbors.get(0);
    }
    public void dfs(UndirectedGraphNode origin, UndirectedGraphNode copy, Map<Integer,UndirectedGraphNode> existNodes) {
        for (UndirectedGraphNode node : origin.neighbors) {
            UndirectedGraphNode searchInExistNodes = existNodes.get(node.label);
            if (searchInExistNodes != null) {
                copy.neighbors.add(searchInExistNodes);
            } else {
                UndirectedGraphNode newNode = new UndirectedGraphNode(node.label);
                existNodes.put(node.label,newNode);
                copy.neighbors.add(newNode);
                dfs(node,newNode,existNodes);
            }
        }
    }
}
```

#### 结果
银弹！
![clone-graph-1](/images/leetcode/clone-graph-1.png)

### 广度优先bfs遍历，使用额外空间$$O(n)$$
当然也可以广度优先遍历。除了需要一个额外的`Map`记录已有节点以外，还需要使用一个额外的`Map`预存下一层需要深入探索的新节点。效率不会比`dfs`更好。

#### 代码
```java
/**
 * Definition for undirected graph.
 * class UndirectedGraphNode {
 *     int label;
 *     List<UndirectedGraphNode> neighbors;
 *     UndirectedGraphNode(int x) { label = x; neighbors = new ArrayList<UndirectedGraphNode>(); }
 * };
 */
    public class Solution {
        public UndirectedGraphNode cloneGraph(UndirectedGraphNode node) {
            if (node == null) { return null; }
            UndirectedGraphNode dummyOrigin = new UndirectedGraphNode(0);
            dummyOrigin.neighbors.add(node);
            UndirectedGraphNode dummyCopy = new UndirectedGraphNode(0);
            bfs(dummyOrigin,dummyCopy,new HashMap<Integer,UndirectedGraphNode>());
            return dummyCopy.neighbors.get(0);
        }
        public void bfs(UndirectedGraphNode origin, UndirectedGraphNode copy, Map<Integer,UndirectedGraphNode> existNodes) {
            Map<UndirectedGraphNode,UndirectedGraphNode> newNodes = new HashMap<>(); // 用一个Map预存接下来需要深入的新节点
            for (UndirectedGraphNode node : origin.neighbors) {
                UndirectedGraphNode searchInExistNodes = existNodes.get(node.label);
                if (searchInExistNodes != null) {
                    copy.neighbors.add(searchInExistNodes);
                } else {
                    UndirectedGraphNode newNode = new UndirectedGraphNode(node.label);
                    copy.neighbors.add(newNode);
                    newNodes.put(node,newNode);
                    existNodes.put(node.label,newNode);
                }
            }
            for (Map.Entry<UndirectedGraphNode,UndirectedGraphNode> pair : newNodes.entrySet()) {
                bfs(pair.getKey(),pair.getValue(),existNodes);
            }
        }
    }
```

#### 结果
和预想的一样，用一个额外的Map预存接下来要探索的节点，影响了效率。
![clone-graph-2](/images/leetcode/clone-graph-2.png)
