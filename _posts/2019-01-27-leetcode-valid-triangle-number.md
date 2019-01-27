---
layout: post
title: "Leetcode - Algorithm - Valid Triangle Number "
date: 2019-01-27 00:20:45
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math", "binary search"]
level: "medium"
description: >
---

### 题目
Given an array consists of non-negative integers, your task is to count the number of triplets chosen from the array that can make triangles if we take them as side lengths of a triangle.

Example 1:
```
Input: [2,2,3,4]
Output: 3
Explanation:
Valid combinations are:
2,3,4 (using the first 2)
2,3,4 (using the second 2)
2,2,3
```

Note:
* The length of the given array won't exceed 1000.
* The integers in the given array are in the range of [0, 1000].

### 直接解
数学原理很简单：
> 三角形的两边之和一定大于第三边

直观的解法也很简单，只需要3个指针（代表三角形的三边），分别遍历数组的过程中，用两边之和大于第三边来检验即可。当然前提条件是先将数组排序。

这样复杂度为`O(n^3)`，其中`n`为数组的长度。

### 二分查找
聪明一点的办法，只需要2个指针。找到前两条边的长度之后，可以计算出第三条边的合法取值范围。比如两条边`[2,2]`，第三条边必须小于`4`。通过二分查找在有序数组上找到`4`，可以计算出和前两条边匹配的第三条边的数量。
```
第三边 < 2 + 2 = 4

i j k
| | |
2,2,3,4,6,6,7
      |
     max

第三边的取值范围为[3,4)
```

复杂度简化为`O(n^2)`.

需要注意的数组中存在重复的数字，二分查找之后需要检查是否之前有重复的数字，如果有，需要将指针前移。比如，
```
二分查找"70"，会指向第二个70。需要将指针前移到第一个70处。

              binary search
                    |
[34,75,96,10,60,70,70,45]
                 |
            修正后的位置
```

#### 代码
```java
class Solution {
    public int triangleNumber(int[] nums) {
        int count = 0;
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {
            for (int j = i + 1; j < nums.length - 1; j++) {
                int max = nums[i] + nums[j];
                int searchResult = Arrays.binarySearch(nums, max);
                int maxIdx = (searchResult < 0)? - (searchResult + 1) : searchResult;
                if (maxIdx < nums.length) {
                    while (maxIdx > 0 && nums[maxIdx - 1] == nums[maxIdx]) maxIdx--;
                }
                int diff = maxIdx - (j + 1);
                if (diff > 0) count += diff;
            }
        }
        return count;
    }
}
```

#### 结果
![valid-triangle-number-1](/images/leetcode/valid-triangle-number-1.png)
