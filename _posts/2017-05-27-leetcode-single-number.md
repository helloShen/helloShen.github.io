---
layout: post
title: "Leetcode - Algorithm - Single Number "
date: 2017-05-27 04:36:52
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation","hash table"]
level: "easy"
description: >
---

### 主要收获 - 感性地理解了`XOR`的数学意义

> `XOR`的数学意义有点类似比特位级别的 **“抵消”**。同一个比特位，两个`1`的信息互相抵消。只有单个`1`信息才会被保留。


### 题目
Given an array of integers, every element appears twice except for one. Find that single one.

Note:
Your algorithm should have a linear runtime complexity. Could you implement it without using extra memory?

### 解法一：用容器。时间复杂度：$$O(n)$$，空间复杂度：$$O(n)$$
简单地把遇到过的数字压入容器，方便查询。

#### `HashSet`版
```java
public class Solution {
    public int singleNumber(int[] nums) {
        Set<Integer> memo = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            if (!memo.add(nums[i])) { memo.remove(nums[i]); }
        }
        Iterator<Integer> ite = memo.iterator();
        return ite.next();
    }
}
```

#### 结果
`HashSet`的实现基于`HashMap`，所以它的`add()`和`remove()`等方法也是 $$O(1)$$ 的复杂度。
![single-number-1](/images/leetcode/single-number-1.png)

#### `HashMap`版
```java
public class Solution {
    public int singleNumber(int[] nums) {
        Map<Integer,Integer> memo = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (memo.get(nums[i]) == null) {
                memo.put(nums[i],0);
            } else {
                memo.remove(nums[i]);
            }
        }
        int res = 0;
        for (Map.Entry<Integer,Integer> entry : memo.entrySet()) {
            res = entry.getKey();
        }
        return res;
    }
}
```

#### 结果
直接用`HashMap`效果更好。但离银弹查多了。
![single-number-2](/images/leetcode/single-number-2.png)



### 解法二：双指针交换法, 时间复杂度：$$O(n^2)$$，不适用额外空间
用一个`slow`指针指向已经成对数字的边界，另一个`fast`指针向后遍历寻找配对。找到后交换`slow`指针和`fast`指针指向的内容，凑成对。比如，
```
  slow
   |
[4,2,6,3,1,2,4,1,5,6,3]
当找到第二个4,把第二个4和第一个2交换位置，同时slow指针，前移，
    slow
     |
[4,4,6,3,1,2,2,1,5,6,3]
```

#### 代码
```java
public class Solution {
    public int singleNumber(int[] nums) {
        if (nums.length == 1) { return nums[0]; }
        int slow = 1;
        whileLoop:
        while (slow < nums.length) {
            for (int fast = slow; fast < nums.length; fast++) { // 遍历配对
                if (nums[fast] == nums[slow-1]) {
                    int temp = nums[slow];
                    nums[slow] = nums[fast];
                    nums[fast] = temp;
                    slow+=2;
                    continue whileLoop;
                }
            }
            return nums[slow-1]; // 配对失败
        }
        if (slow == nums.length) { return nums[nums.length-1]; }
        return -1; // 没找到
    }
}
```

#### 结果
复杂度更高了，不可能更好。只是提供一种思路。
![single-number-3](/images/leetcode/single-number-3.png)


### 银弹！使用位操作`XOR`
这题的银弹我没想出来。很神奇。是这样，

`XOR`位操作是平时用来做加法用的，
> 两个`0`，或者两个`1`，抵消得`0`。否则得`1`。

```
0101
0011  XOR
----------
0110
```

所以
> `XOR`的数学意义有点类似比特位级别的 **“抵消”**。同一个比特位，两个`1`的信息互相抵消。只有单个`1`信息才会被保留。

它有两个重要性质：
1. 交换律。`a^b^c == a^c^b == c^a^b = ...任意abc的排列组合`。
2. 自反性。`x^x = 0`。任何都和自己抵消。

利用这两个性质，**成对的数字的信息会被互相抵消，只剩下落单的那个**。假设我们有`[2,1,4,5,2,4,1]`，

```
=> 0 ^ 2 ^ 1 ^ 4 ^ 5 ^ 2 ^ 4 ^ 1

=> 0^ 2^2 ^ 1^1 ^ 4^4 ^5            (利用交换律，让成对抵消更直观)

=> 0 ^ 0 ^ 0 ^ 0 ^ 5

=> 0 ^ 5

=> 5 :)                             （最后只剩下，落单的那个数）
```

#### 代码
```java
public class Solution {
    public int singleNumber(int[] nums) {
        int res = 0;
        for (int i = 0; i < nums.length; i++) {
            res ^= nums[i];
        }
        return res;
    }
}
```

#### 结果
简直变态！
![single-number-4](/images/leetcode/single-number-4.png)
