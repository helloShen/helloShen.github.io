---
layout: post
title: "Gradle Demo"
date: 2018-12-05 00:54:19
author: "Wei SHEN"
categories: ["gradle"]
tags: ["groovy", "gradle"]
description: >
---

### 有用的资源
关于Gradle在Groovy语言层面的问题，可以查阅官方手册->[【Gradle DSL docs】](https://docs.gradle.org/current/dsl/)

### 下载
官方网址，
https://gradle.org/install/

我们选择直接下载二进制包进行安装。

### 安装
解压以后得到`gradle-5.0`文件夹，全部拷贝到`/usr/local`目录下，
```
sudo mv ~/Downloads/gradle-5.0 /usr/local
```

我用的是MAC，编辑`~/.bash_profile`文件，在里面加两行，设置环境变量。
```
# gradle
export GRADLE_HOME="/usr/local/gradle-5.0"
export PATH="${GRADLE_HOME}/bin":${PATH}
```

因为我又装了`zsh`，还要在`~/.zshrc`里复制上面两行。

完了之后，运行命令检查安装情况，
```
gradle -v
```

看到以下内容，说明安装好了，
![gradle-install-version](/images/gradle-demo/gradle-install-version.png)


### Hello World
我的`gradle-demo`实验文件夹结构如下，
```
.
├── LICENSE
├── README.md
├── bin
└── src
    ├── main
    │   ├── gradle
    │   │   └── build.gradle
    │   ├── groovy
    │   ├── java
    │   └── resources
    └── test
        ├── gradle
        ├── groovy
        ├── java
        └── resources
```

创建一个叫`build.gradle`脚本，gradle会自动找到这个名字的文件，如果它存在的话。其中构建，
1. 一个`task`的名字叫`HelloWorld`。
2. `task`里再定义一个`action`叫`doLast`，它表示当前`task`中最后执行的一个`action`，它负责打印`Hello World`。

```java
task HelloWorld {
    doLast {
        println 'Hello World'
    }
}
```

然后进入到`src/main/gradle`目录下，运行`gradle`命令，
```
gradle HelloWorld
```

控制台正确打印出`Hello World`即是正确，
![helloworld-result](/images/gradle-demo/helloworld-result.png)


希望不要打印与`HelloWorld`任务不相关的内容，可以用`-q`选项，表示安静，
```
gradle -q HelloWorld
```

### 另一个稍复杂的例子
在这个例子里，我们引入gradle另两个特性，
1. 关键字`dependsOn`进行依赖管理
2. 每个`Script`脚本对象自带`ant`对象，能直接使用Ant任务

```java
task StartSession {
        chant()
}

def chant() {
        ant.echo(message: 'Repeat after me ...')
}

3.times {
        task "GradleRocks$it" {
                doLast {
                        println 'Gradle rocks!'
                }
        }
}

GradleRocks0.dependsOn StartSession
GradleRocks2.dependsOn GradleRocks1, GradleRocks0
task GroupThrepy(dependsOn: GradleRocks2)
```

例子其实很简单，主体是3个相同的`GradleRocks`任务，因为把闭包调用者`$it`加到任务名字中，所以三次任务分别叫`GradleRocks0`，`GradleRocks1`，`GradleRocks2`。

然后主入口是`GroupThrepy`任务，它依赖`GradleRocks2`任务，后者依赖于`GradleRocks0`和`GradleRocks1`。最后`GradleRocks0`任务依赖于`StartSession`任务，其中会调用`chant()`函数。`chant()`函数直接使用了一个Ant任务`echo`。所以整个依赖链条如下图所示，

![gradlerocks-depends](/images/gradle-demo/gradlerocks-depends.png)

运行`gradle GroupThrepy`命令，终端显示如下，
![gradlerocks-result](/images/gradle-demo/gradlerocks-result.png)

### Groovy DSL语言层面解析
`task`其实是`Project`对象的一个方法，
![task-project-method](/images/gradle-demo/task-project-method.png)

它接受可以接收2个参数，
1. 任务名称
2. 一个闭包（任务的逻辑主体）

我们定义一个任务，花括号`{}`后面的就是在定义一个闭包。


### 实验项目：任务列表项目
使用Java插件，项目根目录下的`build.gradle`文件里只有一行，
```
apply plugin: 'java'
```

Java插件默认项目经典结构如下，
```
.
├── build.gradle
└── src
    ├── main
    │   ├── java
    │   └── resources
    └── test
        ├── java
        └── resources
```

然后在`com.ciaoshen.gradle_demo.todo`包下创建了`ToDoApp`项目。其实就是一个很简单的“待办事项”的提醒列表。完成以后的界面如下。但是在第一版中，先把精力集中在数据处理模块。用户界面可以在后面的迭代版本更新。
![todoapp-1](/images/gradle-demo/todoapp-1.png)

文件结构如下，
* `ToDoItem.java`：抽象化一个待办事项。
* `InMemoryToDoRepository.java`：多个代办事项的容器，数据全部储存在内存。写入文件的持久化工作之后再做。
* `ToDoRepository.java`：就是为以后的持久化扩展预设的一个接口。
* `CommandLineInput.java`：是一个工具类，负责翻译用户输入的单个字符的`a`，`f`，`i`这类选项。
* `CommandLineInputHandler.java`：根据`CommandLineInput.java`得到的选项，具体执行响应任务。
* `ToDoApp.java`：应用的主入口。

```
.
├── LICENSE
├── README.md
├── build.gradle
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── gradle_demo
    │   │               └── todo
    │   │                   ├── ToDoApp.java
    │   │                   ├── model
    │   │                   │   └── ToDoItem.java
    │   │                   ├── repository
    │   │                   │   ├── InMemoryToDoRepository.java
    │   │                   │   └── ToDoRepository.java
    │   │                   └── utils
    │   │                       ├── CommandLineInput.java
    │   │                       └── CommandLineInputHandler.java
    │   └── resources
    └── test
        ├── java
        └── resources
```

运行命令行，进行自动编译，测试，打包，
```
gradle build
```

项目根目录变成如下结构，
```
.
├── LICENSE
├── README.md
├── build
│   ├── classes
│   │   └── java
│   │       └── main
│   │           └── com
│   │               └── ciaoshen
│   │                   └── gradle_demo
│   │                       └── todo
│   │                           ├── ToDoApp.class
│   │                           ├── model
│   │                           │   └── ToDoItem.class
│   │                           ├── repository
│   │                           │   ├── InMemoryToDoRepository.class
│   │                           │   └── ToDoRepository.class
│   │                           └── utils
│   │                               ├── CommandLineInput.class
│   │                               ├── CommandLineInputHandler$1.class
│   │                               └── CommandLineInputHandler.class
│   ├── libs
│   │   └── gradle-demo.jar
│   └── tmp
│       ├── compileJava
│       └── jar
│           └── MANIFEST.MF
├── build.gradle
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── gradle_demo
    │   │               └── todo
    │   │                   ├── ToDoApp.java
    │   │                   ├── model
    │   │                   │   └── ToDoItem.java
    │   │                   ├── repository
    │   │                   │   ├── InMemoryToDoRepository.java
    │   │                   │   └── ToDoRepository.java
    │   │                   └── utils
    │   │                       ├── CommandLineInput.java
    │   │                       └── CommandLineInputHandler.java
    │   └── resources
    └── test
        ├── java
        └── resources
```

* `./build/classes/`目录下是编译后的类文件
* `./build/libs/`目录下是打包的`jar`
* `./build/tmp/`目录下是一下项目源文件

执行命令行后面加一个`--info`选项，可以看到更多的信息，
```
gradle build --info
```

列表列出的就是Java插件默认执行的构建步骤，
```
➜  gradle-demo git:(develop) ✗ gradle build --info
Initialized native services in: /Users/Wei/.gradle/native
The client will now receive all logging from the daemon (pid: 26949). The daemon log file: /Users/Wei/.gradle/daemon/5.0/daemon-26949.out.log
Starting 12th build in daemon [uptime: 11 mins 53.523 secs, performance: 95%, no major garbage collections]
Using 4 worker leases.
Starting Build
Settings evaluated using settings file '/Users/Wei/github/gradle-demo/settings.gradle'.
Projects loaded. Root project using build file '/Users/Wei/github/gradle-demo/build.gradle'.
Included projects: [root project 'gradle-demo']

> Configure project :
Evaluating root project 'gradle-demo' using build file '/Users/Wei/github/gradle-demo/build.gradle'.
All projects evaluated.
Selected primary task 'build' from project :
Tasks to be executed: [task ':compileJava', task ':processResources', task ':classes', task ':jar', task ':assemble', task ':compileTestJava', task ':processTestResources', task ':testClasses', task ':test', task ':check', task ':build']
:compileJava (Thread[Execution worker for ':',5,main]) started.

> Task :compileJava UP-TO-DATE
Skipping task ':compileJava' as it is up-to-date.
:compileJava (Thread[Execution worker for ':',5,main]) completed. Took 0.014 secs.
:processResources (Thread[Execution worker for ':',5,main]) started.

> Task :processResources NO-SOURCE
Skipping task ':processResources' as it has no source files and no previous output files.
:processResources (Thread[Execution worker for ':',5,main]) completed. Took 0.001 secs.
:classes (Thread[Execution worker for ':',5,main]) started.

> Task :classes UP-TO-DATE
Skipping task ':classes' as it has no actions.
:classes (Thread[Execution worker for ':',5,main]) completed. Took 0.0 secs.
:jar (Thread[Execution worker for ':',5,main]) started.

> Task :jar UP-TO-DATE
Skipping task ':jar' as it is up-to-date.
:jar (Thread[Execution worker for ':',5,main]) completed. Took 0.005 secs.
:assemble (Thread[Execution worker for ':',5,main]) started.

> Task :assemble UP-TO-DATE
Skipping task ':assemble' as it has no actions.
:assemble (Thread[Execution worker for ':',5,main]) completed. Took 0.0 secs.
:compileTestJava (Thread[Execution worker for ':',5,main]) started.

> Task :compileTestJava NO-SOURCE
Skipping task ':compileTestJava' as it has no source files and no previous output files.
:compileTestJava (Thread[Execution worker for ':',5,main]) completed. Took 0.001 secs.
:processTestResources (Thread[Execution worker for ':',5,main]) started.

> Task :processTestResources NO-SOURCE
Skipping task ':processTestResources' as it has no source files and no previous output files.
:processTestResources (Thread[Execution worker for ':',5,main]) completed. Took 0.002 secs.
:testClasses (Thread[Execution worker for ':',5,main]) started.

> Task :testClasses UP-TO-DATE
Skipping task ':testClasses' as it has no actions.
:testClasses (Thread[Execution worker for ':',5,main]) completed. Took 0.0 secs.
:test (Thread[Execution worker for ':',5,main]) started.

> Task :test NO-SOURCE
Skipping task ':test' as it has no source files and no previous output files.
:test (Thread[Execution worker for ':',5,main]) completed. Took 0.002 secs.
:check (Thread[Execution worker for ':',5,main]) started.

> Task :check UP-TO-DATE
Skipping task ':check' as it has no actions.
:check (Thread[Execution worker for ':',5,main]) completed. Took 0.0 secs.
:build (Thread[Execution worker for ':',5,main]) started.

> Task :build UP-TO-DATE
Skipping task ':build' as it has no actions.
:build (Thread[Execution worker for ':',5,main]) completed. Took 0.0 secs.
```

最后运行`java`命令运行应用，
```
java -cp build/classes/java/main  com.ciaoshen.gradle_demo.todo.ToDoApp
```

运行效果如下，
```
➜  gradle-demo git:(develop) ✗ java -cp build/classes/java/main  com.ciaoshen.gradle_demo.todo.ToDoApp

--- To Do Application ---
Please make a choice:
(a)ll items
(f)ind a specific item
(i)nsert a new item
(u)pdate an existing item
(d)elete an existing item
(e)xit
> i
Please enter the name of the item:
> aaa
Successfully inserted to do item with ID 1.

--- To Do Application ---
Please make a choice:
(a)ll items
(f)ind a specific item
(i)nsert a new item
(u)pdate an existing item
(d)elete an existing item
(e)xit
> a
Item[1] = aaa

--- To Do Application ---
Please make a choice:
(a)ll items
(f)ind a specific item
(i)nsert a new item
(u)pdate an existing item
(d)elete an existing item
(e)xit
>e
```

### 定制构造

#### 自定义项目结构
* `version`: 定义项目版本号
* `sourceCompatibility`: 定义java版本
* `jar{ manifest{...} }`: 定义“主入口”等元信息
* `sourceSets`: 定义源文件路径
* `buildDir`: 定义类文件目录

```
apply plugin: 'java'

version = 0.1
sourceCompatibility = 1.8

jar {
    manifest {
        // attributes 'Main-Class': 'com.ciaoshen.gradle-demo.todo.ToDoApp'
        attributes 'Main-Class': 'com.ciaoshen.gradle-demo.another_todo.ToDoApp'
    }
}

sourceSets {
    main {
        java {
            srcDirs = ['src']
        }
    }
    test {
        java {
            srcDirs = ['test']
        }  
    }
}

buildDir = 'out'
```


#### 管理外部依赖
当代码里调用了`org.apache.commons.lang3.CharUtils`类，需要在编译的时候引用外部库包`org.apache.commons.lang3`，
```java
command = CharUtils.toChar(input, DEFAULT_INPUT);
```

可以在`search.maven.org`查到`commons.lang3`库的id是`org.apache.commons:commons-lang3:3.8.1`，因此在`build.gradle`文件里添加如下脚本，
```
/** new dependencies */
repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.apache.commons:commons-lang3:3.8.1'
}
```

### 安装Jetty
下载Jetty -> [【Eclipse Jetty Downloads】](https://www.eclipse.org/jetty/download.html)

Jetty在线文档 -> [【Jetty Documentation】](https://www.eclipse.org/jetty/documentation/9.4.14.v20181114/)

安装Jetty，将下载的压缩包解压至你想要的目录，我放在项目的vendors目录下`~/github/gradle-demo/vendors/jetty-9.4.14`。其中的`start.jar`是一个可执行jar包，用`java`命令即可启动Jetty，
```
cd ~/github/gradle-demo
java -jar vendors/jetty-9.4.14/start.jar
```

控制台显示Jetty已经运行，
![jetty-run-1](/images/gradle-demo/jetty-run-1.png)

但因为还没有配置webapp的路径，所以服务器`0.0.0.0:8080`显示`404`错误，
![jetty-run-2](/images/gradle-demo/jetty-run-2.png)

### Jetty部署HelloWorld网络应用（不使用gradle)
我们从[【Tomcat Sample Application】](https://tomcat.apache.org/tomcat-5.5-doc/appdev/sample/)下载一个最简单的webapp来做测试，打包成单一的`sample.war`包。

#### 默认部署在`webapps/`子目录
最简单的默认部署路径是Jetty根目录的`$JETTY_HOME/webapps/`子目录。只需要把`sample.war`包复制到`$JETTY_HOME/webapps/`目录下即可。我本地的Jetty根目录是`~/github/gradle-demo/jetty-9.4.14/`，所以拷贝命令，
```
cp ~/Downloads/sample.war ~/github/gradle-demo/jetty-9.4.14/webapps/
```

Jetty服务器支持热部署，所以一拷贝完扫描器会自动部署。在浏览器里输入，
```
http://localhost:8080/sample
```

即得到显示，
![jetty-deploy-sample-1](/images/gradle-demo/jetty-deploy-sample-1.png)

#### 部署任意位置的网络应用
不想部署在默认位置，就需要告诉服务器，你的应用在哪儿。需要在`$JETTY_HOME/webapps/`子目录下编辑一个`.xml`文件告诉服务器。我们将这个文件起名叫`sample.xml`（不是必须叫sample.xml，叫其他名字也行）。
```
<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE Configure PUBLIC "-//Mort Bay Consulting//DTD Configure//EN" "http://www.eclipse.org/jetty/configure.dtd">
<Configure class="org.eclipse.jetty.webapp.WebAppContext">
    <Set name="contextPath">/sample</Set>
    <Set name="war">/Users/Wei/github/gradle-demo/vendors/jetty-9.4.14/simple-app/sample.war</Set>
</Configure>
```

上面这段脚本告诉服务器两件事，
1. 浏览器里通过`http://localhost:8080/sample`域访问这个应用。
2. 现在目标应用的物理绝对地址是：`/Users/Wei/github/gradle-demo/vendors/jetty-9.4.14/simple-app/sample.war`。

实际上就是做了一个绑定。Jetty马上会执行热部署，终端显示如下，告诉我们部署成功，
![jetty-deploy-1](/images/gradle-demo/jetty-deploy-1.png)

### 手工把`todo-app`打包成war

#### 标准`.war`包的结构
把刚才下载的`sample.war`解压出来，结构如下，
```
.
├── META-INF
│   └── MANIFEST.MF
├── WEB-INF
│   ├── classes
│   │   └── mypackage
│   │       └── Hello.class
│   ├── lib
│   └── web.xml
├── hello.jsp
├── images
│   └── tomcat.gif
└── index.html
```

这里面实际负责页面显示的有3个东西，
1. `index.html`: 应用的主入口，我们输入`http://localhost:8080/sample`，就直接打开这个主页。

主页上有两个链接，一个跳转到另外的`jsp`页面，另一个跳转到`servlet`页面。
![jetty-deploy-sample-1](/images/gradle-demo/jetty-deploy-sample-1.png)

2. `hello.jsp`: 主页指向的那个`jsp`页面。也可以通过`http://localhost:8080/sample/hello.jsp`直接访问。所谓`jsp`页面本质就是在html静态页面里插入`java`代码。比如这页中的`Hello!`就是直接用java打印语句写的。
![jetty-deploy-sample-hello-jsp](/images/gradle-demo/jetty-deploy-sample-hello-jsp.png)

3. `Hello.class`: 主页指向的那个`servlet`页面。也可以通过`http://localhost:8080/sample/hello`直接访问。`Servlet`页面的本质就是用java语言来写html页面。下图中的整个页面的字节流都是用java语言输出的。做这件事的就是这个`Hello.class`。
![jetty-deploy-sample-hello-servlet](/images/gradle-demo/jetty-deploy-sample-hello-servlet.png)

剩下的东西里面比较重要的就是`WEB-INF/web.xml`文件。`hello.jsp`文件就放在`sample.war`的根目录下，很容易就通过相对路径`sample/hello.jsp`找到。但Servelt就不行，光告诉浏览器`sample/hello`是找不到`Hello.class`文件的。所以，`WEB-INF/web.xml`文件就是Servlet的映射文件，告诉服务器运行哪个java类。`web.xml`里做这件事的代码如下，
```
<servlet>
    <servlet-name>HelloServlet</servlet-name>
    <servlet-class>mypackage.Hello</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>HelloServlet</servlet-name>
    <url-pattern>/hello</url-pattern>
</servlet-mapping>
```

它告诉服务器，我们有一个Servlet叫`HelloServlet`，用户可以通过`/hello`相对路径访问它。它对应的java类文件的包路径是`mypackage.Hello`。

剩下的就是2个次要的资源文件，比如，
1. `images/tomcat.gif`: 主页上那只猫的图片文件。
2. `META-INF/MANIFEST.MF`: 记录像软件版本号这样的元数据。
