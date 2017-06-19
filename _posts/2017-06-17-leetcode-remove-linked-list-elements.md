---
layout: post
title: "Leetcode - Algorithm - Remove Linked List Elements "
date: 2017-06-17 22:47:29
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "easy"
description: >
---

### 题目
Remove all elements from a linked list of integers that have value val.

Example
```
Given: 1 --> 2 --> 6 --> 3 --> 4 --> 5 --> 6, val = 6
Return: 1 --> 2 --> 3 --> 4 --> 5
```

### 总体思路
这题没什么花样，就是老老实实一个个遍历检测。时间复杂度 $$O(n)$$，和空间复杂度 $$O(1)$$ 都已经是极限了，因为list没有排过序，$$O(\log_{}{n})$$ 是不可能的。临时排序 $$O(n\log_{}{n})$$ 得不偿失了。

### 挨个遍历链表

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
    public ListNode removeElements(ListNode head, int val) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode pre = dummy;
        while (pre != null && pre.next != null) {
            if (pre.next.val == val) {
                pre.next = pre.next.next;
            } else {
                pre = pre.next;
            }
        }
        return dummy.next;
    }
}
```

#### 结果
![remove-linked-list-elements-1](/images/leetcode/remove-linked-list-elements-1.png)
