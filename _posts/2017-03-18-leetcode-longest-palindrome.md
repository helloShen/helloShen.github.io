---
layout: post
title: "Leetcode - Algorithm - Longest Palindromic Substring"
date: 2017-03-18 00:39:39
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode","palindrome"]
level: "medium"
description: >
---

### 题目
![palindrome-string](/images/leetcode/Palindrome-String.png)

Given a string s, find the longest palindromic substring in s. You may assume that the maximum length of s is 1000.

Example:
```bash
Input: "babad"
Output: "bab"
```
Note: "aba" is also a valid answer.
Example:
```bash
Input: "cbbd"
Output: "bb"
```

### 朴素遍历
回文就是中心对称的单词。从字符串的中间开始，维护两个指针，逐渐向两边推进。每推进一格，分别以这两个指针指向的字符为中心，向两边扩散检查回文。注意，回文需要同时检查单核`aba`以及双核`abba`的情况。作为一种简单的优化，当检查出的最长回文已经大于当前指针离开两端的距离的两倍（说明不可能再超过这个最长回文了），停止迭代。
```java
public class Solution {
    public String longestPalindrome(String s) {
        //base case
        int length = s.length();
        if (length <= 1) { return s; }

        //取中位数
        int lowerCursor = 0;
        int upperCursor = 0;
        if (length%2 == 0) { //必须交叉，以覆盖正中间的回文
            lowerCursor = (length)/2;
            upperCursor = (length - 2)/2;
        } else {
            lowerCursor = (length - 1)/2;
            upperCursor = (length - 1)/2;
        }

        //迭代调用checkPalindromic()
        String subStr = "";
        int[] max = new int[]{1};
        String[] result = new String[]{s.substring(0,1)};
        while (lowerCursor > 0 && lowerCursor*2 > max[0]) {
            subStr = checkPalindromic(s,lowerCursor,lowerCursor);
            result = updateResult(subStr,max,result);
            subStr = checkPalindromic(s,lowerCursor,lowerCursor-1);
            result = updateResult(subStr,max,result);
            lowerCursor--;
            subStr = checkPalindromic(s,upperCursor,upperCursor);
            result = updateResult(subStr,max,result);
            subStr = checkPalindromic(s,upperCursor,upperCursor+1);
            result = updateResult(subStr,max,result);
            upperCursor++;
        }
        return result[0];
    }

    /**
     * 检查字符串s，从lower,upper位置扩散开，最长的回文。
     * 每个字母都与自己对称
     * s不为空
     */
    public static String checkPalindromic(String s, int lower, int upper) {
        int length = s.length();
        String result = s.substring(lower,lower+1);
        expansionLoop:
        while ((lower >= 0) && (upper < s.length())) {
            char lowerChar = s.charAt(lower);
            char upperChar = s.charAt(upper);
            if (Character.compare(lowerChar,upperChar) == 0) {
                result = s.substring(lower,upper+1);
            } else {
                break expansionLoop;
            }
            lower--;
            upper++;
        }
        return result;
    }

    /**
     * 为了传参数，所以用数组
     */
    public static String[] updateResult(String subStr, int[] max, String[] result) {
        int subLen = subStr.length();
        if (subLen > max[0]) {
            max[0] = subLen;
            result[0] = subStr;
        }
        return result;
    }
}
```

#### 朴素算法结果
尽管通过了，但结果不算好。在`10ms`的位置有一个高峰，说明有个更好的算法比我的`100ms`快10倍。
![longest-palindrome-1](/images/leetcode/longest-palindrome-1.png)
