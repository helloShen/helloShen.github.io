---
layout: post
title: "[Effective Java] Note: - Chapter-4-4：Static Inner Class First"
date: 2017-02-10
author: "Wei SHEN"
categories: ["java","effective java"]
tags: ["inner class"]
description: >
  静态成员类比非静态成员类好。非静态成员类适合提供外围类的另外一个视图。匿名内部类适合动态地创建只用一次的“函数对象”。
---

### 静态成员类比非静态成员类好
因为省下了指向外围类实例的一个引用`outer.this`。静态成员类就可以当一个普通的类来对待，只是碰巧声明在另一个类的内部而已。

### 非静态成员类的优势是可以访问外围类实例
非静态成员类，就是平时说的 **“内部类”**。因为有一个指向外围类实例的一个引用`outer.this`，因此可以用来提供外围类不同类型的 **“视图”**。 典型的例子就是`iterator()`方法返回的`Iterator`其实就是在外围类内部对象上开了一个小视窗。 `Map#entrySet()`方法返回了外围类`Map`的一个`Set`视图。

### 匿名内部类有诸多限制，适合用来创建函数对象
匿名内部类没有名字，因此除了在声明它的地方之外，无法实例化。而且不能用`instanceof`判断。也不能实现多个接口，或者扩展一个类。所以它常见的用法是动态地创建像`Comparator`这样的函数对象。
