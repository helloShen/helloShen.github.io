---
layout: post
title: "Leetcode - Algorithm - Ransom Note "
date: 2017-08-23 19:18:17
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string","array"]
level: "easy"
description: >
---

### 题目
Given an arbitrary ransom note string and another string containing letters from all the magazines, write a function that will return true if the ransom note can be constructed from the magazines ; otherwise, it will return false.

Each letter in the magazine string can only be used once in your ransom note.

Note:
You may assume that both strings contain only lowercase letters.
```
canConstruct("a", "b") -> false
canConstruct("aa", "ab") -> false
canConstruct("aa", "aab") -> true
```

### 基本思路
朴素的做法，把字符串当成一个字符列表来处理。复杂度 $$O(m*n)$$。 `m`是`ransomNote`的长度，`n`是`magazine`的长度。

高级一点，可以把字符串统计成字符的频率表。可以用一个`Map`，也可以用一个`int[26]`就够了。复杂度 $$O(m+n)$$。

### 解法一：用列表

#### 代码
```java
class Solution {
    public boolean canConstruct(String ransomNote, String magazine) {
        List<Character> mgz = new LinkedList<>();
        for (int i = 0; i < magazine.length(); i++) {
            mgz.add(magazine.charAt(i));
        }
        for (int i = 0; i < ransomNote.length(); i++) {
            if (!mgz.remove((Character)ransomNote.charAt(i))) { return false; }
        }
        return true;
    }
}
```

#### 结果
![ransom-note-1](/images/leetcode/ransom-note-1.png)


### 解法二：统计字符频率

#### 代码
```java
class Solution {
    public boolean canConstruct(String ransomNote, String magazine) {
        int[] freq = countFreq(magazine);
        for (int i = 0; i < ransomNote.length(); i++) {
            int offset = ransomNote.charAt(i) - 'a';
            if (freq[offset] > 0) {
                --freq[offset];
            } else {
                return false;
            }
        }
        return true;
    }
    private int[] countFreq(String magazine) {
        int[] freq = new int[26];
        for (int i = 0; i < magazine.length(); i++) {
            ++freq[magazine.charAt(i)-'a'];
        }
        return freq;
    }
}
```

#### 结果
![ransom-note-2](/images/leetcode/ransom-note-2.png)
