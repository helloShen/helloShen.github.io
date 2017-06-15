---
layout: post
title: "Leetcode - Algorithm - Rotate Array "
date: 2017-06-14 00:57:57
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 主要收获1 - 优先考虑半开半闭区间`[begin,end)`
> 操作数组的时候，使用半开半闭区间可以让操作更简单。有效避免各种`+1`，`-1`的麻烦。

比如我在数组中间想插入一个栅栏，把数组分成`A[0,1,2,3,4]`和`B[5,6,7,8,9]`两部分，最好插在`5`，而不是`4`的位置。
```
int[] nums = new int[10];
int mid = 5;
```
这时候数组看上去像下面这个样子，
```
              mid    nums.length
               |         |
    |0|1|2|3|4|5|6|7|8|9|
     |-------| |-------|
         A         B
```
这样，取`A[0,1,2,3,4]`和`B[5,6,7,8,9]`两部分的长度，都会更简洁。
```
int size = nums.length;
int sizeB = size - mid;
int sizeA = mid;
```

### 主要收获2 - 练习《编程珠玑》第2章介绍的4种`转动数组`的方法
1. 方法一：分成2部分交换。
2. 方法二：分成3部分，递归交换其中的2部分。
3. 方法三：求逆法。
4. 方法四：杂技跳跃法。

### 题目
Rotate an array of n elements to the right by `k` steps.

For example, with n = `7` and k = `3`, the array `[1,2,3,4,5,6,7]` is rotated to `[5,6,7,1,2,3,4]`.

Note:
Try to come up as many solutions as you can, there are at least 3 different ways to solve this problem.

Hint:
Could you do it in-place with O(1) extra space?


### 直接转。时间复杂度 $$O(n)$$，不使用额外空间。
最暴力的办法。做一遍，有助于理解问题。

#### 代码
```java
public class Solution {
    public void rotate(int[] nums, int k) {
        k = ((k % nums.length) + nums.length) % nums.length;
        if (nums.length < 2 || k == 0) { return; }
        for (int i = 0; i < k; i++) {
            int pre = nums[nums.length-1];
            for (int j = 0; j < nums.length; j++) {
                int temp = nums[j];
                nums[j] = pre;
                pre = temp;
            }
        }
    }
}
```

#### 结果
![rotate-array-0](/images/leetcode/rotate-array-0.png)


### 用额外空间，只转一次。时间复杂度 $$O(n)$$，空间复杂度 $$O(n)$$
直接创建一个新的空数组。把原始数组的`nums[i]`拷贝到`copy[(i+k)%nums.length]`的位置。

#### 代码
```java
/**
 * 解法1：直接转。用一个辅助array缓存一部分元素。
 */
public class Solution {
    public void rotate(int[] nums, int k) {
        // defense: so that k can accept negative number
        k = ((k % nums.length) + nums.length) % nums.length;
        if (nums.length < 2 || k == 0) { return; }
        int[] copy = new int[nums.length];
        System.arraycopy(nums,0,copy,0,nums.length);
        for (int i = 0; i < nums.length; i++) {
            nums[(i+k)%nums.length] = copy[i];
        }
    }
}
```

#### 结果
![rotate-array-1](/images/leetcode/rotate-array-1.png)


### 分成3部分，递归交换其中两部分。时间复杂度 $$O(n)$$，空间复杂度 $$O(n)$$
1. **第一步**，切割成3部分，
    a. 如果k大于长度一半，分成下面3部分：
      [a,bl,br]，其中 length(bl+br) = k
    b. 如果k小于长度一半，分成下面3部分：
      [a,bl,br]，其中 length(a） = k

2. **第二步**，交换 a 和 br 两部分，变成 [br,bl,a] 三部分。
3. **第三步**，递归。
      a. 如果k大于长度一半，
          [br,bl]部分继续向右转 k-length(br) 位
      b. 如果k小于长度一半，
          [bl,a]部分继续向右转 k 位

#### 代码
```java
public class Solution {
    public void rotate(int[] nums, int k) {
        // defense: so that k can accept negative number
        k = ((k % nums.length) + nums.length) % nums.length;
        if (nums.length < 2 || k == 0) { return; }
        recursion(nums,0,nums.length,k);
    }      
    public void recursion(int[] nums, int lo, int hi, int k) {
        int length = hi - lo;
        if ((length < 2) || (k % length == 0)) { return; } // base case
        int half = length / 2;
        int len = (k <= half)? k : (hi - lo) - k; // length of br part
        int bar1 = lo + len, bar2 = hi - len;
        // exchange [a] & [br]
        for (int i = 0; i < len; i++) {
            int temp = nums[bar2+i];
            nums[bar2+i] = nums[lo+i];
            nums[lo+i] = temp;
        }
        // exchange [bl] & [br]
        if (k <= half) {
            recursion(nums,bar1,hi,k); // continue to rotate k to right
        } else {
            recursion(nums,lo,bar2,k-len); // continue to rotate k-len to right
        }
    }
}
```

#### 结果
![rotate-array-2](/images/leetcode/rotate-array-2.png)


### 求逆法。时间复杂度 $$O(n)$$，空间复杂度 $$O(1)$$
求逆法。如下图所示，
![rotate-array-flip-hand](/images/leetcode/rotate-array-flip-hand.png)
`[1,2,3,4,5,6,7,8,9,10]` 右移 3 格，分三步走：
  1. `[1,2,3,4,5]` 求逆：-> `[5,4,3,2,1]`
  2. `[6,7,8,9,10]` 求逆：-> `[10,9,8,7,6]`
  3. `[1,2,3,4,5,6,7,8,9,10]` 一起再求逆：-> `[10,9,8,7,6,5,4,3,2,1]`

#### 代码
```java
public class Solution {
    public void rotate(int[] nums, int k) {
        // defense: so that k can accept negative number
        k = ((k % nums.length) + nums.length) % nums.length;
        if (nums.length < 2 || k == 0) { return; }
        int bar = nums.length - k;
        reverse(nums,0,bar);
        reverse(nums,bar,nums.length);
        reverse(nums,0,nums.length);
    }
    /**
     * [lo,hi)
     */
    public void reverse(int[] nums, int lo, int hi) {
        int len = hi - lo;
        if (len < 2) { return; }
        int upperMid = lo + (len - 1) / 2;
        int lowerMid = lo + len / 2;
        while (upperMid >= lo && lowerMid < hi) {
            int temp = nums[upperMid];
            nums[upperMid] = nums[lowerMid];
            nums[lowerMid] = temp;
            upperMid--; lowerMid++;
        }
    }
}
```

#### 结果
![rotate-array-3](/images/leetcode/rotate-array-3.png)


### 杂技跳跃法。时间复杂度 $$O(n)$$，空间复杂度 $$O(1)$$
杂技跳跃法。如下图所示，
![rotate-array-jump](/images/leetcode/rotate-array-jump.png)

先求出，`nums.length`和`k`的最小公约数。比如长度是`7`的数组，向右转`3`位，`7`和`3`的最小公约数是`1`。长度是`9`的数组，向右转`3`位，`9`和`3`的最小公约数是`3`。

然后从最高位`0`位开始，先预存`nums[i]`，然后将`nums[0]`的数写入`nums[i]`位置。然后预存`nums[2i]`，再把之前预存的`nums[i]`写入`nums[2i]`位置，以此类推，直到最后把预存的数写入`nums[0]`的位置。整个过程一直对下标取余数。

如果没有完成，就往后移一位`nums[1]`开始重复上面的过程。一共重复 **最小公约数** 次。

例如：`[1,2,3,4,5,6]` 向右转动3格。`6`和`3`的最小公约数是`2`，所以只需要转2圈。
```
Original Array: [1, 2, 3, 4, 5, 6]
第一圈：移动0,2,4
Arrays becomes: [1, 2, 1, 4, 5, 6]
Arrays becomes: [1, 2, 1, 4, 3, 6]
Arrays becomes: [5, 2, 1, 4, 3, 6]
第二圈：移动1,3,5
Arrays becomes: [5, 2, 1, 2, 3, 6]
Arrays becomes: [5, 2, 1, 2, 3, 4]
Arrays becomes: [5, 6, 1, 2, 3, 4]
结果：
Rotated Array: [5, 6, 1, 2, 3, 4]
```

再比如，`[1, 2, 3, 4, 5, 6, 7, 8, 9]` 向后转3格。`9`和`3`的最小公约数为`3`。所以需要转3圈，每圈改3个数。
```
Original Array: [1, 2, 3, 4, 5, 6, 7, 8, 9]
第一圈：移0,3,6位
Arrays becomes: [1, 2, 3, 1, 5, 6, 7, 8, 9]
Arrays becomes: [1, 2, 3, 1, 5, 6, 4, 8, 9]
Arrays becomes: [7, 2, 3, 1, 5, 6, 4, 8, 9]
第二圈：移1,4,7位
Arrays becomes: [7, 2, 3, 1, 2, 6, 4, 8, 9]
Arrays becomes: [7, 2, 3, 1, 2, 6, 4, 5, 9]
Arrays becomes: [7, 8, 3, 1, 2, 6, 4, 5, 9]
第三圈：移2,5,8位
Arrays becomes: [7, 8, 3, 1, 2, 3, 4, 5, 9]
Arrays becomes: [7, 8, 3, 1, 2, 3, 4, 5, 6]
Arrays becomes: [7, 8, 9, 1, 2, 3, 4, 5, 6]
结果：
Rotated Array:  [7, 8, 9, 1, 2, 3, 4, 5, 6]
```

> 理论上讲这应该是这道题最普适的算法。思路很直观，效率高，而且还几乎不使用额外空间。

#### 代码
```java
public class Solution {
    public void rotate(int[] nums, int k) {
        // defense
        k = k % nums.length;
        if (nums.length < 2 || k == 0) { return; }
        // iteration
        int minCovenant = minimumCovenant(nums.length, k); // 求最小公约数
        for (int i = 0; i < minCovenant; i++) { // 必定转满最小公约数圈
            int cur = i, register = nums[cur];
            do {
                cur = (cur + k) % nums.length;
                int temp = nums[cur];
                nums[cur] = register;
                register = temp;
            } while (cur != i);
        }
    }
    // 辗转相除法求最小公约数
    public int minimumCovenant(int numerator, int denominator) {
        int remainder = numerator % denominator;
        if (remainder == 0) { return denominator; }
        return minimumCovenant(denominator,remainder);
    }
}
```

不想求最小公倍数的，可以直接计算移动的次数。每个位置都移动过了，就结束。
```java
public class Solution {
    public void rotate(int[] nums, int k) {
        k = k % nums.length;
        int count = 0;
        for (int start = 0; count < nums.length; start++) {
            int current = start;
            int prev = nums[start];
            do {
                int next = (current + k) % nums.length;
                int temp = nums[next];
                nums[next] = prev;
                prev = temp;
                current = next;
                count++;
            } while (start != current);
        }
    }
}
```

#### 结果
![rotate-array-4](/images/leetcode/rotate-array-4.png)
