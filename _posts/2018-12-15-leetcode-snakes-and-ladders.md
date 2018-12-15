---
layout: post
title: "Leetcode - Algorithm - Snakes And Ladders "
date: 2018-12-15 02:07:09
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["breadth first search"]
level: "medium"
description: >
---

### 题目
On an N x N board, the numbers from 1 to N*N are written boustrophedonically starting from the bottom left of the board, and alternating direction each row.  For example, for a 6 x 6 board, the numbers are written as follows:
![snakes-and-ladders-figure](/images/leetcode/snakes-and-ladders-figure.png)
You start on square 1 of the board (which is always in the last row and first column).  Each move, starting from square x, consists of the following:
* You choose a destination square S with number x+1, x+2, x+3, x+4, x+5, or x+6, provided this number is <= N*N.
(This choice simulates the result of a standard 6-sided die roll: ie., there are always at most 6 destinations.)
* If S has a snake or ladder, you move to the destination of that snake or ladder.  Otherwise, you move to S.

A board square on row r and column c has a "snake or ladder" if board[r][c] != -1.  The destination of that snake or ladder is board[r][c].

Note that you only take a snake or ladder at most once per move: if the destination to a snake or ladder is the start of another snake or ladder, you do not continue moving.  (For example, if the board is `[[4,-1],[-1,3]]`, and on the first move your destination square is `2`, then you finish your first move at `3`, because you do not continue moving to `4`.)

Return the least number of moves required to reach square N*N.  If it is not possible, return -1.

Example 1:
```
Input: [
[-1,-1,-1,-1,-1,-1],
[-1,-1,-1,-1,-1,-1],
[-1,-1,-1,-1,-1,-1],
[-1,35,-1,-1,13,-1],
[-1,-1,-1,-1,-1,-1],
[-1,15,-1,-1,-1,-1]]
Output: 4
Explanation:
At the beginning, you start at square 1 [at row 5, column 0].
You decide to move to square 2, and must take the ladder to square 15.
You then decide to move to square 17 (row 3, column 5), and must take the snake to square 13.
You then decide to move to square 14, and must take the ladder to square 35.
You then decide to move to square 36, ending the game.
It can be shown that you need at least 4 moves to reach the N*N-th square, so the answer is 4.
```

Note:
* 2 <= board.length = board[0].length <= 20
* board[i][j] is between 1 and N*N or is equal to -1.
* The board square with number 1 has no snake or ladder.
* The board square with number N*N has no snake or ladder.

### BFS
拿例子来说，第一步从`1`开始，走一步可以到的位置有：
```
[1] -> [2,3,4,5,6,7]

其中[2]跳到了[15]，所以，如果下列数字中出现了目标数字，就结束了，

[1] -> [2,3,4,5,6,7,15]

下一步因为[2]跳到了[15]，所以不可能从[2]开始，因此，下一步的起点集合为：
[3,4,5,6,7,15] -> [... ...]

以此类推
```

需要注意死循环的情况，比如下面例子，`[2,3,4,5,6,7]`全部会跳回到`[1]`，因此永远走不出去。
```
 1,  1, -1
 1,  1,  1
-1,  1,  1
```

为了解决这个问题，设置一个`HashSet`，专门储存已访问过的起点。在选择下一轮新起点的时候过滤掉这些已经访问过的点。出现死循环的情况，代码很快就能终止。

#### 代码
```java
class Solution {
    public int snakesAndLadders(int[][] board) {
        int size = board.length;
        int target = size * size;
        Map<Integer, Integer> skipMap = skipMap(board);
        List<Integer> list = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        list.add(1);
        visited.add(1);
        int step = 0;
        while (!list.isEmpty()) {
            ++step;
            int len = list.size();
            for (int i = 0; i < len; i++) {
                int start = list.remove(0);
                for (int dice = 1; dice <= 6; dice++) {
                    int to = start + dice;
                    if (to > target) break;
                    if (to == target) return step;
                    if (skipMap.containsKey(to)) {
                        int skipTarget = skipMap.get(to);
                        if (skipTarget == target) return step;
                        if (visited.add(skipTarget)) list.add(skipTarget);
                    } else if (visited.add(to)) {
                        list.add(to);
                    }
                }
            }
        }
        return -1;
    }

    private Map<Integer, Integer> skipMap(int[][] board) {
        Map<Integer, Integer> table = new HashMap<>();
        for (int row = board.length - 1, col = 0, dir = 1, count = 1; row >= 0; row--) {
            while (col >= 0 && col < board.length) {
                if (board[row][col] != -1) table.put(count, board[row][col]);
                col += dir;
                count++;
            }
            dir = -dir;
            col += dir;
        }
        return table;
    }
}
```

#### 结果
![snakes-and-ladders-1](/images/leetcode/snakes-and-ladders-1.png)
