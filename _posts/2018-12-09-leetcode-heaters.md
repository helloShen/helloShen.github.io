---
layout: post
title: "Leetcode - Algorithm - Heaters "
date: 2018-12-09 18:48:11
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["arrays", "math"]
level: "easy"
description: >
---

### 题目
Winter is coming! Your first job during the contest is to design a standard heater with fixed warm radius to warm all the houses.

Now, you are given positions of houses and heaters on a horizontal line, find out minimum radius of heaters so that all houses could be covered by those heaters.

So, your input will be the positions of houses and heaters seperately, and your expected output will be the minimum radius standard of heaters.

Note:
* Numbers of houses and heaters you are given are non-negative and will not exceed 25000.
* Positions of houses and heaters you are given are non-negative and will not exceed 10^9.
* As long as a house is in the heaters' warm radius range, it can be warmed.
* All the heaters follow your radius standard and the warm radius will the same.

Example 1:
```
Input: [1,2,3],[2]
Output: 1
Explanation: The only heater was placed in the position 2, and if we use the radius 1 standard, then all the houses can be warmed.
```

Example 2:
```
Input: [1,2,3,4],[1,4]
Output: 1
Explanation: The two heater was placed in the position 1 and 4. We need to use radius 1 standard, then all the houses can be warmed.
```

### 二分查找，两种思路
要么以`heater`为切入点，取`heater1`和`heater2`的中位数，中位数左边的房间归`heater1`管，右边的归`heater2`管。从而得出`heater1`的最小范围和`heater2`的最小范围，取两者较大的那个。依次类推。唯一的边角情况，就是头部和尾部，情形也是类似的。
```
        mid
 heater1 | heater2   heater3
     |<-----> |<----->|
    [a, b, c,... , d, e, f, ...]    <-- houses
     |--|  |--|
       |     |
heater1范围  heater2范围
```

要么以`house`为切入点，对于每一个`house`，找到其前置heater和后置heater（可以空缺），然后分配给较近的那个。
```
heaterX  heaterY
 |         |
----------------------
    |
  house1 (归heaterX管，因为离heaterX更近)
```

两种方法在查找相邻heater或house的时候，假设heater号和house号都是有序的，可以用 **二分查找**，所以两种方法的复杂度都是一样的`O(NlogN)`，`N`为数组的长度。但后一种方法代码更短，思路更简明一点。但我做题的时候先想到的是第一种思路，所以代码比较复杂。

#### 代码
```java
class Solution {
    public int findRadius(int[] houses, int[] heaters) {
        if (houses.length == 0 || heaters.length == 0) return 0;
        Arrays.sort(houses);
        Arrays.sort(heaters);
        localHouses = houses;
        localHeaters = heaters;
        int radius = 0;
        if (houses[0] <= heaters[0]) {
            radius = heaters[0] - houses[0];
        }
        int lastHouse = houses[houses.length - 1];
        int lastHeater = heaters[heaters.length - 1];
        if (lastHouse >= lastHeater) {
            radius = Math.max(radius, lastHouse - lastHeater);
        }
        for (int i = 1; i < heaters.length; i++) {
            int lo = heaters[i - 1], hi = heaters[i];
            int lowerMid = lo + (hi - lo) / 2;
            int left = leftOfMid(lowerMid);
            if (left >= 0 && left > heaters[i - 1]) {
                radius = Math.max(radius, left - heaters[i - 1]);
            }
            int upperMid = lo + (hi - lo + 1) / 2;
            int right = rightOfMid(upperMid);
            if (right >= 0 && right < heaters[i]) {
                radius = Math.max(radius, heaters[i] - right);
            }
            if (heaters[i] > houses[houses.length - 1]) break;
        }
        return radius;
    }

    private int[] localHouses;
    private int[] localHeaters;

    /**
     * @param  mid  lower median index
     * @return      return the left room number if exists, otherwise return -1
     */
    private int leftOfMid(int mid) {
        int leftIdx = Arrays.binarySearch(localHouses, mid);
        if (leftIdx >= 0) return localHouses[leftIdx];
        leftIdx = - (leftIdx + 1);
        return (leftIdx > 0)? localHouses[leftIdx - 1] : -1;
    }

    /**
     * @param  mid  upper meidan index
     * @return      return the right room number if exists, otherwise return -1
     */
    private int rightOfMid(int mid) {
        int rightIdx = Arrays.binarySearch(localHouses, mid);
        if (rightIdx >= 0) return (rightIdx == localHouses.length)? -1 : localHouses[rightIdx];
        rightIdx = - (rightIdx + 1);
        return (rightIdx == localHouses.length)? -1 : localHouses[rightIdx];
    }

    /**
     * @param heater [room number of heater]
     * @param room [room number]
     * @return      minimum radius so that input heater can cover the given room
     */
    private int radius(int heater, int room) {
        return Math.abs(heater - room) + 1;
    }
}
```

#### 结果
![heaters-1](/images/leetcode/heaters-1.png)
