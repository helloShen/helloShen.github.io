---
layout: post
title: "The Wildcards \"?\" in Java Generics"
date: 2016-08-21 23:30:26
author: "Wei SHEN"
categories: ["java"]
tags: ["generics","wildcards","pesc"]
description: >
---

### `<T>`和`<?>`用在两种不同的场景
讨论`<T>`和`<?>`，首先要区分开两种不同的场景：
* **第一，声明一个泛型类或泛型方法。**
* **第二，使用泛型类或泛型方法。**

类型参数`<T>`主要用于第一种，声明泛型类或泛型方法。
无界通配符`<?>`主要用于第二种，使用泛型类或泛型方法。

### `<T>`声明泛型类的类型参数
`List<T>`最应该出现的地方，应该是定义一个泛型List容器。但List是库里自带的容器，看看ArrayList的源码头一行：
```java
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.o.Serializable {
    ... ...
}
```

`ArrayList<E>`中的“E”也是类型参数。只是表示容器中元素Element的时候，习惯用“E”。换一个简单的例子，我们自己定义一个新泛型容器叫`Box<T>`。
```java
class Box<T>{
    private T item1;
    private T item2;
}
```

为什么这里要用类型参数？因为这是一种”约束“，为了保证Box里的item1, item2都是同一个类型T。`Box<String>`，代表两个item都是String。`Box<Integer>`里两个item都是Integer。

List容器库里都帮我们写好了，所以我们是不会去定义List<T>的。

那什么时候会出现`List<T>`？有几种情况，

要么是作为泛型类的成员字段或成员方法的参数间接出现。还是刚才`Box<T>`的例子，
```java
class Box<T>{
    private List<T> item;
    public List<T> get(){return item;}
    public void set(List<T> t){item=t;}
}
```

现在Box类里有三个地方出现了`List<T>`：
* 成员字段item的类型
* get()方法的返回值
* set()方法的参数

这里写成List<T>为了表示和Box<T>类型参数保持一致。

### `<T>`声明泛型方法
另外一种会出现`List<T>`的地方是泛型方法。比如Function类的reduce是个静态泛型方法，负责对列表里的所有元素求和。这里的`List<T>`出现在参数，函数返回值和函数内部，也是为了保持泛型类型的一致性。
```java
class Fuction{
    public static <T> List<T> reduce(List<T> list){
        //...do something
    }
}
```

### 无界通配符`<?>`不能当“类型参数”用
反观`List<?>`，首先要明确通配符不能拿来声明泛型。像下面这样用通配符"?"来表示类型参数的约束是肯定不行的。
```java
//Error Example
class Box<?>{
    private ? item1;
    private ? item2;
}
```

所以通配符是拿来使用定义好的泛型的。比如声明List容器的一个实例对象。
```java
List<?> list = new ArrayList<String>();
```
### <?>的各种坑
但`List<?>`这个写法非常坑。因为，这时候通配符会捕获具体的String类型，但编译器不叫它String，而是起个临时的代号，比如”CAP#1“。所以以后再也不能往list里存任何元素，包括String。唯一能存的就是null。
```java
List<?> list = new ArrayList<String>();

list.add("hello");    //ERROR
list.add(111);    //ERROR

//argument mismatch; String cannot be converted to CAP#1
//argument mismatch; int cannot be converted to CAP#1
```

另外如果拿`List<?>`做参数，也会有奇妙的事情发生。还是刚才`Box<T>`的例子，有get()和set()两个方法，一个存，一个取。
```java
class Box<T>{
    private List<T> item;
    public List<T> get(){return item;}
    public void set(List<T> t){item=t;}
    //把item取出来，再放回去
    public void getSet(Box<?> box){box.set(box.get());}    //ERROR
}
```

新的getSet()方法，只是把item先用get()方法读出来，然后再用set()方法存回去。按理说不可能有问题。实际运行却会报错。
```java
error: incompatible types: Object cannot be converted to CAP#1
```

原因和前面一样，通配符`box<?>`.set()的参数类型被编译器捕获，命名为CAP#1，和`box<?>.set()`返回的Object对象无法匹配。

解决方法，是要给getSet()方法写一个辅助函数，具体原理可以去查《Java核心技术-卷1》，泛型这章，或者《Java编程思想》。都有讲。
```java
class Box<T>{
    private List<T> item;
    public List<T> get(){return item;}
    public void set(List<T> t){item=t;}
    //helper()函数辅助getSet()方法存取元素
    public void getSet(Box<?> box){helper(box);}
    public <V> void helper(Box<V> box){box.put(box.get());}
}
```

### 有界通配符<? extends XXX>，<? super XXX>
实际更常用的是`<? extends XXX>`或者`<? super XXX>`两种，带有上下界的通配符。关于这两种情况可以参考另一个回答：
