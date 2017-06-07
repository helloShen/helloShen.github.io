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

### 主要收获 - 设计算法的一个普遍性思路三步走
计算最大子串是个非常典型的问题。
```
31, -41, 59, 26 -53, 58, 97, -93, -23, 84
```

> 1. 一开始肯定老老实实考虑所有可能的情况：计算所有任意两点间的子串的和。

> 2. 分治动态规划的思路：计算子串和`sum[0,i]`的时候，看能不能从`sum[0,i-1]`的结果的基础上加上新元素`nums[i]`的捷径方法。

> 3. 前面两种方法都需要计算所有可能的子串的和。实际上不需要。`max[i]`可以根据子问题`max[i-1]`的结果计算出来：`max[0,i] = Math.max(maxsofar[0,i-1], maxendinghere[i])`，其中`maxendinghere[i] = Math.max(maxendinghere[i-1] + nums[i], 0)`。

### 题目
Find the contiguous subarray within an array (containing at least one number) which has the largest product.

For example, given the array `[2,3,-2,4]`,
the contiguous subarray `[2,3]` has the largest product = `6`.

### 最朴素计算所有`x`到`y`的`product[x,y]`
最笨的办法。复杂度 $$O(n^3)$$。就不写了。

### 表驱动的动态规划，复杂度 $$O(n^2)$$
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


### 复杂度 $$O(n)$$ 的方法：不用保留任意两个元素间的连乘结果
保留任意两点间的连乘的积，当然是一种很健壮的，具有普遍意义的解法。但保留所有可能的组合在这题里不是必须的。
可以参考找 **和最大** 的子串的`Maximum Subarray`问题。思路还是动态规划的思路：
> 每个问题都可以分解为: 一个规模更小的子问题，以及一个新加入的元素。

```
        已处理的子问题  新加入的元素     
                 |    |
31, -41, 59, 26 -53, 58, 97, -93, -23, 84
```
假设`[31, -41, 59, 26 -53]`是一个已经解决的子问题，最大子串是`[59,26]=85`。面对新加入的`58`元素，只需要比较 **所有包含`58`元素的新子串能不能大于85？**

如果我们知道`[31, -41, 59, 26 -53]`中所有包含`-53`的子串的最大值，就能在 **O(1)** 时间内算出包含`58`元素的最大子串。

![maximum-product-subarray](/images/leetcode/maximum-product-subarray)

对于加入的新元素`nums[i]`，新的最大值可以根据子问题的`maxsofar`和`maxendinghere`两个历史信息直接计算得到，

> max[0,i] = Math.max(maxsofar[0,i-1], maxendinghere[i])

其中，

> maxendinghere[i] = Math.max(maxendinghere[i-1] + nums[i], 0)   // 注：小于零，就归零。

但这里要求的是最大 **乘积**，情况有点不同，不能光考虑最大值`maxendinghere`。还需要记录最小值`minendinghere`。因为如果当前元素`nums[i] < 0`，之前的最小值如果是负数，马上就会变成最大值。


还是拿`[2,3,-2,4]`举例，
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
