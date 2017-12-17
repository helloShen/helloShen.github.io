---
layout: post
title: "How JSP Works"
date: 2017-12-15 22:20:15
author: "Wei SHEN"
categories: ["java","web"]
tags: ["jsp"]
description: >
---

![jsp-processing](/images/how-jsp-works/jsp-processing.jpg)

### 一张图看懂JSP是怎么工作的
以下步骤表明了 Web 服务器是如何使用JSP来创建网页的：
1. 就像其他普通的网页一样，您的浏览器发送一个 HTTP 请求给服务器。
2. Web 服务器识别出这是一个对 JSP 网页的请求，并且将该请求传递给 JSP 引擎。通过使用 URL或者 .jsp 文件来完成。
3. JSP 引擎从磁盘中载入 JSP 文件，然后将它们转化为 Servlet。这种转化只是简单地将所有模板文本改用 println() 语句，并且将所有的 JSP 元素转化成 Java 代码。
4. JSP 引擎将 Servlet 编译成可执行类，并且将原始请求传递给 Servlet 引擎。
5. Web 服务器的某组件将会调用 Servlet 引擎，然后载入并执行 Servlet 类。在执行过程中，Servlet 产生 HTML 格式的输出并将其内嵌于 HTTP response 中上交给 Web 服务器。
6. Web 服务器以静态 HTML 网页的形式将 HTTP response 返回到您的浏览器中。
最终，Web 浏览器处理 HTTP response 中动态产生的HTML网页，就好像在处理静态网页一样。

关键就在于第3步，如果对Servlet了解的话，JSP一下子就懂了：JSP文件会被翻译成Servlet，然后执行。然后Servlet这边就是通过Response（输出流）动态生成并返回HTML页面。一句话总结的很好，
> JSP是Servlet的扩展。就是用一种机器能懂的方式写Servlet。然后这种方式本身又能很好地嵌入到HTML标签中。
