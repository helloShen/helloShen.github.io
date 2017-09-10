---
layout: post
title: "Leetcode - Algorithm - Bulls And Cows "
date: 2017-09-09 19:15:43
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
You are playing the following Bulls and Cows game with your friend: You write down a number and ask your friend to guess what the number is. Each time your friend makes a guess, you provide a hint that indicates how many digits in said guess match your secret number exactly in both digit and position (called "bulls") and how many digits match the secret number but locate in the wrong position (called "cows"). Your friend will use successive guesses and hints to eventually derive the secret number.

For example:
```
Secret number:  "1807"
Friend's guess: "7810"
Hint: 1 bull and 3 cows. (The bull is 8, the cows are 0, 1 and 7.)
```
Write a function to return a hint according to the secret number and friend's guess, use A to indicate the bulls and B to indicate the cows. In the above example, your function should return "1A3B".

Please note that both secret number and friend's guess may contain duplicate digits, for example:
```
Secret number:  "1123"
Friend's guess: "0111"
```
In this case, the 1st 1 in friend's guess is a bull, the 2nd or 3rd 1 is a cow, and your function should return "1A1B".
You may assume that the secret number and your friend's guess only contain digits, and their lengths are always equal.


### 遍历3次
最直观的遍历三次，
* 第一次遍历统计频率
* 第二次遍历先处理bulls
* 第三次遍历再处理cows

#### 代码
```java
class Solution {
    public String getHint(String secret, String guess) {
        if (secret.length() != guess.length()) { return null; }
        int[] freq = new int[10];
        // 第一次遍历统计频率
        for (char c : secret.toCharArray()) {
            freq[c-'0']++;
        }
        int bulls = 0, cows = 0;
        // 第二次遍历先处理bull
        for (int i = 0; i < guess.length(); i++) {
            int cs = secret.charAt(i) - '0';
            int cg = guess.charAt(i) - '0';
            if (cg == cs) {
                ++bulls;
                --freq[cg];
            }
        }
        // 第三次遍历再处理cow
        for (int i = 0; i < guess.length(); i++) {
            int cs = secret.charAt(i) - '0';
            int cg = guess.charAt(i) - '0';
            if (cg != cs && freq[cg] > 0) {
                 ++cows;
                --freq[cg];
            }
        }
        return bulls + "A" + cows + "B";
    }
}
```

#### 结果
![bulls-and-cows-1](/images/leetcode/bulls-and-cows-1.png)

### 一次遍历
用两个额外数组，`int[] secretMemo`记录到目前出现了但还没猜到的数字。`int[] guessMemo`记录猜过了但答案里还没出现的数字。

然后，每往后读一个新数字，猜一个新数字，就到这两个数组里去查记录。

`int[] secretMemo`和`int[] guessMemo`的下标就代表`[0-9]`十个阿拉伯数字，所以每次查询的复杂度是 $$O(1)$$。总体复杂度为 $$O(n)$$。而且多项式是没有乘数的。

#### 代码
```java
class Solution {
    public String getHint(String secret, String guess) {
        if (secret.length() != guess.length()) { return null; }
        int[] secretMemo = new int[10];
        int[] guessMemo = new int[10];
        int bulls = 0, cows = 0;
        for (int i = 0; i < guess.length(); i++) {
            int numSecret = secret.charAt(i) - '0';
            int numGuess = guess.charAt(i) - '0';
            if (numSecret == numGuess) {
                ++bulls;
            } else {
                if (secretMemo[numGuess] > 0) { // 这次猜到前面的数字
                    ++cows;
                    --secretMemo[numGuess];
                } else {
                    ++guessMemo[numGuess];      // 这次没猜到以前的数，才记录下来
                }
                if (guessMemo[numSecret] > 0) { // 这次的数是以前猜过的
                    ++cows;
                    --guessMemo[numSecret];
                } else {
                    ++secretMemo[numSecret];    // 这次的数以前没猜过，才记录下来
                }
            }
        }
        return bulls + "A" + cows + "B";
    }
}
```

#### 结果
![bulls-and-cows-2](/images/leetcode/bulls-and-cows-2.png)

### 一次遍历，只用一个数组
如果 **每个数出现一次+1，每被猜一次-1**，只用一个数组就够了。

#### 代码
```java
class Solution {
    public String getHint(String secret, String guess) {
        if (secret.length() != guess.length()) { return null; }
        int[] nums = new int[10];
        int bulls = 0, cows = 0;
        for (int i = 0; i < guess.length(); i++) {
            int s = secret.charAt(i) - '0';
            int g = guess.charAt(i) - '0';
            if (s == g) {
                ++bulls;
            } else {
                if (nums[s] < 0) { ++cows; }
                if (nums[g] > 0) { ++cows; }
                ++nums[s]; // 某数出现一次，加一
                --nums[g]; // 某数猜过一次，减一
            }
        }
        return bulls + "A" + cows + "B";
    }
}
```

#### 结果
![bulls-and-cows-3](/images/leetcode/bulls-and-cows-3.png)
