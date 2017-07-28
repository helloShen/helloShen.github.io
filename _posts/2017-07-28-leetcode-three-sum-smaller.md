---
layout: post
title: "Leetcode - Algorithm - Three Sum Smaller "
date: 2017-07-28 15:59:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","two pointers"]
level: "medium"
description: >
---

### 题目
Given an array of n integers nums and a target, find the number of index triplets i, j, k with 0 <= i < j < k < n that satisfy the condition nums[i] + nums[j] + nums[k] < target.

For example, given nums = `[-2, 0, 1, 3]`, and target = `2`.

Return 2. Because there are two triplets which sums are less than 2:
```
[-2, 0, 1]
[-2, 0, 3]
```
Follow up:
Could you solve it in O(n2) runtime?

### 朴素遍历，$$O(n^3)$$
没花样，$$O(n^3)$$肯定能解。

#### 代码
```java
public class Solution {
    public int threeSumSmaller(int[] nums, int target) {
        int count = 0;
        for (int i = 0; i < nums.length-2; i++) {
            for (int j = i+1; j < nums.length-1; j++) {
                for (int k = j+1; k < nums.length; k++) {
                    if (nums[i] + nums[j] + nums[k] < target) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
```

#### 结果
![three-sum-smaller-1](/images/leetcode/three-sum-smaller-1.png)


### `Two Pointers`法, $$O(n^2)$$
先排序，然后用3个指针，
```
  i left           right
  |  |              |
[-3,-1,1,5,8,11,15,16]          target = 10
```
`(-3) + (-1) + 16 > 10`，太大了。所有`left`再往左移的情况都不用再考虑了，因为只会更大。所以`right`往左移。取更小的值。
```
  i left    right
  |  |       |
[-3,-1,1,5,8,11,15,16]          target = 10
```
直到`right = 11`，`(-3) + (-1) + 11 < 10`。这时候说明如果`right`取所有`left`到`right`之间的数，和只会更小，不用再测，所以`count = count + (right - left)`。

然后再把`i`往右移，进入下一个循环，一次类推。
```
     i left        right
     |  |           |
[-3,-1,1,5,8,11,15,16]          target = 10
```

#### 代码
```java
public class Solution {
    public int threeSumSmaller(int[] nums, int target) {
        if (nums.length < 3) { return 0; }
        Arrays.sort(nums);
        int count = 0;
        for (int i = 0; i < nums.length-2; i++) {
            int left = i+1, right = nums.length-1;
            while (left < right) {
                if (nums[i] + nums[left] + nums[right] < target) {
                    count += (right - left); left++;
                } else {
                    right--;
                }
            }
        }
        return count;
    }
}
```

#### 结果
![three-sum-smaller-2](/images/leetcode/three-sum-smaller-2.png)
