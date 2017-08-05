---
layout: post
title: "Leetcode - Algorithm - Fizz Buzz "
date: 2017-08-05 17:27:46
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
Write a program that outputs the string representation of numbers from 1 to n.

But for multiples of three it should output “Fizz” instead of the number and for the multiples of five output “Buzz”. For numbers which are multiples of both three and five output “FizzBuzz”.

Example:

n = 15,

Return:
```
[
    "1",
    "2",
    "Fizz",
    "4",
    "Buzz",
    "Fizz",
    "7",
    "8",
    "Fizz",
    "Buzz",
    "11",
    "Fizz",
    "13",
    "14",
    "FizzBuzz"
]
```

### 用`%`求余

#### 代码
```java
public class Solution {
    private final String FIZZ = "Fizz";
    private final String BUZZ = "Buzz";
    private final String FIZZ_BUZZ = "FizzBuzz";

    public List<String> fizzBuzz(int n) {
        List<String> res = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            if (i % 15 == 0) {
                res.add(FIZZ_BUZZ);
            } else if (i % 3 == 0) {
                res.add(FIZZ);
            } else if (i % 5 == 0) {
                res.add(BUZZ);
            } else {
                res.add("" + i);
            }
        }
        return res;
    }
}
```

#### 结果
![fizz-buzz-1](/images/leetcode/fizz-buzz-1.png)


### 用两个计步器替代`%`求余
用两个计步器每次做自增运算。用很少的几次自增运算替代了求余数计算。

#### 代码
```java
public class Solution {
    private final String FIZZ = "Fizz";
    private final String BUZZ = "Buzz";
    private final String FIZZ_BUZZ = "FizzBuzz";

    public List<String> fizzBuzz(int n) {
        List<String> res = new ArrayList<>();
        int f = 0, b = 0;
        for (int i = 1; i <= n; i++) {
            ++f; ++b;
            if (f == 3 && b == 5) {
                res.add(FIZZ_BUZZ);
                f = 0; b = 0;
            } else if (f == 3) {
                res.add(FIZZ);
                f = 0;
            } else if (b == 5) {
                res.add(BUZZ);
                b = 0;
            } else {
                res.add("" + i);
            }
        }
        return res;
    }
}
```

#### 结果
![fizz-buzz-2](/images/leetcode/fizz-buzz-2.png)
