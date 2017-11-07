---
layout: post
title: "Leetcode - Algorithm - Maximum Product Of Three Numbers "
date: 2017-11-06 19:28:59
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","array"]
level: "easy"
description: >
---

### 题目
Given an integer array, find three numbers whose product is maximum and output the maximum product.

Example 1:
```
Input: [1,2,3]
Output: 6
```
Example 2:
```
Input: [1,2,3,4]
Output: 24
```
Note:
* The length of the given array will be in range [3,104] and all elements are in the range [-1000, 1000].
* Multiplication of any three numbers in the input won't exceed the range of 32-bit signed integer.

### 思路
首先这个问题是要考虑负数的，因为负负得正，是有可能逆袭的。

然后不要慌，先考虑正常情况是什么结果，再考虑什么情况可能逆袭？思考这个问题之前，我们需要一个有序的数组，无序的数组无法分析（后面讨论的数组都是有序数组）。

先考虑都是正数的情况，
```
[1,2,3,4,5,6,7,8,9]
```
肯定是取3个最大的数：`7*8*9`。

然后无论有多少个负数，只要最大的3个数都是正数，结果依然是取最大的3个数：`7*8*9`
```
[-6,-5,-4,-3,-2,-1,7,8,9]
```

然后当第3大的数是负数时，末尾的两个最小的负数是可以逆袭的，结果取`-7*-6*9`。
```
[-7,-6,-5,-4,-3,-2,-1,8,9]
```

当只有最大的数是正数的时候，依然是末尾最小的两个负数逆袭，结果取`-8*-7*9`。
```
[-8,-7,-6,-5,-4,-3,-2,-1,9]
```

最后当所有书都是负数时，结果又变回取最大的三个数：`-3*-2*-1`。
```
[-9,-8,-7,-6,-5,-4,-3,-2,-1]
```

所以问题的关键是：
> 最小的两个数，和最大的三个数。

### 先排序，$$O(n\log_{}{n})$$
所以最简单的做法就可以先给数组排序，得到最大的3个数，和最小的2个数。

#### 代码
```java
class Solution {
    public int maximumProduct(int[] nums) {
        Arrays.sort(nums);
        return Math.max(nums[0]*nums[1]*nums[nums.length-1], nums[nums.length-3]*nums[nums.length-2]*nums[nums.length-1]);
    }
}
```

#### 结果
![maximum-product-of-three-numbers-1](/images/leetcode/maximum-product-of-three-numbers-1.png)


### 不排序，$$O(n)$$得到最大和最小的几个数
其实不用排序，只要遍历数组，然后一直维护最大3个数和最小2个数即可。

#### 代码
```java
class Solution {
    public int maximumProduct(int[] nums) {
        int[] min = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
        int[] max = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
        for (int num : nums) {
            if (num < min[1]) {
                min[1] = num;
                if (num < min[0]) { exch(min,0,1); }
            }
            if (num > max[2]) {
                max[2] = num;
                if (num > max[1]) { exch(max,1,2); }
                if (num > max[0]) { exch(max,0,1); }
            }
        }
        return Math.max(min[0]*min[1]*max[0],max[2]*max[1]*max[0]);
    }
    public void exch(int[] nums, int x, int y) {
        int temp = nums[x];
        nums[x] = nums[y];
        nums[y] = temp;
    }
}
```

#### 结果
![maximum-product-of-three-numbers-2](/images/leetcode/maximum-product-of-three-numbers-2.png)
