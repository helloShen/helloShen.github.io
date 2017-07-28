---
layout: post
title: "Leetcode - Algorithm - Three Sum Smaller "
date: 2017-07-28 15:59:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","two pointers"]
level: "medium"
description: >
---

### 题目
Given an array of n integers nums and a target, find the number of index triplets i, j, k with 0 <= i < j < k < n that satisfy the condition nums[i] + nums[j] + nums[k] < target.

For example, given nums = `[-2, 0, 1, 3]`, and target = `2`.

Return 2. Because there are two triplets which sums are less than 2:
```
[-2, 0, 1]
[-2, 0, 3]
```
Follow up:
Could you solve it in O(n2) runtime?

### 主要思路
这题最朴素的做法很简单，只需要3层loop套嵌。就算如果是`k`个数，也可以用DFS递归遍历。

接下来这种问题很容易想到分治法，或者动态规划，分解到子问题。但这题并没有重复解决的问题，所以分治法并不能提高效率。而且在子问题的基础上再多加一个数，很难利用子问题的结果来推出这一步的结果。

接下来一个很自然的一件事就是排序。很多乱序问题，到了有序空间就会简化。但这题就算排序以后，分治法还是无效。

这时候就应该想到`two pointers`法。是解这类求和问题的常见手段。

普通遍历两个指针`i`和`j`紧挨着。`two pointers`法的特点就是`i`和`j`两个指针分别初始化在数组的一头一尾。这样做的好处，看下面这个例子，
```
 left               right
  |                  |
  1,2,3,4,5,6,7,8,9,10              target = 8

1 + 10 > 8      -> right--
```
> `1 + 10 > 8`，这时候如果`right`不动，`left`从`1-9`的9种可能都排除了，都太大。只能`right`左移。

如果`target = 15`，
```
 left               right
  |                  |
  1,2,3,4,5,6,7,8,9,10              target = 15

1 + 10 < 15      -> left++
```
> `1 + 10 < 15`，这时候如果`left`不动，`right`从`2-10`的9种可能都排除了，都太小。只能`left`右移。

这就是`two pointers`方法的本质。



### 朴素遍历，$$O(n^3)$$
没花样，$$O(n^3)$$肯定能解。

#### 代码
```java
public class Solution {
    public int threeSumSmaller(int[] nums, int target) {
        int count = 0;
        for (int i = 0; i < nums.length-2; i++) {
            for (int j = i+1; j < nums.length-1; j++) {
                for (int k = j+1; k < nums.length; k++) {
                    if (nums[i] + nums[j] + nums[k] < target) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
```

#### 结果
![three-sum-smaller-1](/images/leetcode/three-sum-smaller-1.png)


### `Two Pointers`法, $$O(n^2)$$
先排序，然后用3个指针，
```
  i left           right
  |  |              |
[-3,-1,1,5,8,11,15,16]          target = 10
```
`(-3) + (-1) + 16 > 10`，太大了。所有`left`再往左移的情况都不用再考虑了，因为只会更大。所以`right`往左移。取更小的值。
```
  i left    right
  |  |       |
[-3,-1,1,5,8,11,15,16]          target = 10
```
直到`right = 11`，`(-3) + (-1) + 11 < 10`。这时候说明如果`right`取所有`left`到`right`之间的数，和只会更小，不用再测，所以`count = count + (right - left)`。

然后再把`i`往右移，进入下一个循环，一次类推。
```
     i left        right
     |  |           |
[-3,-1,1,5,8,11,15,16]          target = 10
```

#### 代码
```java
public class Solution {
    public int threeSumSmaller(int[] nums, int target) {
        if (nums.length < 3) { return 0; }
        Arrays.sort(nums);
        int count = 0;
        for (int i = 0; i < nums.length-2; i++) {
            int left = i+1, right = nums.length-1;
            while (left < right) {
                if (nums[i] + nums[left] + nums[right] < target) {
                    count += (right - left); left++;
                } else {
                    right--;
                }
            }
        }
        return count;
    }
}
```

#### 结果
![three-sum-smaller-2](/images/leetcode/three-sum-smaller-2.png)
