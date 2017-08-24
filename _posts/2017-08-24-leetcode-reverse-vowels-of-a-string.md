---
layout: post
title: "Leetcode - Algorithm - Reverse Vowels Of A String "
date: 2017-08-24 18:23:46
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["two pointers","string"]
level: "easy"
description: >
---

### 题目
Write a function that takes a string as input and reverse only the vowels of a string.

Example 1:
```
Given s = "hello", return "holle".
```

Example 2:
```
Given s = "leetcode", return "leotcede".
```

Note:
The vowels does not include the letter "y".



### 用一个数组记录元音的位置
然后用两个指针分别指向这个数组中对称的位置，然后交换两个相应的原因。比如，`leetcode`，的元音位置数组为`[1,2,5,7]`。所以就交换`leetcode`中`[1]<->[7]`，以及`[2]<->[5]`位置的元素。

#### 代码
```java
class Solution {
    private final String vowels = "aeiouAEIOU";
    private char[] chars = new char[0];
    public String reverseVowels(String s) {
        chars = s.toCharArray();
        int[] stack = new int[chars.length];
        int cursor = 0;
        for (int i = 0; i < chars.length; i++) {
            if (vowels.indexOf(chars[i]) != -1) {
                stack[cursor++] = i;
            }
        }
        int lo = 0, hi = --cursor;
        while (hi > lo) {
            exch(stack[lo++],stack[hi--]);
        }
        return new String(chars);
    }
    private void exch(int a, int b) {
        char temp = chars[a];
        chars[a] = chars[b];
        chars[b] = temp;
    }
}
```

#### 结果
![reverse-vowels-of-a-string-1](/images/leetcode/reverse-vowels-of-a-string-1.png)

### 不用额外数组，直接用两个指针

#### 代码
```java
class Solution {
    private final String VOWELS = "aeiouAEIOU";
    private char[] chars = new char[0];
    public String reverseVowels(String s) {
        chars = s.toCharArray();
        int lo = 0, hi = chars.length-1;
        while (lo < hi) {
            if (VOWELS.indexOf(chars[lo]) == -1) {
                ++lo;
            } else if (VOWELS.indexOf(chars[hi]) == -1) {
                --hi;
            } else {
                exch(lo++,hi--);
            }
        }
        return new String(chars);
    }
    private void exch(int a, int b) {
        char temp = chars[a];
        chars[a] = chars[b];
        chars[b] = temp;
    }
}
```

#### 结果
![reverse-vowels-of-a-string-1](/images/leetcode/reverse-vowels-of-a-string-1.png)
