---
layout: post
title: "Leetcode - Algorithm - Backspace String Compare "
date: 2018-10-25 23:05:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack", "array"]
level: "easy"
description: >
---

### 题目
Given two strings S and T, return if they are equal when both are typed into empty text editors. # means a backspace character.

Example 1:
```
Input: S = "ab#c", T = "ad#c"
Output: true
Explanation: Both S and T become "ac".
```

Example 2:
```
Input: S = "ab##", T = "c#d#"
Output: true
Explanation: Both S and T become "".
```

Example 3:
```
Input: S = "a##c", T = "#a#c"
Output: true
Explanation: Both S and T become "c".
```

Example 4:
```
Input: S = "a#c", T = "b"
Output: false
Explanation: S becomes "c" while T becomes "b".
```

Note:
* 1 <= S.length <= 200
* 1 <= T.length <= 200
* S and T only contain lowercase letters and '#' characters.

Follow up:
Can you solve it in O(N) time and O(1) space?


### 用数组模拟Stack的行为
这种题肯定是用Stack。用传统`LinkedList`不如`char[]`模拟Stack行为更快。

#### 代码
```java
class Solution {
    public boolean backspaceCompare(String S, String T) {
        char[] s = S.toCharArray();
        char[] t = T.toCharArray();
        int sp = decode(s);
        int tp = decode(t);
        if (sp != tp) return false;
        while (sp > 0) {
            if (s[--sp] != t[--tp]) return false;
        }
        return true;
    }

    private int decode(char[] s) {
        int p = 0;
        for (int i = 0; i < s.length; i++) {
            switch(s[i]) {
                case '#':
                    if (p > 0) p--;
                    break;
                default:
                    s[p++] = s[i];
                    break;
            }
        }
        return p;
    }
}
```

#### 结果
![backspace-string-compare-1](/images/leetcode/backspace-string-compare-1.png)
