---
layout: post
title: "Leetcode - Algorithm - Plus One Linked List "
date: 2018-08-28 12:58:08
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "medium"
description: >
---

### 题目
Given a non-negative integer represented as non-empty a singly linked list of digits, plus one to the integer.

You may assume the integer do not contain any leading zero, except the number 0 itself.

The digits are stored such that the most significant digit is at the head of the list.

Example:
```
Input:
1->2->3

Output:
1->2->4
```

### 利用递归模拟“双向链表”
单向链表只能往前找下一个元素，无法往回找前驱节点。但是递归可以利用递出去以后回归的过程找到前驱节点的引用。

#### 代码
```java
class Solution {
    public ListNode plusOne(ListNode head) {
        int carry = doubleLinked(head);     
        if (carry == 1) {
            ListNode newHead = new ListNode(1);
            newHead.next = head;
            head = newHead;
        }
        return head;
    }

    private int doubleLinked(ListNode node) {
        if (node == null) {
            return 1;
        }
        node.val += doubleLinked(node.next);
        int carry = node.val / 10;
        node.val %= 10;
        return carry;
    }
}
```

#### 结果
![plus-one-linked-list-1](/images/leetcode/plus-one-linked-list-1.png)
