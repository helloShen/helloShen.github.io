---
layout: post
title: "Leetcode - Algorithm - Middle Of The Linked List "
date: 2018-08-02 00:48:29
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "easy"
description: >
---

### 题目
Given a non-empty, singly linked list with head node head, return a middle node of linked list.

If there are two middle nodes, return the second middle node.


Example 1:
```
Input: [1,2,3,4,5]
Output: Node 3 from this list (Serialization: [3,4,5])
The returned node has value 3.  (The judge's serialization of this node is [3,4,5]).
Note that we returned a ListNode object ans, such that:
ans.val = 3, ans.next.val = 4, ans.next.next.val = 5, and ans.next.next.next = NULL.
```

Example 2:
```
Input: [1,2,3,4,5,6]
Output: Node 4 from this list (Serialization: [4,5,6])
Since the list has two middle nodes with values 3 and 4, we return the second one.
```

Note:
* The number of nodes in the given list will be between 1 and 100.

### 老套路：用一快一慢两个指针
快指针每次跳跃2格，慢指针每次走一格。当快指针走到头的时候，慢指针正好走到一半。

一个窍门是：
> 在前面加一个桩节点可以让代码更简洁（无论奇数或偶数个节点，代码一致）

![middle-of-the-linked-list-a](/images/leetcode/middle-of-the-linked-list-a.png)

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
class Solution {
        public ListNode middleNode(ListNode head) {
            if (head == null) { return null; }
            ListNode lead = new ListNode(0); // 桩节点
            lead.next = head;
            ListNode fast = lead, slow = lead;
            while (fast.next != null && fast.next.next != null) {
                fast = fast.next.next;
                slow = slow.next;
            }
            return slow.next;
        }
}
```

#### 结果
![middle-of-the-linked-list-1](/images/leetcode/middle-of-the-linked-list-1.png)
