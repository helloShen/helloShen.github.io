---
layout: post
title: "Leetcode - Algorithm - Shell Tenth Line "
date: 2017-04-14 19:35:31
author: "Wei SHEN"
categories: ["shell","leetcode"]
tags: [""]
level: "easy"
description: >
---

### 题目
How would you print just the 10th line of a file?

For example, assume that file.txt has the following content:
```
Line 1
Line 2
Line 3
Line 4
Line 5
Line 6
Line 7
Line 8
Line 9
Line 10
```
Your script should output the tenth line, which is:
```
Line 10
```

### 几种错误的解法
#### 用`head`和`tail`
先选取前十行，再选取最后一行。当文件不满十行的时候发生错误，只打印最后一行。
```bash
head -10 file.txt | tail -1
```

#### 用`cat -n`
`cat -n`可以显示出每一行的行号。然后再用`grep`正则匹配数字`10`。这明显不行，第`100`行也被显示，而且不是行的原始信息，前面还带了一个行号。
```bash
cat -n file.txt | grep 10
```

### `sed`命令处理文件
`sed -n`表示按行处理文件。`10p`中`10`代表第十行，`p`代表print打印，连起来就是只打印第十行。
```java
sed -n '10p' file.txt
```

#### 结果
![shell-tenth-line-1](/images/leetcode/shell-tenth-line-1.png)


### `awk`命令
这个先不急，以后再说。

#### 代码
```java

```

#### 结果
![shell-tenth-line-2](/images/leetcode/shell-tenth-line-2.png)
