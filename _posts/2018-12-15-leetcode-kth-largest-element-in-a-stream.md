---
layout: post
title: "Leetcode - Algorithm - Kth Largest Element In A Stream "
date: 2018-12-15 23:17:22
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["priority queue", "list"]
level: "easy"
description: >
---

### 题目
Design a class to find the kth largest element in a stream. Note that it is the kth largest element in the sorted order, not the kth distinct element.

Your KthLargest class will have a constructor which accepts an integer k and an integer array nums, which contains initial elements from the stream. For each call to the method KthLargest.add, return the element representing the kth largest element in the stream.

Example:
```
int k = 3;
int[] arr = [4,5,8,2];
KthLargest kthLargest = new KthLargest(3, arr);
kthLargest.add(3);   // returns 4
kthLargest.add(5);   // returns 5
kthLargest.add(10);  // returns 5
kthLargest.add(9);   // returns 8
kthLargest.add(4);   // returns 8
```

Note:
* You may assume that nums' length ≥ k-1 and k ≥ 1.

### `PriorityQueue`, `O(KlogN)`
最朴素的想法是用一个`PriorityQueue`储存每一个数，然后在查找`kth`数的时候，从PriorityQueue里取出前`k`个数，最后一一倒回去。

#### 代码
```java
class KthLargest {

    private int k;
    private Queue<Integer> queue;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        queue = new PriorityQueue<Integer>((Integer a, Integer b) -> b - a);
        for (int n : nums) queue.add(n);
    }

    public int add(int val) {
        queue.add(val);
        if (queue.size() < k) return 0;
        Deque<Integer> stack = new LinkedList<Integer>();
        for (int i = 0; i < k; i++) {
            stack.push(queue.poll());
        }
        int res = stack.peek();
        while (!stack.isEmpty()) {
            queue.add(stack.pop());
        }
        return res;
    }
}
```

#### 结果
![kth-largest-element-in-a-stream-1](/images/leetcode/kth-largest-element-in-a-stream-1.png)


### 二分查找`ArrayList`, `O(N)`
保持一个`ArrayList`有序，可以很容易地找到第`k`大数字。做`add()`操作插入新元素的时候，可以用 **二分查找** 快速找到插入位置。

#### 代码
```java
class KthLargest {

    private int k;
    private List<Integer> list;

    public KthLargest(int k, int[] nums) {
        this.k = k - 1;
        list = new ArrayList<Integer>();
        for (int num : nums) list.add(num);
        Collections.sort(list, (Integer a, Integer b) ->  b - a);
    }

    public int add(int val) {
        list.add(indexOf(val), val);
        return list.get(k);
    }

    /**
     * @param  val find the position for that val
     * @return     the position to insert given val
     */
    private int indexOf(int val) {
        int lo = 0, hi = list.size() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int midNum = list.get(mid);
            if (val > midNum) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }
}
```

#### 结果
![kth-largest-element-in-a-stream-2](/images/leetcode/kth-largest-element-in-a-stream-2.png)


### `PriorityQueue`进化版, `O(1)`
一个投机取巧的办法是使用固定长度的`PriorityQueue`。也就是始终维护一个长度为`k`的`PriorityQueue`。这样每次只需要调用一次`peek()`就可以找到第`k`大（即最小元素）。

维护这个PriorityQueue的时候，每次插入新元素也只要和最小元素比较，如果小于最小元素就用新元素替换掉。

另一个好处是定长的PriorityQueue效率本身也比不定长的高很多。

#### 代码
```java
class KthLargest {

    private int k;
    private Queue<Integer> queue;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        queue = new PriorityQueue<Integer>(k);
        for (int n : nums) add(n);
    }

    public int add(int val) {
        if (queue.size() < k) {
            queue.offer(val);
        } else if (queue.peek() < val) {
            queue.poll();
            queue.offer(val);
        }
        return (queue.size() == k)? queue.peek() : 0;
    }
}
```

#### 结果
![kth-largest-element-in-a-stream-3](/images/leetcode/kth-largest-element-in-a-stream-3.png)
