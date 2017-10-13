---
layout: post
title: "Leetcode - Algorithm - Find The Celebrity "
date: 2017-10-13 17:57:31
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["array"]
level: "medium"
description: >
---

### 题目
Suppose you are at a party with n people (labeled from `0` to `n - 1`) and among them, there may exist one celebrity. The definition of a celebrity is that all the other `n - 1` people know him/her but he/she does not know any of them.

Now you want to find out who the celebrity is or verify that there is not one. The only thing you are allowed to do is to ask questions like: "Hi, A. Do you know B?" to get information of whether A knows B. You need to find out the celebrity (or verify there is not one) by asking as few questions as possible (in the asymptotic sense).

You are given a helper function `bool knows(a, b)` which tells you whether A knows B. Implement a function `int findCelebrity(n)`, your function should minimize the number of calls to knows.

Note: There will be exactly one celebrity if he/she is in the party. Return the celebrity's label if there is a celebrity in the party. If there is no celebrity, return `-1`.

### 朴素地一个个问，$$O(n^2)$$
对每一个人，都做完整的检查：问他是否认识其他每一个人，然后问每一个人是否认识他。

#### 代码
```java
/* The knows API is defined in the parent class Relation.
      boolean knows(int a, int b); */

public class Solution extends Relation {
    public int findCelebrity(int n) {
        outFor:
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j == i) { continue; }
                if (!knows(j,i)) { continue outFor; }   // 有人不认识此人，此人不是名人
                if (knows(i,j)) { continue outFor; }    // 此人认识某个人，此人不是名人
            }
            return i;
        }
        return -1;
    }
}
```

#### 结果
![find-the-celebrity-1](/images/leetcode/find-the-celebrity-1.png)


### 排除法，**O(n)**
这个问题的关键在于，每次问一个人，至少都能排除一个人：
> 问A是否认识B？ 如果A认识B，A肯定不是名人。如果A不认识B，B肯定不是名人。

这样，前`n-1`次提问，就能排除掉`n-1`个人。剩下的最后一人，我们需要对它做完整的检查，才能确定他是不是真正的名人（因为也有可能没有名人）。

所以一开始用一个`HashSet`表示候选人的名单，然后逐渐从里面删除候选人。

#### 代码
```java
/* The knows API is defined in the parent class Relation.
      boolean knows(int a, int b); */

public class Solution extends Relation {
    public int findCelebrity(int n) {
        Set<Integer> remain = new HashSet<>();
        for (int i = 0; i < n ; i++) { remain.add(i); }
        outFor:
        for (int i = 0; i < n; i++) {
            if (!remain.contains(i)) { continue; }
            for (int j = 0; j < n; j++) {
                if (j == i || !remain.contains(j)) { continue; }
                if (knows(j,i)) {
                    remain.remove(j); // j认识i，j不可能是名人
                } else {
                    remain.remove(i); // j不认识i，i不可能是名人
                    continue outFor;
                }
            }
        }
        // 严格检查剩下的最后一人
        int candidate = remain.iterator().next();
        for (int i = 0; i < n; i++) {
            if (i == candidate) { continue; }
            if (knows(candidate,i)) { return -1; }  // 剩下的人认识某个人，此人不是名人
            if (!knows(i,candidate)) { return -1; } // 剩下的人有人不认识他，他也不是名人
        }
        return candidate;
    }
}
```

#### 结果
![find-the-celebrity-2](/images/leetcode/find-the-celebrity-2.png)


### 使用 **O(1)** 的额外空间选出那个候选人
遍历每个人，先假设`i`是临时候选人，如果`i`认识`i+1`，则`i`不可能是名人，`i+1`成为新的临时候选人。如果`i`不认识`i+1`，则`i+1`不可能是名人，`i`继续保持临时候选人身份，继续往下遍历。

这样就不需要用一个`Set`容器做辅助。

这里还有一个小偷懒，假设`k`是候选人，完整检查最后的候选人时，不需要重复检查候选人是否认识`[k+1,n]`的人，因为已经检查过，结果为不认识。

#### 代码
```java
/* The knows API is defined in the parent class Relation.
      boolean knows(int a, int b); */

public class Solution extends Relation {
    public int findCelebrity(int n) {
        int candidate = 0;
        for(int i = 1; i < n; i++){
            if(knows(candidate, i)) { candidate = i; }
        }
        for(int i = 0; i < n; i++){
            if (i < candidate && (knows(candidate,i) || !knows(i,candidate))) { return -1; }
            if (i > candidate && !knows(i,candidate)) { return -1; }
        }
        return candidate;
    }
}
```

#### 结果
![find-the-celebrity-3](/images/leetcode/find-the-celebrity-3.png)
