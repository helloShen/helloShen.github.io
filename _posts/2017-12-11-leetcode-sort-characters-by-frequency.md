---
layout: post
title: "Leetcode - Algorithm - Sort Characters By Frequency "
date: 2017-12-11 19:35:32
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["heap","bucket","sort","hash table"]
level: "medium"
description: >
---

### 题目
Given a string, sort it in decreasing order based on the frequency of characters.

Example 1:
```
Input:
"tree"

Output:
"eert"

Explanation:
'e' appears twice while 'r' and 't' both appear once.
So 'e' must appear before both 'r' and 't'. Therefore "eetr" is also a valid answer.
```

Example 2:
```
Input:
"cccaaa"

Output:
"cccaaa"

Explanation:
Both 'c' and 'a' appear three times, so "aaaccc" is also a valid answer.
Note that "cacaca" is incorrect, as the same characters must be together.
```

Example 3:
```
Input:
"Aabb"

Output:
"bbAa"

Explanation:
"bbaA" is also a valid answer, but "Aabb" is incorrect.
Note that 'A' and 'a' are treated as two different characters.
```

### 先计算词频，然后按词频排序，用`HashMap`，$$O(n\log_{}{n})$$

#### 代码
```java
class Solution {
    public String frequencySort(String s) {
        Map<Character,Integer> freqMap = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            freqMap.put(c,freqMap.getOrDefault(c,0)+1);
        }
        List<Map.Entry<Character,Integer>> freqList = new ArrayList<>(freqMap.entrySet());
        Collections.sort(freqList,new Comparator<Map.Entry<Character,Integer>>(){
            public int compare(Map.Entry<Character,Integer> a, Map.Entry<Character,Integer> b) {
                return b.getValue() - a.getValue();
            }
        });
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character,Integer> entry : freqList) {
            for (int i = 0; i < entry.getValue(); i++) {
                sb.append(entry.getKey());
            }
        }
        return sb.toString();
    }
}
```

#### 结果
![sort-characters-by-frequency-1](/images/leetcode/sort-characters-by-frequency-1.png)


### 用Heap（`PriorityQueue`），$$O(n\log_{}{n})$$
如果不想排序，而是在插入元素的过程中就维护好元素的顺序，可以用Heap，典型的实现类是`PriorityQueue`。但复杂度不变。

使用`PriorityQueue`的时候要注意，要按照顺序输出元素要用`poll()`方法弹出元素。常规的用迭代器遍历容器得到的会是内部红黑树的顺序(乱序)。

#### 代码
```java
class Solution {
    public String frequencySort(String s) {
        if (s.length() < 2) { return s; }
        PriorityQueue<StringBuilder> queue = new PriorityQueue<>(s.length(), new Comparator<StringBuilder>(){
            public int compare(StringBuilder sb1, StringBuilder sb2) {
                return sb2.length() - sb1.length();
            }
        });
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            StringBuilder old = null;
            Iterator<StringBuilder> ite = queue.iterator();
            while (ite.hasNext()) {
                StringBuilder str = ite.next();
                if (str.charAt(0) == c) {
                    ite.remove();
                    old = str;
                    break;
                }
            }
            queue.offer((old == null)? new StringBuilder().append(c) : (old.append(c)));
        }
        StringBuilder sb = new StringBuilder();
        while(!queue.isEmpty()) {
            sb.append(queue.poll());
        }
        return sb.toString();
    }
}
```

#### 结果
![sort-characters-by-frequency-2](/images/leetcode/sort-characters-by-frequency-2.png)


### Bucket法，$$O(n)$$
还是先用`HashMap`统计词频。然后开一个数组，将词频相同搞得字符存放在数组相应的bucket内（以词频为数组下标）。需要一个和原始字符串长度相等的数组。

#### 代码
```java
class Solution {
    public String frequencySort(String s) {
        if (s.length() < 2) { return s; }
        Map<Character,Integer> freqMap = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            freqMap.put(c,freqMap.getOrDefault(c,0)+1);
        }
        StringBuilder[] buckets = new StringBuilder[s.length()+1];
        for (Map.Entry<Character,Integer> entry : freqMap.entrySet()) {
            int freq = entry.getValue();
            char c = entry.getKey();
            if (buckets[freq] == null) { buckets[freq] = new StringBuilder(); }
            buckets[freq].append(c);
        }
        StringBuilder res = new StringBuilder();
        for (int i = buckets.length - 1; i > 0; i--) {
            StringBuilder sb = buckets[i];
            if (sb != null) {
                for (int j = 0; j < sb.length(); j++) {
                    char c = sb.charAt(j);
                    for (int k = 0; k < i; k++) {
                        res.append(c);
                    }
                }
            }
        }
        return res.toString();
    }
}
```

#### 结果
![sort-characters-by-frequency-3](/images/leetcode/sort-characters-by-frequency-3.png)
