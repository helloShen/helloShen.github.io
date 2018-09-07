---
layout: post
title: "Leetcode - Algorithm - Utf8 Validation "
date: 2018-09-07 15:15:40
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation"]
level: "medium"
description: >
---

### 题目
A character in UTF8 can be from 1 to 4 bytes long, subjected to the following rules:

1. For 1-byte character, the first bit is a 0, followed by its unicode code.
2. For n-bytes character, the first n-bits are all one's, the n+1 bit is 0, followed by n-1 bytes with most significant 2 bits being 10.

This is how the UTF-8 encoding would work:
```
   Char. number range  |        UTF-8 octet sequence
      (hexadecimal)    |              (binary)
   --------------------+---------------------------------------------
   0000 0000-0000 007F | 0xxxxxxx
   0000 0080-0000 07FF | 110xxxxx 10xxxxxx
   0000 0800-0000 FFFF | 1110xxxx 10xxxxxx 10xxxxxx
   0001 0000-0010 FFFF | 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
Given an array of integers representing the data, return whether it is a valid utf-8 encoding.
```

Note:
* The input is an array of integers. Only the least significant 8 bits of each integer is used to store the data. This means each integer represents only 1 byte of data.

Example 1:
```
data = [197, 130, 1], which represents the octet sequence: 11000101 10000010 00000001.

Return true.
It is a valid utf-8 encoding for a 2-bytes character followed by a 1-byte character.
```

Example 2:
```
data = [235, 140, 4], which represented the octet sequence: 11101011 10001100 00000100.

Return false.
The first 3 bits are all one's and the 4th bit is 0 means it is a 3-bytes character.
The next byte is a continuation byte which starts with 10 and that's correct.
But the second continuation byte does not start with 10, so it is invalid.
```

### 位操作
对于`data = [197, 130, 1]`，打头`197`用掩码`1110 0000`做`AND`操作，把前3位切下来，就知道这个字符有2个bytes组成，后面还跟着一个byte。
```
1100 0101
1110 0000 &
-----------
1100 0000       --> 后续还有一个byte尾部
```
再取`130`用掩码`1100 0000`做`AND`操作，切下前2位，发现是`10`，那么`[197, 130]`就是一个合法的UTF-8字符。
```
1000 0010
1100 0000 &
-----------
1000 0000       --> 合法的尾部
```
最后`1`用掩码`1000 0000`切第一位，是`0`，说明是单个byte的字符，
```
0000 0001
1000 0000 &
-----------
0000 0000       --> 单个byte的原生ASCII字符
```

这里要用到的一系列掩码如下，
```java
int mask1 = 0x80; // 1000 0000
int mask2 = 0xc0; // 1100 0000
int mask3 = 0xe0; // 1110 0000
int mask4 = 0xf0; // 1111 0000
int mask5 = 0xf8; // 1111 1000
```

#### 代码
```java
/** Bitwise Operation */
class Solution {

    private int mask1 = 0x80; // 1000 0000
    private int mask2 = 0xc0; // 1100 0000
    private int mask3 = 0xe0; // 1110 0000
    private int mask4 = 0xf0; // 1111 0000
    private int mask5 = 0xf8; // 1111 1000

    public boolean validUtf8(int[] data) {
        for (int i = 0; i < data.length; i++) {
            int head = data[i];
            int len = 0;
            if ((head & mask1) == 0) { // 0xxx xxxx
                continue;
            } else if ((head & mask3) == mask2) { // 110xx xxxx
                len = 1;
            } else if ((head & mask4) == mask3) { // 1110 xxxx
                len = 2;
            } else if ((head & mask5) == mask4) { // 1111 0xxx
                len = 3;
            } else {
                return false;
            }
            for (int j = 0; j < len; j++) {
                if (++i == data.length) { // need more byte
                    return false;
                }
                if ((data[i] & mask2) != mask1) { // not fllowed by 10xx xxxx
                    return false;
                }
            }
        }
        return true;
    }

}
```

#### 结果
![utf8-validation-1](/images/leetcode/utf8-validation-1.png)
