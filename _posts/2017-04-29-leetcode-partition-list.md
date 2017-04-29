---
layout: post
title: "Leetcode - Algorithm - Partition List "
date: 2017-04-29 14:24:31
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "medium"
description: >
---

### 题目
Given a linked list and a value x, partition it such that all nodes less than x come before nodes greater than or equal to x.

You should preserve the original relative order of the nodes in each of the two partitions.

For example,
Given `1->4->3->2->5->2` and x = `3`,
return `1->2->2->4->3->5`.

### 拆分成`ls`和`gt`两个链表
分别用`ls`和`gt`两个指针，拆分出两个链表。最后再把两个链表嫁接在一起。比如`1->4->3->2->5->2`拆成`1->2->2`和`4->3->5`，再接起来。

用两个指针分开整理，比单纯用一个指针跳来跳去更好写，更易懂，更安全。

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
    public ListNode partition(ListNode head, int x) {
        ListNode ls = new ListNode(0), gt = new ListNode(0), lsHead = ls, gtHead = gt;
        ListNode cur = head;
        while (cur != null) {
            if (cur.val < x) {
                ls.next = cur;
                ls = ls.next;
            } else {
                gt.next = cur;
                gt = gt.next;
            }
            cur = cur.next;
        }
        ls.next = gtHead.next;
        gt.next = null;
        return lsHead.next;
    }
}
```

#### 结果
![partition-list-1](/images/leetcode/partition-list-1.png)


### 把小于目标值的元素剔除出链表
大同小异。

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
    public ListNode partition(ListNode head, int x) {
        ListNode sentinel = new ListNode(0), cur = sentinel;
        ListNode ls = new ListNode(0), lsHead = ls;
        sentinel.next = head;
        while (cur != null && cur.next != null) {
            if (cur.next.val < x) { // 发现小点
                ls.next = cur.next; // ls链表收编此小点
                ls = ls.next;
                cur.next = cur.next.next; // 主链表跳过此此小点
            } else { // 主链表正常推进
                cur = cur.next;
            }
        }
        ls.next = sentinel.next;
        return lsHead.next;
    }
}
```

#### 结果
![partition-list-2](/images/leetcode/partition-list-2.png)
