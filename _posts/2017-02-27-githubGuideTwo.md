---
layout: post
title: "Visual Reference for How Git Commands Work"
date: 2017-02-27 23:51:36
author: "Wei SHEN"
categories: ["tools"]
tags: ["git"]
description: >
---

From: <https://marklodato.github.io/visual-git-guide/index-zh-cn.html>

### 基本用法

![1](/images/githubGuideTwo/1.png)

上面的四条命令在工作目录、暂存目录(也叫做索引)和仓库之间复制文件。

  * `git add _files_` 把当前文件放入暂存区域。
  * `git commit` 给暂存区域生成快照并提交。
  * `git reset -- _files_` 用来撤销最后一次`git add _files_`，你也可以用`git reset` 撤销所有暂存区域文件。
  * `git checkout -- _files_` 把文件从暂存区域复制到工作目录，用来丢弃本地修改。

你可以用 `git reset -p`, `git checkout -p`, or `git add -p`进入交互模式。

也可以跳过暂存区域直接从仓库取出文件或者直接提交代码。

![2](/images/githubGuideTwo/2.png)

  * `git commit -a` 相当于运行 `git add` 把所有当前目录下的文件加入暂存区域再运行。`git commit`.
  * `git commit _files_` 进行一次包含最后一次提交加上工作目录中文件快照的提交。并且文件被添加到暂存区域。
  * `git checkout HEAD -- _files_` 回滚到复制最后一次提交。

### 约定

后文中以下面的形式使用图片。

![3](/images/githubGuideTwo/3.png)

绿色的5位字符表示提交的ID，分别指向父节点。分支用橘色显示，分别指向特定的提交。当前分支由附在其上的`_HEAD_`标识。 这张图片里显示最后5次提交，`_ed489_`是最新提交。 `_master_`分支指向此次提交，另一个`_maint_`分支指向祖父提交节点。

### 命令详解

#### Diff

有许多种方法查看两次提交之间的变动。下面是一些示例。

![4](/images/githubGuideTwo/4.png)

#### Commit

提交时，git用暂存区域的文件创建一个新的提交，并把此时的节点设为父节点。然后把当前分支指向新的提交节点。下图中，当前分支是`_master_`。 在运行命令之前，`_master_`指向`_ed489_`，提交后，`_master_`指向新的节点`_f0cec_`并以`_ed489_`作为父节点。

![5](/images/githubGuideTwo/5.png)

即便当前分支是某次提交的祖父节点，git会同样操作。下图中，在`_master_`分支的祖父节点`_maint_`分支进行一次提交，生成了`_1800b_`。 这样，`_maint_`分支就不再是`_master_`分支的祖父节点。此时，合并 (或者 衍合) 是必须的。

![6](/images/githubGuideTwo/6.png)

如果想更改一次提交，使用 `git commit --amend`。git会使用与当前提交相同的父节点进行一次新提交，旧的提交会被取消。

![7](/images/githubGuideTwo/7.png)

另一个例子是分离HEAD提交,后文讲。

#### Checkout

checkout命令用于从历史提交（或者暂存区域）中拷贝文件到工作目录，也可用于切换分支。

当给定某个文件名（或者打开-p选项，或者文件名和-p选项同时打开）时，git会从指定的提交中拷贝文件到暂存区域和工作目录。比如，`git checkout HEAD~ foo.c`会将提交节点`_HEAD~_`(即当前提交节点的父节点)中的`foo.c`复制到工作目录并且加到暂存区域中。（如果命令中没有指定提交节点，则会从暂存区域中拷贝内容。）注意当前分支不会发生变化。

![8](/images/githubGuideTwo/8.png)

当不指定文件名，而是给出一个（本地）分支时，那么_HEAD_标识会移动到那个分支（也就是说，我们“切换”到那个分支了），然后暂存区域和工作目录中的内容会和_HEAD_对应的提交节点一致。新提交节点（下图中的a47c3）中的所有文件都会被复制（到暂存区域和工作目录中）；只存在于老的提交节点（ed489）中的文件会被删除；不属于上述两者的文件会被忽略，不受影响。

![9](/images/githubGuideTwo/9.png)

如果既没有指定文件名，也没有指定分支名，而是一个标签、远程分支、SHA-1值或者是像_master~3_类似的东西，就得到一个匿名分支，称作_detached HEAD_（被分离的_HEAD_标识）。这样可以很方便地在历史版本之间互相切换。比如说你想要编译1.6.6.1版本的git，你可以运行`git checkout v1.6.6.1`（这是一个标签，而非分支名），编译，安装，然后切换回另一个分支，比如说`git checkout master`。然而，当提交操作涉及到“分离的HEAD”时，其行为会略有不同，详情见在下面。

![10](/images/githubGuideTwo/10.png)

#### HEAD标识处于分离状态时的提交操作

当`_HEAD_`处于分离状态（不依附于任一分支）时，提交操作可以正常进行，但是不会更新任何已命名的分支。(你可以认为这是在更新一个匿名分支。)

![11](/images/githubGuideTwo/11.png)

一旦此后你切换到别的分支，比如说`_master_`，那么这个提交节点（可能）再也不会被引用到，然后就会被丢弃掉了。注意这个命令之后就不会有东西引用`_2eecb_`。

![12](/images/githubGuideTwo/12.png)

但是，如果你想保存这个状态，可以用命令`git checkout -b _name_`来创建一个新的分支。

![13](/images/githubGuideTwo/13.png)

#### Reset

reset命令把当前分支指向另一个位置，并且有选择的变动工作目录和索引。也用来在从历史仓库中复制文件到索引，而不动工作目录。

如果不给选项，那么当前分支指向到那个提交。如果用`--hard`选项，那么工作目录也更新，如果用`--soft`选项，那么都不变。

![14](/images/githubGuideTwo/14.png)

如果没有给出提交点的版本号，那么默认用`_HEAD_`。这样，分支指向不变，但是索引会回滚到最后一次提交，如果用`--hard`选项，工作目录也同样。

![15](/images/githubGuideTwo/15.png)

如果给了文件名(或者 `-p`选项), 那么工作效果和带文件名的`checkout`差不多，除了索引被更新。

![16](/images/githubGuideTwo/16.png)

#### Merge

merge 命令把不同分支合并起来。合并前，索引必须和当前提交相同。如果另一个分支是当前提交的祖父节点，那么合并命令将什么也不做。 另一种情况是如果当前提交是另一个分支的祖父节点，就导致_fast-forward_合并。指向只是简单的移动，并生成一个新的提交。

![17](/images/githubGuideTwo/17.png)

否则就是一次真正的合并。默认把当前提交(`_ed489_` 如下所示)和另一个提交(`_33104_`)以及他们的共同祖父节点(`_b325c_`)进行一次三方合并。结果是先保存当前目录和索引，然后和父节点_33104_一起做一次新提交。


![18](/images/githubGuideTwo/18.png)

#### Cherry Pick

cherry-pick命令"复制"一个提交节点并在当前分支做一次完全一样的新提交。

![19](/images/githubGuideTwo/19.png)

#### Rebase

衍合是合并命令的另一种选择。合并把两个父分支合并进行一次提交，提交历史不是线性的。衍合在当前分支上重演另一个分支的历史，提交历史是线性的。 本质上，这是线性化的自动的 cherry-pick

![20](/images/githubGuideTwo/20.png)

上面的命令都在_topic_分支中进行，而不是_master_分支，在_master_分支上重演，并且把分支指向新的节点。注意旧提交没有被引用，将被回收。

要限制回滚范围，使用`--onto`选项。下面的命令在_master_分支上重演当前分支从_169a6_以来的最近几个提交，即_2c33a_。

![21](/images/githubGuideTwo/21.png)

同样有`git rebase --interactive`让你更方便的完成一些复杂操作，比如丢弃、重排、修改、合并提交。没有图片体现这些，细节看这里:[git-rebase(1)][http://www.kernel.org/pub/software/scm/git/docs/git-rebase.html#_interactive_mode]

### 技术说明

文件内容并没有真正存储在索引(`_.git/index_`)或者提交对象中，而是以blob的形式分别存储在数据库中(`_.git/objects_`)，并用SHA-1值来校验。 索引文件用识别码列出相关的blob文件以及别的数据。对于提交来说，以树(`_tree_`)的形式存储，同样用对于的哈希值识别。树对应着工作目录中的文件夹，树中包含的 树或者blob对象对应着相应的子目录和文件。每次提交都存储下它的上一级树的识别码。

如果用detached HEAD提交，那么最后一次提交会被the reflog for HEAD引用。但是过一段时间就失效，最终被回收，与`git commit --amend`或者`git rebase`很像。
