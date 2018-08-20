---
layout: post
title: "Leetcode - Algorithm - Line Reflection "
date: 2018-08-20 17:52:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","hash table"]
level: "medium"
description: >
---

### 题目
Given n points on a 2D plane, find if there is such a line parallel to y-axis that reflect the given points.

Example 1:
```
Input: [[1,1],[-1,1]]
Output: true
```
Example 2:
```
Input: [[1,1],[-1,-1]]
Output: false
```
Follow up:
Could you do better than `O(n^2)` ?


### 排序法，O(nlogn)
首先纵向对称，意味着对称的两个点的y轴必须相等。
![line-reflection-a](/images/leetcode/line-reflection-a.png)

根据这一点，可以将所有点先按y轴排序。在同等高度的点里再按x轴排序，
![line-reflection-b](/images/leetcode/line-reflection-b.png)
![line-reflection-c](/images/leetcode/line-reflection-c.png)

最后检查这些点是否是对同一个轴对称。

#### 代码
```java
class Solution {
    public boolean isReflected(int[][] points) {
        //按照高度y排序
        Arrays.sort(points,new Comparator<int[]>(){
            public int compare(int[] a, int[] b) {
                return a[1] - b[1];
            }
        });
        //比较所有y轴相同的点的x轴位置
        Integer doubleLineParallel = null;
        int lo = 0, hi = 0;
        while (lo < points.length) {
            //找出所有y轴相同的点
            for (hi = lo; hi < points.length && points[lo][1] == points[hi][1]; hi++);
            //y轴相同的点按x轴排序
            Arrays.sort(points,lo,hi,new Comparator<int[]>(){
                public int compare(int[] a, int[] b) {
                    return a[0] - b[0];
                }
            });
            int rememberHi = hi--; //记住遍历到哪儿了
            if (doubleLineParallel == null) { //找到唯一可能的对称轴
                doubleLineParallel = points[lo][0] + points[hi][0];
            }
            while (lo <= hi) {
                if (points[lo++][0] + points[hi--][0] != doubleLineParallel) {
                    return false;
                }
                while (lo <= hi && points[lo][0] == points[lo-1][0]) { lo++; } //跳过重叠的点
                while (lo <= hi && points[hi][0] == points[hi+1][0]) { lo++; } //跳过重叠的点
            }
            lo = rememberHi;
        }
        return true;
    }
}
```

#### 结果
![line-reflection-1](/images/leetcode/line-reflection-1.png)


### 用`HashSet`，O(n)
熟悉原理之后，可以做出一个重要的观察：
> 所有点都必须有对同一个纵向的轴对称的 **对称点**。若我们把所有点都加入某个集合`Set`，那么每个点的对称点也应该包含在这个集合内。

只要知道了这条轴，很容易根据一个点计算出对称点的坐标。

假设对称轴为`x = k`，若A点坐标为`[x,y]`，它的对称点的坐标为：
> [2k - x, y]

关键是怎么计算出这条 **对称轴**。一个很聪明的做法是：
> 找到最靠左（x最小）以及最靠右（x最大）的两个点，他们一定是一对对称点。

#### 代码
```java
class Solution {
    public boolean isReflected(int[][] points) {
        Set<Point> pointsSet = new HashSet<>();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int[] point : points) {
            if (pointsSet.add(new Point(point[0],point[1]))) {
                min = Math.min(min,point[0]);
                max = Math.max(max,point[0]);
            }
        }
        int sum = min + max;
        for (int[] point : points) {
            Point expectedReflection = new Point(sum - point[0],point[1]);
            if (!pointsSet.contains(expectedReflection)) {
                return false;
            }
        }
        return true;
    }
    private class Point {
        private int x;
        private int y;
        private Point() {
            x = 0;
            y = 0;
        }
        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        @Override
        public boolean equals(Object obj) {
            Point anotherPoint = (Point)obj;
            return (anotherPoint.x == x) && (anotherPoint.y == y);
        }
        private int hash = 0;
        @Override
        public int hashCode() { //谨慎使用惰性散列，这样会使HashSet的唯一性收到威胁
            return x * 31 + y;
        }
    }
}
```

#### 结果
![line-reflection-2](/images/leetcode/line-reflection-2.png)
