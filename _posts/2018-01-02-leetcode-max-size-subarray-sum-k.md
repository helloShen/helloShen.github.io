---
layout: post
title: "Leetcode - Algorithm - Max Size Subarray Sum K "
date: 2018-01-02 22:59:07
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table"]
level: "medium"
description: >
---

### 题目
Given an array nums and a target value k, find the maximum length of a subarray that sums to k. If there isn't one, return 0 instead.

Note:
The sum of the entire nums array is guaranteed to fit within the 32-bit signed integer range.

Example 1:
```
Given nums = [1, -1, 5, -2, 3], k = 3,
return 4. (because the subarray [1, -1, 5, -2] sums to 3 and is the longest)
```

Example 2:
```
Given nums = [-2, -1, 2, 1], k = 1,
return 2. (because the subarray [-1, 2] sums to 1 and is the longest)
```

Follow Up:
Can you do it in O(n) time?

### 直观 $$O(n^2)$$ 的遍历数组
预先计算一个累积和数组。比如：
```
原数组：[1, -1, 5, -2, 3]
累积和：[1, 0, 5, 3, 6]
```

然后计算每一个可能长度的可能子串（开一个固定长度的窗口）。

#### 代码
```java
class Solution {
    public int maxSubArrayLen(int[] nums, int k) {
        int[] sums = new int[nums.length+1];
        for (int i = 1; i < sums.length; i++) {
            sums[i] = sums[i-1] + nums[i-1];
        }
        for (int i = nums.length; i > 0; i--) {
            for (int j = 1, l = j + i - 1; l < sums.length; j++, l++) {
                if (sums[l] - sums[j-1] == k) { return i; }
            }
        }
        return 0;
    }
}
```

#### 结果
![max-size-subarray-sum-k-1](/images/leetcode/max-size-subarray-sum-k-1.png)


### 用一个`HashMap`
这个HashMap的键-值对为`[所有i位前元素之和，i]`。
```
       j         i    <- (if sum[i] - sum[j] = k)
       |         |
[x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x]
       |sum = k|
```
如上图所示，遍历数组到`i`位置，Map里有到之前每一个位置累积和。如果`k - sum[i] = sum[j]`，就说明`sum[j,i-1] = k`。

#### 代码
```java
class Solution {
    public int maxSubArrayLen(int[] nums, int k) {
        Map<Integer,Integer> map = new HashMap<>();
        int sum = 0, max = 0;
        for (int i = 1; i <= nums.length; i++) {
            sum += nums[i-1];
            if (sum == k) {
                max = Math.max(max,i);
            } else if (map.containsKey(sum - k)) {
                max = Math.max(max,i - map.get(sum - k));
            }
            if (!map.containsKey(sum)) { map.put(sum,i); } // 只保留和为某值的最短长度
        }
        return max;
    }
}
```

#### 结果
![max-size-subarray-sum-k-2](/images/leetcode/max-size-subarray-sum-k-2.png)
