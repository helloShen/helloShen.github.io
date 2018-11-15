---
layout: post
title: "Leetcode - Algorithm - Two Sum Three "
date: 2018-11-13 17:45:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table"]
level: "easy"
description: >
---

### 题目
Design and implement a TwoSum class. It should support the following operations: add and find.

add - Add the number to an internal data structure.
find - Find if there exists any pair of numbers which sum is equal to the value.

Example 1:
```
add(1); add(3); add(5);
find(4) -> true
find(7) -> false
```

Example 2:
```
add(3); add(1); add(2);
find(3) -> true
find(6) -> false
```

### 有序数列
首先应该考虑有序数列的情况下能不能将问题简化。此时`find()`函数可以用`Two Pointers`法。先将两个指针`lo`和`hi`分别指向数列头和尾。然后慢慢收缩这个窗口。
```
target = 18
 lo                  hi
 |                   |
[1, 3, 5, 8, 14, 16, 21]

 lo               hi
 |                |
[1, 3, 5, 8, 14, 16, 21]

1 + 21 > 18， hi往左边收缩

   lo             hi
    |             |
[1, 3, 5, 8, 14, 16, 21]

1 + 16 < 18， lo往右边收缩

   lo         hi
    |         |
[1, 3, 5, 8, 14, 16, 21]

3 + 16 > 18， hi往左边收缩

...
...
以此类推
```

这样做`find()`函数的复杂度为`O(N)`。

但缺点是无论是用`int[]`数组，或者`LinkedList<Integer>`链表实现，维护有序数列的插入`add()`操作复杂度都是`O(N)`。想要以`O(lgN)`时间内实现`add()`函数，需要用到 **“二叉搜索树（Binary Search Tree）”**，但是二叉搜索树的`find()`因为要搜索所有可能的配对，最坏情况要执行`N`次查找，所以复杂度是`O(NlgN)`。

1. 数组或者链表：
    * `add()`: O(N)
    * `find()`: O(N)
2. 二叉搜索树（红黑树）：
    * `add()`: O(lgN)
    * `find()`: O(NlgN)

### `HashMap`查表法
把所有数字都记录在`HashMap`里。那么对于求和`k`，任何一个数字`x`，只需要在`Map`里查找差值`k - x = y`即可。假设数列`[1, 2, 3]`，求和`5`。
```
1 -> 在Map里查找 5 - 1 = 4

2 -> 在Map里查找 5 - 2 = 3
```

使用`HashMap`的好处是`add()`操作`O(1)`就能完成：
* `add()`: O(1)
* `find()`: O(N)

#### 代码
```java
class TwoSum {

    private Map<Integer, Integer> table;

    public TwoSum() {
        table = new HashMap<Integer, Integer>();
    }

    public void add(int number) {
        Integer prevTimes = table.get(number);
        table.put(number, (prevTimes == null)? 1 : prevTimes + 1);
    }

    public boolean find(int value) {
        Iterator<Map.Entry<Integer, Integer>> ite = table.entrySet().iterator();
        while (ite.hasNext()) {
            Map.Entry<Integer, Integer> next = ite.next();
            int num = next.getKey();
            int target = value - num;
            if (target == num) {
                if (next.getValue() > 1) return true;
            } else {
                if (table.containsKey(target)) return true;
            }
        }
        return false;
    }

}
```

#### 结果
![two-sum-three-1](/images/leetcode/two-sum-three-1.png)

#### 稍微做一点优化
这里开销比较大的是对`HashMap`做的遍历操作，需要将`HashMap`转换成`HashSet`，再动用迭代器`Iterator`以及内置数据结构`Map.Entry`。单纯为了遍历所有数字，可以额外用一个`ArrayList<Integer>`储存所有数字。增加的开销也只是`O(1)`的`add()`操作。

另外，可以记录数列的最大值和最小值，对于明显太大或太小的输入，可以提前淘汰。
```java
class TwoSum {

    private int min, max;
    private List<Integer> nums;
    private Map<Integer, Integer> table;

    public TwoSum() {
        min = Integer.MAX_VALUE;
        max = Integer.MIN_VALUE;
        nums = new ArrayList<Integer>();
        table = new HashMap<Integer, Integer>();
    }

    public void add(int number) {
        min = Math.min(min, number);
        max = Math.max(max, number);
        nums.add(number);
        Integer prevTimes = table.get(number);
        table.put(number, (prevTimes == null)? 1 : prevTimes + 1);
    }

    public boolean find(int value) {
        if (value < min * 2 || value > max * 2) return false;
        for (int n : nums) {
            int target = value - n;
            if (target == n) {
                if (table.containsKey(target) && table.get(target) > 1) return true;
            } else {
                if (table.containsKey(target)) return true;
            }
        }
        return false;
    }

}
```

#### 结果
能这样超过100%还是很开心的。
![two-sum-three-2](/images/leetcode/two-sum-three-2.png)
