---
layout: post
title: "Leetcode - Algorithm - Toeplitz Matrix "
date: 2018-10-06 18:37:37
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
A matrix is Toeplitz if every diagonal from top-left to bottom-right has the same element.

Now given an M x N matrix, return True if and only if the matrix is Toeplitz.

Example 1:
```
Input:
matrix = [
  [1,2,3,4],
  [5,1,2,3],
  [9,5,1,2]
]
Output: True
Explanation:
In the above grid, the diagonals are:
"[9]", "[5, 5]", "[1, 1, 1]", "[2, 2, 2]", "[3, 3]", "[4]".
In each diagonal all elements are the same, so the answer is True.
```

Example 2:
```
Input:
matrix = [
  [1,2],
  [2,2]
]
Output: False
Explanation:
The diagonal "[1, 2]" has different elements.
```

Note:
* matrix will be a 2D array of integers.
* matrix will have a number of rows and columns in range `[1, 20]`.
* `matrix[i][j]` will be integers in range `[0, 99]`.

Follow up:
* What if the matrix is stored on disk, and the memory is limited such that you can only load at most one row of the matrix into the memory at once?
* What if the matrix is so large that you can only load up a partial row into the memory at once?

### 检查向右下角的一条直线
如图所示，以第一列，以及第一行的每个元素为起始，沿着右下角的斜线检查。相邻右下角的元素坐标为`[x + 1][y + 1]`。
![toeplitz-matrix-a](/images/leetcode/toeplitz-matrix-a.png)


#### 代码
```java
class Solution {
    public boolean isToeplitzMatrix(int[][] matrix) {
        int height = matrix.length;
        int width = matrix[0].length;
        for (int i = height - 1; i >= 0; i--) {
            int num = matrix[i][0];
            int x = i, y = 0;
            while (x + 1 < height && y + 1 < width) {
                if (matrix[x + 1][y + 1] != num) return false;
                x++; y++;
            }
        }
        for (int i = 0; i < width; i++) {
            int num = matrix[0][i];
            int x = 0, y = i;
            while (y + 1 < width && x + 1 < height) {
                if (matrix[x + 1][y + 1] != num) return false;
                y++; x++;
            }
        }
        return true;
    }
}
```

#### 结果
![toeplitz-matrix-1](/images/leetcode/toeplitz-matrix-1.png)


### 利用递归思想简化代码
对矩阵中每个元素，只比较和他直接相邻的右下角元素。

#### 代码
```java
class Solution {
    public boolean isToeplitzMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length - 1; i++) {
            for (int j = 0; j < matrix[0].length - 1; j++) {
                if (matrix[i][j] != matrix[i + 1][j + 1]) return false;
            }
        }
        return true;
    }
}
```

#### 结果
![toeplitz-matrix-2](/images/leetcode/toeplitz-matrix-2.png)


### 如果内存有限怎么办？
依照上面的第二种方法，最节省的方法，每次只需要加载2个元素即可。
