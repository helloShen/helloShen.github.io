---
layout: post
title: "Leetcode - Algorithm - Maximum Product Of Word Lengths "
date: 2017-09-29 16:33:41
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation","array"]
level: "medium"
description: >
---

### 题目
Given a string array words, find the maximum value of length(word[i]) * length(word[j]) where the two words do not share common letters. You may assume that each word will contain only lower case letters. If no such two words exist, return 0.

Example 1:
```
Given ["abcw", "baz", "foo", "bar", "xtfn", "abcdef"]
Return 16
The two words can be "abcw", "xtfn".
```

Example 2:
```
Given ["a", "ab", "abc", "d", "cd", "bcd", "abcd"]
Return 4
The two words can be "ab", "cd".
```

Example 3:
```
Given ["a", "aa", "aaa", "aaaa"]
Return 0
No such pair of words.
```

### 朴素两两比较字符串
可以把26个字母是否出现的信息存放在`boolean[26]`数组里。

#### 代码
```java
class Solution {
    public int maxProduct(String[] words) {
        int maxProduct = 0;
        boolean[] letters = new boolean[26];
        int maxSize = 0;
        for (String word : words) { maxSize = Math.max(maxSize,word.length()); }
        for1:
        for (int i = 0; i < words.length - 1; i++) {
            Arrays.fill(letters,false);
            String wordA = words[i];
            int sizeA = wordA.length();
            if (sizeA * maxSize <= maxProduct) { continue for1; }       // 剪枝
            for (int j = 0; j < sizeA; j++) {
                letters[wordA.charAt(j)-'a'] = true;
            }
            for2:
            for (int j = 1; j < words.length; j++) {
                String wordB = words[j];
                int sizeB = wordB.length();
                int possibleSize = sizeA * sizeB;
                if (possibleSize <= maxProduct) { continue for2; }     // 剪枝
                for3:
                for (int k = 0; k < sizeB; k++) {
                    if (letters[wordB.charAt(k)-'a'] == true) {
                        continue for2;
                    }
                }
                maxProduct = possibleSize;
            }
        }
        return maxProduct;
    }
}
```

#### 结果
![maximum-product-of-word-lengths-1](/images/leetcode/maximum-product-of-word-lengths-1.png)


### `bitmap`的思想，把26个字母出现信息放在一个`int`里
根据上面的做法，最简单的优化是不需要每次去重新逐个检查字母。而是一次性把字母信息都记录在`boolean[26]`里。

但这样做，每次还是要遍历`boolean[26]`比较两个单词。

考虑到一共只有26个小写字母，一个32位的`int`做`bitmap`就可以储存字母出现的信息。每个单词可以用一个`int`表示。比如`a`就可以表示成`0000 0000 0000 0000 0000 0000 0000 0001`。

#### 代码
```java
class Solution {
    public int maxProduct(String[] words) {
        int maxProduct = 0;
        int[] bitmap = new int[words.length];
        int[] lengths = new int[words.length];
        for (int i = 0; i < words.length; i++) {
            int len = words[i].length();
            lengths[i] = len;
            for (int j = 0; j < len; j++) {
                bitmap[i] |= (1 << words[i].charAt(j)-'a');
            }
        }
        for (int i = 0; i < words.length - 1; i++) {
            for (int j = 1; j < words.length; j++) {
                if ((bitmap[i] & bitmap[j]) == 0) {
                    maxProduct = Math.max(maxProduct,(lengths[i] * lengths[j]));
                }
            }
        }
        return maxProduct;
    }
}
```

#### 结果
![maximum-product-of-word-lengths-2](/images/leetcode/maximum-product-of-word-lengths-2.png)
