---
layout: post
title: "Leetcode - Algorithm - Valid Anagram "
date: 2017-07-13 19:26:01
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","array"]
level: "easy"
description: >
---

### 题目
Given two strings s and t, write a function to determine if t is an anagram of s.

For example,
```
s = "anagram", t = "nagaram", return true.
s = "rat", t = "car", return false.
```

Note:
You may assume the string contains only lowercase alphabets.

Follow up:
What if the inputs contain unicode characters? How would you adapt your solution to such case?

### 主要思路
字母出现的顺序信息不重要，但出现的次数是重要的。所以只需要用一个表记录字母出现顺序就好。
如果只有非常有限的字符集合，就可以用数组，记录次数信息。
如果字符空间很大，就需要一个`Map`来记录次数。

### 用一个`Map`记录每个字母出现的次数
* time: $$O(n)$$
* space: $$O(n)$$

还不够高效，因为容器本身的开销。

#### 代码
```java
public class Solution {
    public boolean isAnagram(String s, String t) {
        Map<Character,Integer> freq = new HashMap<>();
        int len = s.length();
        if (t.length() != len) { return false; }
        for (int i = 0; i < 26; i++) {
            freq.put((char)(i+'a'),0);
        }
        for (int i = 0; i < len; i++) {
            Character c = s.charAt(i);
            freq.put(c,freq.get(c)+1);
        }
        for (int i = 0; i < len; i++) {
            Character c = t.charAt(i);
            freq.put(c,freq.get(c)-1);
        }
        for (Map.Entry<Character,Integer> entry : freq.entrySet()) {
            if (entry.getValue() != 0) { return false; }
        }
        return true;
    }
}
```

#### 结果
![valid-anagram-1](/images/leetcode/valid-anagram-1.png)


### 用数组记录字母出现的次数
* time: $$O(n)$$
* space: $$O(n)$$

复杂度相同，省去了容器的开销。

#### Java代码
```java
public class Solution {
    public boolean isAnagram(String s, String t) {
        int len = s.length();
        if (t.length() != len) { return false; }
        int[] freq = new int[26];
        for (int i = 0; i < len; i++) {
            freq[s.charAt(i)-'a']++;
            freq[t.charAt(i)-'a']--;
        }
        for (int i = 0; i < 26; i++) {
            if (freq[i] != 0) { return false; }
        }
        return true;
    }
}
```

#### 结果
![valid-anagram-2](/images/leetcode/valid-anagram-2.png)


#### C代码
```c
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

static bool isAnagram(char *, char *);

static bool isAnagram(char *s, char *t) {
    int len = strlen(s);
    if (strlen(t) != len) { return false; }
    int freq[26] = {0}; // the local array will not be initialized to 0 if I declare by with "freq[26];"
    for (int i = 0; i < len; i++) {
        freq[*s++ - 'a']++;
        freq[*t++ - 'a']--;
    }
    for (int i = 0; i < 26; i++) {
        if (freq[i]) { return false; }
    }
    return true;
}
```

#### 结果
![valid-anagram-3](/images/leetcode/valid-anagram-3.png)

### 先排序
也可以先对两个字符串排序，然后再比较。
* time: $$O(n\log_{}{n})$$
* space: $$0$$

直接调用库函数就可以，代码不写了。
