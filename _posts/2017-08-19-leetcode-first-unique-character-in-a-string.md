---
layout: post
title: "Leetcode - Algorithm - First Unique Character In A String "
date: 2017-08-19 12:33:07
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","array"]
level: "easy"
description: >
---

### 题目
Given a string, find the first non-repeating character in it and return it's index. If it doesn't exist, return -1.

Examples:
```
s = "leetcode"
return 0.

s = "loveleetcode",
return 2.
```
Note: You may assume the string contain only lowercase letters.

### 基本思路
用`Map`去记录每个字符的频率，肯定能完成任务。复杂度 $$O(n)$$ 也不高。 但如果考虑到只有26个小写字母，用一个数组去记录这些频率效率更高。

### 用`Map`记录频率

#### 代码
```java
class Solution {
    public int firstUniqChar(String s) {
        Map<Character,Integer> dic = loadDictionary(s);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (dic.get(c) == 1) { return i; }
        }
        return -1;
    }
    private Map<Character,Integer> loadDictionary(String s) {
        Map<Character,Integer> map = new HashMap<>();
        Character c = null;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            map.put(c,(map.containsKey(c))? map.get(c)+1 : 1);
        }
        return map;
    }
}
```

#### 结果
![first-unique-character-in-a-string-1](/images/leetcode/first-unique-character-in-a-string-1.png)


### 用数组记录频率

#### 代码
```java
class Solution {
    public int firstUniqChar(String s) {
        int len = s.length();
        int[] charOffset = new int[len];
        int[] charFreq = new int[26];
        for (int i = 0; i < len; i++) {
            int offset = s.charAt(i) - 'a';
            charOffset[i] = offset;
            charFreq[offset]++;
        }
        int min = 26;
        for (int i = 0; i < len; i++) {
            if (charFreq[charOffset[i]] == 1) { return i; }
        }
        return -1;
    }
}
```

#### 结果
![first-unique-character-in-a-string-2](/images/leetcode/first-unique-character-in-a-string-2.png)
