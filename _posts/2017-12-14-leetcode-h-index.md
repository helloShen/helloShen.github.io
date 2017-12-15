---
layout: post
title: "Leetcode - Algorithm - H Index "
date: 2017-12-14 20:29:52
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","math"]
level: "medium"
description: >
---

### 题目
Given an array of citations (each citation is a non-negative integer) of a researcher, write a function to compute the researcher's h-index.

According to the definition of h-index on Wikipedia: "A scientist has index h if h of his/her N papers have at least h citations each, and the other N − h papers have no more than h citations each."

For example, given citations = `[3, 0, 6, 1, 5]`, which means the researcher has 5 papers in total and each of them had received 3, 0, 6, 1, 5 citations respectively. Since the researcher has 3 papers with at least 3 citations each and the remaining two with no more than 3 citations each, his h-index is 3.

Note: If there are several possible values for h, the maximum one is taken as the h-index.


### 严格按照数学公式，$$O(n\log_{}{n})$$
看下面这个例子和相应解释：
```
  citations
    |
0,1,3,5,6
    |-len-|

解释：
当数组排过序以后，当某篇文章的引用数(例子中=3) >= 从右往左数的这个文章的篇数（例子中用len标出），这个len就就可以拿来做h。

要让这个h最大，其实就是从左往右遍历数组，第一个符合条件的数（因为离数组末尾最远）。
```

#### 代码
```java
class Solution {
    public int hIndex(int[] citations) {
        if (citations == null || citations.length == 0) { return 0; }
        int len = citations.length;
        Arrays.sort(citations);
        for (int i = 0; i < len; i++) {
            int times = citations[i];
            int papers = len - i;
            if (times >= (len - i)) { return papers; }
        }
        return 0;
    }
}
```

#### 结果
![h-index-1](/images/leetcode/h-index-1.png)


### 额外使用一个数组统计文章数，$$O(n)$$
要解决这个问题，首先要好好感受h-index的数学特性。为什么大家要用h-index衡量一个学者的学术水平？

h-index的意义在于它的数学特性，需要文章的“质量”和“数量”两手都要硬：
1. h-index的第一个上限，是他发过文章的质量（引用）。发100篇引用只有10的文章，h-index也只有10.
2. h-index的第二个上限，是他发文章的稳定性。发了100篇文章，只有1篇10000次引用，其他都是10，h-index也只有10.
3. h-index上升的难度是越来越大的。第1篇文章，只要有1个引用，h-index就上升1。如果h-index要上升到100，就需要他至少有100篇文章的引用数大于等于100。

根据h-index的这些特点，可以设计一个数组，数组的下标就是文章的引用次数，每个桶里的数字就是这个引用次数的文章的数量。那么引用数大于等于某个数的文章篇数，就等于这个下标及其之后的所有桶内数字之和。

但这样做有一个缺点就是：需要一个很大的数组。因为文章引用最高可以有几万次。这里可以利用h-index的上限是作者发表文章总数这个特点。因为一个学者最多也就发表几百篇文章。然后所有引用超过文章总数的文章全部统计在最后的那个桶里。

#### 代码
```java
class Solution {
    public int hIndex(int[] citations) {
        if (citations == null || citations.length == 0) { return 0; }
        int len = citations.length;
        int[] freq = new int[len+1];
        for (int i = 0; i < len; i++) {
            if (citations[i] >= len) {
                freq[len]++;
            } else {
                freq[citations[i]]++;
            }
        }
        int sum = 0;
        for (int i = len; i >= 0; i--) {
            sum += freq[i];
            if (sum >= i) {
                return i;
            }
        }
        return 0;
    }
}
```

#### 结果
![h-index-2](/images/leetcode/h-index-2.png)
