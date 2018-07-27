---
layout: post
title: "Useful Makefile Snippets "
date: 2018-07-27 00:28:58
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["makefile"]
description: >
---

### 这不是教程
这不是一个系统的makefile教程。教程网上很多。这里只是摘录几个常用makefile片段作为良好实践的案例。

### 获取所有`.c`源文件，以及`.o`目标文件
用通配符获得特定（当前）文件夹下所有`.c`源文件
```
src = $(wildcard *.c)
```

既可以用`patsubst`函数将`.c`全部替换成`.o`，以获取全部`.o`目标文件，
```
obj = $(patsubst %.c,%.o,$(wildcard *.c))
```
也可以用冒号`:`做静态模式替换，
```
obj = $(src: .c: .o)
```

### 利用隐性规则编译并链接
makefile默认的隐性规则是：
> 默认`.o`目标文件的依赖文件为同名`.c`源文件，并使用命令`cc –c $(CFLAGS) foo.c`来生成`foo.o`。

所以下面简短的两行命令就能把目录下所有`.c`源文件编译成`.o`目标文件，然后再链接成一个可执行文件`myprog`。
```
myprog: $(obj)
    $(CC) -o $@ $^ $(LDFLAGS)
```
命令中的`$(CC)`变量是像`gcc`这样的编译器，`$@`指代最终的可执行文件，`$^`代表全部`.o`目标文件。`$(LDFLAGS)`代表编译时的附加选项。


* `$@`: evaluates to all
* `$<`: evaluates to library.cpp
* `$^`: evaluates to library.cpp main.cpp

### 能处理%99自己小程序的通用makefile
```
src = $(wildcard *.c)
obj = $(src:.c=.o)

LDFLAGS = -lGL -lglut -lpng -lz -lm

myprog: $(obj)
    $(CC) -o $@ $^ $(LDFLAGS)

.PHONY: clean
clean:
    rm -f $(obj) myprog
```


### 参考文献
http://nuclear.mutantstargoat.com/articles/make/
