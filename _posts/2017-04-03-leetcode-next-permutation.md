---
layout: post
title: "Leetcode - Algorithm - Next Permutation "
date: 2017-04-03 16:13:34
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Implement next permutation, which rearranges numbers into the lexicographically next greater permutation of numbers.

If such arrangement is not possible, it must rearrange it as the lowest possible order (ie, sorted in ascending order).

The replacement must be in-place, do not allocate extra memory.

Here are some examples. Inputs are in the left-hand column and its corresponding outputs are in the right-hand column.
```
1,2,3 → 1,3,2
3,2,1 → 1,2,3
1,1,5 → 1,5,1
```

### 单纯数学规律 $$O(n)$$
以`[9, 8, 3, 7, 5, 9, 9, 9, 8, 7, 0]`为例，找到下一个排列法，需要3步：

第一步，从最后一个数字开始往前遍历，找到第一个比小于后面一个数字的位置，比如，`[9, 8, 3, 7, 5, 9, 9, 9, 8, 7, 0]`中，我们找到的`toSwitch`的数字是`5`。下标为`4`。
```
toSwitch = 4. (数字5所在位置)
[9, 8, 3, 7, >5<, 9, 9, 9, 8, 7, 0]
```
第二步，找到`5`后面的数字中最小的那个大于`5`的数字。比如例子中，这个数字就是第二个`7`, 下标为`9`。现在交换`toSwitch`和`anotherToSwitch`两个数字。
```
anotherToSwitch = 9. (第二个7所在位置)
[9, 8, 3, 7, >5<, 9, 9, 9, 8, >7<, 0]
```
交换之后变成，
```
[9, 8, 3, 7, >7<, 9, 9, 9, 8, >5<, 0]
```
第三步，将`toSwitch`位置之后的所有数字从小到大排列。这里不需要用普通的排序算法，因为原来的数字保证是降序排列的，比如`[9, 9, 9, 8, 5, 0]`，只需要按对交换最高位和最低位即可。
```
[9, 8, 3, 7, >7<, 0, 5, 8, 9, 9, 9]
```

特殊情况是，如果数字已经是降序排列，比如`[5,4,3,2,1]`，找到的`toSwitch`位置等于`-1`。因为不需要交换数字，直接将数组按升序排列即可。
```
[5,4,3,2,1]
不交换，直接升序排列，
[1,2,3,4,5]
```

#### 代码
```java
public class Solution {
    public void nextPermutation(int[] nums) {
        // step 1: find 1st number to swap
        int toSwap = -1;
        for (int i = nums.length-1; i > 0; i--) {
            if (nums[i] > nums[i-1]) { toSwap = i-1; break; }
        }
        // step 2: find 2nd number to swap. And swap them
        if (toSwap >= 0){
            int anotherToSwap = 0;
            for (int i = nums.length-1; i > toSwap ; i--) {
                if (nums[i] > nums[toSwap]) { anotherToSwap = i; break; }
            }
            swap(nums,toSwap,anotherToSwap);
        }
        // step 3: reverse sort the remainder
        reverseSort(nums, toSwap+1, nums.length-1);
    }
    public void swap(int[] nums, int high, int low) {
        int temp = nums[high];
        nums[high] = nums[low];
        nums[low] = temp;
    }
    public void reverseSort(int[] nums, int begin, int end) { // begin & end are both included
        while (begin < end) {
            swap(nums,begin,end);
            begin++; end--;
        }
    }
}
```

#### 结果
银弹！
![next-permutation-1](/images/leetcode/next-permutation-1.png)
