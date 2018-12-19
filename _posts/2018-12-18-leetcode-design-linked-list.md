---
layout: post
title: "Leetcode - Algorithm - Design Linked List "
date: 2018-12-18 20:59:14
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "easy"
description: >
---

### 题目
Design your implementation of the linked list. You can choose to use the singly linked list or the doubly linked list. A node in a singly linked list should have two attributes: val and next. val is the value of the current node, and next is a pointer/reference to the next node. If you want to use the doubly linked list, you will need one more attribute prev to indicate the previous node in the linked list. Assume all nodes in the linked list are 0-indexed.

Implement these functions in your linked list class:
* get(index) : Get the value of the index-th node in the linked list. If the index is invalid, return -1.
* addAtHead(val) : Add a node of value val before the first element of the linked list. After the insertion, the new node will be the first node of the linked list.
* addAtTail(val) : Append a node of value val to the last element of the linked list.
* addAtIndex(index, val) : Add a node of value val before the index-th node in the linked list. If index equals to the length of linked list, the node will be appended to the end of linked list. If index is greater than the length, the node will not be inserted.
* deleteAtIndex(index) : Delete the index-th node in the linked list, if the index is valid.

Example:
```
MyLinkedList linkedList = new MyLinkedList();
linkedList.addAtHead(1);
linkedList.addAtTail(3);
linkedList.addAtIndex(1, 2);  // linked list becomes 1->2->3
linkedList.get(1);            // returns 2
linkedList.deleteAtIndex(1);  // now the linked list is 1->3
linkedList.get(1);            // returns 3
```

Note:
* All values will be in the range of [1, 1000].
* The number of operations will be in the range of [1, 1000].
* Please do not use the built-in LinkedList library.

### 解法1

#### 代码
```java
class MyLinkedList {

    public static class Node {
        private int val;
        private Node next;
        public Node(int val) {
            this.val = val;
        }
    }

    private int size;
    private Node head;
    private Node tail;

    /** Initialize your data structure here. */
    public MyLinkedList() {
        size = 0;
        head = null;
        tail = null;
    }

    /** Get the value of the index-th node in the linked list. If the index is invalid, return -1. */
    public int get(int index) {
        if (size == 0 || index >= size) return -1;
        // asset: size > 0 && index >= 0 && exists valid result
        Node target = head;
        for (int i = 0; i < index; i++) {
            target = target.next;
        }
        return target.val;
    }

    /** Add a node of value val before the first element of the linked list. After the insertion, the new node will be the first node of the linked list. */
    public void addAtHead(int val) {
        Node newNode = new Node(val);
        if (size == 0) {
            head = newNode;
            tail = head;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    /** Append a node of value val to the last element of the linked list. */
    public void addAtTail(int val) {
        Node newNode = new Node(val);
        if (size == 0) {
            head = newNode;
            tail = head;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /** Add a node of value val before the index-th node in the linked list. If index equals to the length of linked list, the node will be appended to the end of linked list. If index is greater than the length, the node will not be inserted. */
    public void addAtIndex(int index, int val) {
        if (index > size) return;
        if (index == 0) {
            addAtHead(val);
        } else if (index == size) {
            addAtTail(val);
        } else {
            Node newNode = new Node(val);
            Node pre = head;
            for (int i = 1; i < index; i++) pre = pre.next;
            newNode.next = pre.next;
            pre.next = newNode;
            size++;
        }
    }

    /** Delete the index-th node in the linked list, if the index is valid. */
    public void deleteAtIndex(int index) {
        if (size == 0 || index >= size) return;
        if (size == 1) {
            head = null;
            tail = null;
        } else if (index == 0){
            head = head.next;
        } else {
            Node pre = head;
            for (int i = 1; i < index; i++) pre = pre.next;
            if (pre.next == tail) tail = pre;
            pre.next = pre.next.next;
        }
        size--;
    }
}

/**
 * Your MyLinkedList object will be instantiated and called as such:
 * MyLinkedList obj = new MyLinkedList();
 * int param_1 = obj.get(index);
 * obj.addAtHead(val);
 * obj.addAtTail(val);
 * obj.addAtIndex(index,val);
 * obj.deleteAtIndex(index);
 */
```

#### 结果
![design-linked-list-1](/images/leetcode/design-linked-list-1.png)
