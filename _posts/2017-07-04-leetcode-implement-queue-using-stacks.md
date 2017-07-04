---
layout: post
title: "Leetcode - Algorithm - Implement Queue Using Stacks "
date: 2017-07-04 12:57:55
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["queue","stack"]
level: "easy"
description: >
---

### 题目
Implement the following operations of a queue using stacks.

* push(x) -- Push element x to the back of queue.
* pop() -- Removes the element from in front of queue.
* peek() -- Get the front element.
* empty() -- Return whether the queue is empty.

Notes:
You must use only standard operations of a stack -- which means only push to top, peek/pop from top, size, and is empty operations are valid.
Depending on your language, stack may not be supported natively. You may simulate a stack by using a list or deque (double-ended queue), as long as you use only standard operations of a stack.
You may assume that all operations are valid (for example, no pop or peek operations will be called on an empty queue).


### 用一个额外的`Stack`做缓存
* 时间复杂度：$$O(n)$$
* 空间复杂度：$$O(n)$$

#### 代码
```java
public class MyQueue {

        /** main container */
        private Deque<Integer> stack = new LinkedList<>();
        private Deque<Integer> buffer = new LinkedList<>();

        /** Push element x to the back of queue. */
        public void push(int x) {
            int size = stack.size();
            for (int i = 0; i < size; i++) { buffer.offerFirst(stack.pollFirst()); } // out all
            stack.offerFirst(x);
            for (int i = 0; i < size; i++) { stack.offerFirst(buffer.pollFirst()); } // back all
        }

        /** Removes the element from in front of queue and returns that element. */
        public int pop() {
            return stack.pollFirst();
        }

        /** Get the front element. */
        public int peek() {
            return stack.peekFirst();
        }

        /** Returns whether the queue is empty. */
        public boolean empty() {
            return stack.isEmpty();
        }
}
```

#### 结果
![implement-queue-using-stacks-1](/images/leetcode/implement-queue-using-stacks-1.png)


### 摊销法
前面的朴素方法里，每次都要把元素先倒出来到缓存`Stack`，然后再倒回去。

其实倒出来到缓存以后，就是我们要的`Queue`的顺序，这部分可以直接用。用完了再到原来的`Stack`里去倒出来。

比如，先压入`[a,b,c,d]`，
```
push(a);push(b);push(c);push(d);

    | |     | |
    | |     | |
    |d|     | |
    |c|     | |
    |b|     | |
    |a|     | |
    ---     ---
   input   output
```
现在要检查头部元素，`input`里的全倒出来到`output`，
```
peek();

    | |     | |
    | |     | |
    | |     |a|
    | |     |b|
    | |     |c|
    | |     |d|
    ---     ---
   input   output
```
在`output`已经是`Queue`的顺序。在`output`里的元素用完之前，不需要再从`input`里取出元素。`input`有新元素来，可以继续按照`Stack`顺序往下压。
```
push(e);push(f);push(g);push(h);

    | |     | |
    | |     | |
    |h|     |a|
    |g|     |b|
    |f|     |c|
    |e|     |d|
    ---     ---
   input   output
```

这样做，平均到每个元素，是 $$O(1)$$ 的时间。
* 时间复杂度：$$O(1)$$
* 空间复杂度：$$O(n)$$

#### 代码
```java
public class MyQueue {

    Deque<Integer> input = new LinkedList();
    Deque<Integer> output = new LinkedList();

    public void push(int x) {
        input.push(x);
    }

    public int pop() {
        check();
        return output.pollFirst();
    }

    public int peek() {
        check();
        return output.peekFirst();
    }

    public boolean empty() {
        return input.isEmpty() && output.isEmpty();
    }

    private void check() {
        if (output.isEmpty())
            while (!input.isEmpty())
                output.offerFirst(input.pollFirst());
    }
}
```

#### 结果
![implement-queue-using-stacks-2](/images/leetcode/implement-queue-using-stacks-2.png)
