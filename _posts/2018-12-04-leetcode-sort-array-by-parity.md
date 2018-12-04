---
layout: post
title: "Leetcode - Algorithm - Sort Array By Parity "
date: 2018-12-04 18:14:47
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given an array A of non-negative integers, return an array consisting of all the even elements of A, followed by all the odd elements of A.

You may return any answer array that satisfies this condition.

Example 1:
```
Input: [3,1,2,4]
Output: [2,4,3,1]
The outputs [4,2,3,1], [2,4,1,3], and [4,2,1,3] would also be accepted.
```

Note:
* 1 <= A.length <= 5000
* 0 <= A[i] <= 5000

### 排序算法中常用的交换步骤
用两个指针，
* `lastEven`指向数组中最后一个偶数的位置
* `curr`指向当前元素，用来遍历数组

考虑例子`[3,1,2,4]`，
```
初始lastEven指向-1
curr指向0

lastEven curr
      |  |
        [3,  1,  2,  4]

发现3不是偶数，什么也不做，curr自然往前进一格


lastEven    curr
      |      |
        [3,  1,  2,  4]

发现1不是偶数，什么也不做，curr自然往前进一格


lastEven        curr
      |          |
        [3,  1,  2,  4]

发现2是偶数，和lastEven的下一个元素交换位置，然后lastEven向前进一格，得到，

   lastEven         curr
         |           |
        [2,  1,  3,  4]

最后4也是偶数，再和lastEven的下一个元素交换位置，lastEven向前进一格，

       lastEven         curr
             |           |
        [2,  4,  3,  1]
```

#### 代码
```java
class Solution {
    public int[] sortArrayByParity(int[] A) {
        for (int lastEven = -1, curr = 0; curr < A.length; curr++) {
            if (A[curr] % 2 == 0) swap(A, ++lastEven, curr);
        }
        return A;
    }

    private void swap(int[] arr, int x, int y) {
        int temp = arr[x];
        arr[x] = arr[y];
        arr[y] = temp;
    }
}
```

#### 结果
![sort-array-by-parity-1](/images/leetcode/sort-array-by-parity-1.png)
