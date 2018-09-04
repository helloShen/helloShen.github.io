---
layout: post
title: "Leetcode - Algorithm - Design Phone Directory "
date: 2018-09-03 14:33:43
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["design","hash set"]
level: "medium"
description: >
---

### 题目
Design a Phone Directory which supports the following operations:
1. get: Provide a number which is not assigned to anyone.
2. check: Check if a number is available or not.
3. release: Recycle or release a number.

Example:
```
// Init a phone directory containing a total of 3 numbers: 0, 1, and 2.
PhoneDirectory directory = new PhoneDirectory(3);

// It can return any available phone number. Here we assume it returns 0.
directory.get();

// Assume it returns 1.
directory.get();

// The number 2 is available, so return true.
directory.check(2);

// It returns 2, the only number that is left.
directory.get();

// The number 2 is no longer available, so return false.
directory.check(2);

// Release number 2 back to the pool.
directory.release(2);

// Number 2 is available again, return true.
directory.check(2);
```

### 直观解：数字在2个集合中倒来倒去，复杂度`O(n)`
用一个`Queue`表示可用数字空间（因为希望数字能够顺序发放），一个`Set`表示用过的数字集合。`get()`从可用数字空间里拿走数字，放进用过的数字空间。`release()`则相反。注意`check()`不是检查可用空间里有没有目标数，而是检查这个数在不在用过的空间里（因为`HashSet`查起来快）。
```java
class PhoneDirectory {
    public PhoneDirectory(int maxNumbers) {
        max = maxNumbers;
        for (int i = 0; i < maxNumbers; i++) {
            available.offer(i);
        }
    }

    public int get() {
        Integer ret = available.poll();
        if (ret == null) {
            return -1;
        }
        used.add(ret);
        return ret;
    }

    public boolean check(int number) {
        if (number >= max || number < 0) {
            return false;
        }
        return !used.contains(number);
    }

    public void release(int number) {
        if (used.remove(number)) {
            available.offer(number);
        }
    }

    private Set<Integer> used = new HashSet<Integer>();
    private Queue<Integer> available = new LinkedList<Integer>();
    private int max;
}
```

#### 结果
![design-phone-directory-0](/images/leetcode/design-phone-directory-0.png)

### 用一个`HashSet`回收池，复杂度`O(1)`
假设指定要100个数字的空间：`[0,99]`，用一个指针`bankP`标明现在用到哪个数字。不考虑回收的话数字从小到大顺序发放。

发出去的数字不用管。但用一个`HashSet`收集回收回来的数字。分配数字优先从这个回收池中分配。

时间复杂度`O(1)`，空间复杂度`O(n)`，其中`n`是数字的个数。

#### 代码
```java
class PhoneDirectory {

    /** Initialize your data structure here
        @param maxNumbers - The maximum numbers that can be stored in the phone directory. */
    public PhoneDirectory(int maxNumbers) {
        max = maxNumbers;
        bankP = 0; // point to next number to assign
        recycle = new HashSet<Integer>();    
    }

    /** Provide a number which is not assigned to anyone.
        @return - Return an available number. Return -1 if none is available. */
    public int get() {
        if (!recycle.isEmpty()) {
            Iterator<Integer> ite = recycle.iterator();
            int num = ite.next();
            ite.remove();
            return num;
        }
        if (bankP < max) {
            return bankP++;
        }
        return -1;
    }

    /** Check if a number is available or not. */
    public boolean check(int number) {
        return legal(number) && !assigned(number);
    }

    /** Recycle or release a number. */
    public void release(int number) {
        if (legal(number) && assigned(number)) {
            recycle.add(number);
        }
    }

    /**====================== 【私有成员】 ========================*/
    private int max;
    private int bankP;
    private Set<Integer> recycle;

    private boolean legal(int number) {
        return number >= 0 && number < max;
    }
    private boolean assigned(int number) {
        return number < bankP && !recycle.contains(number);
    }
}
```

#### 结果
![design-phone-directory-1](/images/leetcode/design-phone-directory-1.png)


### 单一数组解
有新意且漂亮的一个解。用的数据结构有点特殊。本质上就是一个数组，但带有链表的性质。这个数组是这样，
1. 每个槽位的下标代表数字
2. 数组桶内的值代表这个数字的下一个元素的偏移值

比如我的取值空间为`[0~9]`，
```
下标[0]代表这个数字的值是0，桶内值为[1]代表这个数的下一个元素的偏移值为1。
 +---+
 |   |
[1,  2,  3,  4,  5,  6,  7,  8,  9,  0]
 |                                   |
 +-----------------------------------+
            下标[9]代表这个数字的值是9，桶内值为[0]代表这个数的下一个元素的偏移值为0。
```

`get()`，`get()`，`release(0)`之后的数组，如下图所示，
![design-phone-directory-a](/images/leetcode/design-phone-directory-a.png)

但由于这个方法初始化数组的复杂度是`O(n)`，所以效率不是最高的。

#### 代码
```java
class PhoneDirectory {

    public PhoneDirectory(int maxNumbers) {
        linkedTable = new int[maxNumbers];
        for (int i = 0; i < maxNumbers; i++) {
            linkedTable[i] = (i + 1) % maxNumbers;
        }
        p = 0;
    }

    public int get() {
        if (linkedTable[p] == -1) {
            return -1;
        }
        int nextNum = p;
        p = linkedTable[p];
        linkedTable[nextNum] = -1;
        return nextNum;
    }

    public boolean check(int number) {
        return linkedTable[number] != -1;
    }

    public void release(int number) {
        if (!check(number)) {
            linkedTable[number] = p;
            p = number;
        }
    }

    /**====================== 【私有成员】 ========================*/
    private int[] linkedTable;
    private int p;

}
```

#### 结果
![design-phone-directory-2](/images/leetcode/design-phone-directory-2.png)


### 用`BitSet`，`O(n)`
每个数字占1位。数字被用掉设为`1`，数字可用设为`0`。只是提供另外一种思路，效率并不高，因为`nextClearBit()`函数也是按位查找，复杂度最坏情况`O(n)`。

#### 代码
```java
class PhoneDirectory {

    public PhoneDirectory(int maxNumbers) {
        set = new BitSet();
        smallestFreeBit = 0;
        max = maxNumbers;        
    }

    public int get() {
        if (smallestFreeBit == max) {
            return -1;
        }
        int res = smallestFreeBit;
        set.set(smallestFreeBit);
        smallestFreeBit = set.nextClearBit(smallestFreeBit);
        return res;
    }

    public boolean check(int number) {
        return !set.get(number);
    }

    public void release(int number) {
        if (set.get(number)) {
            set.clear(number);
            if (number < smallestFreeBit) {
                smallestFreeBit = number;
            }
        }
    }

    /**====================== 【私有成员】 ========================*/
    BitSet set;
    int smallestFreeBit = 0;
    int max;

}
```

#### 结果
![design-phone-directory-3](/images/leetcode/design-phone-directory-3.png)
