---
layout: post
title: "Leetcode - Algorithm - Longest Common Prefix "
date: 2017-03-27 00:05:03
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode"]
level: "easy"
description: >
---

### 题目
Write a function to find the longest common prefix string amongst an array of strings.

Ex: For `aaabbb` and `aaaccc`, their common prefix is `aaa`。

### 暴力遍历 $$O(n^{2})$$
从头开始遍历，找到反例就返回当前最长共有前缀。一开始就确定最短字符串的长度，减少每次判断的开销。

#### 代码
```java
public class Solution {
    public String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) { return ""; }
        if (strs.length == 1) { return strs[0]; }
        int maxLen = Integer.MAX_VALUE;
        for (String str : strs) {
            maxLen = Math.min(maxLen,str.length());
        }
        outerFor:
        for (int i = 0; i < maxLen; i++) {
            char c = strs[0].charAt(i);
            for (String str : strs) {
                if (str.charAt(i) != c) {
                    return strs[0].substring(0,i);
                }
            }
        }
        return strs[0].substring(0,maxLen);
    }
}
```

#### 结果
结果已经不错。
![longest-common-prefix-1](/images/leetcode/longest-common-prefix-1.png)

### 递归分治二分查找 $$O(n\log{}{n})$$
这题不适合用分治二分查找。因为前缀相同的条件是每个字符都相同，二分查找如果中位字符相同，不能说明问题，还需要继续进行左递归和右递归。最坏情况是: $$f(n)=2f(n/2)+n$$，根据主定理的`case 2`: 复杂度渐进于$$O(n\log{}{n})$$。

#### 代码
```java
public class Solution {
    public String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) { return ""; }
        if (strs.length == 1) { return strs[0]; }
        int maxLen = Integer.MAX_VALUE;
        for (String str : strs) { // 每层递归代价： O(n)
            maxLen = Math.min(maxLen,str.length());
        }
        return longestCommonPrefixRecursive(strs,0,maxLen-1);
    }
    private String longestCommonPrefixRecursive(String[] strs, int low, int high) {
        System.out.println("low = " + low + ", high = " + high);
        // base case
        if (high - low < 0) {
            return "";
        }
        int median = low + ((high-low)/2);
        char c = strs[0].charAt(median);
        for (String str : strs) {
            if (str.charAt(median) != c) {
                return longestCommonPrefixRecursive(strs,low,median-1);
            }
        }
        String leftPrefix = longestCommonPrefixRecursive(strs,low,median-1);
        if (leftPrefix.length() == (median-low) || (median - low) < 0) {
            return leftPrefix + c + longestCommonPrefixRecursive(strs,median+1,high);
        } else {
            return leftPrefix;
        }
    }
}
```

#### 结果
![longest-common-prefix-2](/images/leetcode/longest-common-prefix-2.png)
