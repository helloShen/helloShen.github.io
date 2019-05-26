---
layout: post
title: "Spring in Action - 4th Edition - Chaper 3"
date: 2019-04-25 22:54:07
author: "Wei SHEN"
categories: ["spring", "java", "gradle"]
tags: []
description: >
---

### 3.1 通过Java Config配置Profile
`profile_31`包结构如下，
```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch03
    │   │                   └── profile_31
    │   │                       ├── SayHello.java
    │   │                       ├── SayHelloFromDev.java
    │   │                       ├── SayHelloFromProd.java
    │   │                       └── config
    │   │                           └── ProfileConfig.java
    │   └── resources
    │       └── log4j.properties
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch03
                            └── profile_31
                                └── SayHelloTest.java
```

实验中在`ProfileConfig`配置类中用`@Bean`注解手动创建`SayHelloFromDev`和`SayHelloFromProd`类的bean实例各一个。分别用`@Profile("dev")`和`@Profile("prod")`标注。这两个类实现了同一个接口`SayHello`。

测试类`SayHelloTest`中用`@Autowired`让spring自动分配一个`SayHello`类型的bean实例。
```
@Autowired
private SayHello sayHelloBot;
```

在`@ActiveProfiles("dev")`注解激活`dev`环境的情况下，spring自动选择了`SayHelloFromDev`类实例。改成`@ActiveProfiles("prod")`就会自动配置成`SayHelloFromProd`类实例。

### 3.2 `@Conditional`有条件地创建Bean实例
```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch03
    │   │                   └── condition_32
    │   │                       ├── MagicBean.java
    │   │                       ├── condition
    │   │                       │   └── MagicExistCondition.java
    │   │                       └── config
    │   │                           └── ConditionConfig.java
    │   └── resources
    │       ├── env.properties
    │       └── log4j.properties
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch03
                            └── condition_32
                                └── ConditionTest.java
```

`config/ConditionConfig.java`配置类中用`@Bean`手动创建`MagicBean`类bean实例，同时加上`@Conditional(MagicExistCondition.class)`注解，Spring在创建bean实例之前会调用`condition/MagicExistCondition.java`类的`matches()`函数，若判定为真，才创建实例。

具体判定魔法是否存在，我们在`Environment`中设置一个环境变量`magic`，只有当`magic = ON`的时候，才会创建MagicBean单例。配置环境变量，只要在配置类`ConditionConfig`上加上`@PropertySource("classpath:env.properties)`，让他去`src/main/resources/env.properties`配置文件，其中配置了`magic=ON`。`matches()`函数要获得`Environment`实例引用可以通过`matches()`函数的参数`ConditionContext.getEnvironment()`拿到。

`condition_32`包因为没有激活`@Profile`，所以不会和`profile_31`包的测试上下文混起来。每个测试类，比如`profile_31/SayHelloTest.java`和`condition_32/ConditionTest.java`都有各自的上下文`ApplicationContext`实例。

### 3.3 `@Qualifier`实验
```
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia4
    │   │               └── ch03
    │   │                   └── qualifier_33
    │   │                       ├── Dessert.java
    │   │                       ├── IceCream.java
    │   │                       ├── Popsicle.java
    │   │                       ├── config
    │   │                       │   └── QualifierConfig.java
    │   │                       └── qualifier
    │   │                           ├── Cold.java
    │   │                           ├── Creamy.java
    │   │                           └── Fruity.java
    │   └── resources
    │       └── log4j.properties
    └── test
        └── java
            └── com
                └── ciaoshen
                    └── sia4
                        └── ch03
                            └── qualifier_33
                                └── QualifierTest.java
```

有2种甜点，冰淇淋`IceCream`和棒冰`Popsicle`。冰淇淋是又`@Cold`又`@Creamy`。棒冰是又`@Cold`又`@Fruity`。`@Cold`，`@Creamy`，`@Fruity`三个新注释都在`qualifier`包下定义。同样在`config/QualifierConfig.java`配置类手动声明了一个冰淇淋单例和一个棒冰单例。最后在`QualifierTest.java`测试类中，用`@Autowired`执行自动装配2个`Dessert`实例。要求其中一个同时满足又冰又奶，另一个又冰又水果味。分别只有一个满足条件的bean单例，顺利自动装配完成。

### 3.4 `@Scope`作用域
`@Scope`注解标明某个bean的作用域。比如`@Scope(value=WebApplicationContext.SCOPE_SESSION)`说明这个bean在一个Session过程中都有效。目前书中没有涉及到Session的内容，这一节例子略过。`@Scope`可以和自动识别`@Component`和手动标注`@Bean`一起使用。

### 3.5 Spring表达式语言
表达式或算式放到`#{ ... }`里即可。可以做数学计算，可以做条件运算，可以用作字面量，也可以用`T()`括起来调用某种具体java类型。例子太多，这里不一一列举。以后用到就知道了。

### Logging
Spring的日志系统面向Jakarta Commons Logging API (JCL)，打包在`spring-jcl`依赖包中。JCL和`slf4j`一样，不是一个实际的日志系统实现，只是一个日志接口。负责将代码和具体的日志系统，如`log4j`或`java.util.logging`解耦。

JCL的优点是它有一个自动搜索类路径下可用日志实现的功能。只需要将`log4j`加入依赖，项目日志就可以工作。缺点是这个搜索算法会影响效率。更多的项目用的是`slf4j`而不是JCL。

在Spring里如果要用`slf4j`替代`commons-logging`，
1. 先要禁用`spring-jcl`包
2. 再添加`jcl-over-slf4j`包，把之前对`commons-logging`的调用全转到`slf4j`接口上来。
3. 再加入`slf4j-api` -> `slf4j-log4j12` -> `log4j`全家桶。

gradle中配置如下，
```
// log4j & slf4j
configurations.all { // 不禁用spring-jcl，无法嫁接到slf4j上
    exclude group: 'org.springframework', module: 'spring-jcl'
}
implementation 'org.slf4j:jcl-over-slf4j:1.7.26'
implementation "org.slf4j:slf4j-api:1.7.26"
implementation 'org.slf4j:slf4j-log4j12:1.7.26'
implementation "log4j:log4j:1.2.17"
```

简单说明一下，`spring-jcl`包依赖由`spring-core`包传递进来。项目只引用了`spring-context`包依赖，`spring-core`会跟随`spring-context`包传递进来。
```
+--- org.springframework:spring-context:5.1.6.RELEASE
|    +--- org.springframework:spring-aop:5.1.6.RELEASE
|    |    +--- org.springframework:spring-beans:5.1.6.RELEASE
|    |    |    \--- org.springframework:spring-core:5.1.6.RELEASE
|    |    |         \--- org.springframework:spring-jcl:5.1.6.RELEASE
...
...
```

### 简单说一下`jcl-over-slf4j`的原理
Spring面向`commons-logging`，也就是代码里会导入下面的库。代码中对`Log`和`LogFactory`的调用不可逆转地会指向`commons-logging`的库。
```
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
```

为了劫持对`org.apache.commons.logging.Log`的引用，就必须写一个和`commons-logging`一模一样的包结构。实际上`jcl-over-slf4j`就是这么干的，它的包结构如下，彻底伪装成`commons-logging`。所以用`jcl-over-slf4j`的时候，必须禁掉对`commons-logging`的依赖，否则编译器不知道加载谁了。
```
org
└── apache
    └── commons
        └── logging
            ├── Log.class
            ├── LogConfigurationException.class
            ├── LogFactory.class
            └── impl
                ├── NoOpLog.class
                ├── SLF4JLocationAwareLog.class
                ├── SLF4JLog.class
                ├── SLF4JLogFactory.class
                ├── SimpleLog$1.class
                └── SimpleLog.class
```

当`jcl-over-slf4j`顺利劫持原本对`commons-logging`的引用到`slf4j`之后，直接上`slf4j-api` -> `slf4j-log4j12` -> `log4j`全家桶就行了。

详细过程参考下面这张图，
![to-use-slf4j-in-spring](/images/sia4-ch03/to-use-slf4j-in-spring.png)

#### 解决标准输出无法在终端输出的问题
我的`log4j.properties`已经配置了将`console`扩展器定向到标准输出，但此时运行gradle构建，终端不会有输出。
```
# Configure stdout
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.Target=System.out
... ...
... ...
```

问题出在gradle。gradle会把写到标准输出`STANDARD_OUT`的所有内容重定向到它的日志系统的`QUIET`级别中，把标准错误`STANDARD_ERROR`的内容重新定向到`ERROR`级别。gradle一共有6个级别，`QUIET`是仅次于`ERROR`的第二高级别。而gradle默认的日志级别是`LIFECYCLE`，比`QUITE`要低一级。也就是说默认情况标准输出和标准错误的内容都应该正常输出。
![log-levels](/images/sia4-ch03/log-levels.png)

但是gradle的日志容器还加了额外一道锁。负责这个日志容器配置的是`test.testLogging`字段，
![gradle-testlogging-1](/images/sia4-ch03/gradle-testlogging-1.png)
![gradle-testlogging-2](/images/sia4-ch03/gradle-testlogging-2.png)

它是一个`TestLoggingContainer`类，其中的`showStandardStreams`参数默认为不显示标准输出的内容。
![showStandardStreams](/images/sia4-ch03/show-standard-streams.png)

所以想要在控制台输出标准输出，还需要修改这个`showStandardStreams`参数。在`build.gradle`中可以这么修改，
```
test {
    testLogging {
		showStandardStreams = true    // 显示标准输出和标准错误的内容
	}
}
```

一般我们还会把异常跟踪栈内容显示量设置为`full`，以便在测试时获得尽可能多的信息。下面是一个不错的惯用配置，
```
test {
    testLogging {
		outputs.upToDateWhen {false}  // 就算test没有更新内容，仍然输出    
		showStandardStreams = true    // 显示标准输出和标准错误的内容
        exceptionFormat 'full'        // 显示所有异常跟踪栈内容
        events 'started', 'skipped', 'passed', 'failed'     // 记录特定测试事件
	}
}
```


### 参考文献
* 在profile中声明配置文件位置 -> <https://www.jianshu.com/p/948c303b2253>
* 简单的用JavaConfig和XML两种方法使用`@Profile` -> <https://www.concretepage.com/spring-5/spring-profiles>
* 另一个spring官方`@Profile`文档 -> <https://spring.io/blog/2011/02/14/spring-3-1-m1-introducing-profile/>
* 怎么获得所有已注册bean的实例 -> <https://stackoverflow.com/questions/9602664/print-all-the-spring-beans-that-are-loaded>
* 在测试中导入多个配置类 -> <https://spring.io/blog/2011/06/21/spring-3-1-m2-testing-with-configuration-classes-and-profiles>
* 实现`Condition`接口的判定类中不要试图引用bean实例 -> <https://stackoverflow.com/questions/52071886/how-to-inject-a-bean-into-a-spring-condition-class>
* Spring中怎么设置`Environment`环境变量-> <https://docs.spring.io/spring-boot/docs/current/reference/html/howto-properties-and-configuration.html>
* Logging模块的官方解释 -> <https://docs.spring.io/spring/docs/4.3.14.RELEASE/spring-framework-reference/html/overview.html>
* jcl-over-slf4j的官方文档 -> <https://www.slf4j.org/legacy.html>
* 怎么在终端显示gradle标准输出 -> <https://stackoverflow.com/questions/9356543/logging-while-testing-through-gradle>
* gradle`test`任务官方文档 -> <https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.Test.html>
* gradle`TestLoggingContainer`类官方文档 -> <https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.logging.TestLoggingContainer.html>
