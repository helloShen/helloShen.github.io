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
复习Linux文件系统的基本概念。方便以后查阅。这不是命令大全，只强调几个我自己需要搞清楚的重点。

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

### 选项和参数
掌握了下面这个命令的通用格式，
> **command -options arguments**

options一般有 **长选项**，用两个分隔符分割`--`，和 **短选项**，用一个分隔符`-`分割，之分。同一件事，经常同时有长选项和短选项两个版本，效果一样。

**遇到不懂的就用`-h`查手册。** 会查手册，基本Linux命令行就能用起来了。同样，`-h`是短选项，它还有个长选项的版本`-help`。
* **`-h`**
* **`-help`**

help信息太多，一般结合`less`和`grep`命令显示，看得更清楚，
```
wget -h | less   #更多内容，翻页往下看
wget -h | grep XXX  #只显示包含有XXX的内容
```

### 文件系统
记住这句话：
> **在类Unix系统中,一切皆是文件！** (大多数都是文本文件，所以Linux中没有秘密存在！)

类UNIX系统中像`open()`,`read()`,`write()`这样的系统调用，直接面对的不是物理介质。而是`虚拟文件系统(VFS)`。它是一套通用抽象文件模型，拥有统一的接口和数据结构。不管底下的物理介质如何，系统调用面对的都是一套统一的`VFS`接口。

VFS有4种主要对象类型：`superblock`, `dentry`, `inode`和`file`.
1. `superblock`: 文件系统的宏观控制信息。
2. `inode`: 除了文件名之外的描述文件属性的元信息。
3. `dentry`: 文件名和`inode`对象的映射。
4. `file`: 文件本体。目录也是文件。

比如`/etc/foo`路径，有两个目录文件: 根目录`/`和`etc`目录，以及一个普通文件`foo`组成。先通过`dentry`中的`filename-inode`映射，找到对应的`inode`编号，并找到`inode`。找到`inode`就能找到具体的文件所在的位置。

关于`inode`有一篇很好的文章：阮一峰的 [**《理解inode》**](http://www.ruanyifeng.com/blog/2011/12/inode.html)


### 本章的主要命令
有了以下4个命令，就可以在文件系统里，自由穿梭。
* cd: 更改目录
* ls: 列出目录内容
    * -l: 完整信息
    * -t: 按时间先后排序
    * -r: 反向排序
* file: 确定文件类型
* less: 浏览文件内容

### 本章提到的所有命令
* pwd: 打印出当前工作目录名
* cd: 更改目录
* ls: 列出目录内容
* date: 显示日期
* cal: 显示日历
* df: 查看磁盘剩余空间
* free: 显示剩余空间数量
* exit: 关闭终端窗口
* file: 确定文件类型
* less: 浏览文件内容
