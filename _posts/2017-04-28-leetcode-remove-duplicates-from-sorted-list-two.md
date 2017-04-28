---
layout: post
title: "Leetcode - Algorithm - Remove Duplicates From Sorted List Two "
date: 2017-04-28 14:48:28
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: "medium"
description: >
---

### 题目
Given a sorted linked list, delete all nodes that have duplicate numbers, leaving only distinct numbers from the original list.

For example,
Given `1->2->3->3->4->4->5`, return `1->2->5`.
Given `1->1->1->2->3`, return `2->3`.

### 使用额外空间`Set`检查重复数字
这里是可以不使用额外空间的。写一个使用`Set`的版本，来和后面`in space`的版本比较。
* 用一个额外`Set`记录使用过的数字
* 用一个额外`Set`储存结果。

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
    public ListNode deleteDuplicates(ListNode head) {
        Set<Integer> numMemo = new HashSet<>();
        Set<Integer> vals = new LinkedHashSet<>();
        ListNode headCursor = head;
        while (headCursor != null) {
            int value = headCursor.val;
            if (numMemo.add(value)) {
                vals.add(value);
            } else {
                vals.remove(value);
            }
            headCursor = headCursor.next;
        }
        ListNode sentinel = new ListNode(0), senCursor = sentinel;
        for (int num : vals) {
            senCursor.next = new ListNode(num);
            senCursor = senCursor.next;
        }
        return sentinel.next;
    }
}
```

#### 结果
`10ms`最慢。
![remove-duplicates-from-sorted-list-two-1](/images/leetcode/remove-duplicates-from-sorted-list-two-1.png)


### 少用一组`Set`额外空间
直接取出数字比较，不用一组`Set`记录用过的数字。然后储存结果的数据结构换成`Deque`，用来模拟`Stack`的行为。

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
    public ListNode deleteDuplicates(ListNode head) {
        Deque<Integer> stack = new LinkedList<>();
        ListNode cursor = head, last = null, lastlast = null;
        while (cursor != null) {
            if (last == null || cursor.val != last.val) { // not duplicate
                stack.offerFirst(cursor.val); // insert last
            } else if (lastlast == null || (lastlast != null && cursor.val != lastlast.val)){ // first duplicate
                stack.pollFirst(); // remove last
            }
            lastlast = last; last = cursor; cursor = cursor.next;
        }
        ListNode sentinel = new ListNode(0), senCursor = sentinel;
        while (!stack.isEmpty()) {
            senCursor.next = new ListNode(stack.pollLast());
            senCursor = senCursor.next;
        }
        return sentinel.next;
    }
}
```

#### 结果
`4ms`。少用一个`Set`快了一倍。
![remove-duplicates-from-sorted-list-two-2](/images/leetcode/remove-duplicates-from-sorted-list-two-2.png)


### 完全不用额外空间，`In Space`
结果也不储存在额外的`Stack`里，而是直接操作`ListNode`的指针。因为`ListNode`只是个单向链表，一旦插入，删除比较麻烦，所以确定元素不重复之前都只设为`candidate`，确定不重复再加入结果。

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
    public ListNode deleteDuplicates(ListNode head) {
        ListNode sentinel = new ListNode(0), res = sentinel;
        ListNode cursor = head, last = null, lastlast = null;
        ListNode candidate = null;
        while (cursor != null) {
            if (last == null || cursor.val != last.val) { // not duplicate
                if (candidate != null) {
                    res.next = candidate;
                    res = res.next;
                }
                candidate = cursor; // set candidate
            } else if (lastlast == null || (lastlast != null && cursor.val != lastlast.val)){ // first duplicate
                candidate = null; // cancel candidate
            }
            lastlast = last; last = cursor; cursor = cursor.next;
        }
        if (candidate != null) {
            res.next = candidate;
            res = res.next;
        }
        res.next = null;
        return sentinel.next;
    }
}
```

#### 结果
`1ms`，不用额外空间，直接操作指针，又快了4倍。
![remove-duplicates-from-sorted-list-two-3](/images/leetcode/remove-duplicates-from-sorted-list-two-3.png)

### 单纯跳过重复元素，同样不用额外空间
更简单的抽象，如果发现像`[1,1,1,1,2]`重复数字，就一直跳过最后一个`1`，直接到`2`。这样的抽象更简洁，也更常用。一般去重都可以用这个做法。

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
    public ListNode deleteDuplicates(ListNode head) {
        ListNode sentinel = new ListNode(0), res = sentinel;
        sentinel.next = head;
        ListNode cur = head, pre = sentinel;
        while (cur != null) {
            while (cur.next != null && cur.next.val == cur.val) { // until last occurrence of that num
                cur = cur.next;
            }
            if (cur == pre.next) { // no duplicate
                res.next = cur; res = res.next;
            }
            pre = cur; cur = cur.next;
        }
        res.next = null;
        return sentinel.next;
    }
}
```
#### 结果
同样`1ms`。
![remove-duplicates-from-sorted-list-two-4](/images/leetcode/remove-duplicates-from-sorted-list-two-4.png)
