---
layout: post
title: "Leetcode - Algorithm - Container with Most Water"
date: 2017-03-22 22:20:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["two pointers","array"]
level: "medium"
description: >
---

### 题目
![most-water-0](/images/leetcode/most-water-0.png)
Given n non-negative integers a1, a2, ..., an, where each represents a point at coordinate (i, ai). n vertical lines are drawn such that the two endpoints of line i is at (i, ai) and (i, 0). Find two lines, which together with x-axis forms a container, such that the container contains the most water.

Note: You may not slant the container and n is at least 2.

### 暴力遍历 O(n^2)
这种题最无脑的总是可以暴力遍历所有情况，计算面积，然后维护一个最大面积值。但`O(n^2)`的复杂度，效率很低。

#### 代码
```java
public class Solution {
    public int maxArea(int[] height) {
        int max = 0;
        for (int i = 0; i < height.length - 1; i++) {
            for (int j = i + 1; j < height.length; j++) {
                int area = (j-i) * Math.min(height[i],height[j]);
                max = Math.max(max,area);
            }
        }
        return max;
    }
}
```

#### 结果
不出所料，算法是对的，但超时了。
![most-water-1](/images/leetcode/most-water-1.png)

### 我自己的O(nlogn)动态规划算法
通过这题，学到一种设计算法的有效技巧，**`维护当前最优解`**，有点类似于“动态规划”。基本思路如下，
> 对于最大值问题，其实任何一次比较中，较小的那个解都再也不可能是最大解。所以暴力遍历会重复比较很多以前的较小值，对最后求最大值没有任何帮助。合理的做法是只保留每次比较中的较大值。之后的所有情况，都只和最大值比较。

下面是我当时的解题思路，
![most-water-4](/images/leetcode/most-water-4.png)
为了避免使用暴力遍历，就需要换个角度来看这个问题。我画了一个图（上图），通过观察发现，首先水的容量取决于，两边中比较短的那一条边。一条短边搭配其他所有长边，面积只取决于`x`轴的长度。

重点来了，当我只有`1`长度的线段（所有大于1的都等同于1），最大面积取决于这些线段里，分开距离最远的两条划出的面积。当我有了`2`长度的线段，最大面积有了另一种可能，高度为`2`的最大面积，也就是分开最远的两个长度为`2`的线段。把它和刚才高度为`1`的最大面积比较，我就可以得出一个高度小于等于`2`的所有面积的最大面积。以此类推。

但假设所有高度更高的线段都只有等同的长度比较麻烦。我可以从最长的线段开始，先计算最长的两条线段构成的面积，然后再加入短一点的线段，高度小了，但宽度可能大了。算出面积并比较面积大小，维护一个最大面积。有点类似动态规划。复杂度应该是`O(n)`。但因为做之前需要先排序所以总体复杂度`O(nlogn)`。而且是线段长度和它的下标组成一组数据，用`Map.Entry`来装，影响了效率。

#### 代码
```java
import java.util.*;

public class Solution {
    public static int maxArea(int[] height) {
        List<Map.Entry<Integer,Integer>> list = new ArrayList<>();
        for (int i = 0; i < height.length; i++) {
            list.add(new AbstractMap.SimpleEntry<Integer,Integer>(i,height[i]));
        }
        list.sort(new Comparator<Map.Entry<Integer,Integer>>() {
            public int compare(Map.Entry<Integer,Integer> entry1, Map.Entry<Integer,Integer> entry2) {
                return Integer.compare(entry2.getValue(),entry1.getValue());
            }
        });
        int theHeight = list.get(1).getValue();
        int num1 = list.get(0).getKey();
        int num2 = list.get(1).getKey();
        int[] theRange = new int[]{Math.min(num1,num2), Math.max(num1,num2)};
        int maxArea = theHeight * (theRange[1] - theRange[0]);
        for (int i = 2; i < list.size(); i++) {
            theHeight = list.get(i).getValue();
            int newRange = list.get(i).getKey();
            theRange[0] = Math.min(theRange[0],newRange);
            theRange[1] = Math.max(theRange[1],newRange);
            int newArea = theHeight * (theRange[1] - theRange[0]);
            maxArea = Math.max(maxArea,newArea);
        }
        return maxArea;
    }
}
```

#### 结果
狗屎运，恰巧通过。
![most-water-2](/images/leetcode/most-water-2.png)


### 最优算法 O(n)
这题最有效的解法，思路也是类似“动态规划”的维护当前最优解。可以先计算相距最远的两个线段盛水的面积。哪边线段短，就往前推进一格。比较新构成的面积和之前维护的最大面积，只保留较大值。直到两条线段重合。Leetcode上对其的解释如下：
> In this problem, the smart scan way is to set two pointers initialized at both ends of the array. Every time move the smaller value pointer to inner array. Then after the two pointers meet, all possible max cases have been scanned and the max situation is 100% reached somewhere in the scan.


#### 代码
```java
import java.util.*;

public class Solution {
    public int maxArea(int[] height) {
        int left = 0, right = height.length-1, maxArea = 0;
        while (left < right) {
            maxArea = Math.max(maxArea, Math.min(height[left],height[right]) * (right - left));
            if (height[left] <= height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return maxArea;
    }
}
```

#### 结果
结果不错，基本是最有方案了。
![most-water-3](/images/leetcode/most-water-3.png)
