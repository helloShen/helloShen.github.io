---
layout: post
title: "Leetcode - Algorithm - Decode Ways "
date: 2017-05-01 21:45:49
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","string"]
level: "medium"
description: >
---

### 题目

### 暴力递归 $$P(2^n)$$


这个问题的关键就是当出现小于等于`26`的组合的时候，会出现两个分支，考虑`12345`这种情况，在遇到`1`的时候，
```
1 + "2345"子问题
12 + "345"子问题
```
需要特殊处理`0`的情况，
```
1 + "0345"子问题  # "0345"不是正确的编码格式，会解码错误
10 + "345"子问题
```

暴力递归一定会重复解决很多次相同子问题，但还是写一下，练手。递归的思路是，用一个`int[0]`做计数器，每次走通到最后，`count[0]++`。

#### 代码
```java
public class Solution {
    public int numDecodings(String s) {
        if (s.length() == 0) { return 0; }
        char[] c = s.toCharArray();
        int[] count = new int[]{ 0 };
        recursive(c,0,count);
        return count[0];
    }
    public void recursive(char[] c, int cur, int[] count) {
        if (cur >= c.length) { count[0]++; return; }
        if (c[cur] == '0') { return; }
        recursive(c,cur+1,count);
        if (cur+1 < c.length && (c[cur] == '1' || (c[cur] == '2' && c[cur+1] <= '6'))) {
            recursive(c,cur+2,count);
        }
    }
}
```

#### 结果
果然超时。
![decode-ways-1](/images/leetcode/decode-ways-1.png)


### 自底向上的动态规划 $$O(n)$$
记录下每个处理过的子问题的结果。这里递归是`DFS`深度优先的，所以是自底向上的。

#### 代码
```java
public class Solution {
    public int numDecodings(String s) {
        if (s.length() == 0) { return 0; }
        char[] c = s.toCharArray();
        Map<Integer,Integer> memo = new HashMap<>();
        memo.put(s.length(),1);
        return recursive(c,0,memo);
    }
    public int recursive(char[] c, int cur, Map<Integer,Integer> memo) {
        Integer res = memo.get(cur);
        if (res != null) { return res; }
        if (c[cur] == '0') {
            memo.put(cur,0);
            return 0;
        }
        res = recursive(c,cur+1,memo);
        if (cur+1 < c.length && (c[cur] == '1' || (c[cur] == '2' && c[cur+1] <= '6'))) {
            res = res + recursive(c,cur+2,memo);
        }
        memo.put(cur,res);
        return res;
    }
}
```

#### 结果
银弹！
![decode-ways-2](/images/leetcode/decode-ways-2.png)


### 简单用`int[]`替代`Map`做备忘录

#### 代码
```java
public class Solution {
    public int numDecodings(String s) {
        if (s.length() == 0) { return 0; }
        char[] c = s.toCharArray();
        int[] memo = new int[c.length+1];
        Arrays.fill(memo,-1);
        memo[c.length] = 1;
        return dp(c,0,memo);
    }
    public int dp(char[] c, int cur, int[] memo) {
        Integer res = memo[cur];
        if (res != -1) { return res; }
        if (c[cur] == '0') {
            memo[cur] = 0;
            return 0;
        }
        res = dp(c,cur+1,memo);
        if (cur+1 < c.length && (c[cur] == '1' || (c[cur] == '2' && c[cur+1] <= '6'))) {
            res = res + dp(c,cur+2,memo);
        }
        memo[cur] = res;
        return res;
    }
}
```

#### 结果
简单用`int[]`替代`Map`又快了一倍。
![decode-ways-3](/images/leetcode/decode-ways-3.png)


### 迭代版动态规划
因为是自底向上的动态规划，就是从后往前填表的一个过程。而且有点类似`Fibonacci`数列。

#### 代码
```java
public class Solution {
    public int numDecodings(String s) {
        if (s.length() == 0) { return 0; }
        char[] c = s.toCharArray();
        int[] memo = new int[c.length+1];
        memo[c.length] = 1;
        for (int i = c.length-1; i >= 0; i--) {
            if (c[i] == '0') { memo[i] = 0; continue; }
            if (i+1 < c.length && (c[i] == '1' || (c[i] == '2' && c[i+1] <= '6'))) {
                memo[i] = memo[i+1] + memo[i+2];
            } else {
                memo[i] = memo[i+1];
            }
        }
        return memo[0];
    }
}
```

#### 结果
更快了。
![decode-ways-4](/images/leetcode/decode-ways-4.png)
