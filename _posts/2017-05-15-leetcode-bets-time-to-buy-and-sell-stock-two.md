---
layout: post
title: "Leetcode - Algorithm - Bets Time To Buy And Sell Stock Two "
date: 2017-05-15 21:09:34
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","greedy"]
level: "easy"
description: >
---

### 题目
Say you have an array for which the ith element is the price of a given stock on day i.

Design an algorithm to find the maximum profit. You may complete as many transactions as you like (ie, buy one and sell one share of the stock multiple times). However, you may not engage in multiple transactions at the same time (ie, you must sell the stock before you buy again).

### 只有明天涨价，今天才买入
还是以`[7, 1, 5, 3, 6, 4]`为例。第一天`7`块，看到第二天`1`块，就选择不买入。第二天`1`块，第三天`5`块，就买入。

思路一样，代码实现起来，讲故事的方式可以不同。

#### 故事1：用`buy`和`sell`两个指针维护一个窗口
每天都偷看明天的价格，如果下跌，今天就不买入。如果上涨，就买入。如果后天继续上涨，就继续持有，扩大`buy-sell`的窗口。

标签中标明 **贪心算法**，可能就是指这里维护一个尽可能大的窗口。

```java
public class Solution {
    public int maxProfit(int[] prices) {
        int buy = 0, sell = 0, profit = 0;
        while (sell+1 < prices.length) { // 偷看明天的价格，决定今天卖不卖
            if (prices[sell+1] <= prices[sell]) {
                profit += (prices[sell] - prices[buy]);
                sell++; buy = sell;
            } else {
                sell++;
            }
        }
        profit += (prices[sell] - prices[buy]);
        return profit;
    }
}
```

#### 故事2：每天一结算，负收益不计入总收益
只维护一个指针，表示`今天`。每天都计算今天和昨天的差价。如果是负收益，就不计入总收益中。

```java
public class Solution {
    public int maxProfit(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            int todayProfit = prices[i] - prices[i-1];
            if (todayProfit > 0) { profit += todayProfit; }
        }
        return profit;
    }
}
```

#### 结果
![bets-time-to-buy-and-sell-stock-two-1](/images/leetcode/bets-time-to-buy-and-sell-stock-two-1.png)
