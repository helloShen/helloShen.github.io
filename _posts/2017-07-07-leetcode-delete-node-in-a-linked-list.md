---
layout: post
title: "Leetcode - Algorithm - Delete Node In A Linked List "
date: 2017-07-07 11:43:28
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "easy"
description: >
---

### 题目

Write a function to delete a node (except the tail) in a singly linked list, given only access to that node.

Supposed the linked list is `1 -> 2 -> 3 -> 4` and you are given the third node with value `3`, the linked list should become `1 -> 2 -> 4` after calling your function.

### 直接删节点是删不掉的，只能拷贝节点中的值
因为题目不给`head`头节点的引用，所以永远无法找到要删除的节点的前一个节点的地址。

所以，只能依次把后一个节点的值拷贝到前一个节点，然后删除最后一个尾节点。所以才要求需要删除的不是尾节点。

#### 代码
```java
public class Solution {
    public void deleteNode(ListNode node) {
        while (node.next != null) {
            node.val = node.next.val;
            if (node.next.next != null) {
                node = node.next;
            } else {
                node.next = null;
            }
        }
    }
}
```

#### 结果
![delete-node-in-a-linked-list-1](/images/leetcode/delete-node-in-a-linked-list-1.png)
