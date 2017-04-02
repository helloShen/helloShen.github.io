---
layout: post
title: "Leetcode - Algorithm - Remove Element "
date: 2017-04-02 11:10:06
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array","two pointers"]
level: "easy"
description: >
---

### 题目
Given an array and a value, remove all instances of that value in place and return the new length.

Do not allocate extra space for another array, you must do this in place with constant memory.

The order of elements can be changed. It doesn't matter what you leave beyond the new length.
```
Example:
Given input array nums = [3,2,2,3], val = 3

Your function should return length = 2, with the first two elements of nums being 2.
```
### 双指针替换 $$O(n)$$
和`Remove Duplicates`的思路相同，都是维护两个指针。`cursor`指向我们维护的数组的边界，`i`一直向前遍历。
```bash
[1, 1，1，1, 2, 2, 2, 3]
```
需要删除所有`2`。当`i`找到第一个`3`，只需要把`3`复制到`cursor`指向的位置：第一个`2`。然后`cursor`前移，并返回`cursor`即可。
```bash
[1, 1，1，1, 3, 2, 2, 3]
```

#### 代码
```java
public class Solution {
    public int removeElement(int[] nums, int val) {
        int cursor = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != val) {
                nums[cursor++] = nums[i];
            }
        }
        return cursor;
    }
}
```

#### 结果
银弹！
![remove-element-1](/images/leetcode/remove-element-1.png)


### 排序 $$O(n\log_{n})$$
这题双指针的效率已经是$$O(n)$$。排序的$$O(n\log_{n})$$已经没有优势了。
