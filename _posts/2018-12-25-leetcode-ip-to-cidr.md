---
layout: post
title: "Leetcode - Algorithm - Ip To Cidr "
date: 2018-12-25 20:23:13
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "math"]
level: "easy"
description: >
---

### 题目
Given a start IP address ip and a number of ips we need to cover n, return a representation of the range as a list (of smallest possible length) of CIDR blocks.

A CIDR block is a string consisting of an IP, followed by a slash, and then the prefix length. For example: `123.45.67.89/20`. That prefix length `20` represents the number of common prefix bits in the specified range.

Example 1:
```
Input: ip = "255.0.0.7", n = 10
Output: ["255.0.0.7/32","255.0.0.8/29","255.0.0.16/32"]
Explanation:
The initial ip address, when converted to binary, looks like this (spaces added for clarity):
255.0.0.7 -> 11111111 00000000 00000000 00000111
The address "255.0.0.7/32" specifies all addresses with a common prefix of 32 bits to the given address,
ie. just this one address.

The address "255.0.0.8/29" specifies all addresses with a common prefix of 29 bits to the given address:
255.0.0.8 -> 11111111 00000000 00000000 00001000
Addresses with common prefix of 29 bits are:
11111111 00000000 00000000 00001000
11111111 00000000 00000000 00001001
11111111 00000000 00000000 00001010
11111111 00000000 00000000 00001011
11111111 00000000 00000000 00001100
11111111 00000000 00000000 00001101
11111111 00000000 00000000 00001110
11111111 00000000 00000000 00001111

The address "255.0.0.16/32" specifies all addresses with a common prefix of 32 bits to the given address,
ie. just 11111111 00000000 00000000 00010000.

In total, the answer specifies the range of 10 ips starting with the address 255.0.0.7 .

There were other representations, such as:
["255.0.0.7/32","255.0.0.8/30", "255.0.0.12/30", "255.0.0.16/32"],
but our answer was the shortest possible.

Also note that a representation beginning with say, "255.0.0.7/30" would be incorrect,
because it includes addresses like 255.0.0.4 = 11111111 00000000 00000000 00000100
that are outside the specified range.
```

Note:
* ip will be a valid IPv4 address.
* Every implied address `ip + x (for x < n)` will be a valid IPv4 address.
* n will be an integer in the range `[1, 1000]`.

### 例子分析
把问题简化到`8`位，比如起始IP是`[40]`，求之后的`5`个IP，
```
    128 64 32 16 8 4  2 1  
       \ \  | | / /   | |
mask:   1 1 1 1 1 1 | 0 0   -> /30
--------------------+----
40 =    0 0 1 0 1 0 | 0 0
41 =    0 0 1 0 1 0 | 0 1
42 =    0 0 1 0 1 0 | 1 0
43 =    0 0 1 0 1 0 | 1 1

mask:   1 1 1 1 1 1 1 1 |  -> /32
------------------------+
44 =    0 0 1 0 1 1 0 0 |
```

这题的关键在于：
> **数字二进制表示中的`0`位，他们才有填补的空间**

```
128 64  32  16  8   4   2   1  
1   1   1   1   1   1   1   1

0   0   1   0   1   0   0   0  = 40
                |
            最后的1位，对应的是8，就是后面3个0有8(2^3)个数字的变化空间

此时我们的目标是5，也就是最后的8个空间能容纳下。

5 = 4 + 1

4 对应的掩码是： 11111100 -> 30
1 对应的掩码是： 11111111 -> 32
```

但如果，目标数字是`13`，最后的`8`位容纳不下的话，
```
先把最后3位0的8个变换空间全部利用上，

1   1   1   1   1   0   0   0  -> 40/29
-----------------------------
0   0   1   0   1   0   0   0  -> 40
            ... ...
            ... ...
0   0   1   0   1   1   1   1  -> 47


接下来从，48开始，13 - 8 = 5，还需要5个空间，

0   0   1   1   0   0   0   0  -> 48

5 = 4 + 1

4 对应的掩码是： 11111100 -> 30

1   1   1   1   1   1 | 0   0  -> 48/30
----------------------+------
0   0   1   1   0   0 | 0   0  -> 48
0   0   1   1   0   0 | 0   1  -> 49
0   0   1   1   0   0 | 1   0  -> 50
0   0   1   1   0   0 | 1   1  -> 51

1 对应的掩码是： 11111111 -> 32

1   1   1   1   1   1   1   1  -> 52/32
-----------------------------
0   0   1   1   0   1   0   0  -> 52
```

#### 代码
```java
class Solution {
    private static int[] table = new int[]{1045, 1044, 1043, 1042, 1041, 1040, 1039, 1038, 1037, 1036, 1035, 1034, 1033, 1032, 1031, 1030, 1029, 1028, 1027, 1026, 1025, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1};

    public List<String> ipToCIDR(String ip, int n) {
        List<String> res = new ArrayList<>();
        char[] ca = toBinaryIP(ip);
        int idx = 31;
        while (idx > 24 && ca[idx] == '0') idx--;
        while (n > table[idx]) {
            res.add(getCIDR(ca, idx + 1));
            n -= table[idx];
            while (ca[idx] == '1') ca[idx--] = '0';
            ca[idx] = '1';
        }
        while (n > 0) {
            idx = idxOf(n);
            res.add(getCIDR(ca, idx + 1));
            ca[idx] = '1';
            n -= table[idx];
        }
        return res;
    }

    private char[] toBinaryIP(String ip) {
        char[] cs = new char[32];
        String[] segs = ip.split("\\.");
        int p = 0;
        for (String seg : segs) {
            int segNum = Integer.parseInt(seg);
            int mask = 128;
            for (int i = 0; i < 8; i++) {
                if (segNum >= mask) {
                    cs[p++] = '1';
                    segNum -= mask;
                } else {
                    cs[p++] = '0';
                }
                mask >>= 1;
            }
        }
        return cs;
    }

    private String toDecimalIP(char[] ip) {
        StringBuilder sb = new StringBuilder();
        int p = 0;
        for (int i = 0; i < 4; i++) {
            int seg = 0, mask = 128;
            for (int j = 0; j < 8; j++) {
                if (ip[p++] == '1') seg += mask;
                mask >>= 1;
            }
            sb.append(String.valueOf(seg)).append(".");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String getCIDR(char[] ip, int mask) {
        return toDecimalIP(ip) + "/" + String.valueOf(mask);
    }

    private int idxOf(int n) {
        for (int i = 31; i >= 0; i--) {
            if (table[i] > n) return i + 1;
        }
        return -1;
    }
}
```

#### 结果
![ip-to-cidr-1](/images/leetcode/ip-to-cidr-1.png)
