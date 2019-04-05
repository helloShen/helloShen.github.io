---
layout: post
title: "Leetcode - Algorithm - Design Circular Queue "
date: 2019-04-05 15:08:27
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Design your implementation of the circular queue. The circular queue is a linear data structure in which the operations are performed based on FIFO (First In First Out) principle and the last position is connected back to the first position to make a circle. It is also called "Ring Buffer".

One of the benefits of the circular queue is that we can make use of the spaces in front of the queue. In a normal queue, once the queue becomes full, we cannot insert the next element even if there is a space in front of the queue. But using the circular queue, we can use the space to store new values.

Your implementation should support following operations:
* `MyCircularQueue(k)`: Constructor, set the size of the queue to be k.
* `Front`: Get the front item from the queue. If the queue is empty, return -1.
* `Rear`: Get the last item from the queue. If the queue is empty, return -1.
* `enQueue(value)`: Insert an element into the circular queue. Return true if the operation is successful.
* `deQueue()`: Delete an element from the circular queue. Return true if the operation is successful.
* `isEmpty()`: Checks whether the circular queue is empty or not.
* `isFull()`: Checks whether the circular queue is full or not.


Example:
```
MyCircularQueue circularQueue = new MyCircularQueue(3); // set the size to be 3
circularQueue.enQueue(1);  // return true
circularQueue.enQueue(2);  // return true
circularQueue.enQueue(3);  // return true
circularQueue.enQueue(4);  // return false, the queue is full
circularQueue.Rear();  // return 3
circularQueue.isFull();  // return true
circularQueue.deQueue();  // return true
circularQueue.enQueue(4);  // return true
circularQueue.Rear();  // return 4
```

Note:
* All values will be in the range of `[0, 1000]`.
* The number of operations will be in the range of `[1, 1000]`.
* Please do not use the built-in Queue library.

### 用一个定长数组
基本思想很简单：如果下标超出`idx > size`，则对下标取模`idx %= size`。需要额外的一个空间标注出当前窗口头部`head`，以及当前窗口尾部`curr`。
```
        curr  head
         |     |
    | | | | | | | | |
               |-----        <- 实际窗口
    ---->|
```

实际写代码会遇到一个小问题，
> 当`head == curr`的时候到底数组是空还是满了？

所以需要额外的一个空间记录`head`和`curr`是第一次重合，还是第二次重合。首次重合代表数组为空，第二次重合代表数组满了。

最后还要注意一个特例：
> 列表允许的最大空间为0。此时初始化的时候列表处在即为空，又为满的状态。

#### 代码
```java
class MyCircularQueue {

    private int[] table;
    private int size, head, curr;
    private boolean isFull, isEmpty;

    /** Initialize your data structure here. Set the size of the queue to be k. */
    public MyCircularQueue(int k) {
        table = new int[k];
        size = k;
        head = 0;
        curr = 0;
        isFull = (k == 0)? true : false;
        isEmpty = true;
    }

    /** Insert an element into the circular queue. Return true if the operation is successful. */
    public boolean enQueue(int value) {
        if (!isFull()) {
            table[curr++] = value;
            curr %= size;
            if (curr == head) isFull = true;
            isEmpty = false;
            return true;
        } else {
            return false;
        }
    }

    /** Delete an element from the circular queue. Return true if the operation is successful. */
    public boolean deQueue() {
        if (!isEmpty()) {
            head++;
            head %= size;
            if (curr == head) isEmpty = true;
            isFull = false;
            return true;
        } else {
            return false;
        }
    }

    /** Get the front item from the queue. */
    public int Front() {
        if (isEmpty()) return -1;
        return table[head];
    }

    /** Get the last item from the queue. */
    public int Rear() {
        if (isEmpty()) return -1;
        int last = (curr - 1 + size) % size;
        return table[last];
    }

    /** Checks whether the circular queue is empty or not. */
    public boolean isEmpty() {
        return isEmpty;
    }

    /** Checks whether the circular queue is full or not. */
    public boolean isFull() {
        return isFull;
    }
}

/**
 * Your MyCircularQueue object will be instantiated and called as such:
 * MyCircularQueue obj = new MyCircularQueue(k);
 * boolean param_1 = obj.enQueue(value);
 * boolean param_2 = obj.deQueue();
 * int param_3 = obj.Front();
 * int param_4 = obj.Rear();
 * boolean param_5 = obj.isEmpty();
 * boolean param_6 = obj.isFull();
 */
```

#### 结果
![design-circular-queue-1](/images/leetcode/design-circular-queue-1.png)
