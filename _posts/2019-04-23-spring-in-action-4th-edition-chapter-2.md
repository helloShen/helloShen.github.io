---
layout: post
title: "Spring in Action - 4th Edition - Chapter 2"
date: 2019-04-23 17:32:16
author: "Wei SHEN"
categories: ["java", "spring"]
tags: ["dependency injection"]
description: >
---

### 摘要
* `@Component`和`@ComponentScan`实现用注解自动扫描bean组件。`@Component`直接在类上标注，`@ComponentScan`扫描并初始化所有带`@Component`的组件。
* XML也可以开启自动扫描，把`@ComponentScan`注解换成XML的`<context:component-scan>`标签即可。
* 不自动扫描需要在配置类里用`@Bean`注解手动标注。
* XML也可以手动标注，也有`<bean>`标签，但现在不常用了。看看就行。


### 2.2 Annotation自动装配的例子

#### 代码地址：<https://github.com/helloShen/spring-in-action-4th-edition-demo/tree/master/ch02>
`annotation_autowired_22`包结构如下，
```
└── src
    ├── main
    │   └── java
    │       └── com
    │           └── ciaoshen
    │               └── sia4
    │                   └── ch02
    │                       └── annotation_autowired_22
    │                           └── soundsystem
    │                               ├── CDPlayerConfig.java
    │                               ├── CompactDisc.java
    │                               └── SixPense.java
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch02
                            └── annotation_autowired_22
                                └── soundsystem
                                    └── CDPlayerTest.java
```    

自动装备关键靠`spring-context`包下的`@Configuration`和`@ComponentScan`注解。bean组件比如`SixPense`类先用`@Component`注解标注。然后`@ComponentScan`注解开启bean组件自动扫描，专门查找并实例化被`@Component`注解标注过的组件。

测试类`CDPlayerTest`配置context时用`@ContextConfiguration`给出`@ContextConfiguration(classes=CDPlayerConfig.class)`配置类名称。要运行`CDPlayerTest`测试类，可以用JUnit的`@RunWith`注解标注，gradle的`build`任务会自动运行测试。注意这里用到的Runner是`spring-test`包下的`SpringJUnit4ClassRunner`。


### 2.2 XML自动装配的例子
尝试用XML文件来启动spring的组件自动扫描功能。

#### 代码地址： <https://github.com/helloShen/spring-in-action-4th-edition-demo/tree/master/ch02>
`xml_autowired_22`包结构如下，
```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch02
    │   │                   └── xml_autowired_22
    │   │                       └── soundsystem
    │   │                           ├── CompactDisc.java
    │   │                           └── SixPense.java
    │   └── resources
    │       ├── application.properties
    │       └── xml_autowired_22
    │           └── soundsystem.xml
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch02
                            └── xml_autowired_22
                                └── soundsystem
                                    └── CDPlayerTest.java
```

和`annotation_autowired_22`的做法类似，bean组件也要用`@Component`注解标注。只是配置类`soundsystem/CDPlayerConfig.java`改成`src/main/resources/`包下的`soundsystem.xml`配置类。在里面用`<context:component-scan>`标签替代`@ComponentScan`标签。

XML配置文件`soundsystem.xml`放在`src/main/resources/`目录下，这个路径默认包含在gradle的`sourceSets.main.runtimeClasspath`路径集合里，很方便找。为了不搞混，增设了`xml_autowired_22`一级子目录。

XML配置的话，`@ContextConfiguration`注解属性要改成`@ContextConfiguration("classpath:/xml_autowired_22/soundsystem.xml")`XML配置文件的路径。注意路径前要加上`classpath:`前缀。

### 2.3 JavaConfig手动装配的例子

#### 代码地址：<https://github.com/helloShen/spring-in-action-4th-edition-demo/tree/master/ch02>
`javaconfig_23`包结构如下，
```
└── src
    ├── main
    │   └── java
    │       └── com
    │           └── ciaoshen
    │               └── sia4
    │                   └── ch02
    │                       └── javaconfig_23
    │                           └── soundsystem
    │                               ├── CDPlayer.java
    │                               ├── CDPlayerConfig.java
    │                               ├── CompactDisc.java
    │                               ├── MyOldClassmate.java
    │                               └── SixPense.java
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch02
                            └── javaconfig_23
                                └── soundsystem
                                    └── CDPlayerTest.java
```

之前负责自动装备的`@Component`和`@ComponentScan`注解全部去掉。改为在`CDPlayerConfig`里用`@Bean`注解直接注册bean组件。声明的时候，还可以带上`name`属性，方便在出现多个同类型bean组件的时候指定特定的bean。

注册完bean组件之后，同样在`CDPlayerTest`测试类里加上`@Autowired`标签以智能装配合适的bean组件。

像下面这种方式引用其他的bean是一种优良实践。spring会自动推断`compactDisc`合适的实例。并且不会反复调用`CompactDisc`类的构造器，spring装配的始终是上下文中的那个bean单例。
```java
@Bean
public CDPlayer cdPlayer(CompactDisc compactDisc) { // spring会推断出合适的bean来装配
    return new CDPlayer(compactDisc);
}
```

还有像下面这种方式获得的也是bean单例，
```java
@Bean
public CompactDisc sixPense() {
    return new SixPense();
}

@Bean
public CDPlayer cdPlayer() {
    return new CDPlayer(sixPense()); // spring拦截对SixPense()构造函数的调用，始终返回单例。
}
```
