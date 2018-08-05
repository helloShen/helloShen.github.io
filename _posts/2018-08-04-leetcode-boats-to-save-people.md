---
layout: post
title: "Leetcode - Algorithm - Boats To Save People "
date: 2018-08-04 23:39:49
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","greedy","two pointers"]
level: "medium"
description: >
---

### 题目
The i-th person has weight people[i], and each boat can carry a maximum weight of limit.

Each boat carries at most 2 people at the same time, provided the sum of the weight of those people is at most limit.

Return the minimum number of boats to carry every given person.  (It is guaranteed each person can be carried by a boat.)


Example 1:
```
Input: people = [1,2], limit = 3
Output: 1
Explanation: 1 boat (1, 2)
```

Example 2:
```
Input: people = [3,2,2,1], limit = 3
Output: 3
Explanation: 3 boats (1, 2), (2) and (3)
```

Example 3:
```
Input: people = [3,5,3,4], limit = 5
Output: 4
Explanation: 4 boats (3), (3), (4), (5)
```

Note:
* 1 <= people.length <= 50000
* 1 <= people[i] <= limit <= 30000

### 先排序，然后贪心算法，复杂度`O(n^2)`
考虑这个例子`people = [5,4,3,2,1,3,4], limit = 5`，先将人的体重由高到低排序，`[5,4,4,3,3,2,1]`。为了用最少的船，我们贪心地尽可能让每条船在限重内装载最大的重量。

第一条船装一个重量5的人，满了。
```
limit = 5
第一条船
 |
[5,4,4,3,3,2,1]
```
第二条船，装了一个`4`以后，没满，继续从左往右找，结果又找到`1`，
```
limit = 5
    -第二条船-
   |  ->     |
[5,4,4,3,3,2,1]
```
以此类推。



#### 代码
```java
public int numRescueBoats(int[] people, int limit) {
    ArrayList<Integer> list = new ArrayList<>();
    for (int i = 0; i < people.length; i++) {
        list.add(people[i]);
    }
    list.sort(new Comparator<Integer>(){
        public int compare(Integer a, Integer b) {
            return b - a;
        }
    });

    int numBoats = 0;
    while (!list.isEmpty()) {
        int remain = limit - list.remove(0);
        for (int i = 0; remain > 0 && i <list.size(); i++) {
            if (list.get(i) <= remain) {
                remain -= list.remove(i); break;
            }
        }
        numBoats++;
    }
    return numBoats;
}
```


### 非贪心的"Two Pointers"，复杂度`O(n)`
上面的问题是每次都想让每条船载最大重量，其实没必要。我们可以采取另一种策略，
> 不超重的情况下，每次让最重的乘客和最轻的乘客坐一条船。

我们可以使用两个指针，一个从数组头开始往右走，一个从数组尾开始往左走。不超重的情况每次尽量取头取尾。
```
5先走
 |           |
[5,3,3,3,3,2,1]
```
`5`这个乘客太重，无法再搭载`1`。
```
    3和1一起走
   |         |
[5,3,3,3,3,2,1]
```
`3`和`1`一起走。我们不需要担心浪费的情况，不需要强迫3带2，因为，
* 只要后面还有乘客，他的体重一定小于3，肯定能带走2
* 如果后面除了2没有其他乘客，那就算3带走2，剩下的1还是要单独一条船带走，所以强制3带走2并不节省船只

这里一个小窍门，一般排序都是“升序”，为了节省倒排的时间，可以反过来直接在“升序”的数组上操作，并不影响。


#### 代码
```java
class Solution {
    public int numRescueBoats(int[] people, int limit) {
        Arrays.sort(people);
        int numBoats = 0;
        for (int i = people.length - 1, j = 0; i >= 0 && i >= j; i--) {
            if (i > j && people[i] + people[j] <= limit) {
                j++;
            }
            numBoats++;
        }
        return numBoats;
    }
}
```
