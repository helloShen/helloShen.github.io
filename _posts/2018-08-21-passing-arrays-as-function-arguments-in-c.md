---
layout: post
title: "Passing Arrays as Function Arguments in C"
date: 2018-08-21 23:37:45
author: "Wei SHEN"
categories: ["c"]
tags: ["array"]
description: >
---

### 背景：C语言函数参数是传值调用
传递给被调用函数的参数值会被拷贝一份存放在函数的临时变量中，而不是存放在原来的变量中。比如，下面这个函数将参数`n`乘以2.

```c
void double(int n) {
    int old = n;
    n *= 2;
    printf("Multiply %d by 2 = %d\n", old, n);
}
```
但`main()`函数中传递给这个函数的参数`n`的值并不会在被传递给`double()`函数后改变。
```c
int main() {
    int n = 100;
    double(n);
    printf("after calling double(), n = %d\n", n);
}
```

### 数组作为参数时，实际传递的是指向数组首地址的指针
实际上编译器是作弊了。一个典型的例子是`main()`函数的参数`char *argv[]`，
```c
/** args参数实际是一个指向字符串数组的指针 */
int main(int argc, char *argv[]) {
    *++argv; // 这个自增操作是合法的（如果argv是数组，这会是不合法的）
}
```
如果`char *argv[]`是字符串数组，那么针对数组变量名`argv`的自增运算会是不合法的。但这是实际上编译器偷偷创建了一个指向原字符串数组的指针，再传递给`main()`函数。这样做是为了避免在传递数组参数的时候赋值整个数组。

![argv](/images/passing-arrays-as-function-arguments-in-c/argv.png)
