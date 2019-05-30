---
layout: post
title: "Spring in Action 4th Edition - Chapter 5"
date: 2019-05-25 21:49:31
author: "Wei SHEN"
categories: ["spring", "java", "gradle"]
tags: ["controller", "mvc"]
description: >
---


### M-V-C三者的关系
* `M` = "Model"
* `V` = "View"
* `C` = "Controller"

一个Http请求过来之后，`DispatcherServlet`根据请求的信息找到对应的`Controller`。假设有数据层的情况，Controller从数据层查询数据之后（或许再进行计算）得到结果放在`Model`里，然后跳转到指定`View`。这时View就能从Model里拿到数据结果进行渲染。

### 5.2 Basic Controller
这一节的例子是个玩具。没有数据层实体类，测试时也不必真的启动服务器。例子中的代码只是简单配置了DispatcherServlet，然后创建了2个Controller来响应2中不同的请求。具体结构如下，
```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch05
    │   │                   └── basic_controller_52
    │   │                       └── spittr
    │   │                           ├── Spittle.java
    │   │                           ├── config
    │   │                           │   ├── RootConfig.java
    │   │                           │   ├── SpittrWebAppInitializer.java
    │   │                           │   └── WebConfig.java
    │   │                           ├── data
    │   │                           │   └── SpittleRepository.java
    │   │                           └── web
    │   │                               ├── HomeController.java
    │   │                               └── SpittleController.java
    │   └── resources
    │       └── static
    │           └── basic_controller_52
    │               └── spittr
    │                   ├── home.jsp
    │                   └── spittles.jsp
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch05
                            └── basic_controller_52
                                └── spittr
                                    └── HomeControllerTest.java

```

### 配置`DispatcherServlet`
`DispatcherServlet`是每个应用默认的唯一入口。要为它配置一个专属上下文，配置类需要继承`AbstractAnnotationConfigDispatcherServletInitializer`初始化工具类。 初始化`DispatcherServlet`对应的上下文的同时，还初始化了另一个全局上下文。那这两个上下文有什么区别的？

#### `DispatcherServlet`和`ContextLoaderListener`分别对应不同的上下文
先要明确一点：一个Spring应用可以有多个上下文。原则上是一个主从的关系，
1. 最多只能有一个“根”上下文
2. 可以有多个“子”上下文
3. 且任何一个子上下文可以访问根上下文的信息，反之则不行

`ContextLoaderListener`负责初始化的是那个唯一的“根”上下文。而`DispatcherServlet`可以有多个，每个对应一个单独的子上下文。

为什么说是“原则上”？因为“根”上下文不是必须的。完全可以把所有配置都集中到`DispatcherServlet`对应的子上下文。只有当有多个子应用，且有多个`DispatcherServlet`并且之间需要共享某些数据时才考虑创建“根”上下文。

传统的用XML配置中，无论`ContextLoaderListener`还是`DispatcherServlet`都可以在`web.xml`里配置。而书上是用一个继承了`AbstractAnnotationConfigDispatcherServletInitializer`类的配置类做这件事。而且把配置类拆分成了`RootConfig`和`WebConfig`两个配置类，分别对应“根”上下文和“子”上下文。

#### `WebMvcConfigurerAdapter`类被弃用
`WebMvcConfigurerAdapter`是`WebMvcConfigurer`接口的基本实现。继承`WebMvcConfigurerAdapter`就只需要重写部分有改动的方法即可。从Java 8开始接口允许可以有default默认实现，所以`WebMvcConfigurerAdapter`整个类被弃用。直接实现`WebMvcConfigurer`接口即可，效果一样。

### `@Controller`标记的控制器类
在`DispatcherServlet`的配置类`WebConfig`中开启`@EnableWebMvc`，应用就开启了对Spring MVC的支持。`/web`包下的`HomeController`和`SpittleController`负责对Http请求的调度。比如用户浏览器中访问`http://localhost:8080/spittles`，视图路径解析器会把`/spittles`解析成`/WEB-INF/views/spittles.jsp`，最终`spittles()`函数中的`Model`参数会被传递给`spittles.jsp`页面。`Model`实例中附带spittle列表数据就可以被显示在`spittles.jsp`的页面上。
```java
/**
 * 对 http://localhost:8080/spittles 发起GET请求时调用此方法
 * ViewResolver会把 "spittles" 解析成 "/WEB-INF/views/spittles.jsp"
 * 带有SpittleRepository实例的Mode对象会被传递给 spittles.jsp 页面
 */
@RequestMapping(method=GET)
public String spittles(Model model) {
    model.addAttribute(spittleRepository.findSpittles(Long.MAX_VALUE, 20));
    return "spittles";
}
```

#### 用`MockMvc`测试
单元测试类`HomeControllerTest`类用`MockMvc`模拟发出HTTP请求，这样测试就不需要启动服务器。实际测试中大部分组件都是模拟的，只有Controller是真实的。
* repository是模拟的：自动返回20个createSpittleList()函数预构建的Spittle
* view是模拟的：MockMvc对象中预设了假象的jsp路径 "/WEB-INF/views/spittles.jsp"（实际并不存在）
* 服务器是模拟的：MockMvc对象

### 5.3 pass parameters
例子沿用5.2节，做了点小改动，将`SpittleController`中查询当前所有spittle列表的函数，改为根据`id`查询单个spittle。
```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch05
    │   │                   └── with_parameter_53
    │   │                       └── spittr
    │   │                           ├── Spittle.java
    │   │                           ├── config
    │   │                           │   ├── RootConfig.java
    │   │                           │   ├── SpittrWebAppInitializer.java
    │   │                           │   └── WebConfig.java
    │   │                           ├── data
    │   │                           │   └── SpittleRepository.java
    │   │                           └── web
    │   │                               ├── HomeController.java
    │   │                               └── SpittleController.java
    │   └── resources
    │       └── static
    │           └── with_parameter_53
    │               └── spittr
    │                   ├── home.jsp
    │                   └── spittle.jsp
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch05
                            └── with_parameter_53
                                └── spittr
                                    └── HomeControllerTest.java
```    

首先`SpittleController`变了。通过在`@RequestMapping`里加入`value`属性给参数赋值。然后`spittle()`函数中通过`@PathVariable`拿到参数。

其次，拿到要查询的ID，`SpittleRepository`要加一个`findOne()`接口方法，用来根据ID查询单条信息。

`spittle.jsp`页面也要改成显示单条信息。

最后再修改`HomeControllerTest`测试类，模拟查询单条消息的场景。

### 5.4 提交表单
主要是`SpitterController`这个控制器。`/spitter/register`的`GET`请求对应`registerForm.jsp`页面，让用户填写注册表单。提交表单到`/spitter/register`的`POST`请求储存用户信息，并跳转到`/spitter/${username}`，这里`${username}`是刚注册的用户名，对应视图为`profile.jsp`。

测试类`SpitterControllerTest`中照例数据层`SpitterRepository`和HTTP请求都是`mockito`模拟的。不需要启动服务器，也不需要考虑数据库。只考察控制器能不能根据发来的模拟HTTP请求做出正确的调度。
```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch05
    │   │                   └── post_form_54
    │   │                       └── spittr
    │   │                           ├── Spitter.java
    │   │                           ├── config
    │   │                           │   ├── RootConfig.java
    │   │                           │   ├── SpittrWebAppInitializer.java
    │   │                           │   └── WebConfig.java
    │   │                           ├── data
    │   │                           │   └── SpitterRepository.java
    │   │                           └── web
    │   │                               └── SpitterController.java
    │   └── resources
    │       └── static
    │           └── post_form_54
    │               └── spittr
    │                   ├── profile.jsp
    │                   └── registerForm.jsp
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch05
                            └── post_form_54
                                └── spittr
                                    └── SpitterControllerTest.java
```
测试中最后一个断言是错的，因为根据表单提交之后，`SpitterController`的`processRegistration(Spitter spitter)`函数拿到的`Spitter`实例是`MockMvc`创建的，不是测试类中创建的`Spitter unsaved`。
```java
@Test
public void shouldShowRegistration() throws Exception {
    SpitterRepository mockRepository = mock(SpitterRepository.class);
    Spitter unsaved = new Spitter("jbauer", "24hours", "Jack", "Bauer");
    Spitter saved = new Spitter(24L, "jbauer", "24hours", "Jack", "Bauer");
    when(mockRepository.save(unsaved)).thenReturn(saved);

    SpitterController controller = new SpitterController(mockRepository);
    MockMvc mockMvc = standaloneSetup(controller).build();

    mockMvc.perform(post("/spitter/register")
        .param("firstName", "Jack")
        .param("lastName", "Bauer")
        .param("username", "jbauer")
        .param("password", "24hours"))
        .andExpect(redirectedUrl("/spitter/jbauer"));

    /**
     * 这个测试通不过
     * processRegistration(Spitter spitter)函数拿到的参数是mockMvc根据表单提交的参数构造的
     * 和unsave不是一个对象
     */
    verify(mockRepository, atLeastOnce()).save(unsaved);
}
```

### 参考文献
* 第五章官方源码参考 -> <https://github.com/habuma/spring-in-action-4-samples/tree/master/Chapter_05>
* 整个`WebMvcConfigurerAdapter`被弃用了 -> <https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation/WebMvcConfigurerAdapter.html>
* `ContextLoaderListener`是干什么用的 -> <https://stackoverflow.com/questions/11815339/role-purpose-of-contextloaderlistener-in-spring>
* 比较`ContextLoaderListener`和`DispatcherServlet` -> <https://howtodoinjava.com/spring-mvc/contextloaderlistener-vs-dispatcherservlet/>
* 另外一篇比较`ContextLoaderListener`和`DispatcherServlet` -> <http://www.codesenior.com/en/tutorial/Spring-ContextLoaderListener-And-DispatcherServlet-Concepts>
* 关于Spring MVC中的竟态资源 -> <https://juejin.im/entry/57fd9713bf22ec0064a80ee7>
