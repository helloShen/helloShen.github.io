---
layout: post
title: "File System of Linux(to be continued)"
date: 2017-04-07 03:24:28
author: "Wei SHEN"
categories: ["linux"]
tags: ["shell"]
description: >
---

### 按
复习Linux文件系统的基本概念。方便以后查阅。

### 路径

#### 绝对路径
> `/`表示根目录。

> `~`表示当前用户`HOME`目录。

下面的绝对路径表示： **更换当前工作目录至根目录下的`usr`文件夹下的`bin`文件夹。**
```bash
[me@linuxbox bin]$ cd /usr/bin
```

`pwd`命令显示绝对路径：
```bash
[me@linuxbox bin]$ pwd
/usr/bin
```

#### 相对路径
1. `.`指当前工作目录。
2. `..`指当前工作目录的父目录。

如果当前工作目录为系统根目录下的`usr`文件夹，
```bash
[me@linuxbox usr]$ pwd
/usr
[me@linuxbox usr]$ cd ./bin
```
那么原先的`cd /usr/bin`也可以写成`c ./bin`。

> 记住！几乎在所有的情况，`./`是可以省略的。因为`./`默认加入`$PATH`环境变量。
```bash
[me@linuxbox usr]$ cd bin
```

### to be continued...
