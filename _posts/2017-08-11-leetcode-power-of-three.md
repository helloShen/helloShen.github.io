---
layout: post
title: "Leetcode - Algorithm - Power Of Three "
date: 2017-08-11 17:40:20
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "easy"
description: >
---

### 题目
Given an integer, write a function to determine if it is a power of three.

Follow up:
Could you do it without using any loop / recursion?

### 朴素迭代除法

#### 代码
```java
public class Solution {
    public boolean isPowerOfThree(int n) {
        if (n == 0) { return false; }
        while (n % 3 == 0) { n /= 3; }
        return n == 1;
    }
}
```

#### 结果
![power-of-three-1](/images/leetcode/power-of-three-1.png)


### 递归除法

#### 代码
```java
public class Solution {
    public boolean isPowerOfThree(int n) {
        if (n == 1) {
            return true;
        } else if (n == 0 || n % 3 != 0) {
            return false;
        } else {
            return isPowerOfThree(n / 3);
        }
    }
}
```

#### 结果
![power-of-three-2](/images/leetcode/power-of-three-2.png)


### 不循环也不迭代，$$O(1)$$ 计算出结果
$$3^{19} = 1162261467$$，是最大的`int`范围内的power of three. 它能切仅能整除所有power of three。 服！

#### 代码
```java
public class Solution {
    public boolean isPowerOfThree(int n) {
        return n > 0 && 1162261467 % n==0;
    }
}
```

#### 结果
![power-of-three-3](/images/leetcode/power-of-three-3.png)

### 用Log
`log10(n) / log10(3)`如果返回整数，就是`3`的整数次方。但`243`会是个例外。因为`double`和`float`的精确度问题，会出现一定的误差。

```
log(243) = 5.493061443340548    log(3) = 1.0986122886681098
   ==> log(243)/log(3) = 4.999999999999999

log10(243) = 2.385606273598312    log10(3) = 0.47712125471966244
   ==> log10(243)/log10(3) = 5.0
```
