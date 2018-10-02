---
layout: post
title: "Leetcode - Algorithm - Find Pivot Index "
date: 2018-10-02 16:53:53
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "math"]
level: "easy"
description: >
---

### 题目
Given an array of integers nums, write a method that returns the "pivot" index of this array.

We define the pivot index as the index where the sum of the numbers to the left of the index is equal to the sum of the numbers to the right of the index.

If no such index exists, we should return -1. If there are multiple pivot indexes, you should return the left-most pivot index.

Example 1:
```
Input:
nums = [1, 7, 3, 6, 5, 6]
Output: 3
Explanation:
The sum of the numbers to the left of index 3 (nums[3] = 6) is equal to the sum of numbers to the right of index 3.
Also, 3 is the first index where this occurs.
```

Example 2:
```
Input:
nums = [1, 2, 3]
Output: -1
Explanation:
There is no index that satisfies the conditions in the problem statement.
```

Note:
* The length of nums will be in the range [0, 10000].
* Each element nums[i] will be an integer in the range [-1000, 1000].


### 老套路，先把数组求和
考虑数组`[1, 7, 3, 6, 5, 6]`，先按位累加数组，`sum[i]`代表`nums[0] ~ nums[i]`的和。
```
nums = [1,  7,   3,   6,   5,   6]
        |   |    |    |    |    |     
sum =  [1,->8,->11,->17,->22,->28]    累加数组
```

然后对于任意第`i`个数字，如果`sum[i - 1] == sum[len] - sum[i]`，那么就是`pivot`。
```
        0   1    2    3    4    5
sum =  [1,->8,->11,->17,->22,->28]

sum[3 - 1] = 11
sum[5] - sum[3] = 11
所以，
pivot = 3
```

#### 代码
直接在原`nums`数组上计算累加和。如果不想破坏原有数组，可以另开一个数组。
```java
class Solution {
   public int pivotIndex(int[] nums) {
       if (nums.length == 0) return -1;
       for (int i = 1; i < nums.length; i++) {
           nums[i] = nums[i - 1] + nums[i];
       }
       if (nums[nums.length - 1] - nums[0] == 0) return 0;
       for (int i = 1; i < nums.length; i++) {
           if (nums[i - 1] == (nums[nums.length - 1] - nums[i])) return i;
       }
       return -1;
   }
}
```

#### 结果
![find-pivot-index-1](/images/leetcode/find-pivot-index-1.png)


### 不记录累加数组，只记录整个数组的和也可以
和前面的方法本质是一样的，只是省空间。`Space = O(1)`。

还是`[1, 7, 3, 6, 5, 6]`的例子，我只记录最终和是`28`.
```
nums = [1,  7,   3,   6,   5,   6]  sum = 28
                      |
                  left = 11

right = sum - left - nums[3] = 28 - 11 - 6 = 11

right = left = 11
```

#### 代码
```java
class Solution {
    public int pivotIndex(int[] nums) {
        int sum = 0;
        for (int n : nums) {
            sum += n;
        }
        int left = 0;
        for (int i = 0; i < nums.length; i++) {
            if (left == (sum - left - nums[i])) return i;
            left += nums[i];
        }
        return -1;
    }
}
```

#### 结果
![find-pivot-index-2](/images/leetcode/find-pivot-index-2.png)
