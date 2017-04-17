---
layout: post
title: "Leetcode - Algorithm - Length Of Last Word "
date: 2017-04-17 14:19:16
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["String"]
level: "easy"
description: >
---

### 题目
Given a string s consists of upper/lower-case alphabets and empty space characters ' ', return the length of last word in the string.

If the last word does not exist, return 0.

Note: A word is defined as a character sequence consists of non-space characters only.

For example,
```
Given s = "Hello World",
return 5.
```

### 老老实实暴力遍历 $$O(n)$$
这是典型的不适合用二分法的题。跳来跳去是没用的。还是得老老实实从尾部开始遍历。

#### 代码
```java
public class Solution {
    public int lengthOfLastWord(String s) {
        boolean begin = false;
        int len = 0;
        for (int i = s.length()-1; i >= 0; i--) {
            if (!begin && s.charAt(i) != ' ') { begin = true; }
            if (begin && s.charAt(i) != ' ') { len++; }
            if (begin && s.charAt(i) == ' ') { break; }
        }
        return len;
    }
}
```

#### 结果
![length-of-last-word-1](/images/leetcode/length-of-last-word-1.png)


### 利用库
`trim()`可以去掉开头和结尾的所有空格。然后用`lastIndexOf()`找最后一个空格。

#### 代码
```java
public class Solution {
    public int lengthOfLastWord(String s) {
        String trimed = s.trim();
        return trimed.length() - 1 - trimed.lastIndexOf(' ');
    }
}
```

#### 结果
![length-of-last-word-2](/images/leetcode/length-of-last-word-2.png)
