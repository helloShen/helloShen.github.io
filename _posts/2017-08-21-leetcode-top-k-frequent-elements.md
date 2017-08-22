---
layout: post
title: "Leetcode - Algorithm - Top K Frequent Elements "
date: 2017-08-21 21:07:58
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","heap"]
level: "medium"
description: >
---

### 题目
Given a non-empty array of integers, return the k most frequent elements.

For example,
Given `[1,1,1,2,2,3]` and k = 2, return `[1,2]`.

Note:
You may assume k is always valid, 1 ? k ? number of unique elements.
Your algorithm's time complexity must be better than O(n log n), where n is the array's size.


### 基本思路
首先，得用一个`Map`统计所有数字的出现频率。

接下来，朴素的做法，是对这个`Map`中的频率排序。这个过程会是 $$O(n\log_{}{n})$$的。

使用数组，数组的下标代表频率，可以不用排序。因为可能会出现很多数字频率相同的情况，实际需要一个二维数组，每个频率下都需要好几个桶。这个方法的复杂度是 $$O(n)$$。

### 方法一：用`Map`统计频率，然后给`Map`的频率排序

#### 代码
```java
class Solution {
    public List<Integer> topKFrequent(int[] nums, int k) {
        Map<Integer,Integer> freq = new HashMap<>();
        for (int n : nums) {
            freq.put(n,(freq.containsKey(n))? freq.get(n)+1 : 1);
        }
        List<Map.Entry<Integer,Integer>> list = new ArrayList<>(freq.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer,Integer>>(){
            public int compare(Map.Entry<Integer,Integer> a, Map.Entry<Integer,Integer> b) {
                int freqA = a.getValue();
                int freqB = b.getValue();
                if (freqA > freqB) { // 降序
                    return -1;
                } else if (freqA < freqB) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        List<Integer> res = new ArrayList<>();
        for (Map.Entry<Integer,Integer> entry : list) {
            if (k > 0) {
                res.add(entry.getKey());
                --k;
            } else {
                break;
            }
        }
        return res;
    }
}
```

#### 结果
![top-k-frequent-elements-1](/images/leetcode/top-k-frequent-elements-1.png)


### 方法二：不排序，数组存放频率信息

#### 二维数组储存统计好的频率信息

```java
public class Solution {
    public List<Integer> topKFrequent(int[] nums, int k) {
        int len = nums.length;
        Map<Integer,Integer> freq = new HashMap<>();
        for (int n : nums) {
            Integer f = freq.get(n);
            freq.put(n,(f == null)? 1 : f + 1);
        }
        Integer[][] matrix = new Integer[len+1][len+1]; // 行下标表示频率。每行最后一列表示指向当前行元素的指针。
        for (Integer[] numArray : matrix) {
            numArray[len] = 0;
        }
        for (Map.Entry<Integer,Integer> entry : freq.entrySet()) {
            int f = entry.getValue();
            matrix[f][matrix[f][len]++] = entry.getKey();
        }
        List<Integer> res = new ArrayList<>();
        for (int i = len; i >= 0; i--) {
            Integer[] numArray = matrix[i];
            for (Integer num : numArray) {
                if (k > 0) {
                    if (num != null) {
                        res.add(num); --k;
                    } else {
                        break;
                    }
                } else {
                    return res;
                }
            }
        }
        return res;
    }
}
```

#### `List[]`存放频率信息
注意，Java不能创建泛型数组。但可以先创建带有通配符的数组，然后在转型。
```java
class Solution {
    public List<Integer> topKFrequent(int[] nums, int k) {
        Map<Integer,Integer> freq = new HashMap<>();
        for (int num : nums) {
            Integer f = freq.get(num);
            freq.put(num,(f == null)? 1 : f + 1);
        }
        // 不能创建泛型数组。折中方案是先使用通配符，然后转型。
        // 但这样仍然是类型不安全的，需要用@SuppressWarnings去掉警告
        @SuppressWarnings("unchecked")
        List<Integer>[] matrix = (List<Integer>[])new List<?>[nums.length+1];
        for (Map.Entry<Integer,Integer> entry : freq.entrySet()) {
            Integer f = entry.getValue();
            if (matrix[f] == null) { matrix[f] = new ArrayList<Integer>(); }
            matrix[f].add(entry.getKey());
        }
        List<Integer> res = new ArrayList<>();
        for (int i = nums.length; i >= 0; i--) {
            if (matrix[i] != null) {
                for (Integer num : matrix[i]) {
                    if (k > 0) {
                        res.add(num); --k;
                    } else {
                        return res;
                    }
                }
            }
        }
        return res;
    }
}
```

#### 结果
![top-k-frequent-elements-2](/images/leetcode/top-k-frequent-elements-2.png)


### heap

#### `PriorityQueue`标准的`Heap`实现
插入新元素`add()`方法的调用链：
> `add()`->`offer()`->`siftUp()`->`siftUpUsingComparator()`

实际负责插入新元素的是`siftUpUsingComparator()`方法（用户提供Comparator的情况）。可以看出`PriorityQueue`是一棵二叉树。`add()`方法的复杂度是 $$O(n\log_{}{n})$$。
```java
private void siftUpUsingComparator(int k, E x) {
    while (k > 0) {
        int parent = (k - 1) >>> 1; // 二叉树定位父节点
        Object e = queue[parent];
        if (comparator.compare(x, (E) e) >= 0)
            break;
        queue[k] = e;
        k = parent;
    }
    queue[k] = x;
}
```
