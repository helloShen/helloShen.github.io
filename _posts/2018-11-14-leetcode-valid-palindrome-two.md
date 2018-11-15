---
layout: post
title: "Leetcode - Algorithm - Valid Palindrome Two "
date: 2018-11-14 20:32:54
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["palindrome", "string"]
level: "easy"
description: >
---

### 题目
Given a non-empty string s, you may delete at most one character. Judge whether you can make it a palindrome.

Example 1:
```
Input: "aba"
Output: True
```

Example 2:
```
Input: "abca"
Output: True
Explanation: You could delete the character 'c'.
```

Note:
* The string will only contain lowercase characters a-z. The maximum length of the string is 50000.

### 问题分析：“核心”是固定的
这个问题看上去可能性非常多，多余的字符可能出现在任意位置。但根据回文串的特性，回文串的“核心”位置其实几乎是固定的，唯一可能的变数，只存在两种情况：

##### 1. 字符串长度为“奇数”
假设字符串为`cbbcd`，长度为`5`，是奇数。如果只有一个字符多余，最终的合法回文串长度会是`4`，长度是偶数。回文串的基本性质就是，长度为偶数的时候，核心为 **“两个字符”**。2个字符的核心，只有两种情况：
* 多余字符在后半部分：
```
 核心
 ||
cbbcd
```
* 或者，多余字符在前半部分，
```
 核心
  ||
cbbcd
```

##### 2. 字符串长度为“偶数”
与之对应，如果字符串长度为“偶数”，最终的回文串长度会是“奇数”。那么核心只有 **“一个字符”**。 同样一个核心字符的位置也只有两种情况，
* 多余字符在后半部分：
```
 核心
  |
cbbcbc
```
* 或者，多余字符在前半部分，
```
  核心
   |
cbbcbc
```

所以无论最终是哪种情况，只需要做2次尝试，就能找到答案。

#### 代码
```java
class Solution {
    public boolean validPalindrome(String s) {
        char[] cArr = s.toCharArray();
        return isPalindrome(cArr, true) || isPalindrome(cArr, false);
    }

    private boolean isPalindrome(char[] arr, boolean inFirstHalf) {
        int len = arr.length;
        boolean ignored = false;
        int lo = -2, hi = -2;
        if (len % 2 == 0) { // palindrome is odd, 1 core
            lo = (len - 1) / 2;
            hi = lo;
        } else { // palindrome is even, 2 core
            lo = (len - 1) / 2 - 1;
            hi = lo + 1;
        }
        if (inFirstHalf) {
            lo++;
            hi++;
        }
        while (lo >= 0 && hi < len) {
            if (arr[lo--] != arr[hi++]) {
                if (!ignored) {
                    if (inFirstHalf) {
                        hi--;
                    } else {
                        lo++;
                    }
                    ignored = true;

                } else {
                    return false;
                }
            }
        }
        if (!ignored) {
            if (inFirstHalf) {
                lo--;
            } else {
                hi++;
            }
        }
        return (lo == -1 && hi == len);
    }
}
```

#### 结果
![valid-palindrome-two-1](/images/leetcode/valid-palindrome-two-1.png)
