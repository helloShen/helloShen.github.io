---
layout: post
title: "Leetcode - Algorithm - Design Hit Counter "
date: 2018-08-22 13:44:15
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","hash table","design"]
level: "medium"
description: >
---

### 题目
Design a hit counter which counts the number of hits received in the past 5 minutes.

Each function accepts a timestamp parameter (in seconds granularity) and you may assume that calls are being made to the system in chronological order (ie, the timestamp is monotonically increasing). You may assume that the earliest timestamp starts at 1.

It is possible that several hits arrive roughly at the same time.

Example:
```
HitCounter counter = new HitCounter();

// hit at timestamp 1.
counter.hit(1);

// hit at timestamp 2.
counter.hit(2);

// hit at timestamp 3.
counter.hit(3);

// get hits at timestamp 4, should return 3.
counter.getHits(4);

// hit at timestamp 300.
counter.hit(300);

// get hits at timestamp 300, should return 4.
counter.getHits(300);

// get hits at timestamp 301, should return 3.
counter.getHits(301);
```

Follow up:
What if the number of hits per second could be very large? Does your design scale?



### 直观的用`HashMap`
记录每个时间点的次数总和。缺点是
1. 当时间跨度大了以后，表会变得很大
2. 如果时间戳是以毫秒为单位，开销更恐怖

实际应用是不会这么做的。

#### 代码
```java
class HitCounter {

    /** Initialize your data structure here. */
    public HitCounter() {
        counter = new HashMap<Integer,Integer>();
    }

    /** Record a hit.
        @param timestamp - The current timestamp (in seconds granularity). */
    public void hit(int timestamp) {
        if (!counter.containsKey(timestamp)) {
            counter.put(timestamp,1);
        } else {
            counter.put(timestamp,counter.get(timestamp)+1);
        }
    }

    /** Return the number of hits in the past 5 minutes.
        @param timestamp - The current timestamp (in seconds granularity). */
    public int getHits(int timestamp) {
        int count = 0;
        int start = Math.max(0, timestamp - 300 + 1);
        for (int i = start; i <= timestamp; i++) {
            if (counter.containsKey(i)) {
                count += counter.get(i);
            }
        }
        return count;
    }        

    /** ============================== 【以下为私有】 ================================= */
    private Map<Integer,Integer> counter;
}
```

#### 结果
![design-hit-counter-1](/images/leetcode/design-hit-counter-1.png)


### 用定长数组，下标取模

![design-hit-counter-a](/images/leetcode/design-hit-counter-a.png)

#### 代码
```java
class HitCounter {

    public HitCounter() {
        time = new int[SIZE];
        hit = new int[SIZE];
    }
    public void hit(int timestamp) {
        int index = timestamp % SIZE;
        if (time[index] != timestamp) {
            time[index] = timestamp;
            hit[index] = 1;
        } else {
            hit[index]++;
        }
    }
    public int getHits(int timestamp) {
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            if (timestamp - time[i] < SIZE) {
                count += hit[i];
            }
        }
        return count;
    }

    /** ============================== 【以下为私有】 ================================= */
    private final int SIZE = 300;
    private int[] time;
    private int[] hit;

}
```

#### 结果
![design-hit-counter-2](/images/leetcode/design-hit-counter-2.png)
