---
layout: post
title: "Leetcode - Algorithm - Search In Rotated Sorted Array Two "
date: 2017-04-27 19:53:59
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","binary search"]
level: "medium"
description: >
---

### 主要收获
接触了一些二分查找的变种。但基本思想和朴素二分查找是一致的。

### 题目
Suppose an array sorted in ascending order is rotated at some pivot unknown to you beforehand.

(i.e., `0 1 2 4 5 6 7` might become `4 5 6 7 0 1 2`).

Write a function to determine if a given target is in the array.

The array may contain duplicates.

### 先找`pivot`点，再用普通二分法查找，行不通
因为考虑`[2,2,2,0,2,2]`这样的点，二分法找不到中间的`pivot`点`0`。当发现中位数`nums[mid] == nums[hi]`的时候，不能确定最小数在左半边，还是右半边。甚至都无法判断有没有最小数，因为完全有可能数组是这样的`[2,2,2,2,2,2]`。所以先找`pivot`的方法不可行。

**所以下面的代码是错的**。

```java
public boolean search(int[] nums, int target) {
    if (nums.length == 0) { return false; }
    int pivot = pivot(nums);
    int lo = 0, hi = nums.length-1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int absMid = (mid + pivot) % nums.length;
        System.out.println("Pos [" + absMid + "]: " + nums[absMid]);
        if (nums[absMid] < target) {
            lo = mid + 1;
        } else if (nums[absMid] > target) {
            hi = mid - 1;
        } else { // nums[absMid] == target
            return true;
        }
    }
    return false;
}
/**
 * pivot()函数是错误的！ 不保证能找到起始点。
 * 比如下面的情况：[2,2,2,0,2,2]
 * 当比较第3个2和最后一个2相等时，不能丢弃右半边。因为pivot可能在这个区间（这里的0）
 */
public int pivot(int[] nums) {
    if (nums.length < 2) { return nums.length; }
    int lo = 0, hi = nums.length-1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] <= nums[hi]) { // 错误！不能丢弃右半边
            hi = mid;
        } else {
            lo = mid + 1;
        }
    }
    return lo;
}
```

#### 结果
![search-in-rotated-sorted-array-two-1](/images/leetcode/search-in-rotated-sorted-array-two-1.png)

### 暴力遍历数组 $$O(n)$$
用线性逐个遍历数组元素，作为保底的方法。

#### 代码
```java
public class Solution {
    public boolean search(int[] nums, int target) {
        if (nums.length == 0) { return false; }
        for (int num : nums) {
            if (num == target) { return true; }
        }
        return false;
    }
}
```

#### 结果
![search-in-rotated-sorted-array-two-2](/images/leetcode/search-in-rotated-sorted-array-two-2.png)


### 其实可以不找`pivot`点，直接在数组上二分法，复杂度$$O(n)$$
每次迭代还是找中位数，然后和最高位比较，如果`nums[mid] == target`，就找到了，如果不相等，分为三种情况，
1. 如果像`[7,0,1,3,5,6]`这样，`nums[mid] < nums[hi]`，说明右半边`1,3,5,6`是升序的。
    * 如果`1 < target <= 6`，说明目标在`3,5,6`里能找到，所以放弃左半边`7,0,1`。
    * 否则，说明目标不在`1,3,5,6`，只需要到`7,0,1`里找。
2. 如果像`[1,3,5,6,1,1,1]`这样，`nums[mid] > nums[hi]`，说明左半边`1,3,5,6`是升序的。
    * 如果`1 <= target < 6`，目标在`1,3,5`里，放弃右半边`6,1,1,1`。
    * 否则，目标不在`1,3,5,6`里，在剩下的`1,1,1`里找。
3. 如果像`[2,2,2,1,2,2]`这样，`nums[mid] == nums[hi]`。目标数可能在左半边，也可能在右半边。唯一能确定的就是`target != nums[mid] && target != nums[hi]`。所以只能扔掉最高位的数。

很不幸，这种做法复杂度还是$$O(n)$$，考虑最坏情况`[2,1,1,1,1,1,1,1,1]`，每次左移最高位，需要遍历整个数组，所以复杂度是线性的。

#### 代码
```java
public class Solution {
    public boolean search(int[] nums, int target) {
        if (nums.length == 0) { return false; }
        int lo = 0, hi = nums.length-1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) { return true; }
            if (nums[mid] < nums[hi]) { // right half is sorted, ex:[7,0,1,3,5,6]
                if (nums[mid] < target && target <= nums[hi]) { // target can only be in right half
                    lo = mid + 1;
                } else { // target is not in right half
                    hi = mid - 1;
                }
            } else if (nums[mid] > nums[hi]) { // left half is sorted, ex:[1,3,5,6,1,1,1]
                if (nums[lo] <= target && target < nums[mid]) { // target can only be in left half
                    hi = mid - 1;
                } else { // target is not in left half
                    lo = mid + 1;
                }
            } else { // nums[mid] == nums[hi]: [2,2,2,0,2,2]
                hi--;
            }
        }
        return false;
    }
}
```

#### 结果
![search-in-rotated-sorted-array-two-3](/images/leetcode/search-in-rotated-sorted-array-two-3.png)
