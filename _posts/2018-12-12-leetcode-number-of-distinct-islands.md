---
layout: post
title: "Leetcode - Algorithm - Number Of Distinct Islands "
date: 2018-12-12 00:55:05
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["graph", "union find"]
level: "medium"
description: >
---

### 题目
Given a non-empty 2D array grid of `0`'s and `1`'s, an island is a group of `1`'s (representing land) connected 4-directionally (horizontal or vertical.) You may assume all four edges of the grid are surrounded by water.

Count the number of distinct islands. An island is considered to be the same as another if and only if one island can be translated (and not rotated or reflected) to equal the other.

Example 1:
```
11000
11000
00011
00011
```
Given the above grid map, return 1.

Example 2:
```
11011
10000
00001
11011
```
Given the above grid map, return 3.

Notice that:
```
11
1
```
and
```
 1
11
```
are considered different island shapes, because we do not consider reflection / rotation.
Note: The length of each dimension in the given grid does not exceed 50.

### union-find解连通图问题
连通图问题最优解法是`union-find`。我们可以得到单纯的岛的数量。然后再去重。去重我们用一个 **偏移值** 数列来标识一个岛形状的特征。比如左上角这个岛，
```
11000
11000
00011
00011
```

4个`1`的偏移值分别为`[0,0]`,`[1,0]`,`[0,1]`,`[1,1]`。所以偏移值序列为`[0,0,1,0,0,1,1,1]`。右下角岛的偏移值和左上角的一致，所以形状相同。

也可以用`String`代替`List<Integer>`记录偏移值序列。

#### 代码
```java
class Solution {

    private static class UnionFind {

        private int height;
        private int width;
        private int size;
        private int[] board;

        public UnionFind(int height, int width) {
            this.height = height;
            this.width = width;
            size = height * width;
            board = new int[size];
            for (int i = 0; i < size; i++) board[i] = i;
        }

        public void kill(int a) {
            board[a] = -1;
        }

        public void union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);
            board[rootB] = rootA;
        }

        public int find(int n) {
            if (board[n] == n) return n;
            int root = find(board[n]);
            board[n] = root; // path compression
            return root;
        }

        public int indexOf(int x, int y) {
            return x * width + y;
        }

        public int[] indexToPos(int idx) {
            int[] pos = new int[2];
            pos[0] = idx / width;
            pos[1] = idx % width;
            return pos;
        }

        public int count() {
            Map<Integer, List<Integer>> shapeMap = new HashMap<>();
            for (int i = 0; i < size; i++) {
                if (board[i] >= 0) {
                    int root = find(i);
                    int[] pos = indexToPos(i);
                    int[] rootPos = indexToPos(root);
                    int[] offset = new int[]{pos[0] - rootPos[0], pos[1] - rootPos[1]};
                    if (!shapeMap.containsKey(root)) shapeMap.put(root, new LinkedList<Integer>());
                    List<Integer> shapeId = shapeMap.get(root);
                    shapeId.add(offset[0]);
                    shapeId.add(offset[1]);
                }
            }
            Set<List<Integer>> shapeSet = new HashSet<>(shapeMap.values());
            return shapeSet.size();
        }

    }

    public int numDistinctIslands(int[][] grid) {
        if (grid.length == 0) return 0;
        int height = grid.length;
        int width = grid[0].length;
        UnionFind board = new UnionFind(height, width);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int idx = board.indexOf(i, j);
                if (grid[i][j] == 1) {
                    if (i > 0 && grid[i - 1][j] == 1) board.union(idx, board.indexOf(i - 1, j));
                    if (j > 0 && grid[i][j - 1] ==1) board.union(idx, board.indexOf(i, j - 1));
                } else {
                    board.kill(idx);
                }
            }
        }
        return board.count();
    }
}
```

#### 结果
![number-of-distinct-islands-1](/images/leetcode/number-of-distinct-islands-1.png)


### DFS
理论上，`DFS`法效率比`union-find`低。但这里`union-find`解法必须计算完`union-find`之后，再统计形状。而`DFS`法可以在做DFS遍历的过程中就把形状标识记录下来，所以对这题来讲，效率反而更高。

#### 代码
```java
class Solution {

    public int numDistinctIslands(int[][] grid) {
        if (grid.length == 0) return 0;
        height = grid.length;
        width = grid[0].length;
        localGrid = grid;
        Set<String> islands = new HashSet<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (localGrid[i][j] == 1) {
                    islands.add(dfs(i, j, 0, 0).toString());
                }
            }
        }
        return islands.size();
    }

    private static final int[][] dirs = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private int height;
    private int width;
    private int[][] localGrid;

    private StringBuilder dfs(int x, int y, int offsetX, int offsetY) {
        StringBuilder sb = new StringBuilder();
        if (x >= 0 && x < height && y >= 0 && y < width && localGrid[x][y] == 1) {
            sb.append(offsetX).append(offsetY);
            localGrid[x][y] = 0;
            for (int[] dir : dirs) sb.append(dfs(x + dir[0], y + dir[1], offsetX + dir[0], offsetY + dir[1]));
        }
        return sb;
    }

}
```

#### 结果
![number-of-distinct-islands-2](/images/leetcode/number-of-distinct-islands-2.png)
