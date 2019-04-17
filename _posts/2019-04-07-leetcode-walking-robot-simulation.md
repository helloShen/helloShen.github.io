---
layout: post
title: "Leetcode - Algorithm - Walking Robot Simulation"
date: 2019-04-07 17:18:55
author: "Wei SHEN"
categories: ["algorithm", "leetcode"]
tags: ["binary search"]
level: "easy"
description: >
---

### 题目
A robot on an infinite grid starts at point (0, 0) and faces north.  The robot can receive one of three possible types of commands:

* `-2`: turn left 90 degrees
* `-1`: turn right 90 degrees
* `1 <= x <= 9`: move forward x units

Some of the grid squares are obstacles.

The `i-th` obstacle is at grid point (`obstacles[i][0]`, `obstacles[i][1]`)

If the robot would try to move onto them, the robot stays on the previous grid square instead (but still continues following the rest of the route.)

Return the square of the maximum Euclidean distance that the robot will be from the origin.

Example 1:
```
Input: commands = [4,-1,3], obstacles = []
Output: 25
Explanation: robot will go to (3, 4)
```

Example 2:
```
Input: commands = [4,-1,4,-2,4], obstacles = [[2,4]]
Output: 65
Explanation: robot will be stuck at (1, 4) before turning left and going to (1, 8)
```

Note:
* 0 <= commands.length <= 10000
* 0 <= obstacles.length <= 10000
* -30000 <= `obstacle[i][0]` <= 30000
* -30000 <= `obstacle[i][1]` <= 30000
* The answer is guaranteed to be less than `2 ^ 31`.

### 二分查找
把障碍的坐标按行和列分别统计出来，储存在`Map<Integer, List<Integer>>`里。“键”是行号，或列号。“值”是这一行或列上所有的障碍。

每走一步，到`Map`里去查障碍即可。

这题有个坑：就是测试里会出现`[0,0]`是障碍的情况。这样定义不严谨。所以只要遇到从`[0,0]`出发的情况，如果`[0,0]`是障碍，忽略这个障碍就可以了。

#### 代码
```java
class Solution {
    public int robotSim(int[] commands, int[][] obstacles) {
        int dir = 0;
        int x = 0, y = 0;
        int nextX = 0, nextY = 0;
        Map<Integer, List<Integer>> xMap = new HashMap<>();
        Map<Integer, List<Integer>> yMap = new HashMap<>();
        parseObstacles(obstacles, xMap, yMap);
        int maxDis = 0;
        for (int command : commands) {
            switch (command) {
                case -2:
                    dir = turnLeft(dir);
                    break;
                case -1:
                    dir = turnRight(dir);
                    break;
                default:
                    nextX = x + command * moves[dir][0];
                    nextY = y + command * moves[dir][1];
                    if (moves[dir][0] == 0) {
                        Integer stopAt = findObstacle(xMap, x, y, nextY);
                        if (stopAt == null) {
                            x = nextX;
                            y = nextY;
                        } else {
                            y = stopAt;
                        }
                    } else {
                        Integer stopAt = findObstacle(yMap, y, x, nextX);
                        if (stopAt == null) {
                            x = nextX;
                            y = nextY;
                        } else {
                            x = stopAt;
                        }
                    }
                    maxDis = Math.max(maxDis, x * x + y * y);
                    break;

            }
        }
        return maxDis;
    }

    private static final int[][] moves = new int[][]{{0,1},{1,0},{0,-1},{-1,0}};

    private void parseObstacles(int[][] obstacles, Map<Integer, List<Integer>> xMap, Map<Integer, List<Integer>> yMap) {
        for (int[] obstacle : obstacles) {
            if (!xMap.containsKey(obstacle[0])) {
                xMap.put(obstacle[0], new ArrayList<Integer>());
            }
            xMap.get(obstacle[0]).add(obstacle[1]);
            if (!yMap.containsKey(obstacle[1])) {
                yMap.put(obstacle[1], new ArrayList<Integer>());
            }
            yMap.get(obstacle[1]).add(obstacle[0]);
        }
        for(Map.Entry<Integer, List<Integer>> entry : xMap.entrySet()) {
           Collections.sort(entry.getValue());
        }
        for(Map.Entry<Integer, List<Integer>> entry : yMap.entrySet()) {
           Collections.sort(entry.getValue());
        }
    }

    private int turnLeft(int dir) {
        dir--;
        return (dir == -1)? 3 : dir;
    }

    private int turnRight(int dir) {
        dir++;
        return (dir == 4)? 0 : dir;
    }

    // if has no obstacle, return null
    private Integer findObstacle(Map<Integer, List<Integer>> obstaclesMap, int fixPos, int from, int to) {
        if (obstaclesMap.containsKey(fixPos)) {
            List<Integer> obstaclesOnThisRow = obstaclesMap.get(fixPos);
            int findIdx = Collections.binarySearch(obstaclesOnThisRow, from);
            int idxFrom = - (findIdx + 1);
            if (fixPos == 0 && from == 0 && findIdx >= 0) return null; // origin point [0,0] can be an obstacle
            if (from <= to) {
                int firstObsIdx = idxFrom;
                if (firstObsIdx < obstaclesOnThisRow.size()) {
                    int firstObs = obstaclesOnThisRow.get(firstObsIdx);
                    if (firstObs <= to) {
                        return firstObs - 1;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                int firstObsIdx = idxFrom - 1;
                if (firstObsIdx >= 0) {
                    int firstObs = obstaclesOnThisRow.get(firstObsIdx);
                    if (firstObs >= to) {
                        return firstObs + 1;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }
}
```

#### 结果
![walking-robot-simulation-1](/images/leetcode/walking-robot-simulation-1.png)
