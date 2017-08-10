---
layout: post
title: "Leetcode - Algorithm - Counting Bits "
date: 2017-08-09 21:02:13
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","bit manipulation"]
level: "medium"
description: >
---

### 题目
Given a non negative integer number num. For every numbers i in the range 0 ≤ i ≤ num calculate the number of 1's in their binary representation and return them as an array.

Example:
For num = `5` you should return `[0,1,1,2,1,2]`.

Follow up:

It is very easy to come up with a solution with run time `O(n*sizeof(integer))`. But can you do it in linear time `O(n)` /possibly in a single pass?
Space complexity should be `O(n)`.
Can you do it like a boss? Do it without using any builtin function like `__builtin_popcount` in c++ or in any other language.

### 朴素的位操作计算1位数
唯一的技巧已经说过很多遍了，用`n & n-1`来逐个清楚末尾的`1`。

#### 代码
```java
public class Solution {
    public int[] countBits(int num) {
        int[] res = new int[num+1];
        for (int i = 0; i <= num; i++) {
            res[i] = count(i);
        }
        return res;
    }
    private int count(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n-1); // magic
            ++count;
        }
        return count;
    }
}
```

#### 结果
![counting-bits-1](/images/leetcode/counting-bits-1.png)


### 动态规划，复杂度 $$O(n)$$
`base case`是`0`，
```
0000    // 0
```
`1`在`0`的基础上加一。
```
0001    // 1
```
然后，`[2,3]`在`[0,1]`的基础上各加一。
```
0000    // 0
0001    // 1

这一位变1
  |
0010    // 2
0011    // 3
```
然后，`[4,5,6,7]`在`[0,1,2,3]`的基础上各加一。
```
0000    // 0
0001    // 1
0010    // 2
0011    // 3

这一位变1
 |
0100    // 4
0101    // 5
0110    // 6
0111    // 7
```
所以是个标准的动态规划问题。


#### 代码
这题直接用数组来做，比递归要简单。
```java
public class Solution {
    public int[] countBits(int num) {
        int[] res = new int[num+1];
        res[0] = -1;
        int start = 0, size = 1, slow = 0, fast = 0;
        while (true) {
            while (size-- > 0) {
                if (fast > num) { return res; }
                res[fast++] = res[slow++] + 1;
            }
            start = fast;
            size = fast;
            slow = 0;
        }
    }
}
```

#### 结果
![counting-bits-2](/images/leetcode/counting-bits-2.png)


### 抽象出问题的本质 `f[i] = f[i / 2] + i % 2`
这个公式说清楚了问题的本质。

#### 代码
```java
public class Solution {
    public int[] countBits(int num) {
        int[] nums = new int[num+1];
        for (int i = 1; i <= num; i++) { nums[i] = nums[i/2] + i%2; }
        return nums;
    }
}
```

#### 结果
![counting-bits-3](/images/leetcode/counting-bits-3.png)
