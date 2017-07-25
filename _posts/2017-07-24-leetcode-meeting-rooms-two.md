---
layout: post
title: "Leetcode - Algorithm - Meeting Rooms Two "
date: 2017-07-24 20:58:10
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: ""
description: >
---

### 题目
Given an array of meeting time intervals consisting of start and end times `[[s1,e1],[s2,e2],...] (si < ei)`, find the minimum number of conference rooms required.

For example,
Given `[[0, 30],[5, 10],[15, 20]]`,
return `2`.

### 给每个房间为线索，安排一个完整的时间表
注意：**必须先排序**。

假设我们是 **秘书**。需要统筹安排全部会议室的时间表。比如meeting的时间表如下：`[[5,10],[1,3],[2,5],[9,12],[10,14],[3,9]]`。最少只需要`2`个房间，
* Room 1: `[1,3],[3,9],[9,12]`
* Room 2: `[2,5],[5,10],[10,14]`

上面是一个比较极端的例子，最优解正好两个房间时间都充分利用，一个meeting紧接着下一个。但如果不排序直接用贪婪算法，拼凑出的时间段中可能有空白档期。就需要3个房间，
* Room 1: `[[1,3],[5,10],[10,14]]`
* Room 2: `[[2,5],[9,12]`
* Room 3: `[[3,9]]`

按照meeting开始时间排序之后，`[[1,3],[2,5],[3,9],[5,10],[9,12],[10,14]]`，这时候用贪婪算法，在一个meeting结束以后，接着安排的都是和结束时间衔接最紧密的。比如`[1,3]`之后，`[2,5]`插不进来，最先插进来的就是`[3,9]`。

#### 用容器
```java
public class Solution {
    public int minMeetingRooms(Interval[] intervals) {
        Arrays.sort(intervals, new Comparator<Interval>() {
            public int compare(Interval a, Interval b) {
                return a.start - b.start;
            }
        });
        List<Interval> list = new ArrayList<>(Arrays.asList(intervals));
        int count = 0;
        while (!list.isEmpty()) {
            int end = list.remove(0).end; count++; // create new timeline
            Iterator<Interval> ite = list.iterator();
            while (ite.hasNext()) {
                Interval next = ite.next();
                if (next.start >= end) { // add to current timeline
                    end = next.end;
                    ite.remove();
                }
            }
        }
        return count;
    }
}
```

#### 结果
![meeting-rooms-two-1](/images/leetcode/meeting-rooms-two-1.png)



#### 直接在数组上改
```java
public class Solution {
    public int minMeetingRooms(Interval[] intervals) {
        Arrays.sort(intervals, new Comparator<Interval>() {
            public int compare(Interval a, Interval b) {
                return a.start - b.start;
            }
        });
        int count = 0;
        for (int i = 0; i < intervals.length; i++) {
            Interval ii = intervals[i];
            if (ii == null) { continue; }
            int end = ii.end;
            intervals[i] = null; count++;
            for (int j = i+1; j < intervals.length; j++) {
                Interval ij = intervals[j];
                if (intervals[j] == null) { continue; }
                if (ij.start >= end) {
                    end = ij.end;
                    intervals[j] = null;
                }
            }
        }
        return count;
    }
}
```

#### 结果
![meeting-rooms-two-2](/images/leetcode/meeting-rooms-two-2.png)


### `Lazy Releasing`法
如果以一个 **实时** 的角度看这个问题，假设我们是当天的 **会议室管理员**。 只有当有人申请要用一个会议室的时候，我们才去检查当前有没有空闲的会议室。只有当当前开着的会议室全部被占用时，管理员才会去开一个新的房间。

这个过程可以用一个`PriorityQueue`来模拟。`PriorityQueue`里记录的是当前所有开着的会议室的 **会议结束之间**。 它的`poll()`方法负责返回最小值，也就是 **最先开完的那个会议**。只有当最先开完的会议也没有结束，就是没有空闲会议室的时候，管理员才开一个新的会议室。

#### 代码
```java
public class Solution {
    public int minMeetingRooms(Interval[] intervals) {
        if (intervals.length == 0) { return 0; }
        Arrays.sort(intervals, new Comparator<Interval>() {
            public int compare(Interval a, Interval b) { return a.start - b.start; }
        });
        PriorityQueue<Integer> rooms = new PriorityQueue<>(intervals.length);
        for (Interval meeting : intervals) {
            Integer nextRoom = rooms.poll(); // give the room to be available soon
            if (nextRoom == null) { rooms.add(meeting.end); continue; }
            if (nextRoom > meeting.start) { rooms.offer(nextRoom); } // can not use this room
            rooms.offer(meeting.end);
        }
        return rooms.size();
    }
}
```

#### 结果
![meeting-rooms-two-3](/images/leetcode/meeting-rooms-two-3.png)


#### `Lazy Releasing`法的聪明解法
```java
public class Solution {
    public int minMeetingRooms(Interval[] intervals) {
        int len = intervals.length;
        if (len == 0) { return 0; }
        int[] start = new int[len];
        int[] end = new int[len];
        for (int i = 0; i < len; i++) {
            start[i] = intervals[i].start;
            end[i] = intervals[i].end;
        }
        Arrays.sort(start);
        Arrays.sort(end);
        int rooms = 0;
        for (int i = 0, j = 0; i < len; i++) {
            if (start[i] < end[j]) {
                rooms++;
            } else {
                j++;
            }
        }
        return rooms;
    }
}
```

上面的算法实际上做了一件非常奇怪的事情，把会议的开始时间和结束时间存放在两个分开的数组里，并且分别排序。乍一看不可能完成任务，因为比较的开始时间和结束时间都不能确定是哪一个会议的。但实际上这就是遵循`Lazy Releasing`逻辑的做法。

假设会议档期看上去像下面这个例子，
```bash
|_____|
      |______|
|________|
        |_______|
```

把开始时间和结束时间分开排序之后，得到的两个数组看上去像下面这个样子，
```
||    ||                <- start time
     |   |   |  |       <- end time
```

如果我们给开始时间和结束时间都标上号，
```
ab    cd
||    ||                <- start time
     |   |   |  |       <- end time
     1   2   3  4
```
最先`a`时间开始的会议，必须开新房间没问题。然后`b`时间又有个会议，到`b`时间为止，注意，没有任何一个会议结束，所以又要开第二个会议室。

然后`c`时间第三个会议要开始，这时候管理员发现已经有一个会议在`1`号时间点结束了。可能是会议`a`也可能是会议`b`。但这不重要，重要的是现在有一个会议室空出来了，所以不必开第三个会议室。然后在`d`时间，又有一个会议，这时候最快在`2`时间结束的会议还没结束，没有新会议室空出来，所以又要开第三个会议室。

上面`Lazy Releasing`法实际上关心的是会议结束的时间。只有有新的会议要开才检查是不是有会议室空出来。而且空出来的是哪间会议室也不重要，哪间都可以。
```
+----------------------> a会议占据001会议室
|+---------------------> b会议占据002会议室
||    +----------------> c会议开始时，有一个会议室空出来。有可能是001号也可能是002号。
||    |+---------------> d会议占据003会议室
ab    cd
||    ||                <- start time
     |   |   |  |       <- end time
     1   2   3  4
```

#### 结果
![meeting-rooms-two-4](/images/leetcode/meeting-rooms-two-4.png)
