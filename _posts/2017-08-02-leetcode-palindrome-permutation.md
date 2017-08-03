---
layout: post
title: "Leetcode - Algorithm - Palindrome Permutation "
date: 2017-08-02 15:17:33
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash set"]
level: "easy"
description: >
---

### 题目
Given a string, determine if a permutation of the string could form a palindrome.

For example,
`code` -> False, `aab` -> True, `carerac` -> True.

### 用`HashSet`记录遇到过的字符
回文的特性就是：字符都是成对出现。成对抵消之后，最多只留下中间的单核。

这里不能用`XOR`异或操作来抵消字符，是因为多字符的`XOR`异或混合结果不确定，完全可以还是落在正常字符的编码空间，或者为零。比如`a^b^d^g = 0`。但他们并没有两两抵消。

#### 代码
```java
public class Solution {
    public boolean canPermutePalindrome(String s) {
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!set.add(c)) { set.remove(c); }
        }
        return set.size() < 2;
    }
}
```

#### 结果
![palindrome-permutation-1](/images/leetcode/palindrome-permutation-1.png)
