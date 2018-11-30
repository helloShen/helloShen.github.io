---
layout: post
title: "Leetcode - Algorithm - Poor Pigs "
date: 2018-11-30 18:25:02
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
There are 1000 buckets, one and only one of them contains poison, the rest are filled with water. They all look the same. If a pig drinks that poison it will die within 15 minutes. What is the minimum amount of pigs you need to figure out which bucket contains the poison within one hour.

Answer this question, and write an algorithm for the follow-up general case.

Follow-up:
* If there are n buckets and a pig drinking poison will die within m minutes, how many pigs (x) you need to figure out the "poison" bucket within p minutes? There is exact one bucket with poison.

### 问题描述的补充
这题表述不是太清楚，补充条件如下，
* 猪可以同时喝很多杯水
* 毒药发作的15分钟是一个大约范围，不是说一定是`15*60`秒后死亡。所以想让猪每一秒喝一杯水，然后按秒表计时的做法不可行。
* 判断一杯水有没有毒，就要让一头猪喝这杯水，然后等15分钟，15分钟以后猪没死，就说明没毒，否则就是有毒。

### 用`N`维坐标定位一杯水
这里用到一个小技巧，可以让猪一下子喝比如`10`杯水，然后等15分钟。如果猪没死，说明这10杯水都是无毒的。如果死了，就说明毒药就在这10杯水当中。

所以，假设有一个小时`60`分钟，毒药发作时间是`15`分钟，那么`1`头猪，最多可以测试`5`杯水，
```
15m  15m  15m  15m
 |    |    |    |  
 1    2    3    4    5

前4杯水，每喝一杯等15分钟，可以判断出这4杯水的结果，正好1个小时。
最后一杯水不用测试，可以根据前4杯水的结果推测。
```

把问题推广到2维空间，假设有`5 * 5 = 25`杯水，只需要`2`头猪就能试出结果。方法是，
* 让`猪1号`按列`[1,6,11,16,21]`这么喝。`5`杯水一起喝，喝完等`15`分钟出结果。
* `猪2号`按行`[1,2,3,4,5]`这么喝。也一次喝`5`杯水，喝完等`15`分钟出结果。

1个小时之后，这杯水在第几行，第几列我们就知道了。
```
猪1号
15m  15m  15m  15m
 |    |    |    |  
 1    2    3    4    5  <- 15min 猪2号
 6    7    8    9   10  <- 15min
11   12   13   14   15  <- 15min
16   17   18   19   20  <- 15min
21   22   23   24   25
```

同理，如果是`3`维矩阵，最多可以测试`5 * 5 * 5 = 125`杯水。

归纳成数学公式如下，
> (minutesToTest / minutesToDie + 1) ^ pigs >= buckets

这里的`pigs`就是我们要求的猪的数量。

一个特殊情况是当`buckets = 1`的时候，一头猪也不需要。因为只可能是这杯水有毒。

#### 代码
```java
class Solution {
    public int poorPigs(int buckets, int minutesToDie, int minutesToTest) {
        if (buckets == 1) return 0;
        int len = (minutesToTest / minutesToDie + 1);
        for (int pigs = 1, product = 1; pigs <= buckets; pigs++) {
            product *= len;
            if (product >= buckets) return pigs;
        }
        return 0;
    }
}
```

#### 结果
![poor-pigs-1](/images/leetcode/poor-pigs-1.png)
