---
layout: post
title: "Leetcode - Algorithm - Guess Number Higher Or Lower "
date: 2017-08-31 15:51:42
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["binary search"]
level: "easy"
description: >
---

### 题目
We are playing the Guess Game. The game is as follows:

I pick a number from 1 to n. You have to guess which number I picked.

Every time you guess wrong, I'll tell you whether the number is higher or lower.

You call a pre-defined API guess(int num) which returns `3` possible results (`-1`, `1`, or `0`):
```
-1 : My number is lower
 1 : My number is higher
 0 : Congrats! You got it!
```
Example:
```
n = 10, I pick 6.

Return 6.
```

### 标准二分查找，$$O(\log_{}{n})$$

#### 代码
```java
/* The guess API is defined in the parent class GuessGame.
 * @param num, your guess
 * @return -1 if my number is lower, 1 if my number is higher, otherwise return 0
 * int guess(int num);
 */
public class Solution extends GuessGame {
    public int guessNumber(int n) {
        return helper(1,n);
    }
    private int helper(int lo, int hi) {
        if (lo >= hi) { return lo; }
        int mid = lo + (hi - lo) / 2;
        int answer = guess(mid);
        switch (answer) {
            case 1: return helper(mid+1,hi);      // mid too small
            case -1: return helper(lo,mid-1);     // mid too big
            case 0: return mid;                   // BINGO
            default: return 0;
        }
    }
}
```

#### 结果
![guess-number-higher-or-lower-1](/images/leetcode/guess-number-higher-or-lower-1.png)
