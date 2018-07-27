---
layout: post
title: "Leetcode - Algorithm - Paint Fence "
date: 2018-07-22 15:49:20
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic program"]
level: "easy"
description: >
---

### 题目
There is a fence with n posts, each post can be painted with one of the k colors.

You have to paint all the posts such that no more than two adjacent fence posts have the same color.

Return the total number of ways you can paint the fence.

Note:
n and k are non-negative integers.

Example:
```
Input: n = 3, k = 2
Output: 6
Explanation: Take c1 as color 1, c2 as color 2. All possible ways are:

            post1  post2  post3      
 -----      -----  -----  -----       
   1         c1     c1     c2
   2         c1     c2     c1
   3         c1     c2     c2
   4         c2     c1     c1  
   5         c2     c1     c2
   6         c2     c2     c1
```

### 递归
对于有`n`个栅栏的问题`f(n)`，有子问题`f(n-1)`有解。新加入的一条栅栏的颜色有两种情况：
1. 和前一条颜色不同: `k-1`种可能
2. 和前一条颜色相同: `1`中可能

如果是第一种情况，颜色不同，他们都是合法的，大家都很愉快。

但如果是第二种情况，也就是倒数第二条栅栏和最后一条颜色相同，这时候如果倒数第三条颜色也相同，就不合法。颜色不同则是合法的。

所以，递归式：
> f(n) = f(n-1) * (k-1) + f(n-2) * (k-1)


#### 代码
```java
class Solution {
        public int numWays(int n, int k) {
            if (n == 0 || k == 0) { return 0; }
            if (n == 1) { return k; }
            if (n == 2) { return k * k; }
            return (numWays(n-1,k) + numWays(n-2,k)) * (k-1);
        }
}
```

#### 结果
![paint-fence-1](/images/leetcode/paint-fence-1.png)


### 带表格的动态规划

#### 代码
```java
public int numWays(int n, int k) {
    int[] memo = new int[n+1];
    return helper(n,k,memo);
}
private int helper(int n, int k, int[] memo) {
    if (n == 0 || k == 0) { return 0; }
    if (n == 1) { return k; }
    if (n == 2) { return k * k; }
    memo[n] = (((memo[n-1] == 0)? helper(n-1,k,memo) : memo[n-1]) + ((memo[n-2] == 0)? helper(n-2,k,memo) : memo[n-2])) * (k-1);
    return memo[n];
}
```

#### 结果
![paint-fence-2](/images/leetcode/paint-fence-2.png)


### 自底向上的动态规划

#### 代码
```java
public int numWays(int n, int k) {
    if (n == 0 || k == 0) { return 0; }
    if (n == 1) { return k; }
    if (n == 2) { return k * k; }
    int a = k, b = k * k;
    int m = 3;
    while (m++ <= n) {
        int next = (a + b)*(k-1);
        a = b;
        b = next;
    }
    return b;
}
```

#### 结果
![paint-fence-2](/images/leetcode/paint-fence-2.png)
