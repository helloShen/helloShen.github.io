---
layout: post
title: "Leetcode - Algorithm - Single Number Three "
date: 2017-07-28 18:31:22
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation","math"]
level: "medium"
description: >
---

### 题目
Given an array of numbers nums, in which exactly two elements appear only once and all the other elements appear exactly twice. Find the two elements that appear only once.

For example:
```
Given nums = [1, 2, 1, 3, 2, 5], return [3, 5].
```

Note:
The order of the result is not important. So in the above example, [5, 3] is also correct.
Your algorithm should run in linear runtime complexity. Could you implement it using only constant space complexity?

### 用容器
如果想不出下面这种聪明的办法，最简单也能用容器解决问题。

#### 代码
```java
public class Solution {
    public int[] singleNumber(int[] nums) {
        Set<Integer> dic = new HashSet<>();
        for (int num : nums) {
            if (!dic.add(num)) { dic.remove(num); }
        }
        int[] result = new int[2];
        if (dic.size() != 2) { return result; }
        Iterator<Integer> ite = dic.iterator();
        result[0] = ite.next();
        result[1] = ite.next();
        return result;
    }
}
```

#### 结果
![single-number-three-1](/images/leetcode/single-number-three-1.png)


### 利用`XOR`异或混合
在前面的题目已经接触过`XOR`异或混合，
> XOR异或混合，会抵消两个相同的数字。因为它抵消所有相同的位。

这一题，需要遍历两次，做两次`XOR`混合。在第一次常规的`XOR`混合所有数字之后，所有成对的数字被抵消。得到的是两个目标数字的`XOR`混合结果。

本来这个混在一起的信息是没什么用的。但这里有一个窍门，假设目标数是`a`和`b`，
```
    a = 1010 0101
    b = 1001 0101
-----------------
a ^ b = 0011 0000
          ||
          这是a,b两个数字不相等的位
```
`a^b`混合之后的结果里的每一个`1`位就代表`a`,`b`两个数字在这个位上是不同的。要么`a`这位是`1`，要么`b`。

我们只需要切出这些`1`位中的任何一位做掩码，用来把所有的数字分成两组。比如取的是低位第`5`位的那个`1`，
* 第一组，所有数的第5位都是`1`
* 第二组，所有数的第5位都是`0`

这样可以确定两件事，
* `a`和`b`被分在不同的组。
* 所有成对的数都被分在了同一组。

所以最后只要对两组分别做一遍`XOR`异或混合，就可以分别得到`a`和`b`。

想不出这样的解法，也不用气馁。这种奇技淫巧本来就不是随便谁拍脑袋就可以想出来的。记住就好，以后看到类似的问题能有个启发。

#### 代码
```java
public int[] singleNumber(int[] nums) {
    // 用XOR让成对的数字互相抵消，得到两个目标数字的XOR混合后的结果。
    int mix = 0;
    for (int num : nums) { mix ^= num; }
    // 获得两目标数XOR混合后最低的1位。
    // 假设 a = 1010 0101, b = 1001 0101, 混合后 a ^ b = 0011 0000, 这步得到的就是 0001 0000
    // 之所以能这么做，因为int是用2的补码形式编码
    mix &= -mix;
    // 把所有的数分成两组：第一组，两个目标数不同的那一位是1；第二组，两个目标数不同的那一位是0.
    // 然后把两组数分别做XOR混合，最后得到的就是两个目标数。
    // 因为两个目标数被分在了不同的组，和他们分在一组的所有其他数都是成对的，会被XOR混合抵消。
    int[] result = new int[2];
    for (int num : nums) {
        if ((num & mix) == mix) { // 两个目标数不同的那一位是1
            result[0] ^= num;
        } else { // 两个目标数不同的那一位是0
            result[1] ^= num;
        }
    }
    return result;
}
```

#### 结果
![single-number-three-2](/images/leetcode/single-number-three-2.png)
