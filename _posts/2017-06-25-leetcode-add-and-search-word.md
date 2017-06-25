---
layout: post
title: "Leetcode - Algorithm - Add And Search Word "
date: 2017-06-25 17:46:59
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["trie","depth first search"]
level: "medium"
description: >
---

### 题目
Design a data structure that supports the following two operations:

void addWord(word)
bool search(word)
search(word) can search a literal word or a regular expression string containing only letters a-z or .. A . means it can represent any one letter.

For example:
```
addWord("bad")
addWord("dad")
addWord("mad")
search("pad") -> false
search("bad") -> true
search(".ad") -> true
search("b..") -> true
```
Note:
You may assume that all words are consist of lowercase letters a-z.

You should be familiar with how a Trie works. If not, please work on this problem: `Implement Trie (Prefix Tree)` first.

### 标准的 **单词查找树（Trie）**，复杂度 $$O(len)$$，`len`为单词的长度
这题是个标准的 **“单词查找树（Trie）”** 问题。寻求的是在 $$O(\log_{}{N})$$ 时间内完成`addWord()`和`search()`两个动作。这里的`N`是指整个单词空间的大小。

在`Implement Trie (Prefix Tree)`这一题里，已经证明了用普通 **二叉树** 也能在 $$O(\log_{}{N})$$ 时间内完成`addWord()`和`search()`。 但二叉树的深度是以`2`为底对数 $$O(\log_{2}{N})$$。

相比之下，**“单词查找树（Trie）”更为高效** ，因为每个节点都有`26`个子节点，所以树的深度为以`26`为底的对数，$$O(\log_{26}{N})$$。 实际树的最大深度，等于最长单词的长度 `Trie Max Depth = Word Max Length`。而且单词查找树更节省空间。

通过尝试在`Trie`中以不同容器储存单词信息，得到下面几个关于`Trie`的具体实现的基本事实，
1. 用`Array`数组来储存`TrieNode`信息，比`Map`或者`List`更好。`Array`不但和`Map`一样能做到 $$O(1)$$ 时间内的随机访问，而且还可以不用储存字符信息`char`，因为数组的`[1-26]`的下标就代表了对应的字符。
2. 可以用一个`boolean isEnd`来标记单词的结尾。就可以知道比如`ad`到底表示一个完整的单词，还是只是`add`的一个子串。
3. `boolean isEnd`标记的地方，是在单词最后一个字符的下面一层。


```
单词：[add]

            [0|1|2|3|4|5|...|23|24|25]                             (isEnd = false)
             |
             +-> [0|1|2|3|4|5|...|23|24|25]                        (isEnd = false)
                        |
                        +-> [0|1|2|3|4|5|...|23|24|25]             (isEnd = false)
                                   |
                                   +-> [0|1|2|3|4|5|...|23|24|25]  (isEnd = true)
```

所以单词查找树，单个节点的数据结构非常简单，只有只有指向下层节点的26个引用构成的数组，以及一个用来标记单词末尾的布尔型。
```java
public class TrieNode {
    private TrieNode[] children = new TrieNode[26];
    private boolean isEnd;
}
```

#### 具体代码如下
```java
public class WordDictionary {
    private static class Letter {
        private Letter[] postfixs = new Letter[26];
        private boolean isEnd;
    }

    Letter dummy = new Letter();

    /** Adds a word into the data structure. */
    public void addWord(String word) {
        Letter cur = dummy;
        for (int i = 0; i < word.length(); i++) {
            int offset = word.charAt(i) - 'a';
            if (cur.postfixs[offset] == null) {
                cur.postfixs[offset] = new Letter();
            }
            cur = cur.postfixs[offset];
        }
        cur.isEnd = true;
    }

    /** Returns if the word is in the data structure. A word could contain the dot character '.' to represent any one letter. */
    public boolean search(String word) {
        return dfs(word.toCharArray(),0,dummy);
    }

    /** Iterate the Prefix Tree with Depth First Search */
    public boolean dfs(char[] word, int index, Letter curr) {
        // base case
        if (curr == null) { return false; } // too deep
        if (index == word.length) { return curr.isEnd; }
        // recursion
        int offset = word[index] - 'a';
        if (offset >= 0) { // [a-z]
            return dfs(word,index+1,curr.postfixs[offset]);
        } else { // [.]
            for (Letter l : curr.postfixs) {
                if (dfs(word,index+1,l)) { return true; }
            }
        }
        return false;
    }
}
```

#### 结果
![add-and-search-word-1](/images/leetcode/add-and-search-word-1.png)
