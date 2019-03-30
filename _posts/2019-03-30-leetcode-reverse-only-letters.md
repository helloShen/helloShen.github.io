---
layout: post
title: "Leetcode - Algorithm - Reverse Only Letters "
date: 2019-03-30 13:57:26
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string", "reverse"]
level: "easy"
description: >
---

### 题目
Given a string S, return the "reversed" string where all characters that are not a letter stay in the same place, and all letters reverse their positions.

Example 1:
```
Input: "ab-cd"
Output: "dc-ba"
```

Example 2:
```
Input: "a-bC-dEf-ghIj"
Output: "j-Ih-gfE-dCba"
```

Example 3:
```
Input: "Test1ng-Leet=code-Q!"
Output: "Qedo1ct-eeLg=ntse-T!"
```

Note:

* S.length <= `100`
* `33` <= `S[i].ASCIIcode` <= `122`
* `S` doesn't contain `\` or `"`

### 把非字母的字符抽出来
先遍历整个字符串，记录所有非字母字符的位置，然后删掉这些字符。最后只剩下字母，比如，
```
Test1ng-Leet=code-Q!    -->     TestngLeetcodeQ
```

反转后者。最后再把之前记录的非字母字符插入原位置即可。

反转字符串可以递归处理。每次去中位点，从中间切断，交换位置，然后递归到下一层重复这个动作。
```
    abcd
    /  \
  cd    ab
   |    |
  dc    ba
```


#### 代码
```java
class Solution {
    public String reverseOnlyLetters(String S) {
        washStr(S);
        StringBuilder reversed = reverse(cleanSb);
        for (int i = 0; i < poc; i++) {
            reversed.insert(otherCharsOffset[i], otherChars[i]);
        }
        return reversed.toString();
    }

    private StringBuilder cleanSb;
    private char[] otherChars;
    private int poc;
    private int[] otherCharsOffset;
    private int poco;

    // assertion: sb != null
    private StringBuilder reverse(StringBuilder sb) {
        int len = sb.length();
        if (len <= 1) return sb;
        if (len == 2) {
            char last = sb.charAt(1);
            sb.deleteCharAt(1);
            sb.insert(0, last);
            return sb;
        }
        int mid = (sb.length() - 1) / 2;
        StringBuilder left = reverse(new StringBuilder(sb.substring(0, mid + 1)));
        StringBuilder right = reverse(new StringBuilder(sb.substring(mid + 1)));
        return right.append(left);
    }

    private void washStr(String str) {
        cleanSb = new StringBuilder();
        int len = str.length();
        otherChars = new char[len];
        poc = 0;
        otherCharsOffset = new int[len];
        poco = 0;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (Character.isLetter(c)) {
                cleanSb.append(c);
            } else {
                otherChars[poc++] = c;
                otherCharsOffset[poco++] = i;
            }
        }
    }
}
```

#### 结果
![reverse-only-letters-1](/images/leetcode/reverse-only-letters-1.png)


### 也可以直接用`Character.reverse()`库函数

#### 代码
```java
class Solution {
    public String reverseOnlyLetters(String S) {
        washStr(S);
        StringBuilder reversed = cleanSb.reverse();
        for (int i = 0; i < poc; i++) {
            reversed.insert(otherCharsOffset[i], otherChars[i]);
        }
        return reversed.toString();
    }

    private StringBuilder cleanSb;
    private char[] otherChars;
    private int poc;
    private int[] otherCharsOffset;
    private int poco;

    private void washStr(String str) {
        cleanSb = new StringBuilder();
        int len = str.length();
        otherChars = new char[len];
        poc = 0;
        otherCharsOffset = new int[len];
        poco = 0;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (Character.isLetter(c)) {
                cleanSb.append(c);
            } else {
                otherChars[poc++] = c;
                otherCharsOffset[poco++] = i;
            }
        }
    }
}
```

#### 结果
![reverse-only-letters-2](/images/leetcode/reverse-only-letters-2.png)
