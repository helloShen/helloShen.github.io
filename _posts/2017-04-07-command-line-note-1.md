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

主要参考下面三本书：
1. 《The Linux Command Line》(俗称TLCL)：主要了解Shell命令行。
2. 《Linux程序设计-第4版》：配合下面的《Linux内核设计》，有知识点不清楚的情况，当工具书查阅。
3. 《Linux内核设计-第3版》：同上

另外网上还有一些不错的简明教程，推荐下面两个，直接了当，不繁文缛节。
[**极客学院Shell教程**](http://wiki.jikexueyuan.com/project/shell-tutorial/shell-brief-introduction.html)
[**IBM-学习Linux命令行**](https://www.ibm.com/developerworks/cn/linux/l-lpic1-v3-103-1/)

另外`learnshell.org`网站，可以边学边动手做练习。Leetcode的Shell部分只有4题，之能拿`learnshell.org`练手。

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

### 变量和符号的展开
#### `$`展开变量
Shell是弱类型语言，声明变量的时候，不需要定义变量类型。基本都是当成字符串处理。要使用的时候带上美元符`$`，解释器就会展开，
```bash
value=bitch
echo $value
bitch
```
**！注意：** 变量名和等号之间不能有空格。

关于变量名的规约如下，
* 首个字符必须为字母`（a-z，A-Z）`。
* 中间不能有空格，可以使用下划线`_`。
* 不能使用标点符号。
* 不能使用bash里的关键字（可用help命令查看保留关键字）。

#### 分词，切割
Shell对输入单词的切割比较奇怪，比如像下面这个写法，解释器会默认将`$1`识别成输入的参数之一来展开。如果当前没有输入参数，则输出空缺。
```bash
echo total is $1.00
total is .00
```
字符串也一样，下面这样紧挨着，系统是不分词的，会以为变量名就是`$aaaHello`
```bash
aaa=Hello
echo $aaaRonald

```
想帮助解释器认清变量的边界，有两种方法。第一种，在变量外面加`${}`，如下所示。**推荐每个变量都用这种写法，避免歧义。** 不要为了省一个花括号导致不必要的麻烦。
```bash
echo ${aaa}Ronald
HelloRonald
```
第二种，给字符串加上单引号或者双引号，
```bash
echo $aaa"Ronald"
HelloRonald
```
```bash
echo $aaa'Ronald'
HelloRonald
```

当然加个空格解释器也能能正确区分需要展开的变量和普通字符串的边界。但输出的两个单词之间也会加空格。这些细节看上去很简单，但实际上每种语言在处理这些细节都有自己的风格。
```bash
aaa=Hello
echo $aaa Ronald
Hello Ronald
```

#### 展开函数结果
可以用`$()`把一个函数括起来，可以将函数输出作为一个变量使用。
```bash
echo $(ls)
```

也可以用两个倒引号把一个函数括起来，
```bash
echo `ls`
```

#### 展开算数表达式
用`$(())`括起来的内容表示一个数学计算。
```bash
echo $((2 + 2))
4
```

#### 模式展开
`{}`可以批量替换某个模式。
```bash
echo Front-{A,B,C}-Back
Front-A-Back Front-B-Back Front-C-Back
```

#### 路径展开
`\~`可以展开成当前用户的根目录，
```bash
echo ~
/Users/Wei
```

#### 转义符
`\`: 转义符。(转义符的思想来自C语言)

#### 双引号禁止绝大部分展开
> `""`: 双引号禁止除了参数符`$`,反斜杠转义符`\`和倒引号之外的所有展开。

上面这条非常重要。没有读到正式定义之前，很多人都有这个困扰。

下面的例子展示了双引号管不住带`$`符的变量展开。
```bash
aaa=Hello
bbb=Hi
merged="$aaa Wei $bbb Wei"
```
当然也管不住加花括号的变量`${}`，
```bash
aaa=Hello
echo "${aaa}"
Hello
```

而且双引号还管不住`$(())`这样的计算展开，
```bash
echo "$((3+3))"
6
```
基本是沾美元符`$`就管不住。

下面的例子展示了双引号管不住反斜杠`\`将后面的字符转意。
```bash
echo "\nRonald"

Ronald
```
至于管不住倒引号，也想不出什么例子来说明。


#### 单引号禁止所有展开
`''`: 单引号是最强的引用等级，禁止所有展开。

下面是一个单引号和双引号的对比，双引号管不住`$`,`${}`变量，`$(())`计算，`\`转意。单引号能关注所有。
```bash
echo text ~/*.txt {a,b} $(echo foo) $((2+2)) $USER
text /home/me/ls-output.txt a b foo 4 me
echo "text ~/*.txt {a,b} $(echo foo) $((2+2)) $USER"
text ~/*.txt {a,b} foo 4 me
echo 'text ~/*.txt {a,b} $(echo foo) $((2+2)) $USER'
text ~/*.txt {a,b} $(echo foo) $((2+2)) $USER
```

### 运行Shell脚本有两种方法
#### 第一种，作为可执行程序
比如我有一个最基本输出`Hello World!`的脚本，保存为`test.sh`。
```bash
#!/bin/bash
echo "Hello World !"
```
开头`#!/bin/bash`告诉系统用哪个解释器来执行脚本。

接下来，只需要给脚本添加执行权限，就可以当一个可执行程序执行了。因为类UNIX系统本质上 **一切都是文件**。因此可执行程序也可以看成一个普通的储存二进制流的文本文件。

```bash
chmod +x ./test.sh  #使脚本具有执行权限
./test.sh  #执行脚本
```
如果将`.`当前工作路径添加到`$PATH`环境变量，可以省略`./`。而且扩展名`.sh`实际上并不影响脚本的执行，所以直接`test`加回车就可以了。
```bash
test
Hello World !
```
添加环境变量`$PATH`可以用`export`命令，
```bash
export PATH=$PATH:.:..
```
如果想永久扩充`$PATH`变量，需要把这行写入`~/.profile`文件。Mac系统下，对应的是`~/.bash_profile`文件。注意以上两个文件只对单个用户有效。

也可以用`/etc/environment`来设置环境变量，操作系统在登录时使用的第一个文件就是`/etc/environment`文件。但它不接受命令，只能直接为变量赋值，比如，
```bash
PATH="$JAVA_HOME:$PATH:..:."
```
如果要让环境变量设置立即生效，需要运行下面命令，
```bash
source /etc/environment
```
需要注意，Mac系统没有`/etc/environment`文件，此方法不适用。

另外，注意以上的方法对`bash`适用，但对`zsh`不适用。`zsh`的配置文件在`~/.zshrc`。同样是添加`export`命令，
```bash
export PATH=$PATH:.:..
```

#### 第二种，作为解释器参数
这种运行方式是，直接运行解释器，其参数就是shell脚本的文件名，如：
```bash
/bin/sh test.sh
/bin/php test.php
```
这种方式运行的脚本，不需要在第一行指定解释器信息，写了也没用。

### Shell支持数组
> array_name=(value1 value2 value3 value4 value5)

```bash
test_array=(aaa bbb ccc ddd eee)
```
也可以用下标直接赋值，而且不要求下标连续。
```bash
test_array[10]=iii
```
访问也是用下标来访问，
```bash
echo $test_array[1]
aaa
echo ${test_array[1]}
aaa
```

### `#`获取变量长度
下面代码，获取字符串变量长度，
```bash
str=Hello
echo ${#str}
5
```
还可以获取数组长度，还是刚才`test_array`的例子，
```bash
test_array=(aaa bbb ccc ddd eee)
test_array[10]=iii
echo ${#test_array}
10
```
数组长度由它的最后一个非空元素的位置决定。中间没有赋值的元素都为空。

### 向函数，命令，脚本传递参数
* `$1`: 第一个参数
* `$2`: 第二个参数
* `$3`: 第三个参数
* ... 以此类推
* `$0`: 脚本本身(脚本的文件名)
* `$#`: 参数的数量

比如我的脚本分别打印`$0`,`$@`和`$#`，取名为`run`。
```bash
#! /bin/sh
echo $0
echo $@
echo $#
```
运行命令，
```bash
run aaa bbb ccc ddd eee fff
```
得到如下结果，
```bash
run
aaa bbb ccc ddd eee fff
6
```

### 命令列表

#### 开胃菜
* pwd: 打印出当前工作目录名
* cd: 更改目录
* ls: 列出目录内容
* date: 显示日期
* cal: 显示日历
* df: 查看磁盘剩余空间
* free: 显示剩余空间数量
* exit: 关闭终端窗口


#### 在文件系统里畅游
* cd: 更改目录
* ls: 列出目录内容
    * -l: 完整信息
    * -t: 按时间先后排序
    * -r: 反向排序
* file: 确定文件类型
* less: 浏览文件内容

#### 创建，移动和删除文件
* cp: 复制文件和目录
* mv: 移动/重命名文件和目录
* mkdir: 创建目录
* rm: 删除文件和目录
* ln: 创建硬链接和符号链接

#### 获得命令的信息
* type: 说明怎样解释一个命令名
* which: 显示会执行哪个可执行程序
* man: 显示命令手册页
* apropos: 显示一系列适合的命令
* info: 显示命令info
* whatis: 显示一个命令的简洁描述
* alias: 创建命令别名


#### IO重定向
* `>`: 重新定向输出（删除原内容）
* `>>`: 重新定向输出（在原文件后面接着写）
* `<`: 重新定向标准输入
* `|`: 管道线(pipeline)，从标准输出读取内容，再送到标准输入。实现像过滤器一样的管道输送。
* 标准输入文件描述符: `0`
* 标准输出文件描述符: `1`
* 标准错误文件描述符: `2`. 所以`2>`表示重新定向标准错误。
* `cat`: 读取文件，然后复制到标准输出。可以读取多个文件，并且之间没有分页。可以用来连接多个文件。
* `sort`: 排序文本行
* `uniq`: 报道或省略重复行
* `grep`: 打印匹配行
* `wc`: 打印文件中换行符，字，和字节个数
* `head`: 输出文件第一部分
* `tail`: 输出文件最后一部分

#### 符号展开
* `*`: 字符展开（表示正则表达式的匹配任意字符）
* `~`: 路径展开。
* `$((expression))`: 算数表达式展开，可以做加减乘除的运算。
* `{}`: 模式展开。可以用来批量生成目录和文件。
* `$`: 变量展开。
* `$()`: 函数输出作为变量。
* `""`: 双引号禁止除了参数符`$`,反斜杠转义符`\`和倒引号之外的所有展开。
* `''`: 单引号是最强的引用等级，禁止所有展开。
* `\`: 转义符。(转义符的思想来自C语言)

#### 参数
* `$1`: 第一个参数
* `$2`: 第二个参数
* `$3`: 第三个参数
* ... 以此类推
* `$0`: 脚本本身(脚本的文件名)
* `$#`: 参数的数量

### 练习

#### Hello, World!
* Use the "echo" command to print the line "Hello, World!".

代码如下，
```bash
#!/bin/bash
# Text to the right of a '#' is treated as a comment - below is the shell command
echo 'Hello, World!'
```

#### Variables
* The target of this exercise is to create a string, an integer, and a complex variable using command substitution. The string should be named BIRTHDATE and should contain the text "Jan 1 2000". The integer should be named Presents and should contain the number 10. The complex variable should be named BIRTHDAY and should contain the full weekday name of the day matching the date in variable BIRTHDATE e.g. Saturday. Note that the 'date' command can be used to convert a date format into a different date format. For example, to convert date value, $date1, to day of the week of date1, use:

```bash
date -d "$date1" +%A
```

解答如下，可以用`$()`
```bash
#!/bin/bash
# Change this code
BIRTHDATE='Jan 1 2000'
Presents=10
BIRTHDAY=$(date -d "$BIRTHDATE" +%A)
```
也可以用两个倒引号，
```bash
#!/bin/bash
# Change this code
BIRTHDATE="Jan 1 2000"
Presents=10
BIRTHDAY=`date -d "$BIRTHDATE" +%A`
```

#### Array
* In this exercise, you will need to add numbers and strings to the correct arrays. You must add the numbers 1,2, and 3 to the "numbers" array, and the words 'hello' and 'world' to the strings array.
You will also have to correct the values of the variable NumberOfNames and the variable second_name. NumberOfNames should hold the total number of names in the NAMES array, using the $# special variable. Variable second_name should hold the second name in the NAMES array, using the brackets operator [ ]. Note that the index is zero-based, so if you want to access the second item in the list, its index will be 1.

```bash
#!/bin/bash
NAMES=( John Eric Jessica )

# write your code here
NUMBERS=( 1 2 3 )
STRINGS=( hello world )
NumberOfNames=${#NAMES[@]}   # 注意这里的@
second_name=${NAMES[1]}
```

#### Basic Operators
* In this exercise, you will need to calculate to total cost (variable TOTAL) of a fruit basket, which contains 1 pineapple, 2 bananas and 3 watermelons. Don't forget to include the cost of the basket....

```bash
#!/bin/bash
COST_PINEAPPLE=50
COST_BANANA=4
COST_WATERMELON=23
COST_BASKET=1
TOTAL=$(($COST_PINEAPPLE + 2 * $COST_BANANA + 3 * $COST_WATERMELON + $COST_BASKET))
echo "Total Cost is $TOTAL"
```

#### Basic String Operations
* In this exercise, you will need to change Warren Buffett's known saying. First create a variable ISAY and assign it the original saying value. Then re-assign it with a new changed value using the string operations and following the 4 defined changes: Change1: replace the first occurrence of 'snow' with 'foot'. Change2: delete the second occurrence of 'snow'. Change3: replace 'finding' with 'getting'. Change4: delete all characters following 'wet'. Tip: One way to implement Change4, if to find the index of 'w' in the word 'wet' and then use substring extraction.

注意，`expr index`不能匹配字符串。它找的是任意一个char的第一次出现的位置。
`expr index "abc" "b"`找的是`b`在`abc`里第一次出现的位置。`expr index "abc" "ab"`找的是`a`或者`b`在`abc`里第一次出现的位置。

```bash
#!/bin/bash

BUFFETT="Life is like a snowball. The important thing is finding wet snow and a really long hill."
# write your code here
ISAY=$BUFFETT
ISAY=${ISAY[@]/snow/foot}
ISAY=${ISAY[@]/snow/""}
ISAY=${ISAY[@]/finding/getting}
pos=$(expr index "$ISAY" 'w')  
ISAY=${ISAY:0:$(($pos+2))}
```
