---
layout: post
title: "Leetcode - Algorithm - Reverse Linked List "
date: 2017-06-18 23:28:36
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "easy"
description: >
---

### 题目
Reverse a singly linked list.

### 思路
没什么花样，直接操作指针，就已经是 $$O(n)$$ 复杂度。而且不使用额外空间。不可能更好了。

### 直接操作指针
老规矩，前面加一个哨兵，操作更简单。

#### 代码
```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) { val = x; }
 * }
 */
public class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode dummy = new ListNode(0); // 哨兵
        dummy.next = head;
        while (head != null && head.next != null) {
            ListNode next = head.next;
            head.next = next.next;
            next.next = dummy.next;
            dummy.next = next;
        }
        return dummy.next;
    }
}
```

#### 结果
![reverse-linked-list-1](/images/leetcode/reverse-linked-list-1.png)

### 递归版

#### 代码
```java
public class Solution {
    public ListNode reverseList(ListNode head) {
        return recursion(head);
    }
    public ListNode recursion(ListNode head) {
        if (head == null || head.next == null) { return head; }
        ListNode sub = recursion(head.next);
        head.next.next = head;
        head.next = null;
        return sub;
    }
}
```

#### 结果
![reverse-linked-list-2](/images/leetcode/reverse-linked-list-2.png)
