---
layout: post
title: "Leetcode - Algorithm - Count Binary Substrings "
date: 2018-01-04 21:41:05
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "easy"
description: >
---

### 题目
Give a string s, count the number of non-empty (contiguous) substrings that have the same number of 0's and 1's, and all the 0's and all the 1's in these substrings are grouped consecutively.

Substrings that occur multiple times are counted the number of times they occur.

Example 1:
```
Input: "00110011"
Output: 6
Explanation: There are 6 substrings that have equal number of consecutive 1's and 0's: "0011", "01", "1100", "10", "0011", and "01".
```
Notice that some of these substrings repeat and are counted the number of times they occur.

Also, "00110011" is not a valid substring because all the 0's (and 1's) are not grouped together.

Example 2:
```
Input: "10101"
Output: 4
Explanation: There are 4 substrings: "10", "01", "10", "01" that have equal number of consecutive 1's and 0's.
```
Note:
* s.length will be between 1 and 50,000.
* s will only consist of "0" or "1" characters.

### 只有`01`和`10`两种核
虽然表述起来很复杂：“相同数量的连续的0和1”，而且符合条件的序列看起来也很复杂，比如"0011", "01", "1100", "10", "0011", and "01"。但实际上所有符合条件的序列只可能有两种内核：`01`和`10`，然后就是在此基础上的扩展。
```
        01              <- 1号内核
       0011
      000111
     00001111
       ....

        10              <- 2号内核
       1100
      111000
     11110000
       ....
```

#### 代码
```java
class Solution {
    public int countBinarySubstrings(String s) {
        if (s == null || s.length() < 2) { return 0; }
        int len = s.length();
        int count = 0;
        int pre = (int)s.charAt(0), curr = 0;
        for (int i = 1; i < len;) {
            curr = (int)s.charAt(i);
            if (curr != pre) {
                int pl = i-1, pr = i;
                int left = (int)s.charAt(pl), right = (int)s.charAt(pr);
                while ((pl >= 0) && (pr < len) && ((int)s.charAt(pl) == left) && ((int)s.charAt(pr) == right)) {
                    count++;
                    pl--; pr++;
                }
                i = pr;
            } else {
                i++;
            }
            pre = curr;
        }
        return count;
    }
}
```

#### 结果
![count-binary-substrings-1](/images/leetcode/count-binary-substrings-1.png)
