---
layout: post
title: "Leetcode - Algorithm - Sparse Matrix Multiplication "
date: 2017-10-10 19:24:03
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Given two sparse matrices A and B, return the result of AB.

You may assume that A's column number is equal to B's row number.


### 矩阵的乘法
`A`和`B`两个矩阵如下图所示，
```
A = [
  [ 1, 0, 0],
  [-1, 0, 3]
]

B = [
  [ 7, 0, 0 ],
  [ 0, 0, 0 ],
  [ 0, 0, 1 ]
]


     |  1 0 0 |   | 7 0 0 |   |  7 0 0 |
AB = | -1 0 3 | x | 0 0 0 | = | -7 0 3 |
                  | 0 0 1 |
```
`AB`内积的`[x,y]`元素，等于`A`矩阵的第`x`行，和`B`矩阵的第`y`列的乘积。比如`AB[1,2] = 3`，
```
 A的1号行     B的2号列
    |         |
[-1,0,3] * [0,0,1]

-1 * 0 + 0 * 0 + 3 * 1 = 3    
```

### 最直接的遍历两个矩阵，逐个元素相乘并求和

#### 代码
```java
class Solution {
    public int[][] multiply(int[][] A, int[][] B) {
        if (A.length == 0 || B.length == 0) { return null; }
        int[][] res = new int[A.length][B[0].length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j <B[0].length; j++) {
                for (int k = 0; k < A[i].length; k++) {
                    res[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return res;
    }
}
```

#### 结果
![sparse-matrix-multiplication-1](/images/leetcode/sparse-matrix-multiplication-1.png)


### 稀疏矩阵大部分元素为零
为了避免把时间浪费在重复遍历0上，最好先把有效的非零元素提取出来。
> A sparse matrix can be represented as a sequence of rows, each of which is a sequence of (column-number, value) pairs of the nonzero values in the row.

还是上面的例子，矩阵`B`就可以被简写成：
```
B = [
  [ 7, 0, 0 ],
  [ 0, 0, 0 ],
  [ 0, 0, 1 ]
]

只提取有效非零元素：
[
  [0,7],
  [2,1]
]
```

#### 代码
下面的代码用`Map`储存压缩以后的非零元素，
```java
class Solution {
        public int[][] multiply(int[][] A, int[][] B) {
            if (A.length == 0 || B.length == 0) { return null; }
            int[][] res = new int[A.length][B[0].length];
            Map<Integer,Map<Integer,Integer>> mapA = new HashMap<>();
            Map<Integer,Map<Integer,Integer>> mapB = new HashMap<>();
            int num = 0;
            for (int i = 0; i < A.length; i++) {            // A的i行
                for (int j = 0; j < A[i].length; j++) {     // A的j列
                    num = A[i][j];
                    if (num != 0) {
                        if (mapA.get(i) == null) { mapA.put(i,new HashMap<Integer,Integer>()); }
                        mapA.get(i).put(j,num);
                    }
                }
            }
            for (int i = 0; i < B[0].length; i++) {         // B的i列
                for (int j = 0; j < B.length; j++) {        // B的j行
                    num = B[j][i];
                    if (num != 0) {
                        if (mapB.get(i) == null) { mapB.put(i,new HashMap<Integer,Integer>()); }
                        mapB.get(i).put(j,num);
                    }
                }
            }
            Map<Integer, Integer> x, y;
            int i = 0;
            for (Map.Entry<Integer,Map<Integer,Integer>> entryA : mapA.entrySet()) {
                x = entryA.getValue();
                int j = 0;
                for (Map.Entry<Integer,Map<Integer,Integer>> entryB : mapB.entrySet()) {
                    y = entryB.getValue();
                    for (Map.Entry<Integer,Integer> entry : x.entrySet()) {
                        int k = entry.getKey();
                        if (y.containsKey(k)) {
                            res[entryA.getKey()][entryB.getKey()] += entry.getValue() * y.get(k);       // A的i行k列 * B的k行j列
                        }
                    }
                    j++;
                }
                i++;
            }
            return res;
        }
}
```

#### 结果
![sparse-matrix-multiplication-2](/images/leetcode/sparse-matrix-multiplication-2.png)


#### 代码
下面代码用数组储存压缩以后的有效元素，
```java
class Solution {
    public int[][] multiply(int[][] A, int[][] B) {
        if (A.length == 0 || B.length == 0) { return null; }
        int[][] res = new int[A.length][B[0].length];
        // 先统计数组的长度
        int ASize = 0;
        for (int i = 0; i < A.length; i++) {
            int count = 0;
            for (int j = 0; j < A[i].length; j++) {
                if (A[i][j] != 0) { count++; }
            }
            ASize = Math.max(ASize,count);
        }
        int BSize = 0;
        for (int i = 0; i < B[0].length; i++) {
            int count = 0;
            for (int j = 0; j < B.length; j++) {
                if (B[j][i] != 0) { count++; }
            }
            BSize = Math.max(BSize,count);
        }
        // 正式开始收集有效元素
        int[][][] ALight = new int[A.length][ASize][2];      // A每行的有效列
        int[][][] BLight = new int[B[0].length][BSize][2];   // B每列的有效行
        int num = 0;
        for (int i = 0; i < A.length; i++) {
            int offset = 0;
            for (int j = 0; j < A[i].length; j++) {
                num = A[i][j];
                if (num != 0) {                     // 记录[列标，值]对
                    ALight[i][offset][0] = j;
                    ALight[i][offset++][1] = num;
                }
            }
            if (offset < ASize) { ALight[i][offset][0] = -1; }
        }
        for (int i = 0; i < B[0].length; i++) {  // B的i列
            int offset = 0;
            for (int j = 0; j < B.length; j++) { // B的j行
                num = B[j][i];
                if (num != 0) {                     // 记录[行标，值]对
                    BLight[i][offset][0] = j;
                    BLight[i][offset++][1] = num;
                }
            }
            if (offset < BSize) { BLight[i][offset][0] = -1; }
        }
        // 在有效元素间开始计算
        for (int i = 0; i < ALight.length; i++) {
            for (int j = 0; j < BLight.length; j++) {
                int curA = 0, curB = 0;
                for (int k = 0; k < A[0].length; k++) {
                    if (curA == ASize || curB == BSize || ALight[i][curA][0] < 0 || BLight[j][curB][0] < 0) { break; }
                    if (ALight[i][curA][0] > BLight[j][curB][0]) {
                        curB++;
                    } else if (ALight[i][curA][0] < BLight[j][curB][0]) {
                        curA++;
                    } else {
                        res[i][j] += (ALight[i][curA++][1] * BLight[j][curB++][1]);
                    }
                }
            }
        }
        return res;
    }
}
```

#### 结果
![sparse-matrix-multiplication-3](/images/leetcode/sparse-matrix-multiplication-3.png)

### 只压缩一个矩阵
如果还要节约时间，可以只压缩一个矩阵，然后遍历第二个矩阵的时候都和第一个压缩过的矩阵数据匹配，

#### 代码
```java
class Solution {
    public int[][] multiply(int[][] A, int[][] B) {
        int m = A.length, n = A[0].length, nB = B[0].length;
        int[][] result = new int[m][nB];

        List[] indexA = new List[m];
        for(int i = 0; i < m; i++) {
            List<Integer> numsA = new ArrayList<>();
            for(int j = 0; j < n; j++) {
                if(A[i][j] != 0){
                    numsA.add(j);
                    numsA.add(A[i][j]);
                }
            }
            indexA[i] = numsA;
        }

        for(int i = 0; i < m; i++) {
            List<Integer> numsA = indexA[i];
            for(int p = 0; p < numsA.size() - 1; p += 2) {
                int colA = numsA.get(p);
                int valA = numsA.get(p + 1);
                for(int j = 0; j < nB; j ++) {
                    int valB = B[colA][j];
                    result[i][j] += valA * valB;
                }
            }
        }

        return result;   
    }
}
```
#### 结果
![sparse-matrix-multiplication-4](/images/leetcode/sparse-matrix-multiplication-4.png)
