---
layout: post
title: "Leetcode - Algorithm - Reorder Log Files "
date: 2019-03-31 16:32:43
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array", "sort"]
level: "easy"
description: >
---

### 题目
You have an array of logs.  Each log is a space delimited string of words.

For each log, the first word in each log is an alphanumeric identifier.  Then, either:

Each word after the identifier will consist only of lowercase letters, or;
Each word after the identifier will consist only of digits.
We will call these two varieties of logs letter-logs and digit-logs.  It is guaranteed that each log has at least one word after its identifier.

Reorder the logs so that all of the letter-logs come before any digit-log.  The letter-logs are ordered lexicographically ignoring identifier, with the identifier used in case of ties.  The digit-logs should be put in their original order.

Return the final order of the logs.

Example 1:
```
Input: ["a1 9 2 3 1","g1 act car","zo4 4 7","ab1 off key dog","a8 act zoo"]
Output: ["g1 act car","a8 act zoo","ab1 off key dog","a1 9 2 3 1","zo4 4 7"]
```

Note:
* 0 <= logs.length <= 100
* 3 <= logs[i].length <= 100
* logs[i] is guaranteed to have an identifier, and a word after the identifier.


### 区分开“数字log”和“字母log”，只给字母log排序
具体实现有3个地方可以优化效率。

#### 怎么比较`act car`和`off key`？
传统方法是先从空格切分，
```
["act", "car"]
["off", "key"]
```
然后逐个字符串比较。

其实不需要，可以直接比较`act car`和`off key`，找到不相等字符，直接返回两者差值。
```
|
act car
off key
```

就算是`aaa`和`aaaa`这种情况，由于空格符在ascii表上是`0`，远远小于`a`的`97`，
```
   |
aaa
aaaa
```

#### 另外两个提高效率的地方
1. 不要用lambda表达式，效率比老老实实写`Comparator`类慢10倍。用lambda表达式`32ms`，不用`2ms`。
1. `String[]`效率比`List<String>`效率高。


#### 代码
```java
class Solution {
    public String[] reorderLogFiles(String[] logs) {
        String[] letterLogs = new String[100];
        String[] digitLogs = new String[100];
        int pl = 0, pd = 0;
        for (String log : logs) {
            if (Character.isDigit(log.charAt(log.indexOf(' ') + 1))) {
                digitLogs[pd++] = log;
            } else {
                letterLogs[pl++] = log;
            }
        }
        Arrays.sort(letterLogs, 0, pl, new Comparator<String>(){
            public int compare(String a, String b) {
                int pa = a.indexOf(' ') + 1;
                int pb = b.indexOf(' ') + 1;
                return a.substring(pa).compareTo(b.substring(pb));
            }
        });
        String[] res = new String[logs.length];
        int p = 0;
        for (int i = 0; i < pl; i++) {
            res[p++] = letterLogs[i];
        }
        for (int i = 0; i < pd; i++) {
            res[p++] = digitLogs[i];
        }
        return res;
    }
}
```

#### 结果
![reorder-log-files-1](/images/leetcode/reorder-log-files-1.png)
