---
layout: post
title: "Leetcode - Algorithm - Longest Substring Without Repeating Characters"
date: 2017-03-13 21:21:46
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode"]
description: >
---

### 题目
Given a string, find the length of the longest substring without repeating characters.

Examples:

Given `abcabcbb`, the answer is `abc`, which the length is 3.

Given `bbbbb`, the answer is `b`, with the length of 1.

Given `pwwkew`, the answer is `wke`, with the length of 3. Note that the answer must be a substring, `pwke` is a subsequence and not a substring.

### 朴素解法
老老实实从第一个字符开始，两层迭代。
朴素解法通过了所有测试，但是超时。没有被`accepted`。
```java
public int lengthOfLongestSubstring(String s) {
    char[] chars = s.toCharArray();
    Set<Character> charSet = new LinkedHashSet<>();
    int maxSize = 0;
    outerIter:
    for (int i = 0; i < chars.length; i++) {
        if (chars.length - i < maxSize) { break; } // 剩下的字符串比maxSize小，停止遍历
        innerIter:
        for (int j = i; j < chars.length; j++) {
            if (charSet.contains(chars[j])) {
                if (charSet.size() > maxSize) {
                    maxSize = charSet.size();
                }
                charSet.clear();
                break innerIter;
            }
            charSet.add(chars[j]);
        }
        if (charSet.size() > maxSize) {
            maxSize = charSet.size();
        }
        charSet.clear();
    }
    return maxSize;
}
```

#### 结果
通过了所有测试，但是超时。
![longest-substring-1](/images/leetcode/longest-substring-1.png)
