---
layout: post
title: "Concurrency - Active Object"
date: 2016-12-12 21:07:40
author: "Wei SHEN"
categories: ["Java","Design_Pattern"]
tags: ["Concurrency"]
description: >
  活动对象模型的核心就是Future对象。原理就是通过Executor的submit()方法，把一个Callable请求提交给私有线程，立即返回一个Future对象，并插入一个结果队列。最后可以通过Future对象的isDone()方法判断结果是否计算完成。
---

### 什么是活动对象？
“活动对象”是并发编程模型的一种。“活动对象”实际上就是把对象封装成一个独立的线程。它有自己的执行线程，以及等待执行的任务列表。自由线程会按照一定的顺序执行任务列表的中的任务。而所有对对象方法的调用都会被转化成在线程上排队的一个队列。所以看上去，这个对象就会自动处理对这个对象方法的调用，而且暂时来不及处理的请求也会被缓存起来，但同时调用方法不会被阻塞，而是直接就返回了。所以又有了异步执行的特性。

### 核心是Future对象
活动对象模型的核心就是Future对象。原理就是通过Executor的submit()方法，把一个Callable请求提交给私有线程，立即返回一个Future对象，并插入一个结果队列。最后可以通过Future对象的isDone()方法判断结果是否计算完成。

另一个关键点就是对象的私有线程应该用Executors.newSingleThreadExecutor()这个单线程执行器。它维护着自己的无界阻塞队列，这里就免费成为了我们的消息队列。

### 代码示例
考虑下面这个场景:
> 汽车要打蜡（waxOn）和抛光(waxOff)。操作的步骤是：先打蜡，然后抛光，然后再上蜡，再抛光，循环往复。

#### 传统的面向过程的并发模型
这是一个很简单的场景。按照传统的并发编程范式的思路，可以抽象成两个机器人：一个专门打蜡机器人，一个专门抛光的机器人。两个机器人各自代表一个线程，共同来操作汽车对象。用最简单的wait()，notifyAll()互相进行协作，两个机器人的线程如下:

* 打蜡机器人：先打蜡，打完蜡叫醒抛光机器人，挂起，等待抛光机器人抛光完成。
* 抛光机器人：先等打蜡机器人打蜡，被叫醒后开始抛光，完成后叫醒打蜡机器人，自己挂起等待打蜡机器人打蜡。

具体代码如下，汽车类Car有一个boolean域代表汽车上蜡的状态，true代表表面有蜡，false代表表面没蜡是抛光的。然后汽车封装了4个基本动作。把动作封装到Car类里，是为了方便套上互斥锁。

* waxed(): 上蜡
* buffed(): 抛光
* waitForWaxing(): 等待上蜡
* waitForBuffing(): 等待抛光

```java
class Car {
	private boolean waxOn = false;
	public synchronized void waxed() {
		waxOn = true; // Ready to buff
		notifyAll();
	}
	public synchronized void buffed() {
		waxOn = false; // Ready for another coat of wax
		notifyAll();
	}
	public synchronized void waitForWaxing() throws InterruptedException {
		while(waxOn == false){
			wait();
		}
	}
	public synchronized void waitForBuffing() throws InterruptedException {
		while(waxOn == true){
			wait();
		}
	}
}
```

打蜡机器人就是先打蜡，然后叫醒在Car互斥锁上排队的所有线程。然后自己挂起，直到汽车的属性变为没打过蜡了再醒过来。
```java
class WaxOn implements Runnable {
	private Car car;
	public WaxOn(Car c) { car = c; }
	public void run() {
		try {
			while(!Thread.interrupted()) {
				printnb("Wax On! ");
				TimeUnit.MILLISECONDS.sleep(200);
				car.waxed();
				car.waitForBuffing();
			}
		} catch(InterruptedException e) {
			print("Exiting via interrupt");
		}
		print("Ending Wax On task");
	}
}
```

抛光机器人和打蜡机器人相反，上来就挂起，直到汽车属性变为打过蜡了才醒过来，然后开始抛光。然后再循环这个过程。
```java
class WaxOff implements Runnable {
	private Car car;
	public WaxOff(Car c) { car = c; }
	public void run() {
		try {
			while(!Thread.interrupted()) {
				car.waitForWaxing();
				printnb("Wax Off! ");
				TimeUnit.MILLISECONDS.sleep(200);
				car.buffed();
			}
		} catch(InterruptedException e) {
			print("Exiting via interrupt");
		}
		print("Ending Wax Off task");
	}
}
```

#### 利用活动对象模型
“活动对象”模型的处理方法，就是把waxOn()和waxOff()的动作都封装成Callable对象，提交给消息队列，并立即返回一个Future对象。由于使用的SingleThreadExecutor单线程执行器，会按照我们的提交顺序执行。如果我们依次提交 ”打蜡-抛光-打蜡-抛光-打蜡-抛光-... ...“，活动对象就会按顺序很好地完成交给它的工作。
```java
public class Exercise42{
    private static int carCount=0;
    private static int robotCount=0;
    private static List<ActiveCarRobot> robots=new ArrayList<ActiveCarRobot>();

    public class Car{
        private final int id=++carCount;
        private boolean waxOn=false;
        public void waxOn(){
            if(waxOn){System.out.println("Error, the wax already on!");return;}
            waxOn=true;
        }
        public void waxOff(){
            if(!waxOn){System.out.println("Error, should waxOn before waxOff!");return;}
            waxOn=false;
        }
        public String toString(){return "Car#"+id;}
    }

    public class ActiveCarRobot implements Runnable{
        private final int id=++robotCount;
        private final ExecutorService exec=Executors.newSingleThreadExecutor();	//必须是单线程执行器
        private List<Future<String>> results=new CopyOnWriteArrayList<Future<String>>();
        private Car car;
        public ActiveCarRobot(Car c){car=c;robots.add(this);}
        public String toString(){return "Robot#"+id;}

        public void run(){
            for(int i=0;i<10;i++){
                results.add(waxOn());
                sleep(10);
                results.add(waxOff());
            }
            showResults();
            shutdown();
        }
        public Future<String> waxOn(){
            return exec.submit(new Callable<String>(){	//把waxOn的动作封装成一个Callable对象，被提交给消息队列
                public String call(){
                    sleep(10);
                    car.waxOn();
                    return "    "+car+" wax on by "+ActiveCarRobot.this;
                }
            });
        }
        public Future<String> waxOff(){
            return exec.submit(new Callable<String>(){	//把waxOff的动作封装成一个Callable对象，被提交给消息队列
                public String call(){
                    sleep(10);
                    car.waxOff();
                    return "    "+car+" wax off by "+ActiveCarRobot.this;
                }
            });
        }
        public void sleep(int time){
            try{
                TimeUnit.MILLISECONDS.sleep(time);
            }catch(InterruptedException ie){
                System.out.println(this+" interrupted!");
            }
        }
        public void shutdown(){exec.shutdownNow();}
        public void showResults(){
            long endAt=System.currentTimeMillis()+5000;
            while(true){
                for(Future<String> f:results){
                    if(f.isDone()){
                        try{
                            System.out.println(f.get());
                        }catch(Exception e){
                            System.out.println("Error when reading the results!");
                        }
                    }
                    results.remove(f);
                }
                if(System.currentTimeMillis()>=endAt){break;}
            }
        }
    }

    public static void main(String[] args){
        Exercise42 test=new Exercise42();
        ExecutorService exec=Executors.newCachedThreadPool();
        for(int i=0;i<10;i++){
            exec.execute(test.new ActiveCarRobot(test.new Car()));
        }
        try{
            TimeUnit.SECONDS.sleep(5);
        }catch(InterruptedException ie){
            System.out.println("Test interrupted!");
        }
        exec.shutdownNow();
    }
}
```
