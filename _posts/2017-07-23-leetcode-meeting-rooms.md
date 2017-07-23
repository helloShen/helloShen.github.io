---
layout: post
title: "Leetcode - Algorithm - Meeting Rooms "
date: 2017-07-23 12:48:06
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["sort"]
level: "easy"
description: >
---

### 题目
Given an array of meeting time intervals consisting of start and end times `[[s1,e1],[s2,e2],...]` (si < ei), determine if a person could attend all meetings.

For example,
Given `[[0, 30],[5, 10],[15, 20]]`,
return false.

### 先排序，$$O(n\log_{}{n})$$
对于`[[15, 20],[5, 10],[0, 30]]`，先按照开始时间`start`排序。变成`[[0, 30],[5, 10],[15, 20]]`比较好处理，只需要比较相邻两个会议的开始时间和结束时间，看有没有重叠。


#### 代码
```java
/**
 * Definition for an interval.
 * public class Interval {
 *     int start;
 *     int end;
 *     Interval() { start = 0; end = 0; }
 *     Interval(int s, int e) { start = s; end = e; }
 * }
 */
public class Solution {
    public boolean canAttendMeetings(Interval[] intervals) {
        if (intervals.length < 2) { return true; }
        Arrays.sort(intervals,new Comparator<Interval>() {
            public int compare(Interval i1, Interval i2) {
                return i1.start - i2.start;
            }
        });
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i-1].end > intervals[i].start) { return false; }
        }
        return true;
    }
}
```

#### 结果
![meeting-rooms-1](/images/leetcode/meeting-rooms-1.png)


### 在比较的时候，发现冲突就抛出异常
上面的方法都是先完整地排序完了之后，再比较相邻元素间有没有重叠。在比较的时候比较的是两个元素的起始时间`a.start`和`b.start`。如果这时候顺便比较一下`a.start`和`b.end`或者`a.end`和`b.start`，可以马上知道两个元素是否重叠，不用排序完就可以跳出循环。

#### 代码
```java
public class Solution {
        public boolean canAttendMeetings(Interval[] intervals) {
            if (intervals.length < 2) { return true; }
            try {
                Arrays.sort(intervals,new Comparator<Interval>() {
                    public int compare(Interval i1, Interval i2) throws RuntimeException {
                        if (i1.start < i2.start && i1.end <= i2.start) { return -1; }
                        if (i1.start > i2.start && i1.start >= i2.end) { return 1; }
                        throw new RuntimeException("Duplicate!");
                    }
                });
            } catch (RuntimeException e) {
                return false;
            }
            return true;
        }
}
```

#### 结果
![meeting-rooms-2](/images/leetcode/meeting-rooms-2.png)


### 最后证明一遍，朴素的 $$O(n^2)$$的遍历法真的很慢

#### 代码
```java
public class Solution {
    public boolean canAttendMeetings(Interval[] intervals) {
        if (intervals.length < 2) { return true; }
        for (int i = 1; i < intervals.length; i++) {
            for (int j = 0; j < i; j++) {
                if (isConflict(intervals[i],intervals[j])) { return false; }
            }
        }
        return true;
    }
    public boolean isConflict(Interval a, Interval b) {
        boolean ibfj = (a.start < b.start) && (a.end <= b.start);
        boolean iaftj = (a.start > b.start) && (a.start >= b.end);
        return !(ibfj || iaftj);
    }
}
```

#### 结果
![meeting-rooms-3](/images/leetcode/meeting-rooms-3.png)
