---
layout: post
title: "Leetcode - Algorithm - Reverse Words In A String "
date: 2017-09-05 16:09:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "easy"
description: >
---

### 题目
Given a string, you need to reverse the order of characters in each word within a sentence while still preserving whitespace and initial word order.

Example 1:
```
Input: "Let's take LeetCode contest"
Output: "s'teL ekat edoCteeL tsetnoc"
```

### 可以用`StringBuilder#reverse()`

#### 代码
```java
class Solution {
    private static final String SPACE = " ";
    public String reverseWords(String s) {
        String[] words = s.split(SPACE);
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(new StringBuilder(word).reverse().append(SPACE));
        }
        return sb.substring(0,sb.length()-1);
    }
}
```

#### 结果
![reverse-words-in-a-string-three-1](/images/leetcode/reverse-words-in-a-string-three-1.png)


### 也可以在数组里交换元素

#### 代码
```java
class Solution {
    private final String SPACE = " ";
    public String reverseWords(String s) {
        String[] words = s.split(SPACE);
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            char[] ca = word.toCharArray();
            int lo = 0, hi = ca.length-1;
            while (lo < hi) { exch(ca,lo++,hi--); }
            sb = sb.append(ca).append(SPACE);
        }
        return sb.substring(0,sb.length()-1);
    }
    private void exch(char[] ca, int lo, int hi) {
        char temp = ca[lo];
        ca[lo] = ca[hi];    
        ca[hi] = temp;
    }
}
```

#### 结果
![reverse-words-in-a-string-three-2](/images/leetcode/reverse-words-in-a-string-three-2.png)
