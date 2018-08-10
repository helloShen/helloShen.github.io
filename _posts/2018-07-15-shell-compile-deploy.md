---
layout: post
title: "Useful Script to Get Absolute Path"
date: 2018-07-15 18:39:13
author: "Wei SHEN"
categories: ["linux"]
tags: ["shell"]
description: >
---

### 让“进入当前脚本根目录”成为一种习惯
下面这条命令经常被用在一些脚本的开头，
```bash
cd $(dirname -- $0)
```
* `cd`: 进入到脚本的父级目录
* `dirname`: 截取给定路径的所在目录
* `$0`: 获取执行脚本的地址参数
* `--`: 就算后续参数以`-`开头，也不会被当成`dirname`命令的一个选项。
* `$()`: 表示执行括号内的命令

比如执行下面命令执行脚本:
```
sh ./sub/foo.sh
```
* `$0`代表传递给`sh`命令的地址参数`./sub/foo.sh`。
* `dirname`命令切割出`foo.sh`文件所在目录的路径`./sub`。

因为脚本可能在各种奇葩的位置被调用，开头就进入脚本根目录能极大减少出错的概率。

### 在项目根目录上专设一个脚本来获取项目根目录绝对路径
利用`pwd`命令，下面两行脚本会把当前脚本绝对路径打印出来，
```bash
#!/bin/sh
#此脚本getroot.sh专门打印当前所在目录的绝对路径

cd $(dirname -- $0)
pwd
```
如果把这个这个脚本放在项目的根目录，在项目的任意子目录调用此脚本，就能健壮地获取项目根目录的绝对路径，
```
#!/bin/sh
#这是项目某个子目录中的一个脚本a.sh，它现在需要拿到项目根目录的绝对路径

ROOT=$(sh getroot.sh)
```

#### 用`$0`获取路径的脚本要避免用`source`导入
假设我们在项目根目录上有一个脚本`a.sh`，然后在一个子文件夹`sub`中有个`b.sh`脚本，
```
~/root
|
+--a.sh
|
+--sub
   |
   +--b.sh
```
假设`a.sh`脚本中试图用`$0`获得项目根目录的绝对路径`/Users/Wei/root`，
```
#!/bin/sh
#a.sh脚本

ROOT=$(cd $(dirname -- $0); pwd)
```
当`b.sh`用`source`导入`a.sh`时，
```
#!/bin/sh
#b.sh脚本

source ../a.sh
```
`b.sh`的意图可能是获得`a.sh`所在的项目根目录的绝对路径`/Users/Wei/root`，但它实际获得的会是它自己所在目录的绝对路径`/Users/Wei/root/sub`。

这是因为用`source`导入的脚本会被整个拷贝（相当于宏展开）到目标文件，然后直接在目标文件里被执行。比如最终执行的`b.sh`脚本展开后看上去是这个样子，
```
#!/bin/sh
#b.sh脚本

#!/bin/sh
#a.sh脚本

ROOT=$(cd $(dirname -- $0); pwd)
```

### `dirname`命令的亲兄弟：`basename`
和`dirname`相对的是`basename`。`dirname`取路径的父级目录，`basename`则去掉父级路径，只取文件名。
```
        父级目录                | 文件名
"~/github/letsplayc/sh/chapter5/5-1.sh"
           dirname             | basename
```

### 用`%`和`#`切割子路径也可以获得父级目录的路径
比如，我有一个文件
```
filename="/Users/Wei/some/long/path/to/a.txt"
```
除了刚才说的通过`dirname $0`方法，下面的命令也可以获得项目根目录绝对路径`/Users/Wei/some/long/path/to`，
```
ROOT="${filename%/*}"
```
其中`%`表示从后往前，删除一个`/*`模式（最小化匹配），即一个斜杠`/`加上任意长度的文件名。

同样用`#`可以切割获得文件名部分`a.txt`。
```
basename_v1="${filename##*/}"
```
其中`##`表示从前往后，删除最长一个`/*`模式（最大化匹配），所以最后一个斜杠之前的所有路径全被删除，只剩下文件名。

两种方法的效果是一样的，我更喜欢用`dirname`和`basename`，因为他们更健壮。实际这两个函数内部处理的足够多的细节，并向用户隐藏了这些细节。他们被造出来，就是为了应付单纯的`%`或者`##`无法应付的更复杂的场景。我们不应该重复造这些轮子。只需要知道`dirname`和`basename`更可靠。


### 参考文献
* [https://unix.stackexchange.com/questions/253524/dirname-and-basename-vs-parameter-expansion]
* [https://stackoverflow.com/questions/23162299/how-to-get-the-last-part-of-dirname-in-bash]
* [https://blog.csdn.net/apache0554/article/details/47055827]
* [https://www.cnblogs.com/xuxm2007/archive/2011/10/20/2218846.html]
