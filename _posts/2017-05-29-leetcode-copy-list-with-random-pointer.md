---
layout: post
title: "Leetcode - Algorithm - Copy List With Random Pointer "
date: 2017-05-29 21:39:21
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list","hash table"]
level: "medium"
description: >
---

### 题目
A linked list is given such that each node contains an additional random pointer which could point to any node in the list or null.
```
class RandomListNode {
    int label;
    RandomListNode next, random;
    RandomListNode(int x) { this.label = x; }
};
```
Return a deep copy of the list.

**！注意**：前提条件式每个节点的`label`值是唯一的。

### 使用一个额外的`Map`存放所有节点。时间复杂度 $$O(n)$$，额外空间 $$O(n)$$
先把`next`骨干复制好，把所有新节点存在`Map`里。等所有的节点也都有了，再补完`random`链接。比如，
```
Next Chain = 1->2->3->4
Random Chain = [ [1->3], [2->4], [3->1], [4->1] ]
```

#### 代码
```java
/**
 * Definition for singly-linked list with a random pointer.
 * class RandomListNode {
 *     int label;
 *     RandomListNode next, random;
 *     RandomListNode(int x) { this.label = x; }
 * };
 */
public class Solution {
    public RandomListNode copyRandomList(RandomListNode head) {
        RandomListNode dummy = new RandomListNode(0);
        Map<Integer,RandomListNode> memo = new HashMap<>();
        RandomListNode cur = head;
        RandomListNode copyCur = dummy;
        while (cur != null) { // copy next chain
            RandomListNode copyNode = new RandomListNode(cur.label);
            memo.put(cur.label,copyNode);
            copyCur.next = copyNode;
            cur = cur.next;
            copyCur = copyCur.next;
        }
        cur = head;
        copyCur = dummy.next;
        while (cur != null) { // copy random chain
            if (cur.random != null) {
                copyCur.random = memo.get(cur.random.label);
            }
            cur = cur.next;
            copyCur = copyCur.next;
        }
        return dummy.next;
    }
}
```

#### 结果
![copy-list-with-random-pointer-1](/images/leetcode/copy-list-with-random-pointer-1.png)


### 不使用额外空间
把每个复制的新节点紧跟着插在原版节点的后面，这样`O(1)`就能找到每个节点对应的复制节点，以及它的`next`和`random`。

一供遍历三遍整个`list`，比如`1->2->3->4`，
1. 拷贝所有节点，并插在原节点后面，变成`1->1->2->2->3->3->4->4`。
2. 第二次遍历，补全所有`random`引用。
3. 第三次遍历，把`original list`和`copy list`剥离成两个独立的链表。

#### 代码
```java
public class Solution {
    public RandomListNode copyRandomList(RandomListNode head) {
        RandomListNode cur = head, dummy = new RandomListNode(0), copyCur = dummy;
        while (cur != null) { // copy next chain, and insert new node right after the original node
            RandomListNode newNode = new RandomListNode(cur.label);
            newNode.next = cur.next;
            cur.next = newNode;
            copyCur.next = newNode;
            copyCur = newNode.next;
            cur = newNode.next;
        }
        cur = head;
        while (cur != null) { // 拷贝random
            copyCur = cur.next;
            if (cur.random != null) {
                copyCur.random = cur.random.next; // 拷贝random指针
            }
            cur = copyCur.next;
        }
        cur = head;
        while (cur != null) { // 从original list上剥离copy list
            copyCur = cur.next;
            cur.next = copyCur.next;
            if (cur.next != null) {
                copyCur.next = cur.next.next;
            } else {
                copyCur.next = null;
            }
            cur = cur.next;
        }
        return dummy.next;
    }
}
```

#### 结果
银弹！少有的不用空间换时间的。不使用额外空间的方法，反而比使用`Map`的更快。
![copy-list-with-random-pointer-2](/images/leetcode/copy-list-with-random-pointer-2.png)
