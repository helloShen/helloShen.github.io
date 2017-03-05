---
layout: post
title: "To Build Blog on Github using Jekyll and Bootstrap Framework"
date: 2017-02-27 16:44:19
author: "Wei SHEN"
categories: ["Front_End","Framework"]
tags: ["HTML","CSS","Javascript","JQuery","Jekyll","Bootstrap","Sass","Responsive_Design"]
description: >
---

### Bootstrap
Bootstrap官方上手文档：http://v3.bootcss.com/getting-started/。在下面这个链接下载Bootstrap：http://v3.bootcss.com/getting-started/#examples。 有两个版本，一个预编译版，一个源代码版。源码版需要自己先把LESS编译成CSS。最简单的是直接把预编译版包含到项目根目录即可。

预编译版文件结构如下，可以看到有压缩过(*.min.*)和没有压缩过的css和javascript文件，以及glyphicons字体文件。所以Bootstrap到底是什么？很简单，是一套成熟的布局框架。网页的栅格系统，以及导航，菜单这些组件也都准备好，封装成有特定id或class的组件。供不懂设计的程序员随时取用。省去了自己编写css和js，找字体的时间，而且保证美观。
```bash
bootstrap/
├── css/
│   ├── bootstrap.css
│   ├── bootstrap.min.css
│   ├── bootstrap-theme.css
│   └── bootstrap-theme.min.css
├── js/
│   ├── bootstrap.js
│   └── bootstrap.min.js
└── fonts/
    ├── glyphicons-halflings-regular.eot
    ├── glyphicons-halflings-regular.svg
    ├── glyphicons-halflings-regular.ttf
    └── glyphicons-halflings-regular.woff
```

### Jekyll
Jekyll是一个能把Markdown（或者Textile）格式的文本转换成静态html页面的静态博客生成框架。实际上Jekyll做了两件事：

1. 把Markdown格式的文本内容，转换成用html和css渲染的内容。
2. Jekyll还利用Liquid模板语言，批量排版渲染好的文章内容。

简单讲Jekyll帮我们做了这两件是到底有多好，只要一条命令，直接可以从一系列markdown写好的文章，自动转换成一个静态网站。完全不需要数据库。从此博主能够专注于markdown内容的写作。如果再加入Bootstrap框架，我们还可以自行设计网站的排版布局，但又完全不用担心陷入扣css的泥潭。

#### 安装
跟着官方文档一步步走：http://jekyllcn.com/docs/installation/
Jekyll是Ruby写的，没有的话还是先装Ruby
```bash
brew install ruby
```
然后装Ruby Gem。Gem是Ruby程序包管理器，一个gem命令就可以安装ruby其他扩展库。从官网下载最新版：https://rubygems.org/pages/download
解压以后，终端进入下载的文件包根目录，运行命令安装，
```bash
ruby setup.rb
```

然后还要安装Ruby Bundler。 Bundler帮我们自动下载Jekyll依赖哪些第三方包，他自动帮你下载安装多个包，并且会下载这些包依赖的包。
```bash
gem install bundler
```

然后安装Node.js服务器。下载地址：https://nodejs.org/en/
还需要Python：https://www.python.org/downloads/

现在开始安装Jekyll,
```bash
gem install jekyll
```

最后Mac用户还要装Xcode和Command line tool. 运行命令，
```
xcode-select --install
```

#### 开始
安装好Jekyll之后，初始化一个Jekyll站点项目很简单。先进入你希望放置项目的根目录，
```bash
cd your-folder
```
在此根目录下，创建新项目，
```bash
jekyll new your-project-name
```

此时已经有一个默认站点，输入命令运行jekyll自带服务器，就可以在本地localhost:4000端口预览站点，
```bash
jekyll serve
```

刚初始化的项目默认使用minima主题。初始文件结构如下，
```bash
.
├── Gemfile
├── Gemfile.lock
├── _config.yml
├── _posts
│   └── 2016-12-21-welcome-to-jekyll.markdown
├── _site
│   ├── about

│   │   └── index.html
│   ├── assets
│   │   └── main.css
│   ├── feed.xml
│   ├── feed.xslt.xml
│   ├── index.html
│   └── jekyll
│       └── update
│           └── 2016
│               └── 12
│                   └── 21
│                       └── welcome-to-jekyll.html
├── about.md
└── index.md
```
这里面主干内容其实只有四个：
```bash
.
├── _config.yml
├── _posts
├── _site
└── index.html
```
**_config.yml**: 主配置文件
**_posts**: 我们的博客markdown文本存在这里
**_site**: 生成的站点全在里面
**index.html**: 站点主入口

#### 怎么用Jekyll
这里面细节太多，没时间写了，文档里都有[**《Jekyll官方中文文档》**](http://jekyllcn.com/docs/home/)。记录几个关键点：

##### Liquid模板语言
Jekyll用Liquid模板语言批量处理markdown转换成的html页面。这是Jekyll的两大核心之一。下面是一段我的代码：头部的YAML文件可以用来定义一些数据，比如这里的list里定义了我要批处理的几个章节。后面的代码就会到这几个文件夹下抓我写过的markdown内容，转换成html以后加载在我的页面上。

另一大核心从markdown渲染html页面，这个只要知道Jekyll替我们做了就行，不必深究。

```
---
layout: main
title: "Thinking in Java 读书笔记"
list: ['chapter11','chapter12','chapter13','chapter14','chapter15','chapter16','chapter17','chapter18','chapter19','chapter20','chapter21']
---

  {% assign sorted = site.posts | sort:"num" %}
  {% for item in page.list %}
  <section class="article">
  <div class="container">
  <div class="row">
      {% for post in sorted %}
      {% if post.categories contains item and post.categories contains "note" %}
      <div class="col-md-6 description">
        <h2><a href="{{ site.baseurl }}{{ post.url }}">{{ post.title }} </a></h2>
	<blockquote><p>{{ post.description }} <p class="small"><a href="{{ site.baseurl }}{{ post.url }}">阅读全文 &raquo;</a></p></p></blockquote>
      </div>
      {% endif %}
      {% endfor %}

      {% for post in sorted %}
      {% if post.categories contains item and post.categories contains "topics" %}
      <div class="col-md-6 description">
	      <h2><a href="{{ site.baseurl }}{{ post.url }}">{{ post.chapter }} 专题 - {{ post.title }} </a></h2>
	      <blockquote><p>{{ post.description }} <p class="small"><a href="{{ site.baseurl }}{{ post.url }}">阅读全文 &raquo;</a></p></p></blockquote>
      </div>
      {% endif %}
      {% endfor %}
  </div>
  </div>
  </section>

  <section class="exercises">
  <div class="container">
  <div class="row">
      <h3><p class="exercise-title">{{ item }} 习题 </p></h3>
  </div>
  <div class="row">
      {% for post in sorted %}
      {% if post.categories contains item and post.categories contains "exercises" %}
      <div class="col-md-2">
	      <p><a class="exerciseLink" href="{{ site.baseurl }}{{ post.url }}"><b> {{ post.title }} &raquo;</b></a></p>
      </div>
      {% endif %}
      {% endfor %}
  </div>
  </div>
  </br>
  </section>
  {% endfor %}
```

##### Bootstrap怎么嵌入
只要把Bootstrap的css和js和fonts文件夹放到Jekyll项目的目录下面就行。Jekyll都会把他们自动添加到最后生成的_site文件夹下面。比如我就把他们放到了assets文件夹下。**注意，文件名前不能加下划线，这是Jekyll内部留给Collection的命名空间。** 然后在html页面里直接导入css文件和js文件就行。

##### Jekyll可以自设Collection
Jekyll默认的集合是叫posts。在根目录下有个`_posts`文件夹。可以用`for xxx in site.posts`这样的语句来获得`_posts`文件夹下的所有文章。所以初始的`_posts`，`_includes`，`_sites`这些都是Jkeyll初始默认的集合。除此以外，我们也可以自己创建新的集合。比如在根目录下新建一个`_collectionName`文件夹，然后，在`_config.yml`文件里把`collectionName`加到collections属性里。以后`_collectionName`文件夹里的文章都可以一个集合的形式被读取。

##### 文章分类的三种方法

1. 前面讲的新建Collection的方法。
2. 通过在markdown文件头的YAML配置属性里加上categories或者tags这样的属性，然后用`post.categories = xxx`这样的语句来读取。
3. 比较投机取巧的方法是`/A/B/_posts/`路径下的所有文章，都会自动被加入两个属性categories = [A,B]。

##### `_config.yml`配置文件
Jekyll用的是YAML作为数据格式。下面是我站点的配置文件。

```
### site info
title: Jekyll + Bootstrap Demo
email: symantec__@hotmail.com
description: >
  《Thinking in Java》读书笔记
twitter_username: wei Shen
github_username:  helloShen

### theme
# theme: minima

### Serving
detach:  false
port:    4000
host:    127.0.0.1
baseurl: /jekyll_bootstrap_demo # the subpath of your site, e.g. /blog
url: http://ciaoshen.com # the base hostname & protocol for your site, e.g. http://example.com
show_dir_listing: false

### Conversion
markdown:    kramdown
highlighter: rouge
lsi:         false
excerpt_separator: "\n\n"
incremental: false
encoding: utf-8

### kramdown
kramdown:
  syntax_highlighter: rouge
  auto_ids:       true
  footnote_nr:    1
  entity_output:  as_char
  toc_levels:     1..6
  smart_quotes:   lsquo,rsquo,ldquo,rdquo
  input:          GFM
  hard_wrap:      false
  footnote_nr:    1

### Where things are
source:       .
destination:  ./_site
# plugins_dir:  _plugins
layouts_dir:  _layouts
# data_dir:     _data
includes_dir: _includes
collections:
  posts:
    output:   true


### others
gems:
  - jekyll-feed
exclude:
  - Gemfile
  - Gemfile.lock
```

#### 语法高亮
GitHub强制使用kramdown，以及rouge语法高亮装饰器。下面这段是`_config.yml`配置文件中关于解析格式的配置，
```
### Conversion
markdown:    kramdown
highlighter: rouge
lsi:         false
excerpt_separator: "\n\n"
incremental: false
encoding: utf-8

### kramdown
kramdown:
  syntax_highlighter: rouge
  auto_ids:       true
  footnote_nr:    1
  entity_output:  as_char
  toc_levels:     1..6
  smart_quotes:   lsquo,rsquo,ldquo,rdquo
  input:          GFM
  hard_wrap:      false
  footnote_nr:    1
```

rouge的原理很简单，jekyll在把markdown文件解析成html的时候，代码里会插入rouge风格的标签。就是说代码中的语法成分已经被标注起来了，只需要配合一个和这些rouge风格标签配套的css样式表，就能完成语法的高亮着色。下面的清单显示了Jekyll解析生成的html文件中的一段代码，

```html
<div class="language-java highlighter-rouge"><pre class="highlight"><code>
<span class="cm">/**
 * MIT Licence
 * Copyright (C) &lt;2016&gt; &lt;Wei SHEN&gt;
 * @version 1.0.0
 * @author Wei SHEN
 * @website www.ciaoshen.com
 * @contact symantec__@hotmail.com
 */</span>
<span class="kn">package</span> <span class="n">com</span><span class="o">.</span><span class="na">ciaoshen</span><span class="o">.</span><span class="na">thinkinjava</span><span class="o">.</span><span class="na">chapter15</span><span class="o">;</span>
<span class="kn">import</span> <span class="nn">java.util.*</span><span class="o">;</span>
<span class="kd">class</span> <span class="nc">Dog</span> <span class="kd">extends</span> <span class="n">Pet</span><span class="o">{</span><span class="kd">public</span> <span class="n">String</span> <span class="nf">toString</span><span class="o">(){</span><span class="k">return</span> <span class="s">"A Dog!"</span><span class="o">;}}</span>


</code></pre>
</div>
```

部署`rouge`很简单:
```bash
gem install rouge
```

用`rouge`自带的命令生成css样式表文件，放置在项目css样式表文件的目录下，我这里用的是monokai风格，
```bash
rougify style monokai > /your-project-stylesheets-path/rouge.css
```

然后在布局模板里插入对css布局文件的引用，
```html
<link href="{{ site.baseurl }}/assets/css/rouge.css" rel="stylesheet">
```

但原生的`rouge`高亮主题偏亮，只能用黑色背景。博客代码区还是用白色底色比较干净。这时候，我们可以选用`pygments`的高亮主题。因为`pygments`使用的语法标签和`rouge`是兼容的，只需要下载`pygments`的css文件，比如像下面这样，
```css
.py.err { color: #ac4142 } /* Error */
.py.k { color: #aa759f } /* Keyword */
.py.l { color: #d28445 } /* Literal */
.py.n { color: #151515 } /* Name */
```
只需要把`py`全部改成`highlight`，像下面这样，这个`pygments`的css文件就变成了一个`rouge`的css文件，
```css
.highlight .err { color: #ac4142 } /* Error */
.highlight .k { color: #aa759f } /* Keyword */
.highlight .l { color: #d28445 } /* Literal */
.highlight .n { color: #151515 } /* Name */
```
最后再把新的css文件导入html文件即可。
```html
<link href="{{ site.baseurl }}/assets/css/jekyll-github.css" rel="stylesheet">
```

需要获取更多的支持`rouge`的css高亮主题，可以访问：
CSS themes for Rouge/Pygments syntax highlighter: <https://github.com/helloShen/jekyll-pygments-themes>

#### 插入图片
先在根目录下创建新文件夹，比如我取名叫uploads。然后把要引用的图片都放在里面。然后在markdown文件里我对图片是这样引用的，
```
![extends](/jekyll_bootstrap_demo/uploads/tij4-15/lowerBounds.png)
```

Jekyll会把这种格式解析成html图片标签，
```html
<img src="/jekyll_bootstrap_demo/uploads/tij4-15/lowerBounds.png" alt="extends" />
```

要控制图片自适应屏幕大小，可以用weight="100%"属性。但因为有Bootstrap，这一步也可以省了，因为Bootstrap原生样式表里已经帮我们定义好一个类叫"img-responsive"，来妥帖地处理图片大小。我们需要做的就是用js来插入这段类的声明，具体代码如下，
```javascript
$(window).load(function(){

    $("img").addClass("img-responsive center-block");

})
```

#### 分页
几篇重要的参考文献：
1. Bootstrap的分页器模板：http://v4-alpha.getbootstrap.com/components/pagination/
2. Jekyll分页功能文档：http://jekyllrb.com/docs/pagination/
3. Liquid模板语言文档：http://shopify.github.io/liquid/

需要先安装插件`jekyll-paginate`。然后再配置`_config.yml`文件，jekyll会自动生成分页以后的主页`index.html`。

##### 安装`jekyll-paginate`
在`_config_yml`配置文件加一行：
```
gems:
  - jekyll-paginate
```

在`Gemfile`加一行：
```
gem 'jekyll-paginate'
```

然后运行命令行，
```
bundle
```

##### 配置`_config.yml`文件
在`_config.yml`配置文件中，`paginate`设置每页显示多少posts。`paginate_path`设置分页的目标路径。
```
paginate: 50
paginate_path: "/blog/page:num/"
```
比如，上面的设置就是说：每页显示50篇文章，所以`/blog/index.html`会显示前50篇文章，然后`blog/page2/index.html`会显示50-100篇文章。以此类推。

结合`Bootstrap`的分页控件，以及`Liquid`的模板语言，最后我的分页控件代码如下，

```html
<nav aria-label="Page navigation example">
  <ul class="pagination justify-content-center">
    <li id="liPrev" class="page-item">
      <a id="aPrev" class="page-link" href="{{ site.baseurl }}{{ paginator.previous_page_path }}">
          Previous
      </a>
    </li>
    {% if paginator.page == 1 %}
        <script>
            $("li#liPrev").addClass("disabled");  // 页数为1，禁用previous按钮
        </script>
    {% endif %}

    <li id="li1" class="page-item"><a class="page-link" href="{{ site.baseurl }}/index.html">1</a></li>
    {% if paginator.page == 1 %}
        <script>
            $("li#li1").addClass("active");  // 当前页按钮高亮
        </script>
    {% endif %}
    {% for i in (2..paginator.total_pages) %}
        <li id="li{{ i }}" class="page-item"><a class="page-link" href="{{ site.baseurl }}/blog/page{{ i }}/index.html">
            {{ i }}
        </a></li>
        {% if paginator.page == i %}
            <script>
                $("li#li{{ i }}").addClass("active");  // 当前页按钮高亮
            </script>
        {% endif %}
    {% endfor %}
    <li id="liNext" class="page-item">
      <a class="page-link" href="{{ site.baseurl }}{{ paginator.next_page_path }}">Next</a>
    </li>
    {% if paginator.page == paginator.total_pages %}
        <script>
            $("li#liNext").addClass("disabled");  // 最后一页，禁用next按钮
        </script>
    {% endif %}
  </ul>
</nav>
```
