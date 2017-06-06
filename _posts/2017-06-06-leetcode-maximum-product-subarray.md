---
layout: post
title: "Leetcode - Algorithm - Maximum Product Subarray "
date: 2017-06-06 16:27:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","array"]
level: "medium"
description: >
---

### 题目
Find the contiguous subarray within an array (containing at least one number) which has the largest product.

For example, given the array `[2,3,-2,4]`,
the contiguous subarray `[2,3]` has the largest product = `6`.

### 表驱动的动态规划
维护一个二维数组，原始数组中以任意两个元素为起点和终点的一个连乘窗口的结果全部记录在这个二维数组中。比如目标数组`[2,3,-2,4]`，这个二维数组如下，
```
    2   3   -2  4
2   2   6   -12 -48
3   0   3   -6  -24
-2  0   0   -2  -8
4   0   0   0   4
```
找到这个数组中的最大元素即可。

#### 代码
```java
public class Solution {
    public int maxProduct(int[] nums) {
        if (nums.length == 0) { return 0; }
        int max = Integer.MIN_VALUE;
        int[][] memo = new int[nums.length][nums.length];
        for (int i = nums.length-1; i >= 0; i--) {
            for (int j = nums.length-1; j > i; j--) {
                memo[i][j] = nums[i] * memo[i+1][j];
                max = Math.max(max,memo[i][j]);
            }
            memo[i][i] = nums[i];
            max = Math.max(max,memo[i][i]);
        }
        return max;
    }
}
```

#### 结果
算法是正确的，时间上没通过。
![maximum-product-subarray-1](/images/leetcode/maximum-product-subarray-1.png)


### 不用保留任意两个元素间的连乘结果，只需保留`max`和`min`即可
这里有个小窍门，还是拿`[2,3,-2,4]`举例，

* 假设`3,-2,4`这个子问题的最大值已经求得，现在加进来`2`元素。
* 只存在两种情况：
    * 有新元素`2`参与的连乘: 要么只有一个`2`，后面不接`3,-2,4`，要么后面接`3-2-4`。
    * 没有新元素`2`参与的连乘法: 只取决于`3,-2,4`的历史最大值。

#### 代码
因为是自底向上过来，都不用用数组当备忘录。直接 $$O(1)$$ 空间就够了。
```java
public class Solution {
    public int maxProduct(int[] nums) {
        if (nums.length == 0) { return 0; }
        int maxRes = nums[nums.length-1];
        int maxSub = maxRes, minSub = maxRes;
        for (int i = nums.length-2; i >= 0; i--) {
            int val = nums[i];
            if (val >= 0) {
                maxSub = Math.max(val,val*maxSub);
                minSub = Math.min(val,val*minSub);
            } else if (val < 0) {
                int tempMax = Math.max(val,val*minSub);
                minSub = Math.min(val,val*maxSub);
                maxSub = tempMax;
            }
            maxRes = Math.max(maxRes,maxSub);
        }
        return maxRes;
    }
}
```

#### 结果
银弹！
![maximum-product-subarray-2](/images/leetcode/maximum-product-subarray-2.png)
