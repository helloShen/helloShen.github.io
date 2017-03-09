---
layout: post
title: "[Effective Java] Note: - Chapter-4-2: Composition is better than Inheritance"
date: 2017-02-10
author: "Wei SHEN"
categories: ["java","effective java"]
tags: ["composition","inheritance"]
description: >
  记住，让每个类都尽可能的自私。让每个类的成员都只为这个类工作。让每个类只为所在包工作。只在有明确理由的情况下，才提升某些类或成员的可访问等级。最后谨慎选择API。因为一旦公开你的API，就很难再改，它必须永远得到支持。
---

### 对象的基本概念
**对象拥有“状态”和“行为”**。
* **域** 定义状态。
* **方法** 定义行为。

所谓 **类** 就是一组拥有相同行为的对象。所以 **每个对象初始化的是它的“域”，但“方法”不需要初始化。** 因为同类对象的行为是不变的，只有状态是变化的。这就是OOP面向对象范式对“数据”和“过程”的抽象和封装方式。一般来说，对象的“状态”是需要对外隐藏的，只向外暴露部分“行为”为接口，提供某种服务。

下面是一个简单的示例，
```java
class Person {
    /**
     * 状态。同类别不同实例拥有不同的状态，所以需要初始化。
     */
    static final int MAX_AGE;
    static {
        MAX_AGE = 120;
    }
    int age;
    {
        age = 10;
    }
    public Person(int arg) {
        if (arg < 0) { arg = 0; }
        if (arg > MAX_AGE) { arg = MAX_AGE; }
        age = arg;
    }

    /**
     * 行为。同类实例都有相同行为。所以不需要初始化。
     */
    public void sayHello() {
        System.out.println("Hello, I am " + age + " years old!");
    }
}
```

### 对象域初始化的顺序
1. 先静态域（包括静态块。执行先后按照代码顺序。）
2. 再实例域（包括代码块。执行先后按照代码顺序。）
3. 构造器

上面`Person`类初始化的顺序是：
1. 静态域`MAX_AGE`默认值为`0`。
2. 静态块将`MAX_AGE`赋值为`120`。
3. 实例域`age`默认值为`0`。
4. 区块代码将`age`赋值为`10`。
5. 构造器再为`age`域赋值。

### 继承的基本概念
子类会同时继承基类的“状态”的“行为”。而且还可以有自己额外的状态和行为。

记住，**状态，也就是域，是保留双份的**。当子类定义一个和父类同名的域，就会同时有两个同名域，分别为`super.fieldname`和`this.fieldname`。基类构造器调用`fieldname`实际调用的是`super.fieldname`，子类构造器调用`fieldname`，实际调用的会是`this.fieldname`。

但 **行为，也就是方法，是覆盖基类行为的**。除非显示调用`super.methodName()`，否则无论子类还是基类调用`methodName()`都是调用`this.methodName()`。

总的来说，可以这样描述子类 **它同时拥有基类的状态，和它自己的状态。但它的行为却是覆盖父类行为的**。看下面这个代码示例，`Employee`类继承了`Person`类。所以拥有了`Person`类所有的域和方法。基类`Person`的`sayHello()`方法被覆盖。但对域来说就不同了，就算再加一个新的`Employee#age`域，基类`Person#age`域也不会被覆盖。

```java
class Employee extends Person {
    /**
     * Employee继承Person。所以它有Person所有的状态和行为。
     */

    /**
    static final int MAX_AGE;
    static {
        MAX_AGE = 120;
    }
    int age;
    {
        age = 10;
    }
    public Person(int arg) {
        if (arg < 0) { arg = 0; }
        if (arg > MAX_AGE) { arg = MAX_AGE; }
        age = arg;
    }
    public void sayHello() {
        System.out.println("Hello, I am " + age + " years old!");
    }
    */

   /**
    * Employee还可以有自己额外的状态和行为。
    *     1. 状态不覆盖基类状态
    *     2. 行为覆盖基类行为
    * 初始化过程稍微复杂一些：
    *     1. 先基类静态域
    *     2. 再子类静态域
    *     3. 再基类实例域
    *     4. 在子类实例域
    */
    static int MIN_SALARY = 1000;
    int salary;

    public Employee(int age, int salary) {
        super(age);
        if (salary < MIN_SALARY) {
            salary = MIN_SALARY;
        }
        this.salary = salary;
    }
    @Override
    public void sayHello() { // 行为覆盖基类行为。除非显式调用super.sayHello()
        System.out.println("Hello, I am " + age + " years old, and I have a job!");
    }
    public void work() {
        System.out.println("Work for " + salary + " salary!");
    }
}
```

### 子类的初始化顺序
继承以后的初始化过程稍微复杂一些：
1. 先基类静态域
2. 再子类静态域
3. 再基类实例域
4. 在子类实例域

看下面这个详细的例子，
```java
/**
 *    控制台打印
 */
class Log{
    public static String baseFieldInit(){System.out.println("Base Normal Field");return "";}

    public static String baseStaticFieldInit(){System.out.println("Base Static Field");return "";}

    public static String fieldInit(){System.out.println("Normal Field");return "";}

    public static String staticFieldInit(){System.out.println("Static Field");return "";}
}
/**
 *	基类
 */
class Base {
    /*1*/ static {System.out.println("Base Static Block 1");}

    /*1*/ private static String staticValue=Log.baseStaticFieldInit();

    /*1*/ static {System.out.println("Base Static Block 2");}

    /*3*/ {System.out.println("Base Normal Block 1");}

    /*3*/ private String value=Log.baseFieldInit();

    /*3*/ {System.out.println("Base Normal Block 2");}

    /*4*/ Base(){System.out.println("Base Constructor");}
}
/**
 *	派生类
 */
public class Derived extends Base{

    /*2*/ static {System.out.println("Static Block 1");}

    /*2*/ private static String staticValue=Log.staticFieldInit();

    /*2*/ static {System.out.println("Static Block 2");}

    /*5*/ {System.out.println("Normal Block 1");}

    /*5*/ private String value=Log.fieldInit();

    /*5*/ {System.out.println("Normal Block 2");}

    /*6*/ Derived(){System.out.println("Derived Constructor");}



    /**
     *  MAIN 主线程
     */
    public static void main(String[] args){
        Derived d=new Derived();
    }
}
```

以下为实验结果，
```bash
Base Static Block 1
Base Static Field
Base Static Block 2
Static Block 1
Static Field
Static Block 2
Base Normal Block 1
Base Normal Field
Base Normal Block 2
Base Constructor
Normal Block 1
Normal Field
Normal Block 2
Derived Constructor
```

### JVM加载类的过程三部走
一个类第一次被用到的时候，才被动态加载到JVM。一个类完整的过程分为如下三步：

1. 加载`.class`文件：先找并加载`.class`文件里以字节码形式存在的Class类的对象。(类的元信息)
链接：为变量分配内存空间。
2. 预处理
    1. 准备：在方法区把类的静态变量的初始值设成零值（static final可以在这个时候赋值）。给类或接口，字段，类方法，接口方法四种元数据分配内存（也是在方法区的常量池）。具体方法是分配一个没有实际内容的符号引用（Symbolic References）。
    2. 解析：然后对类或接口，字段，类方法，接口方法四种符号开始解析，添加引用（也都是在方法区的常量池里）。到这一步都完全没有对象这回事。
3. 初始化：这一步才真正开始赋值。
    1. ()方法。静态语句块static{}和成员变量的”默认”赋值是一起执行的。具体谁先谁后按照在文件中出现的先后顺序。
    2. ()方法（类构造器）。最后调用类的构造器来构造对象实例。

### 复合优于继承
选择复合还是继承的原则如下：
> **通常情况都应该使用复合，而不是继承。**

#### 继承破坏基类封装
首先 **为了允许继承，基类不得不放宽它的可访问性和可变性策略**。原先可以是`private`的成员现在至少得是`protected`，原先可以是`final`的成员，现在也不行了。这些 **成员都被迫成为了API的一部分，进入了客户端程序员的代码**。不但必须承诺永远支持，而且以后很难改。这也影响到了对象的不可变性。

第二，一个类只有它的公有API是它对用户承诺的一部分。其他的实现细节都有可能随着版本的变化而变化。基类向子类公开了它的一切。**如果子类的行为依赖于基类的某些实现细节，而不是API，一旦基类的实现细节随版本而变化，子类的行为就要受到影响**。书里举了一个很好的`HashSet`的例子。`HashSet`的`addAll()`方法的实现依赖于`add()`方法。对参数`Collection`的每一个元素都执行`add()`方法。
```java
public boolean addAll(Collection<? extends E> c) {
    boolean result = false;
    for (E ele : c) {
        if (add(ele)) {
            result = true;
        }
    }
    return result;
}
```
在不知道在这个细节的情况下，如果想在子类中为插入的元素总数计数，就会在新的`add()`和`addAll()`方法中都添加技术操作。这样`addAll()`方法会导致双倍计数。因为它自己统计了一次，调用`add()`方法又统计了一次。所以对一个设计来被继承的类，**让用户清楚地知道方法的实现细节很重要**。但这又违背了只对API负责的原则。
```java
@Override
public boolean add(E e) {
    count++;
    return super.add(e);
}
@Override
public boolean addAll(Collection<? extends E> c) {
    count += c.size();
    return super.addAll(c);
}
```
如果我知道了这个细节，我就只会在`add()`方法中计数。但这样做是报了一颗定时炸弹。因为哪天`HashSet`的`addAll()`的实现细节变了，不调用`add()`方法了，我的系统就不能正常工作了。因次，**继承一个类的时候不要让代码依赖于不被承诺的细节**。
```java
@Override
public boolean add(E e) {
    count++;
    return super.add(e);
}
```

#### 继承会有很多不必要的麻烦
复杂的继承结构会让很多本来很简单的工作增加很多难度。

##### 会打破equals()方法的对称性和传递性
前几章提到，如果`Point`类有一个子类`ColorPoint`，多了一个`color`域。如果想让`ColorPoint`跨类实现和`Point`类的`equals()`比较，**对称性** 和 **传递性** 就不能实现。有类似问题的还有`compareTo()`和`compare()`方法。

##### 子类不能重复实现Comparable接口
一般的类都只需要和自己比较，所以实现Comparable接口的时候，`class A implements Comparable<A>`，当`A`类的子类`B`类再想实现`Comparable<B>`接口的时候，不能重复实现了，因为擦除以后，他们都是`Comparable`接口。

##### 基类必须为子类clone()方法保障构造器调用链
前几章提过实现`clone()`方法最好是直接调用`Object#clone()`方法。为了让子类能调用到`Object`类的`clone()`方法，它所有的基类都必须递归调用`super.clone()`。因为这些麻烦的问题，`clone()`方法甚至不建议被使用。

##### 实现Serializable接口必须公开readResolve()和writeReplace()方法
一个基类实现`Serializable`接口，就必须使`readResolve()`和`writeReplace()`方法至少是`protected`，而不能是`private`。这些实现细节被迫成了API的一部分。

### 复合不但不暴露原始类实现细节，还能隐藏部分API
复合的典型用法是：**包装类** （**Wrapper Class**）。通过 **转发方法** **（forwarding method）**，提供原始类的服务。
```java
class A {
    public void a() {}
    public void b() {}
    public void c() {}
}
class B {
    private A item ;
    public void a() { item.a(); }
    public void b() { item.b(); }
    public void c() { item.c(); }
}
```

这样做的好处是：
`B`类面向本来就是`A`类的API。是`A`类承诺的服务。不管`A`类内部实现怎么变化，API是始终被支持的。而且`B`类可以只暴露`A`类的部分服务。如果不想提供`b()`方法和`c()`方法，就可以不提供。

包装类一个很小的局限性是不适合使用在 **回调框架** 中。因为`A`类中的方法被外部包装类`B`调用，向调用者`C`类提供了`A`类的引用，等待`C`类回调。实际`C`等待的可能是`B`类的引用。但显然`A`类的视角并不知道`B`类的存在。

### 什么时候可以用继承
**子类和基类严格符合 `is-a` 关系**。

### 怎么设计用来被继承的类
#### 构造器不要调用会被覆盖的方法
当子类的某方法覆盖了基类方法，方法中访问了子类的某个域。当子类的构造器向上调用基类的构造器调用这个被覆盖的方法的时候，子类的域实际还没有被初始化。看下面这个例子，`B`类继承了`A`类。`A`类的构造器调用了`printS()`方法，来打印`s`域。`B`类继承`A`类的时候，它覆盖了`printS()`方法。现在的`printS()`方法将打印`B`类的`s`域，而不是`A`类的。当`B`类的构造器调用`A`的构造器转而调用`printS()`的时候，显然`B`类的`s`域还没有初始化。

```java
class A {
    String s = "Hello Ronald";
    public A() { printS(); }
    public void printS() { System.out.println(s); }
}
class B extends A {
    String s = "Hello Shen";
    public B() { printS(); }
    @Override
    public void printS() { System.out.println(s); }
}
```
输出结果是：
```bash
null
Hello Shen
```
#### 可以提供一些“钩子方法”
**“钩子方法”** 是指一些不包含在公有API中的辅助方法。如果子类酌情覆盖这些方法，可以有助于提升运行的效率。这些方法的访问级别可以是`protected`的。当客户端程序员的子类完成后，只要限制子类不被继承，这些方法就不会继续成为客户端程序API的一部分。但这些钩子方法实际是库API的一部分，客户端代码对它产生了依赖，所以它必须永远被支持。

#### 谨慎实现Cloneable接口和Serializable接口
这样会让继承这个类的程序员需要负担一些棘手的问题。所以原则是，要么不实现这两个类，要么就在真的处理好这两个接口，不要把负担转嫁给客户端程序员。

#### 写好文档。不但要写清楚API。还要写清楚内部实现细节，特别是依赖关系。
避免客户端程序员破坏封装最有效的办法就是告诉他们你的类做了什么，以及怎么做的。从Java的官方API中，经常会有各式各样的“不通用”的约定。必须清楚地说明这些情况。

#### 亲自编写子类来测试。
