---
layout: post
title: "[Code Complete] Note: Chapter 16 - Control Flow"
date: 2017-01-28 23:54:16
author: "Wei SHEN"
categories: ["Java","Code_Complete"]
tags: ["Code_Style","Control_Flow"]
description: >
  无脑的才是最好的
---

### 基本原则：简单好懂
#### 每个循环只做一件事
#### 循环越短越好
#### 套嵌越少越好
#### 变量名，下标尽量取好懂的名字
#### 最好用带退出条件的循环
#### 用break,continue减少循环套嵌
#### 由内而外地构建循环

### 选循环
#### 循环分四种
1. 固定次数的循环。典型：`for`
```java
for(i = 0; i < 10; i++) {
    // do something
}
```

2. 退出条件在开头的无限循环。 典型：`while`.
```java
int cursor = 0;
while(cursor++ < 10) {
    // do something    
}
```

3. 退出条件在末尾的无限循环。 典型：`do-while`.
```java
int cursor = 0;
do {
    // do something
} while (cursor++ < 10);
```

3. 带退出条件的循环，退出条件在循环中间。 如下代码所示,
```java
int cursor = 0;
while(true) {
    // do something
    if (cursor++ == 10) {
        break;    
    }
    // do something
}
```

#### 尽量使用最后第四种中间带退出条件的循环
带退出的循环（Loop-with-exit）就是指上述第四种，终止条件出现在循环中间而不是开始或者末尾的循环。

因为带退出的循环可以有效避免 **“一个半循环（loop-and-a-half)”** 。下面代码就是典型的“一个半循环”,
```java
int targetScore = 90;
int score = getNextScore();
while(score < targetScore) {
    score = getNextScore();    
}
```
如果修改成带退出条件的循环，能让循环更加容易理解，条理清晰的"单入单出"结构：
```java
int targetScore = 90;
while(true) {
    score = getNextScore();
    if (score >= 90) { //单一出口
        break;
    }
}
```

这一条和我自己的实践体会一致。外面用一个最无脑的while(true)的无限循环，控制权全部交给循环体里，最好是只有一个单一的出口，这样是最简单易懂的结构。

#### 尽量用while替代for
承接上一条原则，普遍认为把控制权交给循环内部，比在一开始完全决定循环次数，逼近退出条件，要更好。

#### 当循环控制只是简单递增或递减，比如遍历容器元素，优先使用for
实际上，遍历容器元素的情况，Java中更简单的一个语法糖是foreach语法。
```java
for(String str : stringArray) {
    // so something to each string in array
}
```

#### 由内而外构建循环
和所有构建复杂事物的诀窍相同，构建复杂循环，最好从内部简单的核心内容开始。
具体步骤如下：
1. 先从要做的事务核心入手
2. 先用文字表述要做的事情，和要用的数据
3. 慢慢增加细节
4. 最后在外面套上循环
5. 再加上循环外需要声明的数据
6. 循环往复，再扩展到外层循环

例如，先用文字描述要做的事，
```java
// 从表中取得费率
// 将费率加到总和上
```
增加数据细节，
```java
int rate = table[census.age][census.gender];
int totalrate = totalRate + rate;
```
最后套上循环，
```java
for(Census c : censusList) {
    int rate = table[c.getAge()][c.getGender()];
    int totalRate = totalRate + rate;
}
```
在循环外面声明变量，
```java
int totalRate = 0;
for(Census c : censusList) {
    int rate = table[c.getAge()][c.getGender()];
    totalRate += rate;
}
```

### 循环内部
#### 每个循环只做一件事
循环体也要遵循模块化原则。逻辑复杂的多层套嵌也最好分割成独立的多个循环。

#### 循环越短越好
一个简单的标准是：能不能一目了然。屏幕不用翻屏能完整显示循环体。

#### 循环最多三层套嵌
实验显示，超过三层套嵌，程序员对循环体的理解就开始有些吃力。

#### for循环体内部不要操作循环下标
既然用了for循环，就把循环控制权交给头部信息。不要在循环体内部对循环下标做改动。

#### 不要使用空的循环体


### 进入循环
#### 从头进入循环
不要用类似goto语句，从循环体的中间开始执行。这会让循环体的行为变得难以理解。

#### 数据初始化要直接位于循环前面
根据就近原则，循环体内要用到的数据，最好是贴近循环体的地方声明。

#### 为for循环的下标取个好名字
除非是简单的递增或递减迭代，尽量不要用i,j这样简单的字母做下标。

#### 用整数或枚举做下标。不要用浮点数。
浮点数的问题前几章已经说过了。因为小数部分转成二进制，经常是无限小数，四舍五入容易出问题。尽量不要拿来比较大小，判断相等。

#### 下标变量不能在循环外被使用
Java允许在for语句内部声明变量，出了循环体，变量就失效。这点不太用担心。

#### 宁愿花时间搞清楚循环的端点检查
必要的时候拿出笔来计算一下，不要靠猜，或者+1,-1这样去试循环下标的范围。靠猜的话，可能偶然看似符合了要求，其实里面有非常隐蔽的bug。越是明显容易出错的地方，越应该花精力搞清楚，而不是蒙混过关。


### 退出循环
#### 退出条件必须清晰
就算循环体鼓励使用`while(true)`，也就是循环体头部不承担循环控制的责任，也要把循环控制语句放在重要的位置。并且控制语句最好都集中在一起。读起来，写起来思路都比较清晰。

#### 可以用continue,break退出循环
在循环开头使用`if-continue`结构，可以减少套嵌。
```java
int score = 0;
while (true) {
    score = getScore();
    if (score > 100) { // stop condition
        break;
    }
    if (score%2 == 0) { // skip all even
        continue;
    }
    // code to treat odd score
}
```

#### 使用break,continue的时候尽量带标号
Java支持带标号的break,continue语句。
```java
label_1:
for ( out iteration ){
    for ( inner iteration ){
        // ...
        break; // (1)
        // ...
        continue; // (2)
        // ...
        continue label1; // (3)
        // ...
        break label1; // (4)
    }
}
```
(1)处break中断内部迭代，回到外部迭代。
(2)处continue继续内部迭代。
(3)处break中断所有迭代。
(4)处continue跳回到外部迭代。

#### 退出点不宜太多
尽管使用break和continue有助于减少循环套嵌，减少复杂度。但循环的退出点也不宜太多。单入单出这样最简单的模型是追求的目标。
