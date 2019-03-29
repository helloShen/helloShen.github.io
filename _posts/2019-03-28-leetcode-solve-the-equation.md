---
layout: post
title: "Leetcode - Algorithm - Solve The Equation "
date: 2019-03-28 21:30:47
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["arrays", "math"]
level: "medium"
description: >
---

### 题目
Solve a given equation and return the value of x in the form of string "x=#value". The equation contains only '+', '-' operation, the variable x and its coefficient.

If there is no solution for the equation, return "No solution".

If there are infinite solutions for the equation, return "Infinite solutions".

If there is exactly one solution for the equation, we ensure that the value of x is an integer.

Example 1:
```
Input: "x+5-3+x=6+x-2"
Output: "x=2"
```

Example 2:
```
Input: "x=x"
Output: "Infinite solutions"
```

Example 3:
```
Input: "2x=x"
Output: "x=0"
```

Example 4:
```
Input: "2x+3x-6x=x+2"
Output: "x=-1"
```

Example 5:
```
Input: "x=x+2"
Output: "No solution"
```

### 全部规整化成`ax + b`的形式
因为只是一次元多项式，所以无论是`x+5-3+x`还是`6+x-2`都可以转化成`ax + b`的形式。最后两边一定能转化成，
> a1 * x + b1 = a2 * x + b2

最后两边移项，一定能变成，
> ax = b

此时，只有一种情况会是无穷多解：
> a == 0， 且 b == 0 （即 0 = 0 的形式）

无解也只有一种情况，
> a == 0， 且 b != 0 （即 0 = 1 的形式）

剩下的情况都只有唯一解，即`b / a`。

#### 代码
```java
class Solution {
    public String solveEquation(String equation) {
        int[][] coefficients = new int[2][2];
        String[] twoParts = equation.split("=");
        for (int i = 0; i < 2; i++) coefficients[i] = parseHalfEquation(twoParts[i]);
        int coeffX = coefficients[0][0] - coefficients[1][0];
        int coeffConst = coefficients[1][1] - coefficients[0][1];
        if (coeffX == 0) {
            return (coeffConst == 0)? "Infinite solutions" : "No solution";
        } else {
            return "x=" + (coeffConst / coeffX);
        }
    }

    private int[] parseHalfEquation(String half) {
        int[] ab = new int[2];
        char[] arr = half.toCharArray();
        int p = 0;
        while (p < arr.length) {
            int start = p;
            if (arr[p] == '-' || arr[p] == '+') p++;
            while (p < arr.length && arr[p] != '-' && arr[p] != '+') p++;
            int isX = 1, end = p;
            if (arr[p - 1] == 'x') {
                isX = 0;
                end = p - 1;
                if (start == end || ((start + 1 == end) && arr[start] == '+')) {
                    ab[0]++;
                    continue;
                } else if ((start + 1 == end) && arr[start] == '-') {
                    ab[0]--;
                    continue;
                }
            }
            int val = Integer.parseInt(new String(Arrays.copyOfRange(arr, start, end)));
            ab[isX] += val;
        }
        return ab;
    }
}
```

#### 结果
![solve-the-equation-1](/images/leetcode/solve-the-equation-1.png)
