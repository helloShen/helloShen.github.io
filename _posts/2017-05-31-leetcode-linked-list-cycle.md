---
layout: post
title: "Leetcode - Algorithm - Linked List Cycle "
date: 2017-05-31 18:18:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list","two pointers"]
level: "easy"
description: >
---

### 题目
Given a linked list, determine if it has a cycle in it.

Follow up:
Can you solve it without using extra space?

### 常规的遍历链表，时间复杂度 $$O(n)$$，空间复杂度 $$O(n)$$
用一个`Map`储存遇到过的节点。

#### 代码
```java
/**
 * Definition for singly-linked list.
 * class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
public class Solution {
    public boolean hasCycle(ListNode head) {
        ListNode cur = head;
        Set<ListNode> memo = new HashSet<>();
        while (cur != null) {
            if (cur.next != null && memo.contains(cur)) { return true; }
            memo.add(cur);
            cur = cur.next;
        }
        return false;
    }
}
```

#### 结果
![linked-list-cycle-1](/images/leetcode/linked-list-cycle-1.png)


### 把每个遇到过的节点的`next`节点都指向`head`，时间复杂度 $$O(n)$$，空间复杂度 $$O(0)$$
每个遍历过的节点的`next`引用全部指向`head`。如果有`cycle`存在，则会在此遍历到`head`节点，否则就不会。

这样做的缺点是会破坏链表的结构。遍历过一遍之后，链表就不能用了。

#### 代码
```java
/**
 * Definition for singly-linked list.
 * class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
public class Solution {
    public boolean hasCycle(ListNode head) {
        if (head == null) { return false; }
        ListNode cur = head.next;
        while (cur != null) {
            if (cur == head) { return true; }
            ListNode temp = cur;
            cur = cur.next;
            temp.next = head;
        }
        return false;
    }
}
```

#### 结果
![linked-list-cycle-2](/images/leetcode/linked-list-cycle-2.png)


### 夸父追日的故事，时间复杂度 $$O(n)$$，空间复杂度 $$O(0)$$
好吧，这个方法不是我想出来的。不过实在太聪明。

设置一个每次移动两格的`runner`，和一个每次移动一格的`walker`。如果有`cycle`，则总有一天`runner`能套`walker`一圈，两者指向同一个节点。

这个方法不破坏原有的链表。

#### 代码
```java
/**
 * Definition for singly-linked list.
 * class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
public class Solution {
    public boolean hasCycle(ListNode head) {
        if (head == null) { return false; }
        ListNode walker = head, runner = head;
        while (runner.next != null && runner.next.next != null) { // 能跑到头，则没有cycle
            walker = walker.next;
            runner = runner.next.next;
            if (walker == runner) { return true; }
        }
        return false;
    }
}
```

#### 结果
![linked-list-cycle-3](/images/leetcode/linked-list-cycle-3.png)
