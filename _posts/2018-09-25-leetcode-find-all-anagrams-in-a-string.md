---
layout: post
title: "Leetcode - Algorithm - Find All Anagrams In A String "
date: 2018-09-25 22:23:25
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table"]
level: "easy"
description: >
---

### 题目
Given a string s and a non-empty string p, find all the start indices of p's anagrams in s.

Strings consists of lowercase English letters only and the length of both strings s and p will not be larger than 20,100.

The order of output does not matter.

Example 1:
```
Input:
s: "cbaebabacd" p: "abc"

Output:
[0, 6]

Explanation:
The substring with start index = 0 is "cba", which is an anagram of "abc".
The substring with start index = 6 is "bac", which is an anagram of "abc".
```

Example 2:
```
Input:
s: "abab" p: "ab"

Output:
[0, 1, 2]

Explanation:
The substring with start index = 0 is "ab", which is an anagram of "ab".
The substring with start index = 1 is "ba", which is an anagram of "ab".
The substring with start index = 2 is "ab", which is an anagram of "ab".
```

### 移动窗口
题目的核心在于统计字母频率。虽然用`HashMap`可以很简单地统计频率，并且这题标签也是`Hash Table`，但实际用`int[26]`数组统计词频更好。 考虑`cbaebabacd`的例子，目标是`abc`。可以开一个大小为`3`的 **移动窗口**。
```
cbaebabacd     
|-|  
[c, b, a]       目标 -> [a, b, c]
窗口大小为3

int[26]统计词频
|1|1|1|0|0|0|...  比较   |1|1|1|0|0|0|...  -> 相等
 a b c d e f             a b c d e f  

右移窗口：
cbaebabacd     
 |-|
[b, a, e]       目标 -> [a, b, c]

|0|1|1|0|1|0|...  比较   |1|1|1|0|0|0|...  -> 不相等
 a b c d e f             a b c d e f  
```
每次只需要比较移动窗口中的元素以及目标数组即可。移动窗口的时候可以偷懒，不用每次都拷贝整个数组，只需要移除首元素，添加新尾元素。

#### 代码
```java
class Solution {
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> list = new ArrayList<>();
        if (s.length() < p.length()) return list;
        int[] stdFreq = new int[26];
        int[] actualFreq = new int[26];
        for (int i = 0; i < p.length(); i++) {
            stdFreq[p.charAt(i) - 'a']++;
            actualFreq[s.charAt(i) - 'a']++;
        }
        if (Arrays.equals(stdFreq, actualFreq)) list.add(0);
        for (int i = p.length(), h = 0; i < s.length(); i++, h++) {
            actualFreq[s.charAt(h) - 'a']--;
            actualFreq[s.charAt(i) - 'a']++;
            if (Arrays.equals(stdFreq, actualFreq)) list.add(h + 1);
        }
        return list;
    }
}
```

#### 结果
![find-all-anagrams-in-a-string-1](/images/leetcode/find-all-anagrams-in-a-string-1.png)


### 可以不用每次比较整个数组
另一个偷懒的办法，可以在遍历字符串的过程中直接在数组上维护编辑距离`diff`。具体操作如下，
```
int[26] 统计词频
|0|0|0|0|0|0|...
 a b c d e f

遍历目标数组，统计字母频率，每个字母对应槽位频率+1。diff + 1
a , b, c

得到，
|1|1|1|0|0|0|...     -> diff = 3
 a b c d e f

对于s "cbaebabacd"，开始消去原先统计的词频，每个字母对应槽位频率-1。

|1|1|0|0|0|0|...     -> diff = 2
 a b c d e f
     |
     c -> 词频数组freq[2] = 1 > 0， 说明有多余的c未被消去。此时消去c，离我们的目标近了一步，因此c槽位-1, 同时diff - 1
     |
     cbaebabacd


   |1|0|0|0|0|0|...  -> diff = 1
    a b c d e f
      |
      b -> freq[1] = 1 > 0， 同理b槽位-1， 同时diff - 1
      |
     cbaebabacd

...
...

|0|0|0|-1|-1|0|...  -> diff = 2
 a b c  d e  f
          |
          e -> 词频数组freq[4] = 0。目标没有e，却要强行消去e，离我们的目标更远了一步，所以diff + 1
          |
       cbaebabacd
```

#### 代码
```java
class Solution {
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> list = new ArrayList<>();
        if (s.length() < p.length()) return list;
        int[] table = new int[26];
        int diff = p.length();
        for (int i = 0; i < p.length(); i++) {
            table[p.charAt(i) - 'a']++;
        }
        for (int i = 0; i < p.length(); i++) {
            int idx = s.charAt(i) - 'a';
            if (table[idx] > 0) {
                diff--;
            } else {
                diff++;
            }
            table[idx]--;
        }
        if (diff == 0) list.add(0);
        for (int i = p.length(), h = 0; i < s.length(); i++, h++) {
            int rmIdx = s.charAt(h) - 'a'; // remove first element in window
            if (table[rmIdx] < 0) {
                diff--;
            } else {
                diff++;
            }
            table[rmIdx]++;
            int addIdx = s.charAt(i) - 'a'; // add new element into window
            if (table[addIdx] > 0) {
                diff--;
            } else {
                diff++;
            }
            table[addIdx]--;
            if (diff == 0) list.add(h + 1);
        }
        return list;
    }
}
```

#### 结果
![find-all-anagrams-in-a-string-2](/images/leetcode/find-all-anagrams-in-a-string-2.png)
