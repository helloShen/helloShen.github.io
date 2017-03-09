---
layout: post
title: "Leetcode - Two Sum"
date: 2017-03-08 01:01:31
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode"]
description: >
---

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
