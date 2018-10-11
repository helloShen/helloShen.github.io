---
layout: post
title: "Leetcode - Algorithm - Max Stack "
date: 2018-10-10 23:32:06
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["design", "stack", "tree map", "linked list"]
level: "hard"
description: >
---

### 题目
Design a max stack that supports push, pop, top, peekMax and popMax.

* `push(x)` -- Push element x onto stack.
* `pop()` -- Remove the element on top of the stack and return it.
* `top()` -- Get the element on the top.
* `peekMax()` -- Retrieve the maximum element in the stack.
* `popMax()` -- Retrieve the maximum element in the stack, and remove it. If you find more than one maximum elements, only remove the top-most one.

Example 1:
```
MaxStack stack = new MaxStack();
stack.push(5);
stack.push(1);
stack.push(5);
stack.top(); -> 5
stack.popMax(); -> 5
stack.top(); -> 1
stack.peekMax(); -> 5
stack.pop(); -> 1
stack.top(); -> 5
```

Note:
* -1e7 <= x <= 1e7
* Number of operations won't exceed 10000.
* The last four operations won't be called when stack is empty.

### 分析
这题主要考察的是数据结构。虽然最终结论很简单，但过程中需要考虑各种数据结构以及他们之间任意的组合。我首先考虑到的两种候选数据结构都被排除了。

说到取最大值，肯定先想到`Max Heap`。但问题是`Max Heap`内部作为一个“黑箱”无法对特定元素进行查找，并且删除。

然后是“二叉搜索树”。二叉搜索树的“插入”，“删除”元素的操作都是`O(logN)`，所以我曾经对它抱有幻想。但这题可能存在重复元素，而且是大量的重复元素。考虑最坏情况：所有元素值都相等，二叉树的复杂度也回归到`O(N)`，那就完全失去了意义。

### 老老实实用`LinkedList`
这题保底的暴力解法复杂度也不过是`O(N)`。只要老老实实遍历整个数字集合总能找到最大值。而且这种最朴素的做法效率和最佳策略相差无几。

#### 代码
```java
class MaxStack {

    LinkedList<Pair> list;

    public MaxStack() {
        list = new LinkedList<Pair>();
    }

    public void push(int x) {
        list.add(new Pair(x));
    }

    public int pop() {
        return list.removeLast().val;
    }

    public int top() {
        return list.getLast().val;
    }

    public int peekMax() {
        if (list.isEmpty()) return 0;
        int max = list.getFirst().val;
        for (Pair p : list) {
            max = Math.max(max, p.val);
        }
        return max;
    }

    public int popMax() {
        if (list.isEmpty()) return 0;
        Pair max = list.getFirst();
        for (Pair p : list) {
            if (p.val >= max.val) max = p;
        }
        list.remove(max);
        return max.val;
    }

    private int id;
    private class Pair {
        int idx;
        int val;
        private Pair(int x) {
            idx = id++;
            val = x;
        }
    }

}
```

#### 结果
![max-stack-1](/images/leetcode/max-stack-1.png)


### `Double Linked List`
由于用到的链表操作都不复杂，可以考虑写一个功能简单的“双向链表”。本来希望能稍微快一点，但两者实际表现一致。

#### 代码
```java
class MaxStack {

    private DoubleLinkedListNode dummy;
    private DoubleLinkedListNode curr;

    /** initialize your data structure here. */
    public MaxStack() {
        dummy = new DoubleLinkedListNode(Integer.MIN_VALUE);
        curr = dummy;
    }

    public void push(int x) {
        DoubleLinkedListNode newNode = new DoubleLinkedListNode(x);
        newNode.prev = curr;
        curr.next = newNode;
        curr = newNode;
    }

    public int pop() {
        int res = curr.val;
        curr = curr.prev;
        curr.next = null;
        return res;
    }

    public int top() {
        return curr.val;
    }

    public int peekMax() {
        int max = curr.val;
        DoubleLinkedListNode ite = curr.prev;
        while (ite != dummy) {
            max = Math.max(max, ite.val);
            ite = ite.prev;
        }
        return max;
    }

    public int popMax() {
        DoubleLinkedListNode max = curr;
        DoubleLinkedListNode ite = curr.prev;
        while (ite != dummy) {
            if (ite.val > max.val) max = ite;
            ite = ite.prev;
        }
        max.prev.next = max.next;
        if (curr == max) {
            curr = curr.prev;
        } else {
            max.next.prev = max.prev;
        }
        return max.val;
    }

    private class DoubleLinkedListNode {
        private int val;
        private DoubleLinkedListNode prev, next;
        private DoubleLinkedListNode(int x) {
            val = x;
        }
    }

}
```

#### 结果
![max-stack-2](/images/leetcode/max-stack-2.png)


### 用2个`Stack`
暴力搜索最大值需要遍历整个链表。这个方案的动机是希望能不能不遍历整个链表就找到最大值。解决方法就是利用一个额外的`Stack`，专门记录当前最大值。比如考虑数组`[2, 1, 5, 3, 9]`，
```
num stack = [2, 1, 5, 3, 9]
max stack = [2, 2, 5, 5, 9]  <- 每次插入新元素都记录当前全局最大值
```
这样做的好处是，当我知道了当前最大值为`9`，我只需要移除找到第一个`9`即可，不需要再遍历余下的数字。
```
                已知当前最大值为"9"，只需要移除第一个"9"。余下的数字可以不用遍历。
                         |
num stack = [2, 1, 5, 3, 9]
max stack = [2, 2, 5, 5, 9]
```

#### 代码
```java
class MaxStack {

    private LinkedList<Integer> numStack;
    private LinkedList<Integer> maxStack;

    public MaxStack() {
        numStack = new LinkedList<Integer>();
        maxStack = new LinkedList<Integer>();
    }

    public void push(int x) {
        numStack.push(x);
        maxStack.push((maxStack.isEmpty())? x : Math.max(maxStack.peek(), x));
    }

    public int pop() {
        maxStack.pop();
        return numStack.pop();
    }

    public int top() {
        return numStack.peek();
    }

    public int peekMax() {
        return maxStack.peek();
    }
    public int popMax() {
        int max = maxStack.peek();
        LinkedList<Integer> temp = new LinkedList<>();
        int num = 0;
        while ((num = numStack.pop()) != max) {
            temp.push(num);
            maxStack.pop();
        }
        maxStack.pop();
        while (!temp.isEmpty()) {
            push(temp.pop());
        }
        return max;
    }

}
```

#### 结果
![max-stack-3](/images/leetcode/max-stack-3.png)
