---
layout: post
title: "Leetcode - Algorithm - Online Election "
date: 2019-02-27 02:33:26
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: [""]
level: ""
description: >
---

### 题目
In an election, the "i-th" vote was cast for `persons[i]` at time `times[i]`.

Now, we would like to implement the following query function: TopVotedCandidate.q(int t) will return the number of the person that was leading the election at time t.  

Votes cast at time t will count towards our query.  In the case of a tie, the most recent vote (among tied candidates) wins.

Example 1:
```
Input: ["TopVotedCandidate","q","q","q","q","q","q"], [[[0,1,1,0,0,1,0],[0,5,10,15,20,25,30]],[3],[12],[25],[15],[24],[8]]
Output: [null,0,1,1,0,0,1]
Explanation:
At time 3, the votes are [0], and 0 is leading.
At time 12, the votes are [0,1,1], and 1 is leading.
At time 25, the votes are [0,1,1,0,0,1], and 1 is leading (as ties go to the most recent vote.)
This continues for 3 more queries at time 15, 24, and 8.
```

Note:
* 1 <= persons.length = times.length <= 5000
* 0 <= persons[i] <= persons.length
* times is a strictly increasing array with all elements in [0, 10^9].
* TopVotedCandidate.q is called at most 10000 times per test case.
* TopVotedCandidate.q(int t) is always called with t >= times[0].

### 二分查找
考虑到每次排名的变化都是发生在新的投票到来的时候。因此只需要记录每次新投票到来的时候的此时此刻的领先候选人，做成一个表。查询的时候，只需要用二分查找到表里去搜索即可。

比如，我们的例子，在时刻`[0,5,10,15,20,25,30]`，投票如下`[0,1,1,0,0,1,0]`，制成的当选者表格如下，
```
0,  5,  10, 15, 20, 25, 30
0,  1,  1,  0,  0,  1,  0
```

想知道时刻`12`的领先者，可以在表中二分查找`12`，如果没有找到准确的时刻，则往前找一个领先者，
```
            12的插入位置
            |
0,  5,  10, 15, 20, 25, 30
0,  1,  1,  0,  0,  1,  0
        |
        对应的领先者
```

如果找`15`时刻的领先者，找到准确的对应时刻了，就返回当前位置的领先者，
```
            15的插入位置
            |
0,  5,  10, 15, 20, 25, 30
0,  1,  1,  0,  0,  1,  0
            |
            对应的领先者
```

#### 代码
```java
class TopVotedCandidate {

    private final int maxCandidates = 5000;
    private int[] timeScopes;
    private int[] winners;

    public TopVotedCandidate(int[] persons, int[] times) {
        int voteSize = persons.length;
        timeScopes = new int[voteSize];
        winners = new int[voteSize];
        int[] blackboard = new int[maxCandidates];
        int leaderCount = 0, leader = 0;
        for (int i = 0; i < voteSize; i++) {
            int vote = persons[i];
            int time = times[i];
            if (++blackboard[vote] >= leaderCount) {
                leaderCount = blackboard[vote];
                leader = vote;
            }
            timeScopes[i] = time;
            winners[i] = leader;
        }
    }

    public int q(int t) {
        int idx = Arrays.binarySearch(timeScopes, t);
        if (idx >= 0) {
            return winners[idx];
        } else {
            return winners[-(idx + 1) - 1];
        }
    }
}
```

#### 结果
![online-election-1](/images/leetcode/online-election-1.png)
