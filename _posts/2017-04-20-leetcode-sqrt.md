---
layout: post
title: "Leetcode - Algorithm - Sqrt "
date: 2017-04-20 18:10:15
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","math"]
level: "easy"
description: >
---

### 题目
Implement int sqrt(int x).

Compute and return the square root of x.

### 二分查找 $$O(\log_{}{n})$$
只能用平方查找，没找到就范围减半。比如目标数`10000`，
```
5001 * 5001 > 10000
2501 * 2501 > 10000
1251 * 1251 > 10000
...
...
100 * 100 = 10000
```

#### 迭代版
```java
public class Solution {
    public int mySqrt(int x) {
        if (x <= 0) { return 0; }
        int lo = 1, hi = x;
        while (lo < hi) {
            int mid = lo + ( (hi - lo + 1) / 2 ); // 注意！取上位中位数
            long product = (long)mid * mid;
            if (product > (long)x) {
                hi = mid - 1;
            } else if (product < (long)x) {
                lo = mid;
            } else { // product == x
                return mid;
            }
        }
        return lo;
    }
}
```

#### 递归版
```java
public class Solution {
    public int mySqrt(int x) {
        if (x <= 0) { return 0; }
        return recursive(x,1,x);
    }
    public int recursive(int x, int lo, int hi) {
        if (lo == hi) { return lo; }
        int mid = lo + ( (hi - lo + 1) / 2);
        long product = (long)mid * mid;
        if (product > (long)x) {
            return recursive(x,lo,mid-1);
        } else if (product < (long)x) {
            return recursive(x,mid,hi);
        } else {
            return mid;
        }
    }
}
```

#### 结果
银弹！
![sqrt-1](/images/leetcode/sqrt-1.png)
