---
layout: post
title: "Leetcode - Algorithm - Arithmetic Slices "
date: 2017-12-15 19:54:50
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","math"]
level: "medium"
description: >
---

### 题目
A sequence of number is called arithmetic if it consists of at least three elements and if the difference between any two consecutive elements is the same.

For example, these are arithmetic sequence:
```
1, 3, 5, 7, 9
7, 7, 7, 7
3, -1, -5, -9
```

The following sequence is not arithmetic.
```
1, 1, 2, 5, 7
```

A zero-indexed array A consisting of N numbers is given. A slice of that array is any pair of integers `(P, Q)` such that `0 <= P < Q < N`.

A slice `(P, Q)` of array `A` is called arithmetic if the sequence:
`A[P], A[p + 1], ..., A[Q - 1], A[Q]` is arithmetic. In particular, this means that `P + 1 < Q`.

The function should return the number of arithmetic slices in the array `A`.


### 数学原理，$$O(n)$$
先把相邻的元素间的差都计算出来，然后可以得到一系列的连续等差窗口，
```
1,2,3,4,5,10,12,15,18,21    // 原数组
  1,1,1,1, 5, 2, 3, 3, 3    // 相邻元素差
|   1   | 5|2|    3    |    // 等差窗口
```

然后对于一个任意大小的等差窗口，能构成的`Arithmetic Slices`的数量是可以计算的，
```
1,2,3,4,5

等差窗口 = 5

Arithmetic Slices = 3 + 2 + 1 = 6

1,2,3,4,5,6

等差窗口 = 6

Arithmetic Slices = 4 + 3 + 2 + 1 = 10
```

根据上面的规律，代码如下。复杂度$$O(n)$$。

#### 代码
```java
class Solution {
    public int numberOfArithmeticSlices(int[] A) {
        if (A.length < 3) { return 0; }
        for (int i = 1, pre = A[0]; i < A.length; i++) {
            int diff = A[i] - pre;
            pre = A[i];
            A[i] = diff;
        }
        int cur = 1, res = 0;
        while (cur < A.length) {
            int first = A[cur];
            int begin = (cur++);
            while (cur < A.length && A[cur] == first) { cur++; }
            res += sub(cur - begin + 1);
        }
        return res;
    }
    private int sub(int size) {
        if (size < 3) { return 0; }
        int max = size - 3 + 1;
        int sum = 0;
        while (max > 0) {
            sum += (max--);
        }
        return sum;
    }
}
```

#### 结果
![arithmetic-slices-1](/images/leetcode/arithmetic-slices-1.png)

### 数学法的简化版
原理不变，代码可以简化。下面的代码很聪明（不是我写的），参考: <https://discuss.leetcode.com/topic/63302/simple-java-solution-9-lines-2ms/2>

#### 代码
```java
public int numberOfArithmeticSlices(int[] A) {
    int curr = 0, sum = 0;
    for (int i=2; i<A.length; i++)
        if (A[i]-A[i-1] == A[i-1]-A[i-2]) {
            curr += 1;
            sum += curr;
        } else {
            curr = 0;
        }
    return sum;
}
```

#### 结果
![arithmetic-slices-3](/images/leetcode/arithmetic-slices-3.png)

### 动态规划
`Arithmetic Slices`的数量，可以用一个基于子问题的递归式表达，
> T(n) = T(n-1) + G(n)

其中，`G(n)`是从`A[n]`元素开始的连续等差数列的数量。这个`G(n)`很好计算。

还是得先计算出所有相邻元素的差值。

#### 代码
```java
class Solution {
    private static int[] diff = new int[0];

    public int numberOfArithmeticSlices(int[] A) {
        if (A.length < 3) { return 0; }
        for (int i = 1, pre = A[0]; i < A.length; i++) {
            int diff = A[i] - pre;
            pre = A[i];
            A[i] = diff;
        }
        diff = A;
        return dp(1);
    }
    private int dp(int cur) {
        if (cur == diff.length - 1) { return 0; }
        int sub = dp(cur+1);
        int begin = cur, targetDiff = diff[begin];
        while (cur < diff.length && diff[cur] == targetDiff) { cur++; }
        return sub + (cur - begin - 1);
    }
}
```

#### 结果
![arithmetic-slices-2](/images/leetcode/arithmetic-slices-2.png)
