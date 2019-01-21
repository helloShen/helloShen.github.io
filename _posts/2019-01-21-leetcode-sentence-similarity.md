---
layout: post
title: "Leetcode - Algorithm - Sentence Similarity "
date: 2019-01-21 17:49:31
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hashmap", "hashset"]
level: "easy"
description: >
---

### 题目
Given two sentences words1, words2 (each represented as an array of strings), and a list of similar word pairs pairs, determine if two sentences are similar.

For example, "great acting skills" and "fine drama talent" are similar, if the similar word pairs are pairs = [["great", "fine"], ["acting","drama"], ["skills","talent"]].

Note that the similarity relation is not transitive. For example, if "great" and "fine" are similar, and "fine" and "good" are similar, "great" and "good" are not necessarily similar.

However, similarity is symmetric. For example, "great" and "fine" being similar is the same as "fine" and "great" being similar.

Also, a word is always similar with itself. For example, the sentences words1 = ["great"], words2 = ["great"], pairs = [] are similar, even though there are no specified similar word pairs.

Finally, sentences can only be similar if they have the same number of words. So a sentence like words1 = ["great"] can never be similar to words2 = ["doubleplus","good"].

Note:
* The length of words1 and words2 will not exceed 1000.
* The length of pairs will not exceed 2000.
* The length of each pairs[i] will be 2.
* The length of each words[i] and pairs[i][j] will be in the range [1, 20].

### HashMap
把所有成对的单词都统计到一个`HashMap<String, Set<String>>`里。然后遍历数组，检查每一个配对。

#### 代码
```java
class Solution {
    public boolean areSentencesSimilar(String[] words1, String[] words2, String[][] pairs) {
        if (words1.length != words2.length) return false;
        Map<String, Set<String>> pairDic = new HashMap<>();
        for (String[] pair : pairs) {
            if (!pairDic.containsKey(pair[0])) pairDic.put(pair[0], new HashSet<String>());
            pairDic.get(pair[0]).add(pair[1]);
        }
        for (int i = 0; i < words1.length; i++) {
            if (words1[i].equals(words2[i])) continue;
            if (pairDic.containsKey(words1[i]) && pairDic.get(words1[i]).contains(words2[i])) continue;
            if (pairDic.containsKey(words2[i]) && pairDic.get(words2[i]).contains(words1[i])) continue;
            return false;
        }
        return true;
    }
}
```

#### 结果
![sentence-similarity-1](/images/leetcode/sentence-similarity-1.png)
