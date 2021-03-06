---
layout: post
title: "Radix Sort"
date: 2018-09-10 23:51:50
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["sort","trie"]
description: >
---

### 基数排序（Radix Sort）
基数排序（Radix Sort）又称【桶子法】。顾名思义就是用几个“桶”给数据归类。下面这个例子按照【最低位优先（LSD（Least sgnificant digital）】，也就是从最右边的位开始排序。下面是一堆乱序数字，
```
73, 22, 93, 43, 55, 14, 28, 65, 39, 81
```
先按照个位数归类到`[0~9]`10个桶里，
```
0
1 81
2 22
3 73 93 43
4 14
5 55 65
6
7
8 28
9 39
```
把数字从`[0~9]`10个桶里按顺序收集起来，
```
81, 22, 73, 93, 43, 14, 55, 65, 28, 39
```
在按十位数归类到`[0~9]`桶里，
```
0
1 14
2 22 28
3 39
4 43
5 55
6 65
7 73
8 81
9 93
```
再收集起来，这时候数字已经从小到大排好序，
```
14, 22, 28, 39, 43, 55, 65, 73, 81, 93
```

### 具体实现
可以用传统的`HashMap`，也可以用`Trie`（词典树）。用Trie更节省空间。之前按个位数归类，用Trie实现过程如下图所示，
```
                        root
    +--------------------------------------------+
    |    |    |    |    |    |    |    |    |    |                
         1    2    3    4    5              8    9  
         |   / \  /|\   |   / \             |    |
         8  2   27 9 4  1  5   6            2    3
```

### 参考资料
* [【基数排序 - 作者：NEXTFIND】](https://www.jianshu.com/p/a1012fab9161)
