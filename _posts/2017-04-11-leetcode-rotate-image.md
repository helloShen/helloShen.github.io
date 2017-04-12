---
layout: post
title: "Leetcode - Algorithm - Rotate Image "
date: 2017-04-11 18:46:28
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: "medium"
description: >
---

### 题目
You are given an n x n 2D matrix representing an image.

Rotate the image by 90 degrees (clockwise).

Follow up:
Could you do this in-place?

### 剥洋葱
以一个`4*4`的二维数组为例，
```
1	2	3	4

5	6	7	8

9	10	11	12

13	14	15	16
```
分为两个圈，
```
1	2	3	4

5			8               6	7
                    +
9			12              10	11

13	14	15	16
```
把这两个圈分别装入两个`LinkedList`。然后把末尾一定量的元素插入头部。再写出来就变成，下面这个样子，
```
13	9	5	1

14	10	6	2

15	11	7	3

16	12	8	4
```

#### 代码
```java
public class Solution {
    public void rotate(int[][] matrix) {
        int n = matrix.length;
        int[][] result = new int[n][n];
        for (int i = 0; n-i > i; i++) {
            LinkedList<Integer> list = new LinkedList<>();
            for (int j = i; j < n - i - 1; j++) { list.add(matrix[i][j]); }
            for (int j = i; j < n - i - 1; j++) { list.add(matrix[j][n-i-1]); }
            for (int j = n - i - 1; j > i; j--) { list.add(matrix[n-i-1][j]); }
            for (int j = n - i - 1; j > i; j--) { list.add(matrix[j][i]); }
            int distance = n - 2 * i - 1;
            for (int j = 0; j < distance; j++) { list.offerFirst(list.pollLast()); }
            for (int j = i; j < n - i - 1; j++) { result[i][j] = list.poll(); }
            for (int j = i; j < n - i - 1; j++) { result[j][n-i-1] = list.poll(); }
            for (int j = n - i - 1; j > i; j--) { result[n-i-1][j] = list.poll(); }
            for (int j = n - i - 1; j > i; j--) { result[j][i] = list.poll(); }
        }
        if (matrix.length % 2 == 1) {
            int mid = matrix.length / 2;
            result[mid][mid] = matrix[mid][mid];
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                matrix[i][j] = result[i][j];
            }
        }
    }
}
```

#### 结果
还有优化空间。
![rotate-image-1](/images/leetcode/rotate-image-1.png)


### 用数组替代`LinkedList`
最初版只是为了简化问题，专注于数组的边界值，才用了`LinkedList`。最好的当然是用数组，而且不用变动元素位置，只要用个指针指出起始位置即可。

#### 代码
```java
public class Solution {
    public void rotate(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; n-i > i; i++) {
            int[] circle = new int[(n-i*2-1)*4];
            int cursor = 0;
            for (int j = i; j < n - i - 1; j++) { circle[cursor++] = matrix[i][j]; }
            for (int j = i; j < n - i - 1; j++) { circle[cursor++] = matrix[j][n-i-1]; }
            for (int j = n - i - 1; j > i; j--) { circle[cursor++] = matrix[n-i-1][j]; }
            for (int j = n - i - 1; j > i; j--) { circle[cursor++] = matrix[j][i]; }
            int distance = n - 2 * i - 1;
            int start = circle.length - distance;
            for (int j = i; j < n - i - 1; j++) { matrix[i][j] = circle[start++ % circle.length]; }
            for (int j = i; j < n - i - 1; j++) { matrix[j][n-i-1] = circle[start++ % circle.length]; }
            for (int j = n - i - 1; j > i; j--) { matrix[n-i-1][j] = circle[start++ % circle.length]; }
            for (int j = n - i - 1; j > i; j--) { matrix[j][i] = circle[start++ % circle.length]; }
        }
    }
}
```

#### 结果
属于主流。
![rotate-image-2](/images/leetcode/rotate-image-2.png)


### 上下，左右翻转数组
先上下翻转，再沿左倾对角线翻转。这个方法更容易理解。
```
1 2 3     7 8 9     7 4 1
4 5 6  => 4 5 6  => 8 5 2
7 8 9     1 2 3     9 6 3
```

#### 代码
```java
public class Solution {
    public void rotate(int[][] matrix) {
        for (int i = 0; i <= (matrix.length - 1) / 2; i++) {
            int[] temp = Arrays.copyOf(matrix[i],matrix.length); // 上下翻转
            matrix[i] = matrix[matrix.length-1-i];
            matrix[matrix.length-1-i] = temp;
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (i > j) {
                    int temp = matrix[i][j]; // 沿左倾对角线翻转
                    matrix[i][j] = matrix[j][i];
                    matrix[j][i] = temp;
                }
            }
        }
    }
}
```

#### 结果
![rotate-image-3](/images/leetcode/rotate-image-3.png)
