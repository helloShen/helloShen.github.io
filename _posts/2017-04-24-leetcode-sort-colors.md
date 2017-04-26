---
layout: post
title: "Leetcode - Algorithm - Sort Colors "
date: 2017-04-24 17:31:39
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","two pointers","sort"]
level: "medium"
description: >
---

### 主要收获 1
在排序或者分类的时候，分别放在数组的两头，比都挤在一起更简单。

### 主要收获 2
`Two Pointers`这类问题的关键之一是：要明确指针的责任。不要跳来跳去。比如这题，`lt`表示`<target`的数的边界，`gt`表示`>target`的数的边界，那么在每次迭代的时候就一定要保证他们确实尽到他们的职责。

### 主要收获 3
当逻辑比较复杂的时候，与其一上来就想写出最简化的逻辑流，不如说先老老实实一步一步条件判断下来。先保证跑起来，再做优化。


### 题目
Given an array with n objects colored red, white or blue, sort them so that objects of the same color are adjacent, with the colors in the order red, white and blue.

Here, we will use the integers 0, 1, and 2 to represent the color red, white, and blue respectively.

Note:
You are not suppose to use the library's sort function for this problem.

### `Three Way Partition`问题
把这个问题推广一下就是标准的`Three Way Partition`。 只是其取值空间只有`0,1,2`，且以`1`为分界值。假设有如下数组，
```
[0, 1, 2, 2, 2, 0, 0, 1]
```
整理之后就分成三部分`<1`,`=1`,`>1`。
```
[0, 0, 0, 1, 1, 2, 2, 2]
```

#### 代码
自己瞎写的版本。
`cursor`遍历数组，交换元素位置。然后维护两个指针，
* `firstEqual`指向第一个`=target`的数的位置。（没有就为`-1`）
* `firstGreater`指向第一个`>target`的数的位置。（没有就为`-1`）

和标准版最大的区别是，我的`<target`,`=target`,`>target`的三部分数字，都维护在数组的左边。而标准版，`<target`和`=target`在数组左边，而`>target`在数组的右边。所以我的逻辑比标准版复杂很多。但工作还是能工作的。
```java
public class Solution {
    public void sortColors(int[] colors) {
        threeWayPartition(colors,1);
    }
    public void threeWayPartition(int[] nums, int target) {
        int firstEqual = -1, firstGreater = -1; //维护两个指向三区块间边界的指针
        for (int i = 0; i < nums.length; i++) {
            if (firstGreater < 0 && nums[i] > target) {
                firstGreater = i; continue;
            }
            if (nums[i] == target) {
                if (firstGreater >= 0) {
                    exchange(nums,firstGreater,i);
                    if (firstEqual < 0) { firstEqual = firstGreater; }
                    firstGreater++;
                } else if (firstEqual < 0){
                    firstEqual = i;
                }
            }
            if (nums[i] < target) {
                int tempCursor = i; // 标明需要交换的位置
                if (firstGreater >= 0) {
                    exchange(nums,firstGreater,tempCursor);
                    tempCursor = firstGreater;
                    firstGreater++;
                }
                if (firstEqual >= 0) {
                    exchange(nums,firstEqual,tempCursor);
                    firstEqual++;
                }
            }
        }
    }
    public void exchange(int[] nums, int first, int second) {
        int temp = nums[first];
        nums[first] = nums[second];
        nums[second] = temp;
    }
}
```

#### 结果
![sort-colors-1](/images/leetcode/sort-colors-1.png)


### 标准`Three Way Partition`, 数组分两头维护
![three-way-partition-1](/images/leetcode/three-way-partition-1.png)
如上图所示，`i`遍历数组，整个数组被三个指针`lt`,`gt`,`i`分成4部分。
1. `lt`指向最后一个确定`< target`的数。
2. `gt`指向第一个确定`> target`的数。
3. `i`和`lt`之间是`= target`的数。
4. `i`和`gt`之间是还没有遍历到的数。

`lt`初始化位置在左端`0`.`gt`初始位置在右端`length-1`。当`i`和`gt`交叉的时候，结束迭代。

下面两张图是两个带例子的演示，
![three-way-partition-2](/images/leetcode/three-way-partition-2.png)
![three-way-partition-3](/images/leetcode/three-way-partition-3.png)

#### 代码
这是我的版本。
```java
public void threeWayPartition(int[] nums, int target) {
    int lessThan = 0, greaterThan = nums.length-1, cursor = lessThan + 1;
    while (cursor <= greaterThan) {
        if (nums[cursor] < target) {
            exchange(nums,lessThan++,cursor);
            if (lessThan >= cursor) { cursor++; } // 注意这步，和下面简化版的区别
        } else if (nums[cursor] > target) {
            exchange(nums,greaterThan--,cursor);
        } else {
            cursor++;
        }
    }
}
```
#### 简化版
这段是根据《Algorithm 4th Edition》书里的标准版后的优化版。

只是一个细微的优化。就是当`nums[cursor] < target`的时候，可以在左移`lessThan`的同时，也直接左移`cursor`。因为如果`lessThan`和`cursor`之间有`=target`的数，则替换出来的是`=target`的数，导致`cursor`左移。如果没有，因为`lessThan`和`cursor`重叠，`cursor`也要被挤到左移一格。
```java
public class Solution {
    public void sortColors(int[] colors) {
        threeWayPartition(colors,1);
    }
    public void threeWayPartition(int[] nums, int target) {
        int lessThan = 0, greaterThan = nums.length-1, cursor = lessThan + 1;
        while (cursor <= greaterThan) {
            if (nums[cursor] < target) {
                exchange(nums,++lessThan,cursor++); // 简化在这里，因为不论有没有等于target的数，这里cursor都是加1的。
            } else if (nums[cursor] > target) {
                exchange(nums,--greaterThan,cursor);
            } else {
                cursor++;
            }
        }
    }
    public void exchange(int[] nums, int first, int second) {
        int temp = nums[first];
        nums[first] = nums[second];
        nums[second] = temp;
    }
}
```

#### 结果
![sort-colors-2](/images/leetcode/sort-colors-2.png)


### 红白蓝，专供版

#### 代码
```java
public class Solution {
    public void sortColors(int[] nums) {
        int zero = 0, two = nums.length-1, cursor = 0;
        while (cursor <= two) {
            switch (nums[cursor]) {
                case 0:
                    exchange(nums,zero++,cursor++); break;
                case 1:
                    cursor++; break;
                case 2:
                    exchange(nums,cursor,two--); break;
            }
        }
    }
    public void exchange(int[] nums, int first, int second) {
        int temp = nums[first];
        nums[first] = nums[second];
        nums[second] = temp;
    }
}
```

#### 结果
![sort-colors-3](/images/leetcode/sort-colors-3.png)
