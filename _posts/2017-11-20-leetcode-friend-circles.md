---
layout: post
title: "Leetcode - Algorithm - Friend Circles "
date: 2017-11-20 18:15:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["union find","depth first search"]
level: "medium"
description: >
---

### 题目
There are `N` students in a class. Some of them are friends, while some are not. Their friendship is transitive in nature. For example, if A is a direct friend of B, and B is a direct friend of C, then A is an indirect friend of C. And we defined a friend circle is a group of students who are direct or indirect friends.

Given a `N*N` matrix M representing the friend relationship between students in the class. If `M[i][j] = 1`, then the `ith` and `jth` students are direct friends with each other, otherwise not. And you have to output the total number of friend circles among all the students.

Example 1:
```
Input:
[[1,1,0],
 [1,1,0],
 [0,0,1]]
Output: 2
```

Explanation:The 0th and 1st students are direct friends, so they are in a friend circle.
The 2nd student himself is in a friend circle. So return 2.
Example 2:
```
Input:
[[1,1,0],
 [1,1,1],
 [0,1,1]]
Output: 1
```

Explanation:The 0th and 1st students are direct friends, the 1st and 2nd students are direct friends,
so the 0th and 2nd students are indirect friends. All of them are in the same friend circle, so return 1.

Note:
* `N` is in range `[1,200]`.
* `M[i][i] = 1` for all students.
* If `M[i][j] = 1`, then `M[j][i] = 1`.

### 思路
首先这种连通性问题一般都有两种思路：
* 第一种 “Union Find”。$$O(n^2)$$，其中n代表学生的数量。
* 第二种 “DFS”挖出整棵树。$$O(n^2\log_{}{n})$$，其中n代表学生的数量。

### DFS解法
遍历每个学生，顺藤摸瓜，针对每个学生，把它的朋友圈整个挖出来，然后标记成已访问。直到所有学生都被标记为已访问。 遍历过程中，已经被标记为已访问的学生不再参加递归。

#### 代码
```java
class Solution {
    private boolean[] table = new boolean[0];
    private int[][] local = new int[0][0];
    private void init(int[][] m) {
        table = new boolean[m.length];
        local = m;
    }
    public int findCircleNum(int[][] M) {
        init(M);
        int count = 0;
        for (int i = 0; i <table.length; i++) {
            if (!table[i]) {
                count++;
                dfs(i);
            }
        }
        return count;
    }
    private void dfs(int student) {
        for (int i = 0; i < local.length; i++) {
            if ((local[student][i] == 1) && (table[i] == false)) {
                table[i] = true;
                dfs(i);
            }
        }
    }
}
```

#### 结果
![friend-circles-1](/images/leetcode/friend-circles-1.png)
