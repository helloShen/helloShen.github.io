---
layout: post
title: "How to Declare String in C?"
date: 2017-07-15 17:04:12
author: "Wei SHEN"
categories: ["c"]
tags: ["array","string","memory"]
description: >
---

### 问题
C语言中，
* String的定义是什么？
* String占用的是堆还是栈还是别的什么内存？
* String是否可变？
* 怎么声明和定义一个String？

### String的定义
ISO/IEC 14882:2003 标准中定义如下：
```
2.13. String literals

[...]An ordinary string literal has type “array of n const char” and static storage duration (3.7)
Whether all string literals are distinct (that is, are stored in nonoverlapping objects) is implementation- defined. The effect of attempting to modify a string literal is undefined.
```

有3个关键点，
1. String应该是一个`const char str[]`，字符数组常量。
2. String应该是`static`静态的。内存的位置是和所有`static variable`,`globle variable`,`static local variable`一样，在`data segment`里。不在`栈`也不在`堆`。
3. String是否可变，是一个 **未获定义的事件**。 实际效果取决于平台的具体实现。在有的机器上可以，有些就不可以。


### 不要把String声明成一个指针
理论上讲，一个标准的String声明和定义，应该赋给一个指针。**但不要这样做！** 代码如下，
```c
char *p = "abc"; //不推荐使用
p[0] = 'x'; //未获定义行为
```
这行代码之后，我们会获得两个东西，
1. 一个“标准意义上”的String。它是一个静态常量，储存在data segment。
2. 指向这个String首元素地址的一个指针。这个指针属于在"栈"区的局部变量。

```
    +-----+     +---+---+---+---+
p:  |  *======> | a | b | c |\0 |
    +-----+     +---+---+---+---+
```

实际执行的代码，类似下面的过程，
```c
#include <stdio.h>

const static char literal_constant_34562[4] = {'a', 'b', 'c', '\0'};

int main()
{
    char *p;

    p = &literal_constant_34562[0];

    return 0;
}
```

这么做最大的缺点就是：
> 当我们尝试修改字符串的内容，会获得一个“未获定义行为”。简单说就是，不保证能修改。

所以 **不推荐把字符串赋给一个指针的形式**。

### 推荐把字符串声明成一个char array
```c
char a[] = "abc"; //推荐
a[0] = 'x'; //正确
```
上面代码，会让我们得到一个由字符`a`,`b`,`c`以及末尾`\0`组成的一个字符数组。和所有数组一样，作为局部变量，存在`栈`区。
```
+---+---+---+---+
| a | b | c |\0 |
+---+---+---+---+
```
这样就可以正常修改字符串中的字符。

### 怎么让函数返回一个字符串？
由于数组的名字不属于变量，我们无法对其赋值，下面的操作是 **错误的**。要将字符串内容写入数组，可以用`strcpy()`函数拷贝。
```c
int main() {
    char word[15];
    word = getWord(); // ERROR: array type 'char [15]' is not assignable
    strcpy(word,getWord()); // 正确
}

/** 试图返回一个string. string内存在heap上开辟了。调用的函数负责free(). */
static char *getWord() {
    char word[] = (char *)malloc(15 * sizeof(char));
    strcpy(word,"hello,world!");
    return word;
}
```

当然如果不用数组，而是用一个指针`char *`来接收返回的字符串，是完全可以的。
```c
char *word = getWord();
```



### 参考文献
[String literals: Where do they go?] -> <https://stackoverflow.com/questions/2589949/string-literals-where-do-they-go>

[here in memory are string literals ? stack / heap?] -> <https://stackoverflow.com/questions/4970823/where-in-memory-are-string-literals-stack-heap>

[How to declare strings in C] -> <https://stackoverflow.com/questions/8732325/how-to-declare-strings-in-c>

[Arrays and Pointers] -> <http://www.lysator.liu.se/c/c-faq/c-2.html>

[Data segment] -> <https://en.wikipedia.org/wiki/Data_segment>

[Array type char[] is not assignable?] -> <https://stackoverflow.com/questions/32313150/array-type-char-is-not-assignable>
