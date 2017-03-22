---
layout: post
title: "Leetcode - Algorithm - String to Integer"
date: 2017-03-21 22:34:25
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode","string","int"]
level: "medium"
description: >
---

### 题目
Implement atoi to convert a string to an integer.

Hint: Carefully consider all possible input cases. If you want a challenge, please do not see below and ask yourself what are the possible input cases.

Notes: It is intended for this problem to be specified vaguely (ie, no given input specs). You are responsible to gather all the input requirements up front.

#### C语言中对于atoi()函数的约定
这题对对特殊值的处理，沿用C语言中`atoi()`函数的约定。具体约定如下：
```
The function first discards as many whitespace characters as necessary until the first non-whitespace character is found. Then, starting from this character, takes an optional initial plus or minus sign followed by as many numerical digits as possible, and interprets them as a numerical value.

The string can contain additional characters after those that form the integral number, which are ignored and have no effect on the behavior of this function.

If the first sequence of non-whitespace characters in str is not a valid integral number, or if no such sequence exists because either str is empty or it contains only whitespace characters, no conversion is performed.

If no valid conversion could be performed, a zero value is returned. If the correct value is out of the range of representable values, INT_MAX (2147483647) or INT_MIN (-2147483648) is returned.
```

这和`Java`的`Integer.parseInt(int i)`函数的约定完全不同，`Java`中输入不不符合格式，直接抛出`NumberFormatException`，而`C`是直接返回`0`。当大小超出`INT_MAX`和`INT_MIN`时，`Java`也是抛`NumberFormatException`，而`C`直接返回`2147483647`和`-2147483648`。

### 朴素正则表达式匹配后再全手动解析
先用正则表达式`^[+-]?[0-9]+$`把表示数值的部分抠出来。然后手动维护一个`char`和对应`int`的映射表。

#### 代码
```java
import java.util.regex.*;

public class Solution {
    private static final Pattern P = Pattern.compile("^[+-]?[0-9]+$");
    private static final Map<Character,Integer> DICTIONARY = new HashMap<>();
    static {
        DICTIONARY.put('0',0);
        DICTIONARY.put('1',1);
        DICTIONARY.put('2',2);
        DICTIONARY.put('3',3);
        DICTIONARY.put('4',4);
        DICTIONARY.put('5',5);
        DICTIONARY.put('6',6);
        DICTIONARY.put('7',7);
        DICTIONARY.put('8',8);
        DICTIONARY.put('9',9);
    }
    public int myAtoi(String str) {
        if (str == null || str.isEmpty()) { // 格式不对
            throw new NumberFormatException("String cannot be empty!");
        }
        Matcher m = P.matcher(str);
        if (!m.find()) { // 格式不对
            throw new NumberFormatException("Format not accepted!");
        }
        char[] chars = str.toCharArray();
        int head = (chars[0] == '+' || chars[0] == '-')? 1:0;
        int signum = (chars[0] == '-')? -1:1;
        long result = 0l;
        long max = (long)Integer.MAX_VALUE;
        long min = (long)Integer.MIN_VALUE;
        for (int i = head; i < chars.length; i++) {
            result = (result * 10) + (DICTIONARY.get(chars[i]) * signum);
            if (result > 0 && result > max) {
                throw new NumberFormatException("Too big!");
            }
            if (result < 0 && result < min) {
                throw new NumberFormatException("Too small!");
            }
        }
        return (int)result;
    }
}
```

#### 结果
因为很多可以用库完成的转换全手动，还要维护`char`到`int`的映射表，效率不可能太好。
![string-to-integer-1](/images/leetcode/string-to-integer-1.png)

### 利用ASCII码直接转码
`ASCII`码中，`0`的编码是`48`。`0-9`分别对应`48-57`。利用这个可以直接从`char`转码到`int`。就不用维护一个映射表了。

#### 代码

```java
import java.util.regex.*;

public class Solution {
    private static final Pattern P = Pattern.compile("^[\\s]*([+-]?[0-9]+).*$");

    public int myAtoi(String str) {
        if (str == null || str.isEmpty()) { return 0; }
        Matcher m = P.matcher(str);
        if (!m.find()) { return 0; }

        char[] chars = m.group(1).toCharArray();
        int head = (chars[0] == '+' || chars[0] == '-')? 1:0;
        int signum = (chars[0] == '-')? -1:1;
        long result = 0l;
        long max = (long)Integer.MAX_VALUE;
        long min = (long)Integer.MIN_VALUE;
        for (int i = head; i < chars.length; i++) {
            result = (result * 10) + (((int)chars[i]-'0') * signum); // ascii码中 0 = 48
            if (result > 0 && result > max) { return Integer.MAX_VALUE; }
            if (result < 0 && result < min) { return Integer.MIN_VALUE; }
        }
        return (int)result;
    }
}
```

#### 结果
没想到结果反而没有第一种用映射表的块。
![string-to-integer-2](/images/leetcode/string-to-integer-2.png)

### 直接利用Integer.parseInt()
虽然Java的`Integer.parseInt()`和`atoi()`的约定不同。但还是可以用`正则表达式`帮我们过滤掉格式不对的情况。最后`overflow`的情况，用`try-catch`块处理一下就行。

#### 代码

```java
import java.util.regex.*;

public class Solution {
    private static final Pattern P = Pattern.compile("^[\\s]*([+-]?[0-9]+).*$");

    public int myAtoi(String str) {
        if (str == null || str.isEmpty()) { return 0; }
        Matcher m = P.matcher(str);
        if (!m.find()) { return 0; }

        String num = m.group(1);
        int signum = (num.charAt(0) == '-')? -1:1;
        try {
            return Integer.parseInt(num); // 让 Integer.parseInt()替我们工作
        } catch (NumberFormatException e) { // 溢出时Integer.parseInt()会抛出异常，这里额外处理一下
            if (signum == 1) {
                return Integer.MAX_VALUE;
            } else {
                return Integer.MIN_VALUE;
            }
        }
    }
}
```

#### 结果
还是不好。见了鬼了。难道是机器累了吗？
![string-to-integer-3](/images/leetcode/string-to-integer-3.png)
