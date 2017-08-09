---
layout: post
title: "Leetcode - Algorithm - Single Element In Sorted Array "
date: 2017-08-08 21:26:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","bit manipulation","hash set"]
level: "medium"
description: >
---

### 题目
Given a sorted array consisting of only integers where every element appears twice except for one element which appears once. Find this single element that appears only once.

Example 1:
```
Input: [1,1,2,3,3,4,4,8,8]
Output: 2
```
Example 2:
```
Input: [3,3,7,7,10,11,11]
Output: 10
```
Note: Your solution should run in $$O(log n)$$ time and $$O(1)$$ space.

### 标准`Set`解法，$$O(n)$$

#### 代码
```java
public class Solution {
    public int singleNonDuplicate(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int n : nums) {
            if (!set.add(n)) { set.remove(n); }
        }
        return set.iterator().next();
    }
}
```

#### 结果
![single-element-in-sorted-array-1](/images/leetcode/single-element-in-sorted-array-1.png)


### 用`XOR`位操作，$$O(n)$$

#### 代码
```java
public class Solution {
    public int singleNonDuplicate(int[] nums) {
        int res = 0;
        for (int n : nums) {
            res ^= n;
        }
        return res;
    }
}
```

#### 结果
![single-element-in-sorted-array-2](/images/leetcode/single-element-in-sorted-array-2.png)


### 利用排过序这个特性，$$O(n)$$
只需要比较偶数位的元素和它的下一个元素是否相等。

#### 代码
```java
public class Solution {
    public int singleNonDuplicate(int[] nums) {
        if (nums.length == 0) { return 0; }
        int cur = 1;
        while (cur < nums.length) {
            if (nums[cur-1] != nums[cur]) { return nums[cur-1]; }
            cur += 2;
        }
        return nums[cur-1];
    }
}
```

#### 结果
![single-element-in-sorted-array-3](/images/leetcode/single-element-in-sorted-array-3.png)

### 二分查找，$$O(\log_{}{n})$$
因为排过序，所以可以用二分法整块整块地排除。考虑`[3,3,4,4,7,8,8]`，如果发现这一对`[4,4]`是整齐的（偶数位的数字，和紧接着下一个元素相等），则说明落单的数字不在前半区`[3,3,4,4]`。

#### 代码

```java
public class Solution {
    public int singleNonDuplicate(int[] nums) {
        int lo = 0, hi = nums.length-1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            int even = (mid % 2 == 0)? nums[mid] : nums[mid-1];
            int followingOdd = (mid % 2 == 0)? nums[mid+1] : nums[mid];
            if (even == followingOdd) {
                lo = mid + 1;
            } else {
                hi = (mid / 2) * 2;
            }
        }
        return nums[lo];
    }
}
```

#### 结果
![single-element-in-sorted-array-4](/images/leetcode/single-element-in-sorted-array-4.png)
