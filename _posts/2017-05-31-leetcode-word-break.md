---
layout: post
title: "Leetcode - Algorithm - Word Break "
date: 2017-05-31 17:31:51
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
Given a non-empty string s and a dictionary wordDict containing a list of non-empty words, determine if s can be segmented into a space-separated sequence of one or more dictionary words. You may assume the dictionary does not contain duplicate words.

For example, given
```
s = "leetcode",
dict = ["leet", "code"].
```

Return true because `leetcode` can be segmented as `leet code`.

UPDATE (2017/1/4):
The wordDict parameter had been changed to a list of strings (instead of a set of strings). Please reload the code definition to get the latest changes.

### 自底向上动态规划，递归版，复杂度 $$O(n^2)$$
这道题是非常标准的动态规划问题。假设有字符串`abcde`，问题的解可以转化为一系列子问题的解，
```
T("abcde") =    ( has "abcde"?    &&   T("") )      ||
                ( has "abcd"?     &&   T("e") )     ||
                ( has "abc"?      &&   T("de") )    ||
                ( has "ab"?       &&   T("cde") )   ||
                ( has "a"?        &&   T("bcde") )
```

所以可以总结出下面这个粗略的递归表达式，
> T(n) =    (condition1 && T(n-1))   ||
            (condition2 && T(n-2))   ||
            ...
            (condition(N-1) && T(1)) ||
            (condition(N) && T(0))

#### 代码
```java
public class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Map<Integer,Boolean> memo = new HashMap<>();
        memo.put(s.length(),true);
        return dp(s,0,wordDict,memo);
    }
    public boolean dp(String s, int cur, List<String> wordDict, Map<Integer,Boolean> memo) {
        for (int i = s.length(); i > cur; i--) {
            Boolean sub = memo.get(i);
            if (sub == null) {
                sub = dp(s,i,wordDict,memo);
            }
            if (sub && wordDict.contains(s.substring(cur,i))) {
                memo.put(cur,true);
                return true;
            }
        }
        memo.put(cur,false);
        return false;
    }
}
```

#### 结果
结果还不够好。因为用`Map`做备忘录影响了效率。
![word-break-1](/images/leetcode/word-break-1.png)


### 迭代版动态规划，复杂度$$O(n^2)$$
因为是一个非常标准的自底向上的动态规划，所以最简单的方法不是递归，不需要递出去，再回来。可以直接用一个指针指向字符串的末尾，一点点遍历回来。

#### 代码
```java
public class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        int size = s.length();
        boolean[] memo = new boolean[size+1];
        memo[size] = true;
        for (int i = size-1; i >= 0; i--) {
            for (int j = size; j > i; j--) {
                if (memo[j] && wordDict.contains(s.substring(i,j))) {
                    memo[i] = true;
                    break;
                }
            }
        }
        return memo[0];
    }
}
```

#### 结果
![word-break-2](/images/leetcode/word-break-2.png)


### 用`Map`加快单词查询速度
把所有单词都存入一个`HashMap`，用 $$O(1)$$ 速度查单词。

#### 代码
```java
public class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        int size = s.length();
        boolean[] memo = new boolean[size+1];
        Map<String,Object> dic = new HashMap<>(); // 用Map加快单词查询速度
        for (String word : wordDict) {
            dic.put(word,null);
        }
        memo[size] = true;
        for (int i = size-1; i >= 0; i--) {
            for (int j = size; j > i; j--) {
                if (memo[j] && dic.containsKey(s.substring(i,j))) {
                    memo[i] = true;
                    break;
                }
            }
        }
        return memo[0];
    }
}
```

#### 结果
这里说明测试集合的单词表规模不大。`HashMap`查询的优势没有体现出来。
![word-break-3](/images/leetcode/word-break-3.png)
