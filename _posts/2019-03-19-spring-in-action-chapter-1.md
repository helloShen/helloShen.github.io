---
layout: post
title: "Spring in Action - Chapter 1"
date: 2019-03-19 22:07:55
author: "Wei SHEN"
categories: ["spring"]
tags: ["mvc", "web"]
description: >
---

### 初始化Spring项目
可以在线初始化，也可以通过命令行。目的都是下载一个启动包。在线配置可视化选配置简单点。命令行配置完全手动设，累一点。

#### 在线initializr安装
地址：https://start.spring.io

我的初步配置：
![initialize-spring-initializr](/images/spring-in-action-demo-ch01/initialize-spring-initializr.png)

#### 命令行安装
```
curl https://start.spring.io/starter.tgz -d groupId=com.ciaoshen -d artifactId=sia-ch01-taco -d name=sia-ch01-taco -d packageName=com.ciaoshen.sia_ch01_taco -d dependencies=web,thymeleaf -d type=gradle-project -d packaging=war -d baseDir=ch01/sia-ch01-taco | tar -xzvf-
```

#### 初始化包结构
```
.
└── sia-ch01-taco
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
        │   │           └── sia_ch01_taco
        │   │               ├── ServletInitializer.java
        │   │               └── SiaCh01TacoApplication.java
        │   └── resources
        │       ├── application.properties
        │       ├── static
        │       └── templates
        └── test
            └── java
                └── com
                    └── ciaoshen
                        └── sia_ch01_taco
                            └── SiaCh01TacoApplicationTests.java
```

### 最简单的HelloWorld项目
我们的目的是：在浏览器输入地址`localhost:8080/sia-ch01-taco`，显示如下主页，
![jetty-run-taco](/images/spring-in-action-demo-ch01/jetty-run-taco.png)

初始化包里还缺3个组件，
1. 负责显示html页面的`view`：`templates/home.html`
2. html页面里的这张图片`TacoCloud.png`：`resources/static/images/TacoCloud.png`
3. 跳转到指定`view`的一个`controller`：`SiaCh01TacoHomeController.java`

首先这个页面按理可以直接是一个前端网页，但书上提供了另一种做法，用`thymeleaf`模板。这就是为什么在初始化的时候加上了`thymeleaf`库依赖。现在只需要在`resources/templates`文件夹下提供一个`html`模板即可。

`home.html`页面里的图片`TacoCloud.png`需要储存。`gradle`项目默认图片资源位置在`resources/static`文件夹下，根据`home.html`里给出的相对路径为`images/TacoCloud.png`，因此图片具体位置为：`resources/static/images/TacoCloud.png`。

最后Spring MVC需要一个`controller`来跳转到默认主页`home.html`这个`view`。所以要写一个`SiaCh01TacoHomeController.java`来干这个事。

#### `home.html`
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
xmlns:th="http://www.thymeleaf.org">

    <head>
        <title>Taco Cloud</title>
    </head>

    <body>
        <h1>Welcome to...</h1>
        <img th:src="@{/images/TacoCloud.png}"/> <!-- abs path = ./resources/static/images/TacoCloud.png -->
    </body>

</html>
```

#### `SiaCh01TacoHomeController.java`
```java
package com.ciaoshen.sia_ch01_taco;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SiaCh01TacoHomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

}
```

### 生成`war`包
根目录下自动生成的`build.gradle`已经写好了构造过程。只要命令行运行，
```
gradle build
```

在编译好所有的类之后，在`build/libs/`目录下面会多出一个`sia-ch01-taco-0.0.1-SNAPSHOT.war`，这就是我们要部署的`war`包。
```
.
└── sia-ch01-taco
    ├── bin
    │   ├── main
    │   │   ├── application.properties
    │   │   ├── com
    │   │   │   └── ciaoshen
    │   │   │       └── sia_ch01_taco
    │   │   │           ├── ServletInitializer.class
    │   │   │           ├── SiaCh01TacoApplication.class
    │   │   │           └── SiaCh01TacoHomeController.class
    │   │   ├── static
    │   │   │   └── images
    │   │   │       └── TacoCloud.png
    │   │   └── templates
    │   │       └── home.html
    │   └── test
    │       └── com
    │           └── ciaoshen
    │               └── sia_ch01_taco
    │                   └── SiaCh01TacoApplicationTests.class
    ├── build
    │   ├── classes
    │   │   └── java
    │   │       ├── main
    │   │       │   └── com
    │   │       │       └── ciaoshen
    │   │       │           └── sia_ch01_taco
    │   │       │               ├── ServletInitializer.class
    │   │       │               ├── SiaCh01TacoApplication.class
    │   │       │               └── SiaCh01TacoHomeController.class
    │   │       └── test
    │   │           └── com
    │   │               └── ciaoshen
    │   │                   └── sia_ch01_taco
    │   │                       └── SiaCh01TacoApplicationTests.class
    │   ├── libs
    │   │   └── sia-ch01-taco-0.0.1-SNAPSHOT.war
    │   ├── reports
    │   │   └── tests
    │   │       └── test
    │   │           ├── classes
    │   │           │   └── com.ciaoshen.sia_ch01_taco.SiaCh01TacoApplicationTests.html
    │   │           ├── css
    │   │           │   ├── base-style.css
    │   │           │   └── style.css
    │   │           ├── index.html
    │   │           ├── js
    │   │           │   └── report.js
    │   │           └── packages
    │   │               └── com.ciaoshen.sia_ch01_taco.html
    │   ├── resources
    │   │   └── main
    │   │       ├── application.properties
    │   │       ├── static
    │   │       │   └── images
    │   │       │       └── TacoCloud.png
    │   │       └── templates
    │   │           └── home.html
    │   ├── test-results
    │   │   └── test
    │   │       ├── TEST-com.ciaoshen.sia_ch01_taco.SiaCh01TacoApplicationTests.xml
    │   │       └── binary
    │   │           ├── output.bin
    │   │           ├── output.bin.idx
    │   │           └── results.bin
    │   └── tmp
    │       ├── bootWar
    │       │   └── MANIFEST.MF
    │       ├── compileJava
    │       └── compileTestJava
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
        │   │           └── sia_ch01_taco
        │   │               ├── ServletInitializer.java
        │   │               ├── SiaCh01TacoApplication.java
        │   │               └── SiaCh01TacoHomeController.java
        │   └── resources
        │       ├── application.properties
        │       ├── static
        │       │   └── images
        │       │       └── TacoCloud.png
        │       └── templates
        │           └── home.html
        └── test
            └── java
                └── com
                    └── ciaoshen
                        └── sia_ch01_taco
                            └── SiaCh01TacoApplicationTests.java
```

### 运行
传统运行`war`包的方法，是把`war`包丢进一个服务器里，然后运行服务器。这种方式我用Jetty测试。

但Spring Boot其实已经内置Tomcat了。直接运行Spring Boot也可以。

#### Jetty运行
先要下载和安装Jetty，可以参考这篇->[《Gradle Demo里安装Jetty的部分》](http://www.ciaoshen.com/gradle/2018/12/05/gradle-demo.html)。

安装好之后，要么将`sia-ch01-taco-0.0.1-SNAPSHOT.war`直接复制到`webapps`目录下。要么在`webapps`目录下创建一个`sia-ch01-taco.xml`的配置文件，
```
<!DOCTYPE Configure PUBLIC "-//Mort Bay Consulting//DTD Configure//EN" "http://www.eclipse.org/jetty/configure.dtd">
<Configure class="org.eclipse.jetty.webapp.WebAppContext">
    <Set name="contextPath">/sia-ch01-taco</Set>
    <Set name="war">/Users/Wei/github/spring-in-action-demo/ch01/sia-ch01-taco/build/libs/sia-ch01-taco-0.0.1-SNAPSHOT.war</Set>
</Configure>
```

然后进入到Jetty的根目录，执行，
```
java -jar start.jar
```

成功的话，终端会打印出大大的`Spring`logo，
![jetty-run-taco-command](/images/spring-in-action-demo-ch01/jetty-run-taco-command.png)

在浏览器输入，
```
localhost:8080/sia-ch01-taco
```

得到下面结果，
![jetty-run-taco](/images/spring-in-action-demo-ch01/jetty-run-taco.png)


#### Spring Boot运行
Spring Boot运行更简单，在项目根目录下，直接运行命令，
```
gradle bootRun
```

因为`spring`和`war`的plugin已经让gradle加入了`bootRun`任务。同样，显示大Spring Logo表示成功运行，
![spring-boot-run-taco-command](/images/spring-in-action-demo-ch01/spring-boot-run-taco-command.png)

最后在浏览器输入，
```
localhost:8080
```

就可以看到，
![spring-boot-run-taco](/images/spring-in-action-demo-ch01/spring-boot-run-taco.png)

Jetty里因为我们定义了一个子应用目录`sia-ch01-taco`。而Spring Boot没有，所以关联的直接是`/`。
