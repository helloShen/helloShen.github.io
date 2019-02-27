---
layout: post
title: "Leetcode - Algorithm - Random Pick Index "
date: 2019-02-04 20:39:48
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash map", "math"]
level: "medium"
description: >
---

### 题目
Given an array of integers with possible duplicates, randomly output the index of a given target number. You can assume that the given target number must exist in the array.

Note:
The array size can be very large. Solution that uses too much extra space will not pass the judge.

Example:
```
int[] nums = new int[] {1,2,3,3,3};
Solution solution = new Solution(nums);

// pick(3) should return either index 2, 3, or 4 randomly. Each index should have equal probability of returning.
solution.pick(3);

// pick(1) should return 0. Since in the array only nums[0] is equal to 1.
solution.pick(1);
```

### 直观解法
首先例子给地不好，`[1,2,3,3,3]`给人造成一种数字是有序排列的错觉。实际上不是的，可以打乱来看`[3,1,3,2,3]`，如果要查的数字是`3`应该等概率返回`0,2,4`中的一个。

最直观的做法可以预先遍历整个数组，把每个数字的位置都记录下来，比如我们的例子，
```
[3,1,3,2,3]

1 -> 1
2 -> 3
3 -> 0,2,4
```

然后把这样一个表储存在`Map`里，只要`O(1)`的时间就可以查到目标数字所有位置，然后从中随机返回一个。

但这题不让用太多内存，也就是这种预先储存偏移值的方法不可行。

### 蓄水池抽样（Reservoir Sampling）
不预先统计信息，只有在每次查询的时候都遍历整个数组。这要用到 **“蓄水池抽样”**。蓄水池抽样简单讲是这样，

假设我需要决定今天戴哪一顶帽子，但我不知道我一共有多少顶帽子，甚至帽子的数量都不由我自己决定，可能有源源不断的帽子朝我送过来。我能做的就是在没一定帽子到我面前的时候，决定戴或者不带。怎样能在这样一个过程中，无论何时停下，都保证每顶帽子都拥有同样的被选中的概率？

蓄水池抽样是这样做的：
```
当第一顶帽子来的时候：我只有1顶帽子，我以1/1的概率选择戴这顶帽子。
当第二顶帽子来的时候：我有2顶帽子，我以1/2的概率选择戴这顶帽子（换掉之前的帽子）。
当第三顶帽子来的时候：我有3顶帽子，我以1/3的概率选择戴这顶帽子（换掉之前的帽子）。
...
当第N顶帽子来的时候：我有N顶帽子，我以1/N的概率选择戴这顶帽子（换掉之前的帽子）。
```

看上去每一顶帽子被选中的概率都不一样，但因为每次新帽子来，都有一定概率换掉之前的帽子，因此，之前帽子被选中的概率虽然比较大，但每次新帽子来都在被稀释。整个过程动态地保持每一顶帽子被选中的概率相等。

数学证明：
> 第m个对象最终被选中的概率P=选择m的概率*其后面所有对象不被选择的概率

![reservoir-sampling](/images/leetcode/reservoir-sampling.gif)


应用到这个，我们遍历整个数组，只要遇到和目标数字相等的数，我们就以`1/N`的概率用当前偏移值替换之前选中的偏移值。过程中我们不断递增这个`N`的值。

Youtube有一个很有意思的视频：[**Reservoir Sampling**](https://www.youtube.com/watch?v=A1iwzSew5Q)，解释地非常清楚。

#### 代码
```java
class Solution {

    private int[] nums;
    private Random r;

    public Solution(int[] nums) {
        this.nums = nums;
        r = new Random();
    }

    public int pick(int target) {
        int res = -1;
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                if (r.nextInt(++count) == count - 1) res = i;
            }
        }
        return res;
    }

}
```

#### 结果
![random-pick-index-1](/images/leetcode/random-pick-index-1.png)
