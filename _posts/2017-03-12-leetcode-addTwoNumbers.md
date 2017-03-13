---
layout: post
title: "Leetcode - Add Two Numbers"
date: 2017-03-12 00:15:42
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode"]
description: >
---

### 题目
You are given two non-empty linked lists representing two non-negative integers. The digits are stored in reverse order and each of their nodes contain a single digit. Add the two numbers and return it as a linked list.

You may assume the two numbers do not contain any leading zero, except the number 0 itself.

Input: (2 -> 4 -> 3) + (5 -> 6 -> 4)
Output: 7 -> 0 -> 8

### 朴素解法
短的那个数字，不足的位用`0`补足。
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
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode result = new ListNode(-1); // HEAD
        ListNode index = result;
        int carry = 0;
        int num1 = 0;
        int num2 = 0;
        while (true) {
            if (l1 == null && l2 == null) {
                if (carry == 1) {
                    ListNode next = new ListNode(carry);
                    index.next = next;
                }
                return result.next;
            }
            if (l1 != null) {
                num1 = l1.val;
                l1 = l1.next;
            } else {
                num1 = 0;
            }
            if (l2 != null) {
                num2 = l2.val;
                l2 = l2.next;
            } else {
                num2 = 0;
            }
            int sum = num1 + num2 + carry;
            index.next = new ListNode(sum % 10);
            index = index.next;
            carry = sum / 10;
        }
    }
}
```

#### 结果
![add-two-numbers-1.png](/images/leetcode/add-two-numbers-1.png)
