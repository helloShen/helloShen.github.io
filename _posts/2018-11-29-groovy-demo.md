---
layout: post
title: "Groovy Demo"
date: 2018-11-29 19:47:57
author: "Wei SHEN"
categories: ["groovy"]
tags: ["groovy", "gradle"]
description: >
---

### 下载
在下面地址下载Groovy，选择`Binary Release`版本，
http://groovy-lang.org/install.html

### 安装
解压下载的压缩包，得到`groovy-2.5.4`文件夹。把文件夹移动到用户安装软件的文件夹`/usr/local`，
```
sudo mv path-to-groovy/groovy-2.5.4 /usr/local
```

修改2个环境参数，
1. 设置`GROOVY_HOME`为刚才存放groovy的根目录`/usr/local`。
2. 在`PATH`环境参数里加入一个`${GROOVY_HOME}/bin`。

具体做法，打开`~/.bash_profile`和`~/.zshrc`配置脚本里加上下面两行，
```
# groovy
export GROOVY_HOME="/usr/local/groovy-2.5.4"
export PATH="${GROOVY_HOME}/bin":${PATH}
```

在shell运行下面命令，启动一个groovy交互进程，
```
groovysh
```

看到下面提示符，说明安装成功，
```
➜  java git:(master) ✗ groovysh
Groovy Shell (2.5.4, JVM: 1.8.0_73)
Type ':help' or ':h' for help.
---------------------------------------------------------------------
groovy:000>
```

或者运行下面命令，启动Swing交互界面，
```
groovyConsole
```

弹出下面界面，说明安装成功，
![groovy-console](/images/groovy-demo/groovy-console.png)

### 在`groovysh`中直接输入groovy代码
下面是一段简单的输出前十个Fibonacci数列的Java代码，
```java
public void fibonacci() {
    int current = 1, next = 1;
    for (int i = 0; i < 10; i++) {
        System.out.print(current + " ");
        int oldNext = next;
        next = current + next;
        current = oldNext;
    }
}
```

翻译成groovy代码就是下面这个样子。先不深究groovy具体的语法怎么样，光从表面看，groovy的语法更简洁。
```java
current = 1
next = 1
10.times {
        print current + " "
        oldNext = next
        next += current
        current = oldNext
}
println ""
```

在终端启动`groovysh`，逐行输入上面的代码，每输入一行都会得到一个反馈，最后直接得到结果。过程当中没有源码文件，没有编译后的字节码文件，也不需要编译，执行。形式上和python动态语言很像，所见即所得。
![groovysh-fibonacci](/images/groovy-demo/groovysh-fibonacci.png)

### 在`groovyConsole`中运行groovy脚本
直接将上面这段代码复制到`groovyConsole`中，点击运行，可以看到同样的结果，
![groovy-console-fibonacci](/images/groovy-demo/groovy-console-fibonacci.png)

### 代码保存至`Fibonacci.groovy`文件，直接在终端执行
我的项目目录如下，将上面代码存入`Fibonacci.groovy`源文件，
```
.
├── LICENSE
├── README.md
├── bin
└── src
    ├── main
    │   ├── groovy
    │   │   └── Fibonacci.groovy
    │   └── resources
    └── test
```

终端直接用`groovy`命令执行脚本，
```
groovy script-name
```

执行`Fibonacci.groovy`如下图所示，
![groovy-fibonacci](/images/groovy-demo/groovy-fibonacci.png)

甚至，当我编辑`Book.groovy`类，
```java
class Book {
    private String title
    Book(String title) {
        this.title = title
    }
    String getTitle() {
        return title
    }
}
```

然后用一个脚本去调用`Book`类，代脚本储存在一个`UseBook.groovy`文件中，
```java
Book book = new Book('Groovy in Action')
assert book.getTitle() == 'Groovy in Action'
assert getTitleBackwards(book) == 'noitcA ni yvoorG'
println 'Two Assertion passed!'


String getTitleBackwards(book) {
    title = book.getTitle()
    return title.reverse()
}
```

也可以直接用groovy命令执行`UseBook`脚本，前提是`Book.groovy`和`UseBook.groovy`在同一个目录中，
```
cd src/main/groovy
groovy UseBook.groovy
```

不需要导入库包，不需要`main()`函数，不需要编译`Book.groovy`类，也不需要编译`UseBook.groovy`类，，不需要`java`执行。直接跑到文件的所在目录，用`groovy`命令就可以运行。

如果我只是想简单运行一下某个类，看看效果，groovy的效率比写一个完整的java测试高很多。

### 用`groovyc`编译
在终端输入下面命令，
```
groovyc -d bin src/main/groovy/Fibonacci.groovy
```

会在`bin/`文件夹中生成，
* `Fibonacci.class`: 可直接运行的Java类，包含一个`main()`入口。父类为`groovy.lang.Script`。
* `Fibonacci$_run_closure1.class`: 处理`10.times{}`括号中的每次操作。父类为`groovy.lang.Closure`。目前不用深究，后面会介绍关于闭包的内容。

![groovyc-fibonacci](/images/groovy-demo/groovyc-fibonacci.png)

用`java`命令直接运行`Fibonacci.class`字节码，
```
 java -cp ${GROOVY_HOME}/lib/groovy-2.5.4.jar:bin Fibonacci
```

注意，执行的时候要带上groovy的库包`groovy-2.5.4.jar`。
![groovy-java-fibonacci](/images/groovy-demo/groovy-java-fibonacci.png)

所以非常重要的一点是，
> groovy本质就是就是在java基础上的一个大语法糖。在后台，所有的groovy代码都以java类的形式在jvm中运行。

而之前用`groovy`命令直接运行`.groovy`脚本，在这种方式下，没有生成任何`.class`文件，但是根据`.groovy`文件生成了一个`java.lang.Class`对象的实例。做这件事的是`Groovy ClassLoader`。
1. `xxx.groovy`被传递给groovy的转换器
2. 转换器产生一个抽象语法树（AST）来表示在`xxx.groovy`中的所有代码
3. Groovy类生成器根据AST产生java字节码，可能是一个或多个`java.lang.Class`对象的实例
4. 运行时通过`Class`对象实例反射调用类中的字段或函数

### 反射赋予了groovy动态性
假设groovy代码中有一个`foo()`函数，groovy产生的字节码像下面这样，反射调用`foo()`函数，
```
getMetaClass().invokeMethod(this, "foo", EMPTY_PARAMS_ARRAY)
```

之所以groovy能像一个动态语言一样，在下一行增加新方法，新成员，都是通过反射做到的。但反射的特性本质上还是属于java的。

### groovy动态性更进一步：将任意字符串当成代码来执行
定义下面这个字符串，
```
def code = '5.1 + 1'
```

用`evaluat()`函数执行该字符串，可以得到正确的计算结果，
```
println evaluat(code)
```

这种特性，让groovy能像动态脚本语言一样使用，虽然本质上和java一样是一个一般的编程语言。


### 一个标准的ant`build.xml`构建文件
假设我`groovy-demo`项目根目录还保持之前的结构，此时我增加一个`build.xml`构建文件，以及一个`conf.properties`配置文件。
```
.
├── LICENSE
├── README.md
├── bin
│   ├── Fibonacci$_run_closure1.class
│   ├── Fibonacci.class
│   └── HelloWorld.class
├── build.xml
├── conf.properties
└── src
    ├── main
    │   ├── groovy
    │   │   ├── Fibonacci.groovy
    │   │   └── HelloWorld.groovy
    │   └── resources
    └── test
```

`build.xml`构建文件如下所示，主要就是`compile`和`test`两个任务，分别负责编译和运行代码。
```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<project name="groovy-demo" basedir="." default="test">
    <property file="conf.properties"/>
    <property name="groovy_lib" value="/usr/local/groovy-2.5.4/lib/groovy-2.5.4.jar"/>
    <property name="groovy_ant_lib" value="/usr/local/groovy-2.5.4/lib/groovy-ant-2.5.4.jar"/>
    <property name="class_dir" value="bin"/>
    <property name="src_dir" value="src/main/groovy"/>

    <path id="classpath">
        <pathelement location="${groovy_lib}"/>
        <pathelement location="${groovy_ant_lib}"/>
        <pathelement location="${class_dir}"/>
    </path>

    <taskdef name="groovyc" classname="org.codehaus.groovy.ant.Groovyc" classpathref="classpath"/>

    <target name="compile">
        <mkdir dir="${class_dir}"/>
        <groovyc srcdir="${src_dir}" destdir="${class_dir}" classpathref="classpath" includes="${to_compile}" fork="true" includeAntRuntime="false">
            <classpath refid="classpath"/>
        </groovyc>
    </target>

    <target name="test" depends="compile">
        <java classname="${to_launch}">
            <classpath refid="classpath"/>
        </java>
    </target>
</project>
```

需要编译的`.groovy`文件列表，和最终要执行的`.class`主入口在`conf.properties`里配置，
```
# list of files to compile, separated by space ' '
to_compile=Fibonacci.groovy HelloWorld.groovy

# the main access to run
to_launch=Fibonacci
```

在`build.xml`主要区别是定义了一个叫`groovyc`的`task`。它会调用`org.codehaus.groovy.ant.Groovyc`类来编译`.groovy`脚本。构建文件其他内容和普通Java项目大致相同。

### groovy用闭包实现函数式编程
比如说之前例子中处理循环10次的代码，
![closure-callback-1](images/groovy-demo/closure-callback-1.png)

groovy后台做的事情如下图所示，
![closure-callback-2](images/groovy-demo/closure-callback-2.png)
1. 花括号内的内容为`Closure`类对象。也就是`x++`。后台默认生成一个叫`call()`的方法。
2. 数字`10`这里也是对象，调用`times()`方法。
3. `times()`方法的参数是一个闭包，也就是刚才的`Closure`对象。它会调用闭包对象的`call()`方法很多次。次数就是数字对象决定的。
4. 然后闭包中的`x`其实来自于它的环境，就是前文声明的`x = 0`。

熟悉闭包的话就不难理解，关键点有2个，
1. 闭包能访问到它的上下文环境变量。
2. 闭包对象默认的`call()`方法这个细节向groovy程序员隐藏了。

再看看闭包上下文的范围，如下图所示，`Mother`类内部的`birth()`方法返回一个闭包对象。这个对象会罗列调用者的信息，以及部分环境信息。实验目的就是看看这些信息到底都是啥。
![groovy-closure-scope](images/groovy-demo/groovy-closure-scope.png)

从最后打印的内容反推，
1. 调用闭包对象的是`Script`对象，因为实际是在`Mother`对象下面的代码块中调用的。
2. 然后闭包能访问到`Mother`环境中`field`成员，以及闭包自身的`local`成员，还有闭包的参数`param`，这不奇怪。
3. 然后闭包中的`this`引用指的是闭包对象本身。
4. `this.owner`引用指向的是外部声明闭包的`Mother`对象。
5. 最后`foo()`函数比较复杂，按理说应该是`this.foo()`的简写。那么应该是调用的是闭包的`foo()`方法。但是groovy这里是将所有函数都代理给声明闭包的对象，这里也就是`Mother`类。


### 但函数式编程让代码变得很难理解
下图中，`Drawing`类持有一个`Shape`类型对象的集合，它的`accept()`方法遍历这个集合，逐个调用`Shape`对象的`accept()`方法。Drawing类的`accept()`方法接受一个闭包作为参数，然后把这个闭包也作为参数传递给每个`Shape`的`accept()`方法。然后在Shape的`accept()`方法里，会回调这个闭包，然后将自身引用`this`作为参数传递给闭包。

实际应用中，这个闭包负责传递进来的参数对象的名称，再调用`area()`方法计算面积。

![groovy-visitor](images/groovy-demo/groovy-visitor.png)

但是，像这样的Visitor模式自己写完，估计2个月之后自己一下子也看不懂。
