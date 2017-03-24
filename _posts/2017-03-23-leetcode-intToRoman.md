---
layout: post
title: "Leetcode - Algorithm - Integer to Roman"
date: 2017-03-23 20:00:36
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode","integer"]
level: "medium"
description: >
---

### 题目
Given an integer, convert it to a roman numeral.

Input is guaranteed to be within the range from 1 to 3999.

![int-to-roman-0](/images/leetcode/int-to-roman-0.png)
罗马数字中只有`1`,`5`,`10`,`50`,`100`,`500`,`1000`...这几个数字，有专门的字符表示，其他所有数字都是这几个字符的简单重复。具体的例子见下图。
![roman-number](/images/leetcode/roman-number.jpg)
规则中比较特殊的，就是遇到`5`,`10`之前的一位，`4`和`9`是在左边加一位表示减法。`4`写成`IV`(5-1)，`9`写成`IX`(10-1)。

### 二维数组记录所有
罗马数字的堆砌，是纯粹的加法，比如`327 = 300 + 20 + 7`，只要写出`300`,`20`,`7`三个数字，简单拼在一起就可以了。
```
300 = CCC
20 = XX
7 = VII

327 = CCCXXVII
```
最简单的方法，就是列出下面这个表，
```
["","I","II","III","IV","V","VI","VII","VIII","IX"] // 0,1,2,3,...,9
["","X","XX","XXX","XL","L","LX","LXX","LXXX","XC"] // 0,10,20,30,...,90
["","C","CC","CCC","CD","D","DC","DCC","DCCC","CM"] // 0,100,200,300,...,900
["","M","MM","MMM",null,null,null,null,null,null] // 0,1000,2000,3000
```

#### 代码
```java
public class Solution {
    private static String[][] SYMARRAY = new String[][] {
        {"","I","II","III","IV","V","VI","VII","VIII","IX"}, // 0,1,2,3,...,9
        {"","X","XX","XXX","XL","L","LX","LXX","LXXX","XC"}, // 0,10,20,30,...,90
        {"","C","CC","CCC","CD","D","DC","DCC","DCCC","CM"}, // 0,100,200,300,...,900
        {"","M","MM","MMM",null,null,null,null,null,null} // 0,1000,2000,3000
    };
    public String intToRoman(int num) {
        int carry = 0;
        StringBuilder sb = new StringBuilder();
        while (num > 0) { // 327 = [7,2,3,0]
            sb.insert(0,SYMARRAY[carry++][num%10]);
            num = num/10;
        }
        return sb.toString();
    }
}
```

#### 结果
效率还不错。缺点是，如果要表示很大的数字，这个表就会很长。
![int-to-roman-1](/images/leetcode/int-to-roman-1.png)

### 写一套规则，自动拼装
更普适的方法，只记录下`I`,`V`,`X`这样的符号，根据规则生成罗马数字。因为罗马数字无论是个位，十位，还是百位，从`0-9`的规则都是一样的，都是`4`和`9`去左边。所以可以只写一套规则，然后传递不同的符号即可。

#### 代码
```java
public class Solution {
    private static final char[][] SYM = new char[][] {
        {'I','V','X'},
        {'X','L','C'},
        {'C','D','M'},
        {'M',Character.MIN_VALUE,Character.MIN_VALUE}
    };
    public String intToRoman(int num) {
        int carry = 0;
        StringBuilder sb = new StringBuilder();
        while (num > 0) { // 327 = [7,2,3,0]
            generate(num%10,SYM[carry++],sb);
            num = num/10;
        }
        return sb.toString();
    }
    public void generate(int num, char[] symbol, StringBuilder sb) { // num = [0...9]
        StringBuilder temp = new StringBuilder();
        if (num < 4) {
            for (int i = 0; i < num; i++) {
                temp = temp.append(symbol[0]);
            }
        } else if (num == 4) {
            temp = temp.append(symbol[0]).append(symbol[1]);
        } else if (num < 9) {
            temp = temp.append(symbol[1]);
            for (int i = 0; i< num-5; i++) {
                temp = temp.append(symbol[0]);
            }
        } else if (num == 9) {
            temp = temp.append(symbol[0]).append(symbol[2]);
        } else {
            throw new NumberFormatException();
        }
        sb = sb.insert(0,temp);
    }
}
```

#### 结果
因为不是直接查表，速度慢了点，但普适用性更好。
![int-to-roman-2](/images/leetcode/int-to-roman-2.png)

### 最简短的查表法
最后这段是完全为了这道题写的代码。也是查表，但只能处理最多四位数。因为完全没有多余动作，所以是最快的。

#### 代码
```java
public class Solution {
    private static String[][] SYMARRAY = new String[][] {
        {"","I","II","III","IV","V","VI","VII","VIII","IX"}, // 0,1,2,3,...,9
        {"","X","XX","XXX","XL","L","LX","LXX","LXXX","XC"}, // 0,10,20,30,...,90
        {"","C","CC","CCC","CD","D","DC","DCC","DCCC","CM"}, // 0,100,200,300,...,900
        {"","M","MM","MMM",null,null,null,null,null,null} // 0,1000,2000,3000
    };
    public String intToRoman(int num) {
        return SYMARRAY[3][num/1000] + SYMARRAY[2][num%1000/100] + SYMARRAY[1][num%100/10] + SYMARRAY[0][num%10];
    }
}
```

#### 结果
![int-to-roman-3](/images/leetcode/int-to-roman-3.png)
