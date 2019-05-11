---
layout: post
title: "Logging in Gradle"
date: 2019-05-10 23:14:07
author: "Wei SHEN"
categories: ["spring", "gradle"]
tags: ["log"]
description: >
---

# Gradle中关于Logging的常见问题与常用配置
第三章中做了个总结，这里拎出来自成一篇，方便查阅。

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

#### 简单说一下`jcl-over-slf4j`的原理
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
