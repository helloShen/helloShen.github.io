---
layout: post
title: "Why are arrays covariant but generics are invariant?"
date: 2016-09-08 18:09:47
author: "Wei SHEN"
categories: ["java"]
tags: ["generics","array","covariant"]
description: >
  协变就是指，当B类是A类的派生类，B[]也是A[]的派生类。把数组设计成协变，因为设计数组协变特性的时候，Java还不支持泛型。但还是需要设计一些跨类型的代码，比如以Object[]数组为参数的代码，能作用于所有类型的数组。
---

### 为什么当初数组要设计为协变？
因为SE5之前还没有泛型，但实际上很多代码迫切需要泛型来解决问题。

举个例子，比较两个数组是否“值相等“的Arrays.equals()方法。因为底层实现调用的是Object.equals()方法，和数组中元素的具体类型无关。
```java
for (int i=0; i<length; i++) {
    Object o1 = a[i];
    Object o2 = a2[i];
    if (!(o1==null ? o2==null : o1.equals(o2)))
        return false;
}
```

所以当然不想设计成每个类型都要重新定义Arrays.equals( )方法。而是”泛化“地接受任何元素类型的数组为参数，就像现在这样：
```java
public static boolean equals(Object[] a, Object[] a2) {
    ... ...
}
```
要让Object[]能接受所有数组类型，那个时候又没有泛型，最简单的办法就是让数组接受协变，把String[]，Integer[]都定义成Object[]的派生类，然后多态就起作用了。


### 数组协变好不好？
但为什么数组设计成”协变“不会有大问题呢？这是基于数组的一个独有特性：
数组记得它内部元素的具体类型，并且会在运行时做类型检查。
插一句，这也是为什么不能创建泛型数组的原因，数组创建时必须知道确切类型。此处先不表，参见另一个回答：[**《java为什么不支持泛型数组？》**](http://www.ciaoshen.com/2016/08/21/noGenericArray/)

这就是题主问题里代码能通过编译，但运行时报错的原因：
```java
Number[] num = new Integer[10];
num[0] = 2.1;
```
num变量记得它内部元素是Integer。所以运行时给它插入double型的时候不让执行。

这反而是数组的优点，也是当初”敢于“把数组设计成协变的原因。虽然向上转型以后，编译期类型检查放松了，但因为数组运行时对内部元素类型看得紧，不匹配的类型还是插不进去的。


### 为什么容器不能协变？
这也是为什么容器Collection不能设计成协变的原因。Collection不做运行时类型检查，比较耿直。还是题主Number的例子，如果Collection接受”协变“，List<Integer>的引用能传给List<Number>：
```java
List<Integer> integerList = new ArrayList<Integer>();
List<Number> num = integerList; // 假设现在容器接受”协变“
```
这时候我想往List<Number>里插入一个Double。它不会像数组这样”坚贞“，它将”安静“地接受。
```java
num.add(new Double(2.1));
```
然后当我们从原先的integerList里面取东西，才会发现出问题了。虽然看上去从integerList里取Integer，我们的操作无可指责。但取出来的却是Double型。
```java
Integer itg=integerList.get(0);    //BOOM！
```

而且，在引入了通配符（Wildcard）之后，协变的功能也已经被实现了。而且配合通配符的”上界“和”下界“一起用，容器内元素的类型还是受到严格控制的，虽然有点复杂。
```java
List<? extends Number> derivedNum=new ArrayList<Integer>();
```

关于通配符，可以看另外两个回答：
[**《List<?>和List<T>的区别？》**](http://www.ciaoshen.com/2016/08/21/wildcards/)
[**《Java 泛型 <? super T> 中super怎么理解?与extends有何不同？》**](http://www.ciaoshen.com/2016/08/21/superExtends/)




所以总的来说，虽然数组的协变不是一个完美的设计，但也不能算非常烂。起码还能用，没有捅出大篓子。而且数组又不支持泛型，底层类库到处是Object[]，现在也不可能改了。
