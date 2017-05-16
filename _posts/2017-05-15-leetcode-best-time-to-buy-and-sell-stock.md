---
layout: post
title: "Leetcode - Algorithm - Best Time To Buy And Sell Stock "
date: 2017-05-15 20:21:35
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","dynamic programming"]
level: "easy"
description: >
---

### 题目
Say you have an array for which the ith element is the price of a given stock on day i.

If you were only permitted to complete at most one transaction (ie, buy one and sell one share of the stock), design an algorithm to find the maximum profit.
```
Example 1:
Input: [7, 1, 5, 3, 6, 4]
Output: 5
```

max. difference = 6-1 = 5 (not 7-1 = 6, as selling price needs to be larger than buying price)
```
Example 2:
Input: [7, 6, 4, 3, 1]
Output: 0
```

In this case, no transaction is done, i.e. max profit = 0.

### 把问题抽象成：找截止目前的历史最低价位
直觉上看需要两个指针，一个指向买入价，一个指向卖出价。但实际上没办法决定指向买入价的指针什么时候向前移。因为这个问题不不能用贪心算法，比如买入价指针永远指向最低价格，卖出价指向最高价格。这就会出现想`[7, 1, 5, 3, 6, 4]`里找到`1`和`7`的错误结果。

只需要换个角度看这个问题，就很简单。
> 我站在今天的角度，看历史上的最低价位在哪里，就在那天买入。这就是到今天为止，我最好的投资策略。完整遍历整个数组，就能找到最优投资策略。

时间复杂度：$$O(n)$$
空间复杂度：$$O(1)$$

#### 代码
```java
public class Solution {
    public int maxProfit(int[] prices) {
        int min = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int i = 0; i < prices.length; i++) {
            min = Math.min(min,prices[i]);
            maxProfit = Math.max(maxProfit,prices[i] - min);
        }
        return maxProfit;
    }
}
```

#### 结果
![best-time-to-buy-and-sell-stock-1](/images/leetcode/best-time-to-buy-and-sell-stock-1.png)
