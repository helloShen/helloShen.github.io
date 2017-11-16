---
layout: post
title: "Leetcode - Algorithm - Power Of Four "
date: 2017-11-16 15:25:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation"]
level: "easy"
description: >
---

### 题目
Given an integer (signed 32 bits), write a function to check whether it is a power of 4.

Example:
Given num = 16, return true. Given num = 5, return false.

Follow up: Could you solve it without loops/recursion?

### 用一个掩码
4的次方的数，用二进制表示，只有一个`1`，而且都在奇数位。
```
0000 0001 // 1
0000 0100 // 4
0001 0000 // 16
0100 0000 // 64
```

所以用一个掩码，循环左移可以得到所有的4的整数幂。

#### 代码
```java
class Solution {
    public boolean isPowerOfFour(int num) {
        if (num <= 0) { return false; }
        int mask = 1;
        do {
            if (mask == num) {
                return true;
            } else {
                mask <<= 2;
            }
        } while (mask > 0 && mask <= num);
        return false;
    }
}
```

#### 结果
![power-of-four-1](/images/leetcode/power-of-four-1.png)


### 表驱动
在32位的`int`范围内，只有16个数是4的整数幂，如下所示。全记下来也没几个，可以作弊。
```
1
4
16
64
256
1024
4096
16384
65536
262144
1048576
4194304
16777216
67108864
268435456
1073741824
```

#### 代码
```java
class Solution {
    private static final int[] powerOfFour = new int[]{
        1,
        4,
        16,
        64,
        256,
        1024,
        4096,
        16384,
        65536,
        262144,
        1048576,
        4194304,
        16777216,
        67108864,
        268435456,
        1073741824
    };
    public boolean isPowerOfFour(int num) {
        for (int i = 0; i < powerOfFour.length; i++) {
            if (powerOfFour[i] == num) { return true; }
        }
        return false;
    }
}
```

#### 结果
![power-of-four-2](/images/leetcode/power-of-four-2.png)


### 用位操作，不用循环
大家都知道`n & (n-1)`可以消除最低位。那如果这个数只有1个`1`位，那么`n & (n-1) == 0`就成立。然后为了剔除所有只是`2`的倍数，不是`4`的倍数的数，可以用一个掩码`0x55555555`，
```
0x55555555 = 0101 0101 0101 0101 0101 0101 0101 0101
```

故事就是这样。

#### 代码
```java
class Solution {
    public boolean isPowerOfFour(int num) {
        return (num > 0 && ((num & (num - 1)) == 0) && (num & 0x55555555) != 0);
    }
}
```

#### 结果
![power-of-four-3](/images/leetcode/power-of-four-3.png)
