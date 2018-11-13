---
layout: post
title: "Leetcode - Algorithm - Continuous Subarray Sum "
date: 2018-11-12 18:40:20
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "math"]
level: "medium"
description: >
---

### 题目
Given a list of non-negative numbers and a target integer k, write a function to check if the array has a continuous subarray of size at least 2 that sums up to the multiple of k, that is, sums up to n*k where n is also an integer.

Example 1:
```
Input: [23, 2, 4, 6, 7],  k=6
Output: True
Explanation: Because [2, 4] is a continuous subarray of size 2 and sums up to 6.
```

Example 2:
```
Input: [23, 2, 6, 4, 7],  k=6
Output: True
Explanation: Because [23, 2, 6, 4, 7] is an continuous subarray of size 5 and sums up to 42.
```

Note:
* The length of the array won't exceed 10,000.
* You may assume the sum of all the numbers is in the range of a signed 32-bit integer.

### 2层for遍历所有子串，`O(N^2)`
暴力可解。需要注意`k = 0`的特殊情况。因为`0`不能当除数。所以要单独考虑。要分两种情况，
1. 当`k == 0`, 只有存在连续2个或以上`0`（`[..., 0, 0, ...]`），才返回`true`。因为只有`0`才是0的倍数。
2. 事实上只要数组中存在连续2个或以上`0`（`[..., 0, 0, ...]`），无论`k`等于多少，都返回`true`。因为`0`是所有整数的0倍。

#### 代码
```java
public boolean checkSubarraySum(int[] nums, int k) {
    if (checkContinuousZeros(nums)) return true;
    if (k == 0) return false;
    for (int lo = 0; lo < nums.length - 1; lo++) {
        int sum = nums[lo];
        for (int hi = lo + 1; hi < nums.length; hi++) {
            sum += nums[hi];
            if (sum % k == 0) return true;
        }
    }
    return false;
}

private boolean checkContinuousZeros(int[] nums) {
    for (int i = 0; i < nums.length - 1; i++) {
        if (nums[i] == 0 && nums[i + 1] == 0) return true;
    }
    return false;
}
```

#### 结果
![continuous-subarray-sum-1](/images/leetcode/continuous-subarray-sum-1.png)


### 用一个表记录之前所有的余数，`O(N)`
这个方法只需要遍历一次数组。原理是假设下面示例中`[2~5]`的和是`6`的整数倍，
```
k = 6

  3 + 4 + 5 = 12，是6的整数倍
   |<->|
[2,3,4,5,6,7,8,9]

这里隐含了加到2的总和，除以6的余数，等于加到5的总和除以6的余数。就因为3+4+5是6的整数倍。

2 % 6 = 2

2 + 3 + 4 + 5 = 14， 14 % 6 = 2
```

#### 代码
```java
public boolean checkSubarraySum(int[] nums, int k) {
    Map<Integer, Integer> prevMod = new HashMap<>();
    prevMod.put(0, -1);
    for (int i = 0, sum = 0; i < nums.length; i++) {
        sum += nums[i];
        if (k != 0) sum %= k;
        if (log.isDebugEnabled()) {
            log.debug("mod[{}] = {}", i, sum);
        }
        Integer prevIndex = prevMod.get(sum);
        if (prevIndex != null) {
            if (i - prevIndex > 1) return true;
        } else {
            prevMod.put(sum, i);
        }
    }
    return false;
}
```

#### 结果
![continuous-subarray-sum-2](/images/leetcode/continuous-subarray-sum-2.png)
