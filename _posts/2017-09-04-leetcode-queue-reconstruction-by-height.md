---
layout: post
title: "Leetcode - Algorithm - Queue Reconstruction By Height "
date: 2017-09-04 13:15:38
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["greedy"]
level: "medium"
description: >
---

### 题目
Suppose you have a random list of people standing in a queue. Each person is described by a pair of integers (h, k), where h is the height of the person and k is the number of people in front of this person who have a height greater than or equal to h. Write an algorithm to reconstruct the queue.

Note:
The number of people is less than 1,100.

Example
```
Input:
[[7,0], [4,4], [7,1], [5,0], [6,1], [5,2]]

Output:
[[5,0], [7,0], [5,2], [6,1], [4,4], [7,1]]
```

### 主要思路
首先，这题动态规划，分治法是不行的。每个队列都是一个完整的整体。所以子问题是没有意义的。主要思考的方向应该是归纳数学上的规律。

其次，就算找不出数学上的规律，回溯算法暴力尝试每一种排列，然后用一个函数判断每个队列是否合法，总是可以解的。只是复杂度非常高。

最后这题是能总结出规律的，具体细节下面会说。利用这个规律，可以用贪婪算法。

### 暴力回溯算法
用回溯算法，深度优先地递归遍历全排列。对每种可能的排列方式，都用一个函数判断其是否合法。

#### 代码
```java
class Solution {

    /* 封装[Height,Order]数据 */
    private static class People {
        private int height;
        private int order;
        private People(int height, int order) {
            this.height = height;
            this.order = order;
        }
    }
    /* 方法共享数据静态化 */
    private static List<People> peopleList = new ArrayList<>();
    private static int[][] peopleArray = new int[0][0];
    private static int cur = 0;

    /* 初始化共享数据 */
    private static void init(int[][] people) {
        peopleList.clear();
        peopleArray = new int[people.length][2];
        cur = 0;
        for (int[] p : people) { peopleList.add(new People(p[0],p[1])); }
    }

    /* 主入口 */
    public int[][] reconstructQueue(int[][] people) {
        init(people);
        return backtracking();
    }
    /* 回溯算法 */
    private int[][] backtracking() {
        if (peopleList.isEmpty()) {
            return isValide(peopleArray)? peopleArray : null;
        }
        for (int i = 0; i < peopleList.size(); i++) {
            People p = peopleList.remove(i);
            peopleArray[cur][0] = p.height;
            peopleArray[cur][1] = p.order;
            cur++;
            int[][] res = backtracking();
            if (res != null) { return res; }
            peopleList.add(i,p);
            cur--;
        }
        return null;
    }
    /* 判断一个队列是否合法 */
    private boolean isValide(int[][] people) {
        for (int i = 0; i < people.length; i++) {
            int height = people[i][0];
            int count = 0;
            for (int j = 0; j < i; j++) {
                if (people[j][0] >= height) { ++count; }
            }
            if (count != people[i][1]) { return false; }
        }
        return true;
    }

}
```

#### 结果
![queue-reconstruction-by-height-1](/images/leetcode/queue-reconstruction-by-height-1.png)


### 贪婪算法：排序以后，依次插入元素
数学解法，主要依据下面两个事实：
1. 个子最高的人无论排在哪里，前面比他高的人都是`0`（除非有人和他一样高）。
2. 个子第二高的人，如果前面有比他高的人，只可能是最高的那个人。

也就是说，个子越是高的人，位置越是自由。因为在它前面插入个子比他矮的人，不影响它前面比他高的人数。

所以我们先确定最高和次高的人的相对顺序，然后依次插入再矮一点的人到队列，这时候矮个子的插入，不影响高个子前面更高人数。而且当比矮个子更高的人的相对顺序全部确定了以后，矮个子的位置也可以确定。

考虑下面这个数组， `[[9,0],[7,0],[1,9],[3,0],[2,7],[5,3],[6,0],[3,4],[6,2],[5,2]]`。先按照身高，升序（从矮到高排列），
```
乱序队列： [[9,0],[7,0],[1,9],[3,0],[2,7],[5,3],[6,0],[3,4],[6,2],[5,2]]
按身高升序排列：[[1,9],[2,7],[3,0],[3,4],[5,3],[5,2],[6,0],[6,2],[7,0],[9,0]]
```

有两个人身高为`3`,两个人为`5`,两个人为`6`。身高相同的人之间，按降序排列，
```
身高相同的人按降序排列：[[1,9],[2,7],[3,4],[3,0],[5,3],[5,2],[6,2],[6,0],[7,0],[9,0]]
```
然后从最后最高的`[9,0]`开始，依次把人插入队列，每个人插入的位置就是他们的Order:
```
[[9,0]]                                                         // 9插入在0位
[[7,0],[9,0]]                                                   // 7插入在0位
[[6,0],[7,0],[9,0]]                                             // 6插入在0位
[[6,0],[7,0],[6,2],[9,0]]                                       // 6插入在2位
[[6,0],[7,0],[5,2],[6,2],[9,0]]                                 // 5插入在2位
[[6,0],[7,0],[5,2],[5,3],[6,2],[9,0]]                           // 5插入在3位
[[3,0],[6,0],[7,0],[5,2],[5,3],[6,2],[9,0]]                     // 3插入在0位
[[3,0],[6,0],[7,0],[5,2],[3,4],[5,3],[6,2],[9,0]]               // 3插入在4位
[[3,0],[6,0],[7,0],[5,2],[3,4],[5,3],[6,2],[2,7],[9,0]]         // 2插入在7位
[[3,0],[6,0],[7,0],[5,2],[3,4],[5,3],[6,2],[2,7],[9,0],[1,9]]   // 1插入在9位
```

整个过程的 **贪婪的**。

#### 代码
数组排序，自己写快排。因为`Collection`和`Arrays`的自带排序都不能用在二维数组。
```java
class Solution {
    /* 静态共享数据 */
    private int[][] local = new int[0][0];

    /* 主入口 */
    public int[][] reconstructQueue(int[][] people) {
        if (people.length == 0) { return people; }
        local = people;
        sort(); // 先按Height升序排序。然后在身高相同的人之间，按Order降序排列
        int[][] res = new int[local.length][2];
        for (int i = local.length - 1; i >= 0; i--) { // 从右往左遍历数组，依次插入元素
            int height = local[i][0];
            int order = local[i][1];
            insert(res, order, height, order);
        }
        return res;
    }    

    /* 先按Height升序排序。然后在身高相同的人之间，按Order降序排列 */
    private void sort() {
        sortByHeight(0,local.length-1); // 按身高升序排序
        int cur = 0;
        while (cur < local.length) {    // 对所有身高相同的人按Order降序排列
            int height = local[cur][0];
            int fast = cur;
            while (fast < local.length && local[fast][0] == height) { fast++; }
            sortByOrder(cur,fast-1);
            cur = fast;
        }
    }

    /* 根据Order排序（降序,从大到小) */
    private void sortByOrder(int lo, int hi) {
        if (lo >= hi) { return; }
        int[] border = partitionOrder(lo,hi);
        sortByOrder(lo,border[0]);
        sortByOrder(border[1],hi);
    }

    /* Three-Ways Partition of Quick Sort */
    private int[] partitionOrder(int lo, int hi) {
        int pivot = local[hi][1];
        exch(lo,hi);
        int st = lo, gt = hi;
        int cur = lo + 1;
        while (cur <= gt) {
            int order = local[cur][1];
            if (order > pivot) {
                exch(st++,cur++);
            } else if (order < pivot){
                exch(cur,gt--);
            } else {
                cur++;
            }
        }
        return new int[]{st-1,gt+1};
    }

    /* 按照Height排序 （升序，从小到大） */
    private void sortByHeight(int lo, int hi) {
        if (lo >= hi) { return; }
        int[] border = partitionHeight(lo,hi);
        sortByHeight(lo,border[0]);
        sortByHeight(border[1],hi);
    }

    /* Three-Ways Partition of Quick Sort */
    private int[] partitionHeight(int lo, int hi) {
        int pivot = local[hi][0];
        exch(lo,hi);
        int st = lo, gt = hi;
        int cur = lo + 1;
        while (cur <= gt) {
            int height = local[cur][0];
            if (height < pivot) {
                exch(st++,cur++);
            } else if (height > pivot){
                exch(cur,gt--);
            } else {
                cur++;
            }
        }
        return new int[]{st-1,gt+1};
    }
    /* 交换共享数组local里的两个指定元素 */
    private void exch(int x, int y) {
        int tempHeight = local[y][0];
        int tempOrder = local[y][1];
        local[y][0] = local[x][0];
        local[y][1] = local[x][1];
        local[x][0] = tempHeight;
        local[x][1] = tempOrder;
    }

    /* 往指定数组的指定位置插入一个新元素。后续元素依次后移。 */
    private void insert(int[][] res, int index, int height, int order) {
        for (int i = index; i < res.length; i++) {
            int tempHeight = res[i][0];
            int tempOrder = res[i][1];
            res[i][0] = height;
            res[i][1] = order;
            if (tempHeight == 0) { break; }
            height = tempHeight;
            order = tempOrder;
        }
    }
}
```

#### 结果
![queue-reconstruction-by-height-2](/images/leetcode/queue-reconstruction-by-height-2.png)
