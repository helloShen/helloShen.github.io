---
layout: post
title: "Leetcode - Algorithm - Flip Game "
date: 2018-07-25 14:03:46
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "easy"
description: >
---

### 题目
You are playing the following Flip Game with your friend: Given a string that contains only these two characters: "+" and "-", you and your friend take turns to flip two consecutive "++" into "--". The game ends when a person can no longer make a move and therefore the other person will be the winner.

Write a function to compute all possible states of the string after one valid move.

Example:
```
Input: s = "++++"
Output:
[
  "--++",
  "+--+",
  "++--"
]
```
Note: If there is no valid move, return an empty list "[]".


### 遍历字符串，找到"--"即替换成"++"

#### 代码
```java
class Solution {
    public List<String> generatePossibleNextMoves(String s) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < s.length()-1; i++) {
            if (s.charAt(i) == '+' && s.charAt(i+1) == '+') {
                res.add(s.substring(0,i) +
                        "--" +
                        s.substring(i+2,s.length()));
            }
        }
        return res;
    }
}
```

#### 结果
![flip-game-1](/images/leetcode/flip-game-1.png)
