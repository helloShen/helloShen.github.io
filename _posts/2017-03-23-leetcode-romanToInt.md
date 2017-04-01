---
layout: post
title: "Leetcode - Algorithm - Roman to Integer"
date: 2017-03-23 20:01:01
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["string","math"]
level: "easy"
description: >
---

### 题目
Given a roman numeral, convert it to an integer.

Input is guaranteed to be within the range from 1 to 3999.

### 查表法

#### 代码
```java
public class Solution {
    private static final Map<String,Integer> DIC = new HashMap<>();
    static {
        DIC.put("",0);
        DIC.put("I",1);
        DIC.put("II",2);
        DIC.put("III",3);
        DIC.put("IV",4);
        DIC.put("V",5);
        DIC.put("VI",6);
        DIC.put("VII",7);
        DIC.put("VIII",8);
        DIC.put("IX",9);
        DIC.put("X",10);
        DIC.put("XX",20);
        DIC.put("XXX",30);
        DIC.put("XL",40);
        DIC.put("L",50);
        DIC.put("LX",60);
        DIC.put("LXX",70);
        DIC.put("LXXX",80);
        DIC.put("XC",90);
        DIC.put("C",100);
        DIC.put("CC",200);
        DIC.put("CCC",300);
        DIC.put("CD",400);
        DIC.put("D",500);
        DIC.put("DC",600);
        DIC.put("DCC",700);
        DIC.put("DCCC",800);
        DIC.put("CM",900);
        DIC.put("M",1000);
        DIC.put("MM",2000);
        DIC.put("MMM",3000);
    }

    public int romanToInt(String s) {
        int length = s.length();
        int high = 0, result = 0;
        while (high < length) {
            int low = length;
            while (low > high) {
                Integer num = DIC.get(s.substring(high,low--));
                if (num != null) {
                    result += num;
                    high = low+1;
                    break;
                }
            }
        }
        return result;
    }
}
```

#### 结果
查表法速度还不错。
![roman-to-int-1](/images/leetcode/roman-to-int-1.png)

### 漂亮的搜索法
因为罗马字符是原始的把所有数字都加起来来计数的，`327 = 300 + 20 + 7`，
```
300 = CCC
20 = XX
7 = VII

327 = CCCXXVII
```
所以，每出现一个`I`就代表往结果上累计了`1`, 一个`X`就说明往结果上加`10`。所以，对于前面的例子`327`，我们只要把`CCCXXVII`每个符号对应的数字加起来就得到了结果：
```
CCC = 100 + 100 + 100 = 300
XX = 10 + 10 = 20
VII = 5 + 1 + 1 = 7

327 = 300 + 20 + 7
```
唯一需要注意的是`4 = IV`和`9 = IX`这样的特例。但没出现一个`IV`我们原先计入了`1+5=6`，但实际表示`4`，所以只要减去`6-4=2`即可。相应的`90`，`XC`只需要从`110`中减去`20`。

#### 代码
```java
public class Solution {
    public int romanToInt(String s) {
        char c[]=s.toCharArray();
        int sum=0;
        for(int count = 0; count <= s.length()-1; count++){
           if(c[count]=='M') sum+=1000;
           if(c[count]=='D') sum+=500;
           if(c[count]=='C') sum+=100;
           if(c[count]=='L') sum+=50;
           if(c[count]=='X') sum+=10;
           if(c[count]=='V') sum+=5;
           if(c[count]=='I') sum+=1;
        }
        if(s.indexOf("IV")!=-1){sum-=2;}
        if(s.indexOf("IX")!=-1){sum-=2;}
        if(s.indexOf("XL")!=-1){sum-=20;}
        if(s.indexOf("XC")!=-1){sum-=20;}
        if(s.indexOf("CD")!=-1){sum-=200;}
        if(s.indexOf("CM")!=-1){sum-=200;}
        return sum;
   }
}
```

#### 结果
算法很讨巧，但没有查表法来得直接和有效。
![roman-to-int-2](/images/leetcode/roman-to-int-2.png)
