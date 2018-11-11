---
layout: post
title: "Leetcode - Algorithm - To Lower Case "
date: 2018-11-11 14:48:54
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "easy"
description: >
---

### 题目
Implement function ToLowerCase() that has a string parameter str, and returns the same string in lowercase.

Example 1:
```
Input: "Hello"
Output: "hello"
Example 2:

Input: "here"
Output: "here"
Example 3:

Input: "LOVELY"
Output: "lovely"
```

### 直接遍历字符串
字符`A`在ascii表中编码`65`。字符`a`编码`97`。给所有大写字母编码加上`32`即可。

#### 代码
```java
class Solution {
    public String toLowerCase(String str) {
        char[] sa = str.toCharArray();
        for (int i = 0; i < sa.length; i++) {
            if (sa[i] >= 'A' && sa[i] <= 'Z') sa[i] += 32;
        }
        return new String(sa);
    }
}
```

#### 结果
![to-lower-case-1](/images/leetcode/to-lower-case-1.png)
