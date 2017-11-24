---
layout: post
title: "Leetcode - Algorithm - Moving Average "
date: 2017-11-24 18:19:06
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given a stream of integers and a window size, calculate the moving average of all integers in the sliding window.

For example,
```
MovingAverage m = new MovingAverage(3);
m.next(1) = 1
m.next(10) = (1 + 10) / 2
m.next(3) = (1 + 10 + 3) / 3
m.next(5) = (10 + 3 + 5) / 3
```

### 用一个数组表示窗口
用一个指针指向下一个要输入的位置，可以循环利用数组槽位。

#### 代码
```java
class MovingAverage {

    private static int size = 0;
    private static int[] windows = new int[0];
    private static double sum = 0.0;
    private static int next = 0;

    /** Initialize your data structure here. */
    public MovingAverage(int size) {
        this.size = size;
        windows = new int[size];
        sum = 0;
        next = 0;
    }

    public double next(int val) {
        int offset = next % size;
        sum -= windows[offset];
        sum += val;
        windows[offset] = val;
        next++;
        return (next > size)? (sum / size) : (sum / next);
    }
}

/**
 * Your MovingAverage object will be instantiated and called as such:
 * MovingAverage obj = new MovingAverage(size);
 * double param_1 = obj.next(val);
 */
```

#### 结果
![moving-average-1](/images/leetcode/moving-average-1.png)
