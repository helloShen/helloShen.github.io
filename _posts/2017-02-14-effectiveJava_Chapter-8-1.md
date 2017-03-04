---
layout: post
title: "[Effective Java] Note: - Chapter-8-1: Some Important Tips"
date: 2017-02-14
author: "Wei SHEN"
categories: ["Java","Effective_Java"]
tags: ["Code_Style"]
description: >
  比较琐碎的一些规范。但小中见大。
---

### 局部变量的作用域最小化
1. 不要在代码块的开头处统一声明变量。**在他第一次被使用的地方初始化**。
2. 每个局部变量的声明，都应该包含一个初始化的表达式。
2. 方法小而集中。

### for-each > for > while
遍历集合的既干净，又简洁的做法是：
```java
for (Element ele : list) {
    doSomething(ele);
}
```

遍历数组的最佳实践是：
```java
for (int i = 0; i < array.length; i++) {
    doSomething(array[i]);
}
```

**while循环可能需要在循环外暴露一个变量的引用**，可能在后面和另一个变量搞混，被误用，
```java
Iterator<Element> i = c.iterator();
while(i.hasNext()) {
    doSomething(i.next());
}
// 出了循环，持有Iterator对象引用的i变量还活着。

//万一，在后面的操作中误用了怎么办。 记住，局部变量，早死早超生！
Iterator<Element> i2 = c2.iterator();
while(i.hasNext()) {
    doSomething(i.next());
}
```

所以记住，**设计代表一组元素的数据结构，就算没有实现集合接口，也请实现`Iterable`接口**。

### 有类库尽量使用类库
类库基本上总是做的比自己好，把精力集中在自己的程序上。

一个典型的例子是，`Random#nextInt(int i)`方法。如果我们想自己用一段代码调用无参数的`Random#nextInt()`方法，随机数的质量会很糟糕，完全称不上随机。
```java
// 错误代码
static int random(int n) {
    return Math.abs(rnd.nextInt()) % n;
}
```
看似很简单的随机数功能，其实是经过很多专家级工程师和领域内的数学家严格论证和设计的。这么一个小功能，**自己穷其一生可能也设计不出来**。人生苦短啊！孩子乖！

两个重要的类库是：
1. Collections Framework
2. java.util.concurrent

### 如果精确答案，不要用float和double
`float`和`double`因为用二进制表示小数，但是十进制小数比如`0.1`转成二进制是无穷循环小数，所以计算不精确，`0.1 + 0.2 = 0.300000000004`。

解决方案是：**BigDecimal,int,long进行精确计算**。
1. 没有超过9位可以用`int`
2. 没超过18位可以用`long`
3. 超过18位可以用`BigDecimal`

### 基本型优于装箱类型
尽量使用基本型。但一个例外是 **泛型** 只接受对象，不接受基本型。

### 如果其他类型更合适，尽量避免用String
如果是`int`,`float`,`boolean`这种值型，尽量直接用值型。用`String`表示，别人用的时候还要解析，开销又大，又容易出错。

```java
// 错误示例，不要这样做！
String key = classname + "#" + i.next();
```

### 连接多个字符串用StringBuilder
`String`是不可变的，拼接过程中会产生很多中间垃圾拷贝对象。
```java
String result = "";
for (int i = 0; i < SIZE; i++) {
    result += new String((char)i); // 每次都会完整地拷贝整个字符串
}
```

### 通过接口引用对象
提供更多的灵活性。方便以后更换实现。下面这种操作要养成习惯，
```java
List<String> list = new ArrayList<>();
```

### 少用反射
这我很开心，因为反射的代码和冗长。而且程序可能因此性能受损。Bloch的建议是，
> **如果有无参数的构造器，用newInstance()创建实例，然后通过接口正常访问这些实例。**

参考下面这个惯用法，
```java
Class<?> klass = null;
try {
    klass = Class.forName("Set"); // 获取Class对象
} catch(ClassNotFoundException e) {
    System.err.println("Class not found.");
    System.exit(1);
}

Set<String> set = null;
try {
    Set<String> set = (Set<String>) klass.newInstance(); //用newInstance()构造实例，赋值给接口
} catch (IllegalAccessException e) {
    System.err.println("Class not accessible.");
    System.exit(1);
} catch (InstantiationException e) {
    System.err.println("Class not instantiable.");
    System.exit(1);
}

set.addAll(Arrays.asList(strArray)); // 通过接口访问对象
```

### 谨慎地使用本地方法

### 谨慎优化，或者不要优化
Java **程序员编写的代码和CPU执行的代码之间存在着语义沟**， 要想 **可靠地预测出任何优化的性能结果都非常困难**。
> **大量流传的关于性能的说法，最终都被证明为半真半假，或者根本就不正确**。

你根本不知道JVM替你做了什么。所以Bloch对性能从来不是去分析，而是直接用电脑跑，测时间。

还有一点，JDK带有性能剖析器可以用。

### 遵守命名规范
1. 包： com.google.inject
2. 类，接口： Timer,FutureTask,LinkedHashMap
3. 方法，域： remove,ensureCapacity
4. 局部变量： i, xref,houseNumber
5. 类型参数： T,E,K,V,X,T1,T2
