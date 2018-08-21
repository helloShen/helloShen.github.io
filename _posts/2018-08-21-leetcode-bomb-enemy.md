---
layout: post
title: "Leetcode - Algorithm - Bomb Enemy "
date: 2018-08-21 17:04:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamique programming"]
level: "medium"
description: >
---

### 题目
Given a 2D grid, each cell is either a wall 'W', an enemy 'E' or empty '0' (the number zero), return the maximum enemies you can kill using one bomb.
The bomb kills all the enemies in the same row and column from the planted point until it hits the wall since the wall is too strong to be destroyed.
Note: You can only put the bomb at an empty cell.

Example:
```
Input: [["0","E","0","0"],["E","0","W","E"],["0","E","0","0"]]
Output: 3
Explanation: For the given grid,

0 E 0 0
E 0 W E
0 E 0 0
```
Placing a bomb at (1,1) kills 3 enemies.

### 直观暴力遍历数组，O(n^3)
就是逐个点检查上下左右。

![bomb-enemy-a](/images/leetcode/bomb-enemy-a.png)

#### 代码
```java
public class Solution {
    public int maxKilledEnemies(char[][] grid) {
         if (grid == null || grid.length == 0) { return 0; }
         localGrid = grid;
         int max = 0;
         for (int i = 0; i < localGrid.length; i++) {
             for (int j = 0; j < localGrid[0].length; j++) {
                 if (localGrid[i][j] == '0') {
                     max = Math.max(max,count(i,j));
                 }
             }
         }
         return max;
     }

     /** ================= 【以下为私有成员】 ================== */
     private char[][] localGrid;
     private int up(int row, int col) {
         int count = 0;
         for (int i = row - 1; i >= 0; i--) {
             switch (localGrid[i][col]) {
                 case 'W':
                     return count;
                 case 'E':
                     count++; break;
             }
         }
         return count;
     }
     private int down(int row, int col) {
         int count = 0;
         for (int i = row + 1; i < localGrid.length; i++) {
             switch (localGrid[i][col]) {
                 case 'W':
                     return count;
                 case 'E':
                     count++; break;
             }
         }
         return count;
     }
     private int left(int row, int col) {
         int count = 0;
         for (int i = col - 1; i >= 0; i--) {
             switch (localGrid[row][i]) {
                 case 'W':
                     return count;
                 case 'E':
                     count++; break;
             }
         }
         return count;
     }
     private int right(int row, int col) {
         int count = 0;
         for (int i = col + 1; i < localGrid[0].length; i++) {
             switch (localGrid[row][i]) {
                 case 'W':
                     return count;
                 case 'E':
                     count++; break;
             }
         }
         return count;
     }
     private int count(int row, int col) {
         return up(row, col) + down(row, col) + left(row, col) + right(row, col);
     }
 }
```

#### 结果
![bomb-enemy-1](/images/leetcode/bomb-enemy-1.png)


### 动态规划
每一格统计2个值，
* 到目前为止横向遇到多少个敌人
* 到目前为止纵向遇到多少个敌人

![bomb-enemy-b](/images/leetcode/bomb-enemy-b.png)

从左到右，从上往下遍历二维数组，
* 遇到空格：照抄左边相邻格的横向敌人数，照抄上边相邻格的纵向敌人数。
* 遇到敌人：在左边相邻格横向敌人数基础上加一，上边相邻格的纵向敌人数也加一。
* 遇到墙：横向，纵向敌人数都归零。

遍历完之后，靠右，靠左边的值比较大，这时候返回去，从右到左，从下往上将较大的值拷贝到左边和上边的格子。最后在每个空格处，将横向和纵向敌人数相加，就是我们要的结果。


#### 代码
```java
public class Solution {
    public int maxKilledEnemies(char[][] grid) {
        if (grid == null || grid.length == 0) { return 0; }
        dp(grid);
        backPropagation(grid);
        return collect(grid);
    }


    /** =========================== 【以下私有】 ============================== */
    private int[][][] dpTab;

    //从右往左，从上往下构建动态规划表
    //局部最大值靠右，靠下
    private void dp(char[][] grid) {
        dpTab = new int[grid.length+1][grid[0].length+1][2];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                switch (grid[i][j]) {
                    case 'W':
                        dpTab[i+1][j+1][0] = 0;
                        dpTab[i+1][j+1][1] = 0;
                        break;
                    case 'E':
                        dpTab[i+1][j+1][0] = dpTab[i+1][j][0] + 1;
                        dpTab[i+1][j+1][1] = dpTab[i][j+1][1] + 1;
                        break;
                    case '0':
                        dpTab[i+1][j+1][0] = dpTab[i+1][j][0];
                        dpTab[i+1][j+1][1] = dpTab[i][j+1][1];
                        break;
                    default: return;
                }
            }
        }
    }
    //反向传播
    //从右往左，从下往上将局部最大值更新到整张表
    private void backPropagation(char[][] grid) {
        //horizontal
        for (int i = 0; i < grid.length; i++) {
            for (int j = grid[0].length - 2; j >= 0; ) {
                //skip walls
                while (j >= 0 && (grid[i][j] == 'W' || grid[i][j+1] == 'W')) { j--; }
                //back propagation
                while (j >= 0 && (grid[i][j] != 'W' && grid[i][j+1] != 'W')) {
                    dpTab[i+1][j+1][0] = dpTab[i+1][j+2][0];
                    j--;
                }
            }
        }
        //vertical
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length - 2; j >= 0; ) {
                //skip walls
                while (j >= 0 && (grid[j][i] == 'W' || grid[j+1][i] == 'W')) { j--; }
                //back propagation
                while (j >= 0 && (grid[j][i] != 'W' && grid[j+1][i] != 'W')) {
                    dpTab[j+1][i+1][1] = dpTab[j+2][i+1][1];
                    j--;
                }
            }
        }
    }
    //对计算好的动态规划表做全局统计
    private int collect(char[][] grid) {
        int max = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '0') {
                    max = Math.max(max,dpTab[i+1][j+1][0] + dpTab[i+1][j+1][1]);
                }
            }
        }
        return max;
    }
}
```

#### 结果
![bomb-enemy-2](/images/leetcode/bomb-enemy-2.png)


### One Pass DP
完全一样是DP算法，下面的代码只需要遍历一次二维数组。

#### 代码
```java
class Solution {

        public int maxKilledEnemies(char[][] grid) {
            if (grid == null || grid.length == 0) { return 0; }
            int max = 0;
            int killRow = 0;
            int[] killCol = new int[grid[0].length];
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    if (grid[i][j] == 'W') { continue; }
                    if (j == 0 || grid[i][j-1] == 'W') {
                        killRow = left(i,j,grid);
                    }
                    if (i == 0 || grid[i-1][j] == 'W') {
                        killCol[j] = down(i,j,grid);
                    }
                    if (grid[i][j] == '0') {
                        max = Math.max(max,killRow + killCol[j]);
                    }
                }
            }
            return max;
        }
        private int left(int row, int col, char[][] grid) {
            int count = 0;
            for (int i = col; i < grid[0].length && grid[row][i] != 'W'; i++) {
                if (grid[row][i] == 'E') {
                    count++;
                }
            }
            return count;
        }
        private int down(int row, int col, char[][] grid) {
            int count = 0;
            for (int i = row; i < grid.length && grid[i][col] != 'W'; i++) {
                if (grid[i][col] == 'E') {
                    count++;
                }
            }
            return count;
        }

}
```

参考：【[YunHu](https://leetcode.com/problems/bomb-enemy/discuss/83383/Simple-DP-solution-in-Java)】

#### 结果
![bomb-enemy-3](/images/leetcode/bomb-enemy-3.png)
