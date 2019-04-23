---
layout: post
title: "Spring in Action - 4th Edition - Chapter 1"
date: 2019-04-20 22:05:58
author: "Wei SHEN"
categories: ["java", "spring", "gradle"]
tags: []
description: >
---

### 【春】晓
春眠不觉晓
处处闻啼鸟
夜来风雨声
花落知多少

### 1.1.1 `HelloWorld`例子

#### 代码地址 -> <https://github.com/helloShen/spring-in-action-4th-edition-demo/tree/master/ch01/src/main/java/com/ciaoshen/sia4/ch01/helloworld_111>
封装在`helloworld_111`包下，结构如下，
```
└── com
    └── ciaoshen
        └── sia4
            └── ch01
                └── helloworld_111
                    ├── HelloWorldBean.class
                    ├── HelloWorldMain.class
                    └── config
                        └── HelloWorldConfig.class
```

最简单的Spring应用。初始化管理bean的context容器，然后通过context容器调用bean。
* `HelloWorldBean`: 封装打印`Hello World`这个简单动作。
* `HelloWorldConfig`: 把`HelloWorldBean`标注成一个bean。
* `HelloWorldMain`: 初始化Spring容纳bean的context对象，然后通过这个context调用`HelloWorldBean`执行打印动作。

### 1.1.2 依赖注入，`Knight-Quest`例子

#### 代码地址 -> <https://github.com/helloShen/spring-in-action-4th-edition-demo/tree/master/ch01/src/main/java/com/ciaoshen/sia4/ch01/knights_112>
封装在`knights_112`包下，结构如下，
```
└── com
    └── ciaoshen
        └── sia4
            └── ch01
                └── knights_112
                    ├── BraveKnight.class
                    ├── Knight.class
                    ├── KnightMain.class
                    ├── Quest.class
                    ├── SlayDragonQuest.class
                    └── config
                        └── KnightConfig.class
```

一个最简单的依赖注入例子，目标是将骑士`Knight`类和他将要执行的任务`Quest`类解耦。具体方法是将`Quest`做为`Knight`的一个字段，并通过`Knight`的构造函数指定（注入）。

其中，`Knight`和`Quest`都是接口。具体实现类分别是`BraveKnight`和`SlayDragonQuest`。`Knight`的构造函数接受一个`Quest`型参数，来决定这个骑士专门执行什么任务。

具体执行类`KnightMain`只负责让一个`Knight`型的bean执行他的专属`Quest`。具体是哪个骑士，执行的是哪个任务，执行类完全不需要关心。全部由`KnightConfig`负责。它具体将`BraveKnight`注册成唯一的一个`Knight`型bean，并将另一个唯一的`Quest`型bean`SlayDragonQuest`注入其中。

至此无论是类的定义层面还是最后的执行层面，完全做到面向接口编程。`Knight`只需要知道他能执行`Quest`。最后是由spring（`KnightConfig`类）决定具体招募哪个骑士，并给他指派哪个任务。

#### 打包可执行JAR的时候遇到坑
详见 -> <http://www.ciaoshen.com/java/2019/04/20/about-executable-jar.html>

#### 用`knights.xml`配置失败
报错显示解析`src/main/resources/spring/knights.xml`的时候，`http://www.springframework.org/schema/beans/spring-beans-4.0.xsd`中找不到`beans`元素。

### 1.1.3 面向切面编程（AOP），`Minstrel`例子

#### 代码地址 -> <https://github.com/helloShen/spring-in-action-4th-edition-demo/tree/master/ch01/src/main/java/com/ciaoshen/sia4/ch01/minstrel_113>
封装在`minstrel_113`包下，

```
└── com
    └── ciaoshen
        └── sia4
            └── ch01
                └── minstrel_113
                    ├── BraveKnight.class
                    ├── Knight.class
                    ├── Minstrel.class
                    ├── MinstrelMain.class
                    ├── Quest.class
                    ├── SlayDragonQuest.class
                    └── config
                        └── MinstrelConfig.class
```

`Knight`和`Quest`的框架延续1.1.2的例子。目标是加入`Minstrel`切面类。负责在每个骑士执行`embarkOnQuest()`函数之前调用`Minstrel`的`singBeforeQuest()`函数，并在任务完成之后调用`singAfterQuest()`函数。

书上用的是XML配置法。之前XML配置法有问题，而且现在不常用了，所以该用AspectJ配置。先在`Minstrel`类里用AspectJ的`@Aspect`，`@Before`和`@After`注解定义切面。然后在`MinstrelConfig`配置类里，把`Minstrel`定义成除了`Knight`和`Quest`型之外的第三个bean。

至此，`Minstrel`只是spring中的一个普通bean。虽然被定义成了切面，但spring不会将他视为切面。它关于切面的所有注解不会被解析，也不会转换成切面代理。必须最后在`MinstrelConfig`类上打上`spring-context`包里的`@ComponentScan`和`@EnableAspectJAutoProxy`注解，告诉spring自动扫描切面类，并自动包装成切面代理。这时`Minstrel`的前置和后置通知才能被织入到`Knight-Quest`的事务中。

### 其他
第一章其他内容都是泛泛而谈。之后章节会具体介绍。
