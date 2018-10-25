---
layout: post
title: "Leetcode - Algorithm - Network Delay Time "
date: 2018-10-23 17:27:44
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["graph", "shortest path", "dijkstra", "bellman ford", "floyd warshall"]
level: "medium"
description: >
---

### 题目
There are N network nodes, labelled 1 to N.

Given times, a list of travel times as directed edges times[i] = (u, v, w), where u is the source node, v is the target node, and w is the time it takes for a signal to travel from source to target.

Now, we send a signal from a certain node K. How long will it take for all nodes to receive the signal? If it is impossible, return -1.

Note:
1. N will be in the range [1, 100].
2. K will be in the range [1, N].
3. The length of times will be in the range [1, 6000].
4. All edges times[i] = (u, v, w) will have 1 <= u, v <= N and 1 <= w <= 100.

### 抽象成数学模型
这是典型的 **有向图的最短路径问题**。不需要求每个节点两两间的最短路径。只需要从一个特定的初始点`K`出发，找出其到其他所有节点的最短路径。

关于最短路径问题，考虑《算法》第4版中一个有向图的例子，图中一共有`[0~7]`8个节点，以及15条边连接这些节点，每条边都有自己的权重。图中黑色加粗的路线`0->2->7->3->6`代表`0->6`之间的最短路径。可以看到`0->6`的路径不止这一条，也可以`0->2->7->5->1->3->6`，也可以`0->4->5->1->3->6`，但他们路径权重之和都要大于最短路径。
![network-delay-time-a](/images/leetcode/network-delay-time-a.png)

下面介绍4种比较经典的解决最短路径的方法：
1. Dijkstra算法
2. Bellman-Ford算法
3. Floyd-Warshall算法
4. 动态规划 + 广度优先查找

### Dijkstra算法
Dijkstra的本质是一个“贪心算法”。它的核心理念是：
> 把和出发点K连通的节点视为一个子图。每次只将和这个子图距离最近的点，以及相连的那条边纳入子图。直到把所有能触及到的点都纳入子图为止。

就以刚才的图为例，从`0`节点出发，到各个节点的最短路径如下图所示，
![network-delay-time-b](/images/leetcode/network-delay-time-b.jpg)

需要用到一个`PriorityQueue`，把当前子图和外部节点的连接边都压入队列作为候选边。每次弹出最短边，将连接的新节点纳入子图，再将新节点和外部节点连接的边压入队列作为候选边。如此循环。直到所有可达节点都纳入子图为止。
```
从0出发，当前子图只有[0]节点
0 可以到 2, 4
将0->2=0.26， 0->4=0.38压入PriorityQueue
弹出更短的0->2
将2纳入子图，当前子图[0,2]
从2节点出发往前探索: 2->7=0.34
0->7 = 0->2 + 2->7 = 0.60，将0->7=0.60压入PriorityQueue
在0->4=0.38和0->7=0.60中弹出较短的0->4
将4纳入子图，当前子图[0,2,4]
从4节点出发往前探索：4->5=0.35, 4->7=0.37
0->5 = 0->4 + 4->5 = 0.73，将0->5=0.73压入PriorityQueue
0->7 = 0->4 + 4->7 = 0.75，将0->7=0.75压入PriorityQueue。注意此时队列中有两个0->7: 0.60和0.75
... ...
如此循环往复
... ...
```

再看一个比较极端的例子，上下两边权值都是`100`，中间来回折返的5条边权重为`1`。求`1->6`的最短路径，肯定是走中间折返的路线`1->2->3->4->5->6`，而不是走两边。
![network-delay-time-c](/images/leetcode/network-delay-time-c.png)


Dijkstra算法包含了动态规划的思想，因为每次纳入子图的都是确定的最短的路线，因此可以被作为以后问题的子问题的已知最优解。

**注意！** Dijkstra算法只适用于所有边权值非负的情况。带有负权值边的图不适用，因为有负权值边的加入，局部最短路径不能保证是全局最短路径。

时间复杂度：`O(NlogN)`，`N`为图中边的数量。因为PriorityQueue每次压入，弹出操作是`O(logN)`。

#### 代码
```java
class Solution {
    private class Flight {
        int dest, cost;
        private Flight(int dest, int cost) {
            this.dest = dest;
            this.cost = cost;
        }
    }
    public int networkDelayTime(int[][] times, int N, int K) {
        int[][] flightTable = new int[N + 1][N + 1];
        for (int[] row : flightTable) Arrays.fill(row, -1);
        for (int i = 1; i <= N; i++) flightTable[i][i] = 0;
        for (int[] time : times) flightTable[time[0]][time[1]] = time[2];
        PriorityQueue<Flight> heap = new PriorityQueue<>((Flight a, Flight b) -> (a.cost - b.cost));
        heap.add(new Flight(K, flightTable[K][K]));
        boolean[] visited = new boolean[N + 1];
        int[] minCosts = new int[N + 1];
        Arrays.fill(minCosts, Integer.MAX_VALUE);
        int remain = N;
        int maxPrice = 0;
        while (!heap.isEmpty()) {
            Flight cheapest = heap.poll();
            int from = cheapest.dest;
            if (visited[from]) continue;
            visited[from] = true;
            maxPrice = Math.max(maxPrice, cheapest.cost);
            remain--;
            for (int to = 1; to <= N; to++) {
                if (!visited[to] && flightTable[from][to] >= 0) {
                    int newPrice = cheapest.cost + flightTable[from][to];
                    if (newPrice < minCosts[to]) {
                        minCosts[to] = newPrice;
                        heap.add(new Flight(to, newPrice));
                    }
                }
            }
        }
        return (remain == 0)? maxPrice : -1;
    }
}
```

#### 结果
![network-delay-time-1](/images/leetcode/network-delay-time-1.png)


### Bellman-Ford算法
Bellman-Ford也带有动态规划的思想，而且是非常规整的自底向上的动态规划。它的基本思想是：
> 先确定所有只包含1条边（即原生边）的最短路径。在此基础上看扩展到2条边，然后3条边，4条边，最后到N-1条边。

这个过程中，边有一个淘汰的过程，比如一个简单的三角形`[1->2=2, 1->3=3, 2->3=1]`
```
    1
 1 / \ 3
  /   \
 2 --- 3
    1
```
只有原生边的时候`1->3=3`，当允许中间有个中转点，由两条边组成路线时，`1->2 + 2->3 = 2 < 3`，从1出发，经过2中转，再到3更近。所以1到3的路线就被更新为`1->3=2`。原生路线`1->3=3`被淘汰。

这个过程叫做`relaxing`松弛。

还是刚才极端的例子，
![network-delay-time-d](/images/leetcode/network-delay-time-d.png)

Bellman-Ford算法路子是这样，
1. 先用`1->2->3=2`松弛掉`1->3=100`
2. 再利用`1->3=100`的推论，得到新推论`1->2->3->4=3`
3. 还是利用`1->4=3`的推论，继续得到`1->2->3->4->5=4`
4. 最后根据`1->5=4`得到`1->6=5`

这个例子因为节点顺序正好对了，因此第一次遍历所有边，就能得到最终答案`1->6=5`。一般情况可能遍历到某条边的前置推论还没有得到，所以需要遍历`N-1`次，就能保证所有推论都正确得到。

因为要更新`N-1`次所有路线，所以复杂度为`O(N*E)`，`N`是节点数，`E`是边的数量。


#### 代码
```java
class Solution {
    public int networkDelayTime(int[][] times, int N, int K) {
        int[] disTo = new int[N + 1];
        Arrays.fill(disTo, Integer.MAX_VALUE);
        disTo[K] = 0;
        for (int i = 1; i < N; i++) {
            for (int[] edge : times) {
                if (disTo[edge[0]] == Integer.MAX_VALUE) continue;
                disTo[edge[1]] = Math.min(disTo[edge[1]], disTo[edge[0]] + edge[2]);
            }
        }
        int maxPrice = 0;
        for (int i = 1; i <= N; i++) {
            if (disTo[i] == Integer.MAX_VALUE) return -1;
            maxPrice = Math.max(maxPrice, disTo[i]);
        }
        return maxPrice;
    }
}
```

#### 结果
![network-delay-time-2](/images/leetcode/network-delay-time-2.png)


### Floyd-Warshall算法
前面的Dijkstra算法和Bellman-Ford算法都只计算从单个出发点到其他点的最短路径。Floyd-Warshall算法更系统性计算所有节点两两之间的最短距离。核心思想也是和Bellman-Ford一样的“松弛”淘汰太长的边。

它先用原生边初始化一个矩阵`int[][] matrix`。

然后计算所有节点`i`和`j`两两之间经过节点`1`做中转站做“松弛”以后的情况。

然后在此基础上，再计算`i`和`j`两两之间经过节点`2`做中转站做“松弛”以后的情况。

如此循环，知道节点`N`。

所以Floyd-Warshall也是一个DP的思想。

#### 代码
```java
class Solution {
    public int networkDelayTime(int[][] times, int N, int K) {
        int[][] matrix = new int[N + 1][N + 1];
        // initialize maxtrix
        for (int[] row : matrix) Arrays.fill(row, Integer.MAX_VALUE);
        for (int i = 1; i <= N; i++) matrix[i][i] = 0;
        for (int[] flight : times) matrix[flight[0]][flight[1]] = flight[2];
        // dp
        for (int k = 1; k <= N; k++) {
            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= N; j++) {
                    int includeK = matrix[i][k] + matrix[k][j];
                    if (matrix[i][k] != Integer.MAX_VALUE && matrix[k][j] != Integer.MAX_VALUE && matrix[i][j] > includeK) matrix[i][j] = includeK;
                }
            }
        }
        int maxPrice = 0;
        for (int i = 1; i <= N; i++) {
            if (matrix[K][i] == Integer.MAX_VALUE) return -1;
            maxPrice = Math.max(maxPrice, matrix[K][i]);
        }
        return maxPrice;
    }
}
```

#### 结果
![network-delay-time-3](/images/leetcode/network-delay-time-3.png)

### BFS + DP
以上三个都是著名的最短路径算法。用在这道题效率不一定最高。效率最高的是下面这个BFS解法。在BFS遍历节点的过程中遇到“松弛”的情况，就更新路线，把相关节点再次压入BFS队列下一层。直到BFS队列为空。属于常规手段。

#### 代码
```java
class Solution {
    public int networkDelayTime(int[][] times, int N, int K) {
        int[][] timeMatrix = new int[N][N];
        for (int i = 0; i < N; i++) Arrays.fill(timeMatrix[i], Integer.MAX_VALUE);
        for (int[] time : times) timeMatrix[time[0] - 1][time[1] - 1] = time[2];
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if (timeMatrix[K - 1][i] != Integer.MAX_VALUE) level.add(i);
        }
        while (!level.isEmpty()) {
            int size = level.size();
            for (int i = 0; i < size; i++) {
                int from = level.remove(0);
                for (int to = 0; to < N; to++) {
                    if (to == K - 1) continue;
                    if (timeMatrix[from][to] != Integer.MAX_VALUE) {
                        if (timeMatrix[K - 1][from] + timeMatrix[from][to] < timeMatrix[K - 1][to]) {
                            timeMatrix[K - 1][to] = timeMatrix[K - 1][from] + timeMatrix[from][to];
                            level.add(to);
                        }
                    }
                }
            }
        }
        int maxTime = 0;
        for (int i = 0; i < N; i++) {
            if (i == K - 1) continue;
            if (timeMatrix[K - 1][i] == Integer.MAX_VALUE) return -1;
            maxTime = Math.max(maxTime, timeMatrix[K - 1][i]);
        }
        return maxTime;
    }
}
```

#### 结果
![network-delay-time-4](/images/leetcode/network-delay-time-4.png)
