---
layout: post
title: "Leetcode - Algorithm - Additive Number "
date: 2017-08-28 19:12:14
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["two pointers"]
level: "medium"
description: >
---

### 题目
additive number is a string whose digits can form additive sequence.

A valid additive sequence should contain at least three numbers. Except for the first two numbers, each subsequent number in the sequence must be the sum of the preceding two.

For example:
`112358` is an additive number because the digits can form an additive sequence: `1, 1, 2, 3, 5, 8`.
```
1 + 1 = 2, 1 + 2 = 3, 2 + 3 = 5, 3 + 5 = 8
```
`199100199` is also an additive number, the additive sequence is: `1, 99, 100, 199`.
```
1 + 99 = 100, 99 + 100 = 199
```
Note: Numbers in the additive sequence cannot have leading zeros, so sequence `1, 2, 03` or `1, 02, 3` is invalid.

Given a string containing only digits `0`-`9`, write a function to determine if it's an additive number.

Follow up:
How would you handle overflow for very large input integers?

### `Two Pointers`算法
只要前两个数字已确定，就可以判断后续数字串是否满足`addtive number`的条件。排查起始两个数字没有捷径，只有每一种可能都尝试。用两个指针，分别指向其实两个数字的结尾处，
```bash
    xy
    ||
    199100199

number 1 -> [0,x]
number 2 -> [x+1,y];
```

注意两层遍历的终止条件，如果其中一个数字的长度超过剩下所有数字的长度，则不可能是`additive number`了。所以
* `x`的终止条件：`x <= str.length() / 2 - 1;`
* `y`的终止条件：`(str.length() - y) >= Math.max(x,y)`

另外需要注意：除了`0`以外，所有数字不能以`0`开头。像`199001200`就是不合法的。虽然`199+001=200`但，`001`的表达式不合法。

#### 代码
```java
class Solution {
    /* 主接口 */
    public boolean isAdditiveNumber(String num) {
        int len = num.length();
        if (len < 3) { return false; }
        for (int i = 0; i <= len / 2 - 1; i++) {
            for (int j = i + 1; (len - j) >= Math.max(i,j-i); j++) {
                if (isAdditive(num,i,j)) { return true; }
            }
        }
        return false;
    }
    /* slow points to the end of first number, fast points to the end of second number */
    private boolean isAdditive(String num, int slow, int fast) {
        int pre = 0; // num1: [pre,slow]    num2: [slow+1,fast]
        while (fast < num.length()-1) {
            // 一个不是0，但以0开头的数字是不合法的
            if ((num.charAt(pre) == '0' && slow - pre > 0) || (num.charAt(slow+1) == '0' && fast - slow - 1 > 0)) { return false; }
            String x = num.substring(pre,slow+1);
            String y = num.substring(slow+1,fast+1);
            String sum = sum(x,y);
            int nextEnd = indexOf(num,fast+1,sum);
            if (nextEnd == -1) { return false; }
            pre = slow+1;
            slow = fast;
            fast = nextEnd;
        }
        return true;
    }
    /*
     * 从num的start位置开始，往后匹配sum。
     * 匹配成功，返回sum最后一个元素的下标。
     * 失败，返回-1。
     */
    private int indexOf(String num, int start, String sum) {
        int curNum = start;
        int curSum = 0;
        while (curNum < num.length() && curSum < sum.length()) {
            if (sum.charAt(curSum++) != num.charAt(curNum++)) { return -1; }
        }
        return (curSum == sum.length())? curNum-1 : -1;
    }
    /*
     * 求a+b的和。适用于任意长度的数字。
     */
    private String sum(String x, String y) {
        StringBuilder sb = new StringBuilder();
        int cx = x.length()-1, cy = y.length()-1;
        int carry = 0;
        while (cx >= 0 && cy >= 0) {
            int sum = (x.charAt(cx--) - '0') + (y.charAt(cy--) - '0') + carry;
            carry = sum / 10;
            sb.insert(0,sum % 10);
        }
        while (cx >= 0) {
            int sum = (x.charAt(cx--) - '0') + carry;
            carry = sum / 10;
            sb.insert(0,sum % 10);
        }
        while (cy >= 0) {
            int sum = (y.charAt(cy--) - '0') + carry;
            carry = sum / 10;
            sb.insert(0,sum % 10);
        }
        if (carry == 1) { sb.insert(0,'1'); }
        return sb.toString();
    }
}
```

#### 结果
![additive-number-1](/images/leetcode/additive-number-1.png)

### 后续优化
有两点，
1. 可以用`BigDicimal`代替这里的做加法函数`sum()`。
2. `isAdditive()`用递归写更短。

这里就不重复写了。
