---
layout: post
title: "[Code Complete] Note: Chapter 15 - case"
date: 2017-01-28 18:54:16
author: "Wei SHEN"
categories: ["Java","Code_Complete"]
tags: ["Code_Style","Control_Flow"]
description: >
---

### if 条件判断语句
#### 主次原则：主要分支在前，次要分支在后
1. `true`的分支在前，`false`的分支在后。
2. 概率高的在前面，概率小的在后面。

#### 使用if-elseif语句，用最后的else报告异常
除非是非常有把握，否则把最后的else留作报告异常最后一道屏障，确保所有情况都被考虑到了。
```java
if (condition1) {

} else if (condition2) {

} else if (condition3) {

} else {
    throw new IllegalStateException("Not in three principle conditions!")
}
```

### switch case 判断语句
#### 和if一样，也是主次原则：主要分支在前，次要分支在后
1. 按字母顺序排列。
2. 概率高的在前面，概率小的在后面。
3. 正常的分支在前，异常分支在后。

#### 不要为了用case刻意制造虚假变量，
下面这样的刻意制造虚假变量，很容易引起bug。因为对用户输入的命令字符串完全无法预期，下面这种截取首字母来switch的做法，很容易产生异常。
```java
String userCommand = args[0];
char[] charArray = userCommand.toCharArray();
char action = charArray[0];
switch (action) {
    case 'c':
        copy(); break;
    case 'd':
        delete(); break;
    case 'f':
        format(); break;
    case 'h':
        help(); break;
}
```
#### 尽量使用枚举型
枚举型是最适合做为switch的case使用的。Java在这方面的支持非常好。

#### 留下default字句来检测错误
```java
int[] nums = new int[]{1,2,3};
switch (nums[0]) {
    case 1:
        System.out.println("One");break;
    case 2:
        System.out.println("Two");break;
    case 3:
        System.out.println("Three");break;
    default:
        throw new IllegalStateException("Number is not in 1,2,3!");
}
```
#### 确保每个case子句都有break跳出
如果实在需要越过一条子句的末尾，必须用注释解释清楚为什么必须这么做。

#### 复杂的case判断，直接用if-else语句
毕竟还是`if-else`语句的判断功能更加强大。当switch判断条件过于复杂的情况下，宁愿牺牲一些效率，用`if-else`语句把分支表述地更清楚一点。毕竟易读性和可维护性经常还是高于效率的。
