---
layout: post
title: "[Effective Java] Note: - Chapter-2-1: Create Instances with Factory Method or Builder Pattern"
date: 2017-02-03
author: "Wei SHEN"
categories: ["java","effective java","design pattern"]
tags: ["code style","factory pattern","builder pattern"]
description: >
  静态工厂方法可以为生产实例提供可观的灵活性，也可以进行实例控制。而且静态工厂方法还有利于简化公开接口，隐藏额外子类的好处。如果类的构造器或者静态工厂中具有多个参数，推荐使用Builder模式。它和静态工厂方法一样，也能为实例创建提供了很大的灵活性，以及保障了代码的可读性。而且它还比JavaBeans模式更安全。
---

### 静态工厂方法
某个类的静态方法负责返回自身类型的实例，就是一个典型的静态工厂方法。书上举了一个Boolean的例子，`valueOf()`方法负责将`boolean`基本型转型成`Boolean`包装类。这个方法的实际工作就是分配`Boolean`型的实例。
```java
public static Boolean valueOf(boolean b) {
    return b? Boolean.TRUE : Boolean.FALSE;
}
```
仔细想想`Collection`中返回迭代器的`iterator()`和`Map`中返回`Map.Entry`的`entrySet()`方法都是静态工厂方法的例子。

静态工厂方法的本质就是，由静态方法负责实例的生产。这不是多此一举，而是 **提供了相当的灵活性**。

#### 首先，静态工厂方法有名称
构造器不能选择自己的名称。但静态工厂方法可以。比如一个`Number`类，构造器只能叫`Number`。但静态工厂方法可以叫:`Odd()`,`Even()`,`Prime()`,`Fibonacci()`等等等等。甚至还省去了派生子类的麻烦。

```java
public class Number {
    private Number(){ // 构造器
        // code
    }
    public static Number odd() { // 生产奇数
        // code
    }
    public static Number even() { // 生产偶数
        // code
    }
    public static Number prime() { // 生产素数
        // code
    }
    public static Number fibonacci() { // 甚至是斐波那契数列
        // code
    }
}
```

#### 第二，静态工厂方法可以返回子类型的实例
虽然方法定义返回的是目标类的实例，但如果返回的是目标类的子类型，编译器也是接受的。这又提供了和可观的灵活性。

下面的例子，除了返回子类的灵活性，更重要的是，子类被完全向用户隐藏了。用户只能通过静态工厂方法来使用三个子类。

```java
public class Shape {
    private class Circle extends Shape {}
    private class Triangle extends Shape {}
    private class Hexagon extends Shape {}
    public static Shape circle() {
        return new Circle();
    }
    public static Shape triangle() {
        return new Triangle();
    }
    public static Shape hexagon() {
        return new Hexagon();
    }
}
```

#### 第三，静态工厂不必每次都真的创建一个新实例
完全可以是 **返回之前预存好的有限实例**。这也被称为实例受控技术（instance-control）。如果创建某个实例的开销非常大，或者系统希望只存在数量有限个目标类实例的情况下，静态工厂方法就能派上用场。

注意，如果需要控制实例数量的话，可以将构造器设为私有，仅公开静态工厂方法。

```java
public class Number {
    private static final Random R = new Random();
    private static final Number[] FIBO; // 仅有的几个Number实例
    static {
        Number a = new Number(1);
        Number b = new Number(2);
        Number c = new Number(3);
        Number d = new Number(5);
        Number e = new Number(8);
        Number f = new Number(13);
        Number g = new Number(21);
        FIBO = new Number[] {a,b,c,d,e,f,g};
    }
        private int num;
    private Number(int i) {
        num = i;
    }
    public String toString() {
        return String.valueOf(num);
    }
    public static Number fibonacci() { // 静态工厂方法返回缓存的fibonacci数列值
        return FIBO[R.nextInt(FIBO.length)];
    }
}
```

#### 第四，创建泛型实例更简洁
因为系统提供了泛型类型参数推断。比如下面这个静态工厂方法返回`Map<K,V>`。
```java
public static <K,V> HashMap<K,V> newInstance() {
    return new HashMap<K,V>();
}
```
调用的时候只需要写：
```java
Map<String,List<String>> m = HashMap.newInstance();
```

#### 抽象工厂方法的缺点
1. 如果构造器被私有化，就不能派生子类。
2. 方法太多，用户不好查阅。静态方法没有被特殊标出。

#### 引申一下
##### 基于接口的框架（interface-based framework）
基于接口的框架很帅，很适合向用户提供API：
1. 只向用户提供一组很简洁的接口`Type`。
2. 一组静态工厂方法以这组接口为返回类型。
3. 但静态工厂方法返回的实际类型不向用户公开。
4. 这组静态工厂方法被放置在一个叫`Types`的不可实例化类。

比如`java.util`包里，针对一组公共接口`Collection`，便利方法类`Collections`通过静态工厂方法，向用户提供了几个具有特殊功能的容器，比如`UnmodifiableCollection`和`SynchronizedCollection`。
```bash
static <T> Collection<T>	unmodifiableCollection(Collection<? extends T> c)
static <T> Collection<T>	synchronizedCollection(Collection<T> c)
```
但用户却接触不到上述两个类。因为他们仅仅作为`Collections`类的内部类存在，并没有向用户公开。获得他们的唯一途径就是上述`Collections`类中的这两个静态工厂方法。而且，还是作为统一的`Collection`接口实例返回的。

`Java Collections Framework`提供了32个这样的便利实现，但却没有增加`Collection`公共接口的复杂度。

##### 服务提供者框架（Service Provider Interface）
P6-7，请查阅书籍。

### 增强实例构造过程的可读性
当一个类有很多个域，实例的构造器会有多个参数，而且构造器的数量也会大量增加。这时候有些常用手段，可以增强构造过程的清晰度。其中 **Builder模式是最好的**。

#### 重叠构造器（Telescoping Constructor）：还不够简便
这样的重叠构造器是一个比较朴素的解决办法，**参数较少的构造器可以利用某些参数较为完整的构造器，配上某些字段的默认值**。可以让我们少打一点字。但重叠构造器的缺点是还不够简便。参数很多的时候，光要理解很多个版本的构造器就很累，而且还是会有一长串的参数。

```java
package com.ciaoshen.effectivejava.chapter2;

public class TelescopingConstructor {
    private static class Dog {
        // Dog有不少域
        private final String name;
        private final String category;
        private final int color;
        private final int weight;
        private final int age;
        // 重叠式构造器： 参数少的构造器给出了部分域的默认值
        public Dog() {
            this("Wong");
        }
        public Dog(String name) {
            this(name, "normal");
        }
        public Dog(String name, String category) {
            this(name, category, 0, 0, 0);
        }
        public Dog(int color, int weight, int age) {
            this("Wong", "normal", color, weight, age);
        }
        public Dog(String name, String category, int color, int weight, int age) {
            this.name = name;
            this.category = category;
            this.color = color;
            this.weight = weight;
            this.age = age;
        }
    }
}
```

#### JavaBeans模式：可读性好，但不安全
JavaBeans模式也可以简化构造复杂度。**它给部分或者全部域都配备了专门用来赋值的访问方法**。先调用无参数构造器，构造一个由默认值构成的对象，然后用域访问方法为每个域赋值。

JavaBeans模式的两大优点是：
1. 构建实例的 **灵活性**。给Builder设置参数，可以构建各式各样的实例。
2. 客户端构建实例代码的 **可读性**。把实例构造过程一步步分开，更直观易懂。

下面的代码演示了JavaBeans模式是怎么工作的，
```java
public class JavaBeans {
    private static class Dog {
        // 有很多域
        private String name = "Wong";
        private String category = "Normal";
        private int color = 0;
        private int weight = 0;
        private int age = 0;
        // 域的访问方法
        public void setName(String name) { this.name = name; }
        public void setCategory(String category) { this.category = category; }
        public void setColor(int color) { this.color = color; }
        public void setWeight(int weight) { this.weight = weight; }
        public void setAge(int age) { this.age = age; }
    }
}
```
下面是客户端的对象初始化代码，一步步构建对象，代码好写而且好理解。
```java
    public static void main(String[] args) {
        Dog myPuppy = new Dog();
        myPuppy.setName("Piu Piu");
        myPuppy.setCategory("Hot Dog");
        myPuppy.setColor(5);
        myPuppy.setWeight(100);
        myPuppy.setAge(10);
        System.out.println(myPuppy);
    }
```
但JavaBeans的缺点同样明显，
1. **首先，它增加了产生不完整对象的可能**。构造过程被分到了几个不同的调用中，变相拉长了构造过程的生命周期，增加程序员管理的难度。
2. **它牺牲了对象的不可变性**。域无法用final修饰，而且必须向用户暴露用于修改域的访问方法。安全性打了折扣。


#### Builder模式：Builder就像一个雕版，可以用来批量印刷实例
**JavaBeans是用setter访问方法直接构造对象。而Builder类是目标类的一个模型，构造过程先在模型上逐步实行，等完成以后，复制模型就可以生产出目标类的对象实例。**

所以Builder模式保留了JavaBeans模式可读性的优点，可以慢慢雕琢每个实例，但雕琢的过程又不在目标类上完成，雕琢的是Builder模型，最后再复制模型去生产目标类实例，所以目标类实例可以是一次成型，既缩短了最后目标类构造过程的生命周期，而且对象也可以是不可变的。

另一个巧妙的地方时，Builder的setter方法返回Builder本身，以便把调用连接起来。下面的代码是一个简单示例，`Dog`类有5个域，静态内部类DogBuilder模拟了这5个域，并给每个域配备了专属的setter方法。`Dog`类构造器必须接收一个Builder类为参数。为了构造`Dog`类，我们必须得先构造一个`DogBuilder`类。`DogBuilder`构造好之后，最后的`build()`方法调用了`Dog`类的构造方法，返回一个`Dog`实例。

```java
public class Builder {
    private static class Dog {
        // 不可变的域
        private final String name;
        private final String category;
        private final int color;
        private final int weight;
        private final int age;
        // Dog的构造器面向的是静态内部类DogBuilder，实际上就是复制Builder构造好的信息
        public Dog(DogBuilder builder) {
            name = builder.name;
            category = builder.category;
            color = builder.color;
            weight = builder.weight;
            age = builder.age;
        }
        public String toString() {
            return "Dog: " + name + "\n"
                + "\t >>> Category: " + category + "\n"
                + "\t >>> Color: " + color + "\n"
                + "\t >>> Weight: " + weight + "\n"
                + "\t >>> Age: " + age + "\n";
        }
        private static class DogBuilder {
            // Builder中模拟Dog类的域
            private String name = "Wong";
            private String category = "Normal";
            private int color = 0;
            private int weight = 0;
            private int age = 0;
            // 每个域都有一个专属的setter方法
            public DogBuilder setName(String name) { this.name = name; return this; }
            public DogBuilder setCategory(String category) { this.category = category; return this; }
            public DogBuilder setColor(int color) { this.color = color; return this; }
            public DogBuilder setWeight(int weight) { this.weight = weight; return this; }
            public DogBuilder setAge(int age) { this.age = age; return this; }
            // build()方法调用Dog的构造器复制对象
            public Dog build() {
                return new Dog(this);
            }
        }
    }
}
```
对setter方法的调用形成一个链条。这模拟了具名的可选参数。
```java
    public static void main(String[] args) {
        Dog myPuppy = new Dog.DogBuilder().setName("Piu Piu").setCategory("Hot Dog").setColor(5).setWeight(100).setAge(10).build();
        System.out.println(myPuppy);
    }
```

##### Builder模式的优点总结
1. 构造代码直观易读，是它和JavaBeans共同的优点。
2. Builder提供了最大的灵活性。明显比一个构造器，或者一个静态方法能做的更多。有时甚至省去了派生子类的麻烦。

##### Builder模式的缺点: 开销稍大一些
Builder模式既易读，又安全。缺点就是 **开销稍微大一些**。因为在构建目标类实例之前，先得构造一个Builder实例。大部分情况下这无关痛痒，除了某些特别注重性能的情况。

#### “Builder模式”和“虚拟工厂”模式结合：提供足够的灵活性，以及统一的接口
Builder提供了足够的差异性灵活性，以及实例控制，如果再利用虚拟工厂可以向用户提供一个统一的接口，就更完美。

下面这个代码正好演示了Builder提供的这种创建实例时的灵活性，甚至比直接派生子类还有效。以及向用户提供一个虚拟工厂接口以后，让客户端代码得到的真正的简便。仔细阅读代码里的注释。

```java
/**
 * Effective Java Chapter 2
 * Builder的优势不仅仅在于让实例创建过程变得简洁明了。
 * 更重要的精髓在于：与其创建很多子类，很多构造器，不如利用Builder模式来生产差异化实例。
 * Builder模式拥有用静态方法控制实例生产的大部分优点，因为它本身就是静态方法的升级版。
 * 使用Builder类生产实例的类不应该面向Builder实现的虚拟工厂接口，比如Builder<T>，因为这是暴露给用户的。应该直接面对Builder类，或者另给一个接口。
 *
 */
package com.ciaoshen.effectivejava.chapter2;
import java.util.*;

/**
 * Builder模式 和 虚拟工厂模式 的结合使用
 */
public class AbstractFactory {

    /**
     * 虚拟工厂的泛型接口
     * 用户面对的就是这个接口
     */
    public static interface Builder<T> {
        public T build();
        public T random();
    }

    /**
     * Dog类有好几个域，利用Builder模式来生产实例。
     * 注意：这里没有定义很多狗类的子类，
     * 而是同时定义好几个差异化的Builder，狗类实例的差异化由Builder提供，而不用派生很多子类。体现出Builder很好的灵活性。
     * 注意：由Builder生产出来的实例可以都是“不可变的”。这也是Builder的主要优势之一，安全性。
     */
    public static class Dog implements Animal {
        private final DogType category;
        private final String name;
        private final int color;
        private final int weight;
        private final int age;
        // 注意：Dog的构造器看到的是DogBuilder，而不是Builder<Dog>这个虚拟工厂接口，它是专门暴露给外部用户的。
        private Dog(DogBuilder builder) {
            category = builder.category;
            name = builder.name;
            color = builder.color;
            weight = builder.weight;
            age = builder.age;
        }
        public String toString() {
            return "Dog: " + name + "\n"
                + "\t >>> Category: " + category + "\n"
                + "\t >>> Color: " + color + "\n"
                + "\t >>> Weight: " + weight + "\n"
                + "\t >>> Age: " + age + "\n";
        }
        public static enum DogType { // 仅仅用来提供狗品种的具名常量
            DEFAULT_TYPE, GOLDEN_RETRIEVER, BULLDOG, CHIWUAWUA; // 枚举型保留一个缺省值是一个良好实践
            private static DogType[] VALUES = values();
            private static Random R = new Random();
            public static DogType random() {
                return VALUES[R.nextInt(VALUES.length-1)+1]; // to avoid first DEFAULT_TYPE
            }
        }
        /**
         * DogBuilder是Dog类的静态内部类。和Dog类共享环境。所以它不需要设置getter域访问方法。
         * 如果DogBuilder在Dog类的外部，为了保护域的私有性就应该提供getter域访问方法。也可以再定义一个专门暴露给Dog类的接口。
         * 写DogBuilder类的时候，不需要太多考虑虚拟工厂接口Builder<Dog>。只是在定义的时候声明一下，然后实现build()方法。
         */
        public static class DogBuilder implements Builder<Dog> { // 在这里规定了DogBuilder实现了Builder<Dog>的虚拟工厂接口
            private static final String[] NAME_UNIT = new String[] {"Piu","Miu","Ka","Ji","Ko","Wa"};
            private static final int COLOR_RANGE = 10;
            private static final int WEIGHT_RANGE = 100;
            private static final int AGE_RANGE = 20;

            protected DogType category = DogType.DEFAULT_TYPE;
            protected String name = "Wong";
            protected int color = 0;
            protected int weight = 0;
            protected int age = 0;
            // 这里DogBuilder是功能完整的Builder，可以设置全部5个域。
            public DogBuilder setCategory(DogType category) { this.category = category; return this; }
            public DogBuilder setName(String name) { this.name = name; return this; }
            public DogBuilder setColor(int color) { this.color = color; return this; }
            public DogBuilder setWeight(int weight) { this.weight = weight; return this; }
            public DogBuilder setAge(int age) { this.age = age; return this; }
            public Dog build() {
                return new Dog(this);
            }
            // random()方法为了简化测试时候生产随机实例。
            // 这更显示了Builder模式的的强大，可以满足各种实例的需求。
            public Dog random() {
                Random r = new Random();
                this.setName(NAME_UNIT[r.nextInt(NAME_UNIT.length)] + " " + NAME_UNIT[r.nextInt(NAME_UNIT.length)]);
                return this.setCategory(DogType.random()).setColor(r.nextInt(COLOR_RANGE)).setWeight(r.nextInt(WEIGHT_RANGE)).setAge(r.nextInt(AGE_RANGE)).build();
            }
            // 阉割版random()方法，唯独不设置category域。 专门给后面的特殊Builder继承。
            protected Dog randomNoCategory() { // for derived builder
                Random r = new Random();
                this.setName(NAME_UNIT[r.nextInt(NAME_UNIT.length)] + " " + NAME_UNIT[r.nextInt(NAME_UNIT.length)]);
                return this.setColor(r.nextInt(COLOR_RANGE)).setWeight(r.nextInt(WEIGHT_RANGE)).setAge(r.nextInt(AGE_RANGE)).build();
            }
        }
        /**
         *  黄金猎犬的专属Builder。默认品种为黄金猎犬。
         *  禁掉了setCategory()方法，因为品种已定。
         */
        public static class GoldenRetrieverBuilder extends DogBuilder {
            public GoldenRetrieverBuilder() {
                name = "Little Golden";
                category = DogType.GOLDEN_RETRIEVER;
            }
            @Override
            public DogBuilder setCategory(DogType category) {
                throw new UnsupportedOperationException("GoldenRetrieverBuilder can not use setCategory() method!");
            }
            @Override
            public Dog random() {
                return randomNoCategory();
            }
        }
        /**
         *  斗牛犬的专属Builder。默认品种为斗牛犬。
         *  禁掉了setCategory()方法，因为品种已定。
         */
        public static class BullDogBuilder extends DogBuilder {
            public BullDogBuilder() {
                name = "Little Bull";
                category = DogType.BULLDOG;
            }
            @Override
            public DogBuilder setCategory(DogType category) {
                throw new UnsupportedOperationException("BullDogBuilder can not use setCategory() method!");
            }
            @Override
            public Dog random() {
                return randomNoCategory();
            }
        }
        /**
         *  吉娃娃的专属Builder。默认品种为吉娃娃。
         *  禁掉了setCategory()方法，因为品种已定。
         */
        public static class ChiWuaWuaBuilder extends DogBuilder {
            public ChiWuaWuaBuilder() {
                name = "Little ChiChi";
                category = DogType.CHIWUAWUA;
            }
            @Override
            public DogBuilder setCategory(DogType category) {
                throw new UnsupportedOperationException("ChiWuaWuaBuilder can not use setCategory() method!");
            }
            @Override
            public Dog random() {
                return randomNoCategory();
            }
        }
    }

    /**
     * 面向虚拟工厂的客户端：Zoo类，newInstance()静态泛型方法，利用各种动物的Builder，创建很多动物实例放进列表。
     * 不管有多少种不同的Builder，用户代码newInstance()方法调用的参数，永远只有 ”Builder<? extends E> builder"，以及它的build()和random()方法。
     * 最后创建整个狗狗动物园的代码异常简洁：Zoo.newInstance(new Dog.DogBuilder(),10)
     * 这就是Builder模式和虚拟工厂的力量。
     */
    public static interface Animal {}
    public static class Zoo<T extends Animal> {
        private List<T> animals = new ArrayList<>();
        public String toString() {
            return animals.toString();
        }
        public static <E extends Animal> Zoo<E> newInstance(Builder<? extends E> builder, int size) {
            Zoo<E> zoo = new Zoo<>();
            for (int i = 0; i < size; i++) {
                zoo.animals.add(builder.random());
            }
            return zoo;
        }
    }
    public static void main(String[] args) {
        // test Builder#build()
        Dog myPuppy = new Dog.DogBuilder().setCategory(Dog.DogType.GOLDEN_RETRIEVER).setName("Piu Piu").setColor(5).setWeight(100).setAge(10).build();
        Dog gr = new Dog.GoldenRetrieverBuilder().setName("GR").setColor(5).setWeight(100).setAge(10).build();
        Dog bd = new Dog.BullDogBuilder().setName("BD").setColor(5).setWeight(100).setAge(10).build();
        Dog cww = new Dog.ChiWuaWuaBuilder().setName("CWW").setColor(5).setWeight(100).setAge(10).build();
        System.out.println(myPuppy);
        System.out.println(gr);
        System.out.println(bd);
        System.out.println(cww);
        // test Builder#random()
        System.out.println("-----------------------------------");
        Random r = new Random();
        System.out.println(new Dog.DogBuilder().random());
        System.out.println(new Dog.GoldenRetrieverBuilder().random());
        System.out.println(new Dog.BullDogBuilder().random());
        System.out.println(new Dog.ChiWuaWuaBuilder().random());
        // test Zoo & Abstract Factory interface Builder<T>
        System.out.println("-----------------------------------");
        System.out.println(Zoo.newInstance(new Dog.DogBuilder(),10)); // 最后创建整个狗狗动物园的代码异常简洁。
    }
}
```
