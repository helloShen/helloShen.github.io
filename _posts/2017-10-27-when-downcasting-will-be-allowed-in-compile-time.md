---
layout: post
title: "When Downcasting will be allowed in compile time?"
date: 2017-10-27 20:38:17
author: "Wei SHEN"
categories: ["java"]
tags: ["downcasting"]
description: >
---

### 什么时候向下转型会被允许？
From Stackoverflow -> <https://stackoverflow.com/questions/380813/downcasting-in-java>

当某个向下转型在运行时 **“有可能成功”** 的时候，编译器会允许这个转型语句存在，
```java
Object o = getSomeObject();
String s = (String) o; // this is allowed because o could reference a String
```
哪怕有很大概率失败，但只要可能成功，编译器就放行。如果真的不行，运行时会报错，比如，
```java
Object o = new Object();
String s = (String) o; // this will fail at runtime, because o doesn't reference a String
```
当然，如果编译器能判定某些向下转型肯定能成功，当然更好，坑定放行，比如，
```java
Object o = "a String";
String s = (String) o; // this will work, since o references a String
```
编译器如果不允许某个向下转型，那么就是说编译器已经通过一些规则已经能确定它 **“不可能成功”**，比如，
```java
Integer i = getSomeInteger();
String s = (String) i; // the compiler will not allow this, since i can never reference a String.
```
