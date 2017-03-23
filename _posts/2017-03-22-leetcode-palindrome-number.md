---
layout: post
title: "Leetcode - Algorithm - Palindrome Number"
date: 2017-03-22 13:27:59
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode","integer"]
level: "easy"
description: >
---

### 题目
Determine whether an integer is a palindrome. Do this without extra space.

**Some hints**:
Could negative integers be palindromes? (ie, -1)

If you are thinking of converting the integer to string, note the restriction of using extra space.

You could also try reversing an integer. However, if you have solved the problem "Reverse Integer", you know that the reversed integer might overflow. How would you handle such case?

There is a more generic way of solving this problem.

### 朴素计算法
这题不许用额外的空间，就是不能转化成`String`再反转。所以通过计算直接逆序排列，然后和原数字比较是否相等。

#### 代码
```java
public class Solution {
    public static boolean isPalindrome(int x) {
        if (x < 0) { return false; } // negative integer is not palindrome
        long orig = x, reverse = 0;
        while (x != 0) {
            reverse = reverse * 10 + (x % 10);
            x = x /10;
        }
        return orig == reverse;
    }
}
```

#### 结果
![palindrome-number-1](/images/leetcode/palindrome-number-1.png)

### 只遍历一半数字
基本思路一样，把数字逆序排列，和原数字比较。但其实只需要逆序排列一半数字，就可以和原数字比较是否相等。需要注意的问题是，`10`,`100`,`1000`这样以`0`结尾的数字，会出bug。所以一开始就把这种情况排除出去。

#### 代码
```java
public class Solution {
    public boolean isPalindrome(int x) {
        if (x < 0 || (x!= 0 && x%10 == 0)) { return false; }
        long orig = x, rev = 0;
        while (x != 0) {
            int rmd = x % 10;
            x = x/10;
            if (x == rev) { return true; }
            rev = rev * 10 + rmd;
            if (x == rev) { return true; }
            if (rev > x) { return false; }
        }
        return true; // x = 0
    }
}
```

#### 结果
![palindrome-number-2](/images/leetcode/palindrome-number-2.png)

### 第二种解法，更简洁的代码
第二种方法，可以写得更简短。但代码是简短了，可读性不太好。

#### 代码
```java
public class Solution {
    public boolean isPalindrome(int x) {
        if (x < 0 || (x != 0 && x % 10 == 0)) { return false; }
        int rev = 0;
        while (rev < x) {
            rev = rev * 10 + x % 10;
            x = x/10;
        }
        return (x == rev || x == rev/10);
    }
}
```

#### 结果
![palindrome-number-3](/images/leetcode/palindrome-number-3.png)
