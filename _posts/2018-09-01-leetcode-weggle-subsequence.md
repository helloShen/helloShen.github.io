---
layout: post
title: "Leetcode - Algorithm - Weggle Subsequence "
date: 2018-09-01 13:41:05
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
A sequence of numbers is called a wiggle sequence if the differences between successive numbers strictly alternate between positive and negative. The first difference (if one exists) may be either positive or negative. A sequence with fewer than two elements is trivially a wiggle sequence.

For example, `[1,7,4,9,2,5]` is a wiggle sequence because the differences (6,-3,5,-7,3) are alternately positive and negative. In contrast, `[1,4,7,2,5]` and `[1,7,4,5,5]` are not wiggle sequences, the first because its first two differences are positive and the second because its last difference is zero.

Given a sequence of integers, return the length of the longest subsequence that is a wiggle sequence. A subsequence is obtained by deleting some number of elements (eventually, also zero) from the original sequence, leaving the remaining elements in their original order.

Examples:
```
Input: [1,7,4,9,2,5]
Output: 6
The entire sequence is a wiggle sequence.

Input: [1,17,5,10,13,15,10,5,16,8]
Output: 7
There are several subsequences that achieve this length. One is [1,17,10,13,10,16,8].

Input: [1,2,3,4,5,6,7,8,9]
Output: 2
```
Follow up:
* Can you do it in O(n) time?

### 遍历数组，跳过单调递增或单调递减的元素即可
```
[1, 17, 5, 10, 13, 15, 10, 5, 16, 8]
  16  -12 5  3   2   -5  -5 11  -8
 |+++|--|+++|+++|++|----|--|+++|--|

1
1   -> 17               // 改变单调性就增加列表长度
1   -> 17  -> 5         // ...
1   -> 17  -> 5  -> 10  // ...
1   -> 17  -> 5  -> 13  // 出现连加，或连减，不增加链条长度，只更新最大最小值即可
                           因为13 > 10，所以用较大的13替换较小的10，不影响接下来的单调性
```


#### 代码
```java
class Solution implements Solution {
    public int wiggleMaxLength(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int count = 1, sign = 0;
        for (int i = 1; i < nums.length; i++) {
            int diff = nums[i] - nums[i-1];
            if ((sign >= 0 && diff < 0) || (sign <= 0 && diff > 0)) {
                count++;
                sign = diff;
            }
        }
        return count;
    }
}
```

#### 结果
![weggle-subsequence-1](/images/leetcode/weggle-subsequence-1.png)
