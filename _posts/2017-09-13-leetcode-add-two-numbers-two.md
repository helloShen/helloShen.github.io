---
layout: post
title: "Leetcode - Algorithm - Add Two Numbers Two "
date: 2017-09-13 17:36:41
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack"]
level: "medium"
description: >
---

### 题目
You are given two non-empty linked lists representing two non-negative integers. The most significant digit comes first and each of their nodes contain a single digit. Add the two numbers and return it as a linked list.

You may assume the two numbers do not contain any leading zero, except the number 0 itself.

Follow up:
What if you cannot modify the input lists? In other words, reversing the lists is not allowed.

Example:
```
Input: (7 -> 2 -> 4 -> 3) + (5 -> 6 -> 4)
Output: 7 -> 8 -> 0 -> 7
```

### 用`Stack`逻辑更清楚，代码更干净

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
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        Deque<Integer> stack1 = new LinkedList<>();
        Deque<Integer> stack2 = new LinkedList<>();
        ListNode c1 = l1, c2 = l2;
        while (c1 != null) { stack1.offerFirst(c1.val); c1 = c1.next; }
        while (c2 != null) { stack2.offerFirst(c2.val); c2 = c2.next; }
        int sum = 0, carry = 0;
        ListNode head = null;
        while (!stack1.isEmpty() || !stack2.isEmpty()) {
            sum = carry;
            if (!stack1.isEmpty()) {  sum += stack1.pollFirst(); }
            if (!stack2.isEmpty()) { sum += stack2.pollFirst(); }
            ListNode newHead = new ListNode(sum % 10);
            newHead.next = head;
            head = newHead;
            carry = sum / 10;
        }
        if (carry == 1) {
            ListNode newHead = new ListNode(1);
            newHead.next = head;
            head = newHead;
        }
        return head;
    }
}
```

#### 结果
![add-two-numbers-two-1](/images/leetcode/add-two-numbers-two-1.png)


### 用`Array`效率更高

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
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        if (l1.val == 0 && l2.val == 0) { return new ListNode(0); }
        int len1 = getLen(l1);
        int len2 = getLen(l2);
        boolean l1IsLonger = (len1 > len2)? true : false;
        int diff = l1IsLonger? (len1 - len2) : (len2 - len1);
        int[] res = l1IsLonger? new int[len1+1] : new int[len2+1];
        ListNode longer = l1IsLonger? l1 : l2;
        ListNode shorter = l1IsLonger? l2 : l1;
        int start = 1;
        while (diff > 0) {
            res[start++] = longer.val;
            longer = longer.next;
            --diff;
        }
        while (longer != null) {
            res[start] += longer.val;
            longer = longer.next;
            res[start++] += shorter.val;
            shorter = shorter.next;
        }
        for (int i = res.length-1; i > 0; i--) {
            if (res[i] > 9) {
                res[i] = res[i] % 10;
                res[i-1] += 1; // carry
            }
        }
        start = 0;
        while (res[start] == 0) { ++start; } // trim leading zeros
        ListNode r = new ListNode(0), c = r; // dummy
        for (int i = start; i < res.length; i++) {
            ListNode newNode = new ListNode(res[i]);
            c.next = newNode;
            c = c.next;
        }
        return r.next;
    }
    private int getLen(ListNode l1) {
        int count = 0;
        while (l1 != null) {
            ++count;
            l1 = l1.next;
        }
        return count;
    }
}
```

#### 结果
![add-two-numbers-two-2](/images/leetcode/add-two-numbers-two-2.png)
