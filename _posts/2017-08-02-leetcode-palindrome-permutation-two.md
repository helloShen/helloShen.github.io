---
layout: post
title: "Leetcode - Algorithm - Palindrome Permutation Two "
date: 2017-08-02 22:02:24
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["dynamic programming","backtracking"]
level: "medium"
description: >
---

### 主要收获
> 回溯算法跳过重复元素有时候非常重要。

比如在用回溯算法生成全排列的过程中，遇到像`baaaccd`，就应该跳过第2，第3个`a`和第2个`c`，因为他们重复选用不会带来新的排列串。
```
        跳过这3个重复字符
          || |
        baaaccd
```

### 题目
Given a string s, return all the palindromic permutations (without duplicates) of it. Return an empty list if no palindromic permutation could be form.

For example:

Given s = `aabb`, return [`abba`, `baab`].

Given s = `abc`, return [].

### 直接枚举出全排列，然后逐个判断是不是回文
这里全排列算法，用的是 **回溯算法**。

#### 代码
```java
public class Solution {
    public List<String> generatePalindromes(String s) {
        Set<String> permutations = new HashSet<>();
        permutation(new StringBuilder(s),new StringBuilder(),permutations);
        Iterator<String> ite = permutations.iterator();
        while (ite.hasNext()) {
            if (!isPalindrome(ite.next())) { ite.remove(); }
        }
        return new ArrayList<String>(permutations);
    }
    /* 用StringBuilder，更快的回溯 */
    private void permutation(StringBuilder letters, StringBuilder word, Set<String> permutations) {
        int len = letters.length();
        if (len == 0) { permutations.add(word.toString()); return; }
        for (int i = 0; i < len; i++) {
            char letter = letters.charAt(i);
            word.append(letter);
            letters.delete(i,i+1);
            permutation(letters,word,permutations);
            int wordLen = word.length();
            word = word.delete(wordLen-1,wordLen);
            letters.insert(i,letter);
        }
    }
    /* 判断输入字符串是否是回文 */
    private boolean isPalindrome(String s) {
        int lo = 0, hi = s.length()-1;
        while (lo < hi) {
            if (s.charAt(lo++) != s.charAt(hi--)) { return false; }
        }
        return true;
    }
}
```

#### 结果
![palindrome-permutation-two-1](/images/leetcode/palindrome-permutation-two-1.png)


### 先判断有没有可能是回文，再剥离出对称的一半字符
比如，`aabbc`，拆解成对称元素和单核。
```
half = ['a','b']
core = ['c']
```
只对对称元素做全排列。

#### 代码
这里全排列还是用的 **回溯算法**。
```java
public class Solution {
    public List<String> generatePalindromes(String s) {
        List<String> result = new ArrayList<>();
        char[] half = new char[s.length()/2];
        char[] core = new char[1];
        if (!canGeneratePalindrome(s,half,core)) { return result; }
        Set<String> permutations = new HashSet<>();
        permutation(new StringBuilder(new String(half)), new StringBuilder(), permutations);
        if (permutations.isEmpty() && core[0] != '\0') { result.add(new String(core)); } // s长度为1，只有一个单核
        for (String str : permutations) {
            int halfLen = str.length();
            StringBuilder sb = new StringBuilder(str);
            if (core[0] != '\0') { sb.append(core[0]); }
            for (int i = halfLen - 1; i >= 0; i--) {
                sb.append(sb.charAt(i));
            }
            result.add(sb.toString());
        }
        return result;
    }
    /*
     * 判断能否生成回文。如果能的，拆解出单核，以及对称的一半字符，备用。
     * 字符串长度为零，返回false。
     */
    private boolean canGeneratePalindrome(String s, char[] half, char[] core) {
        int len = s.length();
        if (len == 0) { return false; }
        Set<Character> set = new HashSet<>();
        int cur = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (!set.add(c)) {
                set.remove(c);
                half[cur++] = c;
            }
        }
        int size = set.size();
        if (size == 1) { core[0] = (char)set.toArray()[0]; }
        return size < 2;
    }
    /* 返回一系列字符串的全排列。 */
    private void permutation(StringBuilder letters, StringBuilder word, Set<String> permutations) {
        int len = letters.length();
        int wordLen = word.length();
        if (len == 0 && wordLen > 0) { permutations.add(word.toString()); return; }
        for (int i = 0; i < len; i++) {
            char letter = letters.charAt(i);
            word.append(letter);
            letters.delete(i,i+1);
            permutation(letters,word,permutations);
            letters.insert(i,letter);
            wordLen = word.length();
            word.delete(wordLen-1,wordLen);
        }
    }
}
```

#### 结果
![palindrome-permutation-two-2](/images/leetcode/palindrome-permutation-two-2.png)

#### 回溯算法优化 - 跳过重复元素
看上去上面的回溯算法速度很慢。但其实只要做一点很简单的去重，就可以加速，

```java
private void permutation(StringBuilder letters, StringBuilder word, Set<String> permutations) {
    int len = letters.length();
    int wordLen = word.length();
    if (len == 0 && wordLen > 0) { permutations.add(word.toString()); return; }
    for (int i = 0; i < len; i++) {
        if (i > 0 && letters.charAt(i) == letters.charAt(i-1)) { continue; } // 跳过重复的字母，这一步非常重要
        char letter = letters.charAt(i);
        word.append(letter);
        letters.delete(i,i+1);
        permutation(letters,word,permutations);
        letters.insert(i,letter);
        wordLen = word.length();
        word.delete(wordLen-1,wordLen);
    }
}
```

#### 结果
![palindrome-permutation-two-3](/images/leetcode/palindrome-permutation-two-3.png)

#### 进一步优化回溯算法
除了传统的插入，删除元素。还可以用一个`boolean[]`数组，记录字符的使用情况。这在遇到一些随机插入，删除很麻烦的数据结构的时候，非常有效。
```java
private class Solution {

    public List<String> generatePalindromes(String s) {
        List<String> result = new ArrayList<>();
        char[] half = new char[s.length()/2];
        char[] core = new char[1];
        if (!canGeneratePalindrome(s,half,core)) { return result; }
        Set<String> permutations = new HashSet<>();
        // permutation()函数面对的下层接口是两个char[]，它向上承诺的接口是一个Set<String>
        permutation(half,new boolean[half.length],new StringBuilder(),permutations);
        if (permutations.isEmpty() && core[0] != '\0') { result.add(new String(core)); } // s长度为1，只有一个单核
        for (String str : permutations) {
            int halfLen = str.length();
            StringBuilder sb = new StringBuilder(str);
            if (core[0] != '\0') { sb.append(core[0]); }
            for (int i = halfLen - 1; i >= 0; i--) {
                sb.append(sb.charAt(i));
            }
            result.add(sb.toString());
        }
        return result;
    }
    /*
     * 判断能否生成回文。如果能的，拆解出单核，以及对称的一半字符，备用。
     * 字符串长度为零，返回false。
     */
    private boolean canGeneratePalindrome(String s, char[] half, char[] core) {
        int len = s.length();
        if (len == 0) { return false; }
        Set<Character> set = new HashSet<>();
        int cur = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (!set.add(c)) {
                set.remove(c);
                half[cur++] = c;
            }
        }
        int size = set.size();
        if (size == 1) { core[0] = (char)set.toArray()[0]; }
        return size < 2;
    }
    /* 返回一系列字符串的全排列。用一个boolean[]数组标记字符使用情况，替代传统的回溯插入，删除。但原理是一样的。 */
    private void permutation(char[] letters, boolean[] used, StringBuilder word, Set<String> permutations) {
        int wordLen = word.length();
        if (wordLen > 0 && wordLen == letters.length) { permutations.add(word.toString()); return; }
        for (int i = 0; i < letters.length; i++) {
            if (i > 0 && letters[i-1] == letters[i] && !used[i-1]) { continue; } // 跳过重复字符
            if (!used[i]) {
                word.append(letters[i]);
                used[i] = true;
                permutation(letters,used,word,permutations);
                int len = word.length();
                word.delete(len-1,len);
                used[i] = false;
            }
        }
    }
}
```

当然在生成全排列的时候，可以一起吧回文的另一半补齐。这样就不用后续手动补齐。
```java
/**
 * 还是单核的先去核。找到对称的一半。但全排列的时候直接加上另一半。
 * 全排列还是用回溯算法。
 */
private class Solution {

    public List<String> generatePalindromes(String s) {
        List<String> result = new ArrayList<>();
        char[] half = new char[s.length()/2];
        char[] core = new char[1];
        if (!canGeneratePalindrome(s,half,core)) { return result; }
        Set<String> permutations = new HashSet<>();
        permutation(half,core[0],0,new boolean[half.length],new StringBuilder(),permutations);
        return new ArrayList<String>(permutations);
    }
    /*
     * 判断能否生成回文。如果能的，拆解出单核，以及对称的一半字符，备用。
     * 字符串长度为零，返回false。
     */
    private boolean canGeneratePalindrome(String s, char[] half, char[] core) {
        int len = s.length();
        if (len == 0) { return false; }
        Set<Character> set = new HashSet<>();
        int cur = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (!set.add(c)) {
                set.remove(c);
                half[cur++] = c;
            }
        }
        int size = set.size();
        if (size == 1) { core[0] = (char)set.toArray()[0]; }
        return size < 2;
    }
    /* 返回一系列字符串的全排列。更快的回溯算法，而且直接补齐回文另一半。*/
    private void permutation(char[] letters, char core, int mid, boolean[] used, StringBuilder word, Set<String> permutations) {
        if (word.length() == letters.length * 2) {
            if (core != '\0') { word = word.insert(mid,core); }
            if (word.length() > 0) { permutations.add(word.toString()); }
            if (core != '\0') { word = word.delete(mid,mid+1); }
            return;
        }
        for (int i = 0; i < letters.length; i++) {
            // 下面这行非常重要，消除了很多重复的排列。leetcode有一项测试：aaaaaaaaaaaa，有了这一句的保护，就能通过。
            if (i > 0 && letters[i] == letters[i-1] && !used[i-1]) { continue; }
            if (!used[i]) {
                char[] pair = new char[]{letters[i],letters[i]};
                word = word.insert(mid,pair);
                used[i] = true;
                permutation(letters,core,mid+1,used,word,permutations);
                int len = word.length();
                word.delete(mid,mid+2);
                used[i] = false;
            }
        }
    }
}
```

#### 结果
![palindrome-permutation-two-3](/images/leetcode/palindrome-permutation-two-3.png)


### 还是先拆解，但用自底向上的动态规划做全排列
全排列问题典型的可以用分治法拆解成小规模子问题的。比如`abcd`，先得到子问题`bcd`解的基础上，在每个解中插入新元素`a`。
```
"a"
"bcd"子问题的解：["bcd","bdc","cbd","cdb","dbc","dcb"]

对其中的每一个解，做如下插入：
   a a a a
   | | | |
    b c d
```


#### 代码
```java
public class Solution {
    public List<String> generatePalindromes(String s) {
        List<String> result = new ArrayList<>();
        char[] half = new char[s.length()/2];
        char[] core = new char[1];
        if (!canGeneratePalindrome(s,half,core)) { return result; }
        Set<String> permutations = permutation(new String(half));
        if (permutations.isEmpty() && core[0] != '\0') { result.add(new String(core)); } // s长度为1，只有一个单核
        for (String str : permutations) {
            StringBuilder sb = new StringBuilder(str);
            String mid = (core[0] == '\0')? "" : new String(core);
            result.add(sb.toString() + mid + sb.reverse().toString());
        }
        return result;
    }
    /*
     * 判断能否生成回文。如果能的，拆解出单核，以及对称的一半字符，备用。
     * 字符串长度为零，返回false。
     */
    private boolean canGeneratePalindrome(String s, char[] half, char[] core) {
        int len = s.length();
        if (len == 0) { return false; }
        Set<Character> set = new HashSet<>();
        int cur = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (!set.add(c)) {
                set.remove(c);
                half[cur++] = c;
            }
        }
        int size = set.size();
        if (size == 1) { core[0] = (char)set.toArray()[0]; }
        return size < 2;
    }
    /* 比Solution3更快的产生全排列。用自底向上的动态规划 */
    private Set<String> permutation(String letters) {
        Set<String> result = new HashSet<>();
        if (letters.length() == 0) { return result; }
        if (letters.length() == 1) { result.add(letters); return result ; }
        char c = letters.charAt(0);
        Set<String> subSet = permutation(letters.substring(1));
        for (String str : subSet) {
            int len = str.length();
            for (int i = 0; i <= len; i++) {
                result.add(str.substring(0,i) + c + str.substring(i,len));
            }
        }
        return result;
    }
}
```

#### 结果
![palindrome-permutation-two-3](/images/leetcode/palindrome-permutation-two-3.png)
