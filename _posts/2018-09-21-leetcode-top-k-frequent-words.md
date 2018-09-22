---
layout: post
title: "Leetcode - Algorithm - Top K Frequent Words "
date: 2018-09-21 20:54:25
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","heap","priority queue"]
level: "medium"
description: >
---

### 题目
Given a non-empty list of words, return the k most frequent elements.

Your answer should be sorted by frequency from highest to lowest. If two words have the same frequency, then the word with the lower alphabetical order comes first.

Example 1:
```
Input: ["i", "love", "leetcode", "i", "love", "coding"], k = 2
Output: ["i", "love"]
Explanation: "i" and "love" are the two most frequent words.
    Note that "i" comes before "love" due to a lower alphabetical order.
```
Example 2:
```
Input: ["the", "day", "is", "sunny", "the", "the", "the", "sunny", "is", "is"], k = 4
Output: ["the", "is", "sunny", "day"]
Explanation: "the", "is", "sunny" and "day" are the four most frequent words,
    with the number of occurrence being 4, 3, 2 and 1 respectively.
```
Note:
* You may assume k is always valid, 1 ≤ k ≤ number of unique elements.
* Input words contain only lowercase letters.

Follow up:
* Try to solve it in O(n log k) time and O(n) extra space.


### 直接排序
先用`HashMap`统计词频。然后转换成`List`直接排序。

#### 代码
```java
class Solution {

    public List<String> topKFrequent(String[] words, int k) {
        Map<String, Integer> dic = new HashMap<>();
        for (String word : words) {
            if (!dic.containsKey(word)) {
                dic.put(word, 1);
            } else {
                dic.put(word, dic.get(word) + 1);
            }
        }
        List<Map.Entry<String, Integer>> dicList = new ArrayList<>(dic.entrySet());
        Collections.sort(dicList, new Comparator<Map.Entry<String, Integer>>(){
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b){
                int diff = b.getValue() - a.getValue();
                if (diff != 0) return diff;
                char[] wordA = a.getKey().toCharArray();
                char[] wordB = b.getKey().toCharArray();
                int ap = 0, bp = 0;
                while (ap < wordA.length && bp < wordB.length) {
                    char ca = wordA[ap++];
                    char cb = wordB[bp++];
                    if (ca != cb) return ca - cb;
                }
                if (ap == wordA.length && bp == wordB.length) return 0;
                if (ap == wordA.length) return -1;
                return 1; // bp == wordB.length
            }
        });
        List<String> result = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            result.add(dicList.get(i).getKey());
        }
        return result;
    }

}
```

#### 结果
![top-k-frequent-words-1](/images/leetcode/top-k-frequent-words-1.png)


#### 用String自带的`compareTo`
因为String实现了`Comparable`接口，可以直接比较大小。缺点就是效率低了一点。
```java
class Solution {

    public List<String> topKFrequent(String[] words, int k) {
        Map<String, Integer> dic = new HashMap<>();
        for (String word : words) {
            if (!dic.containsKey(word)) {
                dic.put(word, 1);
            } else {
                dic.put(word, dic.get(word) + 1);
            }
        }
        List<Map.Entry<String, Integer>> dicList = new ArrayList<>(dic.entrySet());
        Collections.sort(dicList, new Comparator<Map.Entry<String, Integer>>(){
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b){
                int diff = b.getValue() - a.getValue();
                if (diff != 0) return diff;
                return a.getKey().compareTo(b.getKey());
            }
        });
        List<String> result = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            result.add(dicList.get(i).getKey());
        }
        return result;
    }

}
```

#### 结果
![top-k-frequent-words-2](/images/leetcode/top-k-frequent-words-2.png)


### 用`Heap`保持有序
不想刻意排序，也可以在统计完词频以后，把所有数据存入`PriorityQueue`。

#### 代码
```java
class Solution {

    public List<String> topKFrequent(String[] words, int k) {
        Map<String, Integer> dic = new HashMap<>();
        for (String word : words) {
            if (!dic.containsKey(word)) {
                dic.put(word, 1);
            } else {
                dic.put(word, dic.get(word) + 1);
            }
        }
        PriorityQueue<Map.Entry<String, Integer>> order = new PriorityQueue<>(words.length, new Comparator<Map.Entry<String, Integer>>(){
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b){
                int diff = b.getValue() - a.getValue();
                if (diff != 0) return diff;
                char[] wordA = a.getKey().toCharArray();
                char[] wordB = b.getKey().toCharArray();
                int ap = 0, bp = 0;
                while (ap < wordA.length && bp < wordB.length) {
                    char ca = wordA[ap++];
                    char cb = wordB[bp++];
                    if (ca != cb) return ca - cb;
                }
                if (ap == wordA.length && bp == wordB.length) return 0;
                if (ap == wordA.length) return -1;
                return 1; // bp == wordB.length
            }
        });
        for (Map.Entry<String, Integer> entry : dic.entrySet()) {
            order.add(entry);
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            result.add(order.poll().getKey());
        }
        return result;
    }
}
```

#### 结果
![top-k-frequent-words-3](/images/leetcode/top-k-frequent-words-3.png)
