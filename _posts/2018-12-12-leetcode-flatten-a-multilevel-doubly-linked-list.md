---
layout: post
title: "Leetcode - Algorithm - Flatten A Multilevel Doubly Linked List"
date: 2018-12-12 20:13:15
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["recursion", "linked list"]
level: "medium"
description: >
---

### 题目
You are given a doubly linked list which in addition to the next and previous pointers, it could have a child pointer, which may or may not point to a separate doubly linked list. These child lists may have one or more children of their own, and so on, to produce a multilevel data structure, as shown in the example below.

Flatten the list so that all the nodes appear in a single-level, doubly linked list. You are given the head of the first level of the list.

Example:
```
Input:
 1---2---3---4---5---6--NULL
         |
         7---8---9---10--NULL
             |
             11--12--NULL

Output:
1-2-3-7-8-11-12-9-10-4-5-6-NULL
```

Explanation for the above example:

Given the following multilevel doubly linked list:
![flatten-a-multilevel-doubly-linked-list](/images/leetcode/multilevellinkedlist-1.png)

We should return the following flattened doubly linked list:
![flatten-a-multilevel-doubly-linked-list](/images/leetcode/multilevellinkedlistflattened.png)

### 递归法
对于任意一个节点，处理的顺序如下，
1. 先把本节点加入链表
2. 递归展平`child`子串，并将其嫁接在本节点之后
3. 递归展平`next`子串，并将其嫁接在`child`子串之后

#### 代码
```java
/*
// Definition for a Node.
class Node {
    public int val;
    public Node prev;
    public Node next;
    public Node child;

    public Node() {}

    public Node(int _val,Node _prev,Node _next,Node _child) {
        val = _val;
        prev = _prev;
        next = _next;
        child = _child;
    }
};
*/
class Solution {

    public Node flatten(Node head) {
        if (head == null) return null;
        Node next = flatten(head.next);
        Node child = flatten(head.child);
        head.child = null;
        head.next = child;
        if (child != null) child.prev = head;
        Node oldHead = head;
        while (head != null && head.next != null) head = head.next;
        head.next = next;
        if (next != null) next.prev = head;
        return oldHead;
    }

}
```

#### 结果
![flatten-a-multilevel-doubly-linked-list-1](/images/leetcode/flatten-a-multilevel-doubly-linked-list-1.png)
