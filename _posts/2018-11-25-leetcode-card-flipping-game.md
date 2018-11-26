---
layout: post
title: "Leetcode - Algorithm - Card Flipping Game "
date: 2018-11-25 19:29:28
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "medium"
description: >
---

### 题目
On a table are N cards, with a positive integer printed on the front and back of each card (possibly different).

We flip any number of cards, and after we choose one card.

If the number `X` on the back of the chosen card is not on the front of any card, then this number `X` is `good`.

What is the smallest number that is good?  If no number is good, output 0.

Here, `fronts[i]` and `backs[i]` represent the number on the front and back of card `i`.

A flip swaps the front and back numbers, so the value on the front is now on the back and vice versa.

Example:
```
Input: fronts = [1,2,4,4,7], backs = [1,3,4,1,3]
Output: 2
Explanation: If we flip the second card, the fronts are [1,3,4,4,7] and the backs are [1,2,4,1,3].
We choose the second card, which has number 2 on the back, and it isn't on the front of any card, so 2 is good.
```

Note:
* 1 <= fronts.length == backs.length <= 1000.
* 1 <= fronts[i] <= 2000.
* 1 <= backs[i] <= 2000.


### 问题分析
假设我有两组数，
```
front:  [1,2,4,4,7]
back:   [1,3,4,1,3]
```
两组数的全局最小值是`1`，如果希望`1`是`good`的，就需要通过翻牌把`1`排除出第一组`front`。

然后可以发现，除了一张牌正反两面是同一个数字的情况，我们都能通过翻牌把这个数字排除在`front`之外。例子里第一张牌正反都是`1`，因此无法将`1`排除出`front`。

### `Set`记录正反两面数字相同的情况
所以解题思路就是用一个数据结构把这些正反两面相同的数字记录下来，然后找出全局除了这些数字之外最小值。能在`O(1)`时间内完成`contains()`检查的首先想到`HashSet`。

#### 代码
```java
class Solution {
    public int flipgame(int[] fronts, int[] backs) {
        int res = 2001;
        Set<Integer> banned = new HashSet<>();
        for (int i = 0; i < fronts.length; i++) if (fronts[i] == backs[i]) banned.add(fronts[i]);
        for (int i = 0; i < fronts.length; i++) {
            if (!banned.contains(fronts[i])) res = Math.min(res, fronts[i]);
            if (!banned.contains(backs[i])) res = Math.min(res, backs[i]);
        }
        return (res == 2001)? 0 : res;
    }
}
```

#### 结果
![card-flipping-game-1](/images/leetcode/card-flipping-game-1.png)
