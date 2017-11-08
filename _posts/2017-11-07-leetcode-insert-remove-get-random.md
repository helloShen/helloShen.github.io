---
layout: post
title: "Leetcode - Algorithm - Insert Remove Get Random "
date: 2017-11-07 21:05:47
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hashmap","list"]
level: "medium"
description: >
---

### 题目
Design a data structure that supports all following operations in average O(1) time.

* insert(val): Inserts an item val to the set if not already present.
* remove(val): Removes an item val from the set if present.
* getRandom: Returns a random element from current set of elements. Each element must have the same probability of being returned.

Example:
```
// Init an empty set.
RandomizedSet randomSet = new RandomizedSet();

// Inserts 1 to the set. Returns true as 1 was inserted successfully.
randomSet.insert(1);

// Returns false as 2 does not exist in the set.
randomSet.remove(2);

// Inserts 2 to the set, returns true. Set now contains [1,2].
randomSet.insert(2);

// getRandom should return either 1 or 2 randomly.
randomSet.getRandom();

// Removes 1 from the set, returns true. Set now contains [2].
randomSet.remove(1);

// 2 was already in the set, so return false.
randomSet.insert(2);

// Since 2 is the only number in the set, getRandom always return 2.
randomSet.getRandom();
```

### 一个HashMap加上一个ArrayList
思路很简单，要在$$O(1)$$时间内完成`search()`和`insert()`，`remove()`操作，只有`HashMap`。但问题是HashMap做不到在 $$O(1)$$ 时间里随机获取元素（`getRandom()`），因为映射的桶有很多空槽，最坏情况下，要考虑所有随机获得的索引都指向一个空槽，复杂度会是 $$O(n)$$。

在 $$O(1)$$ 时间内完成元素的随机访问，首先考虑的是数组，但因为不定长，所以`ArrayList`是很好的替代方案。难点是：如何处理`remove()`方法留下的空槽？ 传统做法是将空槽后的所有元素向前挪一个位置。复杂度还是 $$O(n)$$。但向前平移的做法是为了保持原集合序列的元素排序。但因为`getRandom()`函数不关心元素的排序，所以一个聪明的解决方案是：
> 用List的末尾元素来填补中间删除元素的空白。换种说法，也可以是先交换目标元素和末尾元素，然后删除交换后的末尾元素。

这样就可以在 $$O(1)$$ 时间内完成`List`的`remove()`动作。

#### 代码
```java
class RandomizedSet {

    private static HashMap<Integer,Integer> map = new HashMap<>();    // 键值对：[val,index]
    private static List<Integer> list = new ArrayList<>();            // 所有数字的列表
    private static Random r = new Random();

    /** Initialize your data structure here. */
    public RandomizedSet() {
        map.clear();
        list.clear();
    }

    /** Inserts a value to the set. Returns true if the set did not already contain the specified element. */
    public boolean insert(int val) {
        if (map.containsKey(val)) {
            return false;
        } else {
            int index = map.size();
            map.put(val,map.size());
            list.add(val);
            return true;
        }
    }

    /** Removes a value from the set. Returns true if the set contained the specified element. */
    public boolean remove(int val) {
        if (!map.containsKey(val)) {
            return false;
        } else {
            int last = list.get(list.size()-1);
            int index = map.get(val);
            map.put(last,index);
            map.remove(val);
            list.set(index,last);
            list.remove(list.size()-1);
            return true;
        }
    }

    /** Get a random element from the set. */
    public int getRandom() {
        if (list.isEmpty()) { return 0; }
        return list.get(r.nextInt(list.size()));
    }
}

/**
 * Your RandomizedSet object will be instantiated and called as such:
 * RandomizedSet obj = new RandomizedSet();
 * boolean param_1 = obj.insert(val);
 * boolean param_2 = obj.remove(val);
 * int param_3 = obj.getRandom();
 */
```

#### 结果
![insert-remove-get-random-1](/images/leetcode/insert-remove-get-random-1.png)
