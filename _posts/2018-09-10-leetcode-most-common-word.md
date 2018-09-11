---
layout: post
title: "Leetcode - Algorithm - Most Common Word "
date: 2018-09-10 19:06:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["trie","hash map"]
level: "easy"
description: >
---

### 题目
Given a paragraph and a list of banned words, return the most frequent word that is not in the list of banned words.  It is guaranteed there is at least one word that isn't banned, and that the answer is unique.

Words in the list of banned words are given in lowercase, and free of punctuation.  Words in the paragraph are not case sensitive.  The answer is in lowercase.

Example:
```
Input:
paragraph = "Bob hit a ball, the hit BALL flew far after it was hit."
banned = ["hit"]
Output: "ball"
Explanation:
"hit" occurs 3 times, but it is a banned word.
"ball" occurs twice (and no other word does), so it is the most frequent non-banned word in the paragraph.
Note that words in the paragraph are not case sensitive,
that punctuation is ignored (even if adjacent to words, such as "ball,"),
and that "hit" isn't the answer even though it occurs more because it is banned.
```


Note:
* 1 <= paragraph.length <= 1000.
* 1 <= banned.length <= 100.
* 1 <= banned[i].length <= 10.
* The answer is unique, and written in lowercase (even if its occurrences in paragraph may have uppercase symbols, and even if it is a proper noun.)
* paragraph only consists of letters, spaces, or the punctuation symbols !?',;.
* Different words in paragraph are always separated by a space.
* There are no hyphens or hyphenated words.
* Words only consist of letters, never apostrophes or other punctuation symbols.


### 用`HashSet`和`HashMap`储存单词
Banned的单词储存在一个`HashSet`里，Paragraph里的单词经过处理，
1. 切词
2. 转小写
3. 清除前后标点
4. 确认不在banned列表里

最后存入一个`HashMap`，统计每个单词的词频。

#### 代码
```java
class Solution {
    public String mostCommonWord(String paragraph, String[] banned) {
        Set<String> bannedSet = new HashSet<>();
        for (String ban : banned) {
            bannedSet.add(ban);
        }
        Map<String, Integer> wordFreq = new HashMap<>();
        int maxCount = 0;
        String mostFreqWord = "";
        for (String seg : paragraph.split(" ")) {
            int start = 0, end = 0;
            for ( ; start < seg.length(); start++) {
                if (Character.isLetter(seg.charAt(start))) {
                    break;
                }
            }
            for (end = start + 1; end < seg.length(); end++) {
                if (!Character.isLetter(seg.charAt(end))) {
                    break;
                }
            }
            String word = seg.substring(start, end).toLowerCase();
            if (word != null && word.length() > 0 && !bannedSet.contains(word)) {
                int count = 1;
                if (wordFreq.containsKey(word)) {
                    count = wordFreq.get(word) + 1;
                }
                wordFreq.put(word, count);
                if (count > maxCount) {
                    mostFreqWord = word;
                    maxCount = count;
                }
            }
        }
        return mostFreqWord;
    }
}
```

#### 结果
![most-common-word-1](/images/leetcode/most-common-word-1.png)


### 用`Trie`储存单词
把单词存在`Trie`更节省空间。Trie又叫前缀树（Prefix Tree），它的每个节点都是一个字母，从根节点到某个节点的路径连起来代表一个单词。

```
                    .
                 /     \
              c | 0    d | 0
              /           \
           a | 0          o | 1  [do]
            /                \   
  [cat]  t | 1               g | 1  [dog]
```
`int count`用来统计这个单词的词频，`boolean ban`记录这个词是否被禁用。
```java
private class Trie {
    private Trie[] next = new Trie[26];    // sub nodes
    private int count;                     // word freqence
    private boolean ban;                   // banned?
}
```

复杂度是线性的，这里`N`代表所有被ban的单词的长度总和，`M`代表paragraph里所有单词的总和。
* Time Complexity: O (N + M)
* Space Complexity: O (N + M)

#### 代码
```java
class Solution {
    public String mostCommonWord(String paragraph, String[] banned) {
        Trie root = new Trie();
        Trie curr = root;
        // insert banned words into Trie
        for (String ban : banned) {
            for (int i = 0; i < ban.length(); i++) {
                int idx = ban.charAt(i) - 'a';
                if (curr.next[idx] == null) {
                    curr.next[idx] = new Trie();
                }
                curr = curr.next[idx];
            }
            curr.ban = true;
            curr = root;
        }
        int maxCount = 0;
        String mostFreqWord = "";
        paragraph = paragraph.toLowerCase();
        char[] pArray = paragraph.toCharArray();
        // insert words in paragraph into Trie
        for (int start = 0, end = 0; start < pArray.length; start = end + 1) {
            // skip non-letter characters
            while (start < pArray.length && (pArray[start] < 'a' || pArray[start] > 'z')) { start++; }
            // insert consecutive letters(words) into Trie
            for (end = start; end < pArray.length && (pArray[end] >= 'a' && pArray[end] <= 'z'); end++) {
                int idx = pArray[end] - 'a';
                if (curr.next[idx] == null) {
                    curr.next[idx] = new Trie();
                }
                curr = curr.next[idx];
            }
            // update statistics
            if (curr != root && !curr.ban) {
                curr.count++;
                if (curr.count > maxCount) {
                    mostFreqWord = paragraph.substring(start, end);
                    maxCount = curr.count;
                }
            }
            curr = root;
        }
        return mostFreqWord;
    }
    // simplest Trie data structure
    private class Trie {
        private Trie[] next = new Trie[26];    // sub nodes
        private int count;                     // word freqence
        private boolean ban;                   // banned?
    }
}
```

#### 结果
![most-common-word-2](/images/leetcode/most-common-word-2.png)
