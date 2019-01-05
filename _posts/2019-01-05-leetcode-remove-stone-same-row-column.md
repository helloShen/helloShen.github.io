---
layout: post
title: "Leetcode - Algorithm - Remove Stone Same Row Column "
date: 2019-01-05 17:06:52
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["union find", "math", "graph", "dfs", "backtracking"]
level: "medium"
description: >
---

### 题目
On a 2D plane, we place stones at some integer coordinate points.  Each coordinate point may have at most one stone.

Now, a move consists of removing a stone that shares a column or row with another stone on the grid.

What is the largest possible number of moves we can make?

Example 1:
```
Input: stones = [[0,0],[0,1],[1,0],[1,2],[2,1],[2,2]]
Output: 5
```

Example 2:
```
Input: stones = [[0,0],[0,2],[1,1],[2,0],[2,2]]
Output: 3
```

Example 3:
```
Input: stones = [[0,0]]
Output: 0
```

Note:
* 1 <= stones.length <= 1000
* 0 <= stones[i][j] < 10000

### 用回溯算法实现DFS
暴力解很容易实现。是一个标准的回溯算法实现的DFS深度优先搜索。

#### 代码
```java
class Solution {
    public int removeStones(int[][] stones) {
        maxMoves = 0;
        statistic(stones);
        backtracking(0);
        return maxMoves;
    }

    private void backtracking(int moves) {
        maxMoves = Math.max(moves, maxMoves);
        for (int i = 0; i < list.size(); i++) {
            int[] stone = list.get(i);
            List<Integer> stonesOnThisRow = rowMap.get(stone[0]);
            boolean shareOnRow = (stonesOnThisRow != null) && (stonesOnThisRow.size() > 1);
            List<Integer> stonesOnThisCol = colMap.get(stone[1]);
            boolean shareOnCol = (stonesOnThisCol != null) && (stonesOnThisCol.size() > 1);
            if (shareOnRow || shareOnCol) {
                list.remove(i);
                stonesOnThisRow.remove(new Integer(stone[1]));
                stonesOnThisCol.remove(new Integer(stone[0]));
                backtracking(moves + 1);
                list.add(i, stone);
                stonesOnThisRow.add(stone[1]);
                stonesOnThisCol.add(stone[0]);
            }
        }
    }

    private int maxMoves;
    private List<int[]> list = new LinkedList<>();
    private Map<Integer, List<Integer>> rowMap = new HashMap<>();
    private Map<Integer, List<Integer>> colMap = new HashMap<>();

    private void statistic(int[][] stones) {
        list.clear();
        rowMap.clear();
        colMap.clear();
        for (int[] stone : stones) {
            list.add(stone);
            if (!rowMap.containsKey(stone[0])) rowMap.put(stone[0], new LinkedList<Integer>());
            rowMap.get(stone[0]).add(stone[1]);
            if (!colMap.containsKey(stone[1])) colMap.put(stone[1], new LinkedList<Integer>());
            colMap.get(stone[1]).add(stone[0]);
        }
    }
}
```

#### 结果
![remove-stone-same-row-column-1](/images/leetcode/remove-stone-same-row-column-1.png)


### union-find
如果我们把同行，同列的点都算成一组（算作连通图）。如果每次都小心地从连通图的边缘开始删除点（而不是从中间截断），我们总是可以把一个连通图删到只剩一个点。
```
只要不先删除中心点，每个连通图总能删到只剩一个点。

       0
       |
       |
0------0------0
       |
       |
       0
```

因此，这个问题很容易就转化成连通图问题。最终会剩下的最少点就是连通图的数量。可挪走的最大石头总数就是石头总数减去连通图的数量。

#### 代码
```java
class Solution {
    public int removeStones(int[][] stones) {
        init(stones.length);
        Map<Integer, Integer> rowMap = new HashMap<>();
        Map<Integer, Integer> colMap = new HashMap<>();
        for (int i = 0; i < stones.length; i++) {
            int[] stone = stones[i];
            if (!rowMap.containsKey(stone[0])) {
                rowMap.put(stone[0], i);
            } else {
                union(rowMap.get(stone[0]), i);
            }
            if (!colMap.containsKey(stone[1])) {
                colMap.put(stone[1], i);
            } else {
                union(colMap.get(stone[1]), i);
            }
        }
        return stones.length - countGroup();
    }

    private int[] board;

    private void init(int size) {
        board = new int[size];
        for (int i = 0; i < size; i++) board[i] = i;
    }

    // merge group B to group A
    private void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        board[rootB] = rootA;
    }

    private int find(int a) {
        if (board[a] == a) return a;
        int root = find(board[a]);
        board[a] = root; // path compress
        return root;
    }

    private int countGroup() {
        int count = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == i) count++;
        }
        return count;
    }
}
```

#### 结果
![remove-stone-same-row-column-2](/images/leetcode/remove-stone-same-row-column-2.png)
