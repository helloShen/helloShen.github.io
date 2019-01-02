---
layout: post
title: "Leetcode - Algorithm - Find K Cloest Elements "
date: 2019-01-01 21:00:02
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search"]
level: "medium"
description: >
---

### 题目
Given a sorted array, two integers k and x, find the k closest elements to x in the array. The result should also be sorted in ascending order. If there is a tie, the smaller elements are always preferred.

Example 1:
```
Input: [1,2,3,4,5], k=4, x=3
Output: [1,2,3,4]
```

Example 2:
```
Input: [1,2,3,4,5], k=4, x=-1
Output: [1,2,3,4]
```

Note:
* The value k is positive and will always be smaller than the length of the sorted array.
* Length of the given array is positive and will not exceed 104
* Absolute value of elements in the array and x will not exceed 104

### 正常人的思路
假设目标数`x = 0`，对于下面数列，我们用两个指针，左指针指向第一个`<= x`的数，右指针指向第一个`> x`的数。然后将这两个指针逐渐向外扩展。差值相等的情况下，优先扩展左指针。
```
x = 0
                         小于等于 大于
                        <-----|  |----->
... -5, -4, -4, -3, -1, 0, 0, 0, 1, 5, 5, 8 ...
```

利用 **二分查找**，可以轻松找到数组中第一个大于`k`的数，即下面代码中的`firstGreater()`函数。

#### 代码
```java
class Solution {
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        List<Integer> res = new LinkedList<>();
        int firstGreater = firstGreater(arr, x);
        int left = firstGreater - 1, right = firstGreater;
        int leftDiff = (left >= 0)? x - arr[left] : 20001;
        int rightDiff = (right < arr.length)? arr[right] - x : 20001;
        while (k > 0) {
            if (leftDiff <= rightDiff) {
                res.add(0, arr[left--]);
                leftDiff = (left >= 0)? x - arr[left] : 20001;
            } else {
                res.add(arr[right++]);
                rightDiff = (right < arr.length)? arr[right] - x : 20001;
            }
            k--;
        }
        return res;
    }

    /** find the position of first element greater than given x */
    private int firstGreater(int[] arr, int x) {
        int size = arr.length;
        int lo = 0, hi = size - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (arr[mid] <= x) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return lo;
    }
}
```

#### 结果
![find-k-cloest-elements-1](/images/leetcode/find-k-cloest-elements-1.png)


### 神仙解法
但`StefanPochmann`提出了一个概念，还是刚才的例子，目标数是`x = 0`，要取的数字个数是`k = 7`，结果如下，
```
x = 0
k = 7

                            target
                              |
                    range     |
             |----------------+--|
... -5, -4, -4, -3, -1, 0, 0, 0, 1, 5, 5, 8 ...
             |                      |
           start                 start + k
```

这时候一个必定成立的命题是：
> x - arr[start] <= arr[start + k] - x

放到例子里，就是`0 - (-4) <= 5 - 0`。而且，`start`这个点的特殊性是，
> 所有从start或start右边开始的窗口，都满足这个命题。所有从start点左边开始的窗口都不满足。

```
                            target
                              |
... -5, -4, -4, -3, -1, 0, 0, 0, 1, 5, 5, 8 ...
         |                       |
       start                 start + k

0 - (-4) > 1 - 0
```

感性地理解这个现象，说白了`start`是左起第一个满足这个命题的点。也就是`start`点和`target`点足够接近。

所以`StefanPochmann`的方法就是用二分查找，在`O(logN)`时间里找到`start`点，然后顺序往下取`k`个数字。


#### 代码
```java
class Solution {
    public List<Integer> findClosestElements(int[] arr, int k, int x) {
        int size = arr.length;
        int lo = 0, hi = size - 1 - k;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (x - arr[mid] <= arr[mid + k] - x) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }
        List<Integer> res = new ArrayList<>();
        for (int i = lo; i < lo + k; i++) res.add(arr[i]);
        return res;
    }
}
```

#### 结果
![find-k-cloest-elements-2](/images/leetcode/find-k-cloest-elements-2.png)
