---
layout: post
title: "Leetcode - Algorithm - Range Addition "
date: 2018-08-29 13:35:33
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目

Assume you have an array of length n initialized with all 0's and are given k update operations.

Each operation is represented as a triplet: [startIndex, endIndex, inc] which increments each element of subarray A[startIndex ... endIndex] (startIndex and endIndex inclusive) with inc.

Return the modified array after all k operations were executed.

Example:
```
Given:

    length = 5,
    updates = [
        [1,  3,  2],
        [2,  4,  3],
        [0,  2, -2]
    ]

Output:

    [-2, 0, 3, 5, 3]
```
Explanation:
```
Initial state:
[ 0, 0, 0, 0, 0 ]

After applying operation [1, 3, 2]:
[ 0, 2, 2, 2, 0 ]

After applying operation [2, 4, 3]:
[ 0, 2, 5, 5, 3 ]

After applying operation [0, 2, -2]:
[-2, 0, 3, 5, 3 ]
```


### 老老实实一个一个数加
这题很有意思。第一反应这题除了每个数都加一遍没哟别的办法。但如果真是这样，又完全没有出题的必要。

#### 代码
```java
class Solution {
    public int[] getModifiedArray(int length, int[][] updates) {
        if (length <= 0) {
            return null;
        }
        int[] res = new int[length];
        // assert: numbers in updates are legal
        for (int[] update : updates) {
            int num = update[2];
            for (int i = update[0]; i <= update[1]; i++) {
                res[i] += num;
            }
        }
        return res;
    }
}
```

#### 结果
![range-addition-1](/images/leetcode/range-addition-1.png)


### 利用累加法
这题其实是`Range Sum Query`在二维数组的推广。做法是对于任意区间`[x,y]`都加`n`。我们只需要在`x`的位置`+n`，然后在`y+1`的位置`-n`。然后最后再对整个数组做`Range Sum Query`处理即可。

同样是上面这个例子：
```    
length = 5,

updates = [
    [1,  3,  2],
    [2,  4,  3],
    [0,  2, -2]
]
```
每次更新以后的数组
```
                        [0,0,0,0,0]
-------------------------------------
update: [1,3,2]     ->  [0,2,0,0,-2]    
update: [2,4,3]     ->  [0,2,3,0,-2]
update: [0,2,-2]    ->  [-2,2,3,2,-2]
```
最后做`Range Sum Query`累加处理，
```
[-2,2,3,2,-2]
[-2, -2+2, -2+2+3, -2+2+3+2, -2+2+3+2-2] = [-2,0,3,5,3]
```

仔细看，`[1,3,2]`的目的就是在`[1]`,`[2]`,`[3]`位置三次`+2`。
```
      2   2   2
|-0-|-1-|-2-|-3-|-4-|
```
这和累加法做的是同一件事：
```
累加法：

|-0-|-1-|-2-|-3-|-4-|
  0   2   0   0  -2
>+0
---->+2
-------->+2
------------>+2
---------------->+2-2

 +0  +2  +2  +2  +2-2
  0  +2  +2  +2  0
|-0-|-1-|-2-|-3-|-4-|
```


#### 代码
```java
class Solution {
    public int[] getModifiedArray(int length, int[][] updates) {
        if (length <= 0) {
            return null;
        }
        int[] res = new int[length];
        // assert: numbers in updates are legal
        for (int[] update : updates) {
            int num = update[2];
            for (int i = update[0]; i <= update[1]; i++) {
                res[i] += num;
            }
        }
        return res;
    }
}
```

#### 结果
![range-addition-2](/images/leetcode/range-addition-2.png)
