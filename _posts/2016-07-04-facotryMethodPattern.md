---
layout: post
title: "Why we need Factory Pattern"
date: 2016-07-04 18:24:32
author: "Wei SHEN"
categories: ["java","design pattern"]
tags: ["interface","factory pattern"]
description: >
---

在学到工厂方法模式的时候，不是很明白明明每个类都有自己的构造函数，为什么还要特意再写一个工厂类来负责实例化呢？在网上找答案的时候，就看到了知乎上的这个问题。仔细读了Design Pattern书，静下来思考以后，有点小小的顿悟。所以也写下来和大家讨论，分享。 也方便自己以后查阅。

原来的问题是这样：
> JAVA工厂方法的疑问（为何要创建工厂类或者工厂接口）？Thinking in java书中举了如下这个例子。中间省略了些代码，分别是2个接口的实现，我的疑问是，如果把下面静态方法的参数换成Service。方法体改成service.method1();service.method2();不是代码更简单也能达到效果吗，为何一定要创建新的厂类或者工厂接口,以此来返回Service的新的对象？
```java
interface Service {
    void method1();
    void method2();
}
interface ServiceFactory {
    Service getService();
}

....

public static void serviceConsumer(ServiceFactory fact) {
    Service s = fact.getService();
    s.method1();
    s.method2();
}
```


作者：胖胖
链接：[https://www.zhihu.com/question/30351872/answer/109408868](https://www.zhihu.com/question/30351872/answer/109408868)
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

这是个好问题，也曾经困扰过我。

确实，省掉工厂方法也可以实现多态。比如像下面这样，直接调用Service实例作为参数：向上转型成Service接口，去耦合。
```java
interface Service {
    void method1();
    void method2();
}
public static void serviceConsumer(Service s) {
    s.method1();
    s.method2();
}
```

但！**工厂方法模式真正的精髓并不在于多态去耦合，而是关键在于“工厂模式”！** 在Big4的Design Pattern书里，工厂方法模式是和“虚拟工厂”模式，“生成器”模式，以及“单例器”模式，放在一起来讲的，同属于“创建型模式”。

创建型模式最大的一个共同特征就是，把类型的“定义过程”和“实例化过程”分离开。也就是在类自身构造器之外，附加一个经常被误认为没什么卵用的“工厂类”，比如下面的ServiceFactory。
```java
class ServiceA {
    void method1(){};
    void method2(){};
    //构造器
    ServiceA(){};
}
class ServiceFactory {
    Service getService(){}
}
```

但是实际上，**实际工作中工厂类作用巨大！**

我们经常会说，当一个类有很多不同的变种，就需要工厂类来隔离实例化过程。比如下面代码，
```java
class ServiceFacotry {
    Service getService(){
        if (a) {
            ServiceA sA = new ServiceA();
        } else (b) {
            ServiceB sB = new ServiceB();
        }  else (c) {
            ServiceC sC = new ServiceC();
        } ...
          ...
    }
}
```

这确实有用，但实际工作中，也并不是我们使用工厂类最主要的原因。真正主要的动机，基于下面这个事实：相比于继承，我们更优先使用组合。
> **"Creational patterns become important as systems evolve to depend more on object composition than class inheritance.（《Design Pattern》P-81）"**

题主既然读《Think in Java》，肯定很熟悉作者反复提到的“组合优先于继承”的设计风格。对于组合的好处，可以参考“状态模式(State Pattern)”。利用多态，状态模式可以在runtime改变内部组件的类型，从而完全改变类的行为，因此比继承更灵活。

之前的例子中并没有说Service类型具体的结构，看不出设置工厂类有什么好处。但如果这种Service是由很多组件构成的呢？比如说把Service换成是一个迷宫就出事了。假设一个迷宫是由三种不同的小单元组成的，分别是房间，墙和门。
```java
class Maze {
    //成员字段
    Room r1;
    Room r2;
    ... ...
    Wall w1;
    Wall w2;
    ... ...
    Door d1;
    Door d2;
    ... ...

    //构造器
    Maze(){
        //按顺序摆放成员字段组件
        ... ...
    }
}
```

要生成一个迷宫，可能需要由成百上千个门，墙，房组件构成，因此构造器就会很庞大。更要命的是，迷宫有无数种，如果我们给每个迷宫都创建一个由上百个组件构成的实体类，然后再给出具体构造流程，那就累死了。

这种情况下，合理的办法是写一个随机迷宫生成器，能根据具体要求不同生成无数种千奇百怪的迷宫，而不是写个死板的迷宫放在那里。这就是创建型模式的意义所在。我们可以看到无论是后面的“虚拟工厂”模式，还是“生成器”模式，当然也包括我们的工厂方法模式，都是在具体生成的策略上做文章。但一个大前提不变，就是具体产出的产品“Product”都是由很多小的组件组合而成，而且组装的时候灵活度都非常高，甚至是runtime用户定制的，或者根本就是随机的。这就导致组合出来的产品Product单独作为一个类存在的意义很小，反而是构造器的作用被放大。直接导致索性把构造过程独立出来成为一个方法，把这个方法用到的参数作为成员字段一起封装起来，再来个构造器只负责初始化这些参数，这不就又是一个新的类了吗？这种类就叫做“工厂类”。
```java
class MazeFactory {

    //巨大的迷宫生成算法
    Maze mazeGenerator(int roomNum, int wallNum, int doorNum){
        ... ...
    }

    //构造器：初始化生成迷宫的参数
    MazeFactory(){
        roomNum=100；
        wallNum=1000；
        doorNum=200;
    }

    //字段：生成迷宫的参数
    int roomNum;
    int wallNum;
    int doorNum;
}
```

至于原来的那个迷宫类，本身的构造器就不承担任何功能了，也就是被阉割了。迷宫类的存在，也仅仅是在于在生成之后，怎么来玩了。
```java
class Maze {

    void play(){
        ... ...
    }

    //构造器被阉割，只有白板数据结构
    Maze(int roomNum, int wallNum, int doorNum){
        Room[] roomSet=new Room[roomNum];
        Wall[] wallSet=new Wall[wallNum];
        Door[] doorSet=new Door[doorSet];
    }

    //迷宫由room, wall, door组成
    Room[] roomSet;
    Wall[] wallSet;
    Door[] doorSet;
}
```

所以再回到工厂方法，书里说的所有的东西，都是基于这个前提，也就是我们说好了啊，迷宫这东西的类文件里是没有具体构造方法的，都是要用工厂类MazeFactory来生成的。至于后来，我们加入了方迷宫，和方迷宫生成器。又有了圆迷宫和圆迷宫生成器。有了一堆生成器复杂了之后，又想到用多态来解耦，这都是后话，是在确定了使用工厂类的前提下，利用多态解耦的优化方案。所以才有了最初的这段代码：
```java
//两个接口
interface Maze {
    void play();
}
interface MazeFactory {
    Maze mazeGenerator();
}

//各自继承类
CircularMaze implements Maze {
    ... ...
}
SquareMaze implements Maze {
    ... ...
}

CircularMazeFactory implements MazeFactory {
    ... ...
}
SquareMazeFactory implements MazeFactory {
    ... ...
}

//多态，面向接口
public static void mazeGame(MazeFactory fact) {
    Maze z = fact.mazeGenerator();
    z.play();
}
```

所以，用大白话来说的话，**工厂方法就是“工厂”模式的多态解耦加强版**。
