---
layout: post
title: "Leetcode - Algorithm - Shortest Word Distance Three "
date: 2017-07-18 15:05:04
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 主要收获
怎么对问题归纳，把特殊问题一般化，的思考过程很重要。这题的思维过程比较典型。

问题最朴素的抽象是：**找到a，计算和前一个b的距离。找到b，计算和前一个a的距离。**

进一步归纳，一般化后的抽象是：**只要用两个变量维护a,b上一次出现的位置，就可以根据新匹配到的元素计算距离。**

如果始终维护两个历史记录变量，a,b相等的特殊情况就变成一种特例，需要考虑的是怎么能让普通的流程也能处理特殊情况。


### 题目
This is a follow up of Shortest Word Distance. The only difference is now word1 could be the same as word2.

Given a list of words and two words word1 and word2, return the shortest distance between these two words in the list.

word1 and word2 may be the same and they represent two individual words in the list.

For example,
```
Assume that words = ["practice", "makes", "perfect", "coding", "makes"].

Given word1 = “makes”, word2 = “coding”, return 1.
Given word1 = "makes", word2 = "makes", return 3.
```

Note:
You may assume word1 and word2 are both in the list.


### 最简单的把两种情况分开处理
`word1 == word2`和`word1 != word2`分开处理。分别写两个函数。真正工作上，我比较倾向于这种方法，逻辑简单，不容搞混。而且每种情况都是用最优的办法解决，效率最高。

#### 代码
```java
public class Solution {
    public int shortestWordDistance(String[] words, String word1, String word2) {
        if (word1.equals(word2)) {
            return shortestDistanceEquals(words,word1);
        } else {
            return shortestDistance(words,word1,word2);
        }
    }
    private int shortestDistanceEquals(String[] words, String word) {
        int min = 0, pre = -1;
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(word)) {
                if (pre == -1) {
                    pre = i;
                } else {
                    int dis = i - pre;
                    min = (min == 0)? dis : Math.min(min,dis);
                    pre = i;
                }
            }
        }
        return min;
    }
    private int shortestDistance(String[] words, String word1, String word2) {
        int mindis = 0;
        int pos1 = -1, pos2 = -1;
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(word1)) {
                pos1 = i;
                if (pos2 != -1) {
                    int dis = pos1 - pos2;
                    mindis = (mindis == 0)? dis : Math.min(mindis,dis);
                }
            }
            if (words[i].equals(word2)) {
                pos2 = i;
                if (pos1 != -1) {
                    int dis = pos2 - pos1;
                    mindis = (mindis == 0)? dis : Math.min(mindis,dis);
                }
            }
        }
        return mindis;
    }
}
```

#### 结果
![shortest-word-distance-three-1](/images/leetcode/shortest-word-distance-three-1.png)


### 用两个代词指代两个word
不过做算法，当然要想办法把逻辑梳理清楚，最好把两个大分支合并起来。

用`self`指代当前找到的单词，用`brother`指代另外那个单词。就可以把两个单词相同`word1.equals(word2)`的情况变成一种特例，只不过`self == brother`而已。

#### 代码
```java
public class Solution {
    public int shortestWordDistance(String[] words, String word1, String word2) {
        String self = null, brother = null;
        int pre = -1, len = words.length;
        for (int i = 0; i < len; i++) {
            String word = words[i];
            if (word.equals(word1) || word.equals(word2)) {
                boolean equalsWord1 = word.equals(word1);
                self = (equalsWord1)? word1 : word2;
                brother = (equalsWord1)? word2 : word1;
                pre = i;
                break;
            }
        }
        int min = 0;
        for (int i = pre+1; i < len; i++) {
            String word = words[i];
            if (word.equals(brother)) {
                int dis = i - pre;
                min = (min == 0)? dis : Math.min(min,dis);
                pre = i;
                String temp = brother; // switch self & brother
                brother = self;
                self = temp;
            } else if (word.equals(self)) { // if word1.equals(word2), never enter here
                pre = i;
            }
        }
        return min;
    }
}
```

#### 结果
![shortest-word-distance-three-2](/images/leetcode/shortest-word-distance-three-2.png)


### 更简洁的逻辑
两层逻辑，非常清晰，让`word1.equals(word2)`仅仅变成一种特例：
1. 每一次迭代，只要匹配到`word1`或者`word2`就维护`pos1`和`pos2`两个指针，让`pos1`始终指向上一个`word1`，`pos2`始终指向上一个`word2`。然后根据`pos1`和`pos2`来更新最短距离`min`。这是前导问题`Shortest Word Distance`的最简单逻辑。
2. 如果`word1.equals(word2)`，就无法进入维护`pos2`的分支，所以就在维护`pos1`的分支里同时维护`pos1`和`pos2`。这样就算`word1.equals(word2)`的情况出现，也就是在一个小分支里做一个特殊处理，不会影响大的逻辑分支。

#### Java代码
```java
public class Solution {
    public int shortestWordDistance(String[] words, String word1, String word2) {
        int min = 0;
        int pos1 = -1, pos2 = -1;
        boolean same = word1.equals(word2); // use a flag to mark if word1.equals(word2)
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.equals(word1) || word.equals(word2)) {
                int dis = 0;
                if (word.equals(word1)) {
                    if (same) { // if word1.equals(word2), maintains both pos1 & pos2 in this branch
                        pos2 = pos1;
                        pos1 = i;
                    } else {
                        pos1 = i;
                    }
                    if (pos2 != -1) { dis = pos1 - pos2; }
                } else { // if word1.equals(word2), never enter this branch
                    pos2 = i;
                    if (pos1 != -1) { dis = pos2 - pos1; }
                }
                if (dis != 0) { min = (min == 0)? dis : Math.min(min,dis); }
            }
        }
        return min;
    }
}
```

#### 结果
![shortest-word-distance-three-3](/images/leetcode/shortest-word-distance-three-3.png)
