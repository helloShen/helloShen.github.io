---
layout: post
title: "Leetcode - Algorithm - Summary Ranges "
date: 2017-07-02 12:00:25
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given a sorted integer array without duplicates, return the summary of its ranges.

For example, given `[0,1,2,4,5,7]`, return `["0->2","4->5","7"]`.

### 没什么花样，直接遍历数组

#### 代码
```java
public class Solution {
    public List<String> summaryRanges(int[] nums) {
        List<String> result = new ArrayList<>();
        int cur = 0;
        while (cur < nums.length) {
            String range = String.valueOf(nums[cur++]);
            int start = cur;
            while (cur < nums.length && (nums[cur] == nums[cur-1] + 1)) { cur++; }
            if (cur > start) { range = range + "->" + String.valueOf(nums[cur-1]); }
            result.add(range);
        }
        return result;
    }
}
```

#### 结果
![summary-ranges-1](/images/leetcode/summary-ranges-1.png)
