---
layout: post
title: "Leetcode - Algorithm - Best Time To Buy And Sell Stock With Cooldown "
date: 2017-09-23 00:24:46
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming"]
level: "medium"
description: >
---

### 题目
Say you have an array for which the ith element is the price of a given stock on day i.

Design an algorithm to find the maximum profit. You may complete as many transactions as you like (ie, buy one and sell one share of the stock multiple times) with the following restrictions:

You may not engage in multiple transactions at the same time (ie, you must sell the stock before you buy again).
After you sell your stock, you cannot buy stock on next day. (ie, cooldown 1 day)
Example:
```
prices = [1, 2, 3, 0, 2]
maxProfit = 3
transactions = [buy, sell, cooldown, buy, sell]
```

### 比较笨的动态规划
假设总共有100天。第`100`天的最高受益应该是下面这一系列可能型中的最大值，
```
第97天的最高受益 + [第99天买入,第100天卖出]
第96天的最高受益 + [第98天买入,第100天卖出]
第95天的最高受益 + [第97天买入,第100天卖出]
第94天的最高受益 + [第96天买入,第100天卖出]
...
...
第0天的最高受益 + [第2天买入,第100天卖出]
第-1天的最高受益 + [第1天买入,第100天卖出]
```
这样动态规划的总复杂度是 $$O(n^2)$$。

#### 代码
```java
class Solution {
    public int maxProfit(int[] prices) {
        int[] dp = new int[prices.length+2];
        for (int i = 3; i < dp.length; i++) {
            int curr = prices[i-2];
            if (curr <= prices[i-3]) {
                dp[i] = dp[i-1];
            } else {
                int max = 0;
                for (int j = 2; j <= i; j++) {
                    max = Math.max(max, dp[j-2] + curr - prices[j-2]);
                }
                dp[i] = max;
            }
        }
        return dp[dp.length-1];
    }
}
```

#### 结果
![best-time-to-buy-and-sell-stock-with-cooldown-1](/images/leetcode/best-time-to-buy-and-sell-stock-with-cooldown-1.png)


### 更好的动态规划
把每天结束的状态分为两种：
1. 以一个买入`buy`动作结束
2. 以一个买入`sell`动作结束

以一个买入动作结束，当天的最大受益有两种可能：要么是前天卖出以后，昨天休息一天，今天继续买入，所以，
```
buyToday = sellBeforeYesterday - priceToday
```
要么是在昨天或昨天之前买入的，今天也没有卖出，维持昨天以`buy`动作结束的最大受益，
```
buyToday = buyYesterday
```
所以，今天以买入动作结束的最大受益是这两种可能的最大值，
```
buyToday = Max (buyYesterday, sellBeforeYesterday - priceToday)
```
同理，以卖出动作结束今天的交易，也有两种可能，要么是昨天之前卖出的，今天休息，维持昨天卖出的最高受益，要么昨天是买入状态结束的，今天卖出。同样取两种可能的最大值，
```
sellToday = Max (sellYesterday, buyYesterday + priceToday)
```

#### 代码
```java
class Solution {
    public int maxProfit(int[] prices) {
        if (prices.length == 0) { return 0; }
        int[] buy = new int[]{0,-prices[0]};
        int[] sell = new int[]{0,0,0};
        for (int i = 1; i < prices.length; i++) {
            buy[0] = buy[1];
            sell[0] = sell[1];
            sell[1] = sell[2];
            buy[1] = Math.max(buy[0],sell[0]-prices[i]);
            sell[2] = Math.max(sell[1],buy[0]+prices[i]);
        }
        return sell[2];
    }
}
```

#### 结果
![best-time-to-buy-and-sell-stock-with-cooldown-2](/images/leetcode/best-time-to-buy-and-sell-stock-with-cooldown-2.png)
