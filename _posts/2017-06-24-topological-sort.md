---
layout: post
title: "Graph - Topological Sort"
date: 2017-06-24 19:12:00
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["graph","depth first search","breadth first search","topological sort"]
description: >
---

### 有向图
有向图中的边是单向的。`A->B`表示`A`到`B`的单向连接性。有向图之所以重要，因为它是生活中很多实际应用场景的一种抽象。尤其是表示一系列的 **"依赖关系"**。比如编程语言中类的 **“继承”** 关系，以及库函数的调用顺序。类和类之间有相互的继承关系，库函数之间也有强依赖关系。当这种依赖关系一复杂，就不能靠肉眼来识别。
![topological-sort-directed-graph-1](/images/leetcode/topological-sort-directed-graph-1.png)

### 有向图主要用来处理两类问题：可达性问题，和调度顺序问题
#### 顶点的可达性问题
从一些特定的顶点出发，有两种情况，顶点集合中的另一些点是不可达的，

1. 第一种，单纯地因为没有有向边指向一些顶点
这类问题，最典型的就是垃圾回收问题。从一些根节点出发，如果没有任何一个引用指向堆中的某一些对象，那么他们将被标记成`不可达的`，从而被系统回收。
![topological-sort-garbage-collection-1](/images/leetcode/topological-sort-garbage-collection-1.png)

2. 第二种，当出现闭环的时候
当闭环就是依赖关系间的无限循环。完成`A`之前，必须完成`B`，完成`B`之前，必须完成`C`，完成`C`之前，又必须完成`A`。这时候，`[A,B,C]`任何一个任务都玩不成。
![topological-sort-directed-circle-1](/images/leetcode/topological-sort-directed-circle-1.png)

#### 调度顺序问题
最典型的就是 **选课问题**。 大学的某些课程，都有 **先导课**。 必须完成这些规定的先导课，才有资格选修后续的课程。还有就是库函数的调用顺序的决定。每个包都可能依赖其他的一些包的服务才能运行。所以以怎么样的一个顺序加载这些外部的包，才能保证目标程序的正常运行？

解决调度问题的一个重要前提就是：
> 研究调度顺序问题，必须在 **有向无环图** 中进行。有环的存在，任务互相依赖，都无法完成。

**有向无环图（Directed Acyclic Graph）** 就是没有环的有向图。


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
