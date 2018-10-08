---
layout: post
title: "Leetcode - Algorithm - Flood Fill "
date: 2018-10-08 16:28:37
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "depth first search"]
level: "easy"
description: >
---

### 题目
An image is represented by a 2-D array of integers, each integer representing the pixel value of the image (from 0 to 65535).

Given a coordinate (sr, sc) representing the starting pixel (row and column) of the flood fill, and a pixel value newColor, "flood fill" the image.

To perform a "flood fill", consider the starting pixel, plus any pixels connected 4-directionally to the starting pixel of the same color as the starting pixel, plus any pixels connected 4-directionally to those pixels (also with the same color as the starting pixel), and so on. Replace the color of all of the aforementioned pixels with the newColor.

At the end, return the modified image.

Example 1:
```
Input:
image = [[1,1,1],[1,1,0],[1,0,1]]
sr = 1, sc = 1, newColor = 2
Output: [[2,2,2],[2,2,0],[2,0,1]]
Explanation:
From the center of the image (with position (sr, sc) = (1, 1)), all pixels connected
by a path of the same color as the starting pixel are colored with the new color.
Note the bottom corner is not colored 2, because it is not 4-directionally connected
to the starting pixel.
```

Note:
* The length of image and `image[0]` will be in the range `[1, 50]`.
* The given starting pixel will satisfy `0 <= sr < image.length` and `0 <= sc < image[0]`.length.
* The value of each color in `image[i][j]` and newColor will be an integer in `[0, 65535]`.


### DFS (Depth-First-Search)
DFS最简单的做法就是递归。每个`floodFillHelper()`函数可以递归出“前-后-左-右”4个子问题。数组越界是递归的终止条件。

#### 代码
```java
class Solution {
    public int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
        height = image.length;
        if (height == 0) return image;
        width = image[0].length;
        oldC = image[sr][sc];
        if (oldC == newColor) return image;
        newC = newColor;
        imageRef = image;
        floodFillHelper(sr, sc);
        return image;
    }

    private int oldC, newC;
    private int height, width;
    private int[][] imageRef;

    private void floodFillHelper(int sr, int sc) {
        if (sr < 0 || sr >= height || sc < 0 || sc >= width) return;
        if (imageRef[sr][sc] == oldC) {
            imageRef[sr][sc] = newC;
            floodFillHelper(sr - 1, sc);
            floodFillHelper(sr + 1, sc);
            floodFillHelper(sr, sc - 1);
            floodFillHelper(sr, sc + 1);
        }
    }
}
```

#### 结果
![flood-fill-1](/images/leetcode/flood-fill-1.png)
