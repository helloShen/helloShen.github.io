---
layout: post
title: "Leetcode - Algorithm - License Key Formatting "
date: 2018-09-11 18:08:15
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","string"]
level: "easy"
description: >
---

### 题目
You are given a license key represented as a string S which consists only alphanumeric character and dashes. The string is separated into N+1 groups by N dashes.

Given a number K, we would want to reformat the strings such that each group contains exactly K characters, except for the first group which could be shorter than K, but still must contain at least one character. Furthermore, there must be a dash inserted between two groups and all lowercase letters should be converted to uppercase.

Given a non-empty string S and a number K, format the string according to the rules described above.

Example 1:
```
Input: S = "5F3Z-2e-9-w", K = 4

Output: "5F3Z-2E9W"

Explanation: The string S has been split into two parts, each part has 4 characters.
Note that the two extra dashes are not needed and can be removed.
```

Example 2:
```
Input: S = "2-5g-3-J", K = 2

Output: "2-5G-3J"

Explanation: The string S has been split into three parts, each part has 2 characters except the first part as it could be shorter as mentioned above.
```

Note:
* The length of string S will not exceed 12,000, and K is a positive integer.
* String S consists only of alphanumerical characters (a-z and/or A-Z and/or 0-9) and dashes(-).
* String S is non-empty.


### 直接用String库和StringBuilder处理

#### 代码
```java
class Solution {
    public String licenseKeyFormatting(String S, int K) {
        S = S.toUpperCase();
        S = S.replaceAll("-","");
        StringBuilder sb = new StringBuilder(S);
        for (int end = sb.length(), start = end - K; start > 0; end = start, start = end - K) {
            sb.insert(start, '-');
        }
        return sb.toString();
    }
}
```

#### 结果
![license-key-formatting-1](/images/leetcode/license-key-formatting-1.png)


### 完全不用String和StringBuilder库，只用Array
关键是要先算出所有有效字符（字母或数字）的数量，然后根据这个数字算出最终字符串的长度。假设有效字符的数量是`count`，要求加连字符的窗口长度为`K`，那么最终字符串的长度等于，
> finalLength = count + (count / K) - ((count % K == 0)? 1 : 0)

唯一需要注意的细节就是当长度正好是`K`的整数倍的时候，比如`-xxxx-xxxx-xxxx`，开头的`-`要去掉。

#### 代码
```java
class Solution {

    private final char DASH = '-';

    public String licenseKeyFormatting(String S, int K) {
        char[] s = S.toCharArray();

        // calculate number of alphanumeric characters
        int count = 0;
        for (int i = 0; i < s.length; i++) {
            if (s[i] == DASH) { continue; }
            count++;
        }
        if (count == 0) { return ""; }

        // result array with certain length
        char[] arr = new char[count + (count / K) - ((count % K == 0)? 1 : 0)];
        // pointers
        int wp = 0;                 // pointer on window of K
        int sp = s.length - 1;      // pointer on input array
        int ap = arr.length - 1;    // pointer on result array

        while (sp >= 0) {
            if (wp == K && ap > 0) {
                arr[ap--] = DASH;
            }
            for (wp = 0; sp >= 0 && wp < K; wp++, sp--) {
                while (sp >= 0 && s[sp] == DASH) { sp--; } // skip dash
                if (sp < 0) { break; }
                char c = s[sp];
                if (c >= 'a' && c <= 'z') { // to uppercase
                    c -= 32;
                }
                arr[ap--] = c;
            }
        }
        return new String(arr);
    }
}
```

#### 结果
![license-key-formatting-2](/images/leetcode/license-key-formatting-2.png)
