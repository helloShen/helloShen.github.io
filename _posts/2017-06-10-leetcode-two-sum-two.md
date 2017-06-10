---
layout: post
title: "Leetcode - Algorithm - Two Sum Two "
date: 2017-06-10 19:31:17
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","two pointers"]
level: "easy"
description: >
---

### 题目
Given an array of integers that is already sorted in ascending order, find two numbers such that they add up to a specific target number.

The function twoSum should return indices of the two numbers such that they add up to the target, where index1 must be less than index2. Please note that your returned answers (both index1 and index2) are not zero-based.

You may assume that each input would have exactly one solution and you may not use the same element twice.

Input: numbers=`{2, 7, 11, 15}`, target=`9`
Output: index1=`1`, index2=`2`

### 老实遍历，时间复杂度 $$O(n^2)$$
两个指针`slow`和`fast`两层套嵌遍历。

#### 代码
```java
public class Solution {
    public int[] twoSum(int[] numbers, int target) {
        int[] res = new int[]{-1,-1};
        if (numbers.length < 2) { return res; }
        int slow = 0;
        while (slow < numbers.length-1 && numbers[slow] <= target) {
            int num1 = numbers[slow];
            for (int fast = slow + 1; fast < numbers.length; fast++) {
                int sum = num1 + numbers[fast];
                if (sum == target) { // find target
                    res[0] = slow+1; res[1] = fast+1;
                    return res;
                } else if (sum > target) {
                    break;
                }
            }
            slow++;
        }
        return res; // do not find target
    }
}
```

#### 结果
![two-sum-two-1](/images/leetcode/two-sum-two-1.png)


### `slow`指针老实遍历，`fast`指针二分查找，复杂度 $$O(n\log_{n})$$
因为数组预先排过序，比如`2,7,11,15,19,21`，`target = 18`,当`slow`指向`2`，`fast`就可以做一个`7,11,15,19,21`的二分查找`remain = 18-2 = 16`。
```
 slow fast
    | |
    2,7,11,15,19,21
      |--二分查找--|
```


#### 代码
```java
public class Solution {
    public int[] twoSum(int[] numbers, int target) {
        int[] res = new int[] {-1,-1};
        if (numbers.length < 2) { return res; }
        int slow = 0;
        while (slow < numbers.length-1 && numbers[slow] <= target) {
            int remain = target - numbers[slow];
            int lo = slow+1, hi = numbers.length-1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                int median = numbers[mid];
                if (median < remain) {
                    lo = mid+1;
                } else if (median > remain) {
                    hi = mid-1;
                } else { // median == remain
                    res[0] = slow+1; res[1] = mid+1;
                    return res; // find target
                }
            }
            slow++;
        }
        return res; // do not find target
    }
}
```

#### 结果
![two-sum-two-2](/images/leetcode/two-sum-two-2.png)


### `Two Pointers`的标准解法，复杂度 $$O(n)$$
和`Two Sum`一样，用两个指针`lo`和`hi`分别指向数组边界，
```
target = 18

    lo           hi
    |            |
    2,7,11,15,19,21         //  2 + 21 > 18   ->  hi--

    lo         hi
    |          |
    2,7,11,15,19,21         //  2 + 19 > 18   ->  hi--

    lo      hi
    |       |
    2,7,11,15,19,21         //  2 + 15 < 18   ->  lo++

      lo    hi
      |     |
    2,7,11,15,19,21         //  7 + 15 > 18   ->  hi--

      lo hi
      |  |
    2,7,11,15,19,21         //  7 + 11 == 18   ->  return
```


#### 代码
```java
public class Solution {
    public int[] twoSum(int[] numbers, int target) {
        int[] res = new int[] {-1,-1};
        if (numbers.length < 2) { return res; }
        int lo = 0, hi = numbers.length-1;
        while (lo < hi) {
            int sum = numbers[lo] + numbers[hi];
            if (sum < target) {
                lo++;
            } else if (sum > target) {
                hi--;
            } else { // sum == target [FIND!!!]
                res[0] = lo+1; res[1] = hi+1;
                break;
            }
        }
        return res;
    }
}
```

#### 结果
![two-sum-two-3](/images/leetcode/two-sum-two-3.png)
