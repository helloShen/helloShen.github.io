---
layout: post
title: "Leetcode - Algorithm - Remove Duplicates From Sorted List "
date: 2017-04-28 22:58:23
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "easy"
description: >
---

### 题目
Given a sorted linked list, delete all duplicates such that each element appear only once.

For example,
Given `1->1->2`, return `1->2`.
Given `1->1->2->3->3`, return `1->2->3`.

### 和后一个比较，不使用额外空间
和`Remove Duplicates From Sorted List Two`一样，直接跳到值相同的最后一个元素。然后直接修改指针。

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
    public ListNode deleteDuplicates(ListNode head) {
        ListNode sentinel = new ListNode(0), res = sentinel;
        sentinel.next = head;
        ListNode cur = head;
        while (cur != null) {
            while (cur.next != null && cur.val == cur.next.val) {
                cur = cur.next;
            } // stop at last node holds the same value
            res.next = cur;
            res = res.next;
            cur = cur.next;
        }
        res.next = null;
        return sentinel.next;
    }
}
```

#### 结果
![remove-duplicates-from-sorted-list-1](/images/leetcode/remove-duplicates-from-sorted-list-1.png)


### 和前一个比较，不用额外空间

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
    public ListNode deleteDuplicates(ListNode head) {
        ListNode sentinel = new ListNode(0), res = sentinel;
        sentinel.next = head;
        ListNode pre = null, cur = head;
        while (cur != null) {
            if (pre == null || cur.val != pre.val) {
                res.next = cur;
                res = res.next;
            }
            pre = cur; cur = cur.next;
        }
        res.next = null;
        return sentinel.next;
    }
}
```

#### 结果
![remove-duplicates-from-sorted-list-2](/images/leetcode/remove-duplicates-from-sorted-list-2.png)


### 用$$O(1)$$额外空间，记录上一个元素
按理用一个`register`记录前一个元素，省去了每次都操作指针和值比较的时间。但注意这里必须用`Integer`，而反复的装箱拆箱的开销其实非常大。效率会受影响。

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
    public ListNode deleteDuplicates(ListNode head) {
        ListNode sentinel = new ListNode(0), res = sentinel;
        sentinel.next = head;
        ListNode cur = head;
        Integer register = null;
        while (cur != null) {
            if (register == null || cur.val != register) {
                res.next = cur;
                res = res.next;
                register = cur.val;
            }
            cur = cur.next;
        }
        res.next = null;
        return sentinel.next;
    }
}
```

#### 结果
![remove-duplicates-from-sorted-list-3](/images/leetcode/remove-duplicates-from-sorted-list-3.png)

### 一段完美的代码
不是我写的，但我很喜欢，但我不追求每段代码都这么完美。

```java
public ListNode deleteDuplicates(ListNode head) {
    ListNode current = head;
    while (current != null && current.next != null) { // 和下一个元素比较
        if (current.next.val == current.val) {
            current.next = current.next.next;
        } else {
            current = current.next;
        }
    }
    return head; // 不需要防御，如果list长度小于2，直接返回
}
```
