---
layout: post
title: "How Annotation works in Java？"
date: 2016-10-28 13:49:07
author: "Wei SHEN"
categories: ["Java"]
tags: ["Annotations","Dynamic_Proxies"]
description: >
  当我们使用接口的时候，Java通过动态代理的方式自动创建了实现注释接口的代理类，然后创建了这个类的实例（对于当前的实体来说，例如类、方法、属性域等，这个代理对象是单例的），然后对该代理实例的属性赋值，这样就可以在程序运行时通过反射获取到注解的配置信息。
---

这篇扒一扒Annotation注释。Annotation就不是语法糖这么简单了。转一篇知乎上的一个回答，把Java注释是怎么实现的讲得很清楚。作者的主要结论就是四个字：**“动态代理”**！

**我们定义的注释都是继承自`java.lang.annotation.Annotation`接口的接口。当我们使用接口的时候，Java通过动态代理的方式自动创建了实现注释接口的代理类，然后创建了这个类的实例（对于当前的实体来说，例如类、方法、属性域等，这个代理对象是单例的），然后对该代理实例的属性赋值，这样就可以在程序运行时通过反射获取到注解的配置信息。**

Java的Annotation是一个神奇的特性。通过Annotation直接催生了JUnit的单元测试框架，Hibernate的自动数据访问，以及Sprint的AOP(面向切面编程)的代码注入技术。Annotation以动态代理，自动生成字节码技术为支点，让整个Java编程“动态起来”。

[**《java注解是怎么实现的？》**  作者：曹旭东](https://www.zhihu.com/question/24401191/answer/37601385)

### java注解是怎么实现的？
首先java中的注解是一种继承自接口`java.lang.annotation.Annotation`的特殊接口。参见JDK文档。
* An annotation type declaration specifies a new annotation type, a special kind of interface type. To distinguish an annotation type declaration from a normal interface declaration, the keyword interface is preceded by an at-sign (@).

下面看一下具体示例。
```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2015/1/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestAnnotation {

    int count() default 1;

}
```

经过编译之后，注解`TestAnnotation`的字节码是这样的：
```java
Classfile /e:/workspace/intellij/SpringTest/target/classes/TestAnnotation.class
  Last modified 2015-1-18; size 379 bytes
  MD5 checksum 200dc3a75216b7a88ae17873d5dffd4f
  Compiled from "TestAnnotation.java"
public interface TestAnnotation extends java.lang.annotation.Annotation
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_INTERFACE, ACC_ABSTRACT, ACC_ANNOTATION
Constant pool:
   #1 = Class              #14            // TestAnnotation
   #2 = Class              #15            // java/lang/Object
   #3 = Class              #16            // java/lang/annotation/Annotation
   #4 = Utf8               SourceFile
   #5 = Utf8               TestAnnotation.java
   #6 = Utf8               RuntimeVisibleAnnotations
   #7 = Utf8               Ljava/lang/annotation/Target;
   #8 = Utf8               value
   #9 = Utf8               Ljava/lang/annotation/ElementType;
  #10 = Utf8               TYPE
  #11 = Utf8               Ljava/lang/annotation/Retention;
  #12 = Utf8               Ljava/lang/annotation/RetentionPolicy;
  #13 = Utf8               RUNTIME
  #14 = Utf8               TestAnnotation
  #15 = Utf8               java/lang/Object
  #16 = Utf8               java/lang/annotation/Annotation
{
}
SourceFile: "TestAnnotation.java"
RuntimeVisibleAnnotations:
  0: #7(#8=[e#9.#10])
  1: #11(#8=e#12.#13)
```

从反编译后的信息中可以看出，注解就是一个继承自`java.lang.annotation.Annotation`的接口。

那么接口怎么能够设置属性呢？**简单来说就是java通过动态代理的方式为你生成了一个实现了"接口"`TestAnnotation`的实例（对于当前的实体来说，例如类、方法、属性域等，这个代理对象是单例的）**，然后对该代理实例的属性赋值，这样就可以在程序运行时（如果将注解设置为运行时可见的话）通过反射获取到注解的配置信息。

具体来说是怎么实现的呢？

写一个使用该注解的类：
```java
import java.io.IOException;

/**
 * Created by Administrator on 2015/1/18.
 */
@TestAnnotation(count = 0x7fffffff)
public class TestMain {

    public static void main(String[] args) throws InterruptedException, NoSuchFieldException, IllegalAccessException, IOException {
        TestAnnotation annotation = TestMain.class.getAnnotation(TestAnnotation.class);
        System.out.println(annotation.count());
        System.in.read();
    }

}
```

反编译一下这段代码：
```java
Classfile /e:/workspace/intellij/SpringTest/target/classes/TestMain.class
  Last modified 2015-1-20; size 1006 bytes
  MD5 checksum a2d5367ea568240f078d5fb1de917550
  Compiled from "TestMain.java"
public class TestMain
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #10.#34        // java/lang/Object."<init>":()V
   #2 = Class              #35            // TestMain
   #3 = Class              #36            // TestAnnotation
   #4 = Methodref          #37.#38        // java/lang/Class.getAnnotation:(Ljava/lang/Class;)Ljava/lang/annotation/Annotation;
   #5 = Fieldref           #39.#40        // java/lang/System.out:Ljava/io/PrintStream;
   #6 = InterfaceMethodref #3.#41         // TestAnnotation.count:()I
   #7 = Methodref          #42.#43        // java/io/PrintStream.println:(I)V
   #8 = Fieldref           #39.#44        // java/lang/System.in:Ljava/io/InputStream;
   #9 = Methodref          #45.#46        // java/io/InputStream.read:()I
  #10 = Class              #47            // java/lang/Object
  #11 = Utf8               <init>
  #12 = Utf8               ()V
  #13 = Utf8               Code
  #14 = Utf8               LineNumberTable
  #15 = Utf8               LocalVariableTable
  #16 = Utf8               this
  #17 = Utf8               LTestMain;
  #18 = Utf8               main
  #19 = Utf8               ([Ljava/lang/String;)V
  #20 = Utf8               args
  #21 = Utf8               [Ljava/lang/String;
  #22 = Utf8               annotation
  #23 = Utf8               LTestAnnotation;
  #24 = Utf8               Exceptions
  #25 = Class              #48            // java/lang/InterruptedException
  #26 = Class              #49            // java/lang/NoSuchFieldException
  #27 = Class              #50            // java/lang/IllegalAccessException
  #28 = Class              #51            // java/io/IOException
  #29 = Utf8               SourceFile
  #30 = Utf8               TestMain.java
  #31 = Utf8               RuntimeVisibleAnnotations
  #32 = Utf8               count
  #33 = Integer            2147483647
  #34 = NameAndType        #11:#12        // "<init>":()V
  #35 = Utf8               TestMain
  #36 = Utf8               TestAnnotation
  #37 = Class              #52            // java/lang/Class
  #38 = NameAndType        #53:#54        // getAnnotation:(Ljava/lang/Class;)Ljava/lang/annotation/Annotation;
  #39 = Class              #55            // java/lang/System
  #40 = NameAndType        #56:#57        // out:Ljava/io/PrintStream;
  #41 = NameAndType        #32:#58        // count:()I
  #42 = Class              #59            // java/io/PrintStream
  #43 = NameAndType        #60:#61        // println:(I)V
  #44 = NameAndType        #62:#63        // in:Ljava/io/InputStream;
  #45 = Class              #64            // java/io/InputStream
  #46 = NameAndType        #65:#58        // read:()I
  #47 = Utf8               java/lang/Object
  #48 = Utf8               java/lang/InterruptedException
  #49 = Utf8               java/lang/NoSuchFieldException
  #50 = Utf8               java/lang/IllegalAccessException
  #51 = Utf8               java/io/IOException
  #52 = Utf8               java/lang/Class
  #53 = Utf8               getAnnotation
  #54 = Utf8               (Ljava/lang/Class;)Ljava/lang/annotation/Annotation;
  #55 = Utf8               java/lang/System
  #56 = Utf8               out
  #57 = Utf8               Ljava/io/PrintStream;
  #58 = Utf8               ()I
  #59 = Utf8               java/io/PrintStream
  #60 = Utf8               println
  #61 = Utf8               (I)V
  #62 = Utf8               in
  #63 = Utf8               Ljava/io/InputStream;
  #64 = Utf8               java/io/InputStream
  #65 = Utf8               read
{
  public TestMain();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 7: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   LTestMain;

  public static void main(java.lang.String[]) throws java.lang.InterruptedException, java.lang.NoSuchFieldException, java.lang.IllegalAccessException, java.io.IOException;
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=1
         0: ldc           #2                  // class TestMain
         2: ldc           #3                  // class TestAnnotation
         4: invokevirtual #4                  // Method java/lang/Class.getAnnotation:(Ljava/lang/Class;)Ljava/lang/annotation/Annotation;
         7: checkcast     #3                  // class TestAnnotation
        10: astore_1
        11: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
        14: aload_1
        15: invokeinterface #6,  1            // InterfaceMethod TestAnnotation.count:()I
        20: invokevirtual #7                  // Method java/io/PrintStream.println:(I)V
        23: getstatic     #8                  // Field java/lang/System.in:Ljava/io/InputStream;
        26: invokevirtual #9                  // Method java/io/InputStream.read:()I
        29: pop
        30: return
      LineNumberTable:
        line 10: 0
        line 11: 11
        line 12: 23
        line 13: 30
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      31     0  args   [Ljava/lang/String;
           11      20     1 annotation   LTestAnnotation;
    Exceptions:
      throws java.lang.InterruptedException, java.lang.NoSuchFieldException, java.lang.IllegalAccessException, java.io.IOException
}
SourceFile: "TestMain.java"
RuntimeVisibleAnnotations:
  0: #23(#32=I#33)
```

最后一行的代码说明，注解`TestAnnotation`的属性设置是在编译时就确定了的。

然后，运行上面的程序，通过CLHSDB在eden区找到注解实例，
```bash
hsdb> scanoops 0x00000000e1b80000 0x00000000e3300000 TestAnnotation
0x00000000e1d6c360 com/sun/proxy/$Proxy1
```

类型`com/sun/proxy/$Proxy1`是jdk动态代理生成对象时的默认类型，其中`com.sun.proxy`是默认的包名，定义于`ReflectUtil`类的`PROXY_PACKAGE`字段中。代理类名`$PROXY1`包含两部分，其中前缀`$PROXY`是jdk种默认的代理类类名前缀（参见`java.lang.reflect.Proxy`类的javadoc），后的1是自增的结果。

下面看一下这个代理类的内容。运行java程序时添加参数`-Dsun.misc.ProxyGenerator.saveGeneratedFiles=true`可以将转储出jdk动态代理类的class文件。若是项目较大或是使用了各种框架的话，慎用此参数。
```java
Classfile /e:/workspace/intellij/SpringTest/target/classes/com/sun/proxy/$Proxy1.class
  Last modified 2015-1-19; size 2062 bytes
  MD5 checksum 7321e44402258ba9e061275e313c5c9f
public final class com.sun.proxy.$Proxy1 extends java.lang.reflect.Proxy implements TestAnnotation
  minor version: 0
  major version: 49
  flags: ACC_PUBLIC, ACC_FINAL
...
```

太长了，只截取一部分。从中可以看到，这个代理类实现了继承自`java.lang.reflect.Proxy`类，又实现了“接口”TestAnnotation。

接下来查看一下代理对象的内容：
```java
hsdb> inspect 0x00000000e1d6c360
instance of Oop for com/sun/proxy/$Proxy1 @ 0x00000000e1d6c360 @ 0x00000000e1d6c360 (size = 16)
_mark: 1
_metadata._compressed_klass: InstanceKlass for com/sun/proxy/$Proxy1
h: Oop for sun/reflect/annotation/AnnotationInvocationHandler @ 0x00000000e1ce7670 Oop for sun/reflect/annotation/Annota
tionInvocationHandler @ 0x00000000e1ce7670
其中，0xe1ce74e0是成员变量h的地址（h是定义在`java.lang.reflect.Proxy`类中的），通过查看类`AnnotationInvocationHandler`的源码可以知道注解的代理实例的值就存储在它的成员变量`memberValues`中，然后继续向下挖就好了：

hsdb> inspect 0x00000000e1ce7670
instance of Oop for sun/reflect/annotation/AnnotationInvocationHandler @ 0x00000000e1ce7670 @ 0x00000000e1ce7670 (size =
 24)
_mark: 1
_metadata._compressed_klass: InstanceKlass for sun/reflect/annotation/AnnotationInvocationHandler
type: Oop for java/lang/Class @ 0x00000000e1ccc5f8 Oop for java/lang/Class @ 0x00000000e1ccc5f8
memberValues: Oop for java/util/LinkedHashMap @ 0x00000000e1ce7548 Oop for java/util/LinkedHashMap @ 0x00000000e1ce7548
memberMethods: null null
hsdb> inspect 0x00000000e1ce7548
instance of Oop for java/util/LinkedHashMap @ 0x00000000e1ce7548 @ 0x00000000e1ce7548 (size = 56)
_mark: 1
_metadata._compressed_klass: InstanceKlass for java/util/LinkedHashMap
keySet: null null
values: null null
table: ObjArray @ 0x00000000e1ce75b8 Oop for [Ljava/util/HashMap$Node; @ 0x00000000e1ce75b8
entrySet: null null
size: 1
modCount: 1
threshold: 1
loadFactor: 0.75
head: Oop for java/util/LinkedHashMap$Entry @ 0x00000000e1ce75d0 Oop for java/util/LinkedHashMap$Entry @ 0x00000000e1ce7
5d0
tail: Oop for java/util/LinkedHashMap$Entry @ 0x00000000e1ce75d0 Oop for java/util/LinkedHashMap$Entry @ 0x00000000e1ce75d0
accessOrder: false
hsdb> inspect 0x00000000e1ce75d0
instance of Oop for java/util/LinkedHashMap$Entry @ 0x00000000e1ce75d0 @ 0x00000000e1ce75d0 (size = 40)
_mark: 1
_metadata._compressed_klass: InstanceKlass for java/util/LinkedHashMap$Entry
hash: 94852264
key: "count" @ 0x00000000e1bd7c90 Oop for java/lang/String @ 0x00000000e1bd7c90
value: Oop for java/lang/Integer @ 0x00000000e1ce7630 Oop for java/lang/Integer @ 0x00000000e1ce7630
next: null null
before: null null
after: null null
hsdb> inspect 0x00000000e1ce7630
instance of Oop for java/lang/Integer @ 0x00000000e1ce7630 @ 0x00000000e1ce7630 (size = 16)
_mark: 1
_metadata._compressed_klass: InstanceKlass for java/lang/Integer
value: 2147483647
```

最后可以看到，key=“count”, value=Integer(2147483647 = 0x7fffffff)，正是在TestMain中设置的值.

嗯，就这样吧。

### Annotaion源码中的直接证据
一下内容转自RednaxelaFX的博客，[**《Java annotation的实例是什么类的？》**](http://rednaxelafx.iteye.com/blog/1148983)

在OpenJDK 6里，sun.reflect.annotation.AnnotationParser的第254行：
```java
/**
 * Returns an annotation of the given type backed by the given
 * member -> value map.
 */  
public static Annotation annotationForMap(  
    Class type, Map<String, Object> memberValues)  
{  
    return (Annotation) Proxy.newProxyInstance(  
        type.getClassLoader(), new Class[] { type },  
        new AnnotationInvocationHandler(type, memberValues));  
}  
```

Annotation是基于动态代理的直接证据。
