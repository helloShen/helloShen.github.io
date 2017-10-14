---
layout: post
title: "Leetcode - Algorithm - Longest Increasing Subsequence "
date: 2017-10-14 17:03:04
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","dynamic programming"]
level: "medium"
description: >
---

### 题目
Given an unsorted array of integers, find the length of longest increasing subsequence.

For example,
Given `[10, 9, 2, 5, 3, 7, 101, 18]`,
The longest increasing subsequence is `[2, 3, 7, 101]`, therefore the length is `4`. Note that there may be more than one LIS combination, it is only necessary for you to return the length.

Your algorithm should run in $$O(n2)$$ complexity.

Follow up: Could you improve it to O(n log n) time complexity?

### 动态规划
以`[10, 9, 2, 5, 3, 7, 101, 18]`为例，从最末尾的`18`开始，当只有一个元素时，长度等于`1`.
```bash
Sub(18)     = [18]                    = 1
Sub(101)    = [101,18]                = 1
Sub(7)      = [7,101,18]              = Max(Sub(18),Sub(101)) + 1 = 2
Sub(3)      = [3,7,101,18]            = Max(Sub(18),Sub(101),Sub(7)) + 1 = 3
...
...
```
所以递归式：
> T(n)等于[T(0)~T(n-1)]中所有 nums[k] > nums[n] 的子问题的最大值+1。

根据这个递归式，可以在 $$O(n^2)$$ 时间内解决问题。

#### 代码
```java
class Solution {
    public int lengthOfLIS(int[] nums) {
        if (nums.length == 0) { return 0; }
        int[] dp = new int[nums.length];
        dp[nums.length-1] = 1;
        int max = 1;
        for (int i = nums.length-2; i >= 0; i--) {
            dp[i] = 1;
            for (int j = i+1; j < nums.length; j++) {
                if (nums[j] > nums[i]) { dp[i] = Math.max(dp[i],dp[j]+1); }
            }
            max = Math.max(max,dp[i]);
        }
        return max;
    }
}
```

#### 结果
![longest-increasing-subsequence-1](/images/leetcode/longest-increasing-subsequence-1.png)


### 用二分查找维护一个“最小尾数”数组
首先对于一个升序序列，如果我们希望它尽可能地长，有脑子的人都会 **尽可能地让路线上的每个数字都尽可能小**，比如我们有`[1,3,2,4]`，尽管`[1,2,4]`和`[1,3,4]`两条路线都合法，且长度相等，但我们认为`[1,2,4]`比`[1,3,4]`更优，因为路径上的数字`2 < 3`。如果有一组新的数字`[1,3,2,3,4]`，最优解`[1,2,3,4]`包含了子路经`[1,2,4]`而不是`[1,3,4]`。此为“最小尾数”解法的逻辑前提，
> "最小尾数"数组就是让升序序列中的每一个数都尽可能地小。当新加入一个数，如果这个数能扩展最长升序路径的话，这个扩展在最小尾数数组上一定可行。

更新最小数组的规则只有两条，
* 如果新加入的数大于当前“最小尾数”数组最后一个数，就把新的数插入到“最小尾数”数组的末尾。
* 如果新加入的数介于“最小尾数”数组覆盖的范围内，则更新数组内大于它的最小的那个数字。

下面看`[10,9,2,5,3,7,101,18]`这个数组更新最小尾数数组的过程，
```
[10]                    -> 插入末尾: [10]
[10,9]                  -> 更新10: [9]
[10,9,2]                -> 更新9: [2]
[10,9,2,5]              -> 插入末尾: [2,5]
[10,9,2,5,3]            -> 更新5: [2,3]
[10,9,2,5,3,7]          -> 插入末尾: [2,3,7]
[10,9,2,5,3,7,101]      -> 插入末尾: [2,3,7,101]
[10,9,2,5,3,7,101,18]   -> 更新101: [2,3,7,18]
```

不难看出寻找“更新/插入”点的操作实际上可以用一个 $$O(\log_{}{n})$$ 的二分查找完成。所以整个过程的复杂度就缩减为 $$O(n\log_{}{n})$$。 具体代码如下，

#### 代码
```java
class Solution {
    public int lengthOfLIS(int[] nums) {
        int[] tails = new int[nums.length];
        int cur = 0;
        for (int num : nums) {
            int lo = 0, hi = cur - 1;
            while (lo <= hi) {
                int mid = lo + (hi - lo) / 2;
                if (tails[mid] < num) {
                    lo = mid + 1;
                } else if (tails[mid] > num) {
                    hi = mid - 1;
                } else {
                    lo = mid; break;
                }
            }
            if (lo == cur) {
                tails[cur++] = num;
            } else {
                tails[lo] = num;
            }
        }
        return cur;
    }
}
```

#### 结果
![longest-increasing-subsequence-2](/images/leetcode/longest-increasing-subsequence-2.png)
