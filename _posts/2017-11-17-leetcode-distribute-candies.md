---
layout: post
title: "Leetcode - Algorithm - Distribute Candies "
date: 2017-11-17 15:10:11
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","sort"]
level: "easy"
description: >
---

### 题目
Given an integer array with even length, where different numbers in this array represent different kinds of candies. Each number means one candy of the corresponding kind. You need to distribute these candies equally in number to brother and sister. Return the maximum number of kinds of candies the sister could gain.

Example 1:
```
Input: candies = [1,1,2,2,3,3]
Output: 3
Explanation:
There are three different kinds of candies (1, 2 and 3), and two candies for each kind.
Optimal distribution: The sister has candies [1,2,3] and the brother has candies [1,2,3], too.
The sister has three different kinds of candies.
```

Example 2:
```
Input: candies = [1,1,2,3]
Output: 2
Explanation: For example, the sister has candies [2,3] and the brother has candies [1,1].
The sister has two different kinds of candies, the brother has only one kind of candies.
```

Note:
* The length of the given array is in range [2, 10,000], and will be even.
* The number in given array is in range [-100,000, 100,000].

### 用`HashSet`统计不同数字的个数

#### 代码
```java
class Solution {
    public int distributeCandies(int[] candies) {
        Set<Integer> set = new HashSet<>();
        for (int candy : candies) {
            set.add(candy);
        }
        int kinds = set.size();
        int half = candies.length / 2;
        return (kinds >= half)? half : kinds;  
    }
}
```

#### 结果
![distribute-candies-1](/images/leetcode/distribute-candies-1.png)


### 先排序

#### 代码
```java
class Solution {
    public int distributeCandies(int[] candies) {
        if (candies.length == 0) { return 0; }
        Arrays.sort(candies);
        int cur = 1, kinds = 1;
        while (cur < candies.length) {
            if (candies[cur-1] < candies[cur]) {
                kinds++;
            }
            cur++;
        }
        int half = candies.length / 2;
        return (kinds > half)? half : kinds;
    }
}
```

#### 结果
![distribute-candies-2](/images/leetcode/distribute-candies-2.png)
