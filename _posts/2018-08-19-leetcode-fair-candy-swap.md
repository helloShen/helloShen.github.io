---
layout: post
title: "Leetcode - Algorithm - Fair Candy Swap "
date: 2018-08-19 01:08:07
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Alice and Bob have candy bars of different sizes: A[i] is the size of the i-th bar of candy that Alice has, and B[j] is the size of the j-th bar of candy that Bob has.

Since they are friends, they would like to exchange one candy bar each so that after the exchange, they both have the same total amount of candy.  (The total amount of candy a person has is the sum of the sizes of candy bars they have.)

Return an integer array ans where ans[0] is the size of the candy bar that Alice must exchange, and ans[1] is the size of the candy bar that Bob must exchange.

If there are multiple answers, you may return any one of them.  It is guaranteed an answer exists.



Example 1:
```
Input: A = [1,1], B = [2,2]
Output: [1,2]
Example 2:

Input: A = [1,2], B = [2,3]
Output: [1,2]
Example 3:

Input: A = [2], B = [1,3]
Output: [2,3]
Example 4:

Input: A = [1,2,5], B = [2,4]
Output: [5,4]
```

Note:
* 1 <= A.length <= 10000
* 1 <= B.length <= 10000
* 1 <= A[i] <= 100000
* 1 <= B[i] <= 100000
* It is guaranteed that Alice and Bob have different total amounts of candy.
* It is guaranteed there exists an answer.


### 遍历数组
分两步走，
1. 遍历数组，找出Alice和Bob糖果数量的差`diff`
2. 将这个差除以2: `diff/2`。找到差值正好等于`diff/2`的两颗糖果

#### 代码
```java
class Solution {
    public int[] fairCandySwap(int[] A, int[] B) {
        int sumA = 0;
        for (int a : A) {
           sumA += a;  
        }
        int sumB = 0;
        for (int b : B) {
            sumB += b;
        }
        int diff = sumA - sumB;
        int target = diff / 2;
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B.length; j++) {
                if (A[i] - B[j] == target) {
                    return new int[]{A[i], B[j]};
                }
            }
        }
        return null;
    }
}
```
