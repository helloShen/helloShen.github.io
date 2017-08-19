---
layout: post
title: "Leetcode - Algorithm - Find Duplicate Number "
date: 2017-08-18 21:58:50
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","array","two pointers"]
level: "medium"
description: >
---

### 题目
Given an array nums containing `n + 1` integers where each integer is between `1` and `n` (inclusive), prove that at least one duplicate number must exist. Assume that there is only one duplicate number, find the duplicate one.

Note:
* You must not modify the array (assume the array is read only).
* You must use only constant, O(1) extra space.
* Your runtime complexity should be less than O(n2).
* There is only one duplicate number in the array, but it could be repeated more than once.

### 基本思路
这题如果没有那4个约束条件，有很多基本解法。
1. 朴素遍历，复杂度 $$O(n^2)$$
2. 先排序，再查找，复杂度 $$O(n\log_{}{n})$$
3. 用`Set`容器检查重复。复杂度 $$O(n)$$

但上面的三种方法都不不符合那4个条件。朴素法效率不达标，排序法违反了不能修改数组的约束，容器使用了额外内存是不允许的。

这题有两种更好的解法。一种常规方法，一种奇技淫巧。
* 常规法是：二分查找。
* 奇技淫巧是：龟兔赛跑算法。

### 二分查找
思路是这样的，假设有`101`个数，分布再`[1,100]`的空间。根据`Pigeon Hole`原理，
> 有101根萝卜，只有100个坑。 至少有一个坑里有超过1根萝卜。

先取1到100的中位数：`50`。然后数一下这101个数中，有几个是`<= 50`的。如果这个数量大于50个，就说明重复的那个数字在`[1,50]`的区间内。反之，则这个数在`[51,100]`的区间内。

用萝卜来说，就是，
> 数前50个坑里的萝卜，如果超过50根萝卜，说明萝卜多的那个坑就在1号-50号坑内。反之，则在51号-100号坑内。

这里的一个重要前提条件就是，只有一个坑里的萝卜数超过1。否则上面的推论是不成立的。


#### 代码
```java
class Solution {
    public int findDuplicate(int[] nums) {
        int n = nums.length-1;
        int lo = 1, hi = n;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            int count = 0;
            for (int num : nums) {
                if (num <= mid) { ++count; }
            }
            if (count > mid) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }
}
```

#### 结果
![find-duplicate-number-1](/images/leetcode/find-duplicate-number-1.png)


### 奇技淫巧：龟兔赛跑法
龟兔赛跑法之前被用在`Linked List Cycle II` -> <https://leetcode.com/problems/linked-list-cycle-ii/> 这道题里。

就是设置两个指针，一个`Walker`，一个`Runner`。
* walker每次走一步
* runner每次走两步

如果有`circle`最后他们会相遇在一点。然后这个算法，就是用来找出`circle`的起点。

#### 代码
```java
public class Solution {
    public int findDuplicate(int[] nums) {
        int fast = 0, slow = 0;
        do { fast = nums[nums[fast]];
             slow = nums[slow];
        } while (fast != slow);

        fast = 0;

        while(fast != slow) {
            fast = nums[fast];
            slow = nums[slow];
        }

        return slow;
    }
}
```

#### 结果
![find-duplicate-number-2](/images/leetcode/find-duplicate-number-2.png)


### 其他不可行的几种方法的代码

#### 朴素遍历，复杂度 $$O(n^2)$$
```java
public class Solution {
    public int findDuplicate(int[] nums) {
        for (int i = 0; i < nums.length-1; i++) {
            int a = nums[i];
            for (int j = i+1; j < nums.length; j++) {
                if (nums[j] == a) { return a; }
            }
        }
        return 0; // never reached
    }
}
```
#### 先排序，再查找，复杂度 $$O(n\log_{}{n})$$
```java
public class Solution {
    public int findDuplicate(int[] nums) {
        if (nums.length == 0) { return 0; }
        Arrays.sort(nums);
        int last = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == last) { return last; }
            last = nums[i];
        }
        return 0; // never reached
    }
}
```

#### 用`Set`容器检查重复。复杂度 $$O(n)$$
```java
public class Solution {
    public int findDuplicate(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int i : nums) {
            if (!set.add(i)) { return i; }
        }
        return 0;
    }
}
```

#### 分治法。复杂度 $$O(n^2)$$
因为`merge()`的复杂度是 $$O(n^2)$$。所以根据主定理，整体复杂度以每一步的$$O(n^2)$$为主导。

```java
public class Solution {
    public int findDuplicate(int[] nums) {
        if (nums.length == 0) { return 0; }
        return helper(nums,0,nums.length-1);
    }
    private int helper(int[] nums, int lo, int hi) {
        if (lo == hi) { return 0; }
        int mid = lo + (hi - lo) / 2;
        int left = helper(nums,lo,mid);
        if (left > 0) { return left; }
        int right = helper(nums,mid+1,hi);
        if (right > 0) { return right; }
        return merge(nums,lo,mid,mid+1,hi);
    }
    private int merge(int[] nums, int l1, int h1, int l2, int h2) {
        for (int i = l1; i <= h1; i++) {
            for (int j = l2; j <= h2; j++) {
                if (nums[i] == nums[j]) { return nums[i]; }
            }
        }
        return 0;
    }
}
```
