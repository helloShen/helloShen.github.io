---
layout: post
title: "Leetcode - Algorithm - Generalized Abbreviation "
date: 2018-08-07 23:19:58
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["backtracking"]
level: "medium"
description: >
---

### 题目
Write a function to generate the generalized abbreviations of a word.

Note: The order of the output does not matter.

Example:
```
Input: "word"
Output:
["word", "1ord", "w1rd", "wo1d", "wor1", "2rd", "w2d", "wo2", "1o1d", "1or1", "w1r1", "1o2", "2r1", "3d", "w3", "4"]
```


### 回溯算法
首先这道题属于典型的需要暴力遍历所有分支的题。两条分支很清楚，对每个字符，
* 要么缩写用`1`代替（相邻的`1`可以融合）
* 要么不用缩写

![generalized-abbreviation-a](/images/leetcode/generalized-abbreviation-a.png)

遇到这种暴力遍历的题，递归回溯就比较好。但这题比普通的回溯要麻烦一点，因为两条分支回溯的时机不一样，
![generalized-abbreviation-b](/images/leetcode/generalized-abbreviation-b.png)

#### 代码
```java
class Solution {
    public List<String> generateAbbreviations(String word) {
        List<String> res = new ArrayList<>();
        if (word == null) { return null; }
        backtracking(new StringBuilder(), word, 0, 0, res);
        return res;
    }
    //这里回溯稍微有点复杂，但抓住关键就不会错：
    //回溯的主体是prefix，所以只有当prefix真正改变了，才需要调用delete()回溯。
    //每次递归展开两条支线：
    //      1. 数字支线：prefix暂不变化，暂记一位缩写，所以本层递归不回溯。哪里遇到字符了，缩写兑现到prefix里了，再回溯。
    //      2. 字符支线：本层递归prefix就变化，而且带着之前所有累计的缩写位数一起兑现到prefix里，所以当场回溯。
    //
    private void backtracking(StringBuilder prefix, String word, int p, int count, List<String> list) {
        //base case
        if (p == word.length()) {
            if (count > 0) {
                int len = prefix.length();
                list.add(new String(prefix.append(count)));
                prefix.delete(len,prefix.length()); //数字线在这里真正加到prefix里之后回溯
            } else {
                list.add(new String(prefix));       //字符线在前一步改变prefix的时候已经回溯，这里就不回溯
            }
            return;
        }
        //数字支线
        backtracking(prefix,word,p+1,count+1,list); //数字支线这里没有真的改变prefix，先不回溯
        //字符支线
        int len = prefix.length();
        if (count > 0) { prefix.append(String.valueOf(count)); }
        prefix.append(word.charAt(p));
        backtracking(prefix,word,p+1,0,list);
        prefix.delete(len,prefix.length());         //字符支线因为已经改变prefix，所以在这里回溯
    }
}
```

#### 结果
![generalized-abbreviation-2](/images/leetcode/generalized-abbreviation-2.png)



### 分两步走，回溯算法可以稍微简单一点
如果不想这么麻烦，有个办法可以简化，就是 **【分两步走】**：
* 第一步，只替换（缩写用`1`替换字符），不融合（相邻的`1`不累加）
* 第二步，累加相邻的`1`

比如，`word`的其中一种写法：`w111`，回溯的过程中，直接在字符串或数组上替换，最后要写入结果列表的时候再把`w111`翻译成`w3`（`merge()`函数）。

这样做的好处是，每层递归的两个分支不存在一个先回溯，另一个后回溯的情况。逻辑比较简化。而且修改的过程字符串长度本身不改变，甚至可以用一个固定长度的数组`char[]`代替字符串。

#### 代码
```java
class Solution {
    public List<String> generateAbbreviations(String word) {
        if (word == null) { return null; }
        res = new ArrayList<String>();
        charArray = word.toCharArray();
        backtracking(0);
        return res;
    }
    private List<String> res;
    private char[] charArray;
    private void backtracking(int p) {
        if (p == charArray.length) {
            res.add(merge(charArray));
            return;
        }
        //保留字符分支，完全不需要动  
        backtracking(p+1);
        //缩写成'1'分支，先改成'1'，然后回溯改回来
        char c = charArray[p];
        charArray[p] = '1';
        backtracking(p+1);
        charArray[p] = c;    
    }
    private String merge(char[] word) {
        int len = word.length;
        StringBuilder sb = new StringBuilder();
        int p = 0;
        while (p < len) {
            while (p < len && !Character.isDigit(word[p])) { sb.append(word[p]); p++; }
            int count = 0;
            while (p < len && Character.isDigit(word[p])) { p++; count++; }
            if (count > 0) { sb.append(String.valueOf(count)); }
        }
        return new String(sb);
    }
}
```

#### 结果
![generalized-abbreviation-1](/images/leetcode/generalized-abbreviation-1.png)
