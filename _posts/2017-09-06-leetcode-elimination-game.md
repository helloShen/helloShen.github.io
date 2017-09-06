---
layout: post
title: "Leetcode - Algorithm - Elimination Game "
date: 2017-09-06 16:58:49
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "medium"
description: >
---

### 题目
There is a list of sorted integers from 1 to n. Starting from left to right, remove the first number and every other number afterward until you reach the end of the list.

Repeat the previous step again, but this time from right to left, remove the right most number and every other number from the remaining numbers.

We keep repeating the steps again, alternating left to right and right to left, until a single number remains.

Find the last number that remains starting with a list of length n.

Example:
```
Input:
n = 9,
1 2 3 4 5 6 7 8 9
2 4 6 8
2 6
6

Output:
6
```

### 朴素一步步做
先根据题目意思，用一个真实的`List`，一个一个删除元素。

#### 代码
```java
class Solution {
    public int lastRemaining(int n) {
        List<Integer> list = new LinkedList<>();
        for (int i = 1; i <= n; i++) { list.add(i); }
        while (list.size() > 1) {
            for (int i = 0; list.size() > 1 && i < list.size(); i++) {
                list.remove(i);
            }
            for (int i = list.size()-1; list.size() > 1 && i >= 0; i -= 2) {
                list.remove(i);
            }
        }
        return list.get(0);
    }
}
```

#### 结果
![elimination-game-1](/images/leetcode/elimination-game-1.png)


### 找规律，不需要真实操作数字，把问题抽象化
参照下面这个解释，

```
My idea is to update and record head in each turn. when the total number becomes 1, head is the only number left.

When will head be updated?

if we move from left
if we move from right and the total remaining number % 2 == 1
like 2 4 6 8 10, we move from 10, we will take out 10, 6 and 2, head is deleted and move to 4
like 2 4 6 8 10 12, we move from 12, we will take out 12, 8, 4, head is still remaining 2
then we find a rule to update our head.

example:
1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24

Let us start with head = 1, left = true, step = 1 (times 2 each turn), remaining = n(24)

we first move from left, we definitely need to move head to next position. (head = head + step)
So after first loop we will have:
1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 - > 2 4 6 8 10 12 14 16 18 20 22 24
head = 2, left = false, step = 1 * 2 = 2, remaining = remaining / 2 = 12

second loop, we move from right, in what situation we need to move head?
only if the remaining % 2 == 1, in this case we have 12 % 2 == 0, we don't touch head.
so after this second loop we will have:
2 4 6 8 10 12 14 16 18 20 22 24 - > 2 6 10 14 18 22
head = 2, left = true, step = 2 * 2 = 4, remaining = remaining / 2 = 6

third loop, we move from left, move head to next position
after third loop we will have:
2 6 10 14 18 22 - > 6 14 22
head = 6, left = false, step = 4 * 2 = 8, remaining = remaining / 2 = 3

fourth loop, we move from right, NOTICE HERE:
we have remaining(3) % 2 == 1, so we know we need to move head to next position
after this loop, we will have
6 14 22 - > 14
head = 14, left = true, step = 8 * 2 = 16, remaining = remaining / 2 = 1

while loop end, return head
```

#### 代码
```java
class Solution {
    public int lastRemaining(int n) {
        int head = 1, step = 1, remain = n;
        boolean fromLeft = true;
        while (remain > 1) {
            if (fromLeft || (remain % 2 == 1)) { head += step; }
            step *= 2;
            remain /= 2;
            fromLeft = !fromLeft;
        }
        return head;
    }
}
```

#### 结果
![elimination-game-2](/images/leetcode/elimination-game-2.png)
