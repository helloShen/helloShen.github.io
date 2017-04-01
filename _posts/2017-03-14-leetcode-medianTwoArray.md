---
layout: post
title: "Leetcode - Algorithm - Median of Two Sorted Arrays"
date: 2017-03-14 16:47:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["sort","binary search","array","divide and conquer"]
level: "hard"
description: >
---

### 题目
There are two sorted arrays nums1 and nums2 of size m and n respectively.

Find the median of the two sorted arrays. The overall run time complexity should be O(log (m+n)).

Example 1:
```
nums1 = [1, 3]
nums2 = [2]
```
The median is 2.0

Example 2:
```
nums1 = [1, 2]
nums2 = [3, 4]
```
The median is (2 + 3)/2 = 2.5

### 朴素解法
朴素解法直接遍历，复杂度是`O(n)`，不是`O(log(m+n))`。

思路就是朴素的`O(n)`遍历归并两列数组的算法。维护两个指针，分别指着两个数组。比较两个指针分别指向的两个数字，数字比较小的那个指针前移一位。如果一个数组比较短提前用完，就用`Integer.MAX_VALUE`临时填充。有一个全局计数器，只比较总长度一半的次数，就肯定能拿到我们要的计算中位数的数字了。

如果有奇数个数字，直接取中间那个数字。如果有偶数个数字，返回中间两个数字的平均值。

```java
public class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int totalLength = nums1.length + nums2.length;
        if (totalLength == 0) { // 长度为0特殊处理
            return 0.0d;
        }
        if (totalLength == 1) { // 长度为1特殊处理
            if (nums1.length == 1) {
                return (double)nums1[0];
            } else {
                return (double)nums2[0];
            }
        }
        // 下面开始两个数组长度总和 >= 2
        int index1 = 0;
        int index2 = 0;
        int num1 = 0;
        int num2 = 0;
        int cursor = 0; // 当前窗口
        int[] candidate = new int[2]; // 不管长度是奇数还是偶数，先把两个候选数取出来
        for (int i = 0; i <= totalLength/2; i++) {
            if (index1 < nums1.length) {
                num1 = nums1[index1];
            } else {
                num1 = Integer.MAX_VALUE;
            }
            if (index2 < nums2.length) {
                num2 = nums2[index2];
            } else {
                num2 = Integer.MAX_VALUE;
            }
            if (num1 < num2) {
                cursor = num1;
                index1++;
            } else {
                cursor = num2;
                index2++;
            }
            if (i == (totalLength/2 -1)) {
                candidate[0] = cursor;
            }
            if (i == (totalLength/2)) {
                candidate[1] = cursor;
            }
        }
        // 根据总长度是奇数还是偶数，用候选数计算出结果。
        if (totalLength%2 == 0) { // 长度是偶数：取两个候选数的平均数
            return ((double)candidate[0] + (double)candidate[1]) / 2;
        } else { // 长度是奇数：取两个候选数中的后者
            return (double)candidate[1];
        }
    }
}
```

#### 结果
虽然复杂度是`O(n)`，不是`O(log(m+n))`，但已经通过了。

![median-two-array-1](/images/leetcode/median-two-array-1.png)


### 复杂度为O(log(m) * log(n))的二分法查找

#### 复习二分法查找
用二分法之前，几个数学概念要理清楚。 假设我有下面这个有序互异数组，
```
[2,3,4,7,8,9]
```
* 最小下标：min = 0
* 最大下标：max = 5
* 数组长度：length = max - min + 1;
* 如果数组长度是奇数：
    * 中位数：median = (length - 1) / 2
* 如果数组长度是偶数：
    * 下位中位数：median = (length - 2) / 2
    * 上位中位数：median = length / 2
* 不论数组长度是奇数还是偶数，下位中位数的一般表达式为：
    * median = floor[(length - 1) / 2] (向下取整)

看上面这个例子，首元素`2`的下标为`0`。末尾元素`9`的下标为`5`。数组长度为：`5-0+1 = 6`。下位中位数`4`的下标为：`(6-1)/2 = 2`。

界定好数学概念之后，下面就是一个很干净的递归二分查找的代码。
```java
// 有序数列的递归二分查找
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

#### 二分法代码
思路是：
> 取第一个数组的中位数，算出它在第二个数组中的位置。
> 把这个中位数在第一个数组的位置加上它在第二个数组中的位置，可以知道它的总体位置。
> 如果大于或者小于总体中位数，就可以分别丢弃两个数组中接近一半的元素。
> 当两个数组总元素小于4个时，用朴素的线性归并法算出结果。

还是用的递归法，代码非常复杂，参数太多。平时不可能这样写代码。主要为了测试一下自己注意力，以及细心程度的极限。

```java
public class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) { // 保证数组1比数组2短
            return findMedianSortedArrays(nums2,nums1);
        }
        int mid1 = 0;
        int mid2 = 0;
        if ((nums1.length + nums2.length)%2 == 0) {
            mid1 = ((nums1.length + nums2.length)-2)/2;
            mid2 = (nums1.length + nums2.length)/2;
        } else {
            mid1 = ((nums1.length + nums2.length)-1)/2;
            mid2 = ((nums1.length + nums2.length)-1)/2;
        }
        if ((nums1.length + nums2.length) <= 4) {
            return simpleFindTarget(nums1, nums2, 0, nums1.length-1, 0, nums2.length-1, mid1, mid2);
        } else {
            return findTarget(nums1, nums2, 0, nums1.length-1, 0, nums2.length-1, mid1, mid2);
        }
    }

    /**
     * 递归舍弃部分元素
     * low1,high1代表数组1的当前有效范围
     * low2,high2代表数组2的当前有效范围
     * mid1,mid2是两个目标中位数。当数组长度为奇数时，mid1=mid2
     * 整个过程low1,high1,low2,high2是绝对下标。
     * median和index是相对下标，不包含low1,low2的基数。这是为了递归方便。
     */
    public double findTarget(int[] nums1, int[] nums2, int low1, int high1, int low2, int high2, int mid1, int mid2) {
        // base case
        if (high1 - low1 < 2) {
            return simpleFindTarget(nums1, nums2, low1, high1, low2, high2, mid1, mid2);
        }
        // recursion
        int median = ((high1 - low1 + 1) - 1) / 2; //相对地址（不包含low的基数）
        int index = indexOf(nums1[low1 + median],nums2,low2,high2); //相对地址（不包含low的基数）
        if (median + index < mid1) {
            return findTarget(nums1, nums2, low1 + median + 1, high1, low2 + index, high2 , mid1 - median - index - 1, mid2 - median - index - 1);
        } else if (median + index > mid2) {
            return findTarget(nums1, nums2, low1, low1 + median - 1, low2, low2 + index - 1, mid1, mid2);
        } else if (median + index == mid1) {
            return findTarget(nums1, nums2, low1 + median, high1, low2 + index, high2, mid1 - median - index, mid2 - median - index);
        } else if (median + index == mid2) {
            return findTarget(nums1, nums2, low1, low1 + median, low2, low2 + index - 1, mid1, mid2);
        }
        // never reach
        return 0;
    }

    /**
     * 简单的归并法。用两个指针分别指向两个数组。依次比较。直到获得第mid1和mid2个数。然后取平均值。
     * 为了递归方便，mid1和mid2是没有算上low基数。
     */
    public double simpleFindTarget(int[] nums1, int[] nums2, int low1, int high1, int low2, int high2, int mid1, int mid2) {
        int value1 = 0;
        int value2 = 0;
        int index = 0;
        int cursor = 0;
        while (low1 <= high1 || low2 <= high2) {
            int num1 = (low1 > high1)? Integer.MAX_VALUE : nums1[low1];
            int num2 = (low2 > high2)? Integer.MAX_VALUE : nums2[low2];
            if (num1 <= num2) {
                cursor = num1;
                low1++;
            } else {
                cursor = num2;
                low2++;
            }
            if (index == mid1) {
                value1 = cursor;
            }
            if (index == mid2) {
                value2 = cursor;
                return ((double)value1 + (double)value2) / 2;
            }
            index++;
        }
        return 0;
    }

    // 返回num如果插入array的low到high位子串的话，应该插入的位置。简洁版。
    // 返回相对地址
    public int indexOf(int num, int[] array, int low, int high) {
        if (high < low) {
            return 0;
        }
        if (high == low) {
            return (num <= array[low])? 0:1;
        }
        int median = low + ((high - low + 1) - 1)/2; // 下位中位数数学定义
        if (num <= array[median]) {
            return indexOf(num,array,low,median-1);
        } else {
            return (median - low + 1) + indexOf(num,array,median+1,high);
        }
    }
}
```

#### 二分法结果
![median-two-array-2](/images/leetcode/median-two-array-2.png)

### 重新整理朴素归并法的代码
把奇数和偶数的情况归并成同一种情况，不分别处理。然后逻辑流再写得再简洁一点。
```java
public class Solution {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int totalLength = nums1.length + nums2.length;
        int mid1 = 0;
        int mid2 = 0;
        if (totalLength % 2 == 0) {
            mid1 = (totalLength - 2)/2;
            mid2 = totalLength / 2;
        } else { // 如果是奇数，则 mid1 = mid2
            mid1 = (totalLength - 1)/2;
            mid2 = (totalLength - 1)/2;
        }
        int cursor1 = 0, cursor2 = 0, value1 = 0, value2 = 0, index = 0, temp = 0;
        while (cursor1 < nums1.length || cursor2 < nums2.length) {
            int num1 = (cursor1 >= nums1.length)? Integer.MAX_VALUE : nums1[cursor1];
            int num2 = (cursor2 >= nums2.length)? Integer.MAX_VALUE : nums2[cursor2];
            if (num1 <= num2) {
                temp = num1;
                cursor1++;
            } else {
                temp = num2;
                cursor2++;
            }
            if (index == mid1) {
                value1 = temp;
            }
            if (index == mid2) {
                value2 = temp;
                return ((double)value1 + (double)value2) / 2;
            }
            index++;
        }
        return 0; //never reached
    }
}
```

#### 最终版结果
最终版结果稍差一些。但其核心算法和第一版几乎一样。这点差别属于leetcode正常误差范围之内。
![median-two-array-3](/images/leetcode/median-two-array-3.png)
