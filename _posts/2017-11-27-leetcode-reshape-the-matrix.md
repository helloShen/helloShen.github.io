---
layout: post
title: "Leetcode - Algorithm - Reshape The Matrix "
date: 2017-11-27 19:32:24
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
In MATLAB, there is a very useful function called 'reshape', which can reshape a matrix into a new one with different size but keep its original data.

You're given a matrix represented by a two-dimensional array, and two positive integers r and c representing the row number and column number of the wanted reshaped matrix, respectively.

The reshaped matrix need to be filled with all the elements of the original matrix in the same row-traversing order as they were.

If the 'reshape' operation with given parameters is possible and legal, output the new reshaped matrix; Otherwise, output the original matrix.

Example 1:
```
Input:
nums =
[[1,2],
 [3,4]]
r = 1, c = 4
Output:
[[1,2,3,4]]
Explanation:
The row-traversing of nums is [1,2,3,4]. The new reshaped matrix is a 1 * 4 matrix, fill it row by row by using the previous list.
```

Example 2:
```
Input:
nums =
[[1,2],
 [3,4]]
r = 2, c = 4
Output:
[[1,2],
 [3,4]]
Explanation:
There is no way to reshape a 2 * 2 matrix to a 2 * 4 matrix. So output the original matrix.
```

Note:
* The height and width of the given matrix is in range [1, 100].
* The given r and c are all positive.

### 直接转码坐标

#### 代码
```java
class Solution {
    public int[][] matrixReshape(int[][] nums, int r, int c) {
        int height = nums.length;
        if (height == 0) { return nums; }
        int width = nums[0].length;
        if (width == 0 || (height * width != r * c)) { return nums; }
        int[][] res = new int[r][c];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int offset = i * width + j; // 总位移
                int newR = offset / c;      // 转码纵坐标
                int newC = offset % c;      // 转码横坐标
                res[newR][newC] = nums[i][j];
            }
        }
        return res;
    }
}
```

#### 结果
![reshape-the-matrix-1](/images/leetcode/reshape-the-matrix-1.png)
