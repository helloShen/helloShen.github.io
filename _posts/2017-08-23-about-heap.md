---
layout: post
title: "About Heap"
date: 2017-08-23 20:30:35
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["heap"]
description: >
---

### 为什么需要 **堆(Heap)**？
经常遇到的一种实际应用场景是：
> 我希望从一个数据结构中每次取出来的都是当前的最大元素，或者最小元素。至于这个数据结构内部的具体元素顺序，我不关心。

抽象出来的最简单的一组接口，只有 **入堆** `offer()`和 **出堆** `pop()`两个函数。
```java
interface Heap<T> {
    public void offer(T t);     // 往堆中添加元素
    public T pop();             // 从堆中取出最大元素
}
```


### 朴素的优先序列
首先 **堆** 的朴素实现法肯定不需要 **二叉堆(Binary Heap)** 这么麻烦。任意序列结构，`Array`， `ArrayList`，`LinkedList` 都可以实现。

具体的实现方法基本有两种，
1. 无序（惰性）：序列不需要排序，只有在`pop()`出堆的时候，才遍历元素，找出最大或最小元素。
2. 有序（积极）：在`offer()`里添加代码，随时保持序列有序。

无序（惰性）的方法工作量主要在`pop()`上，用户查询的时候它才去找。有序（积极）的工作量主要在`offer()`上，日常维护序列有序。

![trival-complexity](/images/about-heap/trival-complexity.png)

但这两种方法无论是用`Array`还是`ArrayList`或者`LinkedList`实现，都有一个方法的复杂度为 $$O(n)$$。无序的情况不必多说，遍历无序序列，必定$$O(n)$$。 但有序的情况，本来 **二分查找** 可以实现 $$O(\log_{}{n})$$ 找到插入位置（链表不能用二分查找，但`Array`和`ArrayList`可以）。但因为`Array`和`ArrayList`的`insert()`动作需要把所有元素都往后移，所以复杂度还是 $$O(n)$$。

### 二叉堆(Binary Heap)
相比前面的优先序列，二叉堆在`offer()`和`pop()`方法上都是 $$O(\log_{}{n})$$ 的复杂度。
![binary-heap-complexity](/images/about-heap/binary-heap-complexity.png)

> 二叉堆的本质是一棵 **堆有序(Total Order)** 的 **完全(Complete Binary Tree)** 二叉树。

![binary-heap](/images/about-heap/binary-heap.png)


这里 **堆有序** 是指：

> 当一棵二叉树的每个节点都大于等于它的两个子节点时，他被称为堆有序。

堆有序的特性就保证了二叉堆的 **根节点是最大元素**。更重要的是它还保证了后面的 **上浮** 和 **下沉** 函数在堆中加入了新元素以后，能在 $$O(\log_{}{n})$$ 时间内维护堆有序。

**完全** 二叉树是指，
> 一棵二叉树除了最外一层，其他层都是满的。而且最外层如果不是满的，缺少的也都是右侧的连续的节点。

完全二叉树，保证了二叉树可以很好地储存在一个连续数组中。假设某节点索引为`i`，则它的左子节点索引为`2*i+1`，右子节点为`2*i+2`。它的父节点为`(i-1)/2`。
![binary-heap-array](/images/about-heap/binary-heap-array.png)

如果我们跳过第一个桶位，从`heap[1]`开始储存元素，问题更加简化。假设某节点索引为`i`，则它的左子节点索引为`2*i`，右子节点为`2*i+1`。它的父节点为`i/2`。
![binary-heap-2](/images/about-heap/binary-heap-2.jpg)

### 二叉堆的实现
二叉堆无论在执行入堆`offer()`和出堆`pop()`动作之后都要维护 **堆有序**。

#### `offer()`和`pop()`
`offer()`函数，先将新元素插在堆尾部，然后执行`up()`上浮函数，让它一直上浮到它应该的位置。`pop()`函数，先删除根节点元素，然后将尾部的元素复制到根节点。然后在执行`down()`下沉函数，让它下沉到应该的位置。

```java
/* 插入新元素 */
public void offer(int n) {
    if (size == heap.length) { expand(); }  // arrange size of array
    heap[size++] = n;                       // add to the end
    up();                                   // matain the order
}

/* 返回堆中最大元素 */
public int pop() {
    if (size < 2) { return 0; }
    if (size == 2) { return heap[--size]; }
    int top = heap[1];
    heap[1] = heap[--size];     // delete root, move last element to the root bucket
    down();                    // maintain the order
    return top;
}

private void expand() {
    heap = Arrays.copyOf(heap,heap.length*2-1);
}
```

#### `up()`和`down()`
关键就是这个 **上浮(up)** 和 **下沉(down)** 函数。

`up()`上浮函数将目标元素和它的父节点元素比较，如果大于父节点，则将它和父节点交换（上浮）。一直重复这个动作，直到它成为根节点，或遇到一个比它大的父节点（上浮不动了）。
```java
/* 尾元素上游 */
private void up() {
    int pos = size-1;
    int val = heap[pos];
    while (pos > 1) { // root = 1
        int parent = pos / 2;
        int pv = heap[parent];
        if (pv > val) { break; }
        heap[pos] = pv;
        pos = parent;
    }
    heap[pos] = val;
}
```

`down()`下沉函数和它左右子节点中较大的一个比较，如果它小于子节点，将它和子节点交换。重复这个动作，直到它成为最底层叶节点，或者有两个比它小的子节点。
```java
/* 根元素下潜 */
private void down3() {
    int pos = 1;
    int val = heap[pos];
    while (pos * 2 < heap.length) {
        int next = pos * 2;
        int nv = heap[next];
        if (next + 1 < heap.length && heap[next+1] > heap[next]) { nv = heap[++next]; }
        if (nv <= val) { break; }
        heap[pos] = nv;
        pos = next;
    }
    heap[pos] = val;
}
```

`up()`和`down()`操作的步数，不会超过二叉堆的深度，所以是 $$O(\log_{}{n})$$ 复杂度。


#### 动画演示
下图演示的是`max-heap`的`pop()`函数的过程，
![max-heap-deletion](/images/about-heap/max-heap-deletion.gif)

下图是`min-heap`的`offer()`函数过程，
![min-heap](/images/about-heap/min-heap.gif)

### Java的`PriorityQueue`就是一个二叉堆
插入新元素`add()`方法的调用链：
> `add()`->`offer()`->`siftUp()`->`siftUpUsingComparator()`

```java
public boolean offer(E e) {
    if (e == null)
        throw new NullPointerException();
    modCount++;
    int i = size;
    if (i >= queue.length)
        grow(i + 1);
    size = i + 1;
    if (i == 0)
        queue[0] = e;
    else
        siftUp(i, e);
    return true;
}
```
`siftUpUsingComparator()`方法相当于`up()`函数。
```java
private void siftUpUsingComparator(int k, E x) {
    while (k > 0) {
        int parent = (k - 1) >>> 1; // 二叉树定位父节点
        Object e = queue[parent];
        if (comparator.compare(x, (E) e) >= 0)
            break;
        queue[k] = e;
        k = parent;
    }
    queue[k] = x;
}
```

`poll()`方法，相当于`pop()`方法。它的调用链：
> `poll()`->`siftDown()`->`siftDownUsingComparator()`

```java
public E poll() {
    if (size == 0)
        return null;
    int s = --size;
    modCount++;
    E result = (E) queue[0];
    E x = (E) queue[s];
    queue[s] = null;
    if (s != 0)
        siftDown(0, x);
    return result;
}
```
```java
private void siftDownUsingComparator(int k, E x) {
    int half = size >>> 1;
    while (k < half) {
        int child = (k << 1) + 1;
        Object c = queue[child];
        int right = child + 1;
        if (right < size &&
            comparator.compare((E) c, (E) queue[right]) > 0)
            c = queue[child = right];
        if (comparator.compare(x, (E) c) <= 0)
            break;
        queue[k] = c;
        k = child;
    }
    queue[k] = x;
}
```

从这两个方法可以看到，Java的`PriorityQueue`是一个标准的二叉堆。
