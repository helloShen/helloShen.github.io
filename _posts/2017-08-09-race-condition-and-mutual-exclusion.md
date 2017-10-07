---
layout: post
title: "Race Condition and Mutual Exclusion"
date: 2017-08-09 01:14:01
author: "Wei SHEN"
categories: ["operating system"]
tags: ["concurrency"]
description: >
---

### 前言
这篇文章中讨论的竟态条件都只是指并发条件下，多个 **线程** 之间竞争资源。操作系统层面的进程之间的并发，不在本文讨论范围。

这篇文章主要说明竟态资源，临界区，自旋锁，互斥量，信号量，管程这些概念是怎么回事。要理解这些概念，最重要的是要抓住他们背后的逻辑链条，然后顺藤摸瓜：
1. 首先的头绪就是“竟态资源”就是多个线程能同时访问并修改的对象的公有字段。多个线程同时读写肯定会出乱子。
2. 然后“临界区”就是那些读写竟态资源的代码片段。我们需要管理的就是他们。
3. 最简单的管理的办法就是“忙等”。但是如果只用`if(available) { do(); } else { wait(); }`条件检查是不够的，因为检查和后续操作不是 “原子性”的。
4. 最简单的忙等的结构就是“自旋锁”。大白话说就是用一个`while(!available) { wait(); }`轮询忙等禁止线程在不恰当的时机执行。
5. 但“忙等”的问题就是效率太低。所以要`sleep()`和`wakeup()`高级一点的通信原语。`sleep()`和忙等比的优势就是它在条件不满足的情况下是阻塞自己，让渡出时间线，让别的线程先执行。
6. 然后用一个`bool`型变量来模拟一个锁。每次进入临界区之前都先检查锁的值是否为`0`，如果是`0`就抢占锁，把锁的值改为`1`，然后就可以进入临界区了。这就叫“互斥量”，也叫“互斥锁”。互斥锁的关键就是检查数值，修改变量值，以及可能发生的睡眠操作是由TSL(Test and Set Lock)指令保证其原子性。
7. 只允许一个线程进入临界区的用“互斥量”。允许多个线程同时进入临界区的用“信号量”。信号量把互斥量的`0`和`1`变为`[1~N]`。
8. 管程就是由语言为我们管理“互斥量”，比如Java同一时刻只允许一个有`synchronized`关键字保护的代码片段执行。

### 几个重要概念

### 竞态条件（Race Condition）和竟态资源（Race Resource）
**程序中多个线程共享彼此都能读写的公共存储区，就会形成竞态条件。** 不受任何保护的竟态条件不是线程安全的。主要是因为线程的执行顺序是受系统调度程序的控制。如果线程A在访问公共资源的过程中被挂起，另一个线程B继续访问共享资源，很可能就破坏了线程A所依赖资源的状态。

下面的 **"偶数生成器"** 的是一个很好的例子。`EvenGenerator`有一个域`currentEvenValue`帮助它生成偶数。每次`nextEven()`方法将`currentEvenValue`的值递增两次，然后返回它的值。单线程场景下，它每次都能正常生成递增的偶数。但在并发场景下，它会错误生成奇数。
```java
package com.ciaoshen.howtomcatworks.ex04.concurrency;

class EvenGenerator {

    private int currentEvenValue = 0; // 竟态资源（多个EvenChecker线程同时读写这个域）

    public int nextEven() {
        ++currentEvenValue;
        Thread.yield(); // cause failure faster
        ++currentEvenValue;
        return currentEvenValue;
    }
}
```
`EvenChecker`专门检查偶数生成器生成的数字是不是偶数。
```java
package com.ciaoshen.howtomcatworks.ex04.concurrency;

class EvenChecker implements Runnable {
    private static int count = 0;
    private int id;
    private EvenGenerator generator;
    private boolean stopped = false;
    public EvenChecker(EvenGenerator g) {
        id = ++count;
        generator = g;
    }
    // 可中断任务的良好实践
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int val = generator.nextEven();     // 让偶数生成器生成偶数
                if (val % 2 != 0) {                 // 发现奇数，报告用户
                    System.out.println("EvenChecker#" + id + " find val [" + val + "] is not even!");
                }
                Thread.sleep(1);
            }
        } catch (InterruptedException ie) {
                System.out.println("Thread#" + id + " stopped while sleep!");
                stopped = true;
        }
        System.out.println("Thread#" + id + " stopped correctly!");
    }
}
```
在并发的场景下，比如只有一个偶数生成器，但同时有多个`EvenChecker`调用生成器生成偶数，如果这个偶数生成器没有任何保护措施，它可能会错误生成奇数。原因就是比如1号`EvenChecker`调用`nextEven()`方法，在执行完第一行`currentEvenValue++`以后立即被调度器挂起，此时`currentEvenValue`的值是一个奇数。2号`EvenChecker`接手，对`currentEvenValue`做两次递增，并返回一个奇数。
```java
package com.ciaoshen.howtomcatworks.ex04.concurrency;
import java.util.concurrent.*;

class RaceCondition {
    public static void test(int size) {
        EvenGenerator g = new EvenGenerator();
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < size; i++) {
            exec.execute(new EvenChecker(g));
        }
        Thread.sleep(100);
        exec.shutdownNow();
    }
    public static void main(String[] args) {
        test(10);
    }
}
```

### 临界区域（critical region）和互斥（mutual exclusion)
如果把对访问竟态资源的程序片段称为 **临界区**。比如刚才`EvenGenerator`例子里的`nextEven()`函数，它改变了共享竟态资源`currentEvenValue`的值，
```java
public int nextEven() {
    ++currentEvenValue;
    Thread.yield(); // cause failure faster
    ++currentEvenValue;
    return currentEvenValue;
}
```
并发条件下一种很简单的保证安全的手段就是 **互斥(mutex)**，
> 互斥(mutex)就是确保当一个进程再使用一个共享数据是，其他进程不能同时读写共享数据。

互斥的效果应该是，假设有A和B两个线程都要访问某个公有资源，从A线程的角度看，另一个线程B要么不执行，要么就已经完整执行完全套动作。


### 忙等待互斥
最容易想到的互斥手段就是忙等。用大白话说，**就是A在读写共享数据，B就等他**。

#### 状态锁是不安全的，煮熟的鸭子飞了
互斥最容易想到的办法就是加 **状态锁**。就是增设一个共享的 **锁变量**。`0`表示没有被占用，处于可用状态。`1`表示已经被占用。程序在进入临界区前先检查锁变量的值，看目标资源是否已被占用。
```java
if (state == 0) {   // 测试
    state = 1;      // 加锁
    // do something
}
```

> 但状态锁在"测试和加锁"操作没有原子性保证的情况下，是不安全的。

上面这一小段代码实际上要分三部分来做，
1. 测试`state == 0`
2. 加锁`state = 1`
3. 实际操作资源

问题就在于第1步"测试"和第2步"加锁"之间不是原子性的。如果在A进程检查过资源可用以后进程立即被挂起，状态锁还没来得及改成`1`，B进程照样可以占用资源。进行了一般再切换回A进程时，A进程因为之前已经完成第一步检查状态锁，直接进入第二步，这时候就有两个线程在同时操作共享资源。这就是后面为什么要引入 **TSL** 指令的原因。

所以多线程的世界是不连续的，可以理解为有很多的平行时空。所以就不能用现实世界的经验去理解。你明明看到盘子里有一只煮熟的烤鸭，但当你伸叉子过去的时候，突然鸭子消失了。是因为你和平行时空的你共享这只鸭子，你要吃鸭子的一瞬间，上帝暂停了你的世界的时间片，让另一个平行时空继续运行。平行时空里的你吃了那只鸭子。

#### 朴素的 **自旋锁(spin lock)** 是安全的
忙等最朴素的方法就是自旋锁。自旋锁的关键就在于：
> 用一个`while`循环，让一个线程在状态锁处于某个状态的时候暂停运行， **忙等待**。就算时间片交到这个线程的手上，它也不会进入下一步的执行。

```c
// 线程A
while (TRUE) {
    while (turn != 0) {}    // 锁被占，循环忙等。
    critical_rigion();
    turn = 1;               // 释放锁
    noncritical_rigion();
}
```

```c
// 线程B
while (TRUE) {
    while (turn != 1) {}    // 锁被占，循环忙等
    critical_rigion();
    turn = 0;               // 释放锁
    noncritical_rigion();
}
```

#### Peterson锁
也是用一个循环忙等。但加入了 **后来者惩罚机制**。

#### TSL指令
一个硬件支持的方案。一条特殊的指令 **测试并加锁（Test and Set Lock）**，将检查变量值和加锁动作原子化。确保至少这个原子化的动作是不可中断的。硬件层面就是在锁定内存总线。

为什么要把 **测试** 和 **加锁** 操作合并起来原子化呢？前面说过 **状态锁** 是不安全的。关键问题就在于 `检查状态锁的状态`和`加锁`是两个分开的动作。如果A程序在检查完锁的状态，确认锁没有被占用，但还没来得及加锁的时刻被挂起了，之后B程序占用了锁，再回来A程序就会误以为锁还没有被占用。TSL原子化机器指令就是要把`检查状态锁的状态`和`加锁`两个动作合并为一个原子性的动作，不可以被中断。

用平行时空来理解，就是在我的时空，我要吃鸭子，看了一眼确定鸭子在的一瞬间，马上和上帝建立契约，现在鸭子归我了。就算切换到平行时空运行，那个世界的我也不能吃那只鸭子。


### 睡眠与唤醒
上面的自旋锁也好，Peterson锁也好，都是通过有条件的循环忙等来达到竞态条件下多进程（或线程）的和平相处。但缺点就是 **浪费CPU时间**。所以叫忙等嘛，一直占着CPU不断地问“好了吗，好了吗，好了吗”，其他进程进不来。

更好的办法是：
> 挂起（进入 **阻塞状态** ）无法进入临界区域的进程。

做这件事的系统调用就是 **sleep**。它将 **引起调用进程进入阻塞状态**，直到另外一个进程将其唤醒。 唤醒的这个系统调用叫 **wakeup**。 sleep和wakeup是一对非常重要的 **通信原语**。这里的 **原语** 是指由若干条机器指令完成的某个特定操作，特点是 **执行过程不可被中断，具有原子性**。

！注意，唤醒原语并 **没有直接把CPU让出来**，而只是 **使目标进程进入就绪状态，等待调度程序的调度**。wakeup的具体执行过程如下：
* 在该事件的等待队列中找到相应进程的PCB。
* 将其从等待队列中移出，并置其状态为就绪状态。
* 把该PCB插入就绪队列中，等待调度程序调度。

### 信号量
有了睡眠和唤醒原语，线程就不用忙等了。这时候可以引入信号量的概念。信号量简单讲，就是同时最多允许`N`个线程同时进入临界区。每个线程进入临界区之前都拿掉一个令牌。当令牌用完，想进入临界区的线程就只能阻塞等待。

**!注意**：要保证信号量安全，**检查数值，修改变量，以及可能发生的睡眠操作均必须为一个单一的，不可分割的原子操作**。

### 互斥量
互斥量就是简化版的信号量。只有`0`和`1`两个可能取值。只能表示 **被占用**，和 **未被占用** 两种状态。没有缓冲。实际应用中同一时刻都只允许有一个线程进入临界区，所以互斥量用来保护临界区非常有用。

### Pthread中的 **互斥量**
POSIX标准定义的线程包叫`Pthread`，作为对线程的抽象服务。每个`Pthread`线程都包含一个标识符，一组寄存器和一组存储在结构中的属性。

`Pthread`提供了操作互斥量的函数：
![posix-mutex](/images/thread-race-condition/posix-thread.png)

提一下`pthread_mutex_lock`和`pthread_mutex_trylock`的区别。
* `pthread_mutex_lock`: 如果锁已被占用，阻塞调用者。
* `pthread_mutex_trylock`: 如果锁已被占用，将返回错误代码，不阻塞调用者。

老规矩，所有操作必须保证 **原子性**，不可被中断。


### Pthread中的 **条件变量**
两个最重要的和条件变量有关的调用：
* `pthread_cond_wait`: 阻塞调用线程，直到另一其他线程向它发送一个信号（`pthread_cond_signal`信号）
* `pthread_cond_signal`: 它负责唤醒在条件变量上阻塞的线程。

老规矩，所有操作必须保证 **原子性**，不可被中断。

### **互斥量** 和 **条件变量** 是一起用的
典型的应用场景是：
> 线程A锁住一个互斥量，然后当它不能获得它期待的结果时等待一个条件变量。最后另一个线程会向它发信号，使它可以继续执行。

这里的关键就是:
> `pthread_cond_wait`调用包含一个解锁它持有的互斥量的动作。当然这个动作也是原子性的。

所以，一个线程在阻塞等待的时候，自动地就释放了它持有的互斥量，这样另一个线程才能接手当前进程。

### `Pthread`创建新线程，代码实例
`pthread_create`函数负责创建新的线程。四个参数的作用写在注释里了。
```c
#include <pthread.h>

int pthread_create(pthread_t *restrict tid,     // 新创建的线程ID会被放在thread指向的内存
       const pthread_attr_t *restrict attr,     // 指定不同的线程属性
       void *(*start_routine)(void*),           // 新线程从start_routine函数的地址开始运行
       void *restrict arg);                     // start_routine函数只能有一个参数（必要时可以打包成结构，保证只有一个参数）
```

几个注意点：
* 线程创建时不保证是先执行 **新创建的线程** 还是 **调用线程**。
* 直到主线程运行完成，如果新线程还没有得到机会运行的话，就没机会了，因为程序已经终止了。所以主线程应该主动让新线程接管当前进程。
* 新线程有可能在主线程调用`pthread_create`返回前就运行，这时候线程号`tid`可能还没有被分配好。

```c
#include "apue.h"
#include <pthread.h>

pthread_t ntid;

void printids(const char *s) {
    pid_t pid;
    pthread_t tid;

    pid = getpid();
    tid = pthread_self();
    printf("%s pid %lu tid %lu (0x%lx)\n", s, (unsigned long)pid, (unsigned long)tid, (unsigned long)tid);
}

void *thr_fn(void *arg) {
    printids("new thread: ");
    return((void *)0);
}

int main(void) {
    int err;

    err = pthread_create(&ntid, NULL, thr_fn, NULL);    // (1) 新线程通过thr_fn函数小勇printids()函数
    if (err != 0) {
        err_exit(err, "can't create thread");
    }
    printids("main thread:");   // (2) 主线程直接调用printids()函数
    sleep(1);
    exit(0);
}
```
首先看`pthread_create`函数是怎么调用的。线程ID被放在`pthread_t`类型数据上。实际上就是一个指向正整数的指针，被包装了一下。线程属性`attr`可以先为`NULL`。然后新线程被指定执行`thr_fn()`函数。函数参数`arg`也为`NULL`。

上面这个代码实际打印进程号`pid`和线程号`tid`的是`printids()`函数。`thr_fn()`函数其实不重要，就是间接调用`printids()`函数。关键的区别是：**哪个线程调用了`printids()`函数**。一共调用了两次，
1. 第一次，新线程通过`thr_fn`函数小勇`printids()`函数
2. 第二次，主线程直接调用`printids()`函数

但输出的结果，经常是先输出主线程的线程号`1`，然后在输出新线程的线程号`2`。这是因为主线程在调用了`printids()`函数之后才`sleep()`。之前虽然系统不保证新线程不会抢在主线程之前执行，但因为线程没有时钟中断，所以大部分情况下要等主线程`sleep()`之后，新线程才有机会执行。

### Pthread使用互斥量和条件变量，例子
![mutex-condition](/images/thread-race-condition/mutex-condition.png)

说几个调用，
1. `pthread_mutex_init()`函数初始化互斥量。返回值被存在`the_mutex`指向的内存空间。
2. `pthread_cond_init()`函数初始化条件变量。返回值被存在`condc`和`condp`指向的内存空间。
3. `pthread_create()`函数创建线程，分别制定了新线程执行的函数`producer`和`consumer`。线程号存在`con`和`pro`指向的内存空间。
4. `pthread_cond_wait()`调用线程在某个条件变量上睡眠，释放某个互斥锁。
5. `pthread_cond_signal()`唤醒在某个条件变量上睡眠的线程。但唤醒不代表直接让对方线程执行，只是让它进入就绪状态。

生产者和消费者协作的过程基本如下，
1. `pro`线程扮演生产者，执行`producer()`函数。`con`线程扮演消费者，执行`consumer()`函数。
2. 生产者和消费者共享一个缓冲区，就是`buffer`。缓冲区有个上限`MAX`，下限`0`。
3. 缓冲区的使用是互斥的。互斥锁`the_mutex`的作用就是，确保同一时刻只能有一个线程占用缓冲区。生产者和消费者在进入临界区域之前，都需要申请`the_mutex`互斥锁。申请失败，阻塞挂起(`pthread_mutex_lock`失败就阻塞)。
4. 生产者生产前检查条件变量，如果不为`0`，调用`pthread_cond_wait()`，释放互斥锁阻塞自己，把控制权让给消费者线程。同样，消费者在消费之前，也检查条件变量，如果为`0`，也调用`pthread_cond_wait()`，释放互斥锁阻塞自己，把控制权让给生产者线程。
5. 注意`pthread_cond_wait()`需要指定两个参数，一个条件变量，一个互斥量。所以需要两个条件变量，生产者在条件变量`condp`上睡眠，等会儿消费者调用`pthread_cond_signal()`也在`condp`上唤醒它。然后消费者在条件变量`condc`上睡，后面生产者在`condc`上唤醒它。他们睡眠时释放的互斥锁都是`the_mutex`。
6. 最后`pthread_join()`函数挂起主线程，先让生产者线程`pro`工作，一会儿生产者自己挂起，回到主线程，主线程再让消费者`con`工作。然后就是`con`和`pro`之间互相轮流唤醒，睡眠的过程。

以上。


### 管程（monitor）
在POSIX协议的框架下，完成多进程的互斥操作实际上就是同时控制 **互斥量** 和 **条件变量**。 但整个过程还是比较容易犯错，形成死锁。

所以管程就说，编译器来管 **互斥量**， 程序员只操作 **条件变量**。这样出错的可能性就要小得多。
> 管程的本质就是：由编译器而非程序员来安排互斥。

所以记住，
> 管程是编程语言的一部分。由编译器来保证 **同一时刻管程中只能有一个活跃进程**。

#### Java是支持管程的：`synchronized`关键字
加了`synchronized`关键字修饰的函数，包含了两层承诺，
1. 既保证同一时刻只有一个线程在调用这段函数的代码。
2. 也保证拿到互斥锁的线程对竟态资源所做的修改，能立即被之后拿到锁的线程看到。

接下来条件变量的部分，Java提供了`wait`和`notify`两个过程，分别对应的是`sleep`和`wakeup`。但是像C，Pascal等大多数语言不支持管程。

在Java中使用互斥锁`synchronized`的一种惯用法是：
> 将所有可变状态封装在对象内部，并通过互斥锁对所有访问对象内部可变状态的代码路径进行同步。

另一条原则是：
> 应该尽可能将不影响共享状态，但又执行时间较长的过程从同步过程中分离出去。
