---
layout: post
title: "Leetcode - Algorithm - Relative Ranks "
date: 2017-12-06 16:36:12
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given scores of **N** athletes, find their relative ranks and the people with the top three highest scores, who will be awarded medals: "Gold Medal", "Silver Medal" and "Bronze Medal".

Example 1:
```
Input: [5, 4, 3, 2, 1]
Output: ["Gold Medal", "Silver Medal", "Bronze Medal", "4", "5"]
Explanation: The first three athletes got the top three highest scores, so they got "Gold Medal", "Silver Medal" and "Bronze Medal".

For the left two athletes, you just need to output their relative ranks according to their scores.
```

Note:
* N is a positive integer and won't exceed 10,000.
* All the scores of athletes are guaranteed to be unique.

### 给数组的拷贝排序，再用二分查找，$$O(n\log_{}{n})$$

#### 代码
```java
class Solution {
    public String[] findRelativeRanks(int[] nums) {
        int len = nums.length;
        int[] copy = Arrays.copyOf(nums,len);
        Arrays.sort(copy);
        String[] res = new String[len];
        for (int i = 0; i < len; i++) {
            int index = Arrays.binarySearch(copy,nums[i]);
            if (index == (len - 1)) {
                res[i] = "Gold Medal";
            } else if (index == (len - 2)) {
                res[i] = "Silver Medal";
            } else if (index == (len - 3)) {
                res[i] = "Bronze Medal";
            } else {
                res[i] = String.valueOf(len - index);
            }
        }
        return res;
    }
}
```

#### 结果
![relative-ranks-1](/images/leetcode/relative-ranks-1.png)
