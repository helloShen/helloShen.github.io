---
layout: post
title: "Leetcode - Algorithm - Find And Replace Pattern "
date: 2018-08-19 01:12:42
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash map"]
level: "medium"
description: >
---

### 题目
You have a list of words and a pattern, and you want to know which words in words matches the pattern.

A word matches the pattern if there exists a permutation of letters p so that after replacing every letter x in the pattern with p(x), we get the desired word.

(Recall that a permutation of letters is a bijection from letters to letters: every letter maps to another letter, and no two letters map to the same letter.)

Return a list of the words in words that match the given pattern.

You may return the answer in any order.



Example 1:
```
Input: words = ["abc","deq","mee","aqq","dkd","ccc"], pattern = "abb"
Output: ["mee","aqq"]
Explanation: "mee" matches the pattern because there is a permutation {a -> m, b -> e, ...}.
"ccc" does not match the pattern because {a -> c, b -> c, ...} is not a permutation,
since a and b map to the same letter.
```

Note:
* 1 <= words.length <= 50
* 1 <= pattern.length = words[i].length <= 20

### 关键是怎么把Pattern的信息抽象
比如`abb`，可以抽象为`122`，因为，这里面a是第一个出现的字母，b是第二个，然后b又重复了一遍。同样`mee`也是`122`。m是第一个出现，e第二个出现，然后e又重复了一遍。

原理就是这样，可以用一个`HashMap`来统计Pattern的抽象化数据。

#### 代码
```java
class Solution {
    public List<String> findAndReplacePattern(String[] words, String pattern) {
        List<String> result = new ArrayList<>();
        int[] extractedPattern = extractPattern(pattern);
        for (String word : words) {
            if (meetPattern(word,extractedPattern)) {
                result.add(word);
            }
        }
        return result;      
    }
    private int[] extractPattern(String pattern) {
        int[] extracted = new int[pattern.length()];
        Map<Character,Integer> map = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (!map.containsKey(c)) {
                map.put(c,map.size());
            }
            extracted[i] = map.get(c);            
        }
        return extracted;
    }

    private boolean meetPattern(String word, int[] pattern) {
        if (word.length() != pattern.length) {
            return false;
        }
        int [] wordPattern = extractPattern(word);
        for (int i = 0; i < wordPattern.length; i++) {
            if (wordPattern[i] != pattern[i]) {
                return false;
            }
        }
        return true;
    }
}
```
