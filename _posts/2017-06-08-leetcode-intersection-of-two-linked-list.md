---
layout: post
title: "Leetcode - Algorithm - Intersection of Two Linked List "
date: 2017-06-08 17:46:20
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "easy"
description: >
---

### 题目
Write a program to find the node at which the intersection of two singly linked lists begins.


For example, the following two linked lists:
```
A:          a1 → a2
                   ↘
                     c1 → c2 → c3
                   ↗            
B:     b1 → b2 → b3
```
begin to intersect at node c1.

Notes:

If the two linked lists have no intersection at all, return null.
The linked lists must retain their original structure after the function returns.
You may assume there are no cycles anywhere in the entire linked structure.
Your code should preferably run in O(n) time and use only O(1) memory.
Credits:
Special thanks to @stellari for adding this problem and creating all test cases.

### 用`HashSet`记录`listA`的所有节点
* 时间复杂度: $$O(n)$$
* 空间复杂度: $$O(n)$$

#### 代码
```java
public class Solution {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) { return null; }
        Set<ListNode> memo = new HashSet<>();
        ListNode cur = headA;
        while (cur != null) {
            memo.add(cur);
            cur = cur.next;
        }
        cur = headB;
        while (cur != null) {
            if (memo.contains(cur)) { return cur; }
            cur = cur.next;
        }
        return null;
    }
}
```

#### 结果
![intersection-of-two-linked-list-1](/images/leetcode/intersection-of-two-linked-list-1.png)

### 计算`listA`和`listB`的长度
比如`listA`长度为`2`，`listB`长度为`3`。计算出长度差之后，让`curB`先跑到`b2`位置，和`a1`站在同一起跑线再出发。
```
A:          a1 → a2
                   ↘
                     c1 → c2 → c3
                   ↗            
B:     b1 → b2 → b3
```

#### 代码
```java
public class Solution {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) { return null; }
        int sizeA = size(headA);
        int sizeB = size(headB);
        ListNode curA = headA, curB = headB;
        // curA,curB归同一起跑线
        while (sizeA > sizeB) {
            curA = curA.next;
            sizeA--;
        }
        while (sizeB > sizeA) {
            curB = curB.next;
            sizeB--;
        }
        while (curA != curB && curA != null) {
            curA = curA.next;
            curB = curB.next;
        }
        return (curA == null)? null : curA;
    }
    public int size(ListNode list) {
        ListNode cur = list;
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
![intersection-of-two-linked-list-3](/images/leetcode/intersection-of-two-linked-list-3.png)

#### 有个小诀窍，可以不计算长度
`listA`先跑到`c3`结尾处，跳转到`listB`的开头`b1`接着跑。`listB`后跑到`c3`结尾处，跳转到`listA`的开头`a1`接着跑。如果`listA`和`listB`相交，则会在第二圈同时到达`c1`。如果不想交，会同时跑到`null`。
```
A:          a1 → a2
                   ↘
                     c1 → c2 → c3
                   ↗            
B:     b1 → b2 → b3
```

这里面的数学本质就是：
> 两个指针`curA`和`curB`都跑了`lengthA + lengthB`。

#### 代码
```java
```
#### 结果
![intersection-of-two-linked-list-4](/images/leetcode/intersection-of-two-linked-list-4.png)


### `Walker`和`Runner`的追逐算法
因为已经有了`Linked List Cycle Two`这个问题的最后一种天才的`Runner`追`Walker`的解法，我们可以在 $$O(n)$$ 时间复杂度，以及 $$O(1)$$ 空间复杂度的情况下，轻松找到`Cycle`开始的那个节点。所以只需要把`listA`或者`listB`其中一个首尾相接成一个cycle，问题就转变成`Linked List Cycle Two`问题。

最后找到拼接的头节点之后，再把造出来的cycle复原即可。

#### `Walker`和`Runner`追逐算法细节如下

利用`walker`和`runner`指针，`walker`每轮前进一步，`runner`每轮前进两步。当他们相遇的时候，说明有cycle存在。这时候记下`walker`一共走了`k`步，`runner`就走了`2k`步。然后假设
* 从起始节点到cycle的首个节点之间的距离是`s`步。
* 从cycle的首个节点到`walker`和`runner`相遇的节点之间的距离为`m`步。
* cycle的周长为`r`步。

这时候我们就能得到两个等式，
> 2k - k = nr       (1式)（runner在遇到walker之前，已经绕着cycle跑了n圈了）

> k = s + m         (2式)（就是walker从起始点跑到和runner相遇的地方的距离，可以肯定walker没有跑完一圈）

上面的两个等式`(1)`和`(2)`能推出下面这个等式`(3)`，
> s = nr - m        (3式)

这个等式就是说：**一个指针`cur`从list起始点出发，另一个指针`cycleCur`从`runner`和`walker`相遇点出发，两者每轮都前进一步，最后一定在cycle的首个节点相遇。**

因为假设`n=1`的情况，`s=r-m`，就是说`cycleCur`只要继续跑完这一圈，回到cycle首节点，`cur`也正好到cycle首节点。如果`n=2`，就是`cycleCur`整整多跑一圈，但`cur`和`cycleCur`还是在cycle首节点相遇。事实上无论`n=3,4,5,6,...`，`cur`和`cycleCur`总是在cycle首节点相遇，区别只是`cycleCur`多跑几圈而已。


#### 代码
```java
public class Solution {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) { return null; }
        ListNode end = headA;
        while (end != null && end.next != null) { // cur stop at the last node
            end = end.next;
        }
        end.next = headA; // make a circle
        ListNode walker = headB, runner = headB;
        while (runner != null && runner.next != null) {
            walker = walker.next;
            runner = runner.next.next;
            if (runner == walker) { // find circle
                ListNode anotherWalker = headB;
                while (anotherWalker != walker) { // BLACK MAGIC
                    anotherWalker = anotherWalker.next;
                    walker = walker.next;
                }
                end.next = null;
                return walker;
            }
        }
        end.next = null;
        return null;
    }
}
```

#### 结果
银弹！
![intersection-of-two-linked-list-2](/images/leetcode/intersection-of-two-linked-list-2.png)
