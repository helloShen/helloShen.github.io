---
layout: post
title: "Leetcode - Algorithm - Shortest Word Distance "
date: 2017-07-17 23:58:05
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given a list of words and two words word1 and word2, return the shortest distance between these two words in the list.

For example,
```
Assume that words = ["practice", "makes", "perfect", "coding", "makes"].

Given word1 = “coding”, word2 = “practice”, return 3.
Given word1 = "makes", word2 = "coding", return 1.
```

Note:
You may assume that word1 does not equal to word2, and word1 and word2 are both in the list.

### 一次遍历
* time: O(n)
* space: O(1)


#### Java代码
```java
public class Solution {
    public int shortestDistance(String[] words, String word1, String word2) {
        int mindis = -1;
        int pos1 = -1, pos2 = -1;
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(word1)) { // do not use "==" to compare string
                pos1 = i;
                if (pos2 != -1) {
                    int dis = pos1 - pos2;
                    mindis = (mindis == -1)? dis : Math.min(mindis,dis);
                }
            }
            if (words[i].equals(word2)) { // do not use "==" to compare string
                pos2 = i;
                if (pos1 != -1) {
                    int dis = pos2 - pos1;
                    mindis = (mindis == -1)? dis : Math.min(mindis,dis);
                }
            }
        }
        return (mindis == -1)? 0 : mindis;
    }
}
```

#### 结果
![shortest-word-distance-1](/images/leetcode/shortest-word-distance-1.png)
