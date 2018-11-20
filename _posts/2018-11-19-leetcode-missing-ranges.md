---
layout: post
title: "Leetcode - Algorithm - Missing Ranges "
date: 2018-11-19 20:22:36
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given a sorted integer array nums, where the range of elements are in the inclusive range [lower, upper], return its missing ranges.

Example:
```
Input: nums = [0, 1, 3, 50, 75], lower = 0 and upper = 99,
Output: ["2", "4->49", "51->74", "76->99"]
```

### 先用二分查找找到左右界，然后遍历数组
比如例子中的数组`[0, 1, 3, 50, 75]`，用二分查找搜索`lower=0`和`upper=99`的插入位置。
```
    0               99
    |               |
    [0, 1, 3, 50, 75]

  min lo         hi  max
     |/           |  |
    [0, 1, 3, 50, 75]
```

如上图所示，问题就转化成再`[min,max]`范围内，挖掉从`nums[lo]`到`nums[hi]`中的每一个数字。

这题要注意一个陷阱就是`Integer.MAX_VALUE`和`Integer.MIN_VALUE`是两个隐含的自然边界。比如给出数组`[2147483647]`，`lower = 0`, `upper = 2147483647`。注意处理的时候不要越界。
```
   0
  min lo hi max    
   |   \ | /  
    [2147483647]
```

#### 代码
```java
class Solution {
    public List<String> findMissingRanges(int[] nums, int lower, int upper) {
        List<String> list = new ArrayList<>();
        if (lower > upper) return list;
        String range = "";
        if (nums.length == 0) {
            list.add(toRange(lower, upper));
            return list;
        }
        int loIdx = Arrays.binarySearch(nums, lower);
        if (loIdx < 0) loIdx = -(loIdx + 1);
        int hiIdx = Arrays.binarySearch(nums, upper);
        if (hiIdx < 0) hiIdx = -(hiIdx + 1) - 1;
        if (loIdx <= hiIdx) {
            int leftRange = 0, rightRange = 0;
            if (nums[loIdx] != Integer.MIN_VALUE) {
                leftRange = lower;
                rightRange = nums[loIdx] - 1;
                range = toRange(leftRange, rightRange);
                if (!range.isEmpty()) list.add(range);
            }
            while (loIdx < hiIdx) {
                if (nums[loIdx + 1] != Integer.MIN_VALUE) {
                    leftRange = nums[loIdx] + 1;
                    rightRange = nums[loIdx + 1] - 1;
                    range = toRange(leftRange, rightRange);
                    if (!range.isEmpty()) list.add(range);
                }
                loIdx++;
            }
            if (nums[hiIdx] != Integer.MAX_VALUE) {
                leftRange = nums[hiIdx] + 1;
                rightRange = upper;
                range = toRange(leftRange, rightRange);
                if (!range.isEmpty()) list.add(range);
            }
        }
        return list;
    }

    private String toRange(int left, int right) {
        String range = "";
        if (left <= right) {
            range = String.valueOf(left);
            if (left < right) {
                range += "->";
                range += String.valueOf(right);
            }
        }
        return range;
    }
}
```

也可以不做二分查找，直接遍历数组，这样代码更简明。如果懒得处理`Integer.MIN_VALUE`和`Integer.MAX_VALUE`这样的边角情况，可以直接用`long`型。

```java
class Solution {
    public List<String> findMissingRanges(int[] nums, int lower, int upper) {
        List<String> list = new ArrayList<>();
        long lo = lower;
        for (int i = 0; i < nums.length && nums[i] <= upper; i++) {
            long hi = nums[i];
            if (hi < lo) continue;
            if (hi == lo) {
                lo++;
                continue;
            }
            list.add(getRange(lo, hi - 1));
            lo = hi + 1;
        }
        if ((long)upper >= lo) list.add(getRange(lo, upper));
        return list;
    }

    // assertion: for [lo, hi], lo <= hi
    private String getRange(long lo, long hi) {
        String range = String.valueOf(lo);
        if (hi > lo) {
            range += "->";
            range += String.valueOf(hi);
        }
        return range;
    }
}
```

#### 结果
![missing-ranges-1](/images/leetcode/missing-ranges-1.png)
