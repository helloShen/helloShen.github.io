---
layout: post
title: "Leetcode - Algorithm - Find All Duplicates In An Array "
date: 2017-09-15 19:44:15
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given an array of integers, `1 ≤ a[i] ≤ n` (n = size of array), some elements appear twice and others appear once.

Find all the elements that appear twice in this array.

Could you do it without extra space and in O(n) runtime?

Example:
```
Input:
[4,3,2,7,8,2,3,1]

Output:
[2,3]
```

### 用`Set`，$$O(n)$$

#### 代码
```java
class Solution {
    public List<Integer> findDuplicates(int[] nums) {
        Set<Integer> set = new HashSet<>();
        List<Integer> res = new ArrayList<>();
        for (int num : nums) {
            if (!set.add(num)) { res.add(num); }
        }
        return res;
    }
}
```

#### 结果
![find-all-duplicates-in-an-array-1](/images/leetcode/find-all-duplicates-in-an-array-1.png)


### 直接在原始数组上记录出现信息，$$O(n)$$
拿到`i`桶位的数`nums[i] = x`，那么就把`x`桶位的数变成负数，`nums[x] = -nums[x]`。下次`x`再出现，先检查一下`x`桶位的数`nums[x]`是不是负数。

#### 代码
```java
class Solution {
    public List<Integer> findDuplicates(int[] nums) {
        List<Integer> res = new ArrayList<>();
        for (int num : nums) {
            int offset = Math.abs(num) - 1;
            if (nums[offset] < 0) { res.add(offset+1); }
            nums[offset] = -nums[offset];
        }
        return res;
    }
}
```

#### 结果
![find-all-duplicates-in-an-array-2](/images/leetcode/find-all-duplicates-in-an-array-2.png)
