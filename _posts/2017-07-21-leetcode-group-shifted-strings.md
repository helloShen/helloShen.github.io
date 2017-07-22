---
layout: post
title: "Leetcode - Algorithm - Group Shifted Strings "
date: 2017-07-21 20:11:43
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","string"]
level: "medium"
description: >
---

### 题目
Given a string, we can "shift" each of its letter to its successive letter, for example: `"abc" -> "bcd"`. We can keep "shifting" which forms the sequence:

`"abc" -> "bcd" -> ... -> "xyz"`
Given a list of strings which contains only lowercase alphabets, group all strings that belong to the same shifting sequence.

For example, given: `["abc", "bcd", "acef", "xyz", "az", "ba", "a", "z"]`,
A solution is:
```
[
  ["abc","bcd","xyz"],
  ["az","ba"],
  ["acef"],
  ["a","z"]
]
```

### 主要思路
对于任意的一组平移字符串，比如`"abc" -> "bcd" -> ... -> "xyz"`，都可以定义一个 **标准形式**。所以给定一组随意的字符串，把它们全部映射到标准形式，就可以给它们分组。

这里可以取一个最简单的标准形式：把以`a`开头的那个字符串，定义为标准形式。


### 用`HashMap`

#### Java代码
```java
public class Solution {
    public List<List<String>> groupStrings(String[] strings) {
        Map<String,Integer> dic = new HashMap<>();
        List<List<String>> result = new ArrayList<>();
        for (String s : strings) {
            String std = std(s);
            Integer index = dic.get(std);
            if (index == null) {
                dic.put(std,result.size());
                List<String> list = new ArrayList<>(Arrays.asList(new String[]{s}));
                result.add(list);
            } else {
                List<String> list = result.get(index);
                list.add(s);
            }
        }
        return result;
    }
    // map a string to standard space: start with 'a'
    private String std(String s) {
        int dis = s.charAt(0) - 'a';
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = (char)(s.charAt(i) - dis);
            if (c < 'a') { c += 26; }
            sb.append(c);
        }
        return sb.toString();
    }
}
```

#### 结果
![group-shifted-strings-1](/images/leetcode/group-shifted-strings-1.png)
