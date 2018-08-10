---
layout: post
title: "Shell Script Reuse"
date: 2018-07-15 20:08:41
author: "Wei SHEN"
categories: ["linux"]
tags: ["shell"]
description: >
---

首先要了解有两种方式可以执行一个shell脚本：

1. 一种是新产生一个shell，然后执行相应的shell scripts。

方法是在scripts文件开头加入以下语句
```
#!/bin/bash
```
一般的script文件(.sh)即是这种用法。这种方法先启用新的sub-shell（新的子进程）,然后在其下执行命令。
也可以指定shell类型，如：
```
$sh scriptfile
```

2. 一种是在当前shell下执行，不再启用其他shell。

方法是使用`source`命令，不再产生新的shell，而在当前shell下执行一切命令。
也有两种语法：
```
$source scriptfile
```
或者直接用点号：
```
. scriptfile
```
`sh`只支持点号，不支持source命令，所以建议使用点号。

一个非常形象的类比是：
> shell的source就是C中的include。

注意：shell不会判断一个shell脚本是不是被导入多次，每次source（或者点号）scriptFile，都会在当前shell中执行scriptFile。这点和C的include是一样的。正如C可以使用条件包含避免重复导入头文件，shell也有类似的机制。这个我们在下面会讲到。

### 参考文献
* [http://arganzheng.iteye.com/blog/1174470]
