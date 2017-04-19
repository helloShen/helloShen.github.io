---
layout: post
title: "Leetcode - Algorithm - Permutation Sequence "
date: 2017-04-17 16:43:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 主要收获
有一类问题是要靠数学抽象之后，推算出来的。

### 题目
The set `[1,2,3,…,n]` contains a total of n! unique permutations.

By listing and labeling all of the permutations in order,
We get the following sequence (ie, for n = 3):
```
"123"
"132"
"213"
"231"
"312"
"321"
```
Given n and k, return the kth permutation sequence.

Note: Given n will be between 1 and 9 inclusive.

### 真的转k次，$$O(kn)$$
假设初始数字为`12345`，`k=5`。就真的把`12345`转4次，然后返回结果。
```
12345
12354
12435
12453
12534 <-- k=5
12543
```
每次转的代价是$$O(n)$$，转`k`次，复杂度为$$O(kn)$$。

#### 代码
```java
public class Solution {
    public String getPermutation(int n, int k) {
        char[] array = new char[n];
        for (int i = 0; i < n; i++) {
            array[i] = (char)('0'+i+1);
        }
        for (int i = 1; i < k; i++) {
            rotate(array);
        }
        return new String(array);
    }
    public void rotate(char[] array) {
        char c = Character.MIN_VALUE;
        int target = -1;
        for (int i = array.length-1; i >= 0; i--) {
            if (array[i] < c) {
                target = i;
                break;
            } else {
                c = array[i];
            }
        }
        if (target != -1) {
            for (int i = array.length-1; i > target; i--) {
                if (array[i] > array[target]) {
                    char temp = array[target];
                    array[target] = array[i];
                    array[i] = temp;
                    break;
                }
            }
        }
        for (int start = target+1, end = array.length-1; start < end; start++,end--) {
            char temp = array[start];
            array[start] = array[end];
            array[end] = temp;
        }
    }
}
```

#### 结果
虽然过了，但挺凄惨的。
![permutation-sequence-1](/images/leetcode/permutation-sequence-1.png)


### 数学公式计算出结果
其实不用真的转这么多次。可以利用规律算出来结果。观察`[1,2,3,4,5]`的前30次转动的结果，
```
1  12345
2  12354
3  12435
4  12453
5  12534
6  12543
7  13245 #第7次，第二个数变3
8  13254
9  13425
10 13452
11 13524
12 13542
13 14235 #第13次，第二个数变4
14 14253
15 14325
16 14352
17 14523
18 14532
19 15234 #第19次，第二个数变5
20 15243
21 15324
22 15342
23 15423
24 15432
25 21345 # 第25次，2开头，第二个数变回1
26 21354
27 21435
28 21453
29 21534
30 21543
31 23145 #第31次，第二个数变3
32 23415
33 23451
34 23514
```

规律是因为4的阶乘`4! = 4*3*2*1 = 24`。所以高位万位，每`24`次加1.

3的阶乘`3! = 3*2*1 = 6`。所以高位千位，每`6`次加1.



#### 代码
```java
public class Solution {
    public String getPermutation(int n, int k) {
        List<Character> nums = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            nums.add((char)(i+'0'));
        }
        char[] res = new char[n];
        int period; // 循环周期，5位数的话，首位数的循环周期就是[5-0-1]!，就是4的阶乘，等于24
        k--; // 调整序数，题目是从1开始
        for (int index = 0; index < n; index++) {
            period = 1;
            for (int i = n - index - 1; i > 0; i--) {
                period *= i;
            }
            res[index] = nums.remove(k/period);
            k = k % period;
        }
        return new String(res);
    }
}
```

#### 结果
![permutation-sequence-2](/images/leetcode/permutation-sequence-2.png)
