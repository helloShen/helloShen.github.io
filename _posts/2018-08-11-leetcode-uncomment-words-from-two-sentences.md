---
layout: post
title: "Leetcode - Algorithm - Uncomment Words From Two Sentences "
date: 2018-08-11 23:49:18
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["list","set"]
level: "easy"
description: >
---

### 题目
We are given two sentences `A` and `B`.  (A sentence is a string of space separated words.  Each word consists only of lowercase letters.)

A word is uncommon if it appears exactly once in one of the sentences, and does not appear in the other sentence.

Return a list of all uncommon words.

You may return the list in any order.

Example 1:
```
Input: A = "this apple is sweet", B = "this apple is sour"
Output: ["sweet","sour"]
Example 2:

Input: A = "apple apple", B = "banana"
Output: ["banana"]
```

Note:
* 0 <= A.length <= 200
* 0 <= B.length <= 200
* A and B both contain only spaces and lowercase letters.

### 先找出所有重复元素
考虑下面两个字符串，
```
A = [a,b,c,d,e,f,a,b]
B = [a,b,c,g,h,i,g,d]
```
首先，在两个字符串中，找出重复元素，
```
    原数组                 重复
A = [a,b,c,d,e,f,a,b] ->  [a,b]
B = [a,b,c,g,h,i,g,d] ->  [g]
```
从列表中剔除所有重复元素，
```
A = [c,d,e,f]
B = [a,b,c,d,h,i]
```
最后再剔除`A`和`B`中的重复元素，
```
A = [c,d,e,f]       -+
                     +-> [a,b,e,f,h,i]
B = [a,b,c,d,h,i]   -+
```

#### 代码
```java
class Solution {
    public String[] uncommonFromSentences(String A, String B) {
        String[] wordsA = A.split(" ");
        String[] wordsB = B.split(" ");
        Set<String> duplicate = new HashSet<>();
        Set<String> set = new HashSet<>();
        for (String word : wordsA) {
            if (!set.add(word)) {
                duplicate.add(word);
            }
        }
        for (String word : wordsB) {
            if (!set.add(word)) {
                duplicate.add(word);
            }
        }
        set.removeAll(duplicate);
        return set.toArray(new String[set.size()]);
    }
}
```
