---
layout: post
title: "Leetcode - Algorithm - Rotate List "
date: 2017-04-18 16:03:49
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list","two pointers"]
level: "medium"
description: >
---

### 题目
Given a list, rotate the list to the right by k places, where k is non-negative.

For example:
```
Given 1->2->3->4->5->NULL and k = 2,
return 4->5->1->2->3->NULL.
```

### 先就算NodeList长度，然后计算出断点位置 $$O(n)$$
假设有`1->2->3->4->5`，转动`12`次。先遍历一遍到尾部，计算出List长度为`5`。然后断点的位置就是，因为`k`超过list的长度会转回原来的位置。
> `size - 1 - (k % len)`

用`LinkedList`复杂度本来应该是$$O(1)$$。因为可以直接获得`size`，直接算出断点，直接嫁接。但题目定死了要用最简陋的单向链表，所以计算长度必须遍历链表到末尾节点，复杂度为$$O(n)$$。

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
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null) { return head; }
        ListNode tail = head;
        int size = 1;
        while (tail.next != null) {
            tail = tail.next;
            size++;
        } // stop at the last element, get the size of list
        int breakPoint = (size - 1 - k) % size;
        if (breakPoint < 0) { breakPoint += size; }
        if (breakPoint != (size - 1)) { // when true, keep the same list
            ListNode newTail = head;
            for (int i = 0; i < breakPoint; i++) { // stop at the breaking point
                newTail = newTail.next;
            }
            tail.next = head;
            head = newTail.next;
            newTail.next = null;
        }
        return head;
    }
}
```

#### 结果
![rotate-list-1](/images/leetcode/rotate-list-1.png)
