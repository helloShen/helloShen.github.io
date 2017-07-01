---
layout: post
title: "Leetcode - Algorithm - Rectangle Area "
date: 2017-06-30 19:33:21
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math"]
level: "medium"
description: >
---

### 题目
Find the total area covered by two rectilinear **rectangles** in a **2D** plane.

Each rectangle is defined by its bottom left corner and top right corner as shown in the figure.
![rectangle-area](/images/leetcode/rectangle-area.png)
Assume that the total area is never beyond the maximum possible value of `int`.

### 主要思路
这题不存在用诸如动态规划，或者分治法的可能。纯粹的数学问题。

### 两个长方形的面积，减去重叠部分面积
用减去重叠面积的方法比较好。公式如下：
> $$area_{A} + area_{B} - area_{overlapping}$$

关键比较难计算的就是`overlapping`重叠部分的面积。需要仔细理清楚各种情况，
1. 先去掉所有重叠面积为`0`的情况。
2. 计算重叠区高度
    * A上B下重叠
    * B在A内部
    * A下B上重叠
    * A在B内部
3. 计算重叠区宽度
    * A左B右重叠
    * B在A内部
    * A右B左重叠
    * A在B内部

#### 代码
```java
public class Solution {
    public int computeArea(int A, int B, int C, int D, int E, int F, int G, int H) {
        int leftA = A, upperA = D, bottomA = B, rightA = C;
        int leftB = E, upperB = H, bottomB = F, rightB = G;
        int heightA = upperA - bottomA, widthA = rightA - leftA;
        int heightB = upperB - bottomB, widthB = rightB - leftB;
        int areaA = heightA * widthA;
        int areaB = heightB * widthB;
        int totalArea = areaA + areaB;
        if (upperA <= bottomB || upperB <= bottomA || leftA >= rightB || leftB >= rightA) { return totalArea; } // 不重叠
        int overlappingHeight = 0;
        if (upperA >= upperB ) {
            if (bottomA >= bottomB) { // A上B下重叠
                overlappingHeight = upperB - bottomA;
            } else { // B包含在A内
                overlappingHeight = heightB;
            }
        } else {
            if (bottomA <= bottomB) { // A下B上重叠
                overlappingHeight = upperA - bottomB;
            } else { // A包含在B内
                overlappingHeight = heightA;
            }
        }
        int overlappingWidth = 0;
        if (leftA <= leftB) {
            if (rightA <= rightB) { // A左B右重叠
                overlappingWidth = rightA - leftB;
            } else { // B包含在A内
                overlappingWidth = widthB;
            }
        } else {
            if (rightA >= rightB) { // A右B左重叠
                overlappingWidth = rightB - leftA;
            } else { // A包含在B内
                overlappingWidth = widthA;
            }
        }
        return totalArea - (overlappingHeight * overlappingWidth);
    }
}
```

#### 结果
![rectangle-area-1](/images/leetcode/rectangle-area-1.png)


### 比较聪明的用`Math.max()`和`Maht.min()`去比较
计算重叠部分的面积可以聪明一点。
* 重叠部分的底边，永远是`A`的底边和`B`的底边中，较高的那个。
* 重叠部分的顶边，永远是`A`的顶边和`B`的顶边中，较低的那个。
* 重叠部分的左边，永远是`A`的左边和`B`的左边中，较大的那个。
* 重叠部分的右边，永远是`A`的右边和`B`的右边中，较小的那个。

然后，
* 如果重叠底边`>=`重叠顶边，重叠部分面积为零。
* 如果重叠左边`>=`重叠右边，重叠部分面积为零。

#### 代码
```java
public class Solution {
    public int computeArea(int A, int B, int C, int D, int E, int F, int G, int H) {
        int leftA = A, upperA = D, bottomA = B, rightA = C;
        int leftB = E, upperB = H, bottomB = F, rightB = G;
        int areaA = (upperA - bottomA) * (rightA - leftA);
        int areaB = (upperB - bottomB) * (rightB - leftB);
        int overLappingLeft = Math.max(leftA,leftB);
        int overLappingRight = Math.min(rightA,rightB);
        int overLappingUpper = Math.min(upperA,upperB);
        int overLappingBottom = Math.max(bottomA,bottomB);
        int overlappingHeight = (overLappingUpper > overLappingBottom)? (overLappingUpper - overLappingBottom) : 0;
        int overlappingWidth = (overLappingRight > overLappingLeft)? (overLappingRight - overLappingLeft) : 0;
        int areaOverlapping = overlappingHeight * overlappingWidth;
        return areaA + areaB - areaOverlapping;
    }
}
```

#### 结果
![rectangle-area-2](/images/leetcode/rectangle-area-2.png)
