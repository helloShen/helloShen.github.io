---
layout: post
title: "Leetcode - Algorithm - Evaluate Division "
date: 2018-09-19 16:35:06
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["graph","dynamic programming","backtracking"]
level: "medium"
description: >
---

### 题目
Equations are given in the format A / B = k, where A and B are variables represented as strings, and k is a real number (floating point number). Given some queries, return the answers. If the answer does not exist, return -1.0.

Example:
```
Given a / b = 2.0, b / c = 3.0.
queries are: a / c = ?, b / a = ?, a / e = ?, a / a = ?, x / x = ? .
return [6.0, 0.5, -1.0, 1.0, -1.0 ].
```

The input is:
```
vector<pair<string, string>> equations
vector<double>& values
vector<pair<string, string>> queries
```
where, `equations.size() == values.size()`, and the values are positive. This represents the equations. Return `vector<double>`.

According to the example above:
```
equations = [ ["a", "b"], ["b", "c"] ],
values = [2.0, 3.0],
queries = [ ["a", "c"], ["b", "a"], ["a", "e"], ["a", "a"], ["x", "x"] ].
```
The input is always valid. You may assume that evaluating the queries will result in no division by zero and there is no contradiction.

### 题目分析
首先除法之间的传递，可以用一个“图”表示。每个除法对应图上的一条边。如果`a -> b`连通，`b -> c`连通，可以推论`a -> c`也连通。如果`a / b = 2.0`，`b / c = 3.0`，那么，
> `a / c = (a / b) * (b / c) = 2.0 * 0.333 = 0.666`

可以用一个二维矩阵`double[n][n] matrix`表示一个图。每个除法公式，比如，】`a / b = 2.0`，在图上对应`4`个点:
* matrix[a][a] = 1.0
* matrix[b][b] = 1.0
* matirx[a][b] = 2.0
* matirx[b][a] = 0.5

把所有题目给出的基本除法公式都在矩阵上标识之后，我们就得到一个图。但这个图只是基本图，不包含任何推论。接下来我们有两个选择，
1. 要么直接在这个基本图上用BFS搜索想要的答案。通过`a -> b`找到`b`点，再探索`b -> c`。最后把路径上所有遇到的值，用乘法连起来。
2. 直接系统性的计算每一种可能的推论。


做DFS搜索可以用回溯算法。
```java
public double[] calcEquation(String[][] equations, double[] values, String[][] queries) {
    // detect size
    Map<String, Integer> idTable = new HashMap<>();
    int id = 0;
    for (String[] eq : equations) {
        if (!idTable.containsKey(eq[0]))  idTable.put(eq[0], id++);
        if (!idTable.containsKey(eq[1]))  idTable.put(eq[1], id++);
    }
    int size = idTable.size();
    floydWarshall = new double[size][size];
    // init floydWarshall
    for (int i = 0; i < equations.length; i++) {
        int idA = idTable.get(equations[i][0]);
        int idB = idTable.get(equations[i][1]);
        double value = values[i];
        floydWarshall[idA][idA] = 1.0;
        floydWarshall[idB][idB] = 1.0;
        floydWarshall[idA][idB] = value;
        floydWarshall[idB][idA] = 1 / value;
    }
    // search query by DFS
    double[] result = new double[queries.length];
    Set<Integer> visited = new HashSet<>();
    for (int i = 0; i < queries.length; i++) {
        if (idTable.containsKey(queries[i][0]) && idTable.containsKey(queries[i][1])) {
            visited.clear();
            visited.add(idTable.get(queries[i][0]));
            result[i] = dfs(idTable.get(queries[i][0]), idTable.get(queries[i][1]), 1.0, visited);
        } else {
            result[i] = -1.0;
        }
    }
    return result;
}

private double[][] floydWarshall;

private double dfs(int from, int to, double pre, Set<Integer> visited) {
    if (from == to) {
        return pre;
    }
    for (int media = 0; media < floydWarshall.length; media++) {
        if (floydWarshall[from][media] != 0.0 && !visited.contains(media)) {
            visited.add(media);
            double result = dfs(media, to, pre * floydWarshall[from][media], visited);
            if (result != -1.0) { return result; }
            visited.remove(media);
        }
    }
    return -1.0;
}
```

动态规划直接统计所有推论。
```java
public double[] calcEquation(String[][] equations, double[] values, String[][] queries) {
    // detect size
    Map<String, Integer> idTable = new HashMap<>();
    int id = 0;
    for (String[] eq : equations) {
        if (!idTable.containsKey(eq[0]))  idTable.put(eq[0], id++);
        if (!idTable.containsKey(eq[1]))  idTable.put(eq[1], id++);
    }
    int size = idTable.size();
    // init floydWarshall
    double[][] floydWarshall = new double[size][size];
    for (int i = 0; i < equations.length; i++) {
        int idA = idTable.get(equations[i][0]);
        int idB = idTable.get(equations[i][1]);
        double value = values[i];
        floydWarshall[idA][idA] = 1.0;
        floydWarshall[idB][idB] = 1.0;
        floydWarshall[idA][idB] = value;
        floydWarshall[idB][idA] = 1 / value;
    }
    // derive all relations
    for (int media = 0; media < size; media++) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (floydWarshall[i][media] != 0.0 && floydWarshall[media][j] != 0.0) {
                    floydWarshall[i][j] = floydWarshall[i][media] * floydWarshall[media][j];
                }
            }
        }
    }
    // collect result
    double[] result = new double[queries.length];
    for (int i = 0; i < queries.length; i++) {
        if (idTable.containsKey(queries[i][0]) && idTable.containsKey(queries[i][1])) {
            int idA = idTable.get(queries[i][0]);
            int idB = idTable.get(queries[i][1]);
            result[i] = (floydWarshall[idA][idB] == 0.0)? -1.0 : floydWarshall[idA][idB];
        } else {
            result[i] = -1.0;
        }
    }
    return result;
}
```

#### 结果
![evaluate-division-1](/images/leetcode/evaluate-division-1.png)
