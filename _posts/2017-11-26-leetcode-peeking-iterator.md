---
layout: post
title: "Leetcode - Algorithm - Peeking Iterator "
date: 2017-11-26 16:48:09
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["design"]
level: "medium"
description: >
---

### 题目
Given an Iterator class interface with methods: `next()` and `hasNext()`, design and implement a PeekingIterator that support the `peek()` operation -- it essentially `peek()` at the element that will be returned by the next call to `next()`.

Here is an example. Assume that the iterator is initialized to the beginning of the list: `[1, 2, 3]`.

Call `next()` gets you 1, the first element in the list.

Now you call `peek()` and it returns 2, the next element. Calling `next()` after that still return 2.

You call `next()` the final time and it returns 3, the last element. Calling `hasNext()` after that should return false.

Follow up: How would you extend your design to be generic and work with all types, not just integer?


### 用一个变量缓存下一个要返回的数字
因为传进来的是一个经典的迭代器，所以最朴素的做法，如果这个迭代器的输出和我们要的输出差别较大，且不可控，最差的情况我们可以预先把迭代器里的内容全部读取到内存。

当然这题没必要这么做。因为可以直接利用原始迭代器的输出。唯一需要处理的问题是原始迭代器的`next()`方法获取到下一个元素之后，无法再次获取同一个元素。所以解决办法很简单，就是把下一个元素缓存起来。

#### 代码
```java
// Java Iterator interface reference:
// https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
class PeekingIterator implements Iterator<Integer> {

    private Iterator<Integer> ite = null;
    private Integer next = null; // 缓存下一个要返回的元素

	public PeekingIterator(Iterator<Integer> iterator) {
	    // initialize any member here.
	    this.ite = iterator;
        if (this.ite.hasNext()) {
            next = this.ite.next();
        }
	}

    // Returns the next element in the iteration without advancing the iterator.
	public Integer peek() {
        return next;
	}

	// hasNext() and next() should behave the same as in the Iterator interface.
	// Override them if needed.
	@Override
	public Integer next() {
	    Integer res = next;
        next = (ite.hasNext())? ite.next() : null;
        return res;
	}

	@Override
	public boolean hasNext() {
	    return next != null;
	}
}
```

#### 结果
![peeking-iterator-1](/images/leetcode/peeking-iterator-1.png)
