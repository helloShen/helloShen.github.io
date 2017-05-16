---
layout: post
title: "Leetcode - Algorithm - Valid Palindrome "
date: 2017-05-15 22:07:13
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["two pointers","string"]
level: "easy"
description: >
---

### 题目
Given a string, determine if it is a palindrome, considering only alphanumeric characters and ignoring cases.

For example,
`A man, a plan, a canal: Panama` is a palindrome.
`race a car` is not a palindrome.

Note:
Have you consider that the string might be empty? This is a good question to ask during an interview.

For the purpose of this problem, we define empty string as valid palindrome.

### 利用首尾两个指针，$$O(n)$$
一个指针指`lo`向头部`0`，一个指针`hi`指向尾部`length-1`。同时向中心推进。以`lo > hi`为终止条件。

#### 代码
```java
public class Solution {
    public boolean isPalindrome(String s) {
        int lo = 0, hi = s.length()-1;
        while (lo <= hi) {
            if (!Character.isLetterOrDigit(s.charAt(lo))) { lo++; continue; }
            if (!Character.isLetterOrDigit(s.charAt(hi))) { hi--; continue; }
            if (Character.toLowerCase(s.charAt(lo++)) != Character.toLowerCase(s.charAt(hi--))) { return false; }
        }
        return true;
    }
}
```

#### 结果
银弹！
![valid-palindrome-1](/images/leetcode/valid-palindrome-1.png)
