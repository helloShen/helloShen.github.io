---
layout: post
title: "How to Add Files to .gitignore list?"
date: 2017-07-02 21:45:49
author: "Wei SHEN"
categories: ["git"]
tags: ["problem"]
description: >
---

### 向`.gitignore`中添加要忽略的文件以后，文件还是继续被跟踪？
设置不跟踪某些文件很简单，就是在项目的根目录下创建`.gitignore`文件，然后在里面添加需要忽略的文件。

这里需要注意的是，添加到`.gitignore`列表中的文件，如果这个时候还没有被添加到`stage`暂存区区，git以后就不会再把这个文件添加到`stage`区，文件就不会被跟踪了。但注意：
> 如果添加到`.gitignore`里之前，文件已经被添加到`stage`暂存区，还需要手动将此文件从`stage`暂存区删除。否则系统还将继续跟踪此文件。

从`stage`暂存区删除`.idea/`目录下的所有文件，但不从`work area`删除，需要用到`rm`命令。`--cached`表示只删`stage`暂存区，不删`work area`。`-r`表示递归删除目录下的所有子目录和文件。
```bash
git rm -r --cached .idea/
```

删除以后，然后再执行`commit`的时候，就不会再追踪了。下图是，删除前，
![add-file-to-gitignore-2](/images/add-file-to-gitignore/add-file-to-gitignore-2.png)

下面是删除成功的过程，
![add-file-to-gitignore](/images/add-file-to-gitignore/add-file-to-gitignore.png)


> 如果文件被添加到`.gitignore`列表中之前，已经被提交到`git仓库`。就需要用`reset`命令来撤销提交。

```
git reset .
```

记住`reset`的三个层次，
* --soft: 只删`git仓库`，不删`stage`和`working area`。
* --mixed: 删`git仓库`和`stage`，不删`working area`。
* --hard: 同时删`git仓库`和`stage`以及`working area`.
