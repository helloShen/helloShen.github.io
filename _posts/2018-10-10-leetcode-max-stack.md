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
这题考察的是数据结构。主要是对链表以及二叉树的掌握和活用。尤其能让我们对二叉树的一种衍生结构`TreeMap`的作用有很直观的理解。

### 朴素解法：用`LinkedList`模拟`Stack`的行为，`O(N)`
`Stack`本质上就是一串有序数列，可以简单地用`Array`或者`List`实现。只需要维护一个指向尾元素的指针，移动这个指针，就可以模拟最基本的`push()`和`pop()`动作。
```
                           p: 移动指针模拟压栈和出栈
                        <- | ->
int[] nums -> [2, 1, 9, 3, 5]
```
就算是搜索最大元素`popMax()`，暴力遍历整个数列保底的复杂度是`O(N)`，`N`代表数组中数字的个数。

一个小细节是：数组不擅长从中间删除元素（也包括基于数组的`ArrayList`），因为比如删除中间最大的`9`，需要顺位平移所有的后续元素`[3, 5]`。
```
int[] nums -> [2, 1, 9, 3, 5]
                     | <- 删除'9'
              [2, 1, 3, 5]
```

所以擅长以`O(1)`从中间删除元素的`LinkedList`就是这题最朴素的选择。简单又能解决问题。

```java
class MaxStack {

    LinkedList<Integer> list;

    public MaxStack() {
        list = new LinkedList<Integer>();
    }

    public void push(int x) {
        list.add(x);
    }

    public int pop() {
        return list.removeLast();
    }

    public int top() {
        return list.getLast();
    }

    public int peekMax() {
        int max = Integer.MIN_VALUE;
        for (int n : list) {
            max = Math.max(max, n);
        }
        return max;
    }

    public int popMax() {
        int max = peekMax();
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i) == max) {
                list.remove(i);
                break;
            }
        }
        return max;
    }

}
```

而且效果还不错，
![max-stack-1](/images/leetcode/max-stack-1.png)


### 自己实现`Double Linked List`
如果用到的链表操作不是很复杂，自己写一个功能简单的链表类，可以稍微优化一下效率。因为`Stack`经常回退，这里需要一个“双向链表”。

#### 代码
```java
class MaxStack {

    private DoubleLinkedNode dummy;
    private DoubleLinkedNode curr;

    /** initialize your data structure here. */
    public MaxStack() {
        dummy = new DoubleLinkedNode(Integer.MIN_VALUE);
        curr = dummy;
    }

    public void push(int x) {
        DoubleLinkedNode newNode = new DoubleLinkedNode(x);
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
        DoubleLinkedNode ite = curr.prev;
        while (ite != dummy) {
            max = Math.max(max, ite.val);
            ite = ite.prev;
        }
        return max;
    }

    public int popMax() {
        DoubleLinkedNode max = curr;
        DoubleLinkedNode ite = curr.prev;
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

    private class DoubleLinkedNode {
        private int val;
        private DoubleLinkedNode prev, next;
        private DoubleLinkedNode(int x) {
            val = x;
        }
    }

}
```

#### 结果
![max-stack-2](/images/leetcode/max-stack-2.png)


### 用2个`Stack`，复杂度`O(N)`
链表方案最大的开销在于暴力搜索最大值需要遍历整个链表。优化的一个方向就是看能不能不遍历整个链表就找到最大值。一个可行的办法就是利用一个额外的`Stack`，专门记录当前最大值。还是考虑数组`[2, 1, 9, 3, 5]`，
```
num stack = [2, 1, 9, 3, 5]
max stack = [2, 2, 9, 9, 9]  <- 每次插入新元素都记录当前全局最大值
```
这样做的好处是，当我知道了当前最大值为`9`，我只需要移除找到第一个`9`即可，不需要再遍历余下的数字。
```
        已知当前最大值为"9"，只需要移除第一个"9"。余下的数字可以不用遍历。
                   |
num stack = [2, 1, 9        弹出元素 -> 3, 5]
max stack = [2, 2, 9
```
移除`9`之后，只需要把之前弹出元素`[3,5]`重新压入栈，同时更新`max stack`即可，
```
num stack = [2, 1, 3, 5]
max stack = [2, 2, 3, 5]
                      |
                当前最大值为'5'
```

具体代码如下，
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

虽然比链表要快，但最坏情况下，比如原生数组以降序排列`[5, 4, 3, 2, 1]`，每次也必须遍历到最后一个元素才能找到最大值。因此`popMax()`复杂度还是`O(N)`。
![max-stack-3](/images/leetcode/max-stack-3.png)


### `DoubleLinkedList` + `TreeMap`，复杂度`O(logN)`
优化的方向还是在`popMax()`上。能不能把搜索最大元素优化到`O(logN)`？看上去很有希望。取最大值首先会想到`PriorityQueue`，它本质上是一个`Max Heap`。但它的问题在于它只保证下一个取出的是最大元素，其余内部是一个“黑箱”，无法查找或删除特定元素。当`pop()`函数需要删除一个非最大元素时，`PriorityQueue`就比较尴尬。

但这提供了一个思路， **“二叉搜索树（Binary Search Tree）”** 可以在`O(logN)`时间里完成`insert()`,`remove()`,`search()`操作。

但二叉搜索树有2个常见的问题，一个就是数列中不能有重复元素。另一个就是二叉树深度的平衡问题，如果原生数组以单纯的升序或者降序顺序出现，二叉树等同于一个单侧倾斜的链表，效率退化到`O(N)`。
```
                3
               /
              2
             /
            1
```

这时候，我们需要的是`TreeMap`，它的键值本质上是一个 **“红黑树”**，每次插入新元素，会进行自平衡旋转，保证二叉搜索树的深度大致上是平衡的。如果用一个`List<Node>`作为键-值对中的“值”，可以将所有大小相等的链表节点都收拢到一起。实际用到的结构是`TreeMap<Integer, LinkedList<Node>>`。考虑有重复元素的一串数字`[5, 1, 5, 3, 3, 7]`，键值是以二叉搜索树的形式保存。每个键值映射到一系列具有对应值的节点。
```
[5, 1, 5, 3, 3, 7]

            5->[5,5]
          /   \
         /     \
      1->[1]   7->[7]
          \
           \
         3->[3,3]
```

二叉树在`O(logN)`时间内拿到最大值节点的引用之后，我们还需要一个 **“双向链表（Double Linked List）”** 在`O(1)`的时间内删除此节点。具体代码如下，

```java
class MaxStack {

    private DoubleLinkedList list;
    private TreeMap<Integer, List<DoubleLinkedNode>> map;

    /** initialize your data structure here. */
    public MaxStack() {
        list = new DoubleLinkedList();
        map = new TreeMap<Integer, List<DoubleLinkedNode>>();
    }

    public void push(int x) {
        DoubleLinkedNode newNode = new DoubleLinkedNode(x);
        list.add(newNode);
        List<DoubleLinkedNode> sameValList = map.get(x);
        if (sameValList == null) {
            sameValList = new LinkedList<DoubleLinkedNode>();
        }
        sameValList.add(newNode);
        map.put(x, sameValList);
    }

    public int pop() {
        if (list.isEmpty()) return 0;
        DoubleLinkedNode last = list.removeLast();
        List<DoubleLinkedNode> sameValList = map.get(last.val);
        sameValList.remove(sameValList.size() - 1);
        if (sameValList.isEmpty()) map.remove(last.val);
        return last.val;
    }

    public int top() {
        if (list.isEmpty()) return 0;
        return list.getLast().val;
    }

    public int peekMax() {
        if (list.isEmpty()) return 0;
        return map.lastKey();
    }

    public int popMax() {
        if (list.isEmpty()) return 0;
        int max = peekMax();
        List<DoubleLinkedNode> sameValList = map.get(max);
        DoubleLinkedNode removed = sameValList.remove(sameValList.size() - 1);
        if (sameValList.isEmpty()) map.remove(max);
        list.remove(removed);
        return max;
    }

    private class DoubleLinkedNode {
        private int val;
        private DoubleLinkedNode prev, next;
        private DoubleLinkedNode(int x) {
            val = x;
        }
    }
    private class DoubleLinkedList {
        // 2 sentinals to simplify linked list operations
        private DoubleLinkedNode head;
        private DoubleLinkedNode tail;

        private DoubleLinkedList() {
            head = new DoubleLinkedNode(0);
            tail = new DoubleLinkedNode(0);
            head.next = tail;
            tail.prev = head;
        }

        private boolean isEmpty() {
            return head.next == tail;
        }
        private void add(DoubleLinkedNode node) {
            tail.prev.next = node;
            node.next = tail;
            node.prev = tail.prev;
            tail.prev = node;
        }

        private DoubleLinkedNode getLast() {
            if (isEmpty()) return null;
            return tail.prev;
        }

        private DoubleLinkedNode removeLast() {
            if (isEmpty()) return null;
            return remove(tail.prev);
        }

        private DoubleLinkedNode remove(DoubleLinkedNode node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            return node;
        }
    }

}
```

效果和2个`Stack`的解法差不多。如果测试用例规模更大一些，复杂度上的优势就会显现出来。
![max-stack-4](/images/leetcode/max-stack-4.png)
