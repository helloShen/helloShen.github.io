---
layout: post
title: "Leetcode - Algorithm - Surrounded Regions (to be continued...)"
date: 2017-05-16 21:07:33
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: "medium"
description: >
---

### 题目
Given a 2D board containing 'X' and 'O' (the letter O), capture all regions surrounded by 'X'.

A region is captured by flipping all 'O's into 'X's in that surrounded region.

For example,
```
X X X X
X O O X
X X O X
X O X X
```
After running your function, the board should be:
```
X X X X
X X X X
X X X X
X O X X
```

### 笨办法，杀鸡用牛刀

很像围棋的规则。基本思路是：
> 以所有相邻的`O`为小组，如果小组中有一个`O`在最外一圈，则整个小组就没有被抓住。

```
X X X X
X O O X
X X O X
X O X X
```
假设还是上面这个矩阵，如果按部就班地按下面步骤走，肯定能得到解。
1. 找出所有`O`，并记录位置。
2. 把所有`O`按位置是否连在一起，分成两组`O`和`OOO`。
3. 对于任意一组`O`，其中只要有一个`O`在最外一圈，整个一组就是活的，逃跑了。
4. 反之，某一组`O`，如果没有`O`在最外一圈，整个一组被捕获。
5. 记录完所有被捕获的小组，在`board`上把所有捕获的`O`替换成`X`。

#### 代码
```java
public class Solution {
    /**
     * 主入口
     * 1. 找出所有`O`，并记录位置。
     * 2. 把所有`O`按位置是否连在一起，分成两组`O`和`OOO`。
     * 3. 对于任意一组`O`，其中只要有一个`O`在最外一圈，整个一组就是活的，逃跑了。
     * 4. 反之，某一组`O`，如果没有`O`在最外一圈，整个一组被捕获。
     * 5. 记录完所有被捕获的小组，在`board`上把所有捕获的`O`替换成`X`。
     */
    public void solve(char[][] board) {
        if (board.length == 0) { return; }
        List<List<Integer>> captured = new ArrayList<>();
        List<List<List<Integer>>> groups = groups(board);
        for (List<List<Integer>> group : groups) {
            if (!groupIsFree(board.length,board[0].length,group)) {
                captured.addAll(group);
            }
        }
        for (List<Integer> point : captured) {
            board[point.get(0)][point.get(1)] = 'X';
        }
    }
    /**
     * 找出所有连在一起的O小组。
     */
    public List<List<List<Integer>>> groups(char[][] board) {
        List<List<List<Integer>>> groups = new ArrayList<>();
        List<List<Integer>> points = points(board);
        while (!points.isEmpty()) {
            List<List<Integer>> group = new ArrayList<>();
            List<Integer> head = points.remove(0);
            trackGroup(points,head,group);
            groups.add(group);
            for (List<Integer> point : group) {
                points.remove(point);
            }
        }
        return groups;
    }
    /**
     * 筛选出所有的O
     */
    public List<List<Integer>> points(char[][] board) {
        List<List<Integer>> points = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 'O') {
                    points.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{i,j})));
                }
            }
        }
        return points;
    }
    /**
     * 给定board上的一个O，顺藤摸瓜，找出整个相邻O小组。
     */
    public void trackGroup(List<List<Integer>> points, List<Integer> head, List<List<Integer>> group) {
        if (group.contains(head)) { return; }
        group.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{head.get(0),head.get(1)})));
        for (List<Integer> point : points) {
            if (areNeighbours(point.get(0),point.get(1),head.get(0),head.get(1))) {
                trackGroup(points,point,group);
            }
        }
    }
    /**
     * 判定两个点是否相邻。
     */
    public boolean areNeighbours (int rowA, int colA, int rowB, int colB) {
        if (rowA == rowB && Math.abs(colA - colB) == 1) { return true; }
        if (colA == colB && Math.abs(rowA-rowB) == 1) { return true; }
        return false;
    }
    /**
     * 判定整个相邻O小组是否逃脱。
     */
    public boolean groupIsFree(int height, int width, List<List<Integer>> group) {
        for (List<Integer> point : group) {
            if (pointIsFree(height,width,point.get(0),point.get(1))) { return true; }
        }
        return false;
    }
    /**
     * 判定某个点是否逃脱。
     */
    public boolean pointIsFree(int height, int width, int row, int col) {
        return row == 0 || row == height-1 || col == 0 || col == width-1;
    }
}
```

#### 结果
确实能解决问题，但遇到`O`比较多的矩阵，超时比较严重。

但这个解决方案普适性比较好，是从根本上去解决问题。就算是要做一个围棋游戏，其中部分模块也可以成为核心组件的一部分。
![surrounded-regions-1](/images/leetcode/surrounded-regions-1.png)


### 解法2

#### 代码
```java

```

#### 结果
![surrounded-regions-2](/images/leetcode/surrounded-regions-2.png)


### 解法3

#### 代码
```java

```

#### 结果
![surrounded-regions-3](/images/leetcode/surrounded-regions-3.png)
