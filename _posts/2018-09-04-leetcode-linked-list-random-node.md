---
layout: post
title: "Leetcode - Algorithm - Linked List Random Node "
date: 2018-09-04 23:15:35
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list","recervoir sampling"]
level: "medium"
description: >
---

### 题目
Given a singly linked list, return a random node's value from the linked list. Each node must have the same probability of being chosen.

Follow up:
What if the linked list is extremely large and its length is unknown to you? Could you solve this efficiently without using extra space?

Example:
```
// Init a singly linked list [1,2,3].
ListNode head = new ListNode(1);
head.next = new ListNode(2);
head.next.next = new ListNode(3);
Solution solution = new Solution(head);

// getRandom() should return either 1, 2, or 3 randomly. Each element should have equal probability of returning.
solution.getRandom();
```

### 直观解：随机数组下标
所有数字读入内存中的数组，随机获得下标，返回对应下标的数字。因为每次随机获取数字是一个独立事件，假设链表的长度为`n`，则每个数字的概率是`1/n`。能这么做的前提是链表是定长的，而且我们有足够的内存储存整个链表的数字。

#### 代码
```java
class Solution {


    /** @param head The linked list's head.
        Note that the head is guaranteed to be not null, so it contains at least one node. */
    public Solution(ListNode head) {
        size = size(head);
        nums = collectNums(head, size);
    }

    /** Returns a random node's value. */
    public int getRandom() {
        return nums[R.nextInt(size)];
    }

    /**===================== 【private members】 ========================*/
    private int size;
    private int[] nums;
    private final Random R = new Random();
    private int size(ListNode head) {
        int count = 0;
        while (head != null) {
            ++count;
            head = head.next;
        }
        return count;
    }
    private static int[] collectNums(ListNode head, int size) {
        int[] numsArray = new int[size];
        int p = 0;
        while (head != null) {
            numsArray[p++] = head.val;
            head = head.next;
        }
        return numsArray;
    }   
}
```

#### 结果
![linked-list-random-node-1](/images/leetcode/linked-list-random-node-1.png)


### 标准“蓄水池算法”
如果链表不定长，或者太大无法完整放入内存。就需要著名的“蓄水池算法”。

因为不知道链表的长度`n`，就无法一开始就按照每个数字`1/n`的概率来获取。所以只能走一步看一步，
> 链表中第`i`个数字被选拔的概率为`1/i`
```
   1/1
    |   
    3,  5,  3,  6,  3,  7,  11,  31,  2,  8,  ...

   1/1 1/2
    |   |   
    3,  5,  3,  6,  3,  7,  11,  31,  2,  8,  ...

   1/1 1/2 1/3
    |   |   |   
    3,  5,  3,  6,  3,  7,  11,  31,  2,  8,  ...

   1/1 1/2 1/3 1/4
    |   |   |   |   
    3,  5,  3,  6,  3,  7,  11,  31,  2,  8,  ...

   1/1 1/2 1/3 1/4 1/5
    |   |   |   |   |   
    3,  5,  3,  6,  3,  7,  11,  31,  2,  8,  ...
```

关于蓄水池算法的细节，可以参考这篇文章：[**【蓄水池算法】**](http://www.ciaoshen.com/algorithm/2018/09/04/reservoir-sampling.html)

注意这不是本题的有效解，因为题目要求所有数字的获取概率相同。显然标准的蓄水池算法，越是开头的数字获取的概率越大。

#### 代码
```java
class Solution {

    /** @param head The linked list's head.
        Note that the head is guaranteed to be not null, so it contains at least one node. */
    public Solution(ListNode head) {
        HEAD = head;
        curr = head;
        size = 0;
        reachEnd = false;
    }

    /** Returns a random node's value. */
    public int getRandom() {
        int next = 0;
        while (true) {
            if (!reachEnd) {
                ++size;
            }
            next = curr.val;
            curr = curr.next;
            if (curr == null) {
                reachEnd = true;
                curr = HEAD;
            }
            if (R.nextInt(size) + 1 == size) {
                break;      
            }
        }
        return next;
    }


    private final ListNode HEAD;
    private ListNode curr;
    private int size;
    private boolean reachEnd;
    private final Random R = new Random();
}
```

### 适应本题的“蓄水池算法”变体
虽然还是采用直接在链表上跳跃的做法，但一开始就算出链表长度。一开始就平等对待每个数字，概率都是`1/n`。如果一个数字没有中标，就移至下一个数字继续尝试，直至有个数字中标。如果到达数组末尾，则重新回到链表的开头循环往复。
```
1/9 1/9 1/9 1/9 1/9 1/9 1/9  1/9  1/9
 |   |   |   |   |   |   |    |    |     
 3,  5,  3,  6,  3,  7,  11,  31,  2  
 ```

#### 代码
```java
/** @param head The linked list's head.
    Note that the head is guaranteed to be not null, so it contains at least one node. */
public Solution(ListNode head) {
    HEAD = head;
    curr = head;
    size = size(head);
}

/** Returns a random node's value. */
public int getRandom() {
    while (R.nextInt(size) + 1 != size) {
        curr = (curr.next == null)? HEAD : curr.next;
    }
    return curr.val;
}

/**======================= 【private member】 =======================*/
private final ListNode HEAD;
private ListNode curr;
private int size;
private final Random R = new Random();

private static int size(ListNode head) {
    int count = 0;
    while (head != null) {
        ++count;
        head = head.next;
    }
    return count;
}
```

#### 结果
![linked-list-random-node-2](/images/leetcode/linked-list-random-node-2.png)


### 继续改良蓄水池变体
上面解法还有个缺点是假设数字空间为`n`，每取一个数字，平均要取`n`次随机数。更好一点的办法是只取一次随机数，直接在链表上平移相应的位置。

#### 代码
```java
/** @param head The linked list's head.
    Note that the head is guaranteed to be not null, so it contains at least one node. */
public Solution(ListNode head) {
    HEAD = head;
    curr = head;
    size = size(head);
}

/** Returns a random node's value. */
public int getRandom() {
    int offset = R.nextInt(size);
    while (offset-- > 0) {
        curr = (curr.next == null)? HEAD : curr.next;
    }
    return curr.val;
}

/**======================= 【private member】 =======================*/
private final ListNode HEAD;
private ListNode curr;
private int size;
private final Random R = new Random();

private static int size(ListNode head) {
    int count = 0;
    while (head != null) {
        ++count;
        head = head.next;
    }
    return count;
}
```

#### 结果
![linked-list-random-node-3](/images/leetcode/linked-list-random-node-3.png)
