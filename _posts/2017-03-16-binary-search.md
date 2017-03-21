---
layout: post
title: "Binary Search in Java"
date: 2017-03-16 14:40:40
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["binary search","sort"]
description: >
---

### 二分法查找
二分法思路很简单：
> 每次都和中位数比较大小，如果目标数大于中位数，则丢弃所有中位数之前的数字（包括中位数）。反之，则丢弃中位数和其之后的所有数字。

![binary-search](/images/binary-search/binary-search.gif)

### 二分法查找中几个重要的变量
用二分法之前，先理清几个数学概念。 对于任意一个有序互异数组，

* 最小下标：min = 0
* 最大下标：max = length - 1
* 数组长度：length = max - min + 1;
* 如果数组长度是奇数：
    * 中位数：median = (length - 1) / 2
* 如果数组长度是偶数：
    * 下位中位数：median = (length - 2) / 2
    * 上位中位数：median = length / 2
* 不论数组长度是奇数还是偶数，下位中位数的一般表达式为：
    * median = floor[(length - 1) / 2] (向下取整)

以下面这个数组为例，
```
[2,3,4,7,8,9]
```
首元素`2`的下标为`0`。末尾元素`9`的下标为`5`。数组长度为：`5-0+1 = 6`。下位中位数`4`的下标为：`(6-1)/2 = 2`。


### 代码
界定好数学概念之后，下面就是一个递归版的二分查找的代码。给出一个目标数字，和一个数组。返回这个数字在目标数组中的位置（下标）。数学概念清楚之后，代码就很干净，不会出bug。

**需要注意**： 因为每次比较之后，是连带中位数一起丢弃。所以，basecase是当高位和低位两个指针交叉互换顺序时，表示搜索结束。
```java
// 有序数列的递归二分查找
// 返回目标数字在数组中的下标。如果没找到，就返回-1。
public static int indexOf(int num, int[] array, int min, int max) {
    // base case
    if (max < min) { return -1; } // return -1 if not found

    // 严格的下位中位数数学定义。不论数组长度为奇数还是偶数。
    int length = max - min + 1;
    int median = min + ( (length - 1) / 2 );

    // 尾递归
    if (array[median] > num) {
        return indexOfV2(num, array, min, median-1); // num小于中位数，丢弃中位数及所有大于中位数的数
    } else if (array[median] < num) {
        return indexOfV2(num, array, median+1, max); // num大于中位数，丢弃中位数及所有小于中位数的数
    } else { // array[median] == num  //找到了
        return median;
    }
}
```
