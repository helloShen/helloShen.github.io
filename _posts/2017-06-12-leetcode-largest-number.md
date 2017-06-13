---
layout: post
title: "Leetcode - Algorithm - Largest Number "
date: 2017-06-12 22:13:18
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["sort"]
level: "medium"
description: >
---

### 题目
Given a list of non negative integers, arrange them such that they form the largest number.

For example, given `[3, 30, 34, 5, 9]`, the largest formed number is `9534330`.

Note: The result may be very large, so you need to return a string instead of an integer.


### 数学方法
这题基本总结不出一个数学方法来直接比较两个数的大小。比如，`3232 == 32`。最简单的比较方法就是把两个`String`拼接起来，一位一位地比较。

### 自己写排序算法
自己写排序的好处是，`compare()`函数的接口比较自由。比如可以用一个`int[][] memo`备忘录来记录已经比较过的两个对象。如果比较操作的开销比较大，这么做是值得的。

#### 代码
```java
public class Solution {
    public String largestNumber(int[] nums) {
        String[] strs = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            strs[i] = String.valueOf(nums[i]);
        }
        int[][] memo = new int[nums.length][nums.length];
        sort(strs,0,strs.length-1,memo);
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str);
        }
        return trimLeadingZero(sb.toString());
    }

    public void sort(String[] nums, int lo, int hi, int[][] memo) {
        if (lo == hi) { return; }
        int mid = lo + (hi - lo) / 2;
        sort(nums,lo,mid,memo);
        sort(nums,mid+1,hi,memo);
        merge(nums,lo,mid,mid+1,hi,memo);
    }
    public void merge(String[] nums, int lo1, int hi1, int lo2, int hi2, int[][] memo) {
        String[] temp = new String[hi2-lo1+1];
        int cur = 0, cur1 = lo1, cur2 = lo2;
        while (cur1 <= hi1 && cur2 <= hi2) {
            int ret = compare(nums,cur1,cur2,memo);
            temp[cur++] = (ret >= 2)? nums[cur1++] : nums[cur2++];
        }
        while (cur1 <= hi1) { temp[cur++] = nums[cur1++]; }
        while (cur2 <= hi2) { temp[cur++] = nums[cur2++]; }
        for (int i = 0; i < temp.length; i++) {
            nums[lo1+i] = temp[i];
        }
    }
    /*
     * return:
     *  1: num1 < num2
     *  2: num1 == num2
     *  3: num1 > num2
     */
    public int compare(String[] nums, int pos1, int pos2, int[][] memo) {
        if (memo[pos1][pos2] > 0) { return memo[pos1][pos2]; }
        String sum1 = nums[pos1] + nums[pos2];
        String sum2 = nums[pos2] + nums[pos1];
        for (int i = 0; i < sum1.length(); i++) {
            char c1 = sum1.charAt(i);
            char c2 = sum2.charAt(i);
            if (c1 > c2) {
                memo[pos1][pos2] = 3;
                return 3;
            } else if (c1 < c2){
                memo[pos1][pos2] = 1;
                return 1;
            }
        }
        memo[pos1][pos2] = 2;
        return 2;
    }
    public String trimLeadingZero(String s) {
        int cur = 0;
        while (cur < s.length() && s.charAt(cur) == '0') { cur++; }
        s = s.substring(cur);
        return (s.isEmpty())? "0" : s;
    }
}
```

#### 结果
![largest-number-1](/images/leetcode/largest-number-1.png)


### 用库函数`Arrays.sort()`
只需要写一个`Comparator`，而`Comparator`内部还可以直接调用`String#compareTo()`函数，代码更简洁。但缺点是不能用`int[][] memo`做备忘录了。

#### 代码
```java
public class Solution {
    public String largestNumber(int[] nums) {
        String[] strs = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            strs[i] = String.valueOf(nums[i]);
        }
        int[][] memo = new int[nums.length][nums.length];
        Arrays.sort(strs, new Comparator<String>() {
            public int compare(String s1, String s2) {
                String sum1 = s1 + s2;
                String sum2 = s2 + s1;
                return sum2.compareTo(sum1);
            }
        });
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(str);
        }
        return trimLeadingZero(sb.toString());
    }
    public String trimLeadingZero(String s) {
        int cur = 0;
        while (cur < s.length() && s.charAt(cur) == '0') { cur++; }
        s = s.substring(cur);
        return (s.isEmpty())? "0" : s;
    }
}
```

#### 结果
![largest-number-2](/images/leetcode/largest-number-2.png)
