---
layout: post
title: "Leetcode - Algorithm - Exclusive Time Of Functions "
date: 2018-09-17 01:35:13
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["stack"]
level: "medium"
description: >
---

### 题目
Given the running logs of n functions that are executed in a nonpreemptive single threaded CPU, find the exclusive time of these functions.

Each function has a unique id, start from 0 to n-1. A function may be called recursively or by another function.

A log is a string has this format : `function_id:start_or_end:timestamp`. For example, `0:start:0` means function 0 starts from the very beginning of time 0. `0:end:0` means function 0 ends to the very end of time 0.

Exclusive time of a function is defined as the time spent within this function, the time spent by calling other functions should not be considered as this function's exclusive time. You should return the exclusive time of each function sorted by their function id.

Example 1:
```
Input:
n = 2
logs =
["0:start:0",
 "1:start:2",
 "1:end:5",
 "0:end:6"]
Output:[3, 4]
Explanation:
Function 0 starts at time 0, then it executes 2 units of time and reaches the end of time 1.
Now function 0 calls function 1, function 1 starts at time 2, executes 4 units of time and end at time 5.
Function 0 is running again at time 6, and also end at the time 6, thus executes 1 unit of time.
So function 0 totally execute 2 + 1 = 3 units of time, and function 1 totally execute 4 units of time.
```

Note:
* Input logs will be sorted by timestamp, NOT log id.
* Your output should be sorted by function id, which means the 0th element of your output corresponds to the exclusive time of function 0.
* Two functions won't start or end at the same time.
* Functions could be called recursively, and will always end.
* 1 <= n <= 100

### 用`Stack`模拟线程栈
多看几个例子。例子1：
```
n = 2
logs = "0:start:0", "1:start:2", "1:end:5", "0:end:6"
output = [3,4]
```
![exclusive-time-of-functions-a](/images/leetcode/exclusive-time-of-functions-a.png)

例子2：
```
n = 1
logs = "0:start:0","0:start:1","0:start:2","0:end:3","0:end:4","0:end:5"
output = [6]
```
![exclusive-time-of-functions-b](/images/leetcode/exclusive-time-of-functions-b.png)

例子3：
```
n = 3
logs = "0:start:0","0:end:0","1:start:1","1:end:1","2:start:2","2:end:2","2:start:3","2:end:3"
output = [1,1,2]
```
![exclusive-time-of-functions-c](/images/leetcode/exclusive-time-of-functions-c.png)

因为总线程数`n`是已知的，所以统计时间总长可以用一个`int[n]`的数组。至于线程调用顺序，可以用另外一个`int[n]`记录。

#### 代码
```java
class Solution {
    public int[] exclusiveTime(int n, List<String> logs) {
        // function stack
        LinkedList<Integer> stack = new LinkedList<>();
        // time accumulation
        int[] timeTable = new int[n];
        Arrays.fill(timeTable, -1);
        // the order that functions are called
        int[] callOrder = new int[n];
        int callOrderP = 0;
        // current stack frame
        int runningId = -1; // current function
        int runAt = -1;   // current function start time
        for (String log : logs) {
            // extract log info
            int first = log.indexOf(':');
            int second = log.lastIndexOf(':');
            int id = Integer.parseInt(log.substring(0, first));
            boolean isStart = (log.substring(first + 1, second).equals("start"))? true : false;
            int time = Integer.parseInt(log.substring(second + 1, log.length()));
            if (isStart) {
                // suspend current function
                if (runningId >= 0) {
                    timeTable[runningId] += (time - runAt); // current function end at [time - 1]
                    stack.push(runningId);
                }
                // run new function
                if (timeTable[id] < 0) {
                    callOrder[callOrderP++] = id;
                    timeTable[id] = 0;
                }
                runningId = id;
                runAt = time;
            } else {
                // kill current function
                timeTable[id] += (time - runAt + 1); // current function end at [time]
                // call back father function
                if (!stack.isEmpty()) {
                    runningId = stack.pop();
                    runAt = time + 1;
                } else {
                    runningId = -1;
                }
            }
        }
        int[] res = new int[n];
        for (int i = 0; i < callOrderP; i++) {
            res[i] = timeTable[callOrder[i]];
        }
        return res;
    }
}
```

#### 结果
![exclusive-time-of-functions-1](/images/leetcode/exclusive-time-of-functions-1.png)
