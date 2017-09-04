---
layout: post
title: "Leetcode - Algorithm - Maximum Xor Of Two Numbers In An Array "
date: 2017-09-04 17:46:43
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation"]
level: "medium"
description: >
---

### 题目
Given a non-empty array of numbers, a0, a1, a2, … , an-1, where 0 ≤ ai < 231.

Find the maximum result of ai XOR aj, where 0 ≤ i, j < n.

Could you do this in O(n) runtime?

Example:
```
Input: [3, 10, 5, 25, 2, 8]

Output: 28
```
Explanation: The maximum result is 5 ^ 25 = 28.

### 主要思路
这题肯定是要从位操作中入手。分析每一位是`1`还是`0`。

但常规的动态规划和分治法都没戏。因为高层问题的最优解不是通过子问题的最优解得到。子问题里非常小的一个数，如果和新加入的元素非常互补，有可能逆袭。 考虑下面两个分组，
```
// 分组1：最优解是 a ^ b = 1111 1100
a = 1111 0000
b = 0000 1100
c = 1000 0001

// 分组2：最优解是 d ^ e = 1111 1110
d = 1111 0000 //
e = 0000 1110
f = 0111 1110

// 合并分组1和分组2后，最优解是 c ^ f = 1111 1111
```
分组1和分组2中没用的`c`和`f`两个元素完美互补，合并两个子分组后，`c ^ f`的组合超过原先的两个子问题的最优解。

但如果换一个角度看问题，重新定义子问题，会有奇效。

### 位操作
以`[3, 10, 5, 25, 2, 8]`为例，全部写成比特位的形式，
```
3   = 0000 0011
10  = 0000 1010
5   = 0000 0101
25  = 0001 1001
8   = 0000 1000
```
先用掩码`1000 0000`切下最高位,
```
      1000 0000  // 切最高位
      |
3   = 0000 0000
10  = 0000 0000
5   = 0000 0000
25  = 0000 0000
8   = 0000 0000
```
切下来结果都是`0`。这时候，`max = 0000 0000`，所以如果有两个数`a ^ b = 1000 0000`的话，最大值就可以更新为`1000 0000`。结果当然是没有。

一直重复上面这个操作，前3位`max`一直保持是`0000 0000`，没有更新。直到第4位开始，
```
      1111 0000  // 切前4位
      |
3   = 0000 0000
10  = 0000 0000
5   = 0000 0000
25  = 0001 0000
8   = 0000 0000
```
前4位切下来以后，因为`max`还保持`0000 0000`，如果有两个数`a ^ b = 0001 0000`的话，`max`就要被更新了。这时候，因为`25 = 0001 0000`， 它和任意一个数做异或操作的结果都是`0001 0000`。这时候就把`max`更新为`0001 0000`。

这个解法的思路，就是从最高位开始步步为营。

#### 代码
```java
class Solution {
    public int findMaximumXOR(int[] nums) {
        Set<Integer> prefixSet = new HashSet<>();
        /*********************************************************
         * 开头全是1的掩码，用来切下数字的头部信息
         * 1010 1010
         * 1110 0000 &  // 掩码（切下前3位）
         * -----------
         * 1010 0000
         ********************************************************/
        int mask = Integer.MIN_VALUE >> 1;
        int max = 0;
        for (int i = 30; i >= 0; i--,mask >>= 1) {
            prefixSet.clear();
            // 把切下来的头全存在Set里
            for (int num : nums) {
                prefixSet.add(num & mask);
            }
            // candidate是加入了新的一位以后，唯一可能超过max的解
            int candidate = max | (1 << i);
            /*********************************************************
             * 到Set里找，有没有两个头部 a ^ b = candidate。有的话，更新max
             * 这里用到了XOR操作的结合律，即，
             *      如果 a ^ b = c, 则，
             *      a ^ c = b
             ********************************************************/
            for (Integer prefix : prefixSet) {
                if (prefixSet.contains(candidate ^ prefix)) {
                    max = candidate;
                }
            }
        }
        return max;
    }
}
```

#### 结果
![maximum-xor-of-two-numbers-in-an-array-1](/images/leetcode/maximum-xor-of-two-numbers-in-an-array-1.png)
