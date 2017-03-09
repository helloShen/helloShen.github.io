---
layout: post
title: "Array is Object in Java?"
date: 2016-08-21 22:57:58
author: "Wei SHEN"
categories: ["java"]
tags: ["array","oop"]
description: >
---

Java Language Specification 里关于Array还有这么一段：
> **Every array has an associated Class object**, shared with all other arrays with the same component type. [This] acts as if: the direct superclass of an array type is Object [and] every array type implements the interfaces Cloneable and java.io.Serializable.

数组对象不是从某个类实例化来的，而是由JVM直接创建的。实际上也没有Array这个类（有是有，但只是java.lang.reflect包里的一个反射类）。但每个数组都对应一个Class对象。通过RTTI（Run-Time Type Information）可以直接检查Array的运行时类型，以及它的签名，它的基类，还有其他很多事。
```java
char[] c={'a','b','c'};

System.out.println(c instanceof char[]);
System.out.println(c instanceof Object);
System.out.println(c.getClass().getName());
System.out.println(c.getClass().getSuperclass().getName());

//Output:
//true
//true
//[C
//java.lang.Object
```

这里“[C”就是char[]的运行时类型签名。而且是它的全限定名，没有包名。每种类型的数组的签名还不同，看下面，
```java
char[] c={'a','b','c'};
int[] i={1,2,3};
long[] l={1l,2l,3l};
float[] f={1f,2f,3f};
String[] s={"aaa","bbb","ccc"};

System.out.println(c.getClass().getName());
System.out.println(i.getClass().getName());
System.out.println(l.getClass().getName());
System.out.println(f.getClass().getName());
System.out.println(s.getClass().getName());

//Output:
//[C    //char[]
//[I    //int[]
//[J    //long[]
//[F    //float[]
//[J    //long[]
//[Ljava.lang.String;    //String[]
```

反过来，还能用运行时类型签名得到Array的Class对象，
```java
try{
    Class<?> theClass = Class.forName("[I");
    System.out.println(theClass.getName());
}catch(ClassNotFoundException cnfe){}

//output:
//[I
```

刚才说java.lang.reflect包里有一个反射类Array (Java Platform SE 7 )，也可以在runtime对编译时不知道类型的Array做很多事。
