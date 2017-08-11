---
layout: post
title: "Leetcode - Algorithm - Bulb Switcher "
date: 2017-08-11 16:03:28
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "medium"
description: >
---

### 题目
There are n bulbs that are initially off. You first turn on all the bulbs. Then, you turn off every second bulb. On the third round, you toggle every third bulb (turning on if it's off or turning off if it's on). For the ith round, you toggle every i bulb. For the nth round, you only toggle the last bulb. Find how many bulbs are on after n rounds.

Example:
```
Given n = 3.

At first, the three bulbs are [off, off, off].
After first round, the three bulbs are [on, on, on].
After second round, the three bulbs are [on, off, on].
After third round, the three bulbs are [on, off, off].
```

So you should return 1, because there is only one bulb is on.

### 动态规划（递归），复杂度 $$O(n^2)$$
显性的规律很容易找，
> `n`被小于它的数整除一次，开关就拨动一次。

而且，
> 后面较大的`n`不会影响到前面较小的`n`的结果。

所以很容易想到动态规划，对于`n`个数，前`n-1`个数的结果保持不变，只需要计算第`n`个数就行。
> f(n) = f(n-1) + calculate(n)

对于第`n`个数，
> 计算它能被多少个数整除。

#### 代码
递归版栈内存不够。
```java
public class Solution {
    public int bulbSwitch(int n) {
        if (n == 0) { return 0; }
        int sub = bulbSwitch(n-1);
        boolean nth = false;
        for (int i = 1; i <= n; i++) {
            if (n % i == 0) { nth = !nth; }
        }
        return (nth)? sub + 1 : sub;
    }
}
```

#### 结果
![bulb-switcher-1](/images/leetcode/bulb-switcher-1.png)

#### 迭代版
迭代版超时。
```java
public class Solution {
        public int bulbSwitch(int n) {
            int result = 0;
            for (int i = 1; i <= n; i++) {
                boolean ith = false;
                for (int j = 1; j <= i; j++) {
                    if (i % j == 0) { ith = !ith; }
                }
                if (ith) { ++result; }
            }
            return result;
        }
}
```

#### 结果
![bulb-switcher-2](/images/leetcode/bulb-switcher-2.png)

### 解法2

#### 代码
```java

```

#### 结果
![bulb-switcher-2](/images/leetcode/bulb-switcher-2.png)


### 规律`[1,3,5,7,9,...]`
直到打印出前50个结果，`1个0`，`1个0`，`1个0`，`1个0`，`1个0`...
```bash
n = 0, result = 0   // 1个0
n = 1, result = 1
n = 2, result = 1
n = 3, result = 1   // 3个1
n = 4, result = 2
n = 5, result = 2
n = 6, result = 2
n = 7, result = 2
n = 8, result = 2   // 5个2
n = 9, result = 3
n = 10, result = 3
n = 11, result = 3
n = 12, result = 3
n = 13, result = 3
n = 14, result = 3
n = 15, result = 3  // 7个3
n = 16, result = 4
n = 17, result = 4
n = 18, result = 4
n = 19, result = 4
n = 20, result = 4
n = 21, result = 4
n = 22, result = 4
n = 23, result = 4
n = 24, result = 4  // 9个4
n = 25, result = 5
n = 26, result = 5
n = 27, result = 5
n = 28, result = 5
n = 29, result = 5
n = 30, result = 5
n = 31, result = 5
n = 32, result = 5
n = 33, result = 5
n = 34, result = 5
n = 35, result = 5  // 11个5
n = 36, result = 6
n = 37, result = 6
n = 38, result = 6
n = 39, result = 6
n = 40, result = 6
n = 41, result = 6
n = 42, result = 6
n = 43, result = 6
n = 44, result = 6
n = 45, result = 6
n = 46, result = 6
n = 47, result = 6
n = 48, result = 6  // 13个6
n = 49, result = 7
```


#### 代码
```java

```

#### 结果
![bulb-switcher-3](/images/leetcode/bulb-switcher-3.png)

### 是开着的灯泡的位置，都是整数的平方
再观察上面的结果，亮着的灯泡的位置：`1,4,9,16,25...`。所以代码就变的非常简单。

为什么呢？

顺着下面这个思路往下想，
> `n`被小于它的数整除一次，开关就拨动一次。

所以，
> 亮着的灯泡，都被拨动了 **奇数** 次。

也就是，
> 灯泡亮着的`n`位，要求能被 **奇数个自然数整除**。

怎么才能被奇数个自然数整除呢？
> 普通的整除，都会带来成对的除数，比如`12=1*12`,`12=2*6`,`12=3*4`。只有平方带来两个相等的除数，`4*4=16`,`5*5=25`。

参考Stefan的帖子，<https://discuss.leetcode.com/topic/31929/math-solution?page=1>

#### 代码
```java
public class Solution {
        public int bulbSwitch(int n) {
            return (int)Math.sqrt(n);
        }
}
```

#### 结果
![bulb-switcher-4](/images/leetcode/bulb-switcher-4.png)
