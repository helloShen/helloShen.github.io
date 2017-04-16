---
layout: post
title: "Leetcode - Algorithm - Jump Game "
date: 2017-04-15 15:52:25
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["greedy"]
level: "medium"
description: >
---

### 主要收获
系统复习了`暴力回溯算法`，`动态规划`，以及`贪心算法`。这三个思想是一脉相承的。主要涉及到两个重要概念：`最优子结构`和`子问题重复`。动态规划解决的主要问题就是`子问题重复`，最常用的手段就是用备忘录记录所有已经处理过的子问题，避免重复求解。也可以自底向上地先求得子问题的最优解，然后再求原问题的最优解，这就需要保证问题具有`最优子结构`，就是说原问题的最优解一定是一系列子问题最优解的组合。

动态规划总的来说是 **稳健** 的。就算不具有最优子结构，不用自底向上的求解路线，只是用个备忘录避免暴力回溯算法的重复子问题，最后总是保证能得出最优解。

但贪心算法和动态规划又不一样。**贪心算法是不稳健的**。就算问题具有最优子结构，贪心算法也不是老老实实的先求出子问题的最优解，再往上整合。而是在一开始对未来没有任何预期的情况下盲目做出“贪心”的局部最优解。很可能最后掉入次优解的陷阱。所以用贪心算法，条件非常苛刻。

有两个非常形象的例子。第一个，就是《算法导论》第16章的`活动选择问题`，也叫排课问题。注意，问题一定是 **能排最多数量的课的排课方式**。而不是 **教室利用率最高的排课方式。** 只有是前者，才可以用贪心算法，每次都考虑下课最早的课。如果要求利用率最高，3节课的总时间未必就比2节课要长，所以不能用贪心算法。

第二个例子，就是这个`Jump Game`。只有当 **步数代表我最远可以跳的距离，但我也可以选择跳更近的距离**，才适合用贪心算法。否则如果 **步数代表我必须跳的距离**，则贪心算法得不到最优解。这一步跳得远，可能就错过了中间某个超级强有力的跳板。

### 题目
Given an array of non-negative integers, you are initially positioned at the first index of the array.

Each element in the array represents your maximum jump length at that position.

Determine if you are able to reach the last index.

For example:
```
A = [2,3,1,1,4], return true.

A = [3,2,1,0,4], return false.
```

### 朴素深度优先递归试探每种可能性，指数级复杂度
所以朴素的解法就是深度优先算法，递归尝试每一种可能的跳跃路线。以`[2,3,1,1,4]`为例，首元素`2`，就分裂出去两个递归调用，分别从第2，第3个元素为起点，依次类推。一旦某条路到达最后一个元素，就成功。

#### 代码
```java
public class Solution {
    public boolean canJump(int[] nums) {
        return dfs(nums,0);
    }
    public boolean dfs(int[] nums, int cursor) { // 深度优先递归
        if (cursor == nums.length-1) { return true; }
        if (cursor >= nums.length || nums[cursor] == 0 ) { return false; }
        for (int i = 1; i <= nums[cursor]; i++) {
            if (dfs(nums,cursor+i)) { return true; }
        }
        return false;
    }
}
```

#### 结果
这么大规模的遍历回溯尝试，复杂度至少是指数级的。递归深度也很深。超时属于正常。
![jump-game-1](/images/leetcode/jump-game-1.png)

### 带备忘录的动态规划
暴力尝试所有可能，主要的问题是：**子问题重复**。比如最坏的情况`[5,4,3,2,1]`，以`5`为起点，可以到达后面的所有点，分别递归出去以`4`,`3`,`2`,`1`为起点的子问题。然后以`4`为起点的时候，又递归出去以`3`,`2`,`1`为起点的子问题。靠近尾部的子问题会被重复很多次。

子问题重复非常适合用带备忘录的动态规划来解决，用一个`Map`记录已经处理过的子问题的解。重复求解之前，先查表。

#### 代码
递归版。
```java
public class Solution {
    public boolean canJump(int[] nums) {
        Map<Integer,Boolean> memo = new HashMap<>();
        return dp(nums,0,memo);
    }
    public boolean dp(int[] nums, int cursor, Map<Integer,Boolean> memo) {
        Boolean res = memo.get(cursor);
        if (res != null) { return res; }
        // dfs
        if (cursor == nums.length-1) { memo.put(cursor,true); return true; }
        if (cursor >= nums.length || nums[cursor] == 0 ) { memo.put(cursor,false); return false; }
        res = false;
        for (int i = 1; i <= nums[cursor]; i++) {
            if (dp(nums,cursor+i,memo)) { res = true; break; }
        }
        memo.put(cursor,res);
        return res;
    }
}
```

#### 结果
`StackOverFlow`。主要是递归太深了。深度为`n`,不是$$\log_{}{n}$$，不适合递归。但在我自己机器上测试下来，快多了。
![jump-game-2](/images/leetcode/jump-game-2.png)


### 贪心算法
把能不能跳到最后一格的问题，转变为：**最远能跳到哪里？** 的问题。最后跳得最远的方案，一定是在每一步都选择可以跳得最远的点。

**注意！** 问题不是如果选到`5`就必须一下跳5格。如果是这样，那么最后跳得最远的方案，就未必是每次选择可以跳得最远的点。看到现在跳5格，但5格后可能下个数字是1。说不定现在跳3格，3格后是100，就赚到了。这样就不符合使用贪心算法的必要条件：**最优子结构。**

现在这个问题的特点是，比如`[5,2,2,3,1,2,3]`最开始在`5`不是说就一定要往后跳`5`格，而是跳1,2,3,4,5格任选。所以每一步一定是选可以跳得最远的那个选择，因为代表着接下来有更多的尝试选择。

#### 代码
```java
public class Solution {
    public boolean canJump(int[] nums) {
        int cursor = 0;
        for (int scope = 0; cursor < nums.length && cursor <= scope; cursor++) {
            scope = Math.max(scope, cursor + nums[cursor]); // greedy
        }
        return cursor == nums.length;
    }
}
```

#### 结果
![jump-game-3](/images/leetcode/jump-game-3.png)
