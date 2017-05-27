---
layout: post
title: "Leetcode - Algorithm - Gas Station "
date: 2017-05-27 01:04:43
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["greedy","two pointers"]
level: "medium"
description: >
---

### 题目
There are N gas stations along a circular route, where the amount of gas at station i is gas[i].

You have a car with an unlimited gas tank and it costs cost[i] of gas to travel from station i to its next station (i+1). You begin the journey with an empty tank at one of the gas stations.

Return the starting gas station's index if you can travel around the circuit once, otherwise return -1.

Note:
The solution is guaranteed to be unique.

### 贪心算法, 复杂度 $$O(n^2)$$
最直观的做法，就是用一个`int`模拟油箱的油量。用两个指针分别指向`gas`和`cost`数组，每到一站就加油，开车就耗油。

当油箱为负数时，表明油不够了，继续尝试从下一个车站出发。

#### 代码
```java
public class Solution {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        if (gas.length == 0 || cost.length == 0 || gas.length != cost.length) { return -1; }
        int size = gas.length;
        outFor:
        for (int i = 0; i < size; i++) {
            int tankRemain = 0;
            for (int j = 0, cur = i; j < size; j++, cur = (++cur)%size) {
                tankRemain = tankRemain + gas[cur] - cost[cur];
                if (tankRemain < 0) { continue outFor; }
            }
            return i;
        }
        return -1;
    }
}
```

#### 结果
不清楚问题在哪里。
![gas-station-1](/images/leetcode/gas-station-1.png)


### 表驱动优化, 复杂度 $$O(n^2)$$
每一站能加的油量和到下一站需要的耗油是一对固定的值，也就是每一站都有固定的盈亏。提前把这个表算出来，省去了很多重复计算。
```
gas: [10,  8,  7,  11,  20,  5]
cost:[ 7, 20, 11,   4,   5, 10]
---------------------------------
diff:[ 3,-12, -4,   7,  15, -5]
```

#### 代码
```java
public class Solution {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        if (gas.length == 0 || cost.length == 0 || gas.length != cost.length) { return -1; }
        int size = gas.length;
        int[] diff = new int[size]; // 每个站的独立盈亏表
        for (int i = 0; i < size; i++) {
            diff[i] = gas[i] - cost[i];
        }
        outFor:
        for (int i = 0; i < size; i++) {
            int tankRemain = 0;
            for (int j = 0, cur = i; j < size; j++,cur++) {
                if (cur == size) { cur = 0; }
                tankRemain = tankRemain + diff[cur];
                if (tankRemain < 0) { continue outFor; } // 赤字
            }
            return i;
        }
        return -1;
    }
}
```

#### 结果
虽然通过了，但还差的很远。
![gas-station-2](/images/leetcode/gas-station-2.png)


### 一些进一步的推论
根据题意，可以得出3个重要的推论：
1. 如果总油量，不足以支撑总里程，无论如何都是跑不下来的。
2. 反之，如果总油量足够跑完全程，那么我们总是能够找到一条能够跑下来的路线。
3. 当开到某处油量不够，整段路的总收益为负。可以直接跳过这段，从下一个加油站开始从新探索。

最重要的是推论3，可以直接将复杂度降低到 **$$O(n)$$**。

```
gas: [10,  8,  7,  11,  20,  5]
cost:[ 7, 20, 11,   4,   5, 10]
---------------------------------
diff:[ 3,-12, -4,   7,  15, -5]
```
当我们跑到第二站，`3-12=-9`的时候，前两站的总收益为负数，我们可以直接跳过第二站，从第三站开始接着探索。

#### 代码
```java
public class Solution {
    public int canCompleteCircuit(int[] gas, int[] cost) {
        if (gas.length == 0 || cost.length == 0 || gas.length != cost.length) { return -1; }
        int size = gas.length;
        int[] diff = new int[size];
        int globalSum = 0;
        for (int i = 0; i < size; i++) {
            diff[i] = gas[i] - cost[i];
            globalSum += diff[i];
        }
        if (globalSum < 0) { return -1; } // 如果总油量不够跑全程，迅速失败
        outFor:
        for (int i = 0; i < size; i++) {
            int tankRemain = 0;
            for (int cur = i; cur < i+size; cur++) {
                tankRemain += diff[cur%size];
                if (tankRemain < 0) {
                    i = cur; // 根据推论3，跳过整段总收益为负的里程。
                    continue outFor;
                }
            }
            return i;
        }
        return -1;
    }
}
```

#### 结果
银弹！
![gas-station-3](/images/leetcode/gas-station-3.png)
