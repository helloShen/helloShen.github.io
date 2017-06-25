---
layout: post
title: "Leetcode - Algorithm - Course Schedule Two "
date: 2017-06-24 19:10:17
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["graph","depth first search","breadth first search","topological sort"]
level: "medium"
description: >
---

### 题目
There are a total of n courses you have to take, labeled from `0` to `n - 1`.

Some courses may have prerequisites, for example to take course 0 you have to first take course 1, which is expressed as a pair: `[0,1]`

Given the total number of courses and a list of prerequisite pairs, return the ordering of courses you should take to finish all courses.

There may be multiple correct orders, you just need to return one of them. If it is impossible to finish all courses, return an empty array.

For example:
```
2, [[1,0]]
```
There are a total of 2 courses to take. To take course 1 you should have finished course 0. So the correct course order is [0,1]
```
4, [[1,0],[2,0],[3,1],[3,2]]
```
There are a total of 4 courses to take. To take course `3` you should have finished both courses 1 and 2. Both courses 1 and 2 should be taken after you finished course 0. So one correct course order is `[0,1,2,3]`. Another correct ordering is`[0,2,1,3]`.

Note:
* The input prerequisites is a graph represented by a list of edges, not adjacency matrices. Read more about how a graph is represented.
* You may assume that there are no duplicate edges in the input prerequisites.

### 拓扑排序（Topological Sort）
拓扑排序主要就是用来解决上面讲的第二类问题，调度顺序问题。主要有两种思路：
1. 广度优先探索（BFS）
2. 深度优先探索（DFS）

（ **注！** 后面的拓扑排序算法主要基于下面这张图演示）
![topological-sort-dag-1](/images/leetcode/topological-sort-dag-1.png)


### 广度优先探索（BFS）
广度优先探索的思路非常清楚，最不容易搞混，
> 既然依赖关系错综复杂，要捋清楚思路，就要从完全不依赖其他节点的 **叶节点** 出发（下图中所有灰色的顶点）。先完成能独立完成的所有 **叶节点任务**，然后，再看看现在，现在剩下的哪些任务又可以完成了？ 最后按照完成的顺序，所有节点可以总结出一个深度表。

![topological-sort-bfs-demo-1](/images/leetcode/topological-sort-bfs-demo-1.png)

具体实现的时候，需要额外维护一个数组，用来记录当前每个任务 **还未完成的先导任务的数量**。 一旦某个先导任务完成，这个数字就减去`1`. 未完成先导任务数量为`0`的任务，可以被标记成 **可完成任务**。

#### 代码
```java
/**
 * BFS Topological Sort
 */
public class SolutionV1 {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        // build degree table, and the prerequisites map
        Map<Integer,List<Integer>> edges = new HashMap<>();
        for (int i = 0; i < numCourses; i++) {
            edges.put(i,new ArrayList<Integer>());
        }
        int[] degree = new int[numCourses];
        for (int[] edge : prerequisites) {
            degree[edge[0]]++;
            edges.get(edge[1]).add(edge[0]);
        }
        // iteration from courses with 0 degree
        Queue<Integer> zeroDegree = new LinkedList<>();
        int[] ret = new int[numCourses];
        int cur = 0;
        int count = 0;
        do {
            int size = zeroDegree.size();
            for (int i = 0; i < size; i++) {
                List<Integer> upperCourses = edges.remove(zeroDegree.poll());
                for (int upper : upperCourses) {
                    degree[upper]--;
                }
            }
            for (int i = 0; i < numCourses; i++) {
                if (degree[i] == 0) {
                    zeroDegree.offer(i);
                    ret[cur++] = i;
                    count++;
                    degree[i] = -1;
                }
            }
        } while (!zeroDegree.isEmpty());
        return (count == numCourses)? ret : new int[0];
    }
}
```

#### 结果
![course-schedule-two-1](/images/leetcode/course-schedule-two-1.png)


### 深度优先探索（DFS）
深度优先的思路非常简单直白，只是不太符合一般人思维的习惯。
> 只要是 **有向无环图**，就能保证能够完成所有任务。所以只要最朴素的深度优先探索就行了，重点是：**需要记录标记之前已经完成的任务，并保证每个任务只完成一次**。最后得到的顺序就是一个可行的调度顺序。

还是之前的那个图，`[0]`号任务的先到任务为：`[1]`,`[5]`,`[6]`。顺藤摸瓜，为了完成任务`[0]`，最后我们必须先完成所有 **红色** 标记的任务`[1,4,5,6,9,10,11,12]`。
![topological-sort-dfs-demo-1](/images/leetcode/topological-sort-dfs-demo-1.png)

接下来需要完成 **绿色** 标记的`[2]`和`[3]`号任务，当查询到`[0]`和`[5]`号先导任务的时候，这两个任务都已经完成了，就不需要重复完成了。对于`[7]`和`[8]`号任务，也是同样的道理。
![topological-sort-dfs-demo-2](/images/leetcode/topological-sort-dfs-demo-2.png)

整个 **DFS** 探索的具体过程如下表所示，
![topological-sort-dfs-1](/images/leetcode/topological-sort-dfs-1.jpg)

上面的过程，对应到下面这张表，就是中间的`Post Order`那一列，
![topological-sort-dfs-2](/images/leetcode/topological-sort-dfs-2.jpg)

#### 代码
```java
/**
 * DFS Topological Sort - post-order
 */
public class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        // Reduce the edges list into a Map. The map should not contains null entry
        Map<Integer,List<Integer>> edges = new HashMap<>();
        for (int i = 0; i < numCourses; i++) {
            edges.put(i,new ArrayList<Integer>());
        }
        for (int[] edge : prerequisites) {
            edges.get(edge[0]).add(edge[1]);
        }
        boolean[] finished = new boolean[numCourses];
        List<Integer> postOrder = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            boolean findCircle = dfs(i,edges,new boolean[numCourses],finished,postOrder); // need a new log array each time
            if (findCircle) { return new int[0]; }
        }
        int[] ret = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            ret[i] = postOrder.get(i);
        }
        return ret;
    }
    /**
     * return true if find circle, other wise return false
     */
    public boolean dfs(int course, Map<Integer,List<Integer>> edges, boolean[] log, boolean[] finished, List<Integer> postOrder) {
        // find circle? kill!
        if (log[course] == true) { return true; }
        // solved problem? kill!
        if (finished[course]) { return false; }
        // dfs backtracking recursion
        List<Integer> pres = edges.remove(course);
        log[course] = true;
        for (int pre : pres) {
            if (dfs(pre,edges,log,finished,postOrder)) { return true; } // find circle
        }
        log[course] = false;
        postOrder.add(course); // post-order topological sort
        finished[course] = true;
        return false;
    }
}
```

#### 结果
![course-schedule-two-2](/images/leetcode/course-schedule-two-2.png)


### 深度优先探索（DFS）的第二种方法
利用深度优先的探索，最终也能得到类似广度优先（BFS）的表示顶点深度的表。具体做法就是，**每个顶点的深度，等于它所有先导节点深度的最大值，再加1。**

> T(curr) = Max(T(pre)) + 1

![topological-sort-bfs-demo-1](/images/leetcode/topological-sort-bfs-demo-1.png)

#### 代码
```java
/**
 * Topological Sort - DFS - Calculate Depth of Each Course
 */
public class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        // Reduce the edges list into a Map. The map should not contains null entry
        Map<Integer,List<Integer>> edges = new HashMap<>();
        for (int i = 0; i < numCourses; i++) {
            edges.put(i,new ArrayList<Integer>());
        }
        for (int[] edge : prerequisites) {
            edges.get(edge[0]).add(edge[1]);
        }
        int[] depth = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            int d = dfs(i,edges,new boolean[numCourses],depth); // need a new log array each time
            if (d == 0) { return new int[0]; } // find circle
        }
        // reduce depth into a Map
        Map<Integer,List<Integer>> depthMap = new HashMap<>();
        for (int i = 1; i <= numCourses; i++) {
            depthMap.put(i,new ArrayList<Integer>());
        }
        for (int i = 0; i < numCourses; i++) {
            depthMap.get(depth[i]).add(i);
        }
        int[] ret = new int[numCourses];
        int cur = 0;
        for (int i = 1; i <= numCourses; i++) {
            List<Integer> level = depthMap.remove(i);
            for (int course : level) {
                ret[cur++] = course;
            }
        }
        return ret;
    }
    /**
     * return the depth of the course
     * return 1 if this course has no prerequisites
     * return 0 if find circle
     */
    public int dfs(int course, Map<Integer,List<Integer>> edges, boolean[] log, int[] depth) {
        // find circle? kill!
        if (log[course] == true) { return 0; }
        // solved problem? merge!
        if (depth[course] > 0) { return depth[course]; }
        // dfs backtracking recursion
        List<Integer> pres = edges.remove(course);
        int courseDepth = 1;
        log[course] = true;
        for (int pre : pres) {
            int preDepth = dfs(pre,edges,log,depth);
            if (preDepth == 0) {
                return 0;
            } else {
                courseDepth = Math.max(courseDepth,preDepth+1);
            }
        }
        log[course] = false;
        depth[course] = courseDepth;
        return courseDepth;
    }
}
```

#### 结果
![course-schedule-two-3](/images/leetcode/course-schedule-two-3.png)
