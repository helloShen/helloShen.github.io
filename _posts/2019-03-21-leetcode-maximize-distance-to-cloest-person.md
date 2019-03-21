---
layout: post
title: "Leetcode - Algorithm - Maximize Distance To Cloest Person "
date: 2019-03-21 11:46:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "math"]
level: "easy"
description: >
---

### 题目
In a row of seats, `1` represents a person sitting in that seat, and `0` represents that the seat is empty.

There is at least one empty seat, and at least one person sitting.

Alex wants to sit in the seat such that the distance between him and the closest person to him is maximized.

Return that maximum distance to closest person.

Example 1:
```
Input: [1,0,0,0,1,0,1]
Output: 2
Explanation:
If Alex sits in the second open seat (seats[2]), then the closest person has distance 2.
If Alex sits in any other open seat, the closest person has distance 1.
Thus, the maximum distance to the closest person is 2.
```

Example 2:
```
Input: [1,0,0,0]
Output: 3
Explanation:
If Alex sits in the last seat, the closest person is 3 seats away.
This is the maximum distance possible, so the answer is 3.
```

Note:
* 1 <= seats.length <= 20000
* seats contains only 0s or 1s, at least one 0, and at least one 1.

### 分两种情况数学计算
普通情况就是计算连续的`0`的数量，比如，
```
1,0,0,0,1
    1
| - -
d = 2               -->     d = (3 + 1) / 2 = 2

1,0,0,1
  1
| -
d = 1               -->     d = (2 + 1) / 2 = 1
```

所以归纳出来就是：
> d = (consecutiveZeros + 1) / 2

第二种特殊情况就是起始的连续空座，和末尾的连续空座，
```
0,0,0,1
1
| - - -
d = 3               -->     d = 3

1,0,0,0
      1
- - - |
d = 3               -->     d = 3
```

这种情况，最大距离就等于连续的0的个数，
> d = leading / ending consecutiveZeros

#### 代码
```java
class Solution {
    public int maxDistToClosest(int[] seats) {
        int[] triple = countConsecutiveZeros(seats);
        int max = (triple[1] + 1) / 2;
        return Math.max(max, Math.max(triple[0], triple[2]));
    }

    /**
     * return int[3] res such that:
     *  res[0]: length of leading consecutive zeros
     *  res[0]: maximum length of internal consecutive zeros
     *  res[2]: length of ending consecutive zeros
     */
    private int[] countConsecutiveZeros(int[] seats) {
        int[] res = new int[3];
        int head = 0;
        while (head < seats.length && seats[head] == 0) {
            res[0]++; head++;
        }
        int tail = seats.length - 1;
        while (tail >= 0 && seats[tail] == 0) {
            res[2]++; tail--;
        }
        int count = 0;
        for (int i = head; i <= tail; i++) {
            if (seats[i] == 1) {
                if (count > 0) {
                    res[1] = Math.max(res[1], count);
                    count = 0;
                }
            } else {
                count++;
            }
        }
        return res;
    }
}
```

#### 结果
![maximize-distance-to-cloest-person-1](/images/leetcode/maximize-distance-to-cloest-person-1.png)
