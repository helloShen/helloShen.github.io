---
layout: post
title: "Leetcode - Algorithm - Move Zeros "
date: 2017-08-05 16:32:58
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","two pointers"]
level: "easy"
description: >
---

### 题目
Given an array nums, write a function to move all 0's to the end of it while maintaining the relative order of the non-zero elements.

For example, given nums = `[0, 1, 0, 3, 12]`, after calling your function, nums should be `[1, 3, 12, 0, 0]`.

Note:
You must do this in-place without making a copy of the array.
Minimize the total number of operations.


### 把`0`往后移，复杂度 $$O(n^2)$$
用一个指针一直指向最后一个非零的数。用另一个指针向前找零，然后一位一位往后交换位置。
```
    zero last non zero
     |    |
[0,1,0,3,12]
```

#### 代码
```java
public class Solution {
    public void moveZeroes(int[] nums) {
        int high = nums.length - 1;
        while ( high >= 0 && nums[high] == 0) { --high; } // high points to the last non zero number
        int low = high - 1;
        while (low >= 0) {
            if (nums[low] == 0) {
                int cur = low;
                while (cur < high) {
                    int temp = nums[cur];
                    nums[cur] = nums[cur+1];
                    nums[cur+1] = temp;
                    ++cur;
                }
                --high;
            }
            --low;
        }
    }
}
```

#### 结果
![move-zeros-1](/images/leetcode/move-zeros-1.png)


### 把非零数往前移，复杂度 $$O(n)$$
维护一个指向第一个零的指针，用另一个指针向后遍历，找到非零的数，写到第一个零的位置（注意不是交换元素，那个零不用管它）。最后再把指向第一个零的指针后面的所有元素改为零。
```
first zero
 |
[0, 1, 0, 3, 12]
    |
    non zero
```


#### 代码
```java
public class Solution {
    public void moveZeroes(int[] nums) {
        int firstZero = 0;
        while (firstZero < nums.length && nums[firstZero] != 0) { ++firstZero; } // firstZero points to the first zero in array
        int cur = firstZero + 1;
        while (cur < nums.length) {
            if (nums[cur] != 0) { nums[firstZero++] = nums[cur]; } // don't need to update nums[cur]
            ++cur;
        }
        while (firstZero < nums.length) { nums[firstZero++] = 0; } // the rest are all zero
    }
}
```

#### 结果
![move-zeros-2](/images/leetcode/move-zeros-2.png)
