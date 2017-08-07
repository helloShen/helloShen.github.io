---
layout: post
title: "Leetcode - Algorithm - Array Partition "
date: 2017-08-06 14:33:36
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 主要收获 - 面向断言编程
怎么写出Bug-Free的代码？一个很重要的方法，就是 **面向断言的编程**。第一次看到这个概念，是在《编程珠玑》。当时理解不了其中的精髓。现在有些体会。

> 每一步开始时，设置一些断言。然后在每一步结束之后，检查这些断言是否为真。

举个例子，**三向切分**。把一个数组分为`[<pivot, =pivot, >pivot]`三部分。快排就是对其中的`[<pivot]`和`[>pivot]`两部分递归。编程的过程中，肯定要维护两个指针`lt`和`gt`。分别指向这三部分的两个边界。

一个良好的实践是，给出`lt`和`gt`一个明确的定义，
* `lt`指向第一个等于pivot的元素。 `[lo,lt-1]`空间中的元素，都小于pivot。
* `gt`指向最后一个等于pivot的元素。 `[gt+1,hi]`空间中的元素，都大于pivot。

初始化指针的时候，严格遵照定义，
* int lt = lo; // 指向首元素（>pivot空间为空）
* int gt = hi; // 指向尾元素（<pivot空间为空）

在后续迭代的过程中，保证两个指针严格遵守定义。

好处好远不止这些。这两个断言被承诺之后，后续其他的控制条件，就可以以这两条断言为前提条件。所有的操作都可以面向这两条断言。而且确保是安全的。

这会让编程轻松很多。

至于怎么设置断言比较合理，这需要经验。

### 题目
Given an array of 2n integers, your task is to group these integers into n pairs of integer, say (a1, b1), (a2, b2), ..., (an, bn) which makes sum of min(ai, bi) for all i from 1 to n as large as possible.

Example 1:
Input: `[1,4,3,2]`
Output: 4

Explanation: n is 2, and the maximum sum of pairs is `4 = min(1, 2) + min(3, 4)`.

Note:
n is a positive integer, which is in the range of [1, 10000].
All the integers in the array will be in the range of [-10000, 10000].

### 基本思路
这是个数学题。可以先做一下小实验。比如试验一下差距很大的一组数，`[1,2,100,101]`。所以是分成`[1,2]`,`[100,101]`两组，结果是`1+100=101`。所以规律是：
> 让每个数都和刚好比它大一点点的数分为一组。

或者反过来说，
> 最大的数，要找第二大的数分成一组，尽可能利用它的空间。

所以解法很简单，
1. 对数组排序
2. 把相邻的两个数分为一组即可

### 用`Arrays.sort()`排序

#### 代码
```java
public class Solution {
    public int arrayPairSum(int[] nums) {
        Arrays.sort(nums);
        int cur = 0, sum = 0;
        while (cur < nums.length-1) {
            sum += nums[cur];
            cur += 2;
        }
        return sum;
    }
}
```

#### 结果
![array-partition-1](/images/leetcode/array-partition-1.png)


### 自己写快排

#### 代码
```java
public class Solution {
    public int arrayPairSum(int[] nums) {
        sort(nums,0,nums.length-1);
        int cur = 0, sum = 0;
        while (cur < nums.length-1) {
            sum += nums[cur];
            cur += 2;
        }
        return sum;
    }
    /* 标准三向切分的快排 */
    public void sort(int[] nums, int lo, int hi) {
        if (hi - lo < 1) { return; }
        int pivot = nums[hi];
        exchange(nums,lo,hi);
        int lt = lo; // [lo,lt-1]空间中的元素，都小于pivot （换句话说，lt指向第一个等于pivot的元素）
        int gt = hi; // [gt+1,hi]空间中的元素，都大于pivot （换句话说，gt指向最后一个等于pivot的元素）
        int cur = lt + 1;
        while (cur <= gt) {
            int n = nums[cur];
            if (n < pivot) {
                exchange(nums,lt++,cur++);
            } else if (n > pivot) {
                exchange(nums,gt--,cur);
            } else {
                ++cur;
            }
        }
        sort(nums,lo,lt-1);
        sort(nums,gt+1,hi);
    }
    /* 假设x,y都是合法的下标 */
    private void exchange(int[] nums, int x, int y) {
        int temp = nums[x];
        nums[x] = nums[y];
        nums[y] = temp;
    }
}
```

#### 结果
`Arrays.sort()`果然优化地很好。
![array-partition-2](/images/leetcode/array-partition-2.png)
