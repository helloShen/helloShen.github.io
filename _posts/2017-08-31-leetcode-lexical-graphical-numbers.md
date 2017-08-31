---
layout: post
title: "Leetcode - Algorithm - Lexical Graphical Numbers "
date: 2017-08-31 14:26:15
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dfs"]
level: "medium"
description: >
---

### 题目
Given an integer n, return 1 - n in lexicographical order.

For example, given 13, return: `[1,10,11,12,13,2,3,4,5,6,7,8,9]`.

Please optimize your algorithm to use less time and space. The input size may be as large as `5,000,000`.

### 把数字转换成字符，然后排序，$$O(n\log_{}{n})$$
用`Colletcions.sort()`排序。写一个`Comparator`，把数字转换成字符，然后比较。

#### 代码
```java
class Solution {
    private static final int INT_SIZE = 10;
    private static int[] ca = new int[INT_SIZE];
    private static int[] cb = new int[INT_SIZE];
    public List<Integer> lexicalOrder(int n) {
        List<Integer> res = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            res.add(i);
        }
        Collections.sort(res,new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                int pa = parse(ca,a);
                int pb = parse(cb,b);
                while (pa < INT_SIZE && pb < INT_SIZE) {
                    int x = ca[pa++];
                    int y = cb[pb++];
                    if (x < y) {
                        return -1;
                    } else if (x > y) {
                        return 1;
                    }
                }
                if (pa < INT_SIZE) { return 1; }    // a is longer
                if (pb < INT_SIZE) { return -1; }   // b is longer
                return 0;
            }
        });
        return res;
    }
    private int parse(int[] c, int n) {
        int pa = INT_SIZE;
        while (n > 0) {
            c[--pa] = n % 10;
            n /= 10;
        }
        return pa;
    }
}
```

#### 结果
![lexical-graphical-numbers-1](/images/leetcode/lexical-graphical-numbers-1.png)


### 不转化成字符，左对齐数字然后比较大小，$$O(n\log_{}{n})$$
比如`2 < 111`。但把他们左对齐以后，就是我们要的顺序了。    
```
2    -> 20000000000
111  -> 11100000000
```
但需要注意`1`和`10`和`100`这种相等的情况，这时候，因为原始数字的顺序，`1`在`10`的前面，`10`在`100`的前面，所以，最后他们还是保持这种相对顺序。
```
1    -> 10000000000
10   -> 10000000000
100  -> 10000000000
```

#### 代码
```java
class Solution {
    public List<Integer> lexicalOrder(int n) {
        List<Integer> res = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            res.add(i);
        }
        Collections.sort(res,new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                long diff = leftAlign(a) - leftAlign(b);
                if (diff < 0) {
                    return -1;
                } else if (diff > 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return res;
    }
    private final long STD = 10000000000L;
    private long leftAlign(int n) {
        long l = (long)n;
        while (true) {
            long next = l * 10;
            if (next < STD) {
                l = next;
            } else {
                break;
            }
        }
        return l;
    }
}
```

#### 结果
![lexical-graphical-numbers-2](/images/leetcode/lexical-graphical-numbers-2.png)


### 有限状态机逐个生成数字，$$O(n)$$
先用一个数组，列出一组数字，比如`1,10,100...`。用一个指针指向这个数组中的某元素。然后用有限状态机的方式，让指针在数组的元素间跳跃，逐个递增桶中的元素。

不推荐这种有限状态机的做法，逻辑比较乱，运气好可以写对，但还是比较容易出错。

#### 代码
```java
class Solution {
    private static List<Integer> res = new ArrayList<>();
    private static int[] board = new int[10];
    public List<Integer> lexicalOrder(int n) {
        res.clear();
        for (int i = 1; i < 10; i++) {
            int bp = -1;
            for (int j = i; j <= n; j *= 10) {
                res.add(j);
                board[++bp] = j+1;
            }
            int maxbp = bp;
            while (bp > 0) {
                if (board[bp] / 10 < board[bp-1]) {                         // 检查进位
                    if (bp < maxbp) {                                       // 最后一个计数器
                        res.add(board[bp]++);
                        if (maxbp - bp > 1 || board[maxbp] <= n) { bp++; }  // 最后一个计数器到了最大值，不再往后进一步
                    } else if (board[bp] <= n) {                            // 非最后一个计数器
                        res.add(board[bp]++);
                    } else {
                        bp--;
                    }
                } else {                                                    // 进位
                    bp--;
                }
            }
        }
        return res;
    }
}
```

#### 结果
![lexical-graphical-numbers-3](/images/leetcode/lexical-graphical-numbers-3.png)

### 标准DFS解法，$$O(n)$$
这题的标准解法是DFS递归。考虑`1`的情况，相比于加一变成`11`，优先考虑乘以十变成`10`。如果加一操作是广度上的操作，乘以十是深度上的操作，这个算法就是典型的深度优先遍历一棵树的情况。
```bash
0
1 -> 10 -> 100
2    11    101
3    12    102
.    .     .
9    19    109
```
注意跳过初始`0`的边角情况。

#### 代码
```java
class Solution {
    private static List<Integer> list = new ArrayList<>();
    private static int max = 0;
    public List<Integer> lexicalOrder(int n) {
        list.clear();
        max = n;
        dfs(0);
        return list;
    }
    private void dfs(int seed) {
        for (int i = 0; i < 10; i++) {
            if (seed == 0 && i == 0) { continue; } // 跳过0的情况
            int num = seed + i;
            if (num > max) { return; }
            list.add(num);
            dfs(num * 10);
        }
    }
}
```

#### 结果
![lexical-graphical-numbers-4](/images/leetcode/lexical-graphical-numbers-4.png)
