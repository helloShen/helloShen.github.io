---
layout: post
title: "Leetcode - Algorithm - Cheapest Flights Within K Stops "
date: 2018-10-14 19:14:34
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["graph", "depth first search", "breadth first search", "dynamic programming", "dijkstra", "bellman ford"]
level: "medium"
description: >
---

### 题目
There are n cities connected by m flights. Each fight starts from city u and arrives at v with a price w.

Now given all the cities and fights, together with starting city src and the destination dst, your task is to find the cheapest price from src to dst with up to k stops. If there is no such route, output -1.

Example 1:
* Input: n = 3, edges = [[0,1,100],[1,2,100],[0,2,500]], src = 0, dst = 2, k = 1
* Output: 200

Explanation:
The graph looks like this:
![cheapest-flights-within-k-stops-a](/images/leetcode/cheapest-flights-within-k-stops-a.png)
The cheapest price from city 0 to city 2 with at most 1 stop costs 200, as marked red in the picture.

Example 2:
* Input: n = 3, edges = [[0,1,100],[1,2,100],[0,2,500]], src = 0, dst = 2, k = 0
* Output: 500

Explanation:
The graph looks like this:
![cheapest-flights-within-k-stops-b](/images/leetcode/cheapest-flights-within-k-stops-b.png)
The cheapest price from city 0 to city 2 with at most 0 stop costs 500, as marked blue in the picture.

Note:
* The number of nodes n will be in range [1, 100], with nodes labeled from 0 to n - 1.
* The size of flights will be in range [0, n * (n - 1) / 2].
* The format of each flight will be (src, dst, price).
* The price of each flight will be in the range [1, 10000].
* k is in the range of [0, n - 1].
* There will not be any duplicated flights or self cycles.

### DFS / BFS
很清楚，这题的本质是“图”。求有条件的最短路径。暴力DFS，或者BFS总能解决问题。

#### 代码
先写一个DFS压压惊。
```java
class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int K) {
        flightsMap = new HashMap<Integer, List<int[]>>();
        for (int[] flight : flights) {
            if (!flightsMap.containsKey(flight[0])) {
                flightsMap.put(flight[0], new ArrayList<int[]>());
            }
            flightsMap.get(flight[0]).add(new int[]{flight[1], flight[2]});
        }
        minPrice = Integer.MAX_VALUE;
        dfs(0, K, src, dst, new HashSet<Integer>());
        return (minPrice == Integer.MAX_VALUE)? -1 : minPrice;
    }

    private Map<Integer, List<int[]>> flightsMap;
    private int minPrice;

    private void dfs(int price, int stop, int curr, int dst, Set<Integer> visited) {
        if (stop < -1) return;
        if (curr == dst) {
            minPrice = Math.min(minPrice, price);
            return;
        }
        List<int[]> flightsFromCurr = flightsMap.get(curr);
        if (flightsMap.containsKey(curr)) {
            for (int[] to : flightsMap.get(curr)) {
                if (visited.add(to[0])) {
                    dfs(price + to[1], stop - 1, to[0], dst, visited);
                    visited.remove(to[0]);
                }
            }
        }
    }
}
```

#### 结果
![cheapest-flights-within-k-stops-1](/images/leetcode/cheapest-flights-within-k-stops-1.png)


### 自底向上的动态规划
![cheapest-flights-within-k-stops-c](/images/leetcode/cheapest-flights-within-k-stops-c.png)

假设`dst = b`，我能知道城市`x -> b`直达（K = 0）的价钱。假设`c -> b = 100`，然后又知道`a -> c = 100`，我就能知道当`a -> b (K = 1)`的时候，是之前两个子问题中取价钱更便宜的那个：`min(500, 200) = 200`。
1. `a -> b (K = 0)` = 500
2. `c -> b (K = 0)` + `a -> c` = 100 + 100 = 200

#### 代码
```java
class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int K) {
        int[] prev = new int[n];
        int[] curr = new int[n];
        Arrays.fill(prev, Integer.MAX_VALUE);
        Arrays.fill(curr, Integer.MAX_VALUE);
        int[][] flightsMap = new int[n][n];
        for (int[] flight : flights) {
            flightsMap[flight[0]][flight[1]] = flight[2];
            if (flight[1] == dst) prev[flight[0]] = flight[2];
        }
        for (int stop = 1; stop <= K; stop++) {
            for (int from = 0; from < n; from++) {
                if (from == dst) continue;
                curr[from] = prev[from];
                for (int to = 0; to < n; to++) {
                    if (prev[to] != Integer.MAX_VALUE && flightsMap[from][to] != 0) {
                        curr[from] = Math.min(curr[from], flightsMap[from][to] + prev[to]);
                    }
                }
            }
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }
        return (prev[src] == Integer.MAX_VALUE)? -1 : prev[src];
    }
}
```

#### 结果
![cheapest-flights-within-k-stops-2](/images/leetcode/cheapest-flights-within-k-stops-2.png)


### 在BFS的过程中维护动态规划表
从出发城市开始，如果新路线更新了到达特定城市的最低价格，则把它加入到BFS的下一层节点集合中，继续探索。没有更新最低价就终止这条分支。

#### 代码
```java
class Solution {
    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int K) {
        int[][] flightsTable = new int[n][n];
        for (int[] row : flightsTable) Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] flight : flights) {
            flightsTable[flight[0]][flight[1]] = flight[2];
        }
        List<Integer> flightList = new ArrayList<>();
        List<Integer> priceList = new ArrayList<>();
        for (int to = 0; to < n; to++) {
            if (flightsTable[src][to] != Integer.MAX_VALUE) {
                flightList.add(to);
                priceList.add(flightsTable[src][to]);
            }
        }
        K--;
        while (K-- >= 0 && !flightList.isEmpty()) {
            int size = flightList.size();
            for (int i = 0; i < size; i++) {
                int from = flightList.remove(0);
                int price = priceList.remove(0);
                for (int to = 0; to < n; to++) {
                    if (flightsTable[from][to] != Integer.MAX_VALUE && price + flightsTable[from][to] < flightsTable[src][to]) {
                        flightsTable[src][to] = price + flightsTable[from][to];
                        flightList.add(to);
                        priceList.add(flightsTable[src][to]);
                    }
                }
            }
        }
        return (flightsTable[src][dst] == Integer.MAX_VALUE)? -1 : flightsTable[src][dst];
    }
}
```

#### 结果
![cheapest-flights-within-k-stops-3](/images/leetcode/cheapest-flights-within-k-stops-3.png)
