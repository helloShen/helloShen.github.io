---
layout: post
title: "Leetcode - Algorithm - Expressive Words "
date: 2019-03-23 16:56:24
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string", "array"]
level: "medium"
description: >
---

### 题目
Sometimes people repeat letters to represent extra feeling, such as "hello" -> "heeellooo", "hi" -> "hiiii".  In these strings like "heeellooo", we have groups of adjacent letters that are all the same:  "h", "eee", "ll", "ooo".

For some given string S, a query word is stretchy if it can be made to be equal to S by any number of applications of the following extension operation: choose a group consisting of characters c, and add some number of characters c to the group so that the size of the group is 3 or more.

For example, starting with "hello", we could do an extension on the group "o" to get "hellooo", but we cannot get "helloo" since the group "oo" has size less than 3.  Also, we could do another extension like "ll" -> "lllll" to get "helllllooo".  If S = "helllllooo", then the query word "hello" would be stretchy because of these two extension operations: query = "hello" -> "hellooo" -> "helllllooo" = S.

Given a list of query words, return the number of words that are stretchy.

Example:
```
Input:
S = "heeellooo"
words = ["hello", "hi", "helo"]
Output: 1
Explanation:
We can extend "e" and "o" in the word "hello" to get "heeellooo".
We can't extend "helo" to get "heeellooo" because the group "ll" is not size 3 or more.
```

Notes:
* 0 <= len(S) <= 100.
* 0 <= len(words) <= 100.
* 0 <= len(words[i]) <= 100.
* S and all words in words consist only of lowercase letters

### 问题分析
问题本质在于什么样的单词缩减是允许的？假设给出的是`heeellooo`，

1. 首先只有大于等于3个连续字母，才可以缩减。`ll`不能缩减。
2. 其次缩减最多缩减至1个，不能抹掉。

所以如果把`heeellooo`的模式提取出来，即计算每个连续字符的长度，
```
heeellooo --> [h1e3l2o3]
```

比较就很好进行，只有长度大于等于`3`的字符可以缩减，其他都必须保持一致。

我下面的代码偷了点懒，只识别了`S`的模式。对每个`word`我用指针遍历，中间一旦发现不符合马上终止。

#### 代码
```java
class Solution {

    public int expressiveWords(String S, String[] words) {
        if (S.length() == 0) return 0;
        getPattern(S);
        int count = 0;
        for (String word : words) {
            if (isExpressive(word)) {
                count++;
            }
        }
        return count;
    }

    private char[] ca;
    private int[] na;

    private void getPattern(String word) {
        char[] charArr = new char[100];
        int[] numArr = new int[100];
        int wordP = 0, arrayP = 0;
        while (wordP < word.length()) {
            char c = word.charAt(wordP);
            charArr[arrayP] = c;
            int count = 0;
            while (wordP < word.length() && word.charAt(wordP) == c) {
                wordP++; count++;
            }
            numArr[arrayP++] = count;
        }
        ca = Arrays.copyOf(charArr, arrayP);
        na = Arrays.copyOf(numArr, arrayP);
    }

    private boolean isExpressive(String word) {
        int arrP = 0, wordP = 0;
        while (arrP < ca.length && wordP < word.length()) {
            char c = word.charAt(wordP);
            if (ca[arrP] != c) return false;
            int start = wordP;
            while (wordP < word.length() && word.charAt(wordP) == c) wordP++;
            int len = wordP - start;
            if (na[arrP] < 3) {
                if (len != na[arrP]) return false;
            } else if (len > na[arrP]) {
                return false;
            }
            arrP++;
        }
        return arrP == ca.length && wordP == word.length();
    }

}
```

#### 结果
![expressive-words-1](/images/leetcode/expressive-words-1.png)
