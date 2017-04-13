---
layout: post
title: "Leetcode - Algorithm - Pow "
date: 2017-04-13 18:09:01
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: ""
description: >
---

### 题目
Implement pow(x, n).

注：这题主要考察的是分治法的运用。不是为了考察处理像`Double.NaN`,`Double.POSITIVE_INFINITY`,`Double.NEGATIVE_INFINITY`,`0.0`,`-0.0`这样的极端情况的处理。所以第二个参数幂`n`是`int`型，并且测试数据集中没有刁难以上的极端值。否则根据`Math.pow()`函数的规约（[Math.pow()](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#pow-double-double-)，情况复杂地多。

### 递归分治 $$O(\log_{}{n})$$
终结条件是`n`等于`0`,`1`,`-1`的情况，可以直接返回对应结果。其他情况都是递归让`n`减半。

#### 代码
```java
public class Solution {
    public double myPow(double x, int n) {
        if (n == 0) { return 1.0; }
        if (n == 1) { return x; }
        if (n == -1) { return 1/x; }
        if (n > 0) {
            return (n % 2 == 0)? myPow(x*x,n/2) : x * myPow(x*x,n/2);
        } else { // n<0
            return (n % 2 == 0)? myPow(x*x,n/2) : (1/x) * myPow(x*x,n/2);
        }
    }
}
```

#### 简化代码
把终结条件设为`0`,`1`,`-1`是为了减少一层递归，提高效率。如果单纯为了简化逻辑和代码的话，终结条件可以只考虑`n=0`的情况。但这种情况，效率就会受影响。

```java
public class Solution {
    public double myPow(double x, int n) {
        if (n == 0) { return 1.0; }
        double prefix = (n > 0)? x : 1/x;
        return (n % 2 == 0)? myPow(x*x,n/2) : prefix * myPow(x*x,n/2);
    }
}
```

#### 结果
以`0`,`1`,`-1`为终结条件的结果。
![pow-1](/images/leetcode/pow-1.png)

以`0`为终结条件的结果。
![pow-3](/images/leetcode/pow-3.png)


### 和库函数`Math.pow()`比较

#### 代码
直接调用`Math.pow()`.
```java
public double myPow(double x, int n) {
    return Math.pow(x,(double)n);
}
```

#### 结果
应该说和我们的$$O(\log_{}{n})$$的算法效率差不多。
![pow-2](/images/leetcode/pow-2.png)
