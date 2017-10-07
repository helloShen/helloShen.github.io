---
layout: post
title: "Leetcode - Algorithm - Wiggle Sort Two "
date: 2017-10-07 01:18:34
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["sort"]
level: "medium"
description: >
---

### 题目
Given an unsorted array nums, reorder it such that nums[0] < nums[1] > nums[2] < nums[3]....

Example:
```
(1) Given nums = [1, 5, 1, 1, 6, 4], one possible answer is [1, 4, 1, 5, 1, 6].
(2) Given nums = [1, 3, 2, 2, 3, 1], one possible answer is [2, 3, 1, 3, 1, 2].
```

Note:
You may assume all input has valid answer.

Follow Up:
Can you do it in O(n) time and/or in-place with O(1) extra space?

### 先排序, $$O(n\log_{}{n})$$
以`[1,5,2,1,6,4]`为例，排序以后得到`[1,1,2,4,5,6]`，以中位数为界把数组分为两半`[1,1,2]`和`[4,5,6]`。

然后把他们分别反转变成`[2,1,1]`和`[6,5,4]`，最后把两个数组加塞组合到一起，
```
2,      1,      1
    6,      5,      4
-------------------------
2,  6,  1,  5,  1,  4
```

复杂度取决于排序过程。

#### 代码
```java
class Solution {
    public void wiggleSort(int[] nums) {
        int[] copy = Arrays.copyOf(nums,nums.length);
        Arrays.sort(copy);
        int mid = (nums.length - 1) / 2;
        for (int i = mid, j = 0; i >= 0; i--,j+=2) {
            nums[j] = copy[i];
        }
        for (int i = nums.length-1, j = 1; i > mid; i--, j+=2) {
            nums[j] = copy[i];
        }
    }
}
```

#### 结果
![wiggle-sort-two-1](/images/leetcode/wiggle-sort-two-1.png)


### 可以不排序，只需找到中位数，$$O(n)$$
排序以后让问题变得更清晰，更好掌控。但实际上这个问题的核心在于：**“中位数”** ，不在于排序。下面示例中`M`代表中位数，`S`代表小于中位数的数，`L`代表大于中位数的数。原始的顺序可能是：
```
[L1,M,S1,S2,L2,L3]
```
排序以后我们关注的只是中位数`M`，如果完全是升序，当然可以，
```
[S1,S2,M,L1,L2,L3]
```
但`[S1,S2]`的相对顺序，`[L1,L2,L3]`的相对顺序不影响结果。最后只要符合下面这个格式的插入都是合法的结果。
```
Index :       0   1   2   3   4   5
Small half:   M       S       S    
Large half:       L       L       L
```

不用排序，就可以做到在O(n)的时间内找到中位数。关键是利用`Kth Largest Number`这个问题对 **Three Way Partition** 的运用。

#### 代码
```java
class Solution {
    private int[] copy = new int[0];
    public void wiggleSort(int[] nums) {
        local(nums);
        int midIndex = (nums.length - 1) / 2;
        int mid = kthLargest(midIndex);
        int even = 0;
        for (int i = midIndex; i >= 0; i--) {
            nums[even] = copy[i];
            even += 2;
        }
        int odd = ((nums.length - 1) % 2 == 0)? nums.length - 2 : nums.length - 1;
        for (int i = midIndex + 1; i < copy.length; i++) {
            nums[odd] = copy[i];
            odd -= 2;
        }
    }
    private void local(int[] nums) {
        copy = Arrays.copyOf(nums,nums.length);
    }
    private int kthLargest(int k) {
        int[] pivot = new int[]{-1,-1};
        int lo = 0, hi = copy.length-1;
        while (true) {
            pivot = threeWayPartition(lo,hi);
            if (pivot[1] < k) {
                lo = pivot[1] + 1;
            } else if (pivot[0] > k) {
                hi = pivot[0] - 1;
            } else {
                return copy[pivot[0]];
            }
        }
    }
    private int[] threeWayPartition(int lo, int hi) {
        int pivot = copy[hi];
        exch(lo,hi);
        for (int i = lo + 1; i <= hi; ) {
            int num = copy[i];
            if (num < pivot) {
                exch(lo++,i++);
            } else if (num > pivot){
                exch(hi--,i);
            } else {
                i++;
            }
        }
        int[] res = new int[2];
        res[0] = lo;
        res[1] = hi;
        return res;
    }
    private void exch(int x, int y) {
        int temp = copy[x];
        copy[x] = copy[y];
        copy[y] = temp;
    }
}
```

#### 结果
手写 **Three Way Partiton** 的效率不如Java内置排序算法高。
![wiggle-sort-two-2](/images/leetcode/wiggle-sort-two-2.png)
