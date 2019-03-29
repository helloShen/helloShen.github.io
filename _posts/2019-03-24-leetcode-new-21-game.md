---
layout: post
title: "Leetcode - Algorithm - New 21 Game "
date: 2019-03-24 14:44:32
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math", "dynamic programming"]
level: "medium"
description: >
---

### 题目
Alice plays the following game, loosely based on the card game "21".

Alice starts with 0 points, and draws numbers while she has less than K points.  During each draw, she gains an integer number of points randomly from the range [1, W], where W is an integer.  Each draw is independent and the outcomes have equal probabilities.

Alice stops drawing numbers when she gets K or more points.  What is the probability that she has N or less points?

Example 1:
```
Input: N = 10, K = 1, W = 10
Output: 1.00000
Explanation:  Alice gets a single card, then stops.
```

Example 2:
```
Input: N = 6, K = 1, W = 10
Output: 0.60000
Explanation:  Alice gets a single card, then stops.
In 6 out of W = 10 possibilities, she is at or below N = 6 points.
```

Example 3:
```
Input: N = 21, K = 17, W = 10
Output: 0.73278
```

Note:
* 0 <= K <= N <= 10000
* 1 <= W <= 10000
* Answers will be accepted as correct if they are within 10^-5 of the correct answer.
* The judging time limit has been reduced for this question.

### 暴力DFS
不想用数学公式算，那就让它一个个跳转。从`count[0] = 1.0`开始，
```
count[0] = 1.0
    count[1], count[2], count[3], ..., ..., count[10] += 0.1

然后，
count[1] = 0.1
    count[2], count[3], count[4], ..., ..., count[11] += 0.01

以此类推，累计概率。
```

#### 代码
```java
class Solution {
    public double new21Game(int N, int K, int W) {
        if (K == 0 && N == 0) return (double) 1.0;
        if (N == 0) return (double) 0.0;
        if (K == 0) return (double) 1.0;
        n = N;
        k = K;
        w = W;
        max = K - 1 + W;
        count = new double[max + 1];
        dfs(0, (double)1.0);
        double sumProb = 0.0;
        for (int i = K; i <= N; i++) sumProb += count[i];
        return sumProb;
    }

    private int n;
    private int k;
    private int w;
    private int max;
    private double[] count;

    private void dfs(int num, double prob) {
        if (num < k) {
            for (int i = 1; i <= w; i++) dfs(num + i, prob / w);
        } else {
            count[num] += prob;
        }
    }
}
```

#### 结果
![new-21-game-1](/images/leetcode/new-21-game-1.png)


### 动态规划
核心视角是，
> 到达每一点`i`的概率，都是到达`[i - W]  ~ [i - 1]`这`W`个前驱点的概率总和除以W。

用一个数组`double[] dp`，`dp[i]`表示得到`i`点的概率。很容易写出代码。

#### 代码
```java
class Solution {
    public double new21Game(int N, int K, int W) {
        if (K == 0 && N == 0) return (double) 1.0;
        if (N == 0) return (double) 0.0;
        if (K == 0) return (double) 1.0;
        int max = K - 1 + W;
        double[] dp = new double[max + 1];
        dp[0] = 1.0;
        for (int i = 1; i <= max; i++) {
            for (int j = i - W; j < i; j++) {
                if (j >= 0 && j < K) dp[i] += dp[j];
            }
            dp[i] /= W;
        }
        double sumProb = 0.0;
        for (int i = K; i <= N; i++) sumProb += dp[i];
        return sumProb;
    }
}
```

#### 结果
![new-21-game-2](/images/leetcode/new-21-game-2.png)


### `dp[i]`本身就记录跳转概率总和
上面的动态规划每次都要去找前`W`个前驱点的概率。复杂度`O(W)`。实际上我们可以在`O(1)`时间里完成这件事。想象我们维护一个大小为`W`的窗口，覆盖第`i`个节点的前`W`个前驱点。记录下他们的概率和，记做`Wsum`。然后每次往后移，就加入下一个新节点的概率，再移除窗口内首个点的概率。
* dp[0] = 1.0
* dp[i]: probability of get points i
* dp[i] = sum(last W dp values) / W
* Wsum = sum(last W dp values)

还是以`N = 21, K = 17, W = 10`为例，
```
到达点11的概率总和等于：
    到达[1~10]点的概率总和 / 10

因为[1~10]中每一个点跳转到11的概率都是"1/10"。

同理，到达点15的概率总和等于：到达[5~14]点的概率总和 / 10
```

从`17`开始，跳转结束。比如点`20`，
```
到达点20的概率总和等于：
    到达[10~16]点的概率总和 / 10

17, 18, 19三点不跳转了。他们应该被计入到最后的输出结果中。

所以从点`18`开始，不计入点`17`跳转的概率。
```

#### 代码
```java
class Solution {
    public double new21Game(int N, int K, int W) {
        if (K == 0 && N == 0) return (double) 1.0;
        if (N == 0) return (double) 0.0;
        if (K == 0) return (double) 1.0;
        double[] dp = new double[N + 1];
        dp[0] = 1.0;
        double Wsum = 1.0, res = 0.0;
        for (int i = 1; i <= N; i++) {
            dp[i] = Wsum / W;
            if (i < K) {
                Wsum += dp[i];
            } else {
                res += dp[i];
            }
            if (i - W >= 0) Wsum -= dp[i - W];
        }
        return res;
    }
}
```

#### 结果
![new-21-game-3](/images/leetcode/new-21-game-3.png)
