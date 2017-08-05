---
layout: post
title: "Leetcode - Algorithm - Hamming Distance "
date: 2017-08-04 13:57:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation"]
level: "easy"
description: >
---

### 题目
The Hamming distance between two integers is the number of positions at which the corresponding bits are different.

Given two integers x and y, calculate the Hamming distance.

Note:
0 ≤ x, y < 231.

Example:

Input: x = 1, y = 4

Output: 2

Explanation:
```
1   (0 0 0 1)
4   (0 1 0 0)
       ?   ?
```

The above arrows point to positions where the corresponding bits are different.

### 利用`^`抵消相同项
留下来的就是不同的位。接下里只需要计算位的个数。Java有个库函数`Integer.bitCount()`可以做这件事。

想自己计算`1`位的数量，可以用`n&(n-1)`这个黑魔法。关于位操作的总结可以参考这篇文章， <http://www.ciaoshen.com/algorithm/2017/08/03/explanation-of-some-tricks-of-bit-manipulation.html>

#### 代码
```java
public class Solution {
    public int hammingDistance(int x, int y) {
        int mix = x ^ y;
        int count = 0;
        while (mix != 0) {
            mix = mix & (mix - 1);
            ++count;
        }
        return count;
    }
}
```

#### 用`Integer.bitCount()`
```java
public class Solution {
    public int hammingDistance(int x, int y) {
        return (Integer.bitCount(x ^ y));
    }
}
```

#### 结果
![hamming-distance-1](/images/leetcode/hamming-distance-1.png)
