---
layout: post
title: "Leetcode - Algorithm - Judge Route Circle "
date: 2017-08-25 19:04:11
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string"]
level: "easy"
description: >
---

### 题目
Initially, there is a Robot at position (0, 0). Given a sequence of its moves, judge if this robot makes a circle, which means it moves back to the original place.

The move sequence is represented by a string. And each move is represent by a character. The valid robot moves are R (Right), L (Left), U (Up) and D (down). The output should be true or false representing whether the robot makes a circle.

Example 1:
```
Input: "UD"
Output: true
Example 2:
Input: "LL"
Output: false
```

### 朴素遍历字符串
没什么花样，用两个变量`orz`和`vtc`分别表示水平和纵向偏移量。因为只有上下左右四种选项，一个`switch`就可以完成。

#### 代码
```java
class Solution {
    public boolean judgeCircle(String moves) {
        int vtc = 0, orz = 0;
        for (int i = 0; i < moves.length(); i++) {
            char c = moves.charAt(i);
            switch (c) {
                case 'U': ++vtc; break;
                case 'D': --vtc; break;
                case 'L': --orz; break;
                case 'R': ++orz; break;
                default: return false;
            }
        }
        return vtc == 0 && orz == 0;
    }
}
```

#### 结果
![judge-route-circle-1](/images/leetcode/judge-route-circle-1.png)
