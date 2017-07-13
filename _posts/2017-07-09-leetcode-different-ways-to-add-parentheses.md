---
layout: post
title: "Leetcode - Algorithm - Different Ways To Add Parentheses "
date: 2017-07-09 15:25:30
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["divide and conquer"]
level: "medium"
description: >
---

### 每题除了用Java写，再用C写一遍

### 主要思路
> 遇到这种如果用普通迭代比较复杂的题，可以考虑分治法。

### 题目
Given a string of numbers and operators, return all possible results from computing all the different possible ways to group numbers and operators. The valid operators are `+`, `-` and `*`.

**Example 1**
Input: `2-1-1`.
```
((2-1)-1) = 0
(2-(1-1)) = 2
```
Output: `[0, 2]`


**Example 2**
Input: `2*3-4*5`
```
(2*(3-(4*5))) = -34
((2*3)-(4*5)) = -14
((2*(3-4))*5) = -10
(2*((3-4)*5)) = -10
(((2*3)-4)*5) = 10
```
Output: `[-34, -14, -10, -10, 10]`

### 分治法 $$O(n\log_{}{n})$$
比如`2*3-4*5`可以分解为下面若干子问题，
* 子问题`2` * 子问题`3-4*5`
* 子问题`2*3` * 子问题`4*5`
* 子问题`2*3-4` * 子问题`5`

#### Java代码
```java
public class Solution {

    private Map<String,List<Integer>> memo = new HashMap<>();

    public List<Integer> diffWaysToCompute(String input) {
        int len = input.length();
        // check history
        List<Integer> result = memo.get(input);
        if (result != null) { return result; }
        result = new ArrayList<>();
        // base case
        if (isDigit(input)) {
            result.add(Integer.parseInt(input));
            memo.put(input,result);
            return result;
        }
        // recursion (divid & conquer)
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (c == '+' || c == '-' || c == '*') {
                List<Integer> left = diffWaysToCompute(input.substring(0,i));
                List<Integer> right = diffWaysToCompute(input.substring(i+1,len));
                for (Integer il : left) {
                    for (Integer ir : right) {
                        switch (c) {
                            case '+': result.add(il + ir); break;
                            case '-': result.add(il - ir); break;
                            case '*': result.add(il * ir); break;
                        }
                    }
                }
            }
        }
        memo.put(input,result);
        return result;
    }
    private boolean isDigit(String s) {
        for (Character c : s.toCharArray()) {
            if (!Character==.isDigit(c)) { return false; }
        }
        return true;
    }
}
```

#### 结果
![different-ways-to-add-parentheses-1](/images/leetcode/different-ways-to-add-parentheses-1.png)

#### C代码（第一版）
好久没写`C`，第一版写得很烂。`malloc()`了好多没必要的堆内存。对库函数也不熟，写了一堆没必要的轮子。但总算跑起来了。
```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int size(char *);
int *diffWaysToCompute(char*, int*);
int *merge(int *, int , int *, int);
void charcpy(char *, char *, int);
void intcpy(int *, int *, int);

/**
 * Return an array of size *returnSize.
 * Note: The returned array must be malloced, assume caller calls free().
 */
int* diffWaysToCompute(char *input, int *returnSize) {
    char *cur = input;
    int *result = NULL;
    *returnSize = 0;
    while (*cur++) {
        char c = *(cur-1);
        if (c == '+' || c == '-' || c == '*') { // 递归左右两个子数组
            // 递归左半边
            int ls = cur - 1 - input; // 准备左子字符串
            char *l_str = malloc((ls+1)*sizeof(char));
            charcpy(l_str,input,ls);
            *(l_str+ls) = '\0';
            int  l_rs = 0; // 准备用来返回长度的指针
            int *l_rsp = &l_rs;
            int *l = diffWaysToCompute(l_str,l_rsp); // 递归
            free(l_str);
            // 递归右半边
            int rs = size(input) - ls - 1; // 准备右子字符串
            char *r_str = malloc((rs+1)*sizeof(char));
            charcpy(r_str,cur,rs);
            *(r_str+rs) = '\0';
            int r_rs = 0; // 准备用来返回长度的指针
            int *r_rsp = &r_rs;
            int *r = diffWaysToCompute(r_str,r_rsp); // 递归
            free(r_str);
            // 得到左右两边计算后的结果
            int calculateSize = *l_rsp * *r_rsp;
            int *calculate = (int *)malloc(calculateSize * sizeof(int));
            int *cur = calculate;
            for (int i = 0; i < *l_rsp; i++) {
                for (int j = 0; j < *r_rsp; j++) {
                    switch (c) {
                        case '+': *cur++ = *(l+i) + *(r+j); break;
                        case '-': *cur++ = *(l+i) - *(r+j); break;
                        case '*': *cur++ = *(l+i) * *(r+j); break;
                    }
                }
            }
            // 把这轮计算结果，加入总结果中。
            int *newResult = merge(result,*returnSize,calculate,calculateSize);
            *returnSize += calculateSize;
            free(result); free(calculate);
            result = newResult;
        }
    }
    if (!*returnSize) {
        result = (int *)malloc(sizeof(int));
        *result = atoi(input);
        *returnSize = 1;
    }
    return result;
}

/** return the size of a string */
int size(char *input) {
    int count = 0;
    char *cur = input;
    while (*cur++) { count++; }
    return count;
}

/**
 *  合并两个数组。 合并完之后返回新malloc()出的一块内存。调用函数应负责free()内存。
 *  不负责清理原先两个数组。
 *  如果两个待合并数组都为空，则返回空。
 */
int *merge(int *a, int sa, int*b, int sb) {
    int size = sa + sb;
    size_t memo_size = size * sizeof(int);
    if (!size) { return NULL; }
    int *result = (int *)malloc(memo_size);
    intcpy(result,a,sa);
    int *mid = result + sa;
    intcpy(mid,b,sb);
    return result;
}

/**
 *  copy n characters from src to des.
 *  只负责拷贝，不负责内存
 *  只负责字符串内容的拷贝，不负责字符串末尾的 "\0"结尾符
 */
void charcpy(char *des, char *src, int n) {
    while (n--) { *des++ = *src++; } // len to 1
}
/**
 *  copy n int from src to des.
 *  只负责拷贝不负责内存
 */
void intcpy(int *des, int *src, int n) {
    while (n--) { *des++ = *src++; } // len to 1
}
```

#### 结果
**！注意看**：这才是这道题历史上的第3次用C语言的被接受提交。C语言都没人练了吗？
![different-ways-to-add-parentheses-2](/images/leetcode/different-ways-to-add-parentheses-2.png)

#### C语言（第二版）
尽量用局部栈帧上的内存，少用`malloc()`堆内存（除了题要求的返回值）。尽量全用指针，不用数组，不用字符串（`input`输入除外）。代码总算消肿。
```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

int *diffWaysToCompute(char *, int *);
int *dandc(char *, int, int *);

int *diffWaysToCompute(char *input, int *returnSize) {
    return dandc(input,strlen(input),returnSize);
}

int *dandc(char *input, int size, int *returnSize) {
    // prepare memory
    int * ptr = 0; // init to null
    *returnSize = 0;
    int *l, *r; // result of two sub-problem
    int ls, rs; // size of the result of two sub-problem
    // iteration
    char *cur = input;
    for (int i = 0; i < size; i++) {
        char c = *cur++;
        if (!isdigit((int)c)) {
            l = dandc(input,cur-input-1,&ls);
            r = dandc(cur,size-(cur-input),&rs);
            int start = *returnSize;
            *returnSize += (ls * rs);
            ptr = (int *)realloc(ptr, *returnSize * sizeof(int));
            for (int j = 0; j < ls; j++) {
                for (int k = 0; k < rs; k++) {
                    switch(c) {
                        case '+': ptr[start++] = l[j] + r[k]; break;
                        case '-': ptr[start++] = l[j] - r[k]; break;
                        case '*': ptr[start++] = l[j] * r[k]; break;
                    }
                }
            }
            free(l); free(r);
        }
    }
    if (!ptr) {
        ptr = (int *)realloc(ptr,sizeof(int));
        *returnSize = 1;
        ptr[0] = atoi(input);
    }
    return ptr;
}
```

#### 结果
这题史上第4次C语言提交。达标！4次里，我就占了2次。哈哈。
![different-ways-to-add-parentheses-3](/images/leetcode/different-ways-to-add-parentheses-3.png)
