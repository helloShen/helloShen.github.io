---
layout: post
title: "Leetcode - Algorithm - Contains Duplicate "
date: 2017-06-27 05:18:51
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","hash table"]
level: "easy"
description: >
---

### 题目
Given an array of integers, find if the array contains any duplicates. Your function should return true if any value appears at least twice in the array, and it should return false if every element is distinct.

### 先排序，然后比较相邻元素
排序之后，重复元素必定相邻排列。
* 时间复杂度：$$O(n\log_{}{n})$$
* 空间复杂度：$$O(1)$$

#### 代码
```java
public class Solution {
    public boolean containsDuplicate(int[] nums) {
        if (nums.length == 0) { return false; }
        Arrays.sort(nums);
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i-1]) { return true; }
        }
        return false;
    }
}
```

#### 结果
![contains-duplicate-1](/images/leetcode/contains-duplicate-1.png)


### 用一个容器记录遇到过的元素
用`HashSet`做容器比较好，可以用`add()`方法在 $$O(1)$$ 时间内判断重复。
* 时间复杂度：$$O(n)$$
* 空间复杂度：$$O(n)$$


#### 代码
```java
public class Solution {
    public boolean containsDuplicate(int[] nums) {
        if (nums.length == 0) { return false; }
        Set<Integer> checkSet = new HashSet<>();
        for (int n : nums) {
            if (!checkSet.add(n)) { return true; }
        }
        return false;
    }
}
```

#### 结果
![contains-duplicate-2](/images/leetcode/contains-duplicate-2.png)
