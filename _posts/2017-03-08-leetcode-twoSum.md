---
layout: post
title: "Leetcode - Altorithm - Two Sum"
date: 2017-03-08 01:01:31
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode"]
level: "easy" 
description: >
---

### 题目
Given an array of integers, return indices of the two numbers such that they add up to a specific target.

You may assume that each input would have exactly one solution, and you may not use the same element twice.

Example:
```
Given nums = [2, 7, 11, 15], target = 9,

Because nums[0] + nums[1] = 2 + 7 = 9,
return [0, 1].
```

### log(n2)朴素方法
最简单的两层迭代。
```java
public static int[] twoSum(int[] nums, int target) {
    if (nums.length < 2) {
        throw new IllegalArgumentException();
    }
    int[] result = new int[] {-1,-1};
    for (int i = 0; i < nums.length-1; i++) {
        for (int j = i+1; j < nums.length; j++) {
            if (nums[i] + nums[j] == target) {
                result[0] = i;
                result[1] = j;
                return result;
            }
        }
    }
    return result;
}
```

#### 结果
![two-sum-1](/images/leetcode/two-sum-1.png)

### log(n)解法
空间换时间。用`Map`存下以前遇到过的值。

```java
public static int[] twoSumLogN(int[] nums, int target) {
    if (nums.length < 2) {
        throw new IllegalArgumentException();
    }
    Map<Integer,Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int diff = target - nums[i];
        if (map.containsKey(diff)) {
            return new int[] {map.get(diff),i};
        }
        map.put(nums[i],i);
    }
    return new int[] {0,0};
}
```

#### 结果
![two-sum-2](/images/leetcode/two-sum-2.png)
