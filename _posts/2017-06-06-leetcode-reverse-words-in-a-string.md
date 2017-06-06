---
layout: post
title: "Leetcode - Algorithm - Reverse Words In A String "
date: 2017-06-06 03:58:46
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string","array"]
level: "medium"
description: >
---

### 主要收获
> 复杂的正则表达式非常影响效率。

### 题目
Given an input string, reverse the string word by word.

For example,
```
Given s = "the sky is blue",
return "blue is sky the".
```

### 从末尾开始，每找到一个新单词，就写入新数组
假设字符串是`aaa   bbb   ccc   `，中间有很多空格，

找到`ccc`，
```
          fast slow
            |   |
"aaa   bbb   ccc   "
```
把`ccc`写进新数组，后面加一个` `空格
```
          光标
           |
|c|c|c|空格| | | | | | | | | | | | | | |
```
最后找齐`aaa`,`bbb`,`ccc`后的新数组，如下，
```
                               光标
                                |
|c|c|c|空格|b|b|b|空格|a|a|a|空格| | | | | | |
```
去掉最后多余的空格，得到新`String`，
```
|c|c|c|空格|b|b|b|空格|a|a|a|
```

整个过程没有用容器，没有用`split()`，没有用`trim()`，没有用`StringBuilder`，完全基于数组。难点是需要非常清楚新数组中每一个元素是什么。

#### 代码
```java
public class Solution {
    public String reverseWords(String s) {
        int size = s.length();
        char[] rev = new char[size];
        int revCur = 0;
        int slow = size, fast = size-1;
        while (fast >= 0) { // 对于每个单词
            while (fast >= 0 && s.charAt(fast) != ' ') { fast--; } // 找到单词边界
            for (int cur = fast+1; cur < slow; cur++) { // 复制到新数组
                rev[revCur++] = s.charAt(cur);
            }
            // 每个单词后面加一个空格（最后一个单词，超出边界时不加空格）
            if (revCur < size && slow - fast > 1) { rev[revCur++] = ' '; }
            slow = fast;
            fast--;
        }
        if (revCur-1 < size && revCur > 0 && rev[revCur-1] == ' ') { revCur--; } // 排除最后一个空格（如果有的话）
        return new String(rev,0,revCur);
    }
}
```

#### 结果
![reverse-words-in-a-string-1](/images/leetcode/reverse-words-in-a-string-1.png)


### 使用容器和`split()`以及`trim()`
完全是基于库函数的做法。

#### 代码
```java
public class Solution {
    public String reverseWords(String s) {
        String[] words = s.split(" "); // 注意这里比较快，是因为正则表达式不复杂。
        StringBuilder sb = new StringBuilder();
        for (int i = words.length-1; i >= 0; i--) {
            if (words[i] != null & !words[i].isEmpty()) {
                sb.append(words[i]+ " ");
            }
        }
        return sb.toString().trim();
    }
}
```

#### 结果
结果竟然比基于数组的解法快，主要因为正则表达式比较简单。
![reverse-words-in-a-string-2](/images/leetcode/reverse-words-in-a-string-2.png)


### 用规范正则表达式切割
用`\\s+`表示匹配最大长度的连续空格。此时`split()`函数的复杂度就上升了，效率明显下降。

#### 代码
```java
public class Solution {
    public String reverseWords(String s) {
        String[] words = s.split("\\s+"); // 用正则表达式切割
        StringBuilder sb = new StringBuilder();
        for (int i = words.length-1; i >= 0; i--) {
            sb.append(words[i] + " ");
        }
        return sb.toString().trim();
    }
}
```

#### 结果
用正则表达式影响效率。
![reverse-words-in-a-string-3](/images/leetcode/reverse-words-in-a-string-3.png)
