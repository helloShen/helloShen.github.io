---
layout: post
title: "Concurrency - [Producer-Consumer] Pattern"
date: 2017-10-03 16:42:13
author: "Wei SHEN"
categories: ["java","design pattern"]
tags: ["concurrency"]
description: >
---

### 生产者-消费者模式Demo
直接看代码，
* 全局的竟态资源是`Resturant`类的`meal`域，代表餐馆后厨的桌子（缓冲区）。
* `Chef`类扮演 **生产者** 的角色， 负责做菜，摆在大桌子上。每个`Chef`对象拥有自己独立的线程。
* `Waiter`类扮演 **消费者** 的角色，负责从大桌子上拿菜给顾客上菜。每个`Waiter`对象也拥有自己独立的线程。

整个餐馆有多个厨师，和多个服务员同时工作。后厨桌子有个空间的上限，比如最多放10道菜。如果菜摆满了，厨师线程就阻塞，等桌子还有空位了再继续做菜。服务员也一样，当桌子空了之后，他们也会休息，等厨师做菜。

#### `Resturant.java`
```java
package com.ciaoshen.howtomcatworks.ex04.concurrency;
import java.util.*;
import java.util.concurrent.*;

class Resturant {
    private static volatile int count;
    private final int maxMeal;
    private volatile int meal;
    public Resturant(int max) { maxMeal = max; }

    public synchronized void offer() throws InterruptedException {
        while (meal == maxMeal) {
            wait();
        }
        meal++;
        notifyAll();
    }
    public synchronized void take() throws InterruptedException {
        while (meal == 0) {
            wait();
        }
        meal--;
        notifyAll();
    }
    public static void main(String[] args) {
        for (int j = 0; j < 100; j++) {
            Resturant resturant = new Resturant(10);
            ExecutorService exec = Executors.newCachedThreadPool();
            // 10个厨师，10个服务员. 各自拥有独立线程.
            for (int i = 0; i < 10; i++) {
                exec.submit(new Chef(resturant));
                exec.submit(new Waiter(resturant));
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                System.out.println("Resturant thread interrupted accidently!");
            }
            exec.shutdownNow();
        }
    }
}
```

#### `Chef.java`
```java
package com.ciaoshen.howtomcatworks.ex04.concurrency;
import java.util.*;
import java.util.concurrent.*;

class Chef implements Runnable {
    private Resturant resturant;
    public Chef(Resturant r) {
        synchronized(this) { resturant = r; }
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                resturant.offer();
            }
        } catch (InterruptedException ie) {
            System.out.println("Chef#" + Thread.currentThread().getId() + " interrupted during waiting!");
        }
        System.out.println("Chef#" + Thread.currentThread().getId() + " stopped correctly!");
    }
}
```

#### `Waiter.java`
```java
package com.ciaoshen.howtomcatworks.ex04.concurrency;
import java.util.*;
import java.util.concurrent.*;

class Waiter implements Runnable {
    private Resturant resturant;
    public Waiter(Resturant r) {
        synchronized(this) { resturant = r; }
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                resturant.take();
            }
        } catch (InterruptedException ie) {
            System.out.println("Waiter#" + Thread.currentThread().getId() + " interrupted during waiting!");
        }
        System.out.println("Waiter#" + Thread.currentThread().getId() + " stopped correctly!");
    }
}
```


### 首先，最重要的是找准“竟态资源”
首先要使用互斥锁的最重要的原因就是因为有多个线程同时读写某个 **竟态资源**。所以，所有操作竟态资源的代码构成的 **临界区** 都必须用互斥锁保护起来。进入临界区之前，必须先拿到关键对象的互斥锁，来确保同一时刻，最多只能有一个线程在读写竟态资源。

所以 **最最重要的就是找准“竟态资源”有哪些**。没有竟态资源那就没必要用互斥锁，有竟态资源就必须用互斥锁保护起来。而且判断哪里是临界区，也是根据哪些代码操作了竟态资源来判断。而且要会找竟态资源，不是所有变量都是静态资源。

《Java并发编程》开篇讲了，
> 一个无状态对象肯定是线程安全的。

下面这段代码`i`和`factors`都只是方法内部用到的 **局部变量**，生命周期只局限于方法栈的生命周期内，所以他们不构成对象的“状态”。对象的“状态”指的就是对象的 **成员字段**，伴随对象的整个生命周期而存在。尤其是一个对象的公有字段，任何其他类都可以访问，就暴露在一个非常危险的境地。
```java
public class StateLessFactorizer implements Servlet {
    public void service(ServletRequest req, ServletResponse resp) {
        BigInteger i = new BigInteger(extractFromRequest(req));
        BigInteger[] factors = factors(i);
        encodeIntoResponse(resp,factors);
    }
}
```

刚刚给出的Demo里唯一的竟态资源就是`Resturant`类的`private volatile int meal`字段。两段和`meal`字段有关的操作构成临界区`offer()`和`take()`函数，分别用`synchronized`互斥锁保护起来。`offer()`模拟厨师做菜并放上桌子的过程，让`meal`值自增`1`。`take()`表示服务员从桌子上拿菜给顾客上菜的过程，`meal`值减`1`。

### 第二，把临界过程封装在竟态资源所在的类
`wait()`，`notify()`以及`notifyAll()`比较特殊的是他们是基类`Object`的一部分，而不属于`Thread`的一部分。看上去怪，但其实这是合理的。因为 **这些方法操作的锁也是对象的一部分**。 对于互斥锁，一个恰当的描述是：“拿到这个对象的互斥锁”。所以，
> `wait()`函数必须在`synchronized`代码块中使用，它其实是说：让出当前对象上的互斥锁，然后阻塞当前线程，直到有`notify()`或`notifyAll()`函数来唤醒它。

所以 **把`offer()`和`take()`函数封装在竟态资源所在的`Resturant`类是合理的**。然后`Chef`类和`Waiter`类只要分别调用`offer()`和`take()`方法就行了。

### “生产者-消费者”模型是自旋锁（spin lock）的升级版
自旋锁（spin lock）是实现起来最简单有效的线程协作模型，用一句话概括就是，循环忙等，和操作竟态资源交替执行。

只有当`turn == 0`才执行的一半，
```c
while (TRUE) {
    while (turn != 0) { ; } // 循环忙等
    critical_rigion();
    turn = 1;    
    noncritical_rigion();
}
```
只有当`turn == 1`才执行的另一半，
```c
while (TRUE) {
    while (turn != 1) { ; } // 循环忙等
    critical_rigion();
    turn = 0;
    noncritical_rigion();
}
```

这个模型精彩的地方就在于，
> 它用一个有条件的循环忙等，避免了程序在错误的时候执行任务。

其他还有一些模型，比如 **Piterson锁**，也能完成安全的线程协作。但自旋锁是最简单的一个。然而自旋锁的缺点就是 **忙等待效率低**。 “生产者-消费者”模型就是自旋锁的升级版，利用`wait()`在自旋锁忙等的地方把互斥锁让出去，等条件满足了再用`notify()`或`notifyAll()`调度回来继续执行。

### `notifyAll()`优于`notify()`，并且用`while`轮询包裹`wait()`是良好实践
`notifyAll()`和`notify()`的区别是，`notifyAll()`会唤醒在这个对象的锁上等待的所有线程。而`notify()`只是由调度器选择在这个锁上等待的某一个线程唤醒。但具体唤醒的是哪一个线程，是程序员不可控的。

根据《Effective Java》第一版第50条，对于使用`wait()`和`notify()`，`notifyAll()`的一个良好实践是：
> 总是在一个`while`轮询中使用`wait()`。并且在一般情况下`notifyAll()`的优先级高于`notify()`。

`notifyAll()`优于`notify()`是因为：
> `notifyAll()`可以避免来自不相干线程在对象锁上的意外或恶意等待。

因为，如果一个恶意线程在对象的锁上等待，调度器在`notify()`后不幸将锁分配给了这个恶意线程，那这个重要的 “唤醒通知” 将被 “吞掉”。那个真正需要被唤醒的线程将陷入无限的等待。

如果所有程序员普遍使用`notifyAll()`替代`notify()`，那么`wait()`方法的阻塞必然会经常被在条件不满足的情况下被唤醒，因此，
> 为了配合`notifyAll()`非常 “大方” 的唤醒策略，每个`wait()`必须在每次被唤醒的时候都谨慎测试它的阻塞条件是否被跳过了，否则应该继续阻塞。

这样带来的另一个好处是：
> `while`轮询中的`wait()`同时能抵御来自不相干线程的 “恶意唤醒”。

时刻记住，每个在公有可访问对象上等待的线程都是将自己暴露在一个非常危险的环境里。任何拿到对象互斥锁的线程都能错误或者恶意将这个等待线程唤醒，不管条件是否满足。

#### 一个可中断任务的良好实践
一个线程在运行过程中是可以中断的。我们可以调用Thread.interrupt()方法来实现对线程的中断。或者执行shutdown(),shutdownNow()方法也会调用Thread.interrupt()方法来中断线程。当一个线程在正常运行状态被中断，会抛出一个InterruptedException。

概括起来，有四种情况能进入阻塞状态：
* `sleep()`方法申请休眠
* `wait()`或`await()`方法申请等待。`notify()`,`notifyAll()`或者`signal()`,`signalAll()`方法可以把线程从阻塞状态唤醒。
* I/O读写
* 互斥锁，如`synchronized`, `ReentrantLock`。

但中断请求本质上只是礼貌地请求另一个线程在它愿意并且方便的情况下停止它正在做的事，并没有强制力。所以当线程处于阻塞状态时，中断的请求并不一定会被理睬。概括来说规则很简单：
* 低级阻塞：`sleep()`, `wait()`的阻塞是可中断的。
* 高级阻塞：I/O读写，互斥锁（Synchronized, lock）是不可中断的。

关于什么阻塞可以中断，什么阻塞不可以中断，记住一条规则，
> 低级阻塞可中断，高级阻塞不可中断

换句话说：`sleep()`和`wait()`阻塞可中断。 I/O读写，和`synchronized`互斥锁不可中断。


对于每个希望确保能被中断的线程，都应该保持“轮询中断状态”的基本惯用法。外部的`try{}catch{}`代码块能确保能积极响应中断请求的低级阻塞能及时跳出。但对于不响应中断请求的高级阻塞，就要靠内部的while轮询`Thread.currentThread().isInterrupted()`线程中断状态来判断退出的时机。

```java
public void run() {
	try {
		while (!Thread.currentThread().isInterrupted()){
			/* do something */
		}
	} catch (InterruptedException ie) {
		/* print something */
	}
}
```
