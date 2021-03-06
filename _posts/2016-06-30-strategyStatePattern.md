---
layout: post
title: "Difference between Strategy Pattern and State Pattern"
date: 2016-06-30 21:59:18
author: "Wei SHEN"
categories: ["java","design pattern"]
tags: ["interface","strategy pattern","state pattern"]
description: >
---

先上图：
![strategyPattern2](/images/tij4-9/strategyPattern2.png)
本质上讲，策略模式和状态模式做得是同一件事：去耦合。怎么去耦合？就是把干什么（语境类）和怎么干（策略接口）分开，互不依赖。打个比方，下面是我一天的行程：
```java
class 我{
    吃饭(){}
    逛街(){}
    啪啪啪(){}
    睡觉(){}
}
```

但问题来了，啪啪啪是个技术活，有著名的48式，今天到底要用哪一式呢？于是我的代码变成了这样：
```java
class 我{
    吃饭(){}
    逛街(){}
    啪啪啪1式(){}
    啪啪啪2式(){}
    啪啪啪3式(){}
    睡觉(){}
}
```

但代码如果是这样的，宝宝肯会被老板开掉的。但宝宝发现，啪啪啪的方式其实取决于我的妹子。对三种不同类型的妹子，我的表现是不同的。
```java
class 鬼妹 implements 妹子{
    爱爱(){print(”Come on!“);}
}

class 乖妹 implements 妹子{
    爱爱(){print("I love you!");}
}

class 萌妹 implements 妹子{
    爱爱(){print("Ya Mie Die!");}
}

interface 妹子 {
    爱爱();
}
```

而且我惊讶地发现，所有妹子都把啪啪啪叫做”爱爱“。所以只要我只要知道今天晚上是和哪个妹子在一起，根据多态性，我只要说同一句话：”妹子让我们爱爱吧“。接下来发生的事，我只要闭上眼睛享受就好了。
```java
class 我{
    吃饭();
    逛街();
    啪啪啪(妹子 今晚的妹子){
        今晚的妹子.爱爱();
    }
    睡觉();
}
```

于是，你懂的：
```java
main(){
    我 胖胖 = new 我();
    print("周一：");
    妹子 娘子 = new 乖妹();
    胖胖.啪啪啪(娘子);
    print("周二：");
    妹子 小三 = new 萌妹();
    胖胖.啪啪啪(小三);
}

//OutPut:
周一：I love you!
周二：Ya Mie Die!
```

以上的就叫：**策略模式**！

但后来我发现，其实我做的每一件事都取决我的妹子，我彻底震惊了！我的生活不能没有妹子！
```java
class 我{
    吃饭(妹子 今晚的妹子){
        今晚的妹子.饭饭();
    }
    逛街(妹子 今晚的妹子){
        今晚的妹子.逛逛();
    }
    啪啪啪(妹子 今晚的妹子){
        今晚的妹子.爱爱();
    }
    睡觉(妹子 今晚的妹子){
        今晚的妹子.呼呼();
    }
}
```

于是终于说出了那句话：嫁给我吧！于是，她彻底走进了我的生活！
```java
class 我{
    吃饭(){
        老婆.饭饭();
    }
    逛街(){
        老婆.逛逛();
    }
    啪啪啪(){
        老婆.爱爱();
    }
    睡觉(){
        老婆.呼呼();
    }

    //fields
    妹子 老婆 = new 乖妹子();
}
```

但你们是懂我的，我的小三怎么办呢？要是每天都能切换情人就最好了！
```java
class 我{

    切换情人(妹子 情人){
        老婆 = 情人;
    }   

    吃饭(){
        老婆.饭饭();
    }
    逛街(){
        老婆.逛逛();
    }
    啪啪啪(){
        老婆.爱爱();
    }
    睡觉(){
        老婆.呼呼();
    }

    //fields
    妹子 老婆 = new 乖妹子();
}
```

于是每一天都充满了新鲜感呢！
```java
main(){
    我 胖胖 = new 我();
    print("周一：");
    //老婆
    胖胖.啪啪啪();
    print("周二：");
    妹子 小三 = new 萌妹();
    胖胖.切换情人(小三);
    //小三
    胖胖.啪啪啪();
}

//OutPut:
周一：I love you!
周二：Ya Mie Die!
```

于是，我都爱上了这种叫”状态模式“的生活方式。

所以，”策略模式“就好比单身的时候，没有固定情人，每天用微信出去约泡。”状态模式“就是结婚以后，有了固定的情人，生活可以在老婆和小三之间游刃有余！

说到这里，大家一定都豁然开朗了吧！那么，祝大家生活幸福！
