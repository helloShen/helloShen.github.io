---
layout: post
title: "Concurrency - \"Producer - Consumer\" Pattern in Java"
date: 2016-12-12 23:31:11
author: "Wei SHEN"
categories: ["java","design pattern"]
tags: ["concurrency"]
description: >
  “生产者 - 消费者”模式是并发协作场景中的一种基本模型。它的交叉等待，唤醒的处理方式引发了我对于给哪些对象上锁的一些思考。
---

### 交叉等待唤醒
先上代码，还是书上经典的”服务员-厨师“的例子。
```java
class Meal {
	private final int orderNum;
	public Meal(int orderNum) { this.orderNum = orderNum; }
	public String toString() { return "Meal " + orderNum; }
}

class WaitPerson implements Runnable {
	private Restaurant restaurant;
	public WaitPerson(Restaurant r) { restaurant = r; }
	public void run() {
		try {
			while(!Thread.interrupted()) {
				synchronized(this) {
					while(restaurant.meal == null){
						wait(); // ... for the chef to produce a meal
					}
				}
				print("Waitperson got " + restaurant.meal);
				synchronized(restaurant.chef) {
					restaurant.meal = null;
					restaurant.chef.notifyAll(); // Ready for another
				}
			}
		} catch(InterruptedException e) {
			print("WaitPerson interrupted");
		}
	}
}

class Chef implements Runnable {
	private Restaurant restaurant;
	private int count = 0;
	public Chef(Restaurant r) { restaurant = r; }
	public void run() {
		try {
			while(!Thread.interrupted()) {
				synchronized(this) {
					while(restaurant.meal != null){
						wait(); // ... for the meal to be taken
					}
				}
				if(++count == 10) {
					print("Out of food, closing");
					restaurant.exec.shutdownNow();
				}
				printnb("Order up! ");
				synchronized(restaurant.waitPerson) {
					restaurant.meal = new Meal(count);
					restaurant.waitPerson.notifyAll();
				}
				TimeUnit.MILLISECONDS.sleep(100);
			}
		} catch(InterruptedException e) {
			print("Chef interrupted");
		}
	}
}

public class Restaurant {
	Meal meal;
	ExecutorService exec = Executors.newCachedThreadPool();
	WaitPerson waitPerson = new WaitPerson(this);
	Chef chef = new Chef(this);
	public Restaurant() {
		exec.execute(chef);
		exec.execute(waitPerson);
	}
	public static void main(String[] args) {
		new Restaurant();
	}
}
```

上述代码里，Restaurant类和Meal类被极端地简化，以至于整个餐馆只有一个Meal对象（相当于成菜队列只有一个槽位）。而最主要的就是WaitPerson和Chef两个类。各自的过程，概括来讲就是：
* 当菜没做好时，服务员一直在自己的互斥锁上等着。如果醒过来的时候菜做好了，就拿走菜，然后跑到厨师的互斥锁上叫醒厨师，再做一个菜。
* 厨师当菜做好的时候，一直在自己的互斥锁上等着。如果醒过来的时候菜没了，就再做一个菜，然后跑到服务员的互斥锁上叫醒服务员，菜做好了。

一般更多地是在竟态资源上加互斥锁，像这样在自己锁上等，然后再跑到对方互斥锁上叫醒对方的做法真的很优雅，而且厨师和服务员的行为非常对称，很美。最关键的，确实能解决问题。

### 利用阻塞队列是更好的选择
上面这个服务员-厨师的例子是个很极端的模拟场景。实际的餐馆会有一块专门放成菜的区域，不可能同时只能有一盘菜。所以做菜和上菜的公共区域其实是有一个缓冲区的。而且呢，上面的交叉等待唤醒的做法虽然很美，但缺点是服务员和厨师的耦合非常高。两个线程必须严丝合缝地互相协作，很容易出错。更好的一个做法就是在服务员和厨师之间利用一个有界阻塞队列BlockingQueue来相互沟通。当队列为空时，会阻塞take()方法，当队列满了，会阻塞put()方法。这样厨师和服务员将彻底解耦，各自只面向同一个阻塞队列。而且形式上，这样的做法也更加符合现实。
