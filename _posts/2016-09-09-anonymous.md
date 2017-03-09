---
layout: post
title: "Can Anonymous Class Extend a Class or an Interface ?"
date: 2016-09-09 19:29:24
author: "Wei SHEN"
categories: ["java"]
tags: ["inner class","interface"]
description: >
  答案应该是：匿名内部类可以继承其他类，但不能用extends。 可以实现某接口，但不能用implements。
---

答案应该是：匿名内部类
* **可以继承其他类，但不能用extends。**
* **可以实现某接口，但不能用implements。**

匿名内部类，顾名思义没有名字。没有名字就不能有构造器。就必须调用基类的构造器。所以当然可以继承基类。匿名内部类一个经典应用场景就是用来覆盖基类的某些方法。比如下面这样，A类的a()方法打印“AAA”。但B类的getA()方法返回的A类对象的引用，是打印“BBB”的。因为a()方法被重写了。
```java
class A{
    public void a(){System.out.println("AAA");}
    public void z(){System.out.println("ZZZ");}
}

public class B{
    public static A getA(){
        return new A(){
            public void a(){System.out.println("BBB");}
        };
    }
}
```

这个例子里明显A类就是匿名内部类的基类，因为匿名内部类返回的引用还可以调用z()方法，虽然它自己没有定义。明显继承自A类。
```java
A myA=B.getA();
myA.a();    //输出：BBB
myA.z();    //输出：ZZZ
```

所以匿名内部类必须继承自某个基类。但因为java只接受单继承，所以就不可能再显式地在后面加extends某个类。

实现接口的情况，同理可证。因为语法规定匿名内部类只能实现一个接口。

发明这题的人在玩文字游戏。能隐隐感觉到这位大神当时的那种小得意吗？
