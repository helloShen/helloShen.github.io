---
layout: post
title: "Leetcode - Algorithm - Fraction to Recurring Decimal "
date: 2017-06-09 21:56:54
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","string","hash table"]
level: "medium"
description: >
---

### 题目
Given two integers representing the numerator and denominator of a fraction, return the fraction in string format.

If the fractional part is repeating, enclose the repeating part in parentheses.

For example,

Given numerator = 1, denominator = 2, return "0.5".
Given numerator = 2, denominator = 1, return "2".
Given numerator = 2, denominator = 3, return "0.(6)".

### 如果再次出现相同的除数，说明开始循环
> 进入到小数部分，当一个`临时除数`第二次出现的时候，就是循环的开始。

`临时除数`就是指每次没有除尽的时候，向后借位之后再次得到的除数。

以`2/3`为例，
```
  0.6666...
  ---------
3|2.0000000
/ -
  2 0           <- 第一次出现临时除数20            -|
  1 8                                            |--> 此间经每一步都会循环出现
  ---                                            |
    20          <- 第二次又出现临时除数20，循环开始  -|
    18
    --
     20
     18
     --
      20
      ...
      ...
```

所以就用一个`Map`记录小数部分用到过的`临时除数`。

两个细节需要注意：
1. 负数的情况
2. `int`会溢出

#### 代码
```java
public class Solution {
    private static final String LQ = "(";
    private static final String RQ = ")";
    private static final String DOT = ".";
    private static final String NEG = "-";
    private static final String ZERO = "0";
    private static final char ZERO_C = '0';
    private static final char DOT_C = '.';
    public String fractionToDecimal(int numerator, int denominator) {
        if (denominator == 0) { return null; }
        if (numerator == 0) { return ZERO; }
        // treat sign here. use long, because can't get abs of -2147483648
        int signum = Integer.signum(numerator) * Integer.signum(denominator);
        long numeratorLong = Math.abs((long)numerator);
        long denominatorLong = Math.abs((long)denominator);
        char[] num = String.valueOf(numeratorLong).toCharArray();
        // division loop
        int cur = 0;
        long remain = 0;
        boolean dot = false;
        StringBuilder sb = new StringBuilder();
        Map<Long,Integer> memo = new HashMap<>();
        while (cur < num.length || remain != 0) {
            // create new sub-numerator
            long subNumerator = remain * 10;
            if (cur < num.length) {
                subNumerator += (num[cur]-ZERO_C);
            } else if (!dot) {
                sb.append(DOT);
                cur++;
                dot = true;
            }
            cur++;
            // record each sub-numerator. (only after the dot .)
            if (dot) {
                Integer pos = memo.get(subNumerator);
                if (pos != null) { // find repeat
                    sb = sb.insert(pos,LQ);
                    sb = sb.append(RQ);
                    break;
                } else {
                    memo.put(subNumerator,cur-1);
                }
            }
            // calculate
            char quotient = (char)((int)(subNumerator / denominatorLong) + ZERO_C);
            sb.append(quotient);
            remain = subNumerator % denominatorLong;
        }
        String res = trimZero(sb.toString());
        return (signum < 0)? NEG + res : res; // give back the sign
    }
    public String trimZero(String s) {
        int cur = 0;
        while (s.charAt(cur) == ZERO_C) {
            cur++;
        }
        s = s.substring(cur);
        if (s.charAt(0) == DOT_C) { s = ZERO + s; }
        return s;
    }
}
```

#### 结果
![fraction-to-recurring-decimal-1](/images/leetcode/fraction-to-recurring-decimal-1.png)


### 和上面相同的解法，分开处理整数部分和小数部分
分开处理`整数部分`和`小数部分`能简化逻辑。

#### 代码
```java
public class Solution {
    public String fractionToDecimal(int numerator, int denominator) {
        if (denominator == 0) { return null; }
        StringBuilder sb = new StringBuilder();
        if ( (numerator < 0 && denominator > 0) || (numerator > 0 && denominator < 0) ) {
            sb.append("-");
        }
        long numeratorLong = Math.abs((long)numerator);
        long denominatorLong = Math.abs((long)denominator);
        // integral part
        long integral = numeratorLong / denominatorLong;
        long remainder = numeratorLong % denominatorLong;
        sb.append(String.valueOf(integral));
        if (remainder == 0) { return sb.toString(); }
        // fractional part
        sb.append(".");
        Map<Long,Integer> memo = new HashMap<>();
        int fractionStart = sb.length();
        memo.put(remainder,fractionStart);
        while (remainder != 0) {
            remainder = remainder * 10;
            int quotient = (int)(remainder / denominatorLong);
            remainder = remainder % denominatorLong;
            Integer pos = memo.get(remainder);
            if (pos != null) { // 出现循环
                sb.insert(pos,"(");
                sb.append(String.valueOf(quotient) + ")");
                return sb.toString(); // 出口1：找到循环
            }
            sb.append(String.valueOf(quotient));
            memo.put(remainder,fractionStart+memo.size());
        }
        return sb.toString(); // 出口2：不循环除尽
    }
}
```

#### 结果
![fraction-to-recurring-decimal-2](/images/leetcode/fraction-to-recurring-decimal-2.png)
