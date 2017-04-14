---
layout: post
title: "Leetcode - Algorithm - Maximum Subarray "
date: 2017-04-13 19:29:05
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","dynamic programming","divide and conquer"]
level: "easy"
description: >
---

### 主要收获1 - 动态规划思想的变种
这题虽然是`Easy`难度，但收获很大。最主要又实际运用了一下动态规划思想。

### 主要收获2 - 实践分治法
虽然这题分治法并不是最好的解法，但作为一种非常重要的思想，也实践了一下。

### 主要收获3 - 明确了什么情况不适合递归
一般如果不是$$\log_{}{n}$$的深度，而是$$n$$深度的话，用递归就要权衡一下了。尤其是算法过程中使用的局部变量的规模比较大的时候，就不应该用递归。还是要考虑到栈的容量非常有限，容易栈溢出。这题尤其典型，$$n$$的递归深度，参数里还有一个大数组。

### 题目
Find the contiguous subarray within an array (containing at least one number) which has the largest sum.

For example, given the array `[-2,1,-3,4,-1,2,1,-5,4]`,
the contiguous subarray `[4,-1,2,1]` has the largest sum = 6.

### 暴力$$O(n^2)$$

#### 代码
```java
public class Solution {
    public int maxSubArray(int[] nums) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (sum > max) { max = sum; }
            }
        }
        return max;
    }
}
```

#### 结果
超时。
![maximum-subarray-1](/images/leetcode/maximum-subarray-1.png)


### 丢弃所有累计小于等于零的子串 $$O(n)$$
所以基本原理就是把任何当前子串看成是后面元素的 **前缀**。 **如果这个前缀小于零，就是没有帮助的，就可以丢弃。**

还是考虑`[-2,1,-3,4,-1,2,1,-5,4]`这个例子。只需要维护一个指针，
从首元素开始，`-2 < 0`，所以`-2 + 1 < 1`，也就是`-2`这个前缀，对后面的子串没有任何帮助，只有负作用。所以只需要记下`-2`这个当前最大值，就可以丢弃`-2`这个元素。

接下来从下个元素`1`开始，`1 > 0`，所以`1 + (-3) > -3`，比直接从下一个元素起始要好，所以可以保留`1`。

指针接着往下走到`-3`，`1 - 3 > 0`，也就是`[1,-3]`这个前缀对后面的子串没有帮助，可以丢弃。

指针再从下一个元素`4`开始。`4 > 0`，保留`4`。`4 - 1 > 0`，保留`[4,-1]`前缀。`4 - 1 + 2 > 0`，保留`[4,-1,2]`前缀。直到最后。

#### 代码
```java
public class Solution {
    public int maxSubArray(int[] nums) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; ) {
            int cursor = i, temp = 0;
            while (cursor < nums.length) {
                temp += nums[cursor++];
                max = Math.max(max,temp);
                if (temp < 0) { break; }
            }
            i = cursor;
        }
        return max;
    }
}
```

#### 动态规划思想
把问题抽象成一个遗产问题，用到了 **动态规划（Dynamic Programming)** 的思想。把之前数字之和看成是一种遗产`heritage`，如果`heritage > 0`，说明对下面的累加是有帮助的，所以保留遗产。如果`heritage <= 0`，遗产为负数，那就是累赘，还不如扔掉包袱，从零开始。

> 动态规划的核心思想就是：把当前最优解看成是历史最优解和当下选择的组合。

问题有个良好的抽象之后，再写代码，就会简洁很多。
```java
public class Solution {
    public int maxSubArray(int[] nums) {
        int heritage = 0, max = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            int assets = heritage + nums[i];
            max = Math.max(max,assets);
            heritage = Math.max(assets,0); //遗产为负，就是累赘，不如扔掉历史包袱
        }
        return max;
    }
}
```
这里不推荐递归版，因为这个递归的深度不是$$\log_{}{n}$$，而是$$n$$。容易栈溢出。
```java
public class Solution {
    public int maxSubArray(int[] nums) {
        int[] max = new int[]{ Integer.MIN_VALUE };
        dp(0,max,nums,0);
        return max[0];
    }
    public void dp(int heritage, int[] max, int[] nums, int cursor) { // nums[]数组作为参数是要入栈的，会导致stackoverflow
        if (cursor == nums.length) { return; }
        int assets = heritage + nums[cursor];
        max[0] = Math.max(max[0],assets);
        heritage = Math.max(assets,0);
        dp(heritage,max,nums,++cursor);
    }
}
```

#### 结果
第一版的$$O(n)$$
![maximum-subarray-2](/images/leetcode/maximum-subarray-2.png)

抽象优化后的$$O(n)$$
![maximum-subarray-3](/images/leetcode/maximum-subarray-3.png)

### 分治法 + 回溯算法，复杂度$$O(n^2)$$
以`[1,2,3,4]`为例每层递归分为三部分，
1. 左边`[1,2]`
2. 右边`[3,4]`
3. 中间合并`[1,2,3,4]`，以`(2,3)`为核心，利用回溯算法向两边扩展的所有组合。

因为第三步的复杂度至少是$$O(n^2)$$，所以，
> $$T(n) = 2 * T(n/2) + O(n^2)$$

根据主定理的`case 1`，复杂度以$$O(n^2)$$为主导，总体复杂度为$$O(n^2)$$。

注意这里第三步，不能用`Two Pointer`的思想，向两边扩散的时候，只选取较大的一遍扩散。因为这里最后的最优解，不保证一路走来每部都是局部最优解。

#### 代码

```java
public class Solution {
    public int maxSubArray(int[] nums) {
        if (nums.length == 0) { return 0; }
        return dac(nums,0,nums.length-1);
    }
    public int dac(int[] nums, int low, int high) {
        if (low == high) { return nums[low]; }
        int mid = low + (high - low) / 2; // 下位中位数
        int left = dac(nums,low,mid); // 左半边最大值
        int right = dac(nums,mid+1,high); // 右半边最大值
        int candidate1 = Math.max(left,right);
        int kernel = nums[mid] + nums[mid+1];
        int candidate2 = fromMiddle(nums,low,high,mid,mid+1,kernel,kernel); //中间合并区的最大值
        return Math.max(candidate1,candidate2);
    }
    public int fromMiddle(int[] nums, int left, int right, int low, int high, int soFar, int max) {
        if (low == left && high == right) { return max; }
        int leftMax = Integer.MIN_VALUE;
        if (left < low) {
            int newSoFar = soFar+nums[low-1];
            int newMax = Math.max(max, newSoFar);
            leftMax = fromMiddle(nums,left,right,low-1,high,newSoFar,newMax);
        }
        int rightMax = Integer.MIN_VALUE;
        if (right > high) {
            int newSoFar = soFar+nums[high+1];
            int newMax = Math.max(max,newSoFar);
            rightMax = fromMiddle(nums,left,right,low,high+1,newSoFar,newMax);
        }
        return Math.max(leftMax,rightMax);
    }
}
```

#### 结果
已经预计到分治法$$O(n^2)$$的复杂度，超时属于正常。
![maximum-subarray-4](/images/leetcode/maximum-subarray-4.png)
