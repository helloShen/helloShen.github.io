---
layout: post
title: "Leetcode - Algorithm - Course Schedule "
date: 2017-06-21 02:12:09
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["graph","depth first search","breadth first search","topological sort"]
level: "medium"
description: >
---

### 题目
There are a total of n courses you have to take, labeled from 0 to n - 1.

Some courses may have prerequisites, for example to take course 0 you have to first take course 1, which is expressed as a pair: `[0,1]`

Given the total number of courses and a list of prerequisite pairs, is it possible for you to finish all courses?

For example:
```
2, [[1,0]]
```
There are a total of 2 courses to take. To take course 1 you should have finished course 0. So it is possible.
```
2, [[1,0],[0,1]]
```
There are a total of 2 courses to take. To take course 1 you should have finished course 0, and to take course 0 you should also have finished course 1. So it is impossible.

Note:
* The input prerequisites is a graph represented by a list of edges, not adjacency matrices. Read more about how a graph is represented.
* You may assume that there are no duplicate edges in the input prerequisites.

### 总体思路
首先，这题是一个典型的 **图** 的问题。只不过给 **图** 找了一个恰当的应用场景。

然后，出现“不能完成的课”，只有一种可能，就是 **几门课交叉互为先导课**。抽象到图的话，就是判断图是否有 **闭环**。

判断闭环，首先想到应该是 **通过遍历来检索**。 遍历的过程中，记录下 **当前路径**， 如果又回到了当前路径上的某一点，说明出现了闭环。

遍历有两种基本做法，一个 **深度优先（DFS）**， 一个 **广度优先（BFS）**。

这里比较直观的是 **深度优先（DFS）** 的解法。利用常规的 **递归回溯** 算法就能解决问题。

至于常规的 **广度优先（BFS）** 这里是不适用的。因为，广度优先只是用一个容器缓存当前层的所有节点，没有办法保留路径信息。

所以，需要在传统的广度优先遍历上做一个改变：
> 传统的广度优先遍历，是从根节点遍历到叶节点。这里需要反过来，从叶节点开始，反过来往上捋。

这个特殊的广度优先的算法，叫`Topological Sort`，过程如下，
1. 计算所有课先导课的数量。
2. 标出所有没有先导课的初级课（他们是第一批“叶节点”）
3. 把所有以这些初级课为先导课的课的先导课数量减去1.
4. 标出新一批先导课数量为0的课程，他们也是证明可以完成的课。
5. 重复3和4步骤。

### 深度优先（DFS）的递归回溯算法
用`Map`先把`Edge List`收集起来，复杂度 $$O(m)$$，`m`为`edge`的数量。（**注**：用完的edge就删掉，可以优化效果。）

直接在`Edge List`上做，复杂度 $$O(m*n)$$，`m`为`edge`的数量，`n`为课程的数量。效果不好。

如果把`Edge List`转变成一个`Matrix`效果也不好。

#### 代码
```java
/**
 * 用HashMap和HashSet储存图的信息
 * 用完就删掉用过的edge
 */
public class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        Map<Integer,List<Integer>> schedule = new HashMap<>();
        for (int[] pair : prerequisites) {
            int course = pair[0];
            List<Integer> pre = schedule.get(course);
            if (pre == null) {
                List<Integer> preList = new ArrayList<>();
                preList.add(pair[1]);
                schedule.put(course,preList);
            } else {
                pre.add(pair[1]);
            }
        }
        boolean[] canFinish = new boolean[numCourses];
        boolean[] waitingList = new boolean[numCourses];
        for (int i = 0; i < numCourses; i++) {
            if (!canFinishThisCourse(i,schedule,waitingList,canFinish)) { return false; }
        }
        return true;
    }
    public boolean canFinishThisCourse(int course, Map<Integer,List<Integer>> schedule, boolean[] waitingList, boolean[] canFinish) {
        if (canFinish[course]) { return true; }
        if (waitingList[course]) { return false; }
        List<Integer> preList = schedule.get(course);
        schedule.remove(course);
        if (preList == null) { canFinish[course] = true; return true; }
        // dfs backtracking
        waitingList[course] = true;
        for (int pre : preList) {
            if (!canFinishThisCourse(pre,schedule,waitingList,canFinish)) { return false; }
        }
        waitingList[course] = false;
        canFinish[course] = true; return true;
    }
}
```

#### 结果
![course-schedule-1](/images/leetcode/course-schedule-1.png)

#### 直接用`Edge List`

```java
/**
 * 尝试直接使用Edge List的图信息
 */
public class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        boolean[] canFinish = new boolean[numCourses];
        for (int i = 0; i < numCourses; i++) {
            if (!canFinishThisCourse(i,prerequisites,new boolean[numCourses],canFinish)) { return false; }
        }
        return true;
    }
    public boolean canFinishThisCourse(int course, int[][] prerequisites, boolean[] waitingList, boolean[] canFinish) {
        if (canFinish[course]) { return true; }
        if (waitingList[course]) { return false; } // find circle
        // dfs backtracking
        waitingList[course] = true;
        for (int[] pair : prerequisites) {
            if (pair[0] == course) {
                if (!canFinishThisCourse(pair[1],prerequisites,waitingList,canFinish)) { return false; }
            }
        }
        waitingList[course] = false;
        canFinish[course] = true;
        return true;
    }
}
```

#### 转成`Matrix`

```java
/**
 * 尝试把图信息转换成Matrix的形式。
 * 并且waitingList和canFinish也都用数组表示
 */
public class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        boolean[][] prerequiMatrix = new boolean[numCourses][numCourses];
        for (int[] edge : prerequisites) {
            prerequiMatrix[edge[0]][edge[1]] = true; // edge[0] require edge[1]
        }
        boolean[] canFinish = new boolean[numCourses];
        boolean[] waitingList = new boolean[numCourses];
        for (int i = 0; i < numCourses; i++) {
            if (!canFinishThisCourse(i,prerequiMatrix,waitingList,canFinish)) { return false; }
        }
        return true;
    }
    public boolean canFinishThisCourse(int course, boolean[][] prerequiMatrix, boolean[] waitingList, boolean[] canFinish) {
        if (canFinish[course]) { return true; }
        if (waitingList[course]) { return false; }
        // dfs backtracking
        waitingList[course] = true;
        for (int i = 0; i < prerequiMatrix.length; i++) {
            if (prerequiMatrix[course][i]) { // i is the required course
                if (!canFinishThisCourse(i,prerequiMatrix,waitingList,canFinish)){ return false; }
            }
        }
        waitingList[course] = false;
        canFinish[course] = true;
        return true;
    }
}
```

#### 结果
![course-schedule-2](/images/leetcode/course-schedule-2.png)

### 广度优先（BFS） `Topological Sort`法
同样，用过的`edge`信息，马上删掉，有助于提高效率。整体复杂度 $$O(m)$$，`m`为`edge`的数量。

#### 代码
```java
/**
 * Topological Search (BFS)  改进版
 * 从叶节点开始，按照深度顺序，一层层梳理
 * 改进是：用过的边，马上删掉。可以将search的过程复杂度降到 O(m)。m为edge的数量。
 */
public class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // reduce edge list by pre & calculate degree
        Map<Integer,List<Integer>> edges = new HashMap<>();
        int[] degree = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            edges.put(i,new ArrayList<Integer>());
        }
        for (int[] edge : prerequisites) {
            edges.get(edge[1]).add(edge[0]);
            degree[edge[0]]++;
        }
        // Topological Search
        Queue<Integer> zeroDegree = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (degree[i] == 0) { zeroDegree.offer(i); }
        }
        while (!zeroDegree.isEmpty()) {
            int course = zeroDegree.poll();
            List<Integer> children = edges.remove(new Integer(course));
            for (int child : children) {
                if (--degree[child] == 0) { zeroDegree.offer(child); }
            }
            degree[course] = -1;
        }
        for (int i = 0; i < numCourses; i++) {
            if (degree[i] > 0) { return false; }
        }
        return true;
    }
}
```

#### 结果
![course-schedule-3](/images/leetcode/course-schedule-3.png)
