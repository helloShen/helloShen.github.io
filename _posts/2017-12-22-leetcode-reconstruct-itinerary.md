---
layout: post
title: "Leetcode - Algorithm - Reconstruct Itinerary "
date: 2017-12-22 20:10:06
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search","backtracking"]
level: "medium"
description: >
---

### 题目
Given a list of airline tickets represented by pairs of departure and arrival airports `[from, to]`, reconstruct the itinerary in order. All of the tickets belong to a man who departs from `JFK`. Thus, the itinerary must begin with `JFK`.

Note:
* If there are multiple valid itineraries, you should return the itinerary that has the smallest lexical order when read as a single string. For example, the itinerary `["JFK", "LGA"]` has a smaller lexical order than `["JFK", "LGB"]`.
* All airports are represented by three capital letters (IATA code).
* You may assume all tickets form at least one valid itinerary.


Example 1:
```
tickets = [["MUC", "LHR"], ["JFK", "MUC"], ["SFO", "SJC"], ["LHR", "SFO"]]
Return ["JFK", "MUC", "LHR", "SFO", "SJC"].
```
Example 2:
```
tickets = [["JFK","SFO"],["JFK","ATL"],["SFO","ATL"],["ATL","JFK"],["ATL","SFO"]]
Return ["JFK","ATL","JFK","SFO","ATL","SFO"].
Another possible reconstruction is ["JFK","SFO","ATL","JFK","ATL","SFO"]. But it is larger in lexical order.
```

### 利用回溯算法做DFS探索
为了不遍历所有的情况，需要保证第一个走通的路径即是按字母排序最小的解，需要在`Map`里，预先将目的地机场名称按字母排序。

#### 代码
```java
class Solution {
    public List<String> findItinerary(String[][] tickets) {
        Map<String,List<String>> map = getMap(tickets);
        return dfs(map,"JFK");
    }
    private Map<String,List<String>> getMap(String[][] tickets) {
        Map<String,List<String>> map = new HashMap<>();
        for (String[] ticket : tickets) {
            if (map.containsKey(ticket[0])) {
                map.get(ticket[0]).add(ticket[1]);
            } else {
                map.put(ticket[0],new ArrayList<String>(Arrays.asList(new String[]{ticket[1]})));
            }
        }
        for (Map.Entry<String,List<String>> entry : map.entrySet()) {
            Collections.sort(entry.getValue());
        }
        return map;
    }
    private List<String> dfs(Map<String,List<String>> map, String from) {
        if (map.isEmpty()) {
            return new ArrayList<String>(Arrays.asList(new String[]{from}));
        }
        List<String> destinies = map.get(from);
        if (destinies == null) { return null; }
        for (int i = 0; i < destinies.size(); i++) {
            String to = destinies.remove(i);
            if (destinies.isEmpty()) {
                map.remove(from);
            }
            List<String> subItinerary = dfs(map,to);
            if (subItinerary != null) {
                subItinerary.add(0,from);
                return subItinerary;
            }
            if (destinies.isEmpty()) {
                map.put(from,destinies);
            }
            destinies.add(i,to);
        }
        return null;
    }
}
```

#### 结果
![reconstruct-itinerary-1](/images/leetcode/reconstruct-itinerary-1.png)
