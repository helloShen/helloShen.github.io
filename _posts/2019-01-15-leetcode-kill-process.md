---
layout: post
title: "Leetcode - Algorithm - Kill Process "
date: 2019-01-15 18:32:49
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["breadth first search", "tree"]
level: "medium"
description: >
---

### 题目
Given n processes, each process has a unique PID (process id) and its PPID (parent process id).

Each process only has one parent process, but may have one or more children processes. This is just like a tree structure. Only one process has PPID that is 0, which means this process has no parent process. All the PIDs will be distinct positive integers.

We use two list of integers to represent a list of processes, where the first list contains PID for each process and the second list contains the corresponding PPID.

Now given the two lists, and a PID representing a process you want to kill, return a list of PIDs of processes that will be killed in the end. You should assume that when a process is killed, all its children processes will be killed. No order is required for the final answer.

Example 1:
```
Input:
pid =  [1, 3, 10, 5]
ppid = [3, 0, 5, 3]
kill = 5
Output: [5,10]
Explanation:
           3
         /   \
        1     5
             /
            10
Kill 5 will also kill 10.
```

Note:
1. The given kill id is guaranteed to be one of the given PIDs.
2. n >= 1.

### BFS挖出整棵子树
把进程结构看成一棵树，要杀死某个进程，必定也要杀死他的所有子进程。
```
          3
        /   \
       1     5 <-- 如果目标是5，则删除以5为根的整棵子树
            /
           10
```

因为题目给出了所有进程对应的父进程，所以很容易找出任意进程的所有子进程。所以BFS一层一层顺藤摸瓜很简单。


#### 代码
```java
class Solution {
    // BFS
    public List<Integer> killProcess(List<Integer> pid, List<Integer> ppid, int kill) {
        List<Integer> res = new ArrayList<>();
        Map<Integer, List<Integer>> childrensMap = statistic(pid, ppid);
        List<Integer> queue = new LinkedList<>();
        queue.add(kill);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int target = queue.remove(0);
                res.add(target);
                if (childrensMap.containsKey(target)) queue.addAll(childrensMap.get(target));
            }
        }
        return res;
    }

    // find all childrens of each process
    private Map<Integer, List<Integer>> statistic(List<Integer> pid, List<Integer> ppid) {
        Map<Integer, List<Integer>> childrensMap = new HashMap<>();
        for (int i = 0; i < pid.size(); i++) {
            int id = pid.get(i);
            int parentId = ppid.get(i);
            if (!childrensMap.containsKey(parentId)) childrensMap.put(parentId, new ArrayList<Integer>());
            childrensMap.get(parentId).add(id);
        }
        return childrensMap;
    }
}
```

#### 结果
![kill-process-1](/images/leetcode/kill-process-1.png)
