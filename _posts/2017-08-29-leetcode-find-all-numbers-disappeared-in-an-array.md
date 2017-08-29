---
layout: post
title: "Leetcode - Algorithm - Find All Numbers Disappeared In An Array "
date: 2017-08-29 15:25:54
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given an array of integers where 1 ≤ a[i] ≤ n (n = size of array), some elements appear twice and others appear once.

Find all the elements of [1, n] inclusive that do not appear in this array.

Could you do it without extra space and in O(n) runtime? You may assume the returned list does not count as extra space.

Example:
```
Input:
[4,3,2,7,8,2,3,1]

Output:
[5,6]
```

### 主要思路
因为`nums`长度正好为`n`，正好可以用来记录`1-n`个数的出现情况。所以遍历数组，取到每个数字就到相应的下标，把数字改成`0`。最后每个桶内值为零，就代表这个数字出现过。

关键就是在修改数组值之前，需要先记下当前值。这个过程用递归来做很简单。比如，
```
 +-----+  -> 4,找到下标3
 |     |
[4,3,2,7,8,2,3,1]

先记下int n = nums[3] = 7;

然后把nums[3]的值改成0.

       +-----+   -> 7,找到下标6
       |     |  
[4,3,2,0,8,2,3,1]

重复这个过程
```

#### 递归代码
```java
class Solution {
    private int[] array = new int[0];
    public List<Integer> findDisappearedNumbers(int[] nums) {
        array = nums;
        for (int i = 0; i < array.length; i++) {
            int n = array[i];
            if (n > 0) { check(n-1); }
        }
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            int n = array[i];
            if (n > 0) { res.add(i+1); }
        }
        return res;
    }
    private void check(int i) {
        int n = array[i];
        if (n < 1) { return; }
        array[i] = 0;
        check(n-1);
    }
}
```

#### 结果
![find-all-numbers-disappeared-in-an-array-1](/images/leetcode/find-all-numbers-disappeared-in-an-array-1.png)


#### 迭代更快一点

#### 代码
```java
class Solution {
    public List<Integer> findDisappearedNumbers(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            int offset = nums[i]-1;
            while (offset >= 0) {
                int newOffset = nums[offset]-1;
                nums[offset] = 0;
                offset = newOffset;
            }
        }
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int n = nums[i];
            if (n > 0) { res.add(i+1); }
        }
        return res;
    }
}
```

#### 结果
![find-all-numbers-disappeared-in-an-array-2](/images/leetcode/find-all-numbers-disappeared-in-an-array-2.png)
