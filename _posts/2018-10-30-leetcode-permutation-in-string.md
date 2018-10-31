---
layout: post
title: "Leetcode - Algorithm - Permutation In String "
date: 2018-10-30 20:53:15
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "medium"
description: >
---

### 题目
Given two strings s1 and s2, write a function to return true if s2 contains the permutation of s1. In other words, one of the first string's permutations is the substring of the second string.
Example 1:
```
Input:s1 = "ab" s2 = "eidbaooo"
Output:True
Explanation: s2 contains one permutation of s1 ("ba").
```

Example 2:
```
Input:s1= "ab" s2 = "eidboaoo"
Output: False
```

Note:
* The input strings only contain lower case letters.
* The length of both given strings is in range [1, 10,000].

### 窗口法
核心问题在于怎么记录Permutation的特征信息？最简便的方法就是用一个`int[26]`统计每个字母的出现的频率。因为无论`abb`,`bab`还是`bba`，本质就是1个`a`加上2个`b`。

先统计出`s1`的字母频率，然后在`s2`上开一个和`s1`一样大小的窗口，统计窗口内字母的频率，
```
ab:
 a b
[1,1,0,0,0,0,0,0,0,0,0...0]

         e       i
[0,0,0,0,1,0,0,0,1,0,0...0]
|-|
eidbaooo

        d         i
 [0,0,0,1,0,0,0,0,1,0,0...0]
 |-|
eidbaooo

...
...

    a b
   [1,1,0,0,0,0,0,0,0,0,0...0]
   |-|
eidbaooo
```

#### 代码
```java
class Solution {
    public boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length()) return false;
        char[] c1 = s1.toCharArray();
        int[] count1 = new int[26];
        for (char c : c1) count1[c - 'a']++;
        char[] c2 = s2.toCharArray();
        int[] count2 = new int[26];
        for (int i = 0; i < c1.length; i++) count2[c2[i] - 'a']++;
        for (int lo = 0, hi = c1.length; hi < c2.length; lo++, hi++) {
            if (Arrays.equals(count1, count2)) return true;
            count2[c2[lo] - 'a']--;
            count2[c2[hi] - 'a']++;
        }
        if (Arrays.equals(count1, count2)) return true;
        return false;
    }
}
```

#### 结果
![permutation-in-string-1](/images/leetcode/permutation-in-string-1.png)
