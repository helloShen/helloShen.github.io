---
layout: post
title: "Leetcode - Algorithm - Triangle "
date: 2017-05-15 01:08:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","dynamic programming"]
level: "medium"
description: >
---

### 题目
Given a triangle, find the minimum path sum from top to bottom. Each step you may move to adjacent numbers on the row below.

For example, given the following triangle
```
[
     [2],
    [3,4],
   [6,5,7],
  [4,1,8,3]
]
```
The minimum path sum from top to bottom is 11 (i.e., 2 + 3 + 5 + 1 = 11).

Note:
Bonus point if you are able to do this using only `O(n)` extra space, where n is the total number of rows in the triangle.

### 暴力回溯
时间复杂度 $$O(2^n)$$, 空间复杂度$$O(1)$$。

#### 代码
```java
public class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        return dp(triangle,0,0);
    }
    // depth=[0,triangle.size()-1]
    public int dp(List<List<Integer>> triangle, int depth, int index) {
        if (depth == triangle.size()) { return 0; }
        int num = triangle.get(depth).get(index);
        int left = dp(triangle,depth+1,index);
        int right = dp(triangle,depth+1,index+1);
        return num + Math.min(left,right);
    }
}
```

#### 结果
![triangle-1](/images/leetcode/triangle-1.png)


### 自底向上的动态规划，复杂度$$O(n^2)$$
先把所有元素值抄到`int[][]`二维数组中。时间复杂度$$O(n^2)$$，`n=depth`。空间复杂度$$O(n^2)$$，`n=depth`。

#### 代码
```java
public class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int size = triangle.size();
        int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j <= i; j++) {
                matrix[i][j] = triangle.get(i).get(j);
            }
        }
        for (int i = size-2; i >= 0; i--) {
            for (int j = i; j >= 0; j--) {
                matrix[i][j] = matrix[i][j] + Math.min(matrix[i+1][j],matrix[i+1][j+1]);
            }
        }
        return matrix[0][0];
    }
}
```

#### 结果
![triangle-2](/images/leetcode/triangle-2.png)


### 空间复杂度 $$O(n)$$ 的动态规划
使用一个`int[n]`的数组做备忘录，`n=depth`。

#### 代码
```java
public class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int size = triangle.size();
        int[] memo = new int[size];
        for (int i = 0; i < size; i++) {
            memo[i] = triangle.get(size-1).get(i);
        }
        for (int i = size-2; i >= 0; i--) { // depth
            for (int j = 0; j <= i; j++) { // breadth (必须从小到大，这样可以直接写入，不影响下一步计算)
                memo[j] = triangle.get(i).get(j) + Math.min(memo[j],memo[j+1]);
            }
            System.out.println(Arrays.toString(memo));
        }
        return memo[0];
    }
}
```

#### 结果
空间是节省了，但每次都从`List<List<Integer>>`里随机访问，效率低。
![triangle-3](/images/leetcode/triangle-3.png)
