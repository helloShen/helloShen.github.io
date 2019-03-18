---
layout: post
title: "Leetcode - Algorithm - Longest Word In Dictionary Through Deletion "
date: 2019-03-18 12:12:52
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string", "sort"]
level: "medium"
description: >
---

### 题目
Given a string and a string dictionary, find the longest string in the dictionary that can be formed by deleting some characters of the given string. If there are more than one possible results, return the longest word with the smallest lexicographical order. If there is no possible result, return the empty string.

Example 1:
```
Input:
s = "abpcplea", d = ["ale","monkey", "apple", "plea"]

Output:
"apple"
```

Example 2:
```
Input:
s = "abpcplea", d = ["a","b","c"]

Output:
"a"
```

Note:
* All the strings in the input will only contain lower-case letters.
* The size of the dictionary won't exceed 1,000.
* The length of all the strings in the input won't exceed 1,000.

### 怎么判定字符串能否缩减？
假设我们有`abpcplea`和`apple`两个字符串，用两个指针分别指向两个字符串，比较后一个`apple`中每一个字符在`abpcplea`中的位置，如果每一个都能找到对应的字符，并且偏移值都大于等于`apple`中的位置，则能证明`abpcplea`能缩减成`apple`。
```
0 2 456
| | |||
abpcplea

01234
|||||
apple
```

### 可以先排序
题目要求返回最长的并且是按字母排序最小的字符串，因此，如果不想遍历每个字符串，找到第一个符合条件的字符串就返回，就需要预先排序。
1. 先按照字母排序（升序）
2. 再按照字符串长度排序（降序）

#### 代码
```java
class Solution {
    public String findLongestWord(String s, List<String> d) {
        Collections.sort(d);
        Collections.sort(d, (String a, String b) -> b.length() - a.length());
        for (String w : d) {
            if (canReductTo(s, w)) return w;
        }
        return "";
    }

    private boolean canReductTo(String from, String to) {
        int pf = 0, pt = 0;
        int lf = from.length(), lt = to.length();
        while (pt < lt) {
            char ct = to.charAt(pt);
            while (pf < lf && from.charAt(pf) != ct) pf++;
            if (pf == lf) return false;
            pf++; pt++;
        }
        return true;
    }
}
```


#### 结果
![longest-word-in-dictionary-through-deletion-1](/images/leetcode/longest-word-in-dictionary-through-deletion-1.png)


### 也可以不排序
实际上排序开销比较大，复杂度`O(NlogN)`，`N`为字符串的数量。不排序的话，遍历整个列表每个字符串即可，复杂度为`O(N)`。

#### 代码
```java
class Solution {
    public String findLongestWord(String s, List<String> d) {
        int maxLen = 0;
        String maxString = "~"; // in ascii table "~" is larger than "a"
        for (String w : d) {
            if ((w.length() > maxLen || (w.length() == maxLen && w.compareTo(maxString) < 0)) && canReductTo(s, w)) {
                maxLen = w.length();
                maxString = w;
            }
        }
        return (maxString.equals("~"))? "" : maxString;
    }

    private boolean canReductTo(String from, String to) {
        int pf = 0, pt = 0;
        int lf = from.length(), lt = to.length();
        while (pt < lt) {
            char ct = to.charAt(pt);
            while (pf < lf && from.charAt(pf) != ct) pf++;
            if (pf == lf) return false;
            pf++; pt++;
        }
        return true;
    }
}
```

#### 结果
![longest-word-in-dictionary-through-deletion-2](/images/leetcode/longest-word-in-dictionary-through-deletion-2.png)
