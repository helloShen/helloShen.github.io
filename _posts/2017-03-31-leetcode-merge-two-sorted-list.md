---
layout: post
title: "Leetcode - Algorithm - Merge Two Sorted List "
date: 2017-03-31 14:15:50
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "easy"
description: >
---

### 题目
Merge two sorted linked lists and return it as a new list. The new list should be made by splicing together the nodes of the first two lists.
```
l1: 1->2->3->4->5->6->7->8->9->10
l2: 5->10->15->20
After Merge: 1->2->3->4->5->5->6->7->8->9->10->10->15->20
```

### 无额外空间迭代 $$O(n)$$
直接使用`l1`和`l2`的指针拼接。每次拼接当前指针指向的两个元素中较小的那一个。

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
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode sentinel = new ListNode(0), cursor = sentinel;
        int min = Integer.MAX_VALUE;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                min = l1.val;
                l1 = l1.next;
            } else {
                min = l2.val;
                l2 = l2.next;
            }
            cursor.next = new ListNode(min);
            cursor = cursor.next;
        }
        if (l1 != null) { cursor.next = l1; }
        if (l2 != null) { cursor.next = l2;}
        return sentinel.next;
    }
}
```

#### 结果
![merge-two-sorted-list-1](/images/leetcode/merge-two-sorted-list-1.png)


### 递归

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
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode sentinel = new ListNode(0), cursor = sentinel;
        mergeTwoListsRecursive(cursor,l1,l2);
        return sentinel.next;
    }
    public void mergeTwoListsRecursive(ListNode cursor, ListNode l1, ListNode l2) {
        if (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                cursor.next = l1;
                mergeTwoListsRecursive(cursor.next,l1.next,l2);
            } else {
                cursor.next = l2;
                mergeTwoListsRecursive(cursor.next,l1,l2.next);
            }
        }
        if (l1 == null) { cursor.next = l2; }
        if (l2 == null) { cursor.next = l1; }
    }
}
```

#### 结果
![merge-two-sorted-list-2](/images/leetcode/merge-two-sorted-list-2.png)


### 精简版递归
Sexy!

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
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (l1 == null) { return l2; }
        if (l2 == null) { return l1; }
        if (l1.val <= l2.val) {
            l1.next = mergeTwoLists(l1.next,l2);
            return l1;
        } else {
            l2.next = mergeTwoLists(l1,l2.next);
            return l2;
        }
    }
}
```

#### 结果
![merge-two-sorted-list-3](/images/leetcode/merge-two-sorted-list-3.png)
