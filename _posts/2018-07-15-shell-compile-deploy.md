---
layout: post
title: "shell里用dirname获取当前脚本的绝对路径"
date: 2018-07-15 18:39:13
author: "Wei SHEN"
categories: ["linux"]
tags: ["shell"]
description: >
---

### 获取脚本绝对路径
下面这条命令可以健壮并准确地获取当前sh脚本的绝对路径。
```bash
PATH=$(cd $(dirname $0); pwd)
```

比如我有一个脚本`~/github/letsplayc/sh/chapter5/5-1.sh`。一般情况下，如果我跑到脚本根目录`~/github/letsplayc/sh`的位置去执行如下命令，
```bash
sh ./chapter5/5-1.sh
```

* `$0`: 获取脚本地址参数，即: `./chapter5/5-1.sh`
* `$(dirname $0)`: `$()`表示执行括号内的命令，`dirname`负责剥离出脚本的父级目录，即: `./chapter5`
* `cd`: 进入到脚本的父级目录
* `pwd`: 得到父级目录的绝对路径，即: `/Users/Wei/github/letsplayc/sh/chapter5`。非常准确，连我主目录`~`也被展开成`/Users/Wei/`。
* `PATH=$()`: 最后将绝对路径赋值给`${PATH}`变量。


### 让“进入当前脚本根目录”成为一种习惯
单纯的进入到脚本所在的根目录，把这条命令写在每个脚本的开头，是一个良好的习惯。因为大部分情况，脚本都是在各种奇葩的位置被调用的。开头就进入脚本根目录，让接下来的脚本位置非常明确，减少出错的概率。
```bash
cd $(dirname $0)
```

### 小心`source`给`$0`带来的陷阱
如果脚本将要以`source`的形式导入其他脚本，`$0`变量获取的将不是它所在脚本的相对路径，而是最终执行脚本位置的相对路径。

考虑下面这个文件结构，`root`下有`root-script.sh`，以及`sub`子文件夹。在`sub`子文件夹里有另外一个脚本`call-root-script.sh`，
```
~/root
|
+--root-script.sh
|
+--sub
   |
   +--call--root-script.sh
```
`root-script.sh`的意图是用`$0`找到自己的父级目录的绝对路径，
```
#!/bin/sh
#这是~/root/root-script.sh脚本

cd $(dirname $0)
base=$(pwd)
```
如果在`call-root-script.sh`中直接执行`root-script.sh`脚本，结果符合预期，得到`/Users/Wei/root`。
```
#!/bin/sh
#这是~/root/sub/call-root-script.sh脚本

sh ../root-script.sh
```

但如果`call-root-script.sh`是通过`source`命令导入`root-script.sh`脚本，结果得到的将是`call-root-script.sh`的绝对路径`/Users/Wei/root/sub`，因为那里才是`call-root-script.sh`实际执行的地方。
```
#!/bin/sh
#这是~/root/sub/call-root-script.sh脚本

source ../root-script.sh
```
这是因为`source`命令相当于是把`root-script.sh`的内容全抄录到自己内部，然后执行自己。

### `dirname`的好兄弟`basename`
和`dirname`相对的是`basename`。`dirname`取路径的父级目录，`basename`则去掉父级路径，只取文件名。
```
        父级目录                | 文件名
"~/github/letsplayc/sh/chapter5/5-1.sh"
           dirname             | basename
```

### 其他替代方法
其实取父级目录并不复杂，单纯的字符串处理就可以完成任务。最简单的是用`%`和`#`实现，

比如，我有一个文件
```
filename="/some/long/path/to/a_file.txt"
```

两种方法取路径的父级目录`/some/long/path/to`，
```
parentdir_v1="${filename%/*}"
parentdir_v2="$(dirname "${filename}")"
```
其中`%`表示从后往前，删除一个`/*`模式（最小化匹配），即一个斜杠`/`加上任意长度的文件名。

两种方法区路径的文件名部分"a_file"
```
basename_v1="${filename##*/}"
basename_v2="$(basename "${filename}")"
```
其中`##`表示从前往后，删除最长一个`*/`模式（最大化匹配），所以最后一个斜杠之前的所有路径全被删除，只剩下文件名。

两种方法的效果是一样的，但显然`dirname`和`basename`是更被推荐的写法。这也是这两个函数存在的原因，他们处理的足够多的细节，并向用户隐藏这些细节。而单纯的`%`或者`##`很可能忽略了这些细节，而我们不应该重复造这些轮子。只需要知道`dirname`和`basename`更可靠即可。

一般我们在使用`dirname`的时候，如果小心的话，还需要加上`--`，此举可以避免参数以`-`开头，被误当成选项带来的错误。
```
$(dirname -- $0)
```

### 附录 - 获取c语言练习基本路径的脚本
```bash
#!/bin/sh

#############################################################
# cd $(dirname $0)
# pwd
#
# 这两行脚本是进入shell脚本文件的好习惯，
# 因为大多数时候脚本是从各种奇葩的位置被执行的，
# 此命令可以确保进入到脚本的确切位置，
# 并且取得脚本绝对路径（不包括脚本本身文件名）
#
#
# 例如执行当前脚本的命令为：
# 	sh ./test.sh
# 执行结果显示的是脚本的绝对路径：
# 	/Users/Wei/github/letsplayc/sh/chapter5
#############################################################

#$0: 执行脚本时提供的路径（可能是绝对的也可能是相对的）
#比如：./test.sh
#既包括脚本所在文件夹路径，也包括脚本文件名及扩展名
zero=$0
echo "path=$zero"


#dirname函数负责找出它第一个参数的父级目录
#以脚本路径$0为参数，可以剥离出脚本的父级目录的路径
#比如：sh ./test.sh
#脚本路径$0=./test.sh
#dirname帮我们提取出./这个路径，去掉test.sh这个文件名
dir=$(dirname $0)
echo "dir=$dir"

#cd $(dirname $0): 进入这个目录(切换当前工作目录)
#pwd: 显示当前工作目录的绝对路径
#比如：sh ./test.sh
#$(dirname $0)帮我们过滤出路径./
#cd ./ 相当于进入这个父级目录
#再用pwd打出来的就是这个父级目录的绝对路径
#/Users/Wei/github/letsplayc/sh/chapter5
cd $(dirname $0)
pwd


#$(commend): 获得括号内的命令的输出流
#可以以此将找到的当前文件的绝对路径存在一个变量里
BASEPATH=$(cd $(dirname $0); pwd)

echo $BASEPATH


#############################################################
# 下面是两种方法，分别取路径的父级目录和文件名
# 对于#和##，以及%和%%用法的说明
# ${name%word}
# 从name的尾部开始删除与word匹配的最小部分，然后返回剩余部分
#
# ${name%%word}
# 从name的尾部开始删除与word匹配的最长部分，然后返回剩余部分
#
# ${name#word}
# 从name的头部开始删除与word匹配的最小部分，然后返回剩余部分
#
# ${name##word}
# 从name的头部开始删除与word匹配的最长部分，然后返回剩余部分
#############################################################
filename="/some/long/path/to/a_file"

# 两种方法取路径的父级目录"/some/long/path/to"
parentdir_v1="${filename%/*}"
parentdir_v2="$(dirname "${filename}")"

# 两种方法区路径的文件名部分"a_file"
basename_v1="${filename##*/}"
basename_v2="$(basename "${filename}")"

echo "${parentdir_v1}"
echo "${parentdir_v2}"
echo "${basename_v1}"
echo "${basename_v2}"


##############################################################
# 取父级目录和文件名最标准的做法
# 注意：
# 这里面的两个横杠--表示后面的字符串不用作命令选项（即使以-开头）
##############################################################
# 标准的取路径的父级目录"/some/long/path/to"
parentdir_std=$(dirname -- "${filename}")
# 标准的取文件名"a_file"
basename_std=$(basename -- "${filename}")



##############################################################
# 参考文献：
# https://unix.stackexchange.com/questions/253524/dirname-and-basename-vs-parameter-expansion
# https://stackoverflow.com/questions/23162299/how-to-get-the-last-part-of-dirname-in-bash
# https://blog.csdn.net/apache0554/article/details/47055827
# https://www.cnblogs.com/xuxm2007/archive/2011/10/20/2218846.html
##############################################################
```

### 参考文献
* [https://unix.stackexchange.com/questions/253524/dirname-and-basename-vs-parameter-expansion]
* [https://stackoverflow.com/questions/23162299/how-to-get-the-last-part-of-dirname-in-bash]
* [https://blog.csdn.net/apache0554/article/details/47055827]
* [https://www.cnblogs.com/xuxm2007/archive/2011/10/20/2218846.html]
