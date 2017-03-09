---
layout: post
title: "Build Blog on Github with Hexo static blog framework"
author: "Wei SHEN"
date: 2016-02-28 00:03:25
categories: ["front end"]
tags: ["hexo","github","blog","git"]
description: >
  基于Hexo搭建博客的命令和步骤。
---

### 本地部署Hexo
先装Hexo，让他在本地localhost先跑起来。推荐这个[**Hexo官方安装手册**](https://hexo.io/zh-cn/docs/)。主要有三个组件：     
1. **Node.js** （JavaScript解释器，女主的老爹）    
2. **git** （同步github和本地文件，接送女主的玛莎拉蒂）    
3. **Hexo** （女主）   


#### 安装 Node.js
安装*Node.js*的最佳方式是使用nvm，全称*Node Version Manager*。原因是node.js版本太多，需要nvm来切换。 下面打开Terminal，输入命令行，cURL或Wget，任选其一。
``` bash
$ curl https://raw.github.com/creationix/nvm/master/install.sh | sh
```
``` bash
$ wget -qO- https://raw.github.com/creationix/nvm/master/install.sh | sh
```

安装完成后，重启终端并执行下列命令即可安装 Node.js。    
``` bash
$ nvm install 4   //4代表版本号
```

#### 安装 Git
Windows：下载并安装 git.
Mac：使用 [**Homebrew**](http://brew.sh), [**MacPorts**](https://www.macports.org) 或下载 安装程序 安装。    
Linux (Ubuntu, Debian)：
``` bash
sudo apt-get install git-core
```
Linux (Fedora, Red Hat, CentOS)：
``` bash
sudo yum install git-core
```

#### 安装Hexo
安装完Node.js以及Git之后，用npm安装Hexo。    
``` bash
$ npm install -g hexo-cli
```

#### 初始化Hexo
本地创建一个名叫***Hexo***的文件夹。 bash上进到这个目录下，初始化Hexo。 所谓博客框架，hexo能够自动生成一个站点所需的基本组件。   
``` bash
$ cd yourpath/Hexo  //进入hexo目录
$ hexo init  //初始化
$ hexo g  //生成静态文,==hexo generate
$ hexo s  //启动服务预览，==hexo server
```
hexo默认IP是*`0.0.0.0`*， 端口*`4000`*。这时候我们浏览器里访问*`0.0.0.0：4000`*，看到hexo的原始外观（默认[**landscape**](https://github.com/hexojs/hexo-theme-landscape)主题），说明hexo在本地部署成功了。
![landscape](/images/githubHexo-1/landscape.png)

#### 换NexT主题
作为程序猿，我怎么能暴露选Hexo就是为了用他的NexT主题呢，太不专业了，么么哒。
[**NexT用户手册**](http://theme-next.iissnan.com)，有你想知道的关于NexT的一切，很详细。这里我列几个关键的步骤。

##### 先克隆NexT主题，
``` bash
$ cd your-hexo-site
$ git clone https://github.com/iissnan/hexo-theme-next themes/next
```
这时候在`hexo/themes`路径下就多了一个`next`文件夹。

##### 切换Hexo主题到NexT
打开hexo配置文件`hexo/_config.yml` ,找到`theme`字段，把`landscape`改成`next`。
``` bash
# Extensions
theme: next
```
刷新`0.0.0.0：4000`，就可以看到一个干净的多的页面。
![next-scheme1](/images/githubHexo-1/scheme1.png)

##### 切换NexT不同外观
NexT通过Scheme提供了三种不同的外观，同样可以通过配置文件切换。打开NexT配置文件*`hexo/themes/next/_config.yml`,找到`scheme`字段，在`Muse`，`Mist`，`Pisces`里挑一个吧。我更喜欢Mist。
![next-scheme-config](/images/githubHexo-1/schemeConfig.png)
Mist长这样。
![next-scheme2](/images/githubHexo-1/scheme2.png)

### 创建Github Page
如果没有github账号，先在[**Github**](www.github.com)上申请自己的账号。
登陆以后，点击加号创建新项目：     
![github_new_repo](/images/githubHexo-1/newRepo.png)

**!!注意：** 项目名称必须填：
```
yourusername.github.io
```
比如我github的账号是：`helloShen`，因此我的项目名称为`helloShen.github.io`，同时这也是之后我们访问空间的域名。在浏览器输入这个域名`yourusername.github.io`，就能看到默认页面了，因为太丑就不贴了。

### 在Github上部署Hexo
现在我们本地hexo文件夹里的就是我们站点的全部文件，github上也有一个空间了，接下来就要把本地网站同步到Github上去。Hexo网上有详细的[**Deployment Document**](https://hexo.io/docs/deployment.html)。方法有很多，这里就介绍一下最常用的git。
在github的项目页面，选择`HTTPS`的方式同步文件，复制文本框中的地址。
![httpsAddress](/images/githubHexo-1/httpsAddress.png)
打开hexo配置文件`hexo/_config.yml`,找到`deploy`字段，把`type`改成`git`，`repo`改成刚才复制的链接地址，`branch`填`master`如下图，
![deployment](/images/githubHexo-1/deployment.png)

保存后，Terminal里,在hexo根目录下输入：
``` bash
$ hexo d  //部署,==hexo deploy
```
回到github项目页面，此时本地站点文件已经完整同步到github项目根目录下。到这里，我们的域名`yourusername.github.io`已经可以正常访问了。

### 绑定自己的域名
如果你有一个自己的域名，Github Page是可以绑定自己域名的。没有的话，可以去"[**狗爹**](www.godaddy.com)"买一个，也就一包烟的钱。


#### 绑定DNS服务的IP地址
我们访问网站，实际访问的并不是他们的域名，域名其实只是个昵称。实际访问的是背后服务器的物理IP地址。`DNS`全名（Domain Name System）,就是告诉浏览器我们域名对应的IP地址的服务。域名刚买来是无法访问的，我们需要在购买域名的服务商那里设置我们域名背后指向的那个IP地址（见下图）。
![dnsIP](/images/githubHexo-1/dnsIP.png)
图中，我做了两个设置，
1. 把Github Pages实际的两个IP地址：`192.30.252.153`，`192.30.252.154`，绑定在我的域名上。
2. 之后，我可以给我的域名`ciaoshen.com`设置另一个别名，比如`www.ciaoshen.com`，这样无论用户输入哪一个，都会自动跳转到`192.30.252.153`，`192.30.252.154`这两个IP地址。

#### Github Page端绑定域名
光告诉浏览器我们网站实际的IP地址还不够，我们还要通知Github Page一下我们新的域名，这需要在项目的根目录下新建一个 **`CNAME`** 文件（必须大写），打开之后在上面直接输入一行域名，比如我的：`www.ciaoshen.com`。由于我们经常更新我们的网站，静态地写在Github Page项目里是不行的，必须在我们本地`hexo/source/`路径下创建一个 **`CNAME`** 文件，这样每次hexo都会自动生成他，同步到Github。是不是觉得网络访问机制很傻？没办法，伟大的事物就是这么任性。

设置完以后，点击下图中的`settings`按钮：
![httpsAddress](/images/githubHexo-1/httpsAddress.png)
可以看到，我的Github Page已经绑定到`www.ciaoshen.com`了。已经可以通过`www.ciaoshen.com`访问博客了。
![bandName](/images/githubHexo-1/bandName.png)

### 写博客
最基本的站点搭好了，下面试试最基本的写博客。Hexo用下面的命令生成一篇新博客。
``` bash
$ hexo new post <title>
```
之后在`hexo/source/`目录下就会找到一个名为`title.md`的新文件，之后用任意markdown编辑器就可以写博客了。个人比较推荐[**Mou**](http://25.io/mou/)和[**Byword**](https://bywordapp.com)。写完就可以发布了。
``` bash
$ hexo c  //清除缓存文件
$ hexo g  //生成静态页面
$ hexo d  //部署
```

### 啤酒和炸鸡
接下来就是最重要的一个步骤了：来一杯啤酒喝炸鸡，犒劳一下自己。据说缺少了这一步，网站不能好好运行，哈哈。
