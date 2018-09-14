---
layout: post
title: "Leetcode - Algorithm - Word Ladder "
date: 2018-09-13 20:52:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["backtracking","breath first search"]
level: "medium"
description: >
---

### 题目
Given two words (beginWord and endWord), and a dictionary's word list, find the length of shortest transformation sequence from beginWord to endWord, such that:

Only one letter can be changed at a time.
Each transformed word must exist in the word list. Note that beginWord is not a transformed word.

Note:
* Return 0 if there is no such transformation sequence.
* All words have the same length.
* All words contain only lowercase alphabetic characters.
* You may assume no duplicates in the word list.
* You may assume beginWord and endWord are non-empty and are not the same.

Example 1:
```
Input:
beginWord = "hit",
endWord = "cog",
wordList = ["hot","dot","dog","lot","log","cog"]

Output: 5

Explanation: As one shortest transformation is "hit" -> "hot" -> "dot" -> "dog" -> "cog",
return its length 5.
```

Example 2:
```
Input:
beginWord = "hit"
endWord = "cog"
wordList = ["hot","dot","dog","lot","log"]

Output: 0
```
Explanation: The endWord "cog" is not in wordList, therefore no possible transformation.


### DFS回溯算法
首先如果找出所有的相差一个字母的单词对，把他们之间用线连起来，实际上就构成了像下面这样的一个图。
![word-ladder-a](/images/leetcode/word-ladder-a.png)

这个问题实际可以转化成：从起点出发，到目标点的最短路径问题。
![word-ladder-b](/images/leetcode/word-ladder-b.png)

暴力回溯就是从起点出发，遍历所有可能的分叉路径。前提是跳跃的过程中不允许形成环（就是不重复访问路径上已访问过的点）。最后比较所有到达目的地的路线的长度，取最小值（最短路径）。

#### 代码
```java
class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        if (!wordList.contains(beginWord)) {
                wordList.add(beginWord);
        }
        neighbours = findNeighbours(wordList);
        minLen = null;
        Set<String> path = new HashSet<String>();
        path.add(beginWord);
        backtracking(path, beginWord, endWord);
        return (minLen == null)? 0 : minLen;
    }
    private Map<String, List<String>> neighbours;
    private Integer minLen;
    private Map<String, List<String>> findNeighbours(List<String> wordList) {
        Map<String, List<String>> map = new HashMap<>();
        for (int i = 0; i < wordList.size(); i++) {
            String a = wordList.get(i);
            for (int j = i + 1; j < wordList.size(); j++) {
                String b = wordList.get(j);
                if (distance(a, b) == 1) {
                    if (!map.containsKey(a)) {
                        map.put(a, new ArrayList<String>());
                    }
                    map.get(a).add(b);
                    if (!map.containsKey(b)) {
                        map.put(b, new ArrayList<String>());
                    }
                    map.get(b).add(a);
                }
            }
        }
        return map;
    }
    // assetion: a.length() == b.length()
    private int distance(String a, String b) {
        int dis = 0;
        for (int i = 0; i < a.length(); i++) {
            if(a.charAt(i) != b.charAt(i)) {
                dis++;
            }
        }
        return dis;
    }
    private void backtracking(Set<String> path, String begin, String end) {
        if (begin.equals(end)) {
            minLen = (minLen == null)? path.size() : Math.min(minLen, path.size());
            return;
        }
        if (neighbours.containsKey(begin)) {
            for (String neighbour : neighbours.get(begin)) {
                if (path.add(neighbour)) {
                    backtracking(path, neighbour, end);
                    path.remove(neighbour);
                }
            }
        }
    }
}
```

#### 结果
![word-ladder-2](/images/leetcode/word-ladder-2.png)


### BFS(Breath First Search)
换成BFS的角度看这个问题，思路更清楚。如下图所示，第一层遍历到所有只差一个字母的单词，他们是第二层。然后再以他们为起点，和第二层单词差一个字母的为第三层（除去原来第一层的元素）。以此类推。知道遍历完整棵树（生成一棵最小生成树）。
![word-ladder-c](/images/leetcode/word-ladder-c.png)

#### 代码
```java
class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        return bfs(beginWord, endWord, wordList);
    }

    private int bfs(String begin, String end, List<String> wordList) {
        List<String> thisLevel = new ArrayList<>();
        thisLevel.add(begin);
        int level = 1;
        while (!thisLevel.isEmpty()) {
            List<String> nextLevel = new ArrayList<>();
            for (String word : thisLevel) {
                if (word.equals(end)) {
                    return level;
                }
                Iterator<String> ite = wordList.iterator();
                while (ite.hasNext()) {
                    String anotherWord = ite.next();
                    if (distance(anotherWord, word) == 1) {
                        nextLevel.add(anotherWord);
                        ite.remove();
                    }
                }
            }
            thisLevel = nextLevel;
            level++;
        }
        return 0;
    }
    // assetion: a.length() == b.length()
    private int distance(String a, String b) {
        int dis = 0;
        for (int i = 0; i < a.length(); i++) {
            if(a.charAt(i) != b.charAt(i)) {
                dis++;
            }
        }
        return dis;
    }
}
```

#### 结果
![word-ladder-3](/images/leetcode/word-ladder-3.png)


### 优化的BFS
把一个单词和所有其他单词比较编辑距离，复杂度就是`O(wordLen * numWord)`，`numWord`是单词数量，`wordLen`是单词的平均长度。有一个小窍门，把所有单词放在一个`HashSet`里，然后枚举这个单词所有可能的编辑距离为1的变形（只能修改，不可增删），然后在`HashSet`里用`O(1)`检查该单词是否包含在集合中。这样做复杂度是`O(26 * wordLen)`。所以如果单词列表很长，远远大于`26`的话，后一种枚举变种的方法效率更高。

#### 代码
```java
class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        return bfs(beginWord, endWord, wordSet);
    }
    private int bfs(String begin, String end, Set<String> wordSet) {
        List<String> thisLevel = new ArrayList<>();
        thisLevel.add(begin);
        int level = 1;
        while (!thisLevel.isEmpty()) {
            List<String> nextLevel = new ArrayList<>();
            for (String word : thisLevel) {
                if (word.equals(end)) {
                    return level;
                }
                char[] cs = word.toCharArray();
                for (int i = 0; i < cs.length; i++) {
                    char orig = cs[i];
                    for (char c = 'a' ; c <= 'z'; c++) {
                        if (c == orig) { continue; }
                        cs[i] = c;
                        String variant = new String(cs);
                        if (wordSet.remove(variant)) {
                            nextLevel.add(variant);
                        }
                    }
                    cs[i] = orig;
                }
            }
            thisLevel = nextLevel;
            level++;
        }
        return 0;
    }
}
```

#### 结果
![word-ladder-4](/images/leetcode/word-ladder-4.png)
