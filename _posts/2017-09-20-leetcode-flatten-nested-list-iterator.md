---
layout: post
title: "Leetcode - Algorithm - Flatten Nested List Iterator "
date: 2017-09-20 15:03:40
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack"]
level: "medium"
description: >
---

### 题目
Given a nested list of integers, implement an iterator to flatten it.

Each element is either an integer, or a list -- whose elements may also be integers or other lists.

Example 1:
Given the list `[[1,1],2,[1,1]]`,

By calling next repeatedly until hasNext returns false, the order of elements returned by next should be: `[1,1,2,1,1]`.

Example 2:
Given the list `[1,[4,[6]]]`,

By calling next repeatedly until hasNext returns false, the order of elements returned by next should be: `[1,4,6]`.

### 可以用一个`Stack`记录一系列`Iterator`

#### 代码
```java
/**
 * // This is the interface that allows for creating nested lists.
 * // You should not implement it, or speculate about its implementation
 * public interface NestedInteger {
 *
 *     // @return true if this NestedInteger holds a single integer, rather than a nested list.
 *     public boolean isInteger();
 *
 *     // @return the single integer that this NestedInteger holds, if it holds a single integer
 *     // Return null if this NestedInteger holds a nested list
 *     public Integer getInteger();
 *
 *     // @return the nested list that this NestedInteger holds, if it holds a nested list
 *     // Return null if this NestedInteger holds a single integer
 *     public List<NestedInteger> getList();
 * }
 */
public class NestedIterator implements Iterator<Integer> {

    private List<NestedInteger> nestedList;
    private Integer next;
    private Iterator<NestedInteger> currIte;
    private Deque<Iterator<NestedInteger>> stack;

    public NestedIterator(List<NestedInteger> nestedList) {
        this.nestedList = nestedList;
        currIte = nestedList.iterator();
        stack = new LinkedList<Iterator<NestedInteger>>();
        updateNext();
    }

    public Integer next() {
        Integer res = next;
        if (res != null) { updateNext(); }
        return res;
    }

    public boolean hasNext() {
        return next != null;
    }

    private void updateNext() {
        next = null;
        while (!stack.isEmpty() || currIte.hasNext()) {
            if (currIte.hasNext()) {
                NestedInteger nextEle = currIte.next();
                if (nextEle.isInteger()) {
                    next = nextEle.getInteger();
                    return;
                } else {
                    stack.offerFirst(currIte);
                    currIte = nextEle.getList().iterator();
                }
            } else {
                currIte = stack.pollFirst();
            }
        }
    }
}

/**
 * Your NestedIterator object will be instantiated and called as such:
 * NestedIterator i = new NestedIterator(nestedList);
 * while (i.hasNext()) v[f()] = i.next();
 */
```

#### 结果
![flatten-nested-list-iterator-1](/images/leetcode/flatten-nested-list-iterator-1.png)


### 也可以直接把元素在`Stack`里拆解

#### 代码
```java
/**
 * // This is the interface that allows for creating nested lists.
 * // You should not implement it, or speculate about its implementation
 * public interface NestedInteger {
 *
 *     // @return true if this NestedInteger holds a single integer, rather than a nested list.
 *     public boolean isInteger();
 *
 *     // @return the single integer that this NestedInteger holds, if it holds a single integer
 *     // Return null if this NestedInteger holds a nested list
 *     public Integer getInteger();
 *
 *     // @return the nested list that this NestedInteger holds, if it holds a nested list
 *     // Return null if this NestedInteger holds a single integer
 *     public List<NestedInteger> getList();
 * }
 */
public class NestedIterator implements Iterator<Integer> {
    private Deque<NestedInteger> stack;
    private Integer next;
    public NestedIterator(List<NestedInteger> nestedList) {
        stack = new LinkedList<NestedInteger>();
        int size = nestedList.size();
        for (int i = size - 1; i >= 0; i--) {
            stack.offerFirst(nestedList.get(i));
        }
        updateNext();
    }
    public Integer next() {
        Integer res = next;
        updateNext();
        return res;
    }
    public boolean hasNext() {
        return next != null;
    }
    private void updateNext() {
        next = null;
        while (!stack.isEmpty()) {
            NestedInteger curr = stack.pollFirst();
            if (curr.isInteger()) {
                next = curr.getInteger(); return;
            } else {
                List<NestedInteger> currList = curr.getList();
                int size = currList.size();
                for (int i = size - 1; i >= 0; i--) {
                    stack.offerFirst(currList.get(i));
                }
            }
        }
    }
}

/**
 * Your NestedIterator object will be instantiated and called as such:
 * NestedIterator i = new NestedIterator(nestedList);
 * while (i.hasNext()) v[f()] = i.next();
 */
```

#### 结果
![flatten-nested-list-iterator-2](/images/leetcode/flatten-nested-list-iterator-2.png)
