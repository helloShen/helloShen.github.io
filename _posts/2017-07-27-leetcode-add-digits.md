---
layout: post
title: "Leetcode - Algorithm - Add Digits "
date: 2017-07-27 15:32:32
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
Given a non-negative integer num, repeatedly add all its digits until the result has only one digit.

For example:

Given num = `38`, the process is like: `3 + 8 = 11`, `1 + 1 = 2`. Since `2` has only one digit, return it.

Follow up:
Could you do it without any loop/recursion in O(1) runtime?

### 老老实实一位一位切下来相加

#### 迭代版
```java
public class Solution {
    public int addDigits(int num) {
        while (num >= 10) {
            int sum = 0;
            while (num > 0) {
                sum += (num % 10);
                num /= 10;
            }
            num = sum;
        }
        return num;
    }
}
```

#### 递归版
```java
public class Solution {
    public int addDigits(int num) {
        int sum = 0;
        while (num > 0) {
            sum += (num % 10);
            num /= 10;
        }
        return (sum >= 10)? addDigits(sum) : sum;
    }
}
```

#### 结果
![add-digits-1](/images/leetcode/add-digits-1.png)


### 黑魔法
如果把`1-50`的所有结果都打出来，就找到规律了。
```bash
0 -> 0
1 -> 1
2 -> 2
3 -> 3
4 -> 4
5 -> 5
6 -> 6
7 -> 7
8 -> 8
9 -> 9
10 -> 1
11 -> 2
12 -> 3
13 -> 4
14 -> 5
15 -> 6
16 -> 7
17 -> 8
18 -> 9
19 -> 1
20 -> 2
21 -> 3
22 -> 4
23 -> 5
24 -> 6
25 -> 7
26 -> 8
27 -> 9
28 -> 1
29 -> 2
30 -> 3
31 -> 4
32 -> 5
33 -> 6
34 -> 7
35 -> 8
36 -> 9
37 -> 1
38 -> 2
39 -> 3
40 -> 4
41 -> 5
42 -> 6
43 -> 7
44 -> 8
45 -> 9
46 -> 1
47 -> 2
48 -> 3
49 -> 4
50 -> 5
```

就是正常的`1-9`的循环，只不过没有`0`（除了第一个`0`）。所以做一个对`9`取模运算就可以了。

#### 代码
```java
public class Solution {
    public int addDigits(int num) {
        if (num == 0) { return num; }
        int res = num % 9;
        return (res == 0)? 9 : res;
    }
}
```

#### 结果
![add-digits-2](/images/leetcode/add-digits-2.png)
