---
layout: post
title: "Leetcode - Algorithm - Is Subsequence "
date: 2018-09-06 13:51:54
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search","string","greedy","dynamic programming"]
level: "medium"
description: >
---

### 题目
Given a string s and a string t, check if s is subsequence of t.

You may assume that there is only lower case English letters in both s and t. t is potentially a very long (length ~= 500,000) string, and s is a short string (<=100).

A subsequence of a string is a new string which is formed from the original string by deleting some (can be none) of the characters without disturbing the relative positions of the remaining characters. (ie, "ace" is a subsequence of "abcde" while "aec" is not).

Example 1:
```
s = "abc", t = "ahbgdc"

Return true.
```

Example 2:
```
s = "axc", t = "ahbgdc"

Return false.
```

Follow up:
* If there are lots of incoming S, say S1, S2, ... , Sk where k >= 1B, and you want to check one by one to see if T has its subsequence. In this scenario, how would you change your code?

### 两个指针
两个指针`sp`和`tp`分别指向`s`和`t`，随着指针`tp`不断右移，如果`sp`和`tp`指向的字符匹配上了，`sp`也右移一格。如果`s`是`t`的子串，最终`sp`应该遍历完整个`s`。

这个解法复杂度取决于`s`和`t`中长度较大的那个：`O(Max(lenS, lenT))`。
```
sp                  tp
|                   |
a,b,c               a,h,b,g,d,c

a和a匹配上了，同时右移，
  sp                  tp
  |                   |
a,b,c               a,h,b,g,d,c

b和h没有匹配上，只有tp右移，
  sp                  tp
  |                   |
a,b,c               a,h,b,g,d,c
```



#### 代码
```java
class Solution {
    public boolean isSubsequence(String s, String t) {
        if (s == null || t == null) {
            return false;
        }
        int sp = 0, tp = 0;
        while (sp < s.length() && tp < t.length()) {
            if (s.charAt(sp) == t.charAt(tp)) {
                sp++;
            }
            tp++;
        }
        return sp == s.length();
    }
}
```

#### 结果
![is-subsequence-1](/images/leetcode/is-subsequence-1.png)


### 利用`String.indexOf()`的Binary Search
当`t`的长度远远大于`s`的情况下，采取在`t`中查询`s`中字符的策略，比完整遍历`t`要更有效。在字符串中查询某个字符可以用基于二分查找的`String.indexOf()`函数，
> public int indexOf(String str, int fromIndex)

其中第二个参数`fromIndex`可以从指定位置继续查找。每次匹配到一个元素之后，下一次就从这个字符的下一个位置开始查找，
```
                 fromIndex
|                   |
a,b,c               a,h,b,g,d,c


                    fromIndex
  |                   |
a,b,c               a,h,b,g,d,c
```

#### 代码
```java
class Solution {
    public boolean isSubsequence(String s, String t) {
        if (s == null || t == null) {
            return false;
        }
        char c = 0;
        int prev = 0;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            prev = t.indexOf(c, prev);
            if (prev < 0) {
                return false;
            }
            prev++;
        }
        return true;
    }
}
```

#### 结果
![is-subsequence-2](/images/leetcode/is-subsequence-2.png)
