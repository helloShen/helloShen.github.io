---
layout: post
title: "About Java Executable JAR"
date: 2019-04-20 20:21:15
author: "Wei SHEN"
categories: ["java"]
tags: ["jar", "classpath", "compile"]
description: >
---

### 用`java -jar`运行可执行JAR包的时候，`classpath`参数无效
假设我的可执行文件叫`runnable.jar`，它依赖一个jar库包`a.jar`。
```
java -jar runnable.jar -cp a.jar
```
像上面这样想当然的命令会得到报错`NoClassDefFoundError`，
```
Exception in thread "main" java.lang.NoClassDefFoundError:
    your/library/DependentClass
    ...
    ...
```

原因就是：
> 运行可执行JAR时，虚拟机会忽略`-cp`参数。只会到这个可执行JAR内部去找class。（而且还不能是打包在套嵌jar包里）

具体原因，当年Sun的人肯定有他们的考虑。先不细究。先看解决办法，主要有3种。

### 不打包成JAR，直接执行`.class`
比如用Spring的话，要运行具体类，直接添加Spring缓存的库包路径即可，有特殊需求可以很方便地扩展类路径。
```
task runMain(type: JavaExec) {
	main = 'com.ciaoshen.runnalbe.MainClass'
	classpath += sourceSets.main.runtimeClasspath
}
```

### 配置可执行JAR的`manifest`文件中的`Class-Path`属性
假设我gradle的`dependencies`配置了`spring-context`这个依赖库，
```
dependencies {
	compile 'org.springframework:spring-context:5.1.6.RELEASE'
}
```

在`jar`任务中可以这样把所有直接或间接依赖的库全添加到`manifest`文件的`Class-Path`属性里，
```
jar {
	manifest {
        attributes 'Main-Class': 'com.ciaoshen.runnalbe.MainClass'
		attributes 'Class-Path': configurations.compile.collect { it.name }.join(' ')
	}
}
```

假设我最终的`runnable.jar`应用在`build/libs/`路径下，
```
build
└── libs
    └── runnable.jar
```

我之前的配置就要求`spring-context`直接或间接依赖的库全要在`build/libs/`路径下，
```
build
└── libs
    ├── runnable-app.jar
    ├── spring-aop-5.1.6.RELEASE.jar
    ├── spring-beans-5.1.6.RELEASE.jar
    ├── spring-context-5.1.6.RELEASE.jar
    ├── spring-core-5.1.6.RELEASE.jar
    ├── spring-expression-5.1.6.RELEASE.jar
    └── spring-jcl-5.1.6.RELEASE.jar
```

具体的相对路径都可以自己配置，完全可以创建一个`build/libs/spring/`子目录，和spring有关的类库统一归档。唯一需要注意的要求是：
> **必须在`runnable.jar`包的外面**。因为套嵌在jar包内部的jar包，就算加了`Class-Path`参数也无法访问。

比如下面命令简单拷贝Spring缓存依赖库到本地，
```
task copyLibs(type: Copy) {
	from configurations.compile
	into 'build/libs/'
}
```

下面命令运行我的`runnable.jar`，
```
task runKnightMainJar(dependsOn: jar, type: JavaExec) {
	main = '-jar'; args 'build/libs/runnable.jar'
}
```

### 第二种方法：把所有依赖库`.jar`中的`.class`解压出来，直接添加到`runnable.jar`
如果实在想要 **一个JAR包**，既然在jar里套嵌jar不可行。就只好把所有`.class`类文件抽取出来，直接打包在`runnable.jar`里。比如gradle可以用`zipTree`执行解析，
```
jar {
    manifest {
        attributes 'Main-Class': 'com.ciaoshen.runnable.MainClass'
    }

	configurations.compile.each {
		from(project.zipTree(it))
	}
}
```

解压完，同样执行`runnable.jar`，
```
task runKnightMainJar(dependsOn: jar, type: JavaExec) {
	main = '-jar'; args 'build/libs/runnable.jar'
}
```

### 实在要套嵌jar，可以用`One Jar`或`Uber Jar`
实在想要在`runnable.jar`里套嵌jar库包，可以用`One Jar`工具，详细参考官网 -> <http://one-jar.sourceforge.net/>

还有`Uber Jar`，官网 -> <https://imagej.net/Uber-JAR>

这篇问章也不错 -> <https://longdick.iteye.com/blog/332580>


### 参考文献
1. <https://stackoverflow.com/questions/18413014/run-a-jar-file-from-the-command-line-and-specify-classpath>
2. <https://blog.csdn.net/wenfengzhuo/article/details/10741825>
3. <https://docs.oracle.com/javase/tutorial/deployment/jar/downman.html>
4. <https://stackoverflow.com/questions/22659463/add-classpath-in-manifest-using-gradle>
5. <https://longdick.iteye.com/blog/332580>
