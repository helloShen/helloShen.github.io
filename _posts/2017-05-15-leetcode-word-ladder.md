---
layout: post
title: "Leetcode - Algorithm - Word Ladder (to be continued...)"
date: 2017-05-15 23:52:52
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: "medium"
description: >
---

### 题目
Given two words (beginWord and endWord), and a dictionary's word list, find the length of shortest transformation sequence from beginWord to endWord, such that:

Only one letter can be changed at a time.
Each transformed word must exist in the word list. Note that beginWord is not a transformed word.
For example,

Given:
beginWord = `hit`
endWord = `cog`
wordList = `["hot","dot","dog","lot","log","cog"]`
As one shortest transformation is `"hit" -> "hot" -> "dot" -> "dog" -> "cog"`,
return its length `5`.

Note:
* Return 0 if there is no such transformation sequence.
* All words have the same length.
* All words contain only lowercase alphabetic characters.
* You may assume no duplicates in the word list.
* You may assume beginWord and endWord are non-empty and are not the same.

UPDATE (2017/1/20):
The wordList parameter had been changed to a list of strings (instead of a set of strings). Please reload the code definition to get the latest changes.

### 表驱动法
假设，
wordList = `["hot","dot","dog","lot","log","cog"]`，可以构建一个编辑距离互为1的单词的拓扑结构，
![word-ladder-graph-1](/images/leetcode/word-ladder-graph-1.png)

这个拓扑可以用一张表来表示，`buildLevelOne()`函数负责生成这张表，
![word-ladder-table-1](/images/leetcode/word-ladder-table-1.png)

根据距离为1的信息，可以构建出每个节点和其他所有节点的最短转换距离，具体由`buildHigherLevel()`函数完成，
![word-ladder-table-2](/images/leetcode/word-ladder-table-2.png)

最后拿到`beginWord`和`endWord`，查表即可得到最后结果。

#### 代码
```java
public class Solution {
    /**
     * 主入口
     */
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        if (!wordList.contains(endWord)) { return 0; }
        int[][] matrix = buildLevelOne(wordList);
        matrix = buildHigherLevel(matrix);
        int min = Integer.MAX_VALUE;
        for (String word : wordList) {
            if (distance(beginWord,word) == 1) {
                min = Math.min(min,2+matrix[wordList.indexOf(word)][wordList.indexOf(endWord)]);
            }
        }
        return (min == Integer.MAX_VALUE)? 0 : min;
    }
    /**
     * 生成只包含距离为1的节点信息的表
     */
    public int[][] buildLevelOne(List<String> wordList) {
        int size = wordList.size();
        int[][] matrix = new int[size][size];
        if (size < 2) { return matrix; }
        for (int i = 0; i < size-1; i++) {
            for (int j = i+1; j < size; j++) {
                int dis = distance(wordList.get(i),wordList.get(j));
                if (dis == 1) {
                    matrix[i][j] = 1;
                    matrix[j][i] = 1;
                }
            }
        }
        return matrix;
    }
    /**
     * 计算编辑距离。（只计算修改，不计算增删）
     */
    public int distance(String first, String second) {
        if (first.length() != second.length()) { return -1; }
        int count = 0;
        for (int i = 0; i < first.length(); i++) {
            if (first.charAt(i) != second.charAt(i)) { count++; }
        }
        return count;
    }
    /**
     * 把距离为1的节点信息的表，推算出完整的距离表
     */
    public int[][] buildHigherLevel(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j] == 1) { backtracking(i,j,1,matrix); }
            }
        }
        return matrix;
    }
    /**
     * 回溯递归计算节点间距离
     */
    public void backtracking(int departure, int relay, int distance, int[][] matrix) {
        if (relay == departure) { return; }
        if (matrix[departure][relay] != 0 && matrix[departure][relay] < distance) { return; }
        matrix[departure][relay] = distance;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[relay][i] == 1) {
                backtracking(departure,i,distance+1,matrix);
            }
        }
    }
}
```

#### 结果
算法是对的。但超时太严重。其实这种方法的普适性非常好。就算问题怎么变化，这些模块都是很有用的。
![word-ladder-1](/images/leetcode/word-ladder-1.png)


### 解法2

#### 代码
```java

```

#### 结果
![word-ladder-2](/images/leetcode/word-ladder-2.png)


### 解法3

#### 代码
```java

```

#### 结果
![word-ladder-3](/images/leetcode/word-ladder-3.png)
