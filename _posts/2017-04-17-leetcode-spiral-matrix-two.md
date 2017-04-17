---
layout: post
title: "Leetcode - Algorithm - Spiral Matrix Two "
date: 2017-04-17 15:13:38
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given an integer n, generate a square matrix filled with elements from 1 to n2 in spiral order.

For example,
Given n = 3,

You should return the following matrix:
```
[
 [ 1, 2, 3 ],
 [ 8, 9, 4 ],
 [ 7, 6, 5 ]
]
```

### 剥洋葱
以`[1,2,3,4,5,6,7,8,9]`为例，
```
 [ 1 2 3 ]
 [ 8 9 4 ]   
 [ 7 6 5 ]
```
分为两个洋葱圈，
```
 [ 1 2 3 ]
 [ 8   4 ]   +    [9]
 [ 7 6 5 ]
```
然后分别记住左边界`left`,右边界`right`,上边界`high`,下边界`low`。然后把一个圈分成下面四部分填写，
![spiral-matrix](/images/leetcode/spiral-matrix.png)

#### 代码
```java
public class Solution {
    public int[][] generateMatrix(int n) {
        int[][] res = new int[n][n];
        int left = 0, right = n-1, high = 0, low = n-1;
        int next = 0;
        for (; n > 0; n-=2, left++, right--, high++, low--) {
            if (right == left && low == high) { res[left][high] = ++next; }
            for (int i = left; i < right; i++) {
                res[high][i] = ++next;
            }
            for (int i = high; i < low; i++) {
                res[i][right] = ++next;
            }
            for (int i = right; i > left; i--) {
                res[low][i] = ++next;
            }
            for (int i = low; i > high; i--) {
                res[i][left] = ++next;
            }
        }
        return res;
    }
}
```

#### 结果
![spiral-matrix-two-1](/images/leetcode/spiral-matrix-two-1.png)


### 其他填写法
这题比较开放，可以有很多种填写方法。比如还看到下面这种方法，从里往外填。每次都填写上面一行，然后顺时针旋转矩阵。
```
    ||  =>  |9|  =>  |8|      |6 7|      |4 5|      |1 2 3|
                     |9|  =>  |9 8|  =>  |9 6|  =>  |8 9 4|
                                         |8 7|      |7 6 5|
```
但实际上旋转矩阵的开销不小，这种方法只是作为一种参考。
