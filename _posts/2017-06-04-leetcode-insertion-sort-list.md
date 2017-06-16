---
layout: post
title: "Leetcode - Algorithm - Insertion Sort List "
date: 2017-06-04 01:21:49
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list","sort"]
level: "medium"
description: >
---

### 题目
Sort a linked list using insertion sort.

### 指针遍历
在`list`的头部维护一个排过序的子串，对`list`中的每个节点都遍历这个排过序的子串，然后插到合适的位置，保持这个子串有序。程序打印出来的过程如下：
```
Original List: 77->61->36->40->9->93->91->63->85->54
61 inserted before 77
Now List = 61->77->36->40->9->93->91->63->85->54
36 inserted before 61
Now List = 36->61->77->40->9->93->91->63->85->54
40 inserted before 61
Now List = 36->40->61->77->9->93->91->63->85->54
9 inserted before 36
Now List = 9->36->40->61->77->93->91->63->85->54
93 insert after 77
Now List = 9->36->40->61->77->93->91->63->85->54
91 inserted before 93
Now List = 9->36->40->61->77->91->93->63->85->54
63 inserted before 77
Now List = 9->36->40->61->63->77->91->93->85->54
85 inserted before 91
Now List = 9->36->40->61->63->77->85->91->93->54
54 inserted before 61
Now List = 9->36->40->54->61->63->77->85->91->93
Sorted List: 9->36->40->54->61->63->77->85->91->93
```

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
     public ListNode insertionSortList(ListNode head) {
         if (head == null || head.next == null) { return head; }
         ListNode dummy = new ListNode(0); dummy.next = head; // 哨兵
         ListNode pre = head, cur = head.next; // pre为顺序部分的尾巴。cur是乱序部分的开头，也是当前需要插入的节点
         outWhile:
         while (cur != null) {
             ListNode insertAfterIt = dummy; // 插入在它后面
             while (insertAfterIt.next != cur) {
                 if (insertAfterIt.next.val >= cur.val) {
                     ListNode temp = cur;
                     pre.next = cur.next;
                     cur = cur.next;
                     temp.next = insertAfterIt.next;
                     insertAfterIt.next = temp;
                     continue outWhile;
                 }
                 insertAfterIt = insertAfterIt.next;
             }
             pre = cur;
             cur = cur.next;
         }
         return dummy.next;
     }
 }
```

#### 结果
这里前面还有一个高峰，不用去管它，是除了`insertion`方法之外的排序法通过的。排序法这样已经不错了。
![insertion-sort-list-1](/images/leetcode/insertion-sort-list-1.png)
