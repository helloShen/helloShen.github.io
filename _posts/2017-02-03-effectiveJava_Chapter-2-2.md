---
layout: post
title: "[Effective Java] Note: - Chapter-2-2: Instance-Control"
date: 2017-02-03
author: "Wei SHEN"
categories: ["java","effective java","design pattern"]
tags: ["instance control","singleton","factory pattern"]
description: >
  一个普遍原则是尽量避免生产不必要的对象，能用已有实例就用已有实例。有些情况甚至要将实例数量控制在一定范围之内。最极端的情况就是单例器Singleton，全局只存在唯一的实例。所以这一篇的主题是怎么控制构造器的访问权限，怎么重复利用有限数量的实例。
---

### Singleton
单例器(Singleton)是实例控制的极端情况。但也非常常见。这本书列举了3中常见的单例器的惯用实现方法。但构建单例器的基本思路是不变的：
> 隐藏构造器，只保留一个实例，置于类的静态域中。

#### 直接通过静态域访问
最朴素的Singleton实现就是 **直接公开那个静态域中的唯一实例**。

```java
public class OurPlanet {
    public static final OurPlanet EARTH = new OurPlanet("The Earth"); // 公有访问权限
    private String name;
    private OurPlanet(String name) {
        this.name = name;
    }
}
```

#### 通过公有静态工厂方法访问
前面已经讲过，用静态工厂方法替代构造器，能提供灵活性。
```java
public class OurPlanet {
    private static final OurPlanet EARTH = new OurPlanet("The Earth"); // 私有化静态域中的实例
    private String name;
    private OurPlanet(String name) {
        this.name = name;
    }
    public OurPlanet getInstance() { // 公有的静态工厂方法成为唯一的访问途径
        return EARTH; // 总是返回唯一的实例
    }
}
```
这样可以在不改变API的情况下（用户还是访问`newInstance()`方法），改变是否应该是Singleton的想法。比如，若干年后，人类可以居住在火星。
```java
public class OurPlanet {
    private static final OurPlanet EARTH = new OurPlanet("The Earth"); // 私有化静态域中的实例
    private static final OurPlanet MARS = new OurPlanet("The Mars"); // 私有化静态域中的实例
    private static boolean switch = true;
    private String name;
    private OurPlanet(String name) {
        this.name = name;
    }
    public OurPlanet getInstance() { // 公有的静态工厂方法还是唯一的访问途径
        switch = !switch;
        return switch? EARTH : MARS; // 人类已经有两个家园，地球和火星
    }
}
```

#### 包含单个元素的枚举是实现Singleton的最佳方法
枚举型是个大大的语法糖，它其实是一个实实在在的类。只需编写一个只包含单个元素的枚举型，我们就有了一个质量可靠的Singleton。
```java
public enum OurPlanet { EARTH }
```
上面`EARTH`的实际身份就是一个用`static final`修饰过的公有域。这都和Singleton的模式一模一样。

记住，**单元素的枚举型已经成为实现Singleton的最佳方法**。和传统的Singleton比，枚举明显的优势有两个，
1. 抵御反序列化攻击。为了让Singleton成为可序列化的，光实现`Serializable`接口是不够的，而且所有实例域必须是`transient`的，而且必须重写`readResolve()`方法，否则反序列化的过程会产生一个假冒的实例。防御这些问题的工作，枚举型做的很好，编译器无偿替我们做了。
2. 抵御反射攻击。`AccessibleObject.setAccessable()`方法可以改变私有构造器的访问权限。这方面枚举型的构造器能够抵御这样的攻击。在接到生产额外实例的请求时，枚举型的构造器会抛出异常。关于抵御反射攻击，在下一节会讲到。


### 隐藏构造器
为了控制系统中存在的实例数量，就必须隐藏类的构造器。禁止用户访问它。
#### 抽象类不能强化不可实例化的能力
最简单的设置成抽象类，并不能禁止用户将它实例化。因为虽然不能实例化抽象类本身，**但用户可以实例化抽象类的子类**。

#### 最好是私有化构造器
像前面的Singleton的三个实现，构造器都被设为了`private`权限。

#### 但反射攻击能够改变私有构造器的访问权限
`AccessibleObject.setAccessable()`方法可以私有构造器的访问权限改为公有。

#### 终极防御是让构造器有条件地抛出异常
最简单的比如增加一个计数器，在创造了足够数量的实例之后，构造器再接到实例化请求就抛出异常。

```java
public class TenUnits {
    private static int max = 10;
    private TenUnits() {
        if(max++ >= 10) { // 超出10个实例，抛出异常
            throw new RuntimeException("Only 10 Objects allowed!");
        }
        // some code
    }
}
```

### 避免创建不必要的对象
记住，**一般来说最好能重用对象，而不是在每次需要的时候就创建一个相同功能的新对象**。最简单的，当类的某个方法总是重复创建某些相同的对象时，设置一个域来储存这些对象，能防止每次调用这个方法都重复创建对象。

下面的代码片段用来判断一个人是否出生于`1946-1965`年间的“婴儿潮”。
```java
public class Person {
    private final Date birthDate;
    // other fields, methods ... ...
    public boolean isBabyBoomer() {
        Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        gmtCal.set(1946,Calendar.JANUARY,1,0,0,0);
        Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date boomStart = gmtCal.getTime();
        gmtCal.set(1965,Calendar.JANUARY,1,0,0,0);
        Date boomEnd = gmtCal.getTime();
        return birthDate.compareTo(boomStart) >= 0 && birthDate.compareTo(boomEnd) < 0;
    }
}
```
把`Date`对象设置成静态域以后，每次调用`isBabyBoomer()`方法都不会再创建这么多对象了。
```java
public class Person {
    public boolean isBabyBoomer() {
        private static final Date BOOM_START;
        private static final Date BOOM_END;
        private final Date birthDate;
        // other fields, methods ... ...
        static {
            Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            gmtCal.set(1946,Calendar.JANUARY,1,0,0,0);
            Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            Date boomStart = gmtCal.getTime();
            gmtCal.set(1965,Calendar.JANUARY,1,0,0,0);
            Date boomEnd = gmtCal.getTime();
        }
        public boolean isBabyBoomer() {
            return birthDate.compareTo(boomStart) >= 0 && birthDate.compareTo(boomEnd) < 0;
        }    
    }
}
```

#### 小心”不可变“类特别容易产生多余的对象
当尝试修改一个不可变对象，获得的会是一个拥有不同值的全新的对象，而不是在原对象上修修补补。一个极端的例子就是`String`类。下面的代码其实是返回了字面量为`abc`的另一个`String`对象，虽然变量名还是`s`，但却指向了不同的对象。因为`String`是不可变的，声明的时候是`ABC`就一直是`ABC`，要`abc`只能重新创建一个新对象。
```java
String s = "ABC";
s.toLowerCase();
```
下面的代码，实际产生了两个`String`对象。字面量`ABC`本身已经是一个完整的`String`对象，最后的变量`s`又是一个拥有不同内存地址的新对象。
```java
String s = new String("ABC"); // 不要这样做
```
如果直接使用字面量，就不会产生多余的对象。
```java
String s = "ABC"; // 这样比较好
```

#### 小心“自动装箱”类型
下面这个例子，计算所有int正值的总和，
```java
public static void main(String[] args) {
    Long sum = 0L;
    for (long i = 0; i < Integer.MAX_VALUE; i++) {
        sum += i;
    }
    System.out.println(sum);
}
```

#### 也不是对象越少就越好，适得其反
避免创建不必要的类，**不等于说就一定对象越少越好**。小对象的创建开销相当廉价，因此有意识地添加一些附加对象，提升程序的可读性，功能性还是很好的。而且在创建“对象池”以重用以后对象的时候，也要想清楚，因此带来的代码混乱度是不是值得这么做。
