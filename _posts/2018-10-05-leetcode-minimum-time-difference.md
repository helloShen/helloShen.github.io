---
layout: post
title: "Leetcode - Algorithm - Minimum Time Difference "
date: 2018-10-05 17:11:52
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "medium"
description: >
---

### 题目
Given a list of 24-hour clock time points in "Hour:Minutes" format, find the minimum minutes difference between any two time points in the list.

Example 1:
```
Input: ["23:59","00:00"]
Output: 1
```

Note:
* The number of time points in the given list is at least 2 and won't exceed 20000.
* The input time is legal and ranges from 00:00 to 23:59.

### 问题分析
首先需要再明确一下问题的定义：
* 相同时间戳，比如`[00:00, 00:00]`间隔视为`0`.
* 时间戳的顺序不重要，`[00:00, 23:59]`等价于`[23:59, 00:00]`，时间间隔都为`1`.

最直观的解肯定是直接对字符串列表排序，然后计算两两之间的间隔。但题目给到的提示，字符串最多可能有20000个，而`00:00`~`23:59`最多只有`1440`个槽位。所以用一个`boolean[1440]`来统计时间戳效率更高。

### 类似“声呐”两头搜索
假设我们用`boolean[1440]`数组来统计时间戳。一种看问题的抽象是以单个数字为中心，“向前”或者“向后”搜索遇到的第一个元素，比如只有一个元素`[05:31]`，
```
 -----------------|<----------------    向后搜索
[..., ..., ..., 05:31, ..., ..., ...]
 ---------------->|-----------------    向前搜索
```
两个数字`["05:31","22:08"]`的情况，
```
 -----------------|           |<------  向后搜索
[..., ..., ..., 05:31, ..., 22:08, ...]
                  |---------->|         向前搜索
```

#### 代码
```java
class Solution {
    private static final int SIZE = 1440;
    private static final int HALF = 719;

    public int findMinDifference(List<String> timePoints) {
        boolean[] timeTable = new boolean[SIZE];
        for (String s : timePoints) {
            int time = timeToInt(s);
            if (timeTable[time]) return 0;
            timeTable[time] = true;
        }
        int minGap = Integer.MAX_VALUE;
        for (int i = 0; i < SIZE; i++) {
            if (timeTable[i]) {
                int forward = (i + 1) % SIZE;
                int backward = (i - 1);
                if (backward < 0) backward += SIZE;
                int gap = 1;
                while (gap < minGap && !timeTable[forward] && !timeTable[backward]) {
                    forward++; backward--;
                    if (forward >= SIZE) forward %= forward;
                    if (backward < 0) backward += SIZE;
                    gap++;
                }
                minGap = Math.min(minGap, gap);
            }
        }
        return minGap;
    }

    private int timeToInt(String time) {
        int hour = Integer.parseInt(time.substring(0, 2));
        int min = Integer.parseInt(time.substring(3, 5));
        return hour * 60 + min;
    }
}
```

#### 结果
![minimum-time-difference-1](/images/leetcode/minimum-time-difference-1.png)


### 直接计算所有时间戳两两之间的间隔
假设有`["05:31","22:08","00:35]`，具体情况如图所示，
```
         gap1                   gap2                    gap3
--->|<----------->|<------------------------------->|<-------
["00:35", ..., "05:31", ..., ..., ..., ..., ..., "22:08", ...]
```
实际计算的时候，可以遍历数组，先计算出`gap1`和`gap2`。最后再加上“首元素”和“尾元素”之间的`gap3`，形成一个闭环。

#### 代码
```java
class Solution {
    private static final int SIZE = 1440;

    public int findMinDifference(List<String> timePoints) {
        boolean[] timeTable = new boolean[SIZE];
        for (String s : timePoints) {
            int time = timeToInt(s);
            if (timeTable[time]) return 0;
            timeTable[time] = true;
        }
        int first = Integer.MAX_VALUE, last = Integer.MIN_VALUE;
        int minGap = Integer.MAX_VALUE;
        int pre = 0;
        for (int i = 0; i < SIZE; i++) {
            if (timeTable[i]) {
                if (first != Integer.MAX_VALUE) minGap = Math.min(minGap, i - pre);
                first = Math.min(first, i);
                last = Math.max(last, i);
                pre = i;
            }
        }
        return Math.min(minGap, SIZE - (last - first));
    }

    private int timeToInt(String time) {
        return Integer.parseInt(time.substring(0, 2)) * 60 + Integer.parseInt(time.substring(3, 5));
    }
}
```

#### 结果
![minimum-time-difference-1](/images/leetcode/minimum-time-difference-1.png)
