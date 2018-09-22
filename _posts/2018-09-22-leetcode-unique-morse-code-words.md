---
layout: post
title: "Leetcode - Algorithm - Unique Morse Code Words "
date: 2018-09-22 14:03:18
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "easy"
description: >
---

### 题目
International Morse Code defines a standard encoding where each letter is mapped to a series of dots and dashes, as follows: `a` maps to `.-`, `b`maps to `-...`, `c` maps to `-.-.`, and so on.

For convenience, the full table for the 26 letters of the English alphabet is given below:
```
[".-","-...","-.-.","-..",".","..-.","--.","....","..",".---","-.-",".-..","--","-.","---",".--.","--.-",".-.","...","-","..-","...-",".--","-..-","-.--","--.."]
```
Now, given a list of words, each word can be written as a concatenation of the Morse code of each letter. For example, "cab" can be written as `-.-.-....-`, (which is the concatenation `-.-.` + `-...` + `.-`). We'll call such a concatenation, the transformation of a word.

Return the number of different transformations among all words we have.

Example:
```
Input: words = ["gin", "zen", "gig", "msg"]
Output: 2
Explanation:
The transformation of each word is:
"gin" -> "--...-."
"zen" -> "--...-."
"gig" -> "--...--."
"msg" -> "--...--."

There are 2 different transformations, "--...-." and "--...--.".
```

Note:
* The length of words will be at most 100.
* Each words[i] will have length in range [1, 12].
* words[i] will only consist of lowercase letters.

### 用`Array`加`HashSet`
用一个`String[26]`做一个摩斯密码词典，翻译的时候查表。翻译出来的单词用`HashSet`去重，并计算最终HashSet的长度。

#### 代码
```java
class Solution {
    public int uniqueMorseRepresentations(String[] words) {
        Set<String> morseSet = new HashSet<>();
        for (String word : words) {
            morseSet.add(toMorse(word));
        }
        return morseSet.size();
    }
    private final String[] MORSE = new String[]{
        ".-","-...","-.-.","-..",".","..-.","--.","....","..",
        ".---","-.-",".-..","--","-.","---",".--.","--.-",".-.",
        "...","-","..-","...-",".--","-..-","-.--","--.."
    };
    private String toMorse(String word) {
        char[] arr = word.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(MORSE[arr[i] - 'a']);
        }
        return sb.toString();
    }
}
```

#### 结果
![unique-morse-code-words-1](/images/leetcode/unique-morse-code-words-1.png)
