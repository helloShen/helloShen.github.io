---
layout: post
title: "Leetcode - Algorithm - Unique Word Abbreviation "
date: 2018-07-24 20:55:39
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table"]
level: "medium"
description: >
---

### 题目
An abbreviation of a word follows the form <first letter><number><last letter>. Below are some examples of word abbreviations:
```
a) it                      --> it    (no abbreviation)

     1
     ↓
b) d|o|g                   --> d1g

              1    1  1
     1---5----0----5--8
     ↓   ↓    ↓    ↓  ↓    
c) i|nternationalizatio|n  --> i18n

              1
     1---5----0
     ↓   ↓    ↓
d) l|ocalizatio|n          --> l10n
```

Assume you have a dictionary and given a word, find whether its abbreviation is unique in the dictionary. A word's abbreviation is unique if no other word from the dictionary has the same abbreviation.

Example:
```
Given dictionary = [ "deer", "door", "cake", "card" ]

isUnique("dear") -> false
isUnique("cart") -> true
isUnique("cane") -> false
isUnique("make") -> true
```

### 思路
首先缩写方式就是保留首字母和尾字母，然后插入中间字母的长度。

然后要搞清楚“唯一”的定义：
比如我搜`hello`，缩写成`h2o`，
* 如果词典里所有词的缩写都不是`h2o`，那`hello`就是唯一的。
* 如果词典里仅有`hello`对应的是`h2o`，那`hello`也是唯一的。
* 其他情况都不是唯一的。


#### 代码
```java
class ValidWordAbbr {

        private static Map<String,Set<String>> abbrs = new HashMap<>();

        public ValidWordAbbr(String[] dictionary) {
            abbrs.clear();
            for (String s : dictionary) {
                String abbr = abbr(s);
                if (abbrs.containsKey(abbr)) {
                    abbrs.get(abbr).add(s);
                } else {
                    abbrs.put(abbr,new HashSet<>(Arrays.asList(new String[]{s})));
                }
            }
        }

        public boolean isUnique(String word) {
            String abbr = abbr(word);
            if (!abbrs.containsKey(abbr)) { return true; }      // 没有这个缩写，肯定是唯一的
            Set<String> words = abbrs.get(abbr);
            if (words.size() > 1) { return false; }             // 有多个词是这个缩写，不是唯一
            return words.contains(word);                        // 最后就看唯一是这个缩写的单词，是不是我们要找的
        }
        private String abbr(String word) {
            int len = word.length();
            return (len < 3)? word : (word.substring(0,1) +
                                     String.valueOf(len-2) +
                                     word.substring(len-1,len));
        }

}

/**
 * Your ValidWordAbbr object will be instantiated and called as such:
 * ValidWordAbbr obj = new ValidWordAbbr(dictionary);
 * boolean param_1 = obj.isUnique(word);
 */
```

#### 结果
![unique-word-abbreviation-1](/images/leetcode/unique-word-abbreviation-1.png)


### 尝试简化数据结构
之前在`HashMap`的值域是一个`HashSet`。现在尝试不记录一个缩写对应的所有单词。因为如果对应的单词超过两个，那所有对应这个缩写的单词都不能是唯一的。所以想了个办法，用`HashMap<String,String>`记录。键域是“缩写”，值域是对应的“单词”。如果超过两个对应单词，值域就为`null`。

#### 代码
```java
class ValidWordAbbr {

        // [缩写，原单词]的键值对。 如果多个单词对应一个缩写，原单词这一项为null。
        private static Map<String,String> abbrs = new HashMap<>();

        public ValidWordAbbr(String[] dictionary) {
            abbrs.clear();
            for (String s : dictionary) {
                String abbr = abbr(s);
                if (abbrs.containsKey(abbr)) {
                    String word = abbrs.get(abbr);
                    if (word != null && !word.equals(s)) {      // 有过相同缩写的其他单词，标为null
                        abbrs.put(abbr,null);
                    }
                } else {
                    abbrs.put(abbr,s);
                }
            }
        }

        public boolean isUnique(String word) {
            String abbr = abbr(word);
            if (!abbrs.containsKey(abbr)) { return true; }
            String s = abbrs.get(abbr);
            return ((s != null) && s.equals(word));
        }
        private String abbr(String word) {
            int len = word.length();
            return (len < 3)? word : (word.substring(0,1) +
                                     String.valueOf(len-2) +
                                     word.substring(len-1,len));
        }

}

/**
 * Your ValidWordAbbr object will be instantiated and called as such:
 * ValidWordAbbr obj = new ValidWordAbbr(dictionary);
 * boolean param_1 = obj.isUnique(word);
 */
```

#### 结果
没想到反而慢了。
![unique-word-abbreviation-2](/images/leetcode/unique-word-abbreviation-2.png)
