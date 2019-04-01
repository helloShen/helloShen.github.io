---
layout: post
title: "Leetcode - Algorithm - Prison Cells After N Days "
date: 2019-04-01 17:54:10
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math", "bitwise operation"]
level: "meidum"
description: >
---

### 题目
There are 8 prison cells in a row, and each cell is either occupied or vacant.

Each day, whether the cell is occupied or vacant changes according to the following rules:

If a cell has two adjacent neighbors that are both occupied or both vacant, then the cell becomes occupied.
Otherwise, it becomes vacant.
(Note that because the prison is a row, the first and the last cells in the row can't have two adjacent neighbors.)

We describe the current state of the prison in the following way: cells[i] == 1 if the i-th cell is occupied, else cells[i] == 0.

Given the initial state of the prison, return the state of the prison after N days (and N such changes described above.)

Example 1:
```
Input: cells = [0,1,0,1,1,0,0,1], N = 7
Output: [0,0,1,1,0,0,0,0]
Explanation:
The following table summarizes the state of the prison on each day:
Day 0: [0, 1, 0, 1, 1, 0, 0, 1]
Day 1: [0, 1, 1, 0, 0, 0, 0, 0]
Day 2: [0, 0, 0, 0, 1, 1, 1, 0]
Day 3: [0, 1, 1, 0, 0, 1, 0, 0]
Day 4: [0, 0, 0, 0, 0, 1, 0, 0]
Day 5: [0, 1, 1, 1, 0, 1, 0, 0]
Day 6: [0, 0, 1, 0, 1, 1, 0, 0]
Day 7: [0, 0, 1, 1, 0, 0, 0, 0]
```

Example 2:
```
Input: cells = [1,0,0,1,0,0,1,0], N = 1000000000
Output: [0,0,1,1,1,1,1,0]
```

Note:
* cells.length == 8
* cells[i] is in {0, 1}
* 1 <= N <= 10^9

### 最朴素的做法，直接在数组上操作
很简单，从每一天的状态`today[i - 1]`和`today[i + 1]`决定了明天`tomorrow[i]`的状态。
```
today:      [0, 1, 0, 1, 1, 0, 0, 1]
              \  X  X  X  X  X  /
tomorrow:   [0, 1, 1, 0, 0, 0, 0, 0]
                |
  today[0]和today[2]决定了tomorrow[1]
```

#### 代码
```java
class Solution {
    public int[] prisonAfterNDays(int[] cells, int N) {
        for (int i = 1; i <= N; i++) {
            int[] next = change(cells);
            cells = next;
        }
        return cells;
    }

    private int[] change(int[] pre) {
        int[] curr = new int[pre.length];
        for (int i = 1; i < pre.length - 1; i++) {
            curr[i] = (pre[i - 1] == pre[i + 1])? 1 : 0;
        }
        return curr;
    }
}
```

#### 结果
![prison-cells-after-n-days-1](/images/leetcode/prison-cells-after-n-days-1.png)


### 位操作
这个过程可以用反异或操作`~ (a ^ b)`模拟。
```
0011
1010 ^
------
1001 ~
------
0110
```

#### 代码
```java
class Solution {
    public int[] prisonAfterNDays(int[] cells, int N) {
        int num = 0;
        for (int i = 0; i < cells.length; i++) {
            num <<= 1;
            num |= cells[i];
        }
        for (int i = 1; i <= N; i++) {
            num = change(num);
        }
        int[] res = new int[cells.length];
        for (int i = cells.length - 1; i >= 0; i--) {
            res[i] = num & 1;
            num >>= 1;
        }
        return res;
    }

    private int change(int pre) {
        int mask = 126; // 126 = 0111 1110
        int left = pre << 1;
        int right = pre >> 1;
        int res = (~ (left ^ right)) & mask;
        return res;
    }
}
```

#### 结果
![prison-cells-after-n-days-2](/images/leetcode/prison-cells-after-n-days-2.png)


### 结果是循环的
循环周期为`14`，
```
Day 0:  [0, 1, 0, 1, 1, 0, 0, 1]
--------------------------------
Day 1:  [0, 1, 1, 0, 0, 0, 0, 0] --+
Day 2:  [0, 0, 0, 0, 1, 1, 1, 0]   |
Day 3:  [0, 1, 1, 0, 0, 1, 0, 0]   |
Day 4:  [0, 0, 0, 0, 0, 1, 0, 0]   |
Day 5:  [0, 1, 1, 1, 0, 1, 0, 0]   |
Day 6:  [0, 0, 1, 0, 1, 1, 0, 0]   |
Day 7:  [0, 0, 1, 1, 0, 0, 0, 0]   |
Day 8:  [0, 0, 0, 0, 0, 1, 1, 0]   | 循
Day 9:  [0, 1, 1, 1, 0, 0, 0, 0]   | 环
Day 10: [0, 0, 1, 0, 0, 1, 1, 0]   |
Day 11: [0, 0, 1, 0, 0, 0, 0, 0]   |
Day 12: [0, 0, 1, 0, 1, 1, 1, 0]   |
Day 13: [0, 0, 1, 1, 0, 1, 0, 0]   |
Day 14: [0, 0, 0, 0, 1, 1, 0, 0]   |
--------------------------------   |
Day 15: [0, 1, 1, 0, 0, 0, 0, 0] <-+
                ...
                ...
```

#### 代码
```java
class Solution {
    public int[] prisonAfterNDays(int[] cells, int N) {
        int num = 0;
        for (int i = 0; i < cells.length; i++) {
            num <<= 1;
            num |= cells[i];
        }
        num = change(num);
        int times = (N - 1) % 14;
        for (int i = 0; i < times; i++) {
            num = change(num);
        }
        int[] res = new int[cells.length];
        for (int i = cells.length - 1; i >= 0; i--) {
            res[i] = num & 1;
            num >>= 1;
        }
        return res;
    }

    private int change(int pre) {
        int mask = 126; // 126 = 0111 1110
        int left = pre << 1;
        int right = pre >> 1;
        int res = (~ (left ^ right)) & mask;
        return res;
    }
}
```

#### 结果
![prison-cells-after-n-days-3](/images/leetcode/prison-cells-after-n-days-3.png)
