---
layout: post
title: "Leetcode - Algorithm - Circular Array Loop "
date: 2018-09-17 16:14:33
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["two pointers", "linked list"]
level: "medium"
description: >
---

### 题目
You are given an array of positive and negative integers. If a number n at an index is positive, then move forward n steps. Conversely, if it's negative (-n), move backward n steps. Assume the first element of the array is forward next to the last element, and the last element is backward next to the first element. Determine if there is a loop in this array. A loop starts and ends at a particular index with more than 1 element along the loop. The loop must be "forward" or "backward'.

Example 1: Given the array `[2, -1, 1, 2, 2]`, there is a loop, from index `0 -> 2 -> 3 -> 0`.

Example 2: Given the array `[-1, 2]`, there is no loop.

Note: The given array is guaranteed to contain no element "0".

Can you do it in `O(n)` time complexity and `O(1)` space complexity?


### 一个`fast`一个`slow`指针
有两个指针在数组上跳转，
* `fast`每次跳跃两下
* `slow`每次跳跃一下

这里必须明确，在一个定长数组中跳转，必定会有循环。所以我们才可以用`fast`和`slow`指针，也就是不需要任何控制，最后`fast`和`slow`必定相遇。

但这题里的“循环”必须是单纯的“正循环”或“反循环”。所谓正循环就是每次跳转都是向前跳转（元素为正整数）。反循环则必须都是向后跳（元素为负数）。所以`[-2,1,-1,-2,-2]`不符合条件，因为它最终是在`[1,-1]`之间来回前后摇摆。

但这题还有一个隐含条件，就是元素必须最终回到起手的一个元素。换句话说，像`[-1,-2,-3,-4,-5]`这样半路形成的循环也不符合条件，因为它最终一直在`[-5]`循环，`[-1]`节点没有被包含进去。

另外题目里说循环必须多于1个元素，看起来不是必须的，因为测试用例`[3,1,2]`的预期结果是`true`，但是它从头到尾都只在`[3]`上循环。

#### 代码
```java
class Solution {

    public boolean circularArrayLoop(int[] nums) {
        if (nums == null || nums.length == 0) { return false; }
        localNums = nums;
        size = nums.length;
        isForward = (nums[0] > 0);
        int slow = 0, fast = 0;
        do {
            for (int i = 0; i < 2; i++) {
                if (isCrossward(fast)) return false;
                fast = skip(fast);
            }
            slow = skip(slow);
        } while (slow != fast);
        return fast == 0; // something they called loop here must go back to the first element
    }

    // environment
    private int[] localNums;
    private int size;
    private boolean isForward;

    private int skip(int index) {
        index += localNums[index];
        if (isForward && index >= size) {
            index %= size;
        } else if (!isForward && index < 0) {
            index = index % size + size;
        }
        return index;
    }      

    private boolean isCrossward(int index) {
        return (isForward && localNums[index] < 0) || (!isForward && localNums[index] > 0);
    }

}
```

#### 结果
![circular-array-loop-1](/images/leetcode/circular-array-loop-1.png)
