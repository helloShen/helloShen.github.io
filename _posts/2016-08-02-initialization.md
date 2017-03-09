---
layout: post
title: "Class and Object Initialization in Java"
date: 2016-08-02 22:02:41
author: "Wei SHEN"
categories: ["java"]
tags: ["oop","memory model"]
images:
---

### [类的加载] & [对象初始化] 理论过程
一个类第一次被用到的时候，才被动态加载到JVM。一个类完整的过程分为如下三步：

1. **加载**：先找并加载**.class**文件里以字节码形式存在的**Class类的对象**。(类的元信息)
2. **链接**：为变量分配内存空间。
	2.1 **准备**：在方法区把类的静态变量的初始值设成零值（static final的静态常量可以在这个时候就赋值）。给类或接口，字段，类方法，接口方法四种元数据分配内存（也是在方法区的常量池）。具体方法是分配一个没有实际内容的**符号引用（Symbolic References）**。大白话说就是“先着茅坑不拉屎”。
	2.2 **解析**：开始解析。用**实际引用（Direct References）**替换上一步在方法区的常量池中“占茅坑”的符号引用。（也都是在方法区的常量池里）。到这一步都完全没有对象这回事。
3. **初始化**：这一步才真正开始赋值。
	3.1 静态语句块static{}和成员变量的"默认"赋值是一起执行的。具体谁先谁后按照在文件中出现的先后顺序。
	3.2 最后调用类的构造器来构造对象实例。

### 实验：对象的初始化顺序
下面这个实验，为了看得清楚，已经按照实际执行的先后顺序排列：

```java
/**
 *	工具类。只是为了在成员字段赋值的时候，打印目前在执行哪一步
 */
class Log{
    static String BaseFieldInit(){System.out.println("Base Normal Field");return "";}

    static String BaseStaticFieldInit(){System.out.println("Base Static Field");return "";}

    static String fieldInit(){System.out.println("Normal Field");return "";}

    static String staticFieldInit(){System.out.println("Static Field");return "";}
}
/**
 *	基类
 */
class Base {
    /*1*/ static {System.out.println("Base Static Block 1");}

    /*1*/ private static String staticValue=Log.BaseStaticFieldInit();

    /*1*/ static {System.out.println("Base Static Block 2");}

    /*3*/ {System.out.println("Base Normal Block 1");}

    /*3*/ private String value=Log.BaseFieldInit();

    /*3*/ {System.out.println("Base Normal Block 2");}

    /*4*/ Base(){System.out.println("Base Constructor");}
}
/**
 *	派生类
 */
public class TestInit extends Base{

    /*2*/ static {System.out.println("Static Block 1");}

    /*2*/ private static String staticValue=Log.staticFieldInit();

    /*2*/ static {System.out.println("Static Block 2");}

    /*5*/ {System.out.println("Normal Block 1");}

    /*5*/ private String value=Log.fieldInit();

    /*5*/ {System.out.println("Normal Block 2");}

    /*6*/ TestInit(){System.out.println("Constructor");}



    /**
     *  MAIN 主线程
     */
    public static void main(String[] args){
        TestInit ti=new TestInit();
    }
}
```

#### 实验结果
1. 基类静态代码块，基类静态成员字段 （并列优先级，按代码中出现先后顺序执行）
2. 派生类静态代码块，派生类静态成员字段 （并列优先级，按代码中出现先后顺序执行）
3. 基类普通代码块，基类普通成员字段 （并列优先级，按代码中出现先后顺序执行）
4. 基类构造函数
5. 派生类普通代码块，派生类普通成员字段 （并列优先级，按代码中出现先后顺序执行）
6. 派生类构造函数
