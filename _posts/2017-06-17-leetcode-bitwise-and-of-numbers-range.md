---
layout: post
title: "Leetcode - Algorithm - Bitwise And Of Numbers Range "
date: 2017-06-17 03:43:21
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation"]
level: "medium"
description: >
---

### 题目
Given a range [m, n] where 0 <= m <= n <= 2147483647, return the bitwise AND of all numbers in this range, inclusive.

For example, given the range [5, 7], you should return 4.

### 思路
首先保底算法是老老实实一个个数字做`&`位运算。保底复杂度是线性的 $$O(m-n+1)$$ 。

但考虑到[m,n]区间内的数字是连续的，所以有可能有 $$O(1)$$ 的捷径，只要能归纳出数学规律。

这里的数学规律是这样的：

如下所示，假设`n`的最高位比`m`的最高位高，
```
n =     0010 1010
m =     0000 0101

则，m和n之间至少会有一个k，只保留n的最高一个1，其余全部归零，

k =     0010 0000
m =     0000 0101   &
---------------------
        0000 0000

所以m的任何信息都留不下，而且n的最高的那个1也留不下，
n =     0010 1010
m =     0000 0101   &
---------------------
          0   <- n最高位的1留不下来
```

什么信息能留下来？
> m和n的最高位的1相同，那这些开头的1能保留下来。

```
        这两个1能保留下来
          ||
n =     0011 1010
m =     0011 0101
             |
            后面的数，也全都留不下来

因为，m和n之间所有的数都有这两个1.
后面的数都保留不下来，原因和最开始最高位不同的1留不下来的原因相同。
```

### 老老实实，用掩码一位一位切下来比较

#### 代码
```java
public class Solution {
    public int rangeBitwiseAnd(int m, int n) {
        int ret = 0;
        int mask = 1 << 30;
        for (int i = 1; i < 32; i++) {
            int mBit = m & mask;
            int nBit = n & mask;
            if ((mBit == 0 && nBit == mask) || (mBit == mask && nBit == 0)) { return ret; }
            if (mBit == mask && nBit == mask) { ret |= mask; } // 开头的连续的1
            mask >>>= 1;
        }
        return ret;
    }
}
```

#### 结果
![bitwise-and-of-numbers-range-1](/images/leetcode/bitwise-and-of-numbers-range-1.png)


### 先全部往右推出去，再往左推回来
看下面的例子，
```
n =     0011 1010
m =     0011 0101
```
`m`和`n`同时往右推到相等为止，记录下推了几位，
```
m == n,     step = 4

n =     0011
m =     0011
```
再把`m`和`n`往左推同样的位数，这时候 **只保留下** 最高位的相同的`1`。
```
n =     0011 0000
m =     0011 0000
```

#### 代码
```java
public class Solution {
    public int rangeBitwiseAnd(int m, int n) {
        int step = 0;
        while (m != n) {
            m >>= 1;
            n >>= 1;
            step++;
        }
        return n << step;
    }
}
```

#### 结果
![bitwise-and-of-numbers-range-2](/images/leetcode/bitwise-and-of-numbers-range-2.png)


### 一种奇技淫巧：Brian Kernighan's Algorithm
还是下面的例子，
```
n =     0011 1010
m =     0011 0101
```
他的思路是把`n`的后4位挖空。

#### 代码
```java
public class Solution {
    public int rangeBitwiseAnd(int m, int n) {
        while (n > m) {
            n &= (n-1);
        }
        return m & n;
    }
}
```

#### 结果
![bitwise-and-of-numbers-range-3](/images/leetcode/bitwise-and-of-numbers-range-3.png)
