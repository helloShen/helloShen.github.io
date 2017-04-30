---
layout: post
title: "Leetcode - Algorithm - Subsets Two "
date: 2017-04-30 16:32:35
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["backtracking","array"]
level: "medium"
description: >
---

### 题目
Given a collection of integers that might contain duplicates, nums, return all possible subsets.

Note: The solution set must not contain duplicate subsets.

For example,
If nums = `[1,2,2]`, a solution is:
```
[
  [2],
  [1],
  [1,2,2],
  [2,2],
  [1,2],
  []
]
```

### 暴力回溯（具有动态规划思想），利用`Set`去重
老规矩，在每遇到一个数字的时候，都可以决策`添加`和`不添加`。最后问题就抽象成，一系列这样的决策。

所以每次往后新加入一个数字，就拷贝一遍原数组，在每个元素后插入新数字。比如，`1,2,2`,
```
[]
```
读入`1`,
```
[]
[1]
```
读入第一个`2`,
```
[]
[1]
----------------
[2] -> 在[]后面插入2
[1,2] -> 在[1]后面插入2
```

这么做肯定有重复元素，利用`Set`去重。

#### 代码
```java
public class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        Set<List<Integer>> res = new HashSet<>();
        res.add(new ArrayList<Integer>());
        for (int i = 0; i < nums.length; i++) {
            List<List<Integer>> mirror = new ArrayList<>(res);
            for (List<Integer> list : mirror) {
                list.add(nums[i]);
                res.add(new ArrayList<Integer>(list));
                list.remove(list.size()-1);
            }
        }
        return new ArrayList<List<Integer>>(res);
    }
}
```

#### 结果
![subsets-two-1](/images/leetcode/subsets-two-1.png)


### 遇到重复的数字只在部分元素后面插入重复数字
还是刚才`1,2,2`的例子，插入第一个`2`以后，数组是下面这样子，
```
[]
[1]                             # 上半区
-----------------------------------------  需要记住这个分界点
[2] -> 在[]后面插入2              # 下半区
[1,2] -> 在[1]后面插入2
```
遇到第二个`2`时，因为前面已经插入过`2`，所以在上半区`[]`，`[1]`后面插入`2`的动作已经做过，再插入就是重复。 只需要在下半区`[2]`和`[1,2]`的后面插入`2`就可以。
```
[]
[1]                             # 上半区
-----------------------------------------  需要记住这个分界点
[2] -> 在[]后面插入2              # 下半区
[1,2] -> 在[1]后面插入2
-----------------------------------------
[2,2] -> 对下半区元素后插入2
[1,2,2] -> 对下半区元素后插入2
```

关键就是每次循环插入新元素之前，都要记住老的`List`的边界。

#### 代码
```java
public class Solution {
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        res.add(new ArrayList<Integer>());
        for (int i = 0, start = 0; i < nums.length; i++) {
            if (i == 0 || nums[i] != nums[i-1]) { start = 0; } // append nums[i] to each old member
            int size = res.size();
            for (int j = start; j < size; j++) {
                List<Integer> temp = new ArrayList<>(res.get(j));
                temp.add(nums[i]);
                res.add(temp);
            }
            start = size; // next iteration begin after the last member
        }
        return res;
    }
}
```

#### 结果
![subsets-two-2](/images/leetcode/subsets-two-2.png)
