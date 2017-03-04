---
layout: post
title: "[Code Complete] Note: Chapter 14 - Organizing Straight-Line Code"
date: 2017-01-28 15:54:16
author: "Wei SHEN"
categories: ["Java","Code_Complete"]
tags: ["Code_Style","Control_Flow"]
description: >
---

### 大原则不变：可读性，可维护性
这章讲的直线型代码，就是指没有循环跳转功能的，直接按照先后顺序的放置的语句。虽然是个相对简单的任务，但我们一直秉持的可读性，可维护性的原则是不变的。

### 必须有明确顺序的代码
必须有明确顺序的代码，是指前后有依赖关系，必须以某一特定顺序摆放的语句。比如，下面的代码片段，必须先获取原始数据，然后再计算出结果，最后才能打印。
```java
int data = getData();
int result = calculateResult(data);
System.out.println(result);
```
这些语句，虽然顺序上不能动，但在可读性上还是可以下一点功夫的。

#### 子程序名要凸显依赖关系
如果某些子程序依赖前一个子程序操作的结果，那就给前一个子程序取一个特殊的名字，让人一目了然，比如，加一个`init`前缀或后缀。比如下面代码片段，computeMarketingExpense()其实还承担了初始化数据结构的作用，
```java
List<Integer> expense = computeMarketingExpense();
computeSalesExpense(expense);
computeTravelExpense(expense);
computePersonnelExpense(expense);
```
所以在名称上凸显出后面三个方法依赖于第一个方法的初始化工作的结果，加一个`init`后缀，使程序可读性更高。
```java
List<Integer> expense = expenseInit();
computeSalesExpense(expense);
computeTravelExpense(expense);
computePersonnelExpense(expense);
```

#### 利用子程序参数明确依赖关系
下面三个子程序明确表示，前两个是用来获得一个点的x轴y轴坐标，最后一个函数需要利用前两个参数获得的坐标来创建一个新的点。
```java
int xValue = getX();
int yValue = getY();
Pair<Integer,Integer> point = createPoint(xValue, yValue);
```

#### 也可以添加注释，阐明依赖关系
显式地添加注释应该是最简单的办法了。

#### 利用断言，或者异常处理来检查依赖关系是否被遵守
比如可以在类的成员字段里加一个boolean来表明这个对象有没有被完整地初始化。
```java
public class Expense {
    private boolean initialization = false;
    private Employee;
    private Position;
    private Date;
    private int salesExpense;
    private int travelExpense;
    private int personExpense;
    public boolean isInitialized() {
        return initialization;
    }
}
```
之后每个依赖于初始化工作的函数，都可以对这个boolean进行检查，来确保对象的完整性。
```java
computeSalesExpense(Expense expense) {
    if (! expense isInitialized()) {
        throw new IllegalArgumentException("Expense was not fully initialized!");
    }
    // ... ...
}
```
