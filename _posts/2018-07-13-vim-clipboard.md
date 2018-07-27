---
layout: post
title: "解决Mac OS上vim无法复制粘贴的问题"
date: 2018-07-13 18:07:03
author: "Wei SHEN"
categories: ["linux"]
tags: ["vim"]
description: >
---

### 问题
vim用`y`复制，`p`粘贴一段选中内容。但这仅限于在单个vim窗口中。离开vim编辑器，到其他编辑器，甚至跨越多个vim窗口都不能奏效。而且也无法和传统的`ctrl-c`加`ctrl-v`混用。

### 解决方法
如果你vim的`y`，`d`，`x`和`p`不能当`ctrl-c`和`ctrl-v`用的话，要分两种情况。先用下面命令确定你属于哪一种，
```bash
vim --version | grep clipboard
```

#### 情况一，vim支持clipboard
如果结果里你找到加号开头的`+clipboard`， 恭喜你，你的vim没问题，是你姿势问题。
* 用`"+y` 代替`y`将选中的内容复制到系统剪贴板，效果和ctrl-c一致。
* 用`"+p`代替`p`将剪贴板内容复制到指定位置，也可以用ctrl-v。

`d`，`x`，`c`，`s`也一样，用之前前面加`"+`。

如果想偷懒用y直接把内容复制到系统剪贴板，需要到vim配置文件`.vimrc`里加一行属性。用下面命令开始配置，
```bash
vim ~/.vimrc
```
然后，加入下面这行，
```bash
set clipboard=unnamed
```
现在你的`y`，`d`，`x`，`p`已经能和 `ctrl-c`和`ctrl-v` 一个效果，并且能互相混用。

### 情况二，
如果找到的是负号开头的`-clipboard`，说明你的vim不支持系统剪切板，我的MacOS系统自带vim就不支持，所以跑来了。需要先重新安装vim，

Linux系统，
```
sudo apt install vim-gtk
```

MacOS，
```
brew install vim
```

安装好之后，重复情况一的操作即可。

### 背后的原理
问题解决了，几个细节再解释一下，满足一下好奇心。
首先，**vim的寄存器（Register）有好几个(Vim documentation: change)。** 最常用的默认寄存器`""`叫 **【未命名寄存器（unnamed register）】**。最近一次的`y`，`d`，`s`，`x`，`c`删除，修改，复制内容都默认存放在这里。

```
""      // 默认unnamed寄存器，最近一次"d","c","s","x","y"复制，删除，修改内容

"0      // 最近一次"y"复制内容

"1      // 最近一次"d","c","s","x"删除，修改内容
"2     //  上一次"d","c","s","x"删除，修改内容
"3     // 上上次"d","c","s","x"删除，修改内容
...     
"9      // [1-9]数字以此类推


"a     // 字母寄存器，供用户指定使用，比如"ay就是复制到"a寄存器
"b
...
"z


"-      // 少于一行的"d","c","x"删除内容

".      // 只读寄存器
":      // 只读寄存器
"%     // 只读寄存器
"#     // 只读寄存器

"+      // 系统剪贴板 (有的版本vim不支持)
"*      // 系统剪贴板 (有的版本vim不支持)
```

而ctrl-c以及ctrl-v用到的是 **系统剪贴板（system clipboard）**。vim寄存器和系统剪贴板不是一个东西。顾名思义，vim寄存器的数据作用域仅限于vim本地，甚至如果开多个vim窗口，每个窗口都有一套自己完整的寄存器，互相不影响。而系统剪贴板作为系统级别的全局变量，两边当然不能混用。
所以vim专门提供了`"+`寄存器作为对系统剪贴板的映射。可以理解成自动把`"+`寄存器的内容再复制一份到系统剪贴板，前提是你得把clipboard属性设置成打开。有些版本（比如MacOS自带的vim）就不支持这个映射。重装vim就是为了打开这个开关。（如果有简便的直接改设置方法，请纠正我）。打开以后用`"+y`命令把内容复制到和系统剪贴板关联的寄存器`"+`上。而y只是复制到默认无名寄存器`""`上。
最后`set clipboard=unnamed`就是把默认无名寄存器`""`和系统剪贴板也关联上。 就是用`y`也可以备份到系统剪贴板。缺点是破坏了默认寄存器`""`的本地性。因为`p`操作也会被等同于`"+p`处理，粘贴的是`"+`寄存器的内容， 粘贴的时候`""`默认寄存器内容就会被覆盖。 表现出来的就是复制一次，到任意vim窗口都可以粘贴。但这个特性恰恰是很多人想要的。
还有个细节，大部分系统上`"+`和`"*`是等价的。但在有的系统上不等价，比如Linux，
* `"+`：对应ctrl-c和ctrl-v用到的系统剪贴板：desktop clipboard (XA_SECONDARY)
* `"*`：对应图形界面中鼠标框选的内容（可以用鼠标中键黏贴）：X11 primary selection (XA_PRIMARY)

所以看到`"*`也不要慌，试试看用`"*y`和`"*p`能不能复制粘贴。可以的话就说明是混用的，不行就老老实实用`"+`。

以上。遇到此坑的同学了解一下。


### 参考文献
* <https://stackoverflow.com/questions/3961859/how-to-copy-to-clipboard-in-vim>
* <https://www.zhihu.com/question/19863631>
* <http://vim.wikia.com/wiki/Accessing_the_system_clipboard>
* <https://www.jianshu.com/p/270a5013808b>
* <http://vimdoc.sourceforge.net/htmldoc/change.html#quotequote>
