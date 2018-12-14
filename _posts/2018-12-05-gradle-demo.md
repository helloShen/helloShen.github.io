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
