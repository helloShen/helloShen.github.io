---
layout: post
title: "Leetcode - Algorithm - Reverse Linked List Two "
date: 2017-05-02 16:35:35
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "medium"
description: >
---

### 主要收获
记住一个结论：
> 链表的`in-place`反转是可行的。

以后遇到要翻转链表的时候，不用再纠结不使用额外空间是否可行。

### 题目
Reverse a linked list from position m to n. Do it in-place and in one-pass.

For example:
Given `1->2->3->4->5->NULL`, `m = 2` and `n = 4`,

return `1->4->3->2->5->NULL`.

Note:
Given `m`, `n` satisfy the following condition:
`1 ≤ m ≤ n ≤ length of list`.

### `in-place` and `in-one-pass`, 复杂度$$O(n)$$
直接操作链表指针来改变顺序。要用三个指针，标出三个重要的节点。
1. `wall`: 需要转动范围前的一个点，它是一个不动点，每次都插入在它后面。
2. `right`: 每次迭代向前插入的目标元素。
3. `left`: 目标元素`right`的前驱元素。

考虑`1->2->3->4->5->6->7->8->9`。假设要反转范围是`3-8`。这时候，`wall`,`right`,`left`三点分别初始化为：
```
               wall      right(from)          right(to)
                 |         |------------------>|
sentinel -> 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 9
                      |------------------>|
                    left(from)          left(to)
```
注意，开头加以个`sentinel`为了让`m = 1`的时候也能有个地方放`wall`。然后从`4`开始，每次都把`right`指向的元素插到`wall`的后面。

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
    public ListNode reverseBetween(ListNode head, int m, int n) {
        if (m == n) { return head; }
        ListNode sentinel = new ListNode(0), wall = sentinel;
        sentinel.next = head;
        for (int i = 1; i < m; i++) {
            wall = wall.next;
        } // 确保wall点是m点左边的第一个点。m为head时，wall等于开头的sentinel
        ListNode left = wall.next, right = left.next; // right为要向前插的点，left是它的前一点
        for (int i = m + 1; i <= n; i++) { // assert: n > m
            left.next = right.next; //-|
            right.next = wall.next; //-| >>> 这四步前插动作是本题的关键
            wall.next = right;      //-|
            right = left.next;      //-|
        }
        return sentinel.next;
    }
}
```

#### 结果
银弹！
![reverse-linked-list-two-1](/images/leetcode/reverse-linked-list-two-1.png)
