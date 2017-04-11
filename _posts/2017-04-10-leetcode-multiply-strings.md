---
layout: post
title: "Leetcode - Algorithm - Multiply Strings "
date: 2017-04-10 17:48:10
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: "medium"
description: >
---

### 主要收获
证实了`String`的拼接开销很大。就算用了`StringBuilder`也是一样。换做`Arrays#copyOf()`和`Arrays.copyOfRange()`会快很多。这题快了6倍。

### 题目
Given two non-negative integers num1 and num2 represented as strings, return the product of num1 and num2.

Note:
```
* The length of both num1 and num2 is < 110.
* Both num1 and num2 contains only digits 0-9.
* Both num1 and num2 does not contain any leading zero.
* You must not use any built-in BigInteger library or convert the inputs to integer directly.
```

### `0~9`的乘法，然后进位
加法和乘法都是一个模式，`1024 * 25`，每一位都拎出来乘。每次都是两个`0~9`的数字相乘，每次都记上进位。
```
00120    //进位
 1024
    5    *
----------
 5120
```
加法也是一样，
```
110    //进位
 99
 99    +
--------
198
```

#### 代码
```java
public class Solution {
    public String multiply(String num1, String num2) {
        String res = "0";
        for (int i = num1.length()-1; i >= 0; i--) {
            int multi1 = num1.charAt(i) - '0';
            StringBuilder sb = new StringBuilder();
            int carry = 0;
            for (int j = num2.length()-1; j >= 0; j--) {
                int multi2 = num2.charAt(j) - '0';
                int product = multi1 * multi2 + carry;
                sb.insert(0,product % 10);
                carry = product / 10;
            }
            if (carry > 0) { sb.insert(0,carry); }
            if (sb.charAt(0) != '0') {
                for (int k = 0; k < num1.length()-1-i; k++) {
                    sb.append("0");
                }
                res = plus(res,sb.toString());
            }
        }
        return res;
    }
    public String plus(String first, String second) {
        StringBuilder sb = new StringBuilder();
        int carry = 0;
        for (int i = first.length() - 1, j = second.length() - 1; i >= 0 || j >= 0; i--,j--) {
            int num1 = 0, num2 = 0;
            if (i >= 0) { num1 = first.charAt(i) - '0'; }
            if (j >= 0) { num2 = second.charAt(j) - '0'; }
            int sum = num1 + num2 + carry;
            sb.insert(0,(sum%10));
            carry = sum / 10;
        }
        if (carry == 1) { sb.insert(0,"1"); }
        System.out.println(first + " + " + second + " = " + sb.toString());
        return sb.toString();
    }
}
```

#### 结果
对是对，但不够好，算法层面没找到好方法。银弹的速度是我的5，6倍。
![multiply-strings-1](/images/leetcode/multiply-strings-1.png)


### 缓存`9*9`乘法表
想法是用二维数组缓存`9*9`乘法表，用`table[num1][num2]`数组随机访问来代替`num1 * num2`的乘法操作。

#### 代码
```java
public class Solution {
    int[][] table = new int[][] { // 9*9乘法表
        {0,0,0,0,0,0,0,0,0,0},
        {0,1,2,3,4,5,6,7,8,9},
        {0,2,4,6,8,10,12,14,16,18},
        {0,3,6,9,12,15,18,21,24,27},
        {0,4,8,12,16,20,24,28,32,36},
        {0,5,10,15,20,25,30,35,40,45},
        {0,6,12,18,24,30,36,42,48,54},
        {0,7,14,21,28,35,42,49,56,63},
        {0,8,16,24,32,40,48,56,64,72},
        {0,9,18,27,36,45,54,63,72,81}
    };
    public String multiply(String num1, String num2) {
        String res = "0";
        for (int i = num1.length()-1; i >= 0; i--) {
            int multi1 = num1.charAt(i) - '0';
            StringBuilder sb = new StringBuilder();
            int carry = 0;
            for (int j = num2.length()-1; j >= 0; j--) {
                int multi2 = num2.charAt(j) - '0';
                int product = table[multi1][multi2]+ carry;
                sb.insert(0,product % 10);
                carry = product / 10;
            }
            if (carry > 0) { sb.insert(0,carry); }
            if (sb.charAt(0) != '0') {
                for (int k = 0; k < num1.length()-1-i; k++) {
                    sb.append("0");
                }
                res = plus(res,sb.toString());
            }
        }
        return res;
    }
    public String plus(String first, String second) {
        StringBuilder sb = new StringBuilder();
        int carry = 0;
        for (int i = first.length() - 1, j = second.length() - 1; i >= 0 || j >= 0; i--,j--) {
            int num1 = 0, num2 = 0;
            if (i >= 0) { num1 = first.charAt(i) - '0'; }
            if (j >= 0) { num2 = second.charAt(j) - '0'; }
            int sum = num1 + num2 + carry;
            sb.insert(0,(sum%10));
            carry = sum / 10;
        }
        if (carry == 1) { sb.insert(0,"1"); }
        System.out.println(first + " + " + second + " = " + sb.toString());
        return sb.toString();
    }
}
```

#### 结果
没什么效果，看来问题不在这儿。
![multiply-strings-2](/images/leetcode/multiply-strings-2.png)


### 不用StringBuilder拼接，直接操作`char[]`，快了6倍
接下来，猜测会不会是`String`操作，以及`StringBuilder`的拼接开销太大。毕竟刚开始使用`StringBuilder`拼接，是为了让代码保持简洁，自己也更专注于算法逻辑。

所以，下面的代码，一上来就把`String`转成`char[]`，后面都直接操作`char[]`，参数也直接传`char[]`。

#### 代码
```java
public class Solution {
    public String multiply(String num1, String num2) {
        char[] chars1 = num1.toCharArray();
        char[] chars2 = num2.toCharArray();
        char[] res = new char[]{'0'};
        for (int i = chars2.length-1; i >= 0; i--) {
            char[] temp = multi(chars1,chars2[i],chars2.length-1-i);
            res = plus(res,temp);
        }
        return new String(res);
    }
    public char[] multi(char[] num1, char num2, int zero) {
        if (num1[0] == '0' || num2 == '0') { return new char[]{'0'}; }
        char[] res = new char[num1.length+1+zero];
        for (int i = 0; i < zero; i++) {
            res[num1.length+1+i] = '0';
        }
        int carry = 0;
        int int2 = num2 - '0';
        for (int i = num1.length-1; i >=0; i--) {
            int product = (num1[i]-'0') * int2 + carry;
            res[i+1] = (char)((product % 10) + '0');
            carry = product / 10;
        }
        res[0] = (char)(carry + '0');
        return (carry == 0)? Arrays.copyOfRange(res,1,res.length):res;
    }
    public char[] plus(char[] first, char[] second) {
        int length = Math.max(first.length, second.length);
        char[] res = new char[length+1];
        int carry = 0;
        for (int i = first.length - 1, j = second.length - 1; i >= 0 || j >= 0; i--,j--) {
            int num1 = 0, num2 = 0;
            if (i >= 0) { num1 = (first[i] - '0'); }
            if (j >= 0) { num2 = (second[j] - '0'); }
            int sum = num1 + num2 + carry;
            res[length--] = (char)((sum % 10) + '0');
            carry = sum / 10;
        }
        res[0] = (char)(carry + '0');
        return (carry == 0)? Arrays.copyOfRange(res,1,res.length):res;
    }
}
```

#### 结果
足足快了6倍。证明`StringBuilder`的拼接，以及和`String`之间的转换开销非常大。并不一定说遇到`String`就要用`char[]`代替，但 **遇到要拼接字符串，用`Arrays#copyOf()`和`Arrays#copyOfRange()`会快很多。**
![multiply-strings-3](/images/leetcode/multiply-strings-3.png)

### 数学过程更好的抽象
`i`位数和`j`位数相乘，最多只有`i+j`位数。如下图所示，乘法过程可以抽象成下面这个在`i+j`位上累加的过程，
![multiply-strings-math](/images/leetcode/multiply-strings-math.jpg)

这样，**可以更好地把操作集中到一个`char[]`上。**

#### 代码

```java
public class Solution {
    public String multiply(String num1, String num2) {
        char[] c1 = num1.toCharArray();
        char[] c2 = num2.toCharArray();
        if (c1[0] == '0' || c2[0] == '0') { return "0"; }
        int[] result = new int[c1.length+c2.length];
        for (int i = 0; i < c1.length; i++) {
            for (int j = 0; j < c2.length; j++) {
                int product = (c1[i] - '0') * (c2[j] - '0');
                result[i+j] += product / 10;
                result[i+j+1] += product % 10;
            }
        }
        int carry = 0;
        for (int i = result.length-1; i >= 0; i--) {
            int val = result[i] + carry;
            result[i] = val % 10;
            carry = val / 10;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = (result[0] == 0)? 1:0; i < result.length; i++) {
            sb.append(result[i]);
        }
        return sb.toString();
    }
}
```

#### 结果
真银弹！
![multiply-strings-4](/images/leetcode/multiply-strings-4.png)
