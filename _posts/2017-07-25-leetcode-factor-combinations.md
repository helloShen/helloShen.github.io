---
layout: post
title: "Leetcode - Algorithm - Factor Combinations "
date: 2017-07-25 15:48:56
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["backtracking","dynamic programming"]
level: "medium"
description: >
---

### 题目
Numbers can be regarded as product of its factors. For example,
```
8 = 2 x 2 x 2;
  = 2 x 4.
```
Write a function that takes an integer n and return all possible combinations of its factors.

Note:
You may assume that n is always positive.
Factors should be greater than 1 and less than n.
Examples:
input: `1`
output:
```
[]
```
input: `37`
output:
```
[]
```
input: `12`
output:
```
[
  [2, 6],
  [2, 2, 3],
  [3, 4]
]
```
input: `32`
output:
```
[
  [2, 16],
  [2, 2, 8],
  [2, 2, 2, 4],
  [2, 2, 2, 2, 2],
  [2, 4, 4],
  [4, 8]
]
```

### 自底向上的动态规划
明显地比如`32 = 2 * 16`，可以先确定一组解`[2,16]`，以及`[2,子问题(16)]`。以此类推。

有一个小窍门可以大幅提高效率，就是`>sqrt(n)`的下半区数字全部重复。所以可以只循环到`<=sqrt(n)`为止。
```
32 = 2 * 16
32 = 4 * 8
----------------
32 = 8 * 4      // 下半区，重复
32 = 16 * 2     // 下半区，重复
```

这方法的第二个问题是： 还是会有很多重复解。需要用`Set`去重。但另一个方面，有重复解就代表重复解决了很多次子问题。但这个方法依然高效就是因为 **用一个备忘录记录了过去处理过的子问题的解**，省去了很多重复劳动。

#### 代码
```java
public class Solution {
    private Map<Integer,Set<List<Integer>>> memo = new HashMap<>();
    public List<List<Integer>> getFactors(int n) {
        Set<List<Integer>> result = memo.get(n);
        if (result != null) {
            ArrayList<List<Integer>> resultList = new ArrayList<>();
            resultList.addAll(result);
            return resultList;
        }
        result = new HashSet<>();
        for (int i = 2; i <= (int)Math.sqrt(n); i++) { // 这里开方，省去很多重复操作
            if ((n % i) == 0) {
                int quotien = n / i;
                Integer[] nums = new Integer[]{i,quotien};
                Arrays.sort(nums);
                result.add(new ArrayList<Integer>(Arrays.asList(nums)));
                List<List<Integer>> sublist = getFactors(quotien);
                for (List<Integer> factors : sublist) {
                    List<Integer> localFactors = new ArrayList<>(factors);
                    localFactors.add(i);
                    Collections.sort(localFactors);
                    result.add(localFactors);
                }
            }
        }
        memo.put(n,result);
        ArrayList<List<Integer>> resultList = new ArrayList<>();
        resultList.addAll(result);
        return resultList;
    }
}
```

#### 结果
![factor-combinations-1](/images/leetcode/factor-combinations-1.png)


### 回溯算法
这题用回溯算法可以不产生重复解。首先，和前面一样，只递归到`sqrt(n)`。第二，用一个`start`避免重新分解成之前分解过的小因数。还是以`32`为例，

按照`DFS`顺序的回溯算法，第一个解是：`[2,2,2,2,2]`，然后是`[2,2,2,4]`，以此类推。

后来某个时刻我会处理`[4,子问题(8)]`。这时候对`子问题(8)`来讲当然包括一个解`[2,2,2]`，最终会得到`[4,2,2,2]`，但这是重复解。因为之前在处理`[2,子问题(16)]`的时候，所有包含因数`2`的解我都有了。所以避免重复的一个重要原则是在处理`[4,子问题(8)]`的时候，对`子问题(8)`可以跳过所有的`2`，直接从`4`开始检查。这就是下面代码中参数`start`的作用。

#### 代码
```java
public class Solution {
    public List<List<Integer>> getFactors(int n) {
        List<List<Integer>> result = new ArrayList<>();
        backtracking(n,2,new ArrayList<Integer>(),result);
        return result;
    }
    public void backtracking(int n, int start, List<Integer> path, List<List<Integer>> result) {
        if (n < 2) {
            if (!path.isEmpty()) { result.add(new ArrayList<Integer>(path)); } return;
        }
        for (int i = start; i <= (int)Math.sqrt(n); i++) { // start 和 sqrt 是去重的关键
            if ((n % i) == 0) {
                int quotien = n / i;
                path.add(i);
                backtracking(quotien,i,path,result);
                path.add(quotien);
                result.add(new ArrayList<>(path));
                path.remove(path.size()-1);
                path.remove(path.size()-1);
            }
        }
    }
}
```

#### 结果
竟然比动态规划效果都好。
![factor-combinations-2](/images/leetcode/factor-combinations-2.png)
