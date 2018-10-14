---
layout: post
title: "Leetcode - Algorithm - Exam Room "
date: 2018-10-14 01:41:49
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["priority queue", "heap", "tree"]
level: "medium"
description: >
---

### 题目
In an exam room, there are N seats in a single row, numbered 0, 1, 2, ..., N-1.

When a student enters the room, they must sit in the seat that maximizes the distance to the closest person.  If there are multiple such seats, they sit in the seat with the lowest number.  (Also, if no one is in the room, then the student sits at seat number 0.)

Return a class ExamRoom(int N) that exposes two functions: ExamRoom.seat() returning an int representing what seat the student sat in, and ExamRoom.leave(int p) representing that the student in seat number p now leaves the room.  It is guaranteed that any calls to ExamRoom.leave(p) have a student sitting in seat p.

Example 1:
```
Input: ["ExamRoom","seat","seat","seat","seat","leave","seat"], [[10],[],[],[],[],[4],[]]
Output: [null,0,9,4,2,null,5]
Explanation:
ExamRoom(10) -> null
seat() -> 0, no one is in the room, then the student sits at seat number 0.
seat() -> 9, the student sits at the last seat number 9.
seat() -> 4, the student sits at the last seat number 4.
seat() -> 2, the student sits at the last seat number 2.
leave(4) -> null
seat() -> 5, the student sits at the last seat number 5.
```

Note:
* 1 <= N <= 10^9
* ExamRoom.seat() and ExamRoom.leave() will be called at most 10^4 times across all test cases.
* Calls to ExamRoom.leave(p) are guaranteed to have a student currently sitting in seat number p.


### `Max Heap`管理空座位
首先给 **“连续的空座位”** 一个名字`Gap`，如下图所示，
```
    Gap             Gap
 |<----->|   |<------------->|
X - - - - X X - - - - - - - - X X - - -
```

和左右相邻同学的距离`distance`取决于一个`Gap`的大小。正常情况下，`distance = gap / 2` （向下取整）。
```
    Gap             Gap
 |<----->|   |<------------->|
X - - - - X X - - - - - - - - X X - - -
 |<-|        |<-----|
distance     distance
```
只有当Gap包含头部，或尾部，`distance = gap`，
```
       Gap                                  Gap
|<------------>|                       |<----------->|
- - - - - - - - X X - - - - - - - - X X - - - - - - -
|------------->|                       |------------>|
    distance                               distance
```

每次调用`seat()`函数，我们只想知道当前最大`distance`是多少。然后占用中间的位置，把这个Gap分成两半。到`leave(x)`函数的时候，找到和`x`相邻的两个Gap，再把他们合二为一。

所以`PriorityQueue`可以很好地完成这个任务。
* `seat()`：O(logN)
* `leave()`： O(N)

#### 代码
```java
class ExamRoom {

    private class Gap {
        private int lo, hi, dis;
        private Gap(int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
            this.dis = (lo == 0 || hi == size - 1)? hi - lo : (hi - lo) / 2;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Gap)) return false;
            return this.lo == ((Gap)o).lo;
        }

    }

    private int size;
    private PriorityQueue<Gap> gaps;

    public ExamRoom(int n) {
        size = n;
        gaps = new PriorityQueue<Gap>((Gap a, Gap b) -> (a.dis != b.dis)? b.dis - a.dis : a.lo - b.lo);
        gaps.add(new Gap(0, n - 1));
    }

    public int seat() {
        Gap gap = gaps.poll();
        if (gap.lo == 0) return takeFirst(gap);
        if (gap.hi == size - 1) return takeLast(gap);
        int seat = gap.lo + (gap.hi - gap.lo) / 2;
        if (seat - gap.lo > 0) gaps.add(new Gap(gap.lo, seat - 1));
        if (gap.hi - seat > 0) gaps.add(new Gap(seat + 1, gap.hi));
        return seat;
    }

    private int takeFirst(Gap gap) {
        if (gap.hi > 0) gaps.add(new Gap(1, gap.hi));
        return 0;
    }

    private int takeLast(Gap gap) {
        if (gap.lo < size - 1) gaps.add(new Gap(gap.lo, size - 2));
        return size - 1;
    }

    public void leave(int p) {
        Iterator<Gap> ite = gaps.iterator();
        int head = p, tail = p;
        while (ite.hasNext()) {
            Gap gap = ite.next();
            if (gap.hi == p - 1) {
                head = gap.lo;
                ite.remove();
            } else if (gap.lo == p + 1) {
                tail = gap.hi;
                ite.remove();
            }
        }
        gaps.add(new Gap(head, tail));
    }
}
```

#### 结果
![exam-room-1](/images/leetcode/exam-room-1.png)
