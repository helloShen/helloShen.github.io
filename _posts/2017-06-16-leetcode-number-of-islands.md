---
layout: post
title: "Leetcode - Algorithm - Number Of Islands "
date: 2017-06-16 22:13:25
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["union find","depth first search"]
level: "medium"
description: >
---

### 题目
Given a 2d grid map of `1`s (land) and `0`s (water), count the number of islands. An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.

Example 1:
```
11110
11010
11000
00000
```
Answer: `1`

Example 2:
```
11000
11000
00100
00011
```
Answer: `3`

### 总体思路
看到联通性问题，第一反应就是`Union Find`结构。但需要记住：
> Union Find 最优方案：加权的quick-union，再加上路径压缩，效率到不了线性复杂度 $$O(n)$$。

但这里深度优先的DFS探索的平均复杂度可以做到 $$O(n)$$。虽然DFS从递归的角度看上去复杂度很高，但其实总体来看每个点都访问一次。


### 标准 `Union Find` 解法，复杂度 $$O(n\log_{}{n})$$
下面的`Sea`类，就是一个标准的`Union Find`数据结构。拥有`find()`,`union()`,`create()`,`size()`这些核心接口。所有标为`0`的water都不算联通。

#### 代码
```java
/**
 * Class Sea is a Union Find Data Structure
 */
public class Solution {
    public int numIslands(char[][] grid) {
        if (grid.length == 0) { return 0; }
        int height = grid.length, width = grid[0].length;
        int size = height * width;
        Sea sea = new Sea(size+1);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grid[i][j] == '1') {
                    int pos = i * width + j + 1;
                    sea.createIsland(pos);
                    if (i > 0 && grid[i-1][j] == '1') { sea.union(pos,pos-width); } // upper
                    if (j >0 && grid[i][j-1] == '1') { sea.union(pos,pos-1); } // left
                }
            }
        }
        return sea.size();
    }
    /**
     * Union Find Data Structure
     */
    private class Sea {
        private int[] sea;
        private int numIslands;
        /**
         * water is initialized by zero.
         * all zeroes(water) are not connected to each other.
         */
        public Sea(int size) {
            sea = new int[size];
        }
        /**
         * create a new island
         * x is the id of the new island
         */
        public void createIsland(int x) {
            sea[x] = x;
            numIslands++;
        }
        /**
         * find and return the id of island x
         */
        public int findId(int x) {
            if (sea[x] == x) { return x; } // find id
            sea[x] = findId(sea[x]); // path compression
            return sea[x];
        }
        /**
         * union two exist islands x and y
         * use the id of islands y as the id of new combined island
         */
        public void union(int x, int y) {
            int idY = findId(y);
            int idX = findId(x);
            if (idY != idX) {
                sea[idX] = idY;
                numIslands--;
            }
        }
        /**
         * return the number of isolated islands
         */
         public int size() {
             return numIslands;
         }
    }
}
```

#### 结果
![number-of-islands-1](/images/leetcode/number-of-islands-1.png)


### 为了提高效率，把`Union Find`內建在方法里
只留一个递归的`find()`函数在外面。

#### 代码
```java
/**
 * Do not use Sea class
 * Build-In Union Find Structure in numIslands() method
 */
public class Solution {
    public int numIslands(char[][] grid) {
        if (grid.length == 0) { return 0; }
        int height = grid.length, width = grid[0].length;
        int size = height * width;
        int[] sea = new int[size+1];
        int numIslands = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grid[i][j] == '1') {
                    int pos = i * width + j + 1;
                    if (i > 0 && grid[i-1][j] == '1') {  // merged with upper island
                        sea[pos] = findId(sea,pos-width);
                    } else { // upper is 0
                        sea[pos] = pos;
                        numIslands++;
                    }
                    if (j >0 && grid[i][j-1] == '1') { // merge with left island
                        int idCurr = findId(sea,pos);
                        int idLeft = findId(sea,pos-1);
                        if (idCurr != idLeft) {
                            sea[idCurr] = idLeft;
                            numIslands--;
                        }
                    }
                }
            }
        }
        return numIslands;
    }
    public int findId(int[] sea, int pos) {
        if (sea[pos] == pos) { return pos; }
        sea[pos] = findId(sea,sea[pos]);
        return sea[pos];
    }
}
```

#### 结果
结果好了一点，但离银弹`3ms`还有点距离。
![number-of-islands-2](/images/leetcode/number-of-islands-2.png)


### DFS探索
问题的关键在于：
> Union Find 最优方案：加权的quick-union，再加上路径压缩，效率到不了线性复杂度 $$O(n)$$。

但这里深度优先的DFS探索的复杂度是 $$O(n)$$。

深度优先的递归探索，需要把探索到的`1`临时标记成其他标志。最后如果需要，可以再遍历一遍把`grid`回复原样。

#### 代码
```java
/**
 * DFS探索整个岛。
 */
public class Solution {
     public int numIslands(char[][] grid) {
         int count = 0;
         if (grid.length == 0) { return count; }
         for (int i = 0; i < grid.length; i++) {
             for (int j = 0; j < grid[0].length; j++) {
                 if (grid[i][j] == '1') {
                     count++;
                     dfs(grid,i,j);
                 }
             }
         }
         return count;
     }
     /**
      * 以[x,y]点为起点，DFS探索整个岛。探索到的点，临时标记为'x'。
      * 如果不能改动原来的grid，最后可以再遍历一遍，把所有'x'改回'1'。
      */
     public void dfs(char[][] grid, int x, int y) {
         // base case
         if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length) { return; } // 越界
         char c = grid[x][y];
         if (c == '0' || c == 'x') { return; }
         // assert: grid[x][y] = '1'
         grid[x][y] = 'x';
         dfs(grid,x-1,y);
         dfs(grid,x+1,y);
         dfs(grid,x,y-1);
         dfs(grid,x,y+1);
     }
}
```

#### 结果
![number-of-islands-3](/images/leetcode/number-of-islands-3.png)
