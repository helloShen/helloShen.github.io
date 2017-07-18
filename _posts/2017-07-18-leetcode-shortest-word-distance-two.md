---
layout: post
title: "Leetcode - Algorithm - Shortest Word Distance Two "
date: 2017-07-18 14:16:43
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table"]
level: "medium"
description: >
---

### 题目
This is a follow up of Shortest Word Distance. The only difference is now you are given the list of words and your method will be called repeatedly many times with different parameters. How would you optimize it?

Design a class which receives a list of words in the constructor, and implements a method that takes two words word1 and word2 and return the shortest distance between these two words in the list.

For example,
```
Assume that words = ["practice", "makes", "perfect", "coding", "makes"].

Given word1 = “coding”, word2 = “practice”, return 3.
Given word1 = "makes", word2 = "coding", return 1.
```

Note:
You may assume that word1 does not equal to word2, and word1 and word2 are both in the list.

### 用HashMap
既然要查询很多次，就把单词出现的位置信息用一个`Map`封装起来。之后用 $$O(1)$$ 的时间就能查到某个单词所有出现的位置。

但最坏情况下，时间复杂度还是 $$O(n)$$。
* time: $$O(n)$$
* space: $$O(n)$$

#### Java代码
```java
public class WordDistance {
    private Map<String,List<Integer>> dic = new HashMap<>();
    public WordDistance(String[] words) {
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            List<Integer> position = dic.get(word);
            if (position == null) { position = new ArrayList<Integer>(); }
            position.add(i);
            dic.put(word,position);
        }
    }
    public int shortest(String word1, String word2) {
        List<Integer> list1 = dic.get(word1);
        List<Integer> list2 = dic.get(word2);
        if (list1 == null || list2 == null) { return 0; }
        int min = -1, dis = 0;
        for (int i = 0, j = 0; i < list1.size() && j < list2.size(); ) {
            int pos1 = list1.get(i);
            int pos2 = list2.get(j);
            if (pos1 > pos2) {
                dis = pos1 - pos2;
                j++;
            } else {
                dis = pos2 - pos1;
                i++;
            }
            min = (min == -1)? dis : Math.min(min,dis);
        }
        return (min == -1)? 0 : min;
    }
}
```

#### 结果
![shortest-word-distance-two-1](/images/leetcode/shortest-word-distance-two-1.png)
