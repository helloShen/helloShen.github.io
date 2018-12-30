---
layout: post
title: "Leetcode - Algorithm - Maximum Sum Circular Subarray "
date: 2018-12-30 15:27:17
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "math"]
level: "medium"
description: >
---

### 题目
Given a circular array C of integers represented by A, find the maximum possible sum of a non-empty subarray of C.

Here, a circular array means the end of the array connects to the beginning of the array.  (Formally, C[i] = A[i] when 0 <= i < A.length, and C[i+A.length] = C[i] when i >= 0.)

Also, a subarray may only include each element of the fixed buffer A at most once.  (Formally, for a subarray C[i], C[i+1], ..., C[j], there does not exist i <= k1, k2 <= j with k1 % A.length = k2 % A.length.)

Example 1:
```
Input: [1,-2,3,-2]
Output: 3
Explanation: Subarray [3] has maximum sum 3
```

Example 2:
```
Input: [5,-3,5]
Output: 10
Explanation: Subarray [5,5] has maximum sum 5 + 5 = 10
```

Example 3:
```
Input: [3,-1,2,-1]
Output: 4
Explanation: Subarray [2,-1,3] has maximum sum 2 + (-1) + 3 = 4
```

Example 4:
```
Input: [3,-2,2,-3]
Output: 3
Explanation: Subarray [3] and [3,-2,2] both have maximum sum 3
```

Example 5:
```
Input: [-2,-3,-1]
Output: -1
Explanation: Subarray [-1] has maximum sum -1
```


Note:
* -30000 <= A[i] <= 30000
* 1 <= A.length <= 30000

### 暴力枚举所有子串`O(n^2)`
暴力解法的复杂度也不是很高。因为所有子串的空间是`O(n^2)`,`n`是数字个数。
```
lo和hi两个指针的位置能定义一个子串

lo      hi
 |      |
[0,5,8,-9,9,-7,3,-2]
```

马上能想到的一个小优化，就是提前把相邻的正整数累加起来，因为遇到正数是必取的。这样能缩短数组的长度。
```
相邻正整数，合并同类项

[0,5,8,-9,9,-7,3,-2] -> [13,-9,9,-7,3,-2]
```

#### 代码
```java
public class Solution {
    public int maxSubarraySumCircular(int[] A) {
        if (log.isDebugEnabled()) {
            log.debug("Simplified Array = {}", Arrays.toString(reduce(A)[0]));
        }
        int[][] reduced = reduce(A);
        if (reduced[1][0] <= 0) return reduced[1][0];
        return doJob(reduced[0]);
    }

    private int[][] reduce(int[] nums) {
        int[] sums = new int[nums.length];
        int max = Integer.MIN_VALUE;
        int p = 0;
        int i = 0;
        while (i < nums.length) {
            max = Math.max(max, nums[i]);
            int sum = nums[i++];
            while (i < nums.length && isSameSign(sum, nums[i])) {
                max = Math.max(max, nums[i]);
                sum += nums[i++];
            }
            sums[p++] = sum;
        }
        if (p > 1 && isSameSign(sums[0], sums[p - 1])) sums[0] += sums[--p];
        int[][] res = new int[2][];
        res[0] = Arrays.copyOfRange(sums, 0, p);
        res[1] = new int[]{max};
        return res;
    }

    private boolean isSameSign(int a, int b) {
        return !(a > 0 && b < 0) && !(a < 0 && b > 0);
    }

    private int doJob(int[] nums) {
        if (nums.length == 0) return 0;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i, sum = 0; j < i + nums.length; j++) {
                sum += nums[j % nums.length];
                max = Math.max(max, sum);
            }
        }
        return max;
    }
}
```

#### 结果
![maximum-sum-circular-subarray-1](/images/leetcode/maximum-sum-circular-subarray-1.png)


### 对“非循环最大子串”问题的推广
这题的前导问题是单纯求“一个数组中的最大累加和子串”。这个问题有一个很巧妙的贪婪法的解。简单讲就是：
> 遍历数组，不断累加所有遇到的数字，直到和小于零，则放弃之前的累加和，重新开始累加。

举个例子`[1,2,3,-2,-5,4,7]`，
```
累加到3一直是正整数，很愉快，和为6：

    1+2+3=6
       |
[1, 2, 3, -2, -5, 4, 7]

遇到-2，累加和6-2=4，还是>0，所以继续累加，

      1+2+3-2=4
          |
[1, 2, 3, -2, -5, 4, 7]

遇到-5，4-5=-1 < 0，此时要放弃之前的累加和。因为已经为负，不如从头开始累加。

          1+2+3-2-5=-1
              |
[1, 2, 3, -2, -5, 4, 7]

从4开始，从头开始累加，

                 4=4
                  |
[1, 2, 3, -2, -5, 4, 7]
```

这个方法可以通过一次遍历(`O(n)`复杂度)，找出最大子串和。

我们不妨以这个简单问题为基础来思考。现在这题无非加了一个条件：“循环数组（头尾相接）”。但仔细想只可能存在两种情况，
1. 最终结果不是循环的。这就和无循环数组求最大子串完全一致。
2. 最终结果是循环的（尾部的一部分，加上头部的一部分）。

但第二种情况，如果把数组折叠过来看：
> 最大子串 = total - 最小子串

![maximum-sum-circular-subarray-figure](/images/leetcode/maximum-sum-circular-subarray-figure-1.png)
图中蓝色部分代表“最大子串”，红色部分代表“最小子串”。当最大子串为循环的时候，红色的最小子串必定不是循环的。而最小子串，我们可以用求非循环最大子串的方法简单得到。

所以这个问题被完全重新指向到了前导“非循环最大子串”问题。

注意，有一个边角问题需要考虑，就是当所有数字都是负数，比如`[-1,-2,-3]`，会导致`total - minSum = 0`的情况出现，即图中的蓝色`maxSum`为空。但此时又必须至少取一个数字，哪怕是负数。

#### 代码
```java
class Solution {
    public int maxSubarraySumCircular(int[] A) {
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        int maxAccum = 0, minAccum = 0, total = 0;
        for (int n : A) {
            maxAccum += n;
            max = Math.max(max, maxAccum);
            if (maxAccum < 0) maxAccum = 0;
            minAccum += n;
            min = Math.min(min, minAccum);
            if (minAccum > 0) minAccum = 0;
            total += n;
        }
        if (total == min) return max; // all negative
        return Math.max(max, total - min);
    }
}
```

#### 结果
![maximum-sum-circular-subarray-2](/images/leetcode/maximum-sum-circular-subarray-2.png)
