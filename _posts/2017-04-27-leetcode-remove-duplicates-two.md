---
layout: post
title: "Leetcode - Algorithm - Remove Duplicates Two "
date: 2017-04-27 17:28:47
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","two pointers"]
level: ""
description: >
---

### 主要收获：关于`Two Pointers`这类问题
利用`pointer`的时候的准则，
1. 明确每个pointer的职责
2. 抽象每个pointer行为的时候，要学会站在每个pointer的视角看问题

### 题目
Follow up for "Remove Duplicates":
What if duplicates are allowed at most twice?

For example,
Given sorted array nums = `[1,1,1,2,2,3]`,

Your function should return length = `5`, with the first five elements of nums being `1, 1, 2, 2` and `3`. It doesn't matter what you leave beyond the new length.

### 直接在数组上改
和不允许重复的`Remove Duplicates`比，算法是一样的，用两个指针，
* `fast`指针负责遍历数组
* `slow`指针负责指向目前为止不重复区域的边界。

具体步骤就是，只要`fast`指针看到不重复的数字，就把当前数字拷贝到`slow`指针指向的位置，并且`slow`指针前进一格。

唯一不同在于这里允许重复`2`次。所以用一个`count`变量监视重复次数。

#### 代码
```java
public class Solution {
    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) { return 0; }
        int register = nums[0], count = 1, slow = 1; // init from 1st element
        for (int fast = 1; fast < nums.length; fast++) {
            if (nums[fast] > register) {
                register = nums[fast];
                count = 1;
                nums[slow++] = nums[fast];
            } else if (nums[fast] == register && count < 2) {
                count++;
                nums[slow++] = nums[fast];
            }
        }
        return slow;
    }
}
```

#### 稍微简化代码
去掉`register`变量，直接通过下标访问，
```java
public int removeDuplicates(int[] nums) {
    if (nums.length < 2) { return nums.length; }
    int boundary = 1, count = 1;
    for (int i = boundary; i < nums.length; i++) {
        if (nums[i] > nums[boundary-1]) {
            count = 1;
            nums[boundary++] = nums[i];
        } else if (nums[i] == nums[boundary-1] && count < 2) {
            count++;
            nums[boundary++] = nums[i];
        }
    }
    return boundary;
}
```

#### 结果
![remove-duplicates-two-1](/images/leetcode/remove-duplicates-two-1.png)


### 奇技淫巧
下面这段代码非常短。看上去很漂亮，但逻辑上非常别扭。不推荐。

#### 代码
```java
public class Solution {
    public int removeDuplicates(int[] nums) {
       int i = 0;
       for (int n : nums)
          if (i < 2 || n > nums[i - 2])
             nums[i++] = n;
       return i;
    }
}
```

#### 结果
结果也没有非常好。
![remove-duplicates-two-2](/images/leetcode/remove-duplicates-two-2.png)
