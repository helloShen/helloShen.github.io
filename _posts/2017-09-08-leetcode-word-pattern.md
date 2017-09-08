---
layout: post
title: "Leetcode - Algorithm - Word Pattern "
date: 2017-09-08 14:08:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","string"]
level: "easy"
description: >
---

### 题目
Given a pattern and a string str, find if str follows the same pattern.

Here follow means a full match, such that there is a bijection between a letter in pattern and a non-empty word in str.

Examples:
```
pattern = "abba", str = "dog cat cat dog" should return true.
pattern = "abba", str = "dog cat cat fish" should return false.
pattern = "aaaa", str = "dog cat cat dog" should return false.
pattern = "abba", str = "dog dog dog dog" should return false.
```
Notes:
* You may assume pattern contains only lowercase letters, and str contains lowercase letters separated by a single space.



### 用`Array`
因为Pattern里只有小写字母，所以映射空间是`26`。所以可以用`String[26]`来记录映射。 比如`pattern = "abba", str = "dog cat cat dog"`，`a`桶里记录`dog`，`b`桶里记录`cat`。

需要注意，在一个桶里记录对应单词的时候，还需要检查其他所有桶，确保其他桶里没有这个单词。

#### 代码
```java
class Solution {
    public boolean wordPattern(String pattern, String str) {
        String[] memo = new String[26];
        String[] words = str.split(" ");
        if (pattern.length() != words.length) { return false; }
        for (int i = 0; i < words.length; i++) {
            int offset = pattern.charAt(i) - 'a';
            String history = memo[offset];
            if (history == null) {
                for (int j = 0; j < 26; j++) {
                    String pre = memo[j];
                    if (pre != null && pre.equals(words[i])) { return false; }
                }
                memo[offset] = words[i];
            } else if (!history.equals(words[i])) {
                return false;
            }
        }
        return true;
    }
}
```

#### 结果
![word-pattern-1](/images/leetcode/word-pattern-1.png)
