---
layout: post
title: "[Code Complete] Note: Chapter 12 - Basics Data Types"
date: 2017-01-27 23:54:16
author: "Wei SHEN"
categories: ["java","code complete"]
tags: ["code style"]
description: >
---

### 具名常量
#### 不要出现数值字面量(literal numbers)，
最好全部用具名常量代替，唯一能出现的字面量就是0和1。这样做的好处是好维护。以后需要修改的时候，只要修改一处就行。

直接写死数值，是不好的做法。

```java
public void foo() {
    for (int i = 0; i < 99; i++) {
        // do something
    }
}
```

使用具名常量是更稳妥的做法。

```java
private final int MAX_VALUE = 99;
public void foo() {
    for (int i = 0; i < MAX_VALUE; i++) {
        // do something
    }
}
```

#### 不要使用文字字面量(literal characters)。
下面直接写死文字字面量也不好。

```java
BufferedReader br = new BufferedReader(new FileReader(new File("/Users/Wei/java/Exercise1.java")));
```

要用具名常量或者至少也要声明一个变量来替代。

```java
String path = "/Users/Wei/java/Exercise1.java";
BufferedReader br = new BufferedReader(new FileReader(new File(path)));
```

### 整型
#### int的取值范围是正20亿到负20亿
* byte: -128  ~  127.    占用1个字节（-2的7次方到2的7次方-1）
* short: -32768 ~  32767.    占用2个字节（-2的15次方到2的15次方-1）
* int: -2147483648  ~  2147483647 (大概是正负20亿).    占用4个字节(带符号32bit -2的31次方到2的31次方-1)
* long: -9223372036854774808  ~  9223372036854774807.    占用8个字节（-2的63次方到2的63次方-1）

#### int整型除法不保留小数
`7 / 10 = 0`，不等于`0.7`。

### 浮点数
#### 浮点数不精确
浮点数的计算结果是不精确的。
```bash
0.1 + 0.1 = 0.2
0.1 + 0.1 + 0.1 = 0.30000000000000004
```

原因是，十进制的小数，用二进制表示会变成无限循环小数。只有特定小数才能转换成有限二进制小数，例如，
```bash
0.625 转成二进制是：0.101
0.4 转成二进制是：0.011001100...无限循环
0.1 转成二进制是：0.00011001100110011...无限循环
```

#### 所以不要用浮点数比大小
下面这个比较的结果，10个0.1相加并不等于1.0。
```java
double nominal = 1.0;
double sum = 0.0;
for (int i = 0; i < 10; i++) {
    sum += 0.1;
}
if ( nominal == sum ) {
    System.out.println("Numbers are the same!");
} else {
    System.out.println("Numbers are not the same!");
}
```

#### 解决的办法是编程整型再计算
在一些需要确保精确的场景下，可以换用二进制编码的十进制(binary coded decimal, BCD)变量来解决。是在没有，也可以自己写一个整型变量变成整型变量的库。

### 字符串
#### 尽量不要用字面量字符串
这点在具名具名常量这部分已经说了。声明一个变量，或者具名常量，推迟变量和数据绑定的时机，降低程序的耦合，增加灵活性。

#### 尽量使用Unicode字符集
尽量考虑国际化的因素。而且编码方式也要尽量统一。比如UTF-8。

### 布尔型
#### 使用布尔变量来简化复杂的判断条件
下面这段代码可读性差，
```
if ( (elementIndex < 0) || (MAX_ELEMENTS < elementIndex) || (elementIndex == lastElementIndex) ) {
    // do something    
}
```
比较好的做法是，把局部性的判断条件声明为布尔变量，可以将判断的意图描述地更清晰，更不容易出错。
```
boolean finished =  (elementIndex < 0) || (MAX_ELEMENTS < elementIndex);
boolean repeatedEntry = (elementIndex == lastElementIndex);
if ( finished || repeatedEntry) {
    // do something
}
```

### 枚举型
#### 用枚举型提高代码可读性
下面这个绝对是糟糕的代码，用1代表红色，然后还需要注释来解释。
```java
/**
 * 1 = red
 * 2 = blue
 * 3 = green
 */
if (chosenColor = 1) {
    // ...
}
```

这时候用枚举就会让事情变得非常好懂，
```java
enum Color { RED, BLUE, GREEN }

if (chosenColor = Color.RED) {
    // ...
};
```

#### 在枚举里明确定义第一个和最后一个元素方便用于循环边界
这条在Java可能并不太适用。
```java
enum Color { FIRST_COLOR, RED, BLUE, GREEN, LAST_COLOR }
```

### 数组
#### 检查数组下标边界

#### 放弃下标的随机访问
适用顺序访问更可靠。比如Collection里的Iterator。每次都从头开始遍历，可以减少失误的可能。而且每次调用next()都可以用hasNext()进行判断。

#### 用容器取代数组
显然容器能做的更多，而且更健壮。

#### 小心多维数组的下标有没有用错
