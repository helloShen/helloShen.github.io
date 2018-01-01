---
layout: post
title: "Leetcode - Algorithm - Longest Palindromic Subsequence "
date: 2018-01-01 18:05:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
Given a string s, find the longest palindromic subsequence's length in s. You may assume that the maximum length of s is 1000.

Example 1:
```
Input:

"bbbab"
Output:
4
```
One possible longest palindromic subsequence is "bbbb".
```
Example 2:
Input:

"cbbd"
Output:
2
```
One possible longest palindromic subsequence is "bb".

### 子问题递归思路
Definition:
* dp[i][j]: the longest palindromic subsequence's length of substring(i, j)

State transition:
* dp[i][j] = dp[i+1][j-1] + 2 if s.charAt(i) == s.charAt(j)
* otherwise, dp[i][j] = Math.max(dp[i+1][j], dp[i][j-1])
* Initialization: dp[i][i] = 1

#### 代码
```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        return dp(s, 0, s.length()-1);
    }
    public int dp(String s, int begin, int end) {
        int len = end - begin;
        if (len <= 0) { return len+1; }
        if (s.charAt(begin) == s.charAt(end)) {
            return dp(s, begin+1, end-1) + 2;
        } else {
            return Math.max(dp(s, begin+1, end), dp(s, begin, end-1));
        }
    }
}
```

#### 结果
![longest-palindromic-subsequence-1](/images/leetcode/longest-palindromic-subsequence-1.png)


### 尝试用一个备忘录记录已经处理过的子问题

#### 代码
```java
class Solution {
    private static String local = null;
    private static int[][] memo = null;

    public int longestPalindromeSubseq(String s) {
        local = s;
        int len = s.length();
        memo = new int[len][len];
        return dp(0, local.length()-1);
    }
    private int dp(int begin, int end) {
        int len = end - begin;
        if (len <= 0) { return len+1; }
        if (memo[begin][end] > 0) { return memo[begin][end]; }
        if (local.charAt(begin) == local.charAt(end)) {
            return dp(begin+1, end-1) + 2;
        } else {
            return Math.max(dp(begin+1, end), dp(begin, end-1));
        }
    }
}
```

#### 结果
![longest-palindromic-subsequence-2](/images/leetcode/longest-palindromic-subsequence-2.png)


### 自底向上的动态规划（迭代）

#### 代码
```java
class Solution {
    public int longestPalindromeSubseq(String s) {
        int len = s.length();
        int[][] memo = new int[len][len];
        for (int i = 0; i < len; i++) {
            memo[i][i] = 1;
            char charRight = s.charAt(i);
            for (int j = i-1; j >= 0; j--) {
                if (charRight == s.charAt(j)) {
                    memo[j][i] = memo[j+1][i-1] + 2;
                } else {
                    memo[j][i] = Math.max(memo[j+1][i], memo[j][i-1]);
                }
            }
        }
        return memo[0][len-1];
    }
}
```

#### 结果
![longest-palindromic-subsequence-3](/images/leetcode/longest-palindromic-subsequence-3.png)
