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

最重要的文件类型：**文本文件**。简单说就是 **把字节流翻译成字符流显示出来**。 最基本文本文件当然是 **ASCII文本**。它就是非常简单的 **ASCII字符码和人类字符的一一对应**。 ASCII仅仅包括英语的26个字母`a~z`，`A~Z`，数字`0~9`，以及一些基本的控制符，比如"制表符"：`\t`，"回车符"：`\r`，"换行符"：`\n`，等。
```bash
Hello World =
01001000(H) 01100101(e) 01101100(l) 01101100(l) 01101111(o) 00100000(" ") 01010111(W) 01101111(o) 01110010(r) 01101100(l) 01100100(d)
```

简述以下linux的文件系统：superblock, dentry, inode, file.

怎么找到`/etc/foo`文件，
1. 根据`/`在`superblock`里找到系统根目录挂载点`s_root`。它是一个指向`dentry`结构的指针。
2. 每个`dentry`结构里都有`d_inode`，它是指向`inode`结构的一个指针。以及一个`char[]`数组表示的文件名。
3. `inode`结构是描述文件的元信息。Linux系统不是以文件名来区分文件，而是"节点号"，相当于文件的"id"。这部分信息就在`inode`结构里。具体是`i_ino`的一个`long`型数据编号。通过`inode`就能找到真正的文件在磁盘上的位置，具体是`i_mapping`这个变量。
4. 最后通过地址找到`foo`文件本体。

一个虚拟的过程如下：
看到`/`，就要找系统根目录文件。
到`superblock`里找系统根目录`dentry`：`s_root`。
`s_root`里记录了系统根目录文件地址的`12345`号`inode`的地址是`0xffff`。
到`0xffff`读取编号为`12345`的`inode`。里面的`i_mapping`项写了系统根目录文件的地址是`0xeeee`。
到`0xeeee`找到系统根目录文件。找到里面有一个叫`etc`的`dentry`。
`etc`这个`dentry`里找到`78910`号`inode`的地址是`0xdddd`。
`0xdddd`就是`etc`文件本尊，就是`etc`目录。在里面找到一个叫`foo`的`dentry`。
`foo`这个`dentry`里有一个`54321`号`inode`，显示地址是`0xcccc`。
在`0xcccc`找到了`54321`号`inode`，里面写着文件地址是`0xbbbb`。
在`0xbbbb`找到了`foo`文件本尊。

### 本章的主要命令
有了以下4个命令，就可以在文件系统里，自由穿梭。
* cd: 更改目录
* ls: 列出目录内容
* file: 确定文件类型
* less: 浏览文件内容

### 本章提到的所有命令
* pwd: 打印出当前工作目录名
* cd: 更改目录
* ls: 列出目录内容
    * -l: 完整信息。
    * -t: 按时间先后排序。
    * -r: 反向排序。
* date: 显示日期
* cal: 显示日历
* df: 查看磁盘剩余空间
* free: 显示剩余空间数量
* exit: 关闭终端窗口
* file: 确定文件类型
* less: 浏览文件内容
