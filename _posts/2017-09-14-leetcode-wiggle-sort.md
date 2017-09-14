---
layout: post
title: "Leetcode - Algorithm - Wiggle Sort "
date: 2017-09-14 15:00:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given an unsorted array nums, reorder it in-place such that `nums[0]` <= `nums[1]` >= `nums[2]` <= `nums[3]`....

For example, given nums = `[3, 5, 2, 1, 6, 4]`, one possible answer is `[1, 6, 2, 5, 3, 4]`.

### 逐个检查，遇到不符合的就交换元素
比如`[3, 5, 2, 1, 6, 4]`，
```bash
3 < 5? 符合
5 > 2? 符合
2 < 1? 不符合 -> 交换2和1的位置，因为 5 > 2 成立，所以 5 > 1也一定成立。
数组变成[3,5,1,2,6,4]
2 > 6? 不符合 -> 交换2和6。和上面同理。
数组变成[3,5,1,6,2,4]
2 < 4? 符合
```

#### 代码
```java
class Solution {
    public void wiggleSort(int[] nums) {
        boolean greaterThanNext = false;
        for (int i = 0; i < nums.length-1; i++) {
            if (greaterThanNext) {
                if (nums[i] <= nums[i+1]) { exch(nums,i,i+1); }
            } else {
                if (nums[i] >= nums[i+1]) { exch(nums,i,i+1); }
            }
            greaterThanNext = !greaterThanNext;
        }
    }
    private void exch(int[] nums, int a, int b) {
        int temp = nums[a];
        nums[a] = nums[b];
        nums[b] = temp;
    }
}
```

#### 结果
![wiggle-sort-1](/images/leetcode/wiggle-sort-1.png)
