---
layout: post
title: "Leetcode - Algorithm - Search Insert Position "
date: 2017-04-04 15:14:00
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","binary search"]
level: "easy"
description: >
---

### 主要收获1
事不过三。以后一个问题可以错一次，可以错两次，绝不可以再错第三次。宁愿花点时间彻底搞明白，不能稀里糊涂地过去。

### 主要收获2
从`Divide Two Integer`开始，到`Search for a Range`,`Search in Rotated Sorted Array`，再到今天的`Search Insert Position`连续的二分查找问题。写了好几遍不同的实现，也都Accepted了，但离最优实现总是还差一点。

痛定思痛，对二分查找做了一个专题考察。写了[ **《二分查找总结帖》** ](http://www.ciaoshen.com/algorithm/2017/03/16/binary-search.html)。
知乎上上回答了[ **《二分查找有几种写法？》** ](https://www.zhihu.com/question/36132386/answer/155438728) 这个问题。
看了《编程珠玑》里《怎样编写正确的代码？》这一章。了解了如何利用“循环不变性”以及“断言”检验程序正确性。

### 题目
Given a sorted array and a target value, return the index if the target is found. If not, return the index where it would be if it were inserted in order.

You may assume no duplicates in the array.

Here are few examples.
```
[1,3,5,6], 5 → 2
[1,3,5,6], 2 → 1
[1,3,5,6], 7 → 4
[1,3,5,6], 0 → 0
```

### 递归二分查找 $$O(\log_{}{n})$$
这题很重要。标准的经典二分查找！标准二分查找包含以下两条要求：
1. 如果表中存在目标数，返回目标数在表中的下标。
2. 如果表中不存在目标数，返回表中小于目标数的元素的数量。（和返回元素应该插入的位置是一回事）
3. 假设表中没有重复的数字。

#### 代码
注意！这个版本不是最优的解法。只是我写的第一版。最优的解法在最后。
```java
public class Solution {
    public int searchInsert(int[] nums, int target) {
        if (nums.length == 0) { return 0; }
        return searchInsertRecur(nums,target,0,nums.length-1);
    }
    public int searchInsertRecur(int[] nums, int target, int low, int high) {
        if (low >= high) {
            return (nums[low] >= target)? low : ++low;
        }
        int median = low + ( (high - low) >> 1 );
        if (nums[median] < target) {
            return searchInsertRecur(nums,target,median+1,high);
        } else if (nums[median] > target) {
            return searchInsertRecur(nums,target,low,median-1);
        } else {
            return median;
        }
    }
}
```

#### 结果
银弹！
![search-insert-position-1](/images/leetcode/search-insert-position-1.png)


### 迭代版二分查找 $$O(\log_{}{n})$$
不用递归，迭代的代码看上去更简洁一点。

#### 代码
```java
public class Solution {
    public int searchInsert(int[] nums, int target) {
        if (nums.length == 0) { return 0; }
        int low = 0, high = nums.length-1;
        while (low < high) {
            int median = low + ( (high - low) >> 1 );
            if (nums[median] < target) { low = median + 1; }
            if (nums[median] > target) { high = median - 1; }
            if (nums[median] == target) { return median; }
        }
        return (nums[low] >= target)? low : ++low;
    }
}
```

#### 结果
和递归一样！
![search-insert-position-2](/images/leetcode/search-insert-position-2.png)


### 更简洁的 base case
不把`low == high`作为`base case`。继续往下走一层。把`low > high`作为终结条件。
![search-insert-position-0](/images/leetcode/search-insert-position-0.png)

任何一种情况，都只要返回`low`下标即可。这是对终结条件的一种高度归纳。

仔细观察`low`下标的行为，
> 它的初始值为`0`，而且只有在确定目标数大于某个位置元素之后，`low`才前移到这个位置的下一位，而且永远不会变小。

所以`low`指针最终指向的位置就是我们要找的小于目标数的元素的数量，或者换一种说法，目标元素的位置，或者应该插入的位置。

但也需要注意，未必是每次二分查找都能归纳到这个程度。之前做的例如`Search for a Range`,`Search in Rotated Sorted Array`，能不能把终结条件推迟到`low > high`两者交叉的情况，需要仔细分析实际情况。但往这个方向靠，可以简化代码逻辑。

#### 代码
```java
public class Solution {
    public int searchInsert(int[] nums, int target) {
        int low = 0, high = nums.length-1;
        while (low <= high) {
            int median = low + ( (high - low) >> 1 );
            if (nums[median] < target) { low = median + 1; }
            if (nums[median] > target) { high = median - 1; }
            if (nums[median] == target) { return median; }
        }
        return low;
    }
}
```

#### 结果
![search-insert-position-3](/images/leetcode/search-insert-position-3.png)

### 推广到有重复元素的数组中
标准二分查找推广到有重复元素的空间中，问题就变成了：**查找某元素第一次出现的位置**，或者是 **第一个大于等于目标值的元素位置**。

代码没有变得更复杂，反而更简洁。

#### 迭代版代码
```java
public class Solution {
    public int searchInsert(int[] nums, int target) {
        int low = 0, high = nums.length-1;
        while (low <= high) {
            int mid = low + ( (high - low) >> 1 );
            if (nums[mid] < target) { low = mid + 1; }
            if (nums[mid] >= target) { high = mid - 1; }
        }
        return low;
    }
}
```

#### 递归版代码
```java
public class Solution {
    public int searchInsert(int[] nums, int target) {
        return firstOccurrenceRecur(nums,target,0,nums.length-1);
    }
    public int firstOccurrenceRecur(int[] nums, int target, int low, int high) {
        if (low > high) { return low; }
        int mid = low + ( (high - low) >> 1 );
        if (nums[mid] < target) {
            return firstOccurrenceRecur(nums,target,mid + 1,high);
        } else {
            return firstOccurrenceRecur(nums,target,low,mid-1);
        }
    }
}
```
