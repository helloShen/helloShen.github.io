---
layout: post
title: "Leetcode - Algorithm - Diagonal Traverse "
date: 2017-12-13 16:26:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given a matrix of M x N elements (M rows, N columns), return all elements of the matrix in diagonal order as shown in the below image.

Example:
```
Input:
[
 [ 1, 2, 3 ],
 [ 4, 5, 6 ],
 [ 7, 8, 9 ]
]
Output:  [1,2,4,7,5,3,6,8,9]
Explanation:
![diagonal-traverse](/images/leetcode/diagonal-traverse.png)
```

Note:
* The total number of elements of the given matrix will not exceed 10,000.

### 分为`up()`和`down()`两个动作，递归法
最基本的思路，就是分为`up()`和`down()`两个动作。然后关键点就是要处理好超出二维数组边界以后的转向问题。

#### 代码
```java
class Solution {
    private static int[][] local = null;
    private static int[] res = null;
    private static int cur = 0;
    private static int height = 0, width = 0;

    public int[] findDiagonalOrder(int[][] matrix) {
        if (matrix.length == 0) { return new int[0]; }
        init(matrix);
        up(0,0);
        return res;
    }
    private void init(int[][] matrix) {
        local = matrix;
        height = matrix.length;
        width = matrix[0].length;
        res = new int[height * width];
        cur = 0;
    }
    private void up(int x, int y) {
        if (y >= width) {
            down(x+2,y-1); return;
        } else if (x < 0) {
            down(x+1,y); return;
        }
        res[cur++] = local[x][y];
        if (x != height-1 || y != width-1) {
            up(x-1,y+1); return;
        }
    }
    private void down(int x, int y) {
        if (x >= height) {
            up(x-1,y+2); return;
        } else if (y < 0) {
            up(x,y+1); return;
        }
        res[cur++] = local[x][y];
        if (x != height-1 || y != width-1) {
            down(x+1,y-1); return;
        }
    }
}
```

#### 结果
递归代码更简洁，但这题内存不够用。
![diagonal-traverse-1](/images/leetcode/diagonal-traverse-1.png)

### 还是`up()`和`down()`两个动作，迭代版
同样的事情完全可以用迭代完成。

#### 代码
```java
class Solution {

    private static int[][] local = null;
    private static int[] res = null;
    private static int cur = 0;
    private static int height = 0, width = 0;
    private static boolean up = false;

    public int[] findDiagonalOrder(int[][] matrix) {
        if (matrix.length == 0) { return new int[0]; }
        init(matrix);
        parse(0,0);
        return res;
    }
    private void init(int[][] matrix) {
        local = matrix;
        height = matrix.length;
        width = matrix[0].length;
        res = new int[height * width];
        cur = 0;
        up = true;
    }
    private void parse(int x, int y) {
        while (true) {
            if (up) {
                if (y >= width) {
                    x += 2; y -= 1;
                    up = false; continue;
                } else if (x < 0) {
                    x += 1;
                    up = false; continue;
                }
                res[cur++] = local[x][y];
                if (x == height-1 && y == width-1) { return; }
                x--; y++;
            } else {
                if (x >= height) {
                    x -= 1; y += 2;
                    up = true; continue;
                } else if (y < 0) {
                    y += 1;
                    up = true; continue;
                }
                res[cur++] = local[x][y];
                if (x == height-1 && y == width-1) { return; }
                x++; y--;
            }
        }
    }
}
```

#### 结果
![diagonal-traverse-2](/images/leetcode/diagonal-traverse-2.png)

### 把`up()`和`down()`归并成一个动作
这里的4个边界条件完全不冲突，互相独立。所以可以不需要分成`up()`和`down()`两个动作。只需要设置一个`direction`参数控制遍历的方向即可。

#### 代码
```java
class Solution {
    public int[] findDiagonalOrder(int[][] matrix) {
        if (matrix == null || matrix.length == 0) { return new int[0]; }
        int height = matrix.length;
        int width = matrix[0].length;
        int[] res = new int[height * width];
        int cur = 0;
        int direction = -1; // up: -1, down: 1
        int x = 0, y = 0;
        while (true) {
            if (y >= width) { x += 2; y -= 1; direction = -direction; continue; }
            if (x >= height) { x -= 1; y += 2; direction = -direction; continue; }
            if (x < 0) { x += 1; direction = -direction; continue; }
            if (y < 0) { y += 1; direction = -direction; continue; }
            res[cur++] = matrix[x][y];
            if (x == height-1 && y == width-1) { return res; }
            x += direction; y -= direction;
        }
    }
}
```

#### 结果
![diagonal-traverse-3](/images/leetcode/diagonal-traverse-3.png)
