---
layout: post
title: "Leetcode - Algorithm - Nim Game "
date: 2017-08-03 14:34:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
You are playing the following Nim Game with your friend: There is a heap of stones on the table, each time one of you take turns to remove 1 to 3 stones. The one who removes the last stone will be the winner. You will take the first turn to remove the stones.

Both of you are very clever and have optimal strategies for the game. Write a function to determine whether you can win the game given the number of stones in the heap.

For example, if there are 4 stones in the heap, then you will never win the game: no matter 1, 2, or 3 stones you remove, the last stone will always be removed by your friend.

### 所有4的倍数都必输，其他都必胜
就像题目里描述的那样，
1. 如果只有`1`或`2`或`3`颗石头，我只要全拿走就赢了。
2. 如果有`4`颗石头，无论我拿走`1`或`2`或`3`颗石头，对方都可以拿走剩下的全部，我必输。所以`4`是一个死穴。
3. 如果有`5`或`6`或`7`颗石头，那我都可以拿到只剩下`4`颗石头，把死穴留给对手，则我必胜。
4. 如果有`8`颗石头，无论我拿走`1`,`2`,`3`颗石头，对方都可以把`4`这个死穴留给我，所以我必输。
5. 所以所有`4`的倍数都是死穴。

#### 代码
```java
public class Solution {
    public boolean canWinNim(int n) {
        if (n <= 0) { return false; }
        return n % 4 > 0;
    }
}
```

#### 结果
![nim-game-1](/images/leetcode/nim-game-1.png)
