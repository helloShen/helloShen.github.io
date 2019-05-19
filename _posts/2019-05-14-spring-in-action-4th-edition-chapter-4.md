---
layout: post
title: "Spring in Action 4th Edition - Chapter 4"
date: 2019-05-14 17:04:41
author: "Wei SHEN"
categories: ["spring", "gradle"]
tags: ["aop"]
description: >
---

### 4.3 使用注解创建切面，`@Before`和`@After`通知类型
例子中的业务主体是`Performance`接口和它的实现类`GuoDeGangZhuanChang`（郭德纲相声专场）。切面类是`Audience`，从观众的视角在终端呈现这场演出。具体结构如下，
```
└── src
    ├── main
    │   └─ java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch04
    │   │                   └── aop_before_after_43
    │   │                       ├── concert
    │   │                       │   ├── Audience.java
    │   │                       │   ├── GuoDeGangZhuanChang.java
    │   │                       │   └── Performance.java
    │   │                       └── config
    │   │                           └── AopBeforeAfterConfig.java
    │   └── resources
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch04
                            └── aop_before_after_43
                                └── TestAopBeforeAfter.java
```

具体说就是`GuoDeGangZhuanChang`类内部有一个《你字系列》相声的节目单。`perform(int id)`函数根据传入的`id`节目号来表演节目。`GuoDeGangZhuanChang`内部有一个`stage`字段，为了模拟表演节目，`perform(int id)`函数就把这个`stage`字段设置为当前表演的节目名称。

`Audience`中定义了两个“通知”，所谓“通知”就是定义切面类要做什么事:
1. `@Before`前置增强：表演前的报幕
2. `@AfterReturning`后置增强：打印出当前表演的节目名称，以及表演结束后观众的喝彩。

“切点”的定义如下，所谓“切点”就相当于对“连接点”的搜索语句。下面这个切点就是说当`Performance`类的`perform(int id)`函数被调用的时候切入。函数的参数`id`作为参数传递给两个“通知”函数。
```java
@Pointcut("execution(* com.ciaoshen.sia4.ch04.aop_before_after_43.concert.Performance.perform(int)) " +
          "&& args(id)")
public void performance(int id) {}
```

在测试类`TestAopBeforeAfter`中在节目单中随机抽取一半节目表演。输出如下，
```
com.ciaoshen.sia4.ch04.aop_before_after_43.TestAopBeforeAfter > testAopBeforeAfter STANDARD_OUT
    下面是郭德纲《你字系列》专场第9个节目:
        >>>《你压力大吗》
            ~~~吁吁吁吁吁吁吁吁~~~
    下面是郭德纲《你字系列》专场第5个节目:
        >>>《你好新北京》
            ~~~吁吁吁吁吁吁吁吁~~~
    下面是郭德纲《你字系列》专场第1个节目:
        >>>《你要高雅》
            ~~~吁吁吁吁吁吁吁吁~~~
    下面是郭德纲《你字系列》专场第4个节目:
        >>>《你要做善人》
            ~~~吁吁吁吁吁吁吁吁~~~
    下面是郭德纲《你字系列》专场第2个节目:
        >>>《你得娶我》
            ~~~吁吁吁吁吁吁吁吁~~~
```

`Audience`类中用到了2种获得横切类（被切的`GuoDeGangZhuanChang`类）参数和数据的方法。
1. 第一种用`&& args(id)`将`perform(int id)`的参数`id`传递给“通知”函数`announcement()`和`onStage()`
2. 后一个“通知”函数`onStage()`还通过参数拿到了`JoinPoint`型的“连接点”对象引用。再通过`target()`函数可以直接拿到被代理的横切类`GuoDeGangZhuanChang`对象的引用，用来查看当前舞台在表演哪个节目。

### 4.3 使用`@Around`通知类型
和`aop_before_after_43`做了同一件事情，只不过换成了`@Around`环绕增强。最重要的一个变化就是在传递连接点参数的时候用`ProceedingJoinPoint`替代`JoinPoint`（注意，不可同时使用两者）。
```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch04
    │   │                   └── aop_around_43
    │   │                       ├── concert
    │   │                       │   ├── Audience.java
    │   │                       │   ├── GuoDeGangZhuanChang.java
    │   │                       │   └── Performance.java
    │   │                       └── config
    │   │                           └── AopAroundConfig.java
    │   └── resources
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch04
                            └── aop_around_43
                                └── TestAopAround.java
```    

### 4.4 用XML声明切面
例子就是书上的CD歌曲播放次数统计。`BlancDisc`类是`CompactDisc`接口的实现类。统计歌曲播放功能的组件封装在`TrackCounter`类里。这几个类本身没有用任何aop注解标注。bean的声明和aop的声明全集中在`beans.xml`配置文件。具体结构如下，
```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch04
    │   │                   └── aop_parameter_xml_44
    │   │                       ├── BlancDisc.java
    │   │                       ├── CompactDisc.java
    │   │                       └── TrackCounter.java
    │   └── resources
    │       └── aop_parameter_xml_44
    │           └── beans.xml
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch04
                            └── aop_parameter_xml_44
                                └── TestAopParameterXml.java
```

### 4.5 使用Aspectj
这部分书上例子不完整，如果仅仅按照书上的代码，如下所示，还不能正常运行。至于Aspectj和Spring怎么配合使用，这里做一点简单说明。
```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch04
    │   │                   └── aop_aspectj_45
    │   │                       ├── aspect
    │   │                       │   └── CriticAspect.aj
    │   │                       ├── concert
    │   │                       │   ├── CriticismEngine.java
    │   │                       │   ├── CriticismEngineImpl.java
    │   │                       │   ├── Performance.java
    │   │                       │   └── PerformanceImpl.java
    │   │                       └── config
    │   │                           └── AopAspectjConfig.java
    │   └── resources
    │       └── aop_aspectj_45
    │           └── concert.xml
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch04
                            └── aop_aspectj_45
                                └── TestAopAspectj.java
```

例子的目的很清楚，和之前一样`Performance`接口是主要的业务逻辑。`CriticAspect`是切面类，负责在表演结束后做评论（后置增强）。评论的内容从`CriticismEngine`接口中随机抽取。例子里`Performance`和`CriticismEngine`接口和他们各自的实现都是用Java写的。唯独`CriticAspect`是用Aspectj的语法写的，扩展名`.aj`文件。

问题就在这里。Java编译器是不能编译`.aj`文件的。和Spring里用`@Aspect`注解标注的切面类一样，`CriticAspect`只是对切面的一个定义，最终工作的是添加了前置或后置增强的`Performance`类的代理类。Spring中根据`@Aspect`注解生成动态代理的工作由Spring的AOP模块完成。在Aspectj这里则是由Aspectj的专属编译器完成。

因此需要下载 [AspectJ Development Tool](https://www.eclipse.org/aspectj/downloads.php#ides) 工具包，里面包含了一个AspectJ编译器。它负责把`CriticAspect.aj`编译成`CriticAspect.class`二进制类文件，并且实例化。书上关于怎么使用AspectJ工具的这部分没解释。书上通过`aspectOf()`静态方法获得`CriticAspect`类实例都是建立在AspectJ编译器正确创建这个实例的基础上。而且既然我们能拿到`CriticAspect`类的实例，我们自己就能拿它来做所有的事。没有Spring容器替我们管理Bean也可以。

原本Gradle有个`aspectj.gradle`插件可以做这件事，可惜很久没有人维护，和新的Gradle 5已经不兼容。详见这个帖子 -> <https://discuss.gradle.org/t/could-not-get-unknown-property-classesdir-for-main-classes/30591/5>

先不在这个问题上纠结太久。实在要做，这里有个很好的参考帖子，而且不是用插件，很有参考价值 -> <https://stackoverflow.com/questions/32949541/aspectj-gradle-configuration>

至于gradle的插件，除了`aspectj.gradle`，还有`io.freefair.aspectj.compile-time-weaving`，具体参考 -> <https://plugins.gradle.org/search?term=aspectj>

如果不想用XML配置，也可以用注释配置，参考 -> <https://www.mkyong.com/spring3/spring-aop-aspectj-annotation-example/>

### 参考文献
* 几个`@Aspect`的高级用法 -> <https://juejin.im/post/5b79193a51882542c963e9db>
* 关于“切面”，“切点”，“连接点”，“通知”几个基本概念 -> <https://blog.csdn.net/troubleshooter/article/details/78467637>
* 通知函数如何获得被切函数的参数 -> <https://stackoverflow.com/questions/15660535/get-method-arguments-using-spring-aop>
* 如何在Spring项目中使用Aspectj -> <https://www.baeldung.com/aspectj>
* Gradle配置AspectJ不用插件的方法 -> <https://stackoverflow.com/questions/32949541/aspectj-gradle-configuration>
* `aspectj.gradle`插件不支持Gradle 5 -> <https://discuss.gradle.org/t/could-not-get-unknown-property-classesdir-for-main-classes/30591/5>
* AspectJ官方文档 -> <https://www.eclipse.org/aspectj/doc/released/devguide/printable.html>
* 另一篇官方文档 -> <https://docs.spring.io/spring/docs/3.0.0.M3/reference/html/ch08s08.html>
* 关于AspectJ一篇不错的文章 -> <https://juejin.im/entry/5a40abb16fb9a0451e400886>
* Spring怎么同时使用Java配置类和Xml配置文件 -> <https://stackoverflow.com/questions/27979735/cannot-process-locations-and-classes-for-context-configuration>
* 用注释而不是xml配置aspectj -> <https://www.mkyong.com/spring3/spring-aop-aspectj-annotation-example/>
* Spring`@EnableAspectJAutoProxy`背后的那些事 -> <https://www.cnblogs.com/foreveravalon/p/8653832.html>
