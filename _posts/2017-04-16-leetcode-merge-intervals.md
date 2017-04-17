---
layout: post
title: "Leetcode - Algorithm - Merge Intervals "
date: 2017-04-16 16:20:27
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","sort"]
level: "medium"
description: >
---

### 题目
Given a collection of intervals, merge all overlapping intervals.

For example,
```
Given [1,3],[2,6],[8,10],[15,18],
return [1,6],[8,10],[15,18].
```

### 先排序 $$O(n\log_{}{n})$$
假设有`[2,6],[1,3],[15,18],[8,10]`。先按起始位置排序，变成`[1,3],[2,6],[8,10],[15,18]`就好办多了。依次遍历，维护一个最大`scope`。只要起始位置没有超过这个最大`scope`就可以判断是`overlapping`了。

比如，`[1,3]`最大scope是`3`，`[2,6]`的起始位置`2 < 3`，代表区间重叠。然后最大scope更新为`6`。下一个元素`[8,10]`的起始元素`8 > 6`，就没有重叠。

#### 代码

```java
public class Solution {
    public List<Interval> merge(List<Interval> intervals) {
        List<Interval> res = new ArrayList<>();
        if (intervals.isEmpty()) { return res; }
        Collections.sort(intervals, new Comparator<Interval>() {
            public int compare(Interval first, Interval second) {
                return first.start - second.start;
            }
        });
        Interval pool = intervals.get(0);
        for (Interval next : intervals) {
            if (next.start > pool.end) {
                res.add(pool);
                pool = next;
            } else {
                pool.end = Math.max(pool.end,next.end);
            }
        }
        res.add(pool);
        return res;
    }
    private static class Interval {
        int start;
        int end;
        Interval() { start = 0; end = 0; }
        Interval(int s, int e) { start = s; end = e; }
    }
}
```

#### Lambda Expression 简化版
因为`Comparator`接口只有一个虚拟方法`compare()`，所以属于 **Functional Interface**。
> A functional interface is any interface that contains only one abstract method.

所以使用`Java 8`新特性`Lambda Expression`能很好地简化代码，
```java
public List<Interval> merge(List<Interval> intervals) {
        List<Interval> res = new ArrayList<>();
        if (intervals.isEmpty()) { return res; }
        Collections.sort(intervals, (Interval first, Interval second) -> first.start - second.start);
        Interval pool = intervals.get(0);
        for (Interval next : intervals) {
            if (next.start > pool.end) {
                res.add(pool);
                pool = next;
            } else {
                pool.end = Math.max(pool.end,next.end);
            }
        }
        res.add(pool);
        return res;
}
```

#### 结果
![merge-intervals-2](/images/leetcode/merge-intervals-2.png)
