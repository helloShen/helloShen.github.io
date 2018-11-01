---
layout: post
title: "Leetcode - Algorithm - Subarray Product Less Than K "
date: 2018-11-01 19:20:18
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "math", "two pointers"]
level: "medium"
description: >
---

### 题目
Your are given an array of positive integers nums.

Count and print the number of (contiguous) subarrays where the product of all the elements in the subarray is less than k.

Example 1:
```
Input: nums = [10, 5, 2, 6], k = 100
Output: 8
Explanation: The 8 subarrays that have product less than 100 are: [10], [5], [2], [6], [10, 5], [5, 2], [2, 6], [5, 2, 6].
Note that [10, 5, 2] is not included as the product of 100 is not strictly less than k.
```

Note:
* 0 < nums.length <= 50000.
* 0 < nums[i] < 1000.
* 0 <= k < 10^6.

### `two pointers`标准暴力过程
两个指针`lo`和`hi`，经过两个`for`循环遍历数组，可以暴力算出每个连续子串的积。复杂度`O(N^2)`。
```
10 * 5 * 2 = 100
 lo     hi
 |<---->|
[10, 5, 2, 6]
```

#### 代码
```java
class Solution {
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        int count = 0;
        for (int from = 0; from < nums.length; from++) {
            int prod = 1;
            for (int to = from; to < nums.length; to++) {
                prod *= nums[to];
                if (prod >= k) break;
                count++;
            }
        }
        return count;
    }
}
```

#### 结果
![subarray-product-less-than-k-1](/images/leetcode/subarray-product-less-than-k-1.png)


### 窗口法
上面的暴力算法每个人都会。窗口法可以少做一些计算。还是刚才的例子，假设`lo`指向`10`的时候，`hi`最大可以推进到`6`，在往下一个数字`3`就不满足`< 800`的条件了。
```
MAX = 800
当lo指向10时，hi最远推进到5

10 * 5 * 2 * 6 = 600 < 800
lo         hi
 |<------->|
[10, 5, 2, 6, 3, 4, 9, 7]
```

下一步把`lo`往前推进一个数字到`5`，但不需要完全重新计算，只需要除以之前的数字`10`，然后重新尝试找到`hi`最远的位置，
```
MAX = 800
当lo指向10时，hi最远推进到5

10 * 5 * 2 * 6 = 600 < 800
lo         hi
 |<------->|
[10, 5, 2, 6, 3, 4, 9, 7]
     |<--->|
    lo     hi
5 * 2 * 6 = 600 / 10 = 60 < 800

所以[5], [5, 2] 和 [5, 2, 6]三个子串是确定小于800的，直接计入，不需要重复计算。

尝试重新定位hi的最远位置，
[10, 5, 2, 6, 3, 4, 9, 7]
     |<--------->|
    lo           hi
5 * 2 * 6 * 3 * 4 = 720 < 800

此时再加入[5, 2, 6, 3] 和 [5, 2, 6, 3, 4]两个子串

重复以上过程
```

唯一需要注意一下的是单个数字都大于`k`的情况，这时`lo`会超过`hi`，这时候需要重置`hi`以及累计的`product`，全部清零。

#### 代码
```java
class Solution {
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k < 1) return 0;
        int lo = 0, hi = 0, prod = 1, count = 0;
        while (lo < nums.length) {
            if (lo < hi) {
                count += (hi - lo);
            } else {
                prod = 1;
                hi = lo;
            }
            while (hi < nums.length && prod * nums[hi] < k) {
                prod *= nums[hi++];
                count++;
            }
            prod /= nums[lo++];
        }
        return count;
    }
}
```

#### 结果
![subarray-product-less-than-k-2](/images/leetcode/subarray-product-less-than-k-2.png)
