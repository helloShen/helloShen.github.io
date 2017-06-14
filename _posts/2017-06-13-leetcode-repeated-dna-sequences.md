---
layout: post
title: "Leetcode - Algorithm - Repeated Dna Sequences "
date: 2017-06-13 21:13:45
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation","hash table"]
level: "medium"
description: >
---

### 题目
All DNA is composed of a series of nucleotides abbreviated as A, C, G, and T, for example: "ACGAATTCCG". When studying DNA, it is sometimes useful to identify repeated sequences within the DNA.

Write a function to find all the 10-letter-long sequences (substrings) that occur more than once in a DNA molecule.

For example,
```
Given s = "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT",
```
Return:
```
["AAAAACCCCC", "CCCCCAAAAA"].
```

### 思路分析
首先，这题老老实实遍历，在 $$O(n)$$ 时间里是能解决问题的。

其次，在 $$O(long_{}{n})$$ 时间内解决问题，是不可能的。必须至少遍历整个`String`。所以类似于 **分治法**，**二分法** 都不用考虑了。

再次，因为是连续的子串，所以也不涉及排列组合问题，所以也不需要类似 **回溯算法**，或者 **动态规划**。

问题的关键在于 **映射空间**。在“如何比较子串”的问题上可以做文章。

### 直接比较`String`
用一个`HashSet`储存遇到过的所有子串。

#### 代码
```java
public class Solution {
    public List<String> findRepeatedDnaSequences(String s) {
        Set<String> retSet = new HashSet<>();
        if (s == null || s.length() <= 10) { return new ArrayList<String>(retSet); }
        Set<String> memo = new HashSet<>();
        for (int i = 0; i <= s.length() - 10; i++) {
            String sub = s.substring(i,i+10);
            if (!memo.add(sub)) { retSet.add(sub); }
        }
        return new ArrayList<String>(retSet);
    }
}
```

#### 结果
已经是银弹！
![repeated-dna-sequences-1](/images/leetcode/repeated-dna-sequences-1.png)


### 计算`hashCode`
把10位长的子串，映射到`long`型空间。传统方法计算每个子串的散列值。

#### 代码
```java
public class Solution {
    public List<String> findRepeatedDnaSequences(String s) {
        Set<Long> memo = new HashSet<>();
        if (s == null || s.length() <= 10) { return new ArrayList<>(); }
        Set<String> retSet = new HashSet<>();
        for (int i = 0; i <= s.length()-10; i++) {
            if (!memo.add(hashDNA(s,i,i+9))) { retSet.add(s.substring(i,i+10)); }
        }
        return new ArrayList<String>(retSet);
    }
    public Long hashDNA(String s, int lo, int hi) {
        Long hash = 17l;
        for (int i = lo; i <= hi; i++) {
            switch (s.charAt(i)) {
                case 'A':
                    hash = (hash << 5 - 1) + 3; break;
                case 'C':
                    hash = (hash << 5 - 1) + 5; break;
                case 'G':
                    hash = (hash << 5 - 1) + 7; break;
                case 'T':
                    hash = (hash << 5 - 1) + 11; break;
            }
        }
        return hash;
    }
}
```

#### 结果
![repeated-dna-sequences-2](/images/leetcode/repeated-dna-sequences-2.png)


### 用一个32位的`int`做映射空间
因为DNA只有4种可能的键：`A,C,G,T`。可以用一个`2 bits`编码表示：
* `A` : `00`
* `C` : `01`
* `G` : `10`
* `T` : `11`

一个`int`有`32` bits，用低位的`20` bits储存信息。

#### 代码
```java
public class Solution {
    public List<String> findRepeatedDnaSequences(String s) {
        // defense
        if (s == null || s.length() <= 10) { return new ArrayList<String>(); }
        // two Set
        Set<Integer> memo = new HashSet<>(); // dictionary of bit code
        Set<String> retSet = new HashSet<>(); // result
        // iteration
        for (int i = 0; i <= s.length()-10; i++) {
            if (! memo.add(getBitCode(s,i,i+9))) { retSet.add(s.substring(i,i+10)); }
        }
        // return
        return new ArrayList<String>(retSet);
    }
    public int getBitCode(String s, int lo, int hi) {
        int bitCode = 0;
        int mask = 1;
        for (int i = hi; i >= lo; i--) {
            switch (s.charAt(i)) {
                case 'A':
                    mask = mask << 2; break;
                case 'C':
                    bitCode = bitCode | mask;
                    mask = mask << 2; break;
                case 'G':
                    mask = mask << 1;
                    bitCode = bitCode | mask;
                    mask = mask << 1; break;
                case 'T':
                    bitCode = bitCode | mask;
                    mask = mask << 1;
                    bitCode = bitCode | mask;
                    mask = mask << 1; break;
            }
        }
        return bitCode;
    }
}
```

#### 结果
![repeated-dna-sequences-3](/images/leetcode/repeated-dna-sequences-3.png)


### `int`做映射空间，改进版
主要做了两个改进：
1. 不是每次编码都重新扫描整个子串。每次由前一个编码：去掉头两位，再补上末两位得来。
2. 与其用一个`swith`结构，用一个`int[]`来储存`A,C,G,T`字母的映射，简化代码。

#### 代码
```java
public class Solution {
    public List<String> findRepeatedDnaSequences(String s) {
        // defense
        if (s == null || s.length() <= 10) { return new ArrayList<String>(); }
        // two Set
        Set<Integer> memo = new HashSet<>(); // dictionary of bit code
        Set<String> retSet = new HashSet<>(); // result
        // iteration
        int bitCode = -1;
        int[] map = new int[26];
        map['C' - 'A'] = 1;
        map['G' - 'A'] = 2;
        map['T' - 'A'] = 3;
        for (int i = 0; i <= s.length()-10; i++) {
            if (bitCode == -1) {
                bitCode = 0;
                for (int j = i; j <= i+9; j++) {
                    bitCode = bitCode << 2;
                    bitCode |= map[s.charAt(j) - 'A'];
                }
            } else {
                // 掐头
                bitCode = bitCode << 14 >>> 12;
                // 补尾
                bitCode |= map[s.charAt(i+9) - 'A'];
            }
            if (! memo.add(bitCode)) { retSet.add(s.substring(i,i+10)); }
        }
        // return
        return new ArrayList<String>(retSet);
    }
}
```

#### 结果
结果更差了，可能是服务器的原因。理论上效率应该是更高了。
![repeated-dna-sequences-4](/images/leetcode/repeated-dna-sequences-4.png)
