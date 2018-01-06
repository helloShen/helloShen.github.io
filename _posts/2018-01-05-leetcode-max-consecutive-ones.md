---
layout: post
title: "Leetcode - Algorithm - Max Consecutive Ones "
date: 2018-01-05 22:00:39
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given a binary array, find the maximum number of consecutive 1s in this array.

Example 1:
```
Input: [1,1,0,1,1,1]
Output: 3
Explanation: The first two digits or the last three digits are consecutive 1s. The maximum number of consecutive 1s is 3.
```
Note:
1. The input array will only contain 0 and 1.
2. The length of input array is a positive integer and will not exceed 10,000

### 最朴素的遍历数组

#### 代码
```java
class Solution {
    public int findMaxConsecutiveOnes(int[] nums) {
        int count = 0, max = 0, cur = 0;
        while (cur < nums.length) {
            if (nums[cur] == 1) {
                count = 0;
                while (cur < nums.length && nums[cur] == 1) {
                    count++; cur++;
                }
                max = Math.max(count,max);
            } else {
                cur++;
            }
        }
        return max;
    }
}
```

#### 结果
![max-consecutive-ones-1](/images/leetcode/max-consecutive-ones-1.png)
