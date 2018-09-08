---
layout: post
title: "Leetcode - Algorithm - Jewels And Stones "
date: 2018-09-08 15:19:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string","array"]
level: "easy"
description: >
---

### 题目
You're given strings J representing the types of stones that are jewels, and S representing the stones you have.  Each character in S is a type of stone you have.  You want to know how many of the stones you have are also jewels.

The letters in J are guaranteed distinct, and all characters in J and S are letters. Letters are case sensitive, so "a" is considered a different type of stone from "A".

Example 1:
```
Input: J = "aA", S = "aAAbbbb"
Output: 3
```

Example 2:
```
Input: J = "z", S = "ZZ"
Output: 0
```

Note:
* S and J will consist of letters and have length at most 50.
The characters in J are distinct.

### 用两个`boolean[26]`数组记录珠宝的种类
因为所有珠宝种类要么大写`[A~Z]`，要么小写`[a~z]`。

#### 代码
```java
class Solution {
    public int numJewelsInStones(String J, String S) {
        boolean[] upper = new boolean[26];
        boolean[] lower = new boolean[26];
        for (char c : J.toCharArray()) {
            if (Character.isUpperCase(c)) {
                upper[c - 'A'] = true;
            } else { // lower case
                lower[c - 'a'] = true;
            }
        }
        int count = 0;
        for (char c : S.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (upper[c - 'A']) {
                    count++;
                }
            } else if (lower[c - 'a']) {
                    count++;
            }
        }
        return count;
    }
}
```

#### 结果
![jewels-and-stones-1](/images/leetcode/jewels-and-stones-1.png)


### 用一个`boolean[123]`数组记录珠宝种类
上面做法每次还要调用`Character`区分大小写。直接用一个`boolean[256]`映射所有ascii码，可以不用区分。因为字母中编号最大的`z = 122`，所以只要一个`boolean[123]`即可。

#### 代码
```java
class Solution {
    public int numJewelsInStones(String J, String S) {
        boolean[] isJewel = new boolean[123];
        int count = 0;
        for (char c : J.toCharArray()) {
            isJewel[c] = true;
        }
        for (char c : S.toCharArray()) {
            if (isJewel[c]) count++;
        }
        return count;
    }
}
```

#### 结果
![jewels-and-stones-2](/images/leetcode/jewels-and-stones-2.png)
