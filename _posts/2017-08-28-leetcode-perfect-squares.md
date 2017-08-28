---
layout: post
title: "Leetcode - Algorithm - Perfect Squares "
date: 2017-08-28 15:40:37
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dfs","dynamic programming","math"]
level: "medium"
description: >
---

### 题目
Given a positive integer n, find the least number of perfect square numbers (for example, 1, 4, 9, 16, ...) which sum to n.

For example, given n = 12, return 3 because 12 = 4 + 4 + 4; given n = 13, return 2 because 13 = 4 + 9.

### 第一层 - 朴素DFS
这题非常好。首先这题用 **DFS** 递归肯定能解决问题。每层递归减去一个平方数。直到剩下的数归零。`dfs(15)->dfs(6)->dfs(2)->dfs(1)->dfs(0)`

#### 代码
```java
class Solution {
        private Map<Integer,Integer> memo = new HashMap<>();
        public int numSquares(int n) {
            memo = new HashMap<>();
            return dfs(n);
        }
        private int dfs(int remain) {
            if (remain == 0) { return 0; }
            Integer min = memo.get(remain);
            if (min != null) { return min; }
            min = Integer.MAX_VALUE;
            for (int i = 1; i <= (int)Math.sqrt(remain); i++) {
                min = Math.min(min,1+dfs(remain-i*i));
            }
            return min;
        }
}
```

#### 结果
![perfect-squares-1](/images/leetcode/perfect-squares-1.png)


### 第二层 - 带备忘录的DFS（动态规划）
但DFS主要的问题就是会重复解决很多次相同的子问题。这个时候用一个备忘录，随时记录已经处理过的子问题的结果，就能有很大提升。

用标准动态规划的思路来思考这个问题，`T(n)`就变成若干减去平方数后的子问题中的最小值加一：
> T(n) = min( T(n-1*1), T(n-2*2), T(n-3*3), ... ) + 1

另外DFS的过程中，注意及时剪枝，能进一步提升效率。

复杂度 $$O(n*sqrt(n))$$

#### 代码
带备忘录的DFS，已经可以算做是动态规划了。
```java
class Solution {
    private Map<Integer,Integer> memo = new HashMap<>();
    private int globalMin = Integer.MAX_VALUE;
    public int numSquares(int n) {
        globalMin = Integer.MAX_VALUE;
        return dfs(n,n,0);
    }
    private Integer dfs(int orig, int remain, int count) {
        if (remain == 0) { return 0; }                                  // base case
        if (count == globalMin) { return null; }                        // 剪枝
        Integer history = memo.get(remain);                             // 查表
        if (history != null) { return history; }
        Integer min = null;                                             // 递归
        for (int i = (int)Math.sqrt(remain); i > 0; i--) {
            Integer sub = dfs(orig,remain-i*i,count+1);
            if (sub == null) { continue; }
            min = (min == null)? sub+1 : Math.min(min,1+sub);
            if (orig == remain) {                                       // 随时更新最小值
                globalMin = Math.min(globalMin,min);
            }
        }
        if (min != null) { memo.put(remain,min); }
        return min;
    }
}
```

#### 结果
![perfect-squares-2](/images/leetcode/perfect-squares-2.png)


### 第三层 - 标准自底向上的动态规划
如果用标准自底向上的动态规划，更有序地处理问题，就可以从小到大计算`[1,n]`每个数的结果。复杂度同样是 $$O(n*sqrt(n))$$。

但这里的结果比方法二，带备忘录的DFS好了很多，主要是因为这里用的是迭代，而不是递归。省去了很多次方法调用的时间。而且方法二用的不是尾递归，编译器没有办法优化。

另外这里我们用数组代替了原先的`Map`做备忘录，更高效。

#### 代码
```java
class Solution {
    public int numSquares(int n) {
        if (n < 1) { return 0; }
        int[] res = new int[n+1];
        // base case 0->0, 1->1
        res[1] = 1;
        for (int i = 2; i <= n; i++) {
            for (int j = 1; j <= Math.sqrt(i); j++) {
                res[i] = (res[i] == 0)? res[i-j*j] + 1 : Math.min(res[i],res[i-j*j]+1);
            }
        }
        return res[n];
    }
}
```

#### 结果
![perfect-squares-3](/images/leetcode/perfect-squares-3.png)

#### 使用静态的备忘录，避免每次创建新的备忘录对象
这里只做了一个小改动，只是把备忘录变成静态成员。因为Leetcode不断地创建新的`Solution`对象来测试的时候，之前的备忘录就可以一直使用。
```java
class Solution {
    private static int[] res = new int[16];
    private static int cursor = 2;
    { res[1] = 1; }
    public int numSquares(int n) {
        if (n < 1) { return 0; }
        if (cursor > n) { return res[n]; }                          // result exists
        if (res.length < n+1) { res = Arrays.copyOf(res,n*2); }     // result array not long enough
        for (int i = cursor; i <= n; i++,cursor++) {
            for (int j = 1; j <= Math.sqrt(i); j++) {
                res[i] = (res[i] == 0)? res[i-j*j] + 1 : Math.min(res[i],res[i-j*j]+1);
            }
        }
        return res[n];
    }
}
```

#### 结果
效率提升了10倍。
![perfect-squares-4](/images/leetcode/perfect-squares-4.png)

### 第四层 - 数学法`Legendre Three Squares Theorem`
根据`Legendre Three Squares Theorem`定理，绝大部分正整数都可以用三个正整数的平方和表示。除非某些特殊的数，符合下面特性，才可能由四个数的平方和组成。  
![legendre-theorem](/images/leetcode/legendre-theorem.png)

所以可以一次检测由1个，2个，4个数的平方和组成的情况，剩下的都是3个数的平方和。

#### 代码
```java
class Solution {
    public int numSquares(int n) {
        // is 1?
        if (isSquare(n)) { return 1; }
        // is 4?
        int copy = n;
        while ((copy & 3) == 0) { // n % 4 = 0
            copy >>= 2;
        }
        if ((copy & 7) == 7) { // n % 8 = 7
            return 4;
        }
        // is 2?
        for (int i = 1; i <= Math.sqrt(n); i++) {
            if (isSquare(n-i*i)) { return 2; }
        }
        // is 3!
        return 3;
    }
    private boolean isSquare(int n) {
        int sqrt = (int)Math.sqrt(n);
        return sqrt * sqrt == n;
    }
}
```
#### 结果
![perfect-squares-5](/images/leetcode/perfect-squares-5.png)
