---
layout: post
title: "Leetcode - Algorithm - Maximum Length Of Repeated Subarray "
date: 2019-01-23 00:52:27
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
Given two integer arrays A and B, return the maximum length of an subarray that appears in both arrays.

Example 1:
```
Input:
A: [1,2,3,2,1]
B: [3,2,1,4,7]
Output: 3
Explanation:
The repeated subarray with maximum length is [3, 2, 1].
```

Note:
* 1 <= len(A), len(B) <= 1000
* 0 <= A[i], B[i] < 100

### 暴力遍历所有组合
暴力解的复杂度也只有`O(MN)`，其中`M`是数组`A[]`的长度，`N`是数组`B[]`的长度。

#### 代码
```java
class Solution {
    public int findLength(int[] A, int[] B) {
        int maxWindow = 0;
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B.length; j++) {
                if (A[i] == B[j]) {
                    int window = 1;
                    for (int k = i + 1, l= j + 1; k < A.length && l < B.length && A[k] == B[l]; k++, l++) window++;
                    maxWindow = Math.max(maxWindow, window);
                }
            }
        }
        return maxWindow;
    }
}
```

#### 结果
![maximum-length-of-repeated-subarray-1](/images/leetcode/maximum-length-of-repeated-subarray-1.png)


### 动态规划
还是看例子，
```
A: [1,2,3,2,1]
B: [3,2,1,4,7]
```
可以目测出最长相等子串应该是`[3,2,1]`，我们可以错开数组看问题，
```
1,2  <- A取前两位
     <- B目前为空


 对上1位
    |
1,2,3  <-A再往后取一位
    3  <-B再往后取一位

  对上2位
    | |
1,2,3,2  <-A再往后取一位
    3,2  <-B再往后取一位

    对上3位
    | | |
1,2,3,2,1  <-A再往后取一位
    3,2,1  <-B再往后取一位
```

因此，用`i`和`j`两个指针，如果`A[i] == B[j]`，则`dp[i][j] = dp[i - 1][j - 1] + 1`，否则`dp[i][j] = 0`。
```
         i
         |
[... ...]1,2,3

         j
         |
[... ...]1,3,2
```

#### 代码
```java
class Solution {
    public int findLength(int[] A, int[] B) {
        int sizeA = A.length;
        int sizeB = B.length;
        int[][] dp = new int[sizeA + 1][sizeB + 1];
        int maxWindow = 0;
        for (int i = 1; i <= sizeA; i++) {
            for (int j = 1; j <= sizeB; j++) {
                dp[i][j] = (A[i - 1] == B[j - 1])? dp[i - 1][j - 1] + 1 : 0;
                maxWindow = Math.max(maxWindow, dp[i][j]);
            }
        }
        return maxWindow;
    }
}
```

#### 结果
![maximum-length-of-repeated-subarray-2](/images/leetcode/maximum-length-of-repeated-subarray-2.png)
