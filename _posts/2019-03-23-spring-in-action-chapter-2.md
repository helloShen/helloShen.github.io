---
layout: post
title: "Spring in Action - Chapter 2"
date: 2019-03-23 20:49:29
author: "Wei SHEN"
categories: ["spring"]
tags: ["web", "mvc"]
description: >
---

### 初始化本章Demo项目
使用在线初始化工具`https://start.spring.io`更快捷。基本配置如下图所示，
![initialize-1](/images/spring-in-action-demo-ch02/initialize-1.png)
![initialize-2](/images/spring-in-action-demo-ch02/initialize-2.png)

依赖的库除了沿袭上一章的`Web`和`Thymeleaf`外，又加了一个`Lombok`。主要为了简化像`Ingredient`这样的POJO类的代码。Lombok会自动生成像`equals()`,`hashCode()`和`toString()`这些常用基本方法。


生成之后，项目根目录为`ch02/sia-ch02-taco/`，项目结构如下，
```
.
└── sia-ch02-taco
    ├── HELP.md
    ├── build.gradle
    ├── gradle
    │   └── wrapper
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    ├── gradlew
    ├── gradlew.bat
    ├── settings.gradle
    └── src
        ├── main
        │   ├── java
        │   │   └── com
        │   │       └── ciaoshen
        │   │           └── sia_ch02_taco
        │   │               ├── ServletInitializer.java
        │   │               └── SiaCh02TacoApplication.java
        │   └── resources
        │       ├── application.properties
        │       ├── static
        │       └── templates
        └── test
            └── java
                └── com
                    └── ciaoshen
                        └── sia_ch02_taco
                            └── SiaCh02TacoApplicationTests.java
```
