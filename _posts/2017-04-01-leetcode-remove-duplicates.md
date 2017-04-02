---
layout: post
title: "Leetcode - Algorithm - Remove Duplicates "
date: 2017-04-01 20:12:57
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","two pointers"]
level: "easy"
description: >
---

### 题目
Given a sorted array, remove the duplicates in place such that each element appear only once and return the new length.

Do not allocate extra space for another array, you must do this in place with constant memory.
```
For example,
Given input array nums = [1,1,2],
```
Your function should return length = 2, with the first two elements of nums being 1 and 2 respectively. It doesn't matter what you leave beyond the new length.

### 笨办法，整体平移数组
维护两个指针，`fast`跑到前面去找更大的数，`slow`指向当前维护的不重复数组的边界。考虑有下面这个数组，
```bash
[1, 1，1，1, 2, 2, 2, 3]
```
当`fast`找到第一个`2`，就把`2`及它之后的所有元素向前平移几个单位。就像`ArrayList`做的那样。之后数组变成这样，
```bash
[1, 2, 2, 2, 3]
```

#### 代码
```java
public class Solution {
    public int removeDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) { return 0; }
        int slow = 0, fast = 0, end = nums.length;
        while (fast < end) {
            if (nums[fast] - nums[slow] > 0) {
                if (fast - slow > 1) { // need to move
                    int gap = fast - slow - 1;
                    for (int j = fast; j < end; j++) {
                        nums[j-gap] = nums[j];
                    }
                    end -= gap;
                }
                slow++;
                fast = slow;
            }
            fast++;
        }
        return slow+1;
    }
}
```

#### 结果
很差，不是银弹。
![remove-duplicates-1](/images/leetcode/remove-duplicates-1.png)


### 不复制整个数组，只复制当前数字
复制整个数组是不必要的。还是下面这个例子，
```bash
[1, 1，1，1, 2, 2, 2, 3]
```
当`fast`找到第一个`2`，只需要把`2`复制到第一个`1`的后面，然后`fast`指针接着往前走。之后数组变成这样，
```bash
[1, 2，1，1, 2, 2, 2, 3]
```

#### 代码
```java
public class Solution {
    public int removeDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) { return 0; }
        int slow = 0, fast = 0;
        int max = nums[slow];
        while (fast < nums.length) {
            if (nums[fast] > max) {
                nums[++slow] = nums[fast];
                max = nums[slow];
            }
            fast++;
        }
        return slow + 1;
    }
}
```

#### 结果
这就是银弹！
![remove-duplicates-2](/images/leetcode/remove-duplicates-2.png)


### 把代码写得更整洁

#### 代码
Sexy! Mia...Mia...
```java
public class Solution {
    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) { return 0; }
        int cursor = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[cursor]) {
                nums[++cursor] = nums[i];
            }
        }
        return ++cursor;
    }
}
```

#### 结果
Nice!
![remove-duplicates-3](/images/leetcode/remove-duplicates-3.png)
