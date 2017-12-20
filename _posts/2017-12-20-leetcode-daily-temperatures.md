---
layout: post
title: "Leetcode - Algorithm - Daily Temperatures "
date: 2017-12-20 15:47:58
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack","hash table"]
level: "medium"
description: >
---

### 主要收获
> 乱序数列可以看成一组有起伏的波形态。
```
|          
|         |      
| |   |   |     |
| | | | | | | | |
-----------------
4 2 1 2 1 3 1 1 2
  |-----| |
     ^    | 这个3 决定了左边4个比他小的数字的最近更大元素。
     |    |
     +----+
```

### 题目
Given a list of daily temperatures, produce a list that, for each day in the input, tells you how many days you would have to wait until a warmer temperature. If there is no future day for which this is possible, put 0 instead.

For example, given the list temperatures = `[73, 74, 75, 71, 69, 72, 76, 73]`, your output should be `[1, 1, 4, 2, 1, 1, 0, 0]`.

**Note**: The length of temperatures will be in the range `[1, 30000]`. Each temperature will be an integer in the range `[30, 100]`.

### 从后往前遍历，$$O(n)$$
从后往前遍历，用一个大小为101的数组记录每个温度最近一次出现的日子。

#### 代码
```java
class Solution {
    private static int[] memo = new int[101];

    public int[] dailyTemperatures(int[] temperatures) {
        Arrays.fill(memo,0);
        int[] res = new int[temperatures.length];
        for (int i = temperatures.length-1; i >= 0; i--) {
            int temperature = temperatures[i];
            int wait = Integer.MAX_VALUE;
            for (int j = temperature + 1; j < 101; j++) {
                int future = memo[j];
                if (future > i) {
                    wait = Math.min(wait,future - i);
                }
            }
            res[i] = (wait == Integer.MAX_VALUE)? 0 : wait;
            memo[temperature] = i;
        }
        return res;
    }
}
```

#### 结果
系统没有足够的提交数据。


### 用`Stack`是这一类题目的标准解法，$$O(n)$$
这一类题目的共同点就是在一组乱序数列中，找最近的下一个更大的元素。

这里的诀窍是怎么去看待一个乱序数组。其实乱序数组就是一组有起伏的波。任何一个元素都是所有紧挨着它左边的比它小的元素的最近更大元素。
```
|          
|         |      
| |   |   |     |
| | | | | | | | |
-----------------
4 2 1 2 1 3 1 1 2
  |-----| |
     ^    | 这个3 决定了左边4个比他小的数字的最近更大元素。
     |    |
     +----+
```

用一个`Stack`储存所有没有找到最近更大元素的元素，找到了之后从Stack中弹出。

比如我有`[76, 74, 71, 75, 69, 72, 76, 73]`，Stack先连续压入3个递减的数字，
```
76
76, 74
76,74,71
```
等处理`75`的时候，就可以确定它左边的`[74,71]`两个元素的最近更大元素是`75`。这时候弹出`[74,71]`，再压入`75`，Stack变成，
```
76,75
```
然后再压入更小的`69`。等处理`72`的时候，再把`69`弹出，依次类推。

#### 代码
```java
class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int[] stack = new int[temperatures.length];
        int[] res = new int[temperatures.length];
        int top = -1;
        for (int i = 0; i < temperatures.length; i++) {
            while (top > -1 && temperatures[i] > temperatures[stack[top]]) {
                int index = stack[top--];
                res[index] = i - index;
            }
            stack[++top] = i;
        }
        return res;
    }
}
```

#### 结果
系统没有足够的提交数据。
