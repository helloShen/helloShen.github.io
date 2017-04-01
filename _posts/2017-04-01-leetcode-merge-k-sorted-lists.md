---
layout: post
title: "Leetcode - Algorithm - Merge K Sorted Lists "
date: 2017-04-01 13:06:47
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: ["hard"]
block: ["yes"]
description: >
---

### 题目
Merge k sorted linked lists and return it as one sorted list. Analyze and describe its complexity.

### 指针归并法 $$O(mn^2)$$
和`Merge Two Sorted List`一样，为每个List维护一个指针。每次查找指针指向的最小值，并且指针向前推进。复杂度$$O(mn^2)$$。`m`为List的平均长度，`n`为List的数量。就是典型的`One Pass`，每个元素都要遍历一遍，每次都要遍历`n`来找到最小值。

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
    public ListNode mergeKLists(ListNode[] lists) {
        ListNode sentinel = new ListNode(0), cursor = sentinel;
        while (true) {
            int flag = -1;
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < lists.length; i++) {
                ListNode current = lists[i];
                if (current != null && current.val < min) {
                    min = current.val;
                    flag = i;
                }
            }
            if (flag == -1) { break; } // the only exit point
            ListNode selected = lists[flag];
            cursor.next = lists[flag];
            lists[flag] = selected.next;
            cursor = cursor.next;
        }
        return sentinel.next;
    }
}
```

#### 结果
没通过。还有银弹没找到。
![merge-k-sorted-lists-1](/images/leetcode/merge-k-sorted-lists-1.png)


### 维护一个Binary Min Heap
初步有个思路，对于`Lists`里的当前元素，维护一个`Binary Min Heap`。保证能在`O(1)`的时间里找到下一个最小值，并花`O(\log_{n})`的时间维护这个`Binary Min Heap`。 因此总体复杂度是`mn\log_{n}`。

#### 代码
```java

```

#### 结果
![merge-k-sorted-lists-2](/images/leetcode/merge-k-sorted-lists-2.png)


### 解法3

#### 代码
```java

```

#### 结果
![merge-k-sorted-lists-3](/images/leetcode/merge-k-sorted-lists-3.png)
