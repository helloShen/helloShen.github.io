---
layout: post
title: "Leetcode - Algorithm - Super Ugly Number "
date: 2018-08-04 20:38:04
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","math","heap"]
level: "meidum"
description: >
---

### 题目
Write a program to find the nth super ugly number.

Super ugly numbers are positive numbers whose all prime factors are in the given prime list primes of size k.

Example:
```
Input: n = 12, primes = [2,7,13,19]
Output: 32
Explanation: [1,2,4,7,8,13,14,16,19,26,28,32] is the sequence of the first 12 super ugly numbers given primes = [2,7,13,19] of size 4.
```
Note:
* 1 is a super ugly number for any given primes.
* The given numbers in primes are in ascending order.
* 0 < k ≤ 100, 0 < n ≤ 106, 0 < primes[i] < 1000.
* The nth super ugly number is guaranteed to fit in a 32-bit signed integer.

### 暴力除法
比较笨的办法是逐个数字去判断它是不是丑陋数。

对丑陋数的判断很简单，拿列表中的素数挨个除，如果最后能除尽，就是丑陋数，否则就不是。

#### 代码
```java
public int nthSuperUglyNumber(int n, int[] primes) {
    if (n == 1) { return 1; }
    n--;
    int next = 2;
    for( ; n > 0; next++) {
        if (isUglyNumber(next,primes)) {
            n--;
        }
    }
    return next-1;
}

private boolean isUglyNumber(int n, int[] primes) {
    for (int prime : primes) {
        while (n % prime == 0) { n /= prime;}
    }
    return n == 1;    // 最后除干净得到1，就肯定是。
}
```

#### 结果
![super-ugly-number-1](/images/leetcode/super-ugly-number-1.png)


### 自己构造每个丑陋数
以`[2,7,13,19]`为例，首先`1`肯定是丑陋数，
```
丑陋数： 1
```
然后用`1`去乘以`[2,7,13,19]`里的每一个数，得到4个候选数。这时候，四个候选数的乘数都是`1`，
```
原始素数：   2,      7,      13,     19
候选乘数：   1,      1,      1,      1
候选数：    2*1,    7*1,   13*1,    19*1
丑陋数： 1,
```
取其中最小的数`2`作为下一个丑陋数，同时拿走的那个`2`的位置更新下一个候选数：`2*2`。这个`*2`就是指`1`后面的一个丑陋数。
```
原始素数：   2,      7,      13,     19
候选乘数：   2,      1,      1,      1
候选数：    2*2,    7*1,   13*1,    19*1
丑陋数： 1,2
```
第三轮，还是在4个候选数中选一个最小的，`4`，更新为下一个丑陋数，
```
原始素数：   2,      7,      13,     19
候选乘数：   2,      1,      1,      1
候选数：    2*4,    7*1,   13*1,    19*1
丑陋数： 1,2,4
```
以此类推。

原理是这样，
> 丑陋数的本质就是，除了1和它本身，它只有我们列出来的这些质因数。

因为素数自带 **“原子性”**，话再说得白一点就是，
> 丑陋数就是我们列出来那几个素数乘来乘去的出来的。

所有“丑陋数”其实是 **“很纯净的”**，因为它不掺杂其他质因数的杂质。

所以后面这个算法做的工作就是 **确保只用给定的那几个素数生成后面一系列的“丑陋数”**，以 **保持丑陋数的纯洁**。

需要注意可能会发生重复的情况比如`2*7 = 7*2`。如果本轮最小候选数和上一个丑陋数重复是，本轮不添加新丑陋数。

#### 代码
```java
class Solution {
     public int nthSuperUglyNumber(int n, int[] primes) {
        int[] uglyNumbers = new int[n]; // 储存结果
        int uglyNumbersP = 0;           // 结果列表的指针
        uglyNumbers[uglyNumbersP++] = 1;
        int[] pointers = new int[primes.length];    // 标识每个素数基元都倍化到哪儿了
        int[] candidates = new int[primes.length];  // 本轮参加竞选的倍化好的基元
        for (int i = 0; i < primes.length; i++) {
            candidates[i] = primes[i];
        }
        while (uglyNumbersP < n) {
            // 取本轮最小值
            int minP = 0; // 指示本轮是第几个候选数当选
            for (int j = 1; j < candidates.length; j++) {
                if (candidates[j] < candidates[minP]) {
                    minP = j;
                }
            }
            // 最小候选数成功当选。注意候选数和上一个丑陋数重复是，本轮不添加新丑陋数。
            if (candidates[minP] > uglyNumbers[uglyNumbersP-1]) {
                uglyNumbers[uglyNumbersP++] = candidates[minP];
            }
            // 更新候选数列表
            candidates[minP] = primes[minP] * (uglyNumbers[++pointers[minP]]);
        }
        return uglyNumbers[n-1];
    }
}
```

#### 结果
![super-ugly-number-2](/images/leetcode/super-ugly-number-2.png)


### 用`Min Heap`维护最小候选数
如果用一个数组储存所有的候选数，每次都需要正比于`primes.length`的时间（遍历整个`primes[]`）来查找下一个应该当选的最小候选数。

而一个标准的Min Heap可以在`O(logn)`时间里完成取得最小值和插入新元素的动作。如果primes规模非常大，用Min Heap储存候选数，效率更高。

我这里写了一个特殊变种的Min Heap，除了候选数的信息，还附带了指向丑陋数组的指针（这个指针不影响Min Heap元素的大小，即比较Min Heap元素大小，只看候选数）。

#### 代码
```java
class Solution {
    public int nthSuperUglyNumber(int n, int[] primes) {
        // 初始化丑陋数数组
        int[] uglyNumbers = new int[n];
        uglyNumbers[0] = 1;
        int uglyNumbersP = 1;
        // 初始化候选数列表（是一个Min Heap）
        // 每个元素是一个int[2]，其中[0]是实际候选数，[1]是指向现有丑陋数组的指针
        MinHeapWithPointer candidates = new MinHeapWithPointer(primes.length);
        for (int prime : primes) {
            candidates.insert(prime,0);
        }
        // 每次从候选数组中取出最小的数添加到丑陋数组中，然后再根据丑陋数组更新候选数
        while (uglyNumbersP < n) {
            int[] candidate = candidates.getMin();
            if (candidate[0] != uglyNumbers[uglyNumbersP-1]) {
                uglyNumbers[uglyNumbersP++] = candidate[0];
            }
            int newCandidate = candidate[0] / uglyNumbers[candidate[1]] * uglyNumbers[candidate[1]+1];
            candidates.insert(newCandidate,candidate[1]+1);
        }
        return uglyNumbers[n-1];
    }

    // 这道题专用的，每个内部节点都有2个值的Min Heap
    // 用来封装一系列的候选数以及他们当前对应到丑陋数列表上的指针
    // 比较大小只依靠heap[]中的值，不用info[]。
    private class MinHeapWithPointer {

        public MinHeapWithPointer(int size) {
            heap = new int[size+1];
            info = new int[size+1];
            p = 1;
        }
        /**
         * return min value in heap and update the heap
         * @return min value in heap (current root node)
         */
        public int[] getMin() {
            int[] min = new int[2];
            min[0] = heap[1];
            min[1] = info[1];
            heap[1] = heap[--p];
            info[1] = info[p];
            minHelper(1);
            return min;
        }
        // bubble-down the pseudo-root
        // 冒泡只依靠heap[]中的值，不考虑info[]
        private void minHelper(int root) {
            int left = root * 2, right = left + 1;
            // 当左右子节点中至少有一个大于父节点时
            if ((left < p && heap[left] < heap[root]) || (right < p && heap[right] < heap[root])) {
                // 优先考虑换左节点，只有当右节点确实比左节点小才考虑换右节点
                if (right < p && heap[left] > heap[right]) {
                    swap(root,right);
                    minHelper(right);
                } else {
                    swap(root,left);
                    minHelper(left);
                }
            }
        }
        // insert a new number at the end of array and bubble-up it
        public void insert(int val, int addition) {
            int curr = p, parent = p / 2;
            heap[p] = val;
            info[p] = addition;
            p++;
            // 如果新节点值小于其父节点，冒泡
            while (parent > 0 && heap[curr] < heap[parent]) {
                swap(curr,parent);
                curr = parent;
                parent = curr / 2;
            }
        }
        public boolean isEmpty() {
            return p <= 1;
        }
        public String toString() {
            return Arrays.toString(heap) + "\n" + Arrays.toString(info);
        }

        private int[] heap;
        private int[] info;
        private int p;

        private void swap(int a, int b) {
            int temp = heap[a];
            int tempInfo = info[a];
            heap[a] = heap[b];
            info[a] = info[b];
            heap[b] = temp;
            info[b] = tempInfo;
        }
    }
}
```

#### 结果
![super-ugly-number-3](/images/leetcode/super-ugly-number-3.png)
