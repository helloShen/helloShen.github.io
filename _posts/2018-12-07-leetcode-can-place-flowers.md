---
layout: post
title: "Leetcode - Algorithm - Can Place Flowers "
date: 2018-12-07 21:34:56
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Suppose you have a long flowerbed in which some of the plots are planted and some are not. However, flowers cannot be planted in adjacent plots - they would compete for water and both would die.

Given a flowerbed (represented as an array containing 0 and 1, where 0 means empty and 1 means not empty), and a number n, return if n new flowers can be planted in it without violating the no-adjacent-flowers rule.

Example 1:
```
Input: flowerbed = [1,0,0,0,1], n = 1
Output: True
```

Example 2:
```
Input: flowerbed = [1,0,0,0,1], n = 2
Output: False
```

Note:
* The input array won't violate no-adjacent-flowers rule.
* The input array size is in the range of [1, 20000].
* n is a non-negative integer which won't exceed the input array size.

### 窗口法
没什么花样，最直观的贪心策略即可。假设`[1,0,0,0,1]`，我们需要找出每一段连续的`0`，
```
 [lo     hi)
   |<--->|
[1,0,0,0,1]
```

可以发现，中间可以插入的花，对于固定长度的连续`0`串是一定的，
```
0       -> 0
00      -> 0
000     -> 1
0000    -> 1
00000   -> 2
000000  -> 2
0000000 -> 3
...
...
```

数学公式很简单，
> flower(numZero) = (numZero - 1) / 2

一个特例是，当这段`0`靠边时（头部或尾部），这个数字要加一。
> if (head or tail) >> flower(numZero) = (numZero - 1) / 2 + 1

除此之外，还有一个特例是当花圃的长度本身为`1`的时候，如果是`[0]`，最多能种1，如果为`[1]`，就一朵也种不了。

最后一个特例是当`n == 0`时，无论如何结果都为`true`。

#### 代码
```java
class Solution {
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        int size = flowerbed.length;
        if (size == 1) return flowerbed[0] == 0 || n == 0;
        int lo = 0, hi = 0;
        while (lo < size && n > 0) {
            while (lo < size && flowerbed[lo] == 1) lo++;
            hi = lo;
            while (hi < size && flowerbed[hi] == 0) hi++;
            if (lo < size) n -= plantForWindow(lo, hi , size);
            lo = hi;
        }
        return n <= 0;
    }

    /** window range = [lo, hi) */
    private int plantForWindow(int lo, int hi, int size) {
        if (hi - lo == 1) return 0;
        int half = (hi - lo - 1) / 2;
        return (lo == 0 || hi == size)? half + 1 : half;
    }
}
```

#### 结果
![can-place-flowers-1](/images/leetcode/can-place-flowers-1.png)
