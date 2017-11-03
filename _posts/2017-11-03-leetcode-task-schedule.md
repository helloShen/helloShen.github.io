---
layout: post
title: "Leetcode - Algorithm - Task Schedule "
date: 2017-11-03 18:52:02
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["math","array","greedy"]
level: "medium"
description: >
---

### 题目
Given a char array representing tasks CPU need to do. It contains capital letters A to Z where different letters represent different tasks.Tasks could be done without original order. Each task could be done in one interval. For each interval, CPU could finish one task or just be idle.

However, there is a non-negative cooling interval n that means between two same tasks, there must be at least n intervals that CPU are doing different tasks or just be idle.

You need to return the least number of intervals the CPU will take to finish all the given tasks.

Example 1:
```
Input: tasks = ["A","A","A","B","B","B"], n = 2
Output: 8
```
Explanation: `A -> B -> idle -> A -> B -> idle -> A -> B`.

Note:
The number of tasks is in the range `[1, 10000]`.
The integer n is in the range `[0, 100]`.

### 数学原理
这是一个数学问题。首先`n`相当于一个窗口。每个窗口里不允许有相同的字母出现。问题最关键的一个硬性约束是：**出现频率最高的字母**。比如`AAAABBBEEFFG`，`n=3`的情况，频率最高的`A`出现了4次，基本框架如下所示，
```
 gap1 gap2 gap3
|    |    |    |
 AXXX AXXX AXXX A
```
然后剩下的字母再往中间空隙填充，每个窗口轮流填充。
```
 gap1 gap2 gap3
|    |    |    |
 AXXX AXXX AXXX A

剩下的`BBBEEFFG`依次往窗口里填充：
 ABXX ABXX ABXX A
 ABEX ABEX ABFX A
 ...
 ...
 ABEF ABEG ABFX A
```
上面例子里填充不满，不满的位置就空着。这时候，最终的长度等于，
> (MaxFreq - 1) * (n + 1) + 1

另一种情况是填满这些空位后，字母还没有用完，比如把上面的例子再加长一点`AAAABBBEEFFGGHHHIIJK`，`n=3`，填满之后如下，
```
填满以后：
ABEF ABEG ABFG A

还剩下："HHHIIJK"
```
这时候再每个窗口的后面继续填充，还是能保证符合要求。这种情况下：
> 最终的长度等于原有数组的长度。

```
ABEFHIK ABEGHI ABFGHJ A
```

最后还要考虑一种可能出现的情况，就是频率最高的字母不止一个，比如`AAAABBBBEEFFG`，模板就变成:
```
ABXX ABXX ABXX AB
```
所以之前的公式修正一下，
> (MaxFreq - 1) * (n + 1) + NumOfMaxFreqTask

最后补充一种情况，`AAABBBCCCDDDEEF`，`n = 2`，模板应该是，这时候`ABCD`的长度已经超过`n+1 = 3`了。
```
ABCD ABCD ABCD
```
但这不需要当做特殊情况考虑，因为这种情况可以和第二种情况合并，
> 最终的长度等于原有数组的长度。

因为，这种情况可以看做`ABC`是模板，并且所有的空槽已经被填满，所以最终的结果一定等于原数组的长度。
```
ABC ABC ABC
```

#### 代码
```java
class Solution {
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (int i = 0; i < tasks.length; i++) {
            freq[tasks[i] - 'A']++;
        }
        Arrays.sort(freq);
        int cur = freq.length - 1;
        int maxSize = freq[cur], num = 0;
        while (cur >= 0 && freq[cur] == maxSize) {
            num++; cur--;
        }
        int standard = (maxSize - 1) * (n + 1) + num;
        return Math.max(tasks.length,standard);
    }
}
```

#### 结果
![task-schedule-1](/images/leetcode/task-schedule-1.png)
