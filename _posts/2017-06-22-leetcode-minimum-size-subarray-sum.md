---
layout: post
title: "Leetcode - Algorithm - Minimum Size Subarray Sum "
date: 2017-06-22 04:23:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","two pointers"]
level: "medium"
description: >
---

### 题目
Given an array of n positive integers and a positive integer s, find the minimal length of a contiguous subarray of which the sum ≥ s. If there isn't one, return 0 instead.

For example, given the array `[2,3,1,2,4,3]` and `s = 7`,
the subarray `[4,3]` has the minimal length under the problem constraint.

click to show more practice.

More practice:
If you have figured out the $$O(n)$$ solution, try coding another solution of which the time complexity is $$O(n\log_{}{n})$$.

### 基本思路
不要小看这题虽然简单，但细琢磨非常非常好玩！因为这题用最常用的几种解题范式都可以解。下面是4种完全不同的解法，
1. 贪婪`Two Pointers`
2. 二分查找
3. 动态规划
4. 分治法

以上四种最常见的解题范式，通常各自用来解某些特定类型的问题。但这题在这几个思路上都沾边。

### 解法一：贪婪`Two Pointers`，复杂度 $$O(n)$$
用两个指针`slow`和`fast`维护一个窗口。四种方法中的最优解。

#### 代码
```java
public class Solution {
    public int minSubArrayLen(int s, int[] nums) {
        int len = 0, minLen = 0;
        int sum = 0;
        for (int slow = 0, fast = 0; fast < nums.length; fast++) {
            sum+= nums[fast];
            len++;
            if (sum >= s) {
                minLen = (minLen == 0)? len : Math.min(len,minLen);
                while (slow <= fast) {
                    int forward = sum - nums[slow];
                    if (forward >= s) {
                        slow++;
                        len--;
                        sum = forward;
                    } else {
                        break;
                    }
                }
                minLen = Math.min(minLen,len);
            }
        }
        return minLen;
    }
}
```

#### 结果
![minimum-size-subarray-sum-1](/images/leetcode/minimum-size-subarray-sum-1.png)


### 解法二：Binary Search，复杂度 $$O(n\log_{}{n})$$
四种方法中，最巧妙的一种。如果利用原始的`nums[]`数组，是没办法在 $$O(n\log_{}{n})$$ 时间里完成二分查找的。因为每次取中位数`mid`，必须计算`[lo,mid]`区间所有元素的和。求和的步骤本身就是 $$O(n)$$ 复杂度。

这里巧妙地创建一个新数组，储存到到某一位位置 **累加的和** 。比如，数组`[2,3,1,2,4,3]`，转换成累加数组为，
```
    [2,  3,  1,  2,  4,   3]
     |   |   |   |   |    |
[0,->2,->5,->6,->8,->12,->15]
```

这样的好处是，如果要求`[lo,hi]`区间所有元素的和，只需要一次计算 `cumulate[hi] - cumulate[lo-1]`。

#### 代码
```java
/**
 * Binary Search O(nlogn)
 */
public class Solution {
    public int minSubArrayLen(int s, int[] nums) {
        // cumulate sum
        int[] cumulate = new int[nums.length+1];
        for (int i = 0; i < nums.length; i++) {
            cumulate[i+1] = cumulate[i] + nums[i];
        }
        // 计算从每个节点开始的最小窗口
        int minLen = 0, len = 0;
        for (int i = 1; i <= nums.length; i++) {
            int lo = i, hi = nums.length, pre = i-1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                int diff = cumulate[mid] - cumulate[pre];
                if (diff < s) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
            len = (lo > nums.length)? 0 : lo - pre;
            if (len > 0) {
                minLen = (minLen == 0)? len : Math.min(minLen,len);
            }
        }
        return minLen;
    }
}
```

#### 结果
![minimum-size-subarray-sum-2](/images/leetcode/minimum-size-subarray-sum-2.png)


### 解法三：Dynamic Programming，复杂度 $$O(n*m)$$，`m`是窗口均值
这个问题也是可以抽象成：**从一个已解决的子问题一步步归纳上来** 的模型。这就是典型的自底向上的动态规划的做法。

对于任何一个`T(n)`，已知`T(n+1)`的解，
1. 如果`T(n+1)`加一起都不够大，那就把`nums[n] + sum[n+1,end]`加起来看看够不够大。
2. 如果`T(n+1)`有某个窗口加起来够大，就从`nums[n]`开始开个窗口，看看能不能比`T(n+1)`的最小窗口小。

#### 代码
```java
/**
 * Dynamic Programming
 */
public class Solution {
    public int minSubArrayLen(int s, int[] nums) {
        int minLen = 0, sum = 0;
        for (int i = nums.length-1; i >= 0; i--) {
            if (minLen == 0) { // sum < target number
                sum += nums[i];
                if (sum >= s) {
                    int newTail = nums.length;
                    do {
                        sum -= nums[--newTail];
                    } while (sum >= s);
                    minLen = newTail - i + 1;
                }
            } else {
                int localSum = 0;
                for (int j = i; j < i+minLen-1; j++) {
                    localSum += nums[j];
                    if (localSum >= s) { minLen = j-i+1; }
                }
            }
        }
        return minLen;
    }
}
```

#### 结果
![minimum-size-subarray-sum-3](/images/leetcode/minimum-size-subarray-sum-3.png)


### 解法四：Divede & Conquer，复杂度 $$O(n\log{}{n})$$
分治法，最典型的就是切成左右两部分，然后再合并。比如有`[2,3,1,2,4,3]`，
```
int[] nums = [2,3,1,2,4,3], target = 7

分成左右两个子块，
left: [2,3,1]  >>> minLeft = 0
right:[2,4,3]  >>> minRight = 2

最后合并起来，检查以[l,r]为核心的子串，能不能得到更小的窗口？
     l r
     | |
[2,3,1,2,4,3] >>> minMiddle = 3
```

#### 代码
```java
/**
 * Divide and Conquer O(nlogn)
 */
public class Solution {
    public int minSubArrayLen(int s, int[] nums) {
        if (nums.length == 0) { return 0; }
        return recursive(s,nums,0,nums.length-1);
    }
    public int recursive(int s, int[] nums, int lo, int hi) {
        if (lo == hi) { return (nums[lo] >= s)? 1 : 0; }
        int mid = lo + (hi - lo) / 2;
        int left = recursive(s,nums,lo,mid);
        int right = recursive(s,nums,mid+1,hi);
        int minLen = Math.min(left,right);
        if (left == 0) { minLen = right; }
        if (right == 0) { minLen = left; }
        // merge left & right
        int l = mid, r = mid+1, middleSize = 2, limit = (minLen == 0)? Integer.MAX_VALUE : minLen;
        int middleSum = nums[l] + nums[r];
        middleSize = middleRecursion(s,nums,lo,hi,mid,mid+1,limit,middleSum);
        int ret = Math.min(minLen,middleSize);
        if (minLen == 0) { ret = middleSize; }
        if (middleSize == 0) { ret = minLen; }
        return ret;
    }
    public int middleRecursion(int s, int[] nums, int lo, int hi, int l, int r, int limit, int localSum) {
        int size = r-l+1;
        if (localSum >= s) { return size; }
        int minLeft = 0, minRight = 0;
        if (l > 0 && l > lo && size < limit) { minLeft = middleRecursion(s,nums,lo,hi,l-1,r,limit,localSum+nums[l-1]); }
        if (r < nums.length && r < hi && size < limit) { minRight = middleRecursion(s,nums,lo,hi,l,r+1,limit,localSum+nums[r+1]); }
        int minLen = Math.min(minLeft,minRight);
        if (minLeft == 0) { minLen = minRight; }
        if (minRight == 0) { minLen = minLeft; }
        return minLen;
    }
}
```

#### 结果
分治法复杂度不高，但算法上，递归里套嵌递归，机器表示压力很大。
![minimum-size-subarray-sum-4](/images/leetcode/minimum-size-subarray-sum-4.png)
