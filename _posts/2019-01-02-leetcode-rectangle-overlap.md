---
layout: post
title: "Leetcode - Algorithm - Rectangle Overlap "
date: 2019-01-02 22:25:02
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
A rectangle is represented as a list `[x1, y1, x2, y2]`, where (x1, y1) are the coordinates of its bottom-left corner, and (x2, y2) are the coordinates of its top-right corner.

Two rectangles overlap if the area of their intersection is positive.  To be clear, two rectangles that only touch at the corner or edges do not overlap.

Given two (axis-aligned) rectangles, return whether they overlap.

Example 1:
```
Input: rec1 = [0,0,2,2], rec2 = [1,1,3,3]
Output: true
```

Example 2:
```
Input: rec1 = [0,0,1,1], rec2 = [1,0,2,1]
Output: false
```

Notes:
* Both rectangles rec1 and rec2 are lists of 4 integers.
* All coordinates in rectangles will be between -10^9 and 10^9.


### 复杂问题分解出来看
如果是2维长方形不太好看出来规律，先看1维，即只看x轴或y轴，即两条线段重叠的情况，
```
a1      a2
|-------|
    |--------|
    b3       b4
```

这样好像也不太容易看出来，那就举反例，两条线段错开的情况如下，只有两种情况
```
第一种情况，a在b左边：必须满足 a2 < b1
a1      a2  b1       b2
 |-------|  |--------|
   线段a       线段b

第二种情况，a在b右边：必须满足 b2 < a1
b1      b2  a1       a2
 |-------|  |--------|
   线段b       线段a
```

所以去掉这两个反例，如果a线段要和b线段重叠必须满足：
> a2 > b1 && a1 < b2

Y轴情况和X轴完全相同。

另外需要考虑一个边角情况，即某个长方形面积为零，也不能算重合。因此代码如下。

#### 代码
```java
public boolean isRectangleOverlap(int[] rec1, int[] rec2) {
    if (isZeroArea(rec1) || isZeroArea(rec2)) return false;
    return oneDOverlap(rec1[0], rec1[2], rec2[0], rec2[2]) && oneDOverlap(rec1[1], rec1[3], rec2[1], rec2[3]);
}

private boolean isZeroArea(int[] rec) {
    return rec[0] == rec[2] || rec[1] == rec[3];
}

private boolean oneDOverlap(int aLo, int aHi, int bLo, int bHi) {
    return aLo < bHi && aHi > bLo;
}
```

#### 结果
![rectangle-overlap-1](/images/leetcode/rectangle-overlap-1.png)
