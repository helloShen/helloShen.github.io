---
layout: post
title: "Leetcode - Algorithm - Design Log Storage System "
date: 2018-10-29 20:30:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string", "design"]
level: "medium"
description: >
---

### 题目
You are given several logs that each log contains a unique id and timestamp. Timestamp is a string that has the following format: `Year:Month:Day:Hour:Minute:Second`, for example, `2017:01:01:23:59:59`. All domains are zero-padded decimal numbers.

Design a log storage system to implement the following functions:
1. `void Put(int id, string timestamp)`: Given a log's unique id and timestamp, store the log in your storage system.
2. `int[] Retrieve(String start, String end, String granularity)`: Return the id of logs whose timestamps are within the range from start to end. Start and end all have the same format as timestamp. However, granularity means the time level for consideration. For example, start = "2017:01:01:23:59:59", end = "2017:01:02:23:59:59", granularity = "Day", it means that we need to find the logs within the range from Jan. 1st 2017 to Jan. 2nd 2017.

Example 1:
```
put(1, "2017:01:01:23:59:59");
put(2, "2017:01:01:22:59:59");
put(3, "2016:01:01:00:00:00");
retrieve("2016:01:01:01:01:01","2017:01:01:23:00:00","Year"); // return [1,2,3], because you need to return all logs within 2016 and 2017.
retrieve("2016:01:01:01:01:01","2017:01:01:23:00:00","Hour"); // return [1,2], because you need to return all logs start from 2016:01:01:01 to 2017:01:01:23, where log 3 is left outside the range.
```

Note:
* There will be at most 300 operations of Put or Retrieve.
* Year ranges from [2000,2017]. Hour ranges from [00,23].
* Output for Retrieve has no order required.

### 有序和乱序之间的取舍
首先，如果是乱序，采用暴力逐个元素比较大小，保底复杂度是`O(N)`，`N`代表时间戳的个数。如果想超过这个复杂度，来到`O(logN)`，首先需要时间戳序列是有序排列的，然后用二分查找找到起首时间戳和终止时间戳。但最终需要返回`List<Integer>`的`id`序列，代表着就算在`O(logN)`事件内找到了目标时间段，之后逐一读取`id`的过程也至少是`O(N)`的。况且在需要随时`put()`新元素的情况下，维护一个有序数列复杂度至少是`O(NlogN)`。

所以这题`O(N)`的朴素遍历所有元素比较大小的方案即是最佳方案。

当然在需要维护有序数列的情况下，比较好的数据结构是`TreeMap`，无论是插入，查找或删除操作都保证在`O(logN)`时间内完成。

#### 代码
具体实现过程中，系统性地把每个时间段的数字都识别出来，以一个`int[7]`（最后一位储存`id`）来储存，然后构造一个新的类`Time`可以让代码更清晰。
```java
class LogSystem {

    private final String SEP = ":";
    private class Time {
        private int[] t;
        private Time(int id, String s) {
            t = new int[7];
            String[] segs = s.split(SEP);
            for (int i = 0; i < 6; i++) {
                t[i] = Integer.parseInt(segs[i]);
            }
            t[6] = id;
        }
        private int compareTo(Time another, int depth) {
            for (int i = 0; i <= depth; i++) {
                if (t[i] != (another.t)[i]) return t[i] - (another.t)[i];
            }
            return 0;
        }
    }

    private final int MAX = 300;
    private Time[] times;
    private int size;

    public LogSystem() {
        times = new Time[MAX];
        size = 0;
    }

    public void put(int id, String timestamp) {
        times[size++] = new Time(id, timestamp);
    }

    public List<Integer> retrieve(String s, String e, String gra) {
        List<Integer> res = new ArrayList<>();
        Time ts = new Time(-1, s);
        Time te = new Time(-1, e);
        int depth = depth(gra);
        for (int i = 0; i < size; i++) {
            Time t = times[i];
            if (t.compareTo(ts, depth) >= 0 && t.compareTo(te, depth) <= 0) res.add(t.t[6]);
        }
        return res;
    }

    private int depth(String gra) {
        switch(gra) {
            case "Year":
                return 0;
            case "Month":
                return 1;
            case "Day":
                return 2;
            case "Hour":
                return 3;
            case "Minute":
                return 4;
            case "Second":
                return 5;
            default:
                return -1;
        }
    }

}

/**
 * Your LogSystem object will be instantiated and called as such:
 * LogSystem obj = new LogSystem();
 * obj.put(id,timestamp);
 * List<Integer> param_2 = obj.retrieve(s,e,gra);
 */
```

另外稍微做一点变化，也可以不把时间戳的每一部分时间都截取出来，而是以整个`String`的形式储存。比较大小的时候可以比较一定长度的子串。因为`String`本身实现了`Comparable`接口，`2016:01:01`和`2017:01:01`是可以直接比较的。唯一需要定制的是年月日时分秒不同粒度分别对应的子串长度：`{4,7,10,13,16,19}`，这个长度是固定的。
```
    4  7  10 13 16 19
    |  |  |  |  |  |
1980:02:12:07:34:18
```

因为复杂度还是`O(N)`，所以并不会影响太多性能。

```java
class LogSystem {

    private class Time {
        int id;
        String timestamp;
        private Time(int id, String s) {
            this.id = id;
            timestamp = s;
        }
        private int compareTo(Time another, int depth) {
            return timestamp.substring(0, depth).compareTo(another.timestamp.substring(0, depth));
        }
    }

    private final int MAX = 300;
    private Time[] times;
    private int size;

    public LogSystem() {
        times = new Time[MAX];
        size = 0;
    }

    public void put(int id, String timestamp) {
        times[size++] = new Time(id, timestamp);
    }

    public List<Integer> retrieve(String s, String e, String gra) {
        List<Integer> res = new ArrayList<>();
        Time ts = new Time(-1, s);
        Time te = new Time(-1, e);
        int depth = depth(gra);
        for (int i = 0; i < size; i++) {
            Time t = times[i];
            if (t.compareTo(ts, depth) >= 0 && t.compareTo(te, depth) <= 0) res.add(t.id);
        }
        return res;
    }

    private int depth(String gra) {
        switch(gra) {
            case "Year":
                return 4;
            case "Month":
                return 7;
            case "Day":
                return 10;
            case "Hour":
                return 13;
            case "Minute":
                return 16;
            case "Second":
                return 19;
            default:
                return -1;
        }
    }

}

/**
 * Your LogSystem object will be instantiated and called as such:
 * LogSystem obj = new LogSystem();
 * obj.put(id,timestamp);
 * List<Integer> param_2 = obj.retrieve(s,e,gra);
 */
```

#### 结果
![design-log-storage-system-1](/images/leetcode/design-log-storage-system-1.png)
