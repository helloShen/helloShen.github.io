---
layout: post
title: "Leetcode - Algorithm - Odd Even Linked List "
date: 2017-08-07 18:47:31
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "medium"
description: >
---

### 题目
Given a singly linked list, group all odd nodes together followed by the even nodes. Please note here we are talking about the node number and not the value in the nodes.

You should try to do it in place. The program should run in O(1) space complexity and O(nodes) time complexity.

Example:
```
Given 1->2->3->4->5->NULL,
return 1->3->5->2->4->NULL.
```

Note:
The relative order inside both the even and odd groups should remain as it was in the input.
The first node is considered odd, the second node even and so on ...


### 直接操作指针
继续贯彻面向断言编程。这次必须维护的两个指针变成了：
* odd: 指向最后一个奇数位元素。
* even: 指向最后一个偶数位元素。

麻烦的是一些边角情况，比如`odd`或`even`指针指向的元素为空。一种做法是把长度小于2的情况单独拿出来讨论。剩下的情况，指针都不为空。

另一种方法是提前设置两个 **哨兵**。预先把`odd`和`even`指针初始化在两个哨兵上。最后再想办法去掉。

无论用哪一种方法，只要初始化的时候确保上面两个指针的断言为真，并且在后续操作中始终维护`odd`和`even`指针，编程的过程就既安全有简单，没有特殊情况，完全不会出错。

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
        public ListNode oddEvenList(ListNode head) {
            ListNode odd = new ListNode(0); // dummy odd
            ListNode even = new ListNode(0); // dummy even
            odd.next = even;
            even.next = head;
            ListNode cur = head;
            while (cur != null) {
                // find next odd
                ListNode nextOdd = cur.next;
                if (nextOdd != null) { nextOdd = nextOdd.next; }
                // insert curr odd
                even.next = cur.next;
                even = cur.next;
                cur.next = odd.next;
                odd.next = cur;
                odd = cur;
                // pass to next odd
                cur = nextOdd;
            }
            odd.next = odd.next.next; // skip dummy even
            return head;
        }
}
```

#### 结果
![odd-even-linked-list-1](/images/leetcode/odd-even-linked-list-1.png)


### 更优雅的解法
上面的解法是遇到链表问题比较万能的方法。但这第二种解法直击这题的本质。非常的简洁。

![odd-even-linked-list](/images/leetcode/odd-even-linked-list.png)

如上图所示，修改之后的链表看上去应该是这个样子。所以只需要做一件事：**无论是奇数还是偶数节点，都把指针指针跳过下一个节点，指向下下个节点。**

然后最后不要忘了把奇数的最后一个元素的`next`指针指向第一个偶数元素。而且无论最后一个偶数元素是在最后一个奇数元素之前(图中的`6`号元素)，还是之后（图中的`8`号元素），都不影响这个操作（`7`指向`2`）。

所以，能有一个万能的解法，很重要。但如果能弄明白问题的本质，有一个优雅的代码，当然也很好。

#### 代码
```java
public class Solution {
    public ListNode oddEvenList(ListNode head) {
        if (head == null) { return null; }
        ListNode odd = head, even = head.next, firstEven = even;
        while (even != null && even.next != null) {
            odd.next = odd.next.next;
            even.next = even.next.next;
            odd = odd.next;
            even = even.next;
        }
        odd.next = firstEven;
        return head;
    }
}
```

#### 结果
![odd-even-linked-list-2](/images/leetcode/odd-even-linked-list-2.png)
