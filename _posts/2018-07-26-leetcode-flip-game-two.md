---
layout: post
title: "Leetcode - Algorithm - Flip Game Two "
date: 2018-07-26 18:26:24
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","back tracking"]
level: "medium"
description: >
---

### 题目
You are playing the following Flip Game with your friend: Given a string that contains only these two characters: + and -, you and your friend take turns to flip two consecutive "++" into "--". The game ends when a person can no longer make a move and therefore the other person will be the winner.

Write a function to determine if the starting player can guarantee a win.

Example:
```
Input: s = "++++"
Output: true
Explanation: The starting player can guarantee a win by flipping the middle "++" to become "+--+".
```
Follow up:
Derive your algorithm's runtime complexity.

### 朴素的递归思路
始终以“我”的主视角回答我是否必胜。分为我的回合，和对手的回合，那么，
* 我所有可能解里只要找到一条必胜解，我就必胜（因为我肯定就选这个解法）
* 对手所有可能解里，我都必胜，我才必胜（否则对手一旦选了我不能必胜的策略，我就尴尬了）

#### 代码
```java
public boolean canWin(String s) {
    return helper(s,true);
}
// 思路一样，但不需要预先找好所有可行解，走一步看一步
// 每次递归回答的都是我有没有必胜解
// 我所有可能解里只要找到一条必胜解，我就必胜
// 对手所有可能解里，我都必胜，我才必胜
private boolean helper(String s, boolean isMe) {
    for (int i = 0; i < s.length()-1; i++) {
        if (s.charAt(i) == '+' && s.charAt(i+1) == '+') {   // 找到可翻转的"++"
            String way = s.substring(0,i) +
                         "--" +
                         s.substring(i+2,s.length());
            boolean res = helper(way,!isMe);                // 递归
            if (isMe) {
                if (res) { return true; }                   // 我所有可能解里只要找到一条必胜解，我就必胜
            } else {
                if (!res) { return false; }                 // 对手所有可能解里，我都必胜，我才必胜
            }
        }
    }
    return !isMe;       // 注意！for循环之后的剩余支线和无解的base case正好重合
}
```

#### 结果
![flip-game-two-1](/images/leetcode/flip-game-two-1.png)


### 回溯算法
第一种方法因为每次递归构造了新的字符串，所以不需要每次递归后将字符串改回原值。如果用一个`char[]`数组代替字符串，代码就会是一个更标准的回溯算法。

#### 代码
```java
private char[] way = new char[0];

public boolean canWin(String s) {
    way = s.toCharArray();
    return helper(true);
}
// 思路一样，但运用回溯算法，并且用一个数组代替每次拷贝字符串
private boolean helper(boolean isMe) {
    for (int i = 0; i < way.length-1; i++) {
        if (way[i] == '+' && way[i+1] == '+') {
            way[i] = '-'; way[i+1] = '-';
            boolean res = helper(!isMe);                // 递归
            way[i] = '+'; way[i+1] = '+';               // 马上回溯
            if (isMe) {
                if (res) { return true; }               // 我所有可能解里只要找到一条必胜解，我就必胜
            } else {
                if (!res) { return false; }             // 对手所有可能解里，我都必胜，我才必胜
            }
        }
    }
    return !isMe;       // 注意！for循环之后的剩余支线和无解的base case正好重合
}
```

#### 结果
![flip-game-two-2](/images/leetcode/flip-game-two-2.png)


### 表驱动的动态规划
回溯过程中会重复解决很多次相同的子问题，比如，下面两种情况都递归到了`----++`这个子问题，

路径一，
```
++++++
--++++
----++
```
路径二，
```
++++++
++--++
----++
```

所以考虑用一个`HashMap`储存之前处理过的全部子问题，避免重复解决，

#### 代码
```java
private static char[] way = new char[0];
private static Map<String,Boolean> memo = new HashMap<>();

public boolean canWin(String s) {
    way = s.toCharArray();
    memo.clear();
    return helper(true);
}
// 回溯 + 表驱动的动态规划
private boolean helper(boolean isMe) {
    String s = new String(way);
    if (memo.containsKey(new String(way))) { return memo.get(s); }
    for (int i = 0; i < way.length-1; i++) {
        if (way[i] == '+' && way[i+1] == '+') {
            way[i] = '-'; way[i+1] = '-';
            boolean res = helper(!isMe);                // 递归
            way[i] = '+'; way[i+1] = '+';               // 马上回溯
            if (isMe) {
                if (res) {                              // 我所有可能解里只要找到一条必胜解，我就必胜
                    memo.put(s,true);
                    return true;
                }
            } else {
                if (!res) {                             // 对手所有可能解里，我都必胜，我才必胜
                    memo.put(s,false);
                    return false;
                }
            }
        }
    }
    memo.put(s,!isMe);
    return !isMe;       // 注意！for循环之后的剩余支线和无解的base case正好重合
}
```

#### 结果
![flip-game-two-3](/images/leetcode/flip-game-two-3.png)
