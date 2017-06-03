---
layout: post
title: "Leetcode - Algorithm - Reorder List "
date: 2017-06-02 23:47:54
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "medium"
description: >
---

### 题目
Given a singly linked list L: L0→L1→…→Ln-1→Ln,
reorder it to: L0→Ln→L1→Ln-1→L2→Ln-2→…

You must do this in-place without altering the nodes' values.

For example,
Given `{1,2,3,4}`, reorder it to `{1,4,2,3}`.



### 直接调换指针，时间复杂度 $$O(n)$$，不使用额外空间
需要遍历整个链表3遍，
1. 第一遍，获得链表的长度。
2. 第二遍：
    * 前半段：找出上位中位节点，把链表一切为二。`1-2-3-4-5-6`变成`1-2-3`和`4-5-6`。
    * 后半段：把第二段`4-5-6`反转成`6-5-4`。
3. 第三遍：把`1-2-3`和`6-5-4`交叉嫁接到一起，组成`1-6-2-5-3-4`。

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
    public void reorderList(ListNode head) {
        // 1st iteration: get size
        ListNode cur = head;
        int size = 0;
        while (cur != null) {
            size++;
            cur = cur.next;
        }
        if (size < 3) { return; }
        // 2nd iteration: 1-2-3-4-5-6  >>>   [1-2-3], [4-5-6]
        int mid = (size - 1) / 2; // 上位中位数
        cur = head;
        for (int i = 0; i < mid; i++) {
            cur = cur.next;
        }
        ListNode headTwo = cur.next;
        cur.next = null;
        // 2nd iteration: [1-2-3], [4-5-6]  >>>   [1-2-3], [6-5-4]
        ListNode curTwo = headTwo;
        while (curTwo.next != null) {
            ListNode temp = curTwo.next;
            curTwo.next = temp.next;
            temp.next = headTwo;
            headTwo = temp;
        }
        // 3rd iteration: [1-2-3],[6-5-4]  >>>  1-6-2-5-3-4
        cur = head;
        curTwo = headTwo;
        while (cur != null && curTwo != null) {
            ListNode one = cur;
            ListNode two = curTwo;
            cur = cur.next;
            curTwo = curTwo.next;
            two.next = one.next;
            one.next = two;
        }
    }
}
```

这里值得改进的一点是：**找中位节点，可以用一个`runner`指针，和一个`walker`指针完成。未必需要先求一遍链表的`size`。** 虽然，先找长度其实也没错。

整理一下，模块化以后，代码可以更整洁一点，
```java
public class Solution {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) { return; }
        // 1st iteration: 1-2-3-4-5-6  >>>   [1-2-3], [4-5-6]
        ListNode walker = head, runner = head, pre = head;
        while (runner != null && runner.next != null) {
            pre = walker;
            walker = walker.next;
            runner = runner.next.next;
        }
        ListNode headTwo = walker;
        pre.next = null;
        // 1st iteration: [1-2-3], [4-5-6]  >>>   [1-2-3], [6-5-4]
        headTwo = reverse(headTwo);
        // 2nd iteration: [1-2-3],[6-5-4]  >>>  1-6-2-5-3-4
        head = merge(head,headTwo);
    }
    public ListNode reverse(ListNode head) {
        ListNode cur = head;
        while (cur.next != null) {
            ListNode next = cur.next;
            cur.next = next.next;
            next.next = head;
            head = next;
        }
        return head;
    }
    public ListNode merge(ListNode firstHalf, ListNode secondHalf) {
        ListNode cur = firstHalf, curTwo = secondHalf, temp = null;
        while (cur != null && curTwo != null) {
            temp = curTwo;
            curTwo = curTwo.next;
            temp.next = cur.next;
            cur.next = temp;
            cur = temp.next;
        }
        if (curTwo != null) { temp.next = curTwo; }
        return firstHalf;
    }
}
```

#### 结果
银弹！
![reorder-list-1](/images/leetcode/reorder-list-1.png)
