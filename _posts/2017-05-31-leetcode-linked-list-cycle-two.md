---
layout: post
title: "Leetcode - Algorithm - Linked List Cycle Two "
date: 2017-05-31 23:33:56
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list","two pointers"]
level: "medium"
description: >
---

### 题目
Given a linked list, return the node where the cycle begins. If there is no cycle, return null.

Note: Do not modify the linked list.

Follow up:
Can you solve it without using extra space?

### 先确保用`Map`肯定能解
每个路过的元素都存入`Map`，第一个遇到两次的节点就是`cycle`的起始节点。时间复杂度 $$O(n)$$，空间复杂度 $$O(n)$$。

#### 代码
```java
public class Solution {
    public ListNode detectCycle(ListNode head) {
        if (head == null) { return null; }
        Set<ListNode> memo = new HashSet<>();
        ListNode cur = head;
        while (cur != null) {
            if (memo.contains(cur)) { return cur; }
            memo.add(cur);
            cur = cur.next;
        }
        return null;
    }
}
```

#### 结果
![linked-list-cycle-two-1](/images/leetcode/linked-list-cycle-two-1.png)


### 不使用额外空间
受`Linked List Cycle`的启发，可以用一个`walker`指针，每次前进一格，一个`runner`指针，每次前移两格，先确定有没有cycle。

如果确定有cycle，再用两个指针，一个`cur`指针从`head`节点开始遍历，另一个`cycleCur`指针一直在cycle里绕圈。`cur`每前进一格，`cycleCur`都绕一圈，探测有咩有遇到`cur`。什么时候遇到了，`cur`就是我们要找的cycle的起始节点。

#### 代码
```java
public class Solution {
    public ListNode detectCycle(ListNode head) {
        if (head == null) { return null; }
        // find cycle
        ListNode mileStone = null;
        ListNode walker = head, runner = head;
        while (runner.next != null && runner.next.next != null) {
            runner = runner.next.next;
            walker = walker.next;
            if (runner == walker) {
                mileStone = runner; // find the cycle, milestone must be one of the node in cycle
                break;
            }
        }
        if (mileStone == null) { return null; } // do not have cycle
        ListNode cur = head, cycleCur = mileStone;
        while (true) { // assert: must have cycle
            do {
                if (cur == cycleCur) { return cur; }
                cycleCur = cycleCur.next;
            } while (cycleCur != mileStone);
            cur = cur.next;
        }
    }
}
```

#### 结果
结果不理想。离银弹差好远。
![linked-list-cycle-two-2](/images/leetcode/linked-list-cycle-two-2.png)


### 天才的数学计算法
这题的银弹比较神奇。需要一点数学上的洞察力。还是利用`walker`和`runner`指针，`walker`每轮前进一步，`runner`每轮前进两步。当他们相遇的时候，说明有cycle存在。这时候记下`walker`一共走了`k`步，`runner`就走了`2k`步。然后假设
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
    public ListNode detectCycle(ListNode head) {
        if (head == null) { return null; }
        // find cycle
        ListNode mileStone = null;
        ListNode walker = head, runner = head;
        int step = 0;
        while (runner.next != null && runner.next.next != null) {
            runner = runner.next.next;
            walker = walker.next;
            if (runner == walker) {
                mileStone = runner; // find the cycle, milestone must be one of the node in cycle
                break;
            }
            step++;
        }
        if (mileStone == null) { return null; } // do not have cycle
        ListNode cur = head, cycleCur = mileStone;
        while (cur != cycleCur) {
            cur = cur.next;
            cycleCur = cycleCur.next;
        }
        return cur;
    }
}
```

#### 结果
结果非常好。但这个方法能不能推广到某一类问题，还值得观察。
![linked-list-cycle-two-3](/images/leetcode/linked-list-cycle-two-3.png)
