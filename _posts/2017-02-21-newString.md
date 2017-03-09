---
layout: post
title: "The Memory Model of the Immutable String Object"
date: 2016-2-21 21:07:40
author: "Wei SHEN"
categories: ["java"]
tags: ["string","memory model","jvm"]
---

### 摘要

代码中出现的字符串字面量（两个双引号括起来的内容）都会被编译进class文件的常量池。然后在加载这个class文件的时候在堆中创建一个对应的字符串实例。方法区的运行时常量池会引用这个对象，字符串常量池也会驻留这个对象。

### 几个关键示例的解释
下面的`常量池`指代的都是`字符串常量池`。不是方法区的`运行时常量池`。

用字面量`aaa`给`String`变量赋值的时候，这个`aaa`会在编译class文件的时候，被写进class文件的常量池。然后在第一次用到这个类的时候，在堆上创建一个新的`aaa`字符串对象，然后方法区的运行时常量池会引用这个对象，字符串常量池也会驻留这个对象。然后，运行时运行到这行代码时，当前栈帧的局部变量表会创建`sa`变量，它的引用也指向堆中的这个`aaa`对象。
```java
String sa = "aaa"; // heap里有一个"aaa"对象。常量池驻留了这个对象。
System.out.println(sa == sa.intern()); // true
```

但用`new`关键字创建的字符串就不一样了。
* heap里有两个bbb对象。
* 一号"bbb"对象对应class文件里的bbb字面量常量。
* 二号"bbb"对象是new关键字和String构造函数创建的。
* sb引用的是二号"bbb"对象。
* 常量池里驻留引用的是一号"bbb"对象。

具体看下面这个例子，`intern()`测试，表明sb变量引用的不是字符串变量池里驻留的那个`bbb`对象。
```java
// heap里有两个bbb对象。
// 一号"bbb"对象对应class文件里的bbb字面量。
// 二号"bbb"对象是new关键字和String构造函数创建的。
// sb引用的是二号"bbb"对象。
// 常量池里驻留引用的是一号"bbb"对象。
String sb = new String("bbb");
System.out.println(sb == sb.intern()); // false
```

在拼接字符串的时候，也是一样，只要是用到的字面量（用两个引号括起来）都会被编译到class文件的常量池。在加载class文件的时候就会在堆中创建字符串实例，然后字符串常量池会驻留这个对象（保留一份引用）。

需要注意的是，用字面量拼接的时候，编译器会`字面量折叠`。`"c" + "cc"`编译器会直接编译成`ccc`，保留在class文件里。用`javap -v`就能查看class文件里的详细内容。
```java
String sc = "c" + "cc"; // c和cc被编译器折叠为ccc。加载class文件的时候驻留字符串常量池。
System.out.println(sc == sc.intern()); // true
String sd = new String("d") + new String("dd"); // d和dd会驻留字符串常量池。ddd不会。虽然运行时sd指向ddd对象，但不会驻留到字符串常量池。class文件的常量池里也没有ddd字符串。
System.out.println(sd == sd.intern()); // true
```

### 注意字面量驻留字符串常量池的时机

看下面这段代码，

首先代码里有这么几个字符串字面量：`static`,`he`,`llo`,`hello`，他们都进入了class文件的常量池。

然后，加载类的阶段(运行时)，在 **`resolve(constant pool resolution)`** 阶段，并不是所有的class文件常量池里的所有字符串字面量都在堆中创建了对应的对象，

如果在此之前，这个字符串已经被`intern()`方法驻留了，或者出于别的原因字符串常量池中已经有了`hello`字符串，这个class文件中的字面量就不会在堆中创建一个新的`hello`对象。 而是直接引用这个对象的地址。

注意下面这个场景中的第`(6)`步，`s1==s1.intern()`的结果是`true`。证明虽然因为第`(4)`步中的`hello`字面量出现在了class文件的常量池中，但并没有在堆中创建新对象。

```java
class NewTest1{
    public static String s ="static";  // (1)
    public static void main(String[] args) {
        String s1 =new String("he")+new String("llo"); //(2) he和llo都驻留字符串常量池了，但hello没有驻留字符串常量池。
        s1.intern();   // (3) s1引用的hello对象被驻留到了字符串常量池
        String s2 ="hello";  // (4) 因为字符串常量池已经驻留了hello对象，所以这一步不创建新hello对象了。s2直接引用字符常量池中的hello对象。
        System.out.println(s1==s2); // true (5)
        System.out.println(s1 == s1.intern()); // true (6) 证明s1.intern()执行之前，字符串常量池里没有驻留hello字符串
        System.out.println(s1 == s2.intern()); // true (7)
        System.out.println(s2 == s2.intern()); // true (8)
    }
}
```

这时候如果把第`(4)`步移到最开头，情况就都变了，先执行第`(4)`步，class文件里的hello字面量常量就驻留在字符串常量池了。
```java
class NewTest1{
    public static String s ="static";  // (1)
    public static void main(String[] args) {
        String s2 ="hello";  // (4) 先执行这一步，class文件里的hello字面量常量就驻留在字符串常量池了。
        String s1 =new String("he")+new String("llo"); //(2) he和llo照样驻留字符串常量池了。这里拼接的结果是创建了一个新的String对象。
        s1.intern();   // (3) s1引用的hello对象被驻留到了字符串常量池

        System.out.println(s1==s2); // true (5)
        System.out.println(s1 == s1.intern()); // false (6) 证明s1.intern()执行之前，字符串常量池里已经驻留了hello字符串
        System.out.println(s1 == s2.intern()); // false (7)
        System.out.println(s2 == s2.intern()); // true (8)
    }
}
```

注意看`Java虚拟机规范`中的这一段：
```
● The Java programming language requires that identical string literals (that is, literals that contain the same sequence of characters) must refer to the same instance of class String. In addition, if the method String.intern is called on any string, the result is a reference to the same class instance that would be returned if that string appeared as a literal. Thus,
Java代码  收藏代码
("a" + "b" + "c").intern() == "abc"  

must have the value true.

● To derive a string literal, the Java virtual machine examines the sequence of characters given by the CONSTANT_String_info structure.

  ○ If the method String.intern has previously been called on an instance of class String containing a sequence of Unicode characters identical to that given by the CONSTANT_String_info structure, then the result of string literal derivation is a reference to that same instance of class String.

  ○ Otherwise, a new instance of class String is created containing the sequence of Unicode characters given by the CONSTANT_String_info structure; that class instance is the result of string literal derivation. Finally, the intern method of the new String instance is invoked.
```

### 怎么理解class文件里的内容
看下面这段`javap -v`打印出的信息，

```bash
0: new  #2; //class java/lang/String  
3: dup  
4: ldc  #3; //String xyz  
6: invokespecial    #4; //Method java/lang/String."<init>":(Ljava/lang/String;)V  
9: astore_1  
```

R大解释这里发生了一些什么事：

在Java语言里，“new”表达式是负责创建实例的，其中会调用构造器去对实例做初始化；构造器自身的返回值类型是void，并不是“构造器返回了新创建的对象的引用”，而是new表达式的值是新创建的对象的引用。

对应的，在JVM里，“new”字节码指令只负责把实例创建出来（包括分配空间、设定类型、所有字段设置默认值等工作），并且把指向新创建对象的引用压到操作数栈顶。此时该引用还不能直接使用，处于未初始化状态（uninitialized）；如果某方法a含有代码试图通过未初始化状态的引用来调用任何实例方法，那么方法a会通不过JVM的字节码校验，从而被JVM拒绝执行。
能对未初始化状态的引用做的唯一一种事情就是通过它调用实例构造器，在Class文件层面表现为特殊初始化方法“<init>”。实际调用的指令是invokespecial，而在实际调用前要把需要的参数按顺序压到操作数栈上。在上面的字节码例子中，压参数的指令包括dup和ldc两条，分别把隐藏参数（新创建的实例的引用，对于实例构造器来说就是“this”）与显式声明的第一个实际参数（"xyz"常量的引用）压到操作数栈上。

在构造器返回之后，新创建的实例的引用就可以正常使用了。

对于下面这段代码：
```java
String s1 = "a";  
String s2 = s1.concat("");  
String s3 = null;  
new String(s1);  
```

R大的分析如下，

这段代码会涉及3个String类型的变量，
1、s1，指向下面String实例的1
2、s2，指向与s1相同
3、s3，值为null，不指向任何实例

以及3个String实例，
1、"a"字面量对应的驻留的字符串常量的String实例
2、""字面量对应的驻留的字符串常量的String实例
（String.concat()是个有趣的方法，当发现传入的参数是空字符串时会返回this，所以这里不会额外创建新的String实例）
3、通过new String(String)创建的新String实例；没有任何变量指向它。


### Java语言规范里关于String的一些定义

```
2.3 Literals

A literal is the source code representation of a value of a primitive type (§2.4.1), the String type (§2.4.8), or the null type (§2.4). String literals and, more generally, strings that are the values of constant expressions are "interned" so as to share unique instances, using the method String.intern.

The null type has one value, the null reference, denoted by the literal null. The boolean type has two values, denoted by the literals true and false.

2.4.8 The Class String

Instances of class String represent sequences of Unicode characters (§2.1). A String object has a constant, unchanging value. String literals (§2.3) are references to instances of class String.

2.17.6 Creation of New Class Instances

A new class instance is explicitly created when one of the following situations occurs:

Evaluation of a class instance creation expression creates a new instance of the class whose name appears in the expression.
Invocation of the newInstance method of class Class creates a new instance of the class represented by the Class object for which the method was invoked.

A new class instance may be implicitly created in the following situations:

Loading of a class or interface that contains a String literal may create a new String object (§2.4.8) to represent that literal. This may not occur if the a String object has already been created to represent a previous occurrence of that literal, or if the String.intern method has been invoked on a String object representing the same string as the literal.
Execution of a string concatenation operator that is not part of a constant expression sometimes creates a new String object to represent the result. String concatenation operators may also create temporary wrapper objects for a value of a primitive type (§2.4.1).

Each of these situations identifies a particular constructor to be called with specified arguments (possibly none) as part of the class instance creation process.

5.1 The Runtime Constant Pool

...

● A string literal (§2.3) is derived from a CONSTANT_String_info structure (§4.4.3) in the binary representation of a class or interface. The CONSTANT_String_info structure gives the sequence of Unicode characters constituting the string literal.

● The Java programming language requires that identical string literals (that is, literals that contain the same sequence of characters) must refer to the same instance of class String. In addition, if the method String.intern is called on any string, the result is a reference to the same class instance that would be returned if that string appeared as a literal. Thus,

("a" + "b" + "c").intern() == "abc"  

must have the value true.

● To derive a string literal, the Java virtual machine examines the sequence of characters given by the CONSTANT_String_info structure.

  ○ If the method String.intern has previously been called on an instance of class String containing a sequence of Unicode characters identical to that given by the CONSTANT_String_info structure, then the result of string literal derivation is a reference to that same instance of class String.

  ○ Otherwise, a new instance of class String is created containing the sequence of Unicode characters given by the CONSTANT_String_info structure; that class instance is the result of string literal derivation. Finally, the intern method of the new String instance is invoked.

...

The remaining structures in the constant_pool table of the binary representation of a class or interface, the CONSTANT_NameAndType_info (§4.4.6) and CONSTANT_Utf8_info (§4.4.7) structures are only used indirectly when deriving symbolic references to classes, interfaces, methods, and fields, and when deriving string literals.
```

下面是Java Doc关于`intern()`方法的描述，

```
public String intern()

    Returns a canonical representation for the string object.

    A pool of strings, initially empty, is maintained privately by the class String.

    When the intern method is invoked, if the pool already contains a string equal to this String object as determined by the equals(Object) method, then the string from the pool is returned. Otherwise, this String object is added to the pool and a reference to this String object is returned.

    It follows that for any two strings s and t, s.intern() == t.intern() is true if and only if s.equals(t) is true.

    All literal strings and string-valued constant expressions are interned. String literals are defined in §3.10.5 of the Java Language Specification

    Returns:
        a string that has the same contents as this string, but is guaranteed to be from a pool of unique strings.
```

### 证据清单
我的`aaa`, `bbb`, `ccc`, `ddd`例子的代码，下面是用`javap -v`编译的结果：

```bash
Classfile /Users/Wei/JavaCode/bin/com/ciaoshen/test/TestString2.class
  Last modified 2017-02-21; size 1046 bytes
  MD5 checksum 150ff2b127881114fea7a5cb88d0357e
  Compiled from "TestString2.java"
final class com.ciaoshen.test.TestString2
  minor version: 0
  major version: 52
  flags: ACC_FINAL, ACC_SUPER
Constant pool:
   #1 = Methodref          #17.#30        // java/lang/Object."<init>":()V
   #2 = String             #31            // aaa
   #3 = Fieldref           #32.#33        // java/lang/System.out:Ljava/io/PrintStream;
   #4 = Methodref          #6.#34         // java/lang/String.intern:()Ljava/lang/String;
   #5 = Methodref          #35.#36        // java/io/PrintStream.println:(Z)V
   #6 = Class              #37            // java/lang/String
   #7 = String             #38            // bbb
   #8 = Methodref          #6.#39         // java/lang/String."<init>":(Ljava/lang/String;)V
   #9 = String             #40            // ccc
  #10 = Class              #41            // java/lang/StringBuilder
  #11 = Methodref          #10.#30        // java/lang/StringBuilder."<init>":()V
  #12 = String             #42            // d
  #13 = Methodref          #10.#43        // java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
  #14 = String             #44            // dd
  #15 = Methodref          #10.#45        // java/lang/StringBuilder.toString:()Ljava/lang/String;
  #16 = Class              #46            // com/ciaoshen/test/TestString2
  #17 = Class              #47            // java/lang/Object
  #18 = Utf8               <init>
  #19 = Utf8               ()V
  #20 = Utf8               Code
  #21 = Utf8               LineNumberTable
  #22 = Utf8               main
  #23 = Utf8               ([Ljava/lang/String;)V
  #24 = Utf8               StackMapTable
  #25 = Class              #48            // "[Ljava/lang/String;"
  #26 = Class              #37            // java/lang/String
  #27 = Class              #49            // java/io/PrintStream
  #28 = Utf8               SourceFile
  #29 = Utf8               TestString2.java
  #30 = NameAndType        #18:#19        // "<init>":()V
  #31 = Utf8               aaa
  #32 = Class              #50            // java/lang/System
  #33 = NameAndType        #51:#52        // out:Ljava/io/PrintStream;
  #34 = NameAndType        #53:#54        // intern:()Ljava/lang/String;
  #35 = Class              #49            // java/io/PrintStream
  #36 = NameAndType        #55:#56        // println:(Z)V
  #37 = Utf8               java/lang/String
  #38 = Utf8               bbb
  #39 = NameAndType        #18:#57        // "<init>":(Ljava/lang/String;)V
  #40 = Utf8               ccc
  #41 = Utf8               java/lang/StringBuilder
  #42 = Utf8               d
  #43 = NameAndType        #58:#59        // append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
  #44 = Utf8               dd
  #45 = NameAndType        #60:#54        // toString:()Ljava/lang/String;
  #46 = Utf8               com/ciaoshen/test/TestString2
  #47 = Utf8               java/lang/Object
  #48 = Utf8               [Ljava/lang/String;
  #49 = Utf8               java/io/PrintStream
  #50 = Utf8               java/lang/System
  #51 = Utf8               out
  #52 = Utf8               Ljava/io/PrintStream;
  #53 = Utf8               intern
  #54 = Utf8               ()Ljava/lang/String;
  #55 = Utf8               println
  #56 = Utf8               (Z)V
  #57 = Utf8               (Ljava/lang/String;)V
  #58 = Utf8               append
  #59 = Utf8               (Ljava/lang/String;)Ljava/lang/StringBuilder;
  #60 = Utf8               toString
{
  com.ciaoshen.test.TestString2();
    descriptor: ()V
    flags:
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 3: 0

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=4, locals=5, args_size=1
         0: ldc           #2                  // String aaa
         2: astore_1
         3: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
         6: aload_1
         7: aload_1
         8: invokevirtual #4                  // Method java/lang/String.intern:()Ljava/lang/String;
        11: if_acmpne     18
        14: iconst_1
        15: goto          19
        18: iconst_0
        19: invokevirtual #5                  // Method java/io/PrintStream.println:(Z)V
        22: new           #6                  // class java/lang/String
        25: dup
        26: ldc           #7                  // String bbb
        28: invokespecial #8                  // Method java/lang/String."<init>":(Ljava/lang/String;)V
        31: astore_2
        32: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
        35: aload_2
        36: aload_2
        37: invokevirtual #4                  // Method java/lang/String.intern:()Ljava/lang/String;
        40: if_acmpne     47
        43: iconst_1
        44: goto          48
        47: iconst_0
        48: invokevirtual #5                  // Method java/io/PrintStream.println:(Z)V
        51: ldc           #9                  // String ccc
        53: astore_3
        54: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
        57: aload_3
        58: aload_3
        59: invokevirtual #4                  // Method java/lang/String.intern:()Ljava/lang/String;
        62: if_acmpne     69
        65: iconst_1
        66: goto          70
        69: iconst_0
        70: invokevirtual #5                  // Method java/io/PrintStream.println:(Z)V
        73: new           #10                 // class java/lang/StringBuilder
        76: dup
        77: invokespecial #11                 // Method java/lang/StringBuilder."<init>":()V
        80: new           #6                  // class java/lang/String
        83: dup
        84: ldc           #12                 // String d
        86: invokespecial #8                  // Method java/lang/String."<init>":(Ljava/lang/String;)V
        89: invokevirtual #13                 // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        92: new           #6                  // class java/lang/String
        95: dup
        96: ldc           #14                 // String dd
        98: invokespecial #8                  // Method java/lang/String."<init>":(Ljava/lang/String;)V
       101: invokevirtual #13                 // Method java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
       104: invokevirtual #15                 // Method java/lang/StringBuilder.toString:()Ljava/lang/String;
       107: astore        4
       109: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
       112: aload         4
       114: aload         4
       116: invokevirtual #4                  // Method java/lang/String.intern:()Ljava/lang/String;
       119: if_acmpne     126
       122: iconst_1
       123: goto          127
       126: iconst_0
       127: invokevirtual #5                  // Method java/io/PrintStream.println:(Z)V
       130: return
      LineNumberTable:
        line 5: 0
        line 6: 3
        line 13: 22
        line 14: 32
        line 16: 51
        line 17: 54
        line 18: 73
        line 19: 109
        line 20: 130
      StackMapTable: number_of_entries = 8
        frame_type = 255 /* full_frame */
          offset_delta = 18
          locals = [ class "[Ljava/lang/String;", class java/lang/String ]
          stack = [ class java/io/PrintStream ]
        frame_type = 255 /* full_frame */
          offset_delta = 0
          locals = [ class "[Ljava/lang/String;", class java/lang/String ]
          stack = [ class java/io/PrintStream, int ]
        frame_type = 255 /* full_frame */
          offset_delta = 27
          locals = [ class "[Ljava/lang/String;", class java/lang/String, class java/lang/String ]
          stack = [ class java/io/PrintStream ]
        frame_type = 255 /* full_frame */
          offset_delta = 0
          locals = [ class "[Ljava/lang/String;", class java/lang/String, class java/lang/String ]
          stack = [ class java/io/PrintStream, int ]
        frame_type = 255 /* full_frame */
          offset_delta = 20
          locals = [ class "[Ljava/lang/String;", class java/lang/String, class java/lang/String, class java/lang/String ]
          stack = [ class java/io/PrintStream ]
        frame_type = 255 /* full_frame */
          offset_delta = 0
          locals = [ class "[Ljava/lang/String;", class java/lang/String, class java/lang/String, class java/lang/String ]
          stack = [ class java/io/PrintStream, int ]
        frame_type = 255 /* full_frame */
          offset_delta = 55
          locals = [ class "[Ljava/lang/String;", class java/lang/String, class java/lang/String, class java/lang/String, class java/lang/String ]
          stack = [ class java/io/PrintStream ]
        frame_type = 255 /* full_frame */
          offset_delta = 0
          locals = [ class "[Ljava/lang/String;", class java/lang/String, class java/lang/String, class java/lang/String, class java/lang/String ]
          stack = [ class java/io/PrintStream, int ]
}
SourceFile: "TestString2.java"
```
