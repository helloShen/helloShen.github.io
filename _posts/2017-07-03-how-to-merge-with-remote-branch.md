---
layout: post
title: "How to Merge with a Remote Branch"
date: 2017-07-03 17:57:49
author: "Wei SHEN"
categories: ["git"]
tags: ["problem"]
description: >
---

### `git pull`或者`git fetch`+`git merge FETCH_HEAD`都可以
`git pull`等同于`git fetch`+`git merge FETCH_HEAD`。
* `git fetch origin master`：把远程某项目的`master`分支(比如github)拷贝到本地一个叫`FETCH_HEAD`的分支。
* `git merge FETCH_HEAD`：合并本地当前分支和`FETCH_HEAD`分支。

`pull`同时完成以上两步。相当于一个快捷键。

### `merge`的时候出现冲突怎么办？
比如两边的分支都加了`file.txt`文件。但本地的是旧版本，远程仓库的是新版本，
```
             ----remotemaster(add "file.txt[V2]")
            /
    ----commonbase----localmaster(add "file.txt[V1]")
```
发生冲突以后，就需要直接从`FETCH_HEAD`仓库拷贝比较新的`file.txt[V2]`到本地`work space`。可以用`checkout`命令，
```
git checkout FETCH_HEAD -- file.txt
```
然后再分别提交到`stage`和`仓库`，
```
git add file.txt
git commit -m "fix conflict of file.txt"
```

`checkout`命令的操作如下图所示，
![checkout-command](/images/merge-with-remote-branch/checkout-command.png)
