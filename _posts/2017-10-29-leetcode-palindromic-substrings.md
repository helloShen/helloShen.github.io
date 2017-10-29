---
layout: post
title: "Leetcode - Algorithm - Palindromic Substrings "
date: 2017-10-29 18:26:31
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","string","array"]
level: "medium"
description: >
---

### 题目
Given a string, your task is to count how many palindromic substrings in this string.

The substrings with different start indexes or end indexes are counted as different substrings even they consist of same characters.

Example 1:
```bash
Input: "abc"
Output: 3
```
Explanation: Three palindromic strings: "a", "b", "c".

Example 2:
```bash
Input: "aaa"
Output: 6
```
Explanation: Six palindromic strings: "a", "a", "a", "aa", "aa", "aaa".

Note:
The input string length won't exceed 1000.

### 朴素扩展法, $$O(n^2)$$
遍历字符串，以每个字母为中心，向外扩展，检查是否是回文。
```bash
    core
 <-  |  ->
longtimenosee


     core
  <-  |  ->
longtimenosee


      core
   <-  |  ->
longtimenosee

...
...
```

#### 代码
```java
class Solution {
    private char[] str = new char[0];
    private int count;

    public int countSubstrings(String s) {
        init(s);
        for (int i = 0; i < str.length; i++) {
            extendPalindrome(i,i);      // odd
            extendPalindrome(i,i+1);    // even
        }
        return count;
    }
    private void init(String s) {
        str = s.toCharArray();
        count = 0;
    }
    private void extendPalindrome(int lo, int hi) {
        while (lo >= 0 && hi < str.length && (str[lo--] == str[hi++])) {
            count++;
        }
    }
}
```

#### 结果
![palindromic-substrings-1](/images/leetcode/palindromic-substrings-1.png)


### 动态规划， $$O(n^3)$$
已知`aaa`有6组回文子串的情况下，怎么计算`aaaa`? 只需要检查所有包含最后一个`a`字符的子串即可，
```bash
 new char
    |
aaa[a]
aa[aa]
a[aaa]
[aaaa]
```

但这里动态规划的复杂度反而更高。

#### 代码
```java
class Solution {
    private static char[] str = new char[0];

    public int countSubstrings(String s) {
        init(s);
        return dp(0);
    }
    private void init(String s) {
        str = s.toCharArray();
    }
    private int dp(int start) {
        if (start == str.length - 1) { return 1; }
        int sub = dp(start+1);
        for (int i = start; i < str.length; i++) {
            if (isPalindrome(start,i)) {
                sub++;
            }
        }
        return sub;
    }
    private boolean isPalindrome(int lo, int hi) {
        while (lo < hi) {
            if (str[lo++] != str[hi--]) {
                return false;
            }
        }
        return true;
    }
}
```

#### 结果
![palindromic-substrings-2](/images/leetcode/palindromic-substrings-2.png)
