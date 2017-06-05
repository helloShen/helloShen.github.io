---
layout: post
title: "Leetcode - Algorithm - Sort List "
date: 2017-06-04 20:38:30
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list","sort"]
level: "medium"
description: >
---

### 题目
Sort a linked list in O(n log n) time using constant space complexity.

### 分治法，递归版
典型的`分治-合并`排序法。

假设有`4-1-3-2`，
1. 分成`4`,`1`,`3`,`2`。
2. `4`,`1`合并成`1-4`。
3. `3`,`2`合并成`2-3`。
4. `1-4`,`2-3`合并成`1-2-3-4`。

关于复杂度，根据递归式，
> $$T(n) = T(2n/2) + n$$

适用于主定理 **`case 2`**：前半部分 $$n^{\log_{2}{2}} = n^1 = n$$ ， 和后半部分n同阶。所以 $$T(n)$$ 的复杂度等于每层的规模n 乘以递归深度 $$\lg{n}$$ ，等于 $$n\lg{n}$$ 。

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
    public ListNode sortList(ListNode head) {
        // base case: 长度为[0-1]
        if (head == null || head.next == null) { return head; }
        // slow停在下位中位点，是第二部分的开头
        // pre是slow的前一个节点，是第一部分的结尾
        ListNode slow = head, fast = head, pre = null;
        while (fast != null && fast.next != null) {
            pre = slow;
            slow = slow.next;
            fast = fast.next.next;
        }
        pre.next = null;
        ListNode left = sortList(head);
        ListNode right = sortList(slow);
        return merge(left,right);
    }
    public ListNode merge(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0), cur = dummy, cur1 = list1, cur2 = list2;
        while (cur1 != null && cur2 != null) {
            if (cur1.val <= cur2.val) {
                cur.next = cur1;
                cur1 = cur1.next;
            } else {
                cur.next = cur2;
                cur2 = cur2.next;
            }
            cur = cur.next;
        }
        if (cur1 != null) { cur.next = cur1; }
        if (cur2 != null) { cur.next = cur2; }
        return dummy.next;
    }
}
```

#### 结果
银弹！
![sort-list-1](/images/leetcode/sort-list-1.png)
