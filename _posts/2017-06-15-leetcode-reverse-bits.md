---
layout: post
title: "Leetcode - Algorithm - Reverse Bits "
date: 2017-06-15 02:19:31
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation"]
level: "easy"
description: >
---

### 题目
Reverse bits of a given `32` bits unsigned integer.

For example, given input `43261596` (represented in binary as `00000010100101000001111010011100`), return `964176192` (represented in binary as `00111001011110000010100101000000`).

Follow up:
If this function is called many times, how would you optimize it?

### 正常掩码法
用`1`做掩码，一位一位地切。下面的代码是相对比较合理的切法。掩码`1`不变，切最低位。`n`往右移。切下来也放在结果最低位，然后结果往左移动。
```
n           01010101      right >>>  00101010
mask        00000001   &
            ------------
bit         00000001   
reverse     00000000   |
            ------------
            00000001      left  <<   00000010
```

#### 代码

```java
public class Solution {
    public int reverseBits(int n) {
        int reverse = 0;
        for (int i = 0; i < 32; i++) {
            int bit = n & 1;
            reverse <<= 1;
            reverse |= bit;
            n >>>= 1;
        }
        return reverse;
    }
}
```

#### 结果
![reverse-bits-1](/images/leetcode/reverse-bits-1.png)


### 小优化，跳过开头的多个`0`
如果大部分数字比较小的话，开头会有很多个`0`。比如这样，`00000000000000000001111010011100`。如果探测到剩下的都是`0`，就直接出结果。

#### 代码
```java
public class Solution {
    public int reverseBits(int n) {
        int reverse = 0;
        for (int i = 0; i < 32; i++) {
            if (n == 0) { reverse <<= (32 - i); break; }
            int bit = n & 1;
            reverse <<= 1;
            reverse |= bit;
            n >>>= 1;
        }
        return reverse;
    }
}
```

#### 结果
![reverse-bits-2](/images/leetcode/reverse-bits-2.png)


### 每次多读几位，可以预存所有可能的反转结果

#### 每次读2位
读两位的话，只有`01`和`10`的情况需要反转。`11`和`00`不需要。
```java
public class Solution {
    public int reverseBits(int n) {
        int reverse = 0;
        for (int i = 0; i < 16; i++) {
            if (n == 0) { reverse <<= (32 - 2 * i); break; }
            int bit = n & 3;
            reverse <<= 2;
            if (bit == 2) {
                reverse |= 1;
            } else if (bit == 1) {
                reverse |= 2;
            } else {
                reverse |= bit;
            }
            n >>>= 2;
        }
        return reverse;
    }
}
```

#### 每次读4位
读4位就有`16`种可能的反转，可以都预存下来，

`int[] MAP = new int[]{ 0,8,4,12,2,10,6,14,1,9,5,13,3,11,7,15 };`
```java
public class Solution {
    private final int[] MAP = new int[]{ 0,8,4,12,2,10,6,14,1,9,5,13,3,11,7,15 };
    public int reverseBits(int n) {
        int reverse = 0;
        for (int i = 0; i < 8; i++) {
            int bit = n & 15;
            reverse <<= 4;
            reverse |= MAP[bit];
            n >>>= 4;
        }
        return reverse;
    }
}
```

#### 结果
![reverse-bits-3](/images/leetcode/reverse-bits-3.png)


### 方法3：脑洞方法
通过多伦反转，能够反转整个数字，
```
abcdefgh -> efghabcd -> ghefcdab -> hgfedcba
```
然后反转的过程，完全用`Bit Manipulation`完成。在leetcode社区看到的方法。挺威武的。

#### 代码
```java
public class Solution {
    public int reverseBits(int n) {
        int ret=n;
        ret = ret >>> 16 | ret<<16;
        ret = (ret & 0xff00ff00) >>> 8 | (ret & 0x00ff00ff) << 8;
        ret = (ret & 0xf0f0f0f0) >>> 4 | (ret & 0x0f0f0f0f) << 4;
        ret = (ret & 0xcccccccc) >>> 2 | (ret & 0x33333333) << 2;
        ret = (ret & 0xaaaaaaaa) >>> 1 | (ret & 0x55555555) << 1;
        return ret;
    }
}
```

#### 结果
![reverse-bits-4](/images/leetcode/reverse-bits-4.png)
