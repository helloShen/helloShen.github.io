---
layout: post
title: "Leetcode - Algorithm - Keyboard Row "
date: 2017-09-07 19:16:09
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "easy"
description: >
---

### 题目
Given a List of words, return the words that can be typed using letters of alphabet on only one row's of American keyboard like the image below.

![keyboard](/images/leetcode/american-keyboard.png)

Example 1:
```
Input: ["Hello", "Alaska", "Dad", "Peace"]
Output: ["Alaska", "Dad"]
```
Note:
* You may use one character in the keyboard more than once.
* You may assume the input string will only contain letters of alphabet.


### 关键是怎么储存键盘键位信息
直观的方法是用三个`char[]`分别记录三行的字母。
```java
char[] line1 = new char[]{'q','w','e','r','t','y','u','i','o','p'};
char[] line2 = new char[]{'a','s','d','f','g','h','j','k','l'};
char[] line3 = new char[]{'z','x','c','v','b','n','m'};
```

但这样查询字母的时候需要遍历整个数组。更简便的方法是用`int[]`储存字母的行号，这样可以在 $$O(1)$$时间里访问数组下标。
```java
int[] keyboard = new int[]{2,3,3,2,1,2,2,2,1,2,2,2,3,3,1,1,1,1,2,1,1,3,1,3,1,3};
```

#### 代码
```java
class Solution {
    private static final int[] LETTER = new int[]{2,3,3,2,1,2,2,2,1,2,2,2,3,3,1,1,1,1,2,1,1,3,1,3,1,3};
    public String[] findWords(String[] words) {
        int j = 0;
        for (int i = 0; i < words.length; i++) {
            if (isOneRow(words[i])) { exch(words,i,j++); }
        }
        return Arrays.copyOfRange(words,0,j);
    }
    private boolean isOneRow(String s) {
        int len = s.length();
        if (len == 0) { return true; }
        int line = LETTER[getOffset(s.charAt(0))];
        for (int i = 1; i < len; i++) {
            if (LETTER[getOffset(s.charAt(i))] != line) { return false; }
        }
        return true;
    }
    private void exch(String[] words, int x, int y) {
        String temp = words[x];
        words[x] = words[y];
        words[y] = temp;
    }
    private int getOffset(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        } else if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        } else {
            return -1;
        }
    }
}
```

#### 结果
![keyboard-row-1](/images/leetcode/keyboard-row-1.png)
