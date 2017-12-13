---
layout: post
title: "Leetcode - Algorithm - Image Smoother "
date: 2017-12-12 19:47:56
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given a 2D integer matrix M representing the gray scale of an image, you need to design a smoother to make the gray scale of each cell becomes the average gray scale (rounding down) of all the 8 surrounding cells and itself. If a cell has less than 8 surrounding cells, then use as many as you can.

Example 1:
```
Input:
[[1,1,1],
 [1,0,1],
 [1,1,1]]
Output:
[[0, 0, 0],
 [0, 0, 0],
 [0, 0, 0]]
Explanation:
For the point (0,0), (0,2), (2,0), (2,2): floor(3/4) = floor(0.75) = 0
For the point (0,1), (1,0), (1,2), (2,1): floor(5/6) = floor(0.83333333) = 0
For the point (1,1): floor(8/9) = floor(0.88888889) = 0
```

Note:
* The value in the given matrix is in the range of [0, 255].
* The length and width of the given matrix are in the range of [1, 150].


### 朴素遍历二维数组，使用额外一个二维数组空间

#### 代码
```java
class Solution {
    private int height = 0, width = 0;

    public int[][] imageSmoother(int[][] M) {
        height = M.length;
        if (height == 0) { return M; }
        width = M[0].length;
        int[][] res = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int count = 0, sum = 0;
                for (int x = i-1; x <= i+1; x++) {
                    for (int y = j-1; y <= j+1; y++) {
                        if (isValid(x,y)) {
                            count++;
                            sum += M[x][y];
                        }
                    }
                }
                res[i][j] = sum / count;
            }
        }
        return res;
    }
    private boolean isValid(int x, int y) {
        return (x >= 0) && (x < height) && (y >= 0) && (y < width);
    }
}
```

#### 结果
![image-smoother-1](/images/leetcode/image-smoother-1.png)


### 在原数组上修改，只使用2个额外一维数组作为缓存

#### 代码
```java
class Solution {
    private int[][] local = null;
    private int height = 0, width = 0;

    public int[][] imageSmoother(int[][] M) {
        height = M.length;
        if (height == 0) { return M; }
        local = M;
        width = local[0].length;
        int[] pre = null, curr = null, temp = null;
        int copyIndex = 0, scanIndex = 0;
        while (scanIndex < height) {
            if (curr == null) {
                curr = new int[width];
            } else {
                copy(local[copyIndex++],curr);
            }
            for (int i = 0; i < width; i++) {
                curr[i] = smooth(scanIndex,i);
            }
            temp = curr;
            curr = pre;
            pre = temp;
            scanIndex++;
        }
        if (curr != null) { copy(local[copyIndex++],curr); }
        if (pre != null) { copy(local[copyIndex++],pre); }
        return local;
    }
    private void copy(int[] target, int[] resource) {
        for (int i = 0; i < target.length; i++) {
            target[i] = resource[i];
        }
    }
    private int smooth(int row, int col) {
        int[] pair = collectRow(row,col);   // collect mid line
        int count = pair[0], sum = pair[1];
        if (row > 0) {                      // if not the first line, collect first line
            pair = collectRow(row-1,col);
            count += pair[0];
            sum += pair[1];
        }
        if (row < height - 1) {             // if not the last line, collect last line
            pair = collectRow(row+1,col);
            count += pair[0];
            sum += pair[1];
        }
        return sum / count;
    }
    private int[] collectRow(int row, int col) {
        int count = 1, sum = local[row][col];
        if (col > 0) {              // not first column
            count++;
            sum += local[row][col-1];
        }
        if (col < width - 1) {      // not the last column
            count++;
            sum += local[row][col+1];
        }
        return new int[]{count,sum};
    }
}
```

#### 结果
![image-smoother-2](/images/leetcode/image-smoother-2.png)
