---
layout: post
title: "Leetcode - Algorithm - Validate Ip Adress "
date: 2018-11-01 01:25:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "medium"
description: >
---

### 题目
Write a function to check whether an input string is a valid IPv4 address or IPv6 address or neither.

IPv4 addresses are canonically represented in dot-decimal notation, which consists of four decimal numbers, each ranging from `0` to `255`, separated by dots (`.`), e.g.,`172.16.254.1`;

Besides, leading zeros in the IPv4 is invalid. For example, the address `172.16.254.01` is invalid.

IPv6 addresses are represented as eight groups of four hexadecimal digits, each group representing 16 bits. The groups are separated by colons (`:`). For example, the address `2001:0db8:85a3:0000:0000:8a2e:0370:7334` is a valid one. Also, we could omit some leading zeros among four hexadecimal digits and some low-case characters in the address to upper-case ones, so `2001:db8:85a3:0:0:8A2E:0370:7334` is also a valid IPv6 address(Omit leading zeros and using upper cases).

However, we don't replace a consecutive group of zero value with a single empty group using two consecutive colons (`::`) to pursue simplicity. For example, `2001:0db8:85a3::8A2E:0370:7334` is an invalid IPv6 address.

Besides, extra leading zeros in the IPv6 is also invalid. For example, the address `02001:0db8:85a3:0000:0000:8a2e:0370:7334` is invalid.

Note: You may assume there is no extra space or special characters in the input string.

Example 1:
```
Input: "172.16.254.1"

Output: "IPv4"

Explanation: This is a valid IPv4 address, return "IPv4".
```

Example 2:
```
Input: "2001:0db8:85a3:0:0:8A2E:0370:7334"

Output: "IPv6"

Explanation: This is a valid IPv6 address, return "IPv6".
```

Example 3:
```
Input: "256.256.256.256"

Output: "Neither"

Explanation: This is neither a IPv4 address nor a IPv6 address.
```

### 单纯的字符串处理，注意边角情况
首先`IP4`和`IP6`的处理方法是不同的，所以要分开处理。

处理`IP4`的时候，有下面几条原则，
1. 必须以`.`分割
2. 分割出来的数字必须有`4`段
3. 所有数字都必须是十进制，只有`[0~9]`，而且不能有`+`,`-`号（`+`,`-`号会被`parseInt()`函数认为是合法的）
4. 所有数字必须在`[0~255]`区间的十进制（所有`parseInt()`函数认为不合法的表达式都判不合法）
5. 以`0`开头的只能是`0`

处理`IP6`的规则如下，
1. 必须以`:`分割
2. 分割出来必须是`8`段，每段不能为空，最长不超过`4`个字符
3. 所有数字都必须是十六进制，不能有`+`,`-`号

#### 代码
```java
class Solution {
    public String validIPAddress(String IP) {
        if (IP.contains(".")) return (isValidIP4(IP))? "IPv4" : "Neither";
        if (IP.contains(":")) return (isValidIP6(IP))? "IPv6" : "Neither";
        return "Neither";
    }

    private boolean isValidIP4(String IP) {
        String[] segments = IP.split("\\.");
        if (segments.length != 4 || IP.charAt(IP.length() - 1) == '.') return false;
        for (String seg : segments) {
            if (seg.length() == 0) return false;
            char c0 = seg.charAt(0);
            if (c0 == '+' || c0 == '-' || (c0 == '0' && (!seg.equals("0")))) return false;
            try {
                int num = Integer.parseInt(seg);
                if (num < 0 || num > 255) return false;
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
        return true;
    }


    private boolean isValidIP6(String IP) {
        int segLen = 0, segCount = 0;
        for (int i = 0; i < IP.length(); i++) {
            char c = IP.charAt(i);
            if (!isValidIP6Char(c)) return false;
            if (c == ':') {
                if (segLen == 0) return false;
                segLen = 0;
                segCount++;
                continue;
            }
            if (++segLen > 4) return false;
        }
        return segCount == 7 && segLen > 0;
    }

    private boolean isValidIP6Char(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F') || (c == ':');
    }
}
```

#### 结果
![validate-ip-adress-1](/images/leetcode/validate-ip-adress-1.png)
