---
layout: post
title: "Explain Inherit and Polymorphism with 2 Examples"
date: 2017-10-18 17:28:01
author: "Wei SHEN"
categories: ["java"]
tags: ["inherit","polymorphism"]
description: >
---

### 外观类型（Apparent Type）和实际类型（Actual Type）
```java
class Father {
    public void show() { System.out.println("I am Father!"); }
}
class Son {
    public void show() { System.out.println("I am Son!"); }
}
Father f = new Son();
f.show(); // I am Son!
```
从后续代码的角度看`f`代表一个`Father`类型实例的引用，我们称`Father`是变量的外观类型（Apparent Type）。

但当我们调用`f.show()`，实际执行的是`Son`类型的`show()`方法。因为变量`f`的实际类型（Actual Type）是`Son`。

变量的最终外观类型在编译器是可知的，而实际类型需要到运行期才能确定。

### 动态绑定关键步骤1： 抓住对象的“实际类型”
看下面这个例子，`C`类的`f1()`方法中调用`super.f2()`，因为`C`类继承自`B`类，因此`super`指向一个`B`类对象，调用的就是`B`类的`f2()`函数，输出`I am f2() from B!`。
```java
private static class A {
    public void f1() {
        f2();
    }
    public void f2() {
        System.out.println("I am f2() from A!");
    }
}
private static class B extends A {
    public void f2() {
        System.out.println("I am f2() from B!");
    }
}
private static class C extends B {
    public void f1() {
        super.f2();
    }
    public void f2() {
        System.out.println("I am f2() from C!");
    }
}
```

```java
A a = new A();
a.f1(); // I am f2() from A!
B b = new B();
b.f1(); // I am f2() from B!
C c = new C();
c.f1(); // I am f2() from B!
```

### 动态绑定关键步骤2：如果实际类型没有目标方法的实现，就到父类中去找
为了更快地找到实际类型，虚拟机在方法区中简历了一个虚方法表（Virtual Method Table）。虚方法表中存放着各个方法的实际入口地址。如果某个方法在子类中没有被重写，那子类的虚方法表里的地址入口和父类相同方法的地址入口是一致的。
![method-table](/images/inherit-polymorphism/method-table.jpg)

如上图所示，`Father`类和`Son`类继承自`java.lang.Object`类的方法的入口地址，和`Object`是一致的。而`Father`类新增的两个`hardChoise()`重载方法有他们自己的入口地址。而当这两个方法在`Son`类中被重写了之后，`Son`类中的这两个方法又有了两个新的入口地址。如果没有重写，将保持`Father`类中`hardChoise()`方法的入口地址。

看下面这个例子，变量`s`的实际类型是`Son`，但因为`Son`类没有重写`Father`类的`method()`方法，因此`s.method()`绑定的依然是`Father#method()`函数的入口地址。通过这个入口再调用`show()`函数，调用的自然是`Father#show()`函数。所以两个测试输出的都是`Father`。
```java
class Father{
    private void show(){
        System.out.println("Father");
    }
    public void method(){
        show();
    }
}
class Son extends Father{
    public void show(){
        System.out.println("Son");
    }
}
class Test {
    public static void main(String[] args) {
        Father f=new Father();
        f.method(); // Father
        Son s=new Son();
        s.method(); // Father
    }
}
```

### 关于Java继承的两点总结
1. 子类同名成员字段不覆盖父类成员字段。
2. 子类重写父类成员方法将更新方法区虚方法表中方法的入口地址。

本质上讲，Java子类继承父类，是对父类的一种增强。构造子类之前，都需要先构造父类实例。子类修改部分父类成员，只是又增加了一份新的同名成员（不管是字段还是方法），并且新成员占据了默认入口地址。父类成员依旧保留，随时可以通过`super`引用调用。


### 参考资料
* 《深入立即Java虚拟机》P252-P258
