---
layout: post
title: "Leetcode - Algorithm - Walls And Gates "
date: 2018-07-23 14:32:12
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
You are given a m x n 2D grid initialized with these three possible values.

* `-1` - A wall or an obstacle.
* `0` - A gate.
* `INF` - Infinity means an empty room. We use the value 2^31 - 1 = 2147483647 to represent INF as you may assume that the distance to a gate is less than 2147483647.

Fill each empty room with the distance to its nearest gate. If it is impossible to reach a gate, it should be filled with INF.

Example:

Given the 2D grid:
```
INF  -1  0  INF
INF INF INF  -1
INF  -1 INF  -1
  0  -1 INF INF
```
After running your function, the 2D grid should be:
```
  3  -1   0   1
  2   2   1  -1
  1  -1   2  -1
  0  -1   3   4
```

### 解法1
两步走：
1. 遍历每个位置，找门。
2. 找到门，从每个门的位置向前后左右四个位置，递归找房间，并更新到门的距离。

![walls-and-gates-2](/images/leetcode/walls-and-gates-2.png)

#### 代码
```java
class Solution {
        private static int localRooms[][] = new int[0][0];
        private static int height = 0, width = 0;
        public void wallsAndGates(int[][] rooms) {
            if (rooms.length == 0) { return; }
            localRooms = rooms;
            height = rooms.length;
            width = rooms[0].length;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (rooms[i][j] == 0) {
                        update(i-1, j, 1);
                        update(i+1, j, 1);
                        update(i, j-1, 1);
                        update(i, j+1, 1);
                    }
                }
            }
        }
        // y纵轴，x横轴
        private void update(int y, int x, int dis) {
            if (y >= 0 && y < height && x >= 0 && x < width) {
                if (localRooms[y][x] > 0 && localRooms[y][x] > dis) {
                    localRooms[y][x] = dis;
                    update(y+1, x, dis+1); // up
                    update(y-1, x, dis+1); // down
                    update(y, x-1, dis+1); // left
                    update(y, x+1, dis+1); // right
                }
            }
        }
}
```

#### 结果
![walls-and-gates-1](/images/leetcode/walls-and-gates-1.png)
