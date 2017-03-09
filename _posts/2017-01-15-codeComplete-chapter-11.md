---
layout: post
title: "[Code Complete] Note: Chapter 11 - Variable Naming Rules"
date: 2017-01-02 23:54:16
author: "Wei SHEN"
categories: ["java","code complete"]
tags: ["code style"]
description: >
---

### 前言
必须记住变量命名两大原则：可读性，一致性。

### 两大基本原则

#### 可读性
1. **好懂。** 一看到名字就知道变量代表什么，不需要解释。(P260)
2. **长度控制在5-20个字母最佳。** （P262)
    1. **全局变量最好加限定词避免重复。** 原则是越局部性的变量越短，越全局越长。

#### 一致性
3. **我用驼峰式命名。** (theName)。
4. **类名首字母大写。** (ClassName)  
5. **对象名首字母小写。** (objectName)
6. **常量全大写，加下划线。** (CONSTANT_VALUE)
6. **修饰性形容词都放在中心词后面。** (P263)
7. **对仗。** (P264)
    1. begin/end
    2. first/last
    3. locked/unlocked
    4. min/max
    5. next/previous
    6. old/new
    7. opened/closed
    8. visible/invisible
    9. source/target
    10. source/destination
    11. up/down

### 具体命名规则
Java具体命名规则参见下图：

![valueName](../uploads/codeComplete/valueName.jpg)

还有Google Java 编程风格指南：http://www.hawstein.com/posts/google-java-style.html

### 特殊变量

#### 循环下标
8. **简单循环约定俗成i,j,k。** 复杂循环可以考虑取特殊名字。

#### 状态标记
9. **表示有限状态用枚举型，或者具名常量。** 不要用flag，index这种词草草了事。 (P266)

#### 临时变量
10. **临时变量也好好取名。** 而已短一点，但不要用temp这种词敷衍。

#### 布尔型
11. 几个经典布尔型的名字：**done？ error？ found？ success？** 这样能一看就能表示真假的词。
    1. 也可以加is前缀。isDone? isError? isFound? isSuccess?

#### 枚举
12. **我枚举型对象，全大写，加下划线组合。** ENUM_OBJECT
13. **枚举对象可以用当前枚举型的类名做前缀。** 比如Enum Color的枚举就可以叫：COLOR_RED, COLOR_YELLOW.

#### 常量
14. **全大写，下划线。** CONSTANT_VALUE.
