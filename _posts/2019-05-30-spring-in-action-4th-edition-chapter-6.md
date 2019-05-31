---
layout: post
title: "Spring in Action 4th Edition - Chapter 6"
date: 2019-05-30 22:10:39
author: "Wei SHEN"
categories: ["java", "spring"]
tags: ["jsp", "thymeleaf"]
description: >
---

# Spring in Action - 4th Edition - Chaper 6 Note

### 摘要
本章讲视图解析。主要讲3个主题，
1. JSP视图
2. Tiles视图定义布局
3. Thymeleaf模板

### JSP视图
视图解析器`InternalResourceViewResolver`负责将逻辑视图名，比如`home`解析为物理视图的路径比如`/WEB-INF/views/home.jsp`。这样就能顺利找到JSP文件。

前几章的例子都用到了`InternalResourceViewResolver`，像下面这样在Spring容器中声明称一个bean。
```java
@Bean
public ViewResolver viewResolver() {
    InternalResourceViewResolver resolver = new InternalResourceViewResolver();    
    resolver.setPrefix("/WEB-INF/views/");
    resolver.setSuffix(".jsp");
    return resolver;
}
```
正常的JSP解析器这样就行了，如果要解析带JSTL标签的JSP，还要再给解析器一个`Local`对象（可以从`JstlView`对象中获得）。
```java
resolver.setViewClass(org.springframework.web.servlet.view.JstlView.class);
```

然后在JSP文件中，有两个可用标签库，
* JavaServer Pages Standard Tag Library (JSTL, JSP标准标签库)
* Spring提供的两个JSP标签库（一个专用于表单，另一个通用标签库）

标签库的作用，比如用于绑定表单的Spring标签库，负责将表单中的输入数据绑定到Spring MVC暴露给JSP页面的对象中对应的域。比如在JSP页面中，Spring绑定表单的标签库这么声明，其中`prefix="sf"`属性，不一定要用`sf`，什么缩写都可以。
```
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
```
Spring通用标签库这么声明，这里的`prefix="s"`同样随便用什么缩写。
```
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
```

项目代码中，`ch06/jsp`目录下是两个例子，演示了怎么用带Spring表单标签库的JSP页面。`registerForm_rightBeside`将错误信息直接显示在表单输入栏右边。`registerForm_label`将错误信息集中显示在表单输入栏的上方。
```
.
├── README.md
└── jsp
    ├── css
    │   ├── registerForm_label.css
    │   └── registerForm_rightBeside.css
    ├── java
    │   └── Spitter.java
    ├── jsp
    │   ├── registerForm_label.jsp
    │   └── registerForm_rightBeside.jsp
    └── prop
        └── ValidationMessages.properties
```
这里面`.css`样式表被独立出来了，都在`/css`目录下。只需要在`<head>`区声明JSP页面用到的CSS样式表文件即可。
```html
<link rel="stylesheet" type="text/css"
      href="<c:url value="css/registerForm_label.css" />" >
```

另一个注意点，错误检查找到错误之后打印的消息被集中到`/prop`目录中的`ValidationMessages.properties`文件中。错误检查命令直接标注在`Spitter`类的域上。比如，下面`message`属性定义了如果`Spitter.firstName`域长度不符合要求，对应的视图中会显示`ValidationMessages.properties`文件里定义的`firstName.size`属性。
```java
@NotNull
@Size(min=2, max=30, message="{firstName.size}")
private String firstName;
```

### Tiles布局
Tiles布局就是说把一个复杂的JSP页面经常复用的部分，比如`header`，`footer`独立出来。到具体的某个JSP页面，拿来组装比较节省时间。例子都在`ch06/tile`子目录下，
```
└── tile
    ├── jsp
    │   ├── footer.jsp
    │   ├── header.jsp
    │   ├── home.jsp
    │   └── page.jsp
    └── xml
        └── tiles.xml
```
其中`tiles.xml`是元素，以及组装规则的定义。`header`和`footer`这种就是最小粒度复用布局。最终`home.jsp`是由`header.jsp`，`page.jsp`和`footer.jsp`拼装起来的一个页面。

### Thymeleaf模板
用Spring标签库的缺点是会污染HTML文件，导致很难阅读。如果没有JSP解释器的渲染成HTML，代码对只认识HTML标签的浏览器是无意义的。并且JSP规范是个Servlet规范紧密耦合的，因此只能用在基于Servlet的Web应用中。这点Thymeleaf的代码和原生HTML更接近，而且Thymeleaf也可用于非Servlet的Web应用。

要在Spring里用Thymeleaf要导入3个组件，
1. Thymeleaf视图解析器
2. 模板引擎
3. 模板解析器

```java
// Thymeleaf视图解析器
@Bean
public ViewResolver viewResolver(SpringTemplateEngine templateEngine) {
    ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
    viewResolver.setTemplateEngine(templateEngine);
    return viewResolver;
}

// 模板引擎
@Bean
public templateEngine templateEngine(TemplateResolver templateResolver) {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);
    return templateEngine;
}

// 模板解析器
@Bean
public TemplateResolver templateResolver() {
    TemplateResolver templateResolver = new TemplateResolver();
    templateResolver.setPrefix("/WEB-INF/templates/");
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode("HTML5");
    return templateResolver;
}
```

例子中`registerForm.html`就是一个Thymeleaf表单模板。
```
├── thymeleaf
    └── registerForm.html
```
