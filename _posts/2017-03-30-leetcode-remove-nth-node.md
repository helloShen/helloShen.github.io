---
layout: post
title: "Leetcode - Algorithm - Remove Nth Node "
date: 2017-03-30 14:32:03
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode","pointer","linkedlist"]
level: "medium"
description: >
---

### 题目
Given a linked list, remove the nth node from the end of list and return its head.

For example,
```
   Given linked list: 1->2->3->4->5, and n = 2.

   After removing the second node from the end, the linked list becomes 1->2->3->5.
```
Note:
Given n will always be valid.
Try to do this in one pass.

### 维护一个额外List，$$O(n)$$
如果要删除第n个元素，操作的是第n-1个元素的指针。单向链表的问题是：回退不到上一个元素。用空间换时间的话，可以额外维护一个列表，储存单向链表中每个元素的引用。接下来就可以用$$O(1)$$的随机访问，找到目标元素的前一个元素。总体复杂度就是遍历一遍的开销$$O(n)$$。 为了逻辑更简单，在链表开头，加了一个哨兵元素。

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
    public ListNode removeNthFromEnd(ListNode head, int n) {
        List<ListNode> ref = parseListNode(head);
        int size = ref.size();
        if (size-1 < n) { return head; } // length < n
        int prevIndex = size - n - 1; // find the previous element
        ListNode prevNode = ref.get(prevIndex);
        prevNode.next = prevNode.next.next;
        return ref.get(0).next; // the 1st node after the sentinel
    }
    public List<ListNode> parseListNode(ListNode head) {
        List<ListNode> result = new ArrayList<>();
        ListNode sentinel = new ListNode(0); // sentinel
        sentinel.next = head;
        result.add(sentinel);
        ListNode cursor = head;
        while (cursor != null) {
            result.add(cursor);
            cursor = cursor.next;
        }
        return result;
    }
}
```

#### 结果
![remove-nth-node-1](/images/leetcode/remove-nth-node-1.png)


### 不使用额外空间 $$O(n)$$
这次不使用额外的List来保存每个元素的指针。而是只维护两个指针。**一个快指针，一个慢指针**。两者之间保持间隔n个元素。同样，为了让边界特殊情况一般化，在List开头插入了一个哨兵元素。

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
    // 不使用额外空间
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode sentinel = new ListNode(0);
        sentinel.next = head;
        ListNode slow = null, fast = sentinel;
        int cursor = 0; // index of fast
        while (fast != null) {
            fast = fast.next;
            if (slow != null) { slow = slow.next; }
            if (cursor - n == 0) { slow = sentinel; } // slow和fast之间间隔n格距离
            cursor++;
        }
        if (slow != null) {
            slow.next = slow.next.next;
        }
        return sentinel.next;
    }
}
```

#### 结果
银弹。
![remove-nth-node-2](/images/leetcode/remove-nth-node-2.png)
