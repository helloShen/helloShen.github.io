---
layout: post
title: "Builder Pattern"
date: 2017-02-14
author: "Wei SHEN"
categories: ["java","design pattern"]
tags: ["builder pattern"]
description: >
---

### Builder模式
Builder模式用大白话讲就是：**我有一个东西构造起来很复杂，那我就定义一个工厂类，专门负责构建这个复杂对象**。

用一个专门的工厂类来负责构建某类对象，意义有两个：
1. 对象很复杂，构造函数参数太多。工厂类可以让构造代码更清晰。
2. 目标对象必须是不可变对象，必须一次性为所有域赋值。在所有域没有全部就绪的情况下，我的可变工厂类可以一点点将对象构造起来。最后一次性构造一个目标对象。

比如下面的例子，`Builder<T>`泛型接口很简单，只定义了一个能返回泛型类型参数`T`型的对象引用。
```java
interface Builder<T> {
    public T build();
}
```

然后下面的代码，在不可变的`ImmutableClass`类的内部套嵌着一个可变的配套工厂类`ImmuBuilder`。
```java
final class ImmutableClass {
    private final int a;
    private final int b;
    private final int c;
    private final int d;
    private final int e;
    private ImmutableClass(ImmuBuilder b){
        a = b.a;
        b = b.b;
        c = b.c;
        d = b.d;
        e = b.e;
    }
    static final class ImmuBuilder implements Builder<ImmutableClass> {
        private final int a;
        private final int b;
        private final int c;
        private final int d;
        private final int e;
        public int setA(int num) { a = num; }
        public int setB(int num) { b = num; }
        public int setC(int num) { c = num; }
        public int setD(int num) { d = num; }
        public int setE(int num) { e = num; }
        public ImmutableClass build() {
            return new ImmutableClass(this);
        }
    }
}
```
