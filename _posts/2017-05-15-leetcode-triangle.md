---
layout: post
title: "Leetcode - Algorithm - Triangle "
date: 2017-05-15 01:08:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
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
使用一个`int[n]`的数组做备忘录，`n=depth`，外加`2`个临时寄存器。因为写入备忘录必须延迟一步，否则影响下一个值的计算。

#### 代码
```java
public class Solution {
    public int minimumTotal(List<List<Integer>> triangle) {
        int size = triangle.size();
        int[] memo = new int[size];
        for (int i = 0; i < size; i++) {
            memo[i] = triangle.get(size-1).get(i);
        }
        int register = 0;
        for (int i = size-2; i >= 0; i--) { // depth
            for (int j = i; j >= 0; j--) { // breadth
                int num = triangle.get(i).get(j);
                int temp = num + Math.min(memo[j],memo[j+1]);
                if (j < i) { memo[j+1] = register; } // 延迟一步写入。每次写入上次寄存器记录的内容。
                register = temp; // 不能马上写入备忘录，先寄存起来。
            }
            memo[0] = register;
            System.out.println(Arrays.toString(memo));
        }
        return memo[0];
    }
}
```

#### 结果
时间换空间，效率低了一点。
![triangle-3](/images/leetcode/triangle-3.png)
