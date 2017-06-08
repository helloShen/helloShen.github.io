---
layout: post
title: "Leetcode - Algorithm - Min Stack "
date: 2017-06-07 21:11:22
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack","design"]
level: "easy"
description: >
---

### 题目
Design a stack that supports push, pop, top, and retrieving the minimum element in constant time.
```
push(x) -- Push element x onto stack.
pop() -- Removes the element on top of the stack.
top() -- Get the top element.
getMin() -- Retrieve the minimum element in the stack.
Example:
MinStack minStack = new MinStack();
minStack.push(-2);
minStack.push(0);
minStack.push(-3);
minStack.getMin();   --> Returns -3.
minStack.pop();
minStack.top();      --> Returns 0.
minStack.getMin();   --> Returns -2.
```


### 用数组实现
关键就是加一个`min`字段，记录当前最小值。所有操作都要加入维护这个最小值的代码。

#### 代码
```java
public class MinStack {
    private static final int DEFAULT_SIZE = 16;
    private int[] nums;
    private int min;
    private int size;

    /** initialize your data structure here. */
    public MinStack() {
        nums = new int[DEFAULT_SIZE];
        min = Integer.MAX_VALUE;
        size = 0;
    }

    public void push(int x) {
        if (size == nums.length) {
            nums = Arrays.copyOf(nums,nums.length * 2); // auto double the size
        }
        nums[size] = x;
        size++;
        min = Math.min(min,x);
    }

    public void pop() {
        if (size > 0) {
            size--;
            if (nums[size] == min) {
                min = Integer.MAX_VALUE;
                for (int i = 0; i < size; i++) {
                    min = Math.min(min,nums[i]);
                }
            }
        }
    }

    public int top() {
        if (size == 0) { return 0; }
        return nums[size-1];
    }

    public int getMin() {
        return min;
    }
}
```

#### 结果
只有`pop()`在最坏的情况下是 $$O(n)$$ 的复杂度。要达到 $$O(\log{n})$$ 是不可能的。
![min-stack-1](/images/leetcode/min-stack-1.png)


### 直接用`list`
上面的数组是自己维护长度扩展的，可能影响了效率。用现成的`list`容器，效率提高了很多。

#### 代码
```java
public class MinStack {

        private List<Integer> nums;
        private int min;

        public MinStack() {
            nums = new LinkedList<>();
            min = Integer.MAX_VALUE;
        }

        public void push(int x) {
            nums.add(x);
            min = Math.min(min,x);
        }

        public void pop() {
            if (! nums.isEmpty()) {
                if (min == nums.remove(nums.size()-1)) {
                    min = Integer.MAX_VALUE;
                    for (int num : nums) {
                        if (num < min) { min = num; }
                    }
                }
            }
        }

        public int top() {
            if (! nums.isEmpty()) {
                return nums.get(nums.size()-1);
            } else {
                return 0;
            }
        }

        public int getMin() {
            return min;
        }
}
```

#### 结果
![min-stack-2](/images/leetcode/min-stack-2.png)


### 每次都记录当前值和`min`的差值
维护一个`Stack`，每个元素记录的是当前值和`min`的差值`x-min`。
* 当这个差`>0`，表示当前值比当前`min`大多少。
* 当这个差`<0`，表示当前`min`被更新为当前值，而这个差代表`当前min`和`前任min`之间的差值。

#### 代码
```java
public class MinStack {

        private Deque<Long> nums;
        private long min;

        public MinStack() {
            nums = new LinkedList<>();
            min = (long)Integer.MAX_VALUE;
        }

        public void push(int x) { // 储存的是和前任min的差值
            long gap = (long)x - min; // always store the difference to the min value
            //System.out.println("Gap of " + x + " and " + min + " = " + gap);
            nums.offerFirst(gap);
            if (gap < 0) {
                min = x;
            }
        }

        public void pop() {
            if (! nums.isEmpty()) {
                long gap = nums.pollFirst();
                if (gap < 0) { // current ele == min value
                    min = min - gap; // to find thw previous min value
                }
            }
        }

        public int top() {
            if (! nums.isEmpty()) {
                long gap = nums.peekFirst();
                if (gap < 0) { // 当前堆顶就是最小值
                    return (int)min;
                } else { // 当前堆顶不是当前最小值，需要计算
                    return (int)(min + gap);
                }
            } else {
                return 0;
            }
        }

        public int getMin() {
            return (int)min;
        }
}
```

#### 结果
![min-stack-3](/images/leetcode/min-stack-3.png)
