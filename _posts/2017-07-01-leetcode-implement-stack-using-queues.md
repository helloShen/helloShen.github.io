---
layout: post
title: "Leetcode - Algorithm - Implement Stack Using Queues "
date: 2017-07-01 11:24:41
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack","queue","design"]
level: "easy"
description: >
---

### 题目
Implement the following operations of a stack using queues.
* push(x) -- Push element x onto stack.
* pop() -- Removes the element on top of the stack.
* top() -- Get the top element.
* empty() -- Return whether the stack is empty.

Notes:
You must use only standard operations of a queue -- which means only `push` to back, `peek/pop` from front, `size`, and `is empty` operations are valid.
Depending on your language, queue may not be supported natively. You may simulate a queue by using a list or deque (double-ended queue), as long as you use only standard operations of a queue.
You may assume that all operations are valid (for example, no pop or top operations will be called on an empty stack).

### 先全部倒出来，再插入
先把`queue`所有元素倒出来，缓存起来，再把新元素插入在头部。然后再把老元素放回来。

#### 代码
```java
public class MyStack {

        Queue<Integer> q = new LinkedList<>();

        /** * Push element x onto stack.  O(n) */
        public void push(int x) {
            Queue<Integer> buffer = new LinkedList<>();
            while (!q.isEmpty()) { buffer.offer(q.poll()); }
            q.offer(x);
            while (!buffer.isEmpty()) { q.offer(buffer.poll()); }
        }

        /** Removes the element on top of the stack and returns that element. */
        public int pop() {
            return q.poll();
        }

        /** Get the top element. */
        public int top() {
            return q.peek();
        }

        /** Returns whether the stack is empty. */
        public boolean empty() {
            return q.isEmpty();
        }

}
```

#### 结果
![implement-stack-using-queues-1](/images/leetcode/implement-stack-using-queues-1.png)


### 可以不使用额外辅助容器
从头部取出来的元素，再重新插回队列尾部。

#### 代码
```java
public class MyStack {

        Queue<Integer> q = new LinkedList<>();

        /** * Push element x onto stack.  O(n) */
        public void push(int x) {
            int size = q.size();
            q.offer(x);
            for (int i = 0; i < size; i++) {
                q.offer(q.poll());
            }
        }

        /** Removes the element on top of the stack and returns that element. */
        public int pop() {
            return q.poll();
        }

        /** Get the top element. */
        public int top() {
            return q.peek();
        }

        /** Returns whether the stack is empty. */
        public boolean empty() {
            return q.isEmpty();
        }

}
```

#### 结果
![implement-stack-using-queues-2](/images/leetcode/implement-stack-using-queues-2.png)
