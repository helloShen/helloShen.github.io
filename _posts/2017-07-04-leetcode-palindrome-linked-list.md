---
layout: post
title: "Leetcode - Algorithm - Palindrome Linked List "
date: 2017-07-04 21:00:51
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list","stack","two pointers"]
level: "easy"
description: >
---

### 题目
Given a singly linked list, determine if it is a palindrome.

Follow up:
Could you do it in $$O(n)$$ time and $$O(1)$$ space?

### 用一个`Stack`记录前半段的节点
* time: $$O(n)$$
* space: $$O(n)$$

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
    public boolean isPalindrome(ListNode head) {
        if (head == null) { return true; }
        Deque<Integer> stack = new LinkedList<>();
        int size = size(head);
        boolean isOdd = ((size % 2) == 0)? false : true;
        int mid = (size - 1) / 2;
        ListNode cur = head;
        for (int i = 0; i <= mid; i++) {
            stack.offerFirst(cur.val);
            cur = cur.next;
        }
        int rest = mid;
        if (isOdd) { stack.pollFirst(); rest = mid-1; }
        for (int i = 0; i <= rest; i++) {
            Integer history = stack.pollFirst();
            if (history == null || cur == null) { return false; }
            if (history != cur.val) { return false; }
            cur = cur.next;
        }
        if (!stack.isEmpty() || cur != null) { return false; }
        return true;
    }
    public int size(ListNode head) {
        ListNode cur = head;
        int count = 0;
        while (cur != null) {
            count++;
            cur = cur.next;
        }
        return count;
    }
}
```

#### 结果
![palindrome-linked-list-1](/images/leetcode/palindrome-linked-list-1.png)


### 反转前半段，再和后半段比较
比如有链表，
```
    1->2->3->3->2->1
```
反转前半段，后变成，
```
   head                    slow
    |                       |
    3->2->1     和          3->2->1
```

* time: $$O(n)$$
* space: $$O(1)$$


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
    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) { return true; }
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            ListNode next = slow.next;
            slow.next = dummy.next;
            dummy.next = slow;
            if (head.next != null) { head.next = null; }
            slow = next;
        }
        if (fast != null) { slow = slow.next; } // size is odd
        head = dummy.next;
        while (head != null && slow != null) { // two linked list must have the same size
            if (head.val != slow.val) { return false; }
            head = head.next; slow = slow.next;
        }
        return true;
    }
}
```

#### 结果
![palindrome-linked-list-2](/images/leetcode/palindrome-linked-list-2.png)
