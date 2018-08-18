---
layout: post
title: "Leetcode - Algorithm - Logger Rate Limiter "
date: 2018-08-18 16:11:13
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["hash table","design"]
level: "easy"
description: >
---

### 题目
Design a logger system that receive stream of messages along with its timestamps, each message should be printed if and only if it is not printed in the last 10 seconds.

Given a message and a timestamp (in seconds granularity), return true if the message should be printed in the given timestamp, otherwise returns false.

It is possible that several messages arrive roughly at the same time.

Example:
```
Logger logger = new Logger();

// logging string "foo" at timestamp 1
logger.shouldPrintMessage(1, "foo"); returns true;

// logging string "bar" at timestamp 2
logger.shouldPrintMessage(2,"bar"); returns true;

// logging string "foo" at timestamp 3
logger.shouldPrintMessage(3,"foo"); returns false;

// logging string "bar" at timestamp 8
logger.shouldPrintMessage(8,"bar"); returns false;

// logging string "foo" at timestamp 10
logger.shouldPrintMessage(10,"foo"); returns false;

// logging string "foo" at timestamp 11
logger.shouldPrintMessage(11,"foo"); returns true;
```

### 用一个Map记录每个单词最后一次出现的时间

#### 代码
```java
class Logger {

        public Logger() {
            history = new HashMap<String,Integer>();
        }
        public boolean shouldPrintMessage(int timestamp, String message) {
            if (!history.containsKey(message) || timestamp - history.get(message) >= 10) {
                    history.put(message,timestamp);
                    return true;
            }
            return false;
        }

        /** ============= 【私有成员】 ================ */

        private Map<String,Integer> history;

}
```

#### 结果
![logger-rate-limiter-1](/images/leetcode/logger-rate-limiter-1.png)
