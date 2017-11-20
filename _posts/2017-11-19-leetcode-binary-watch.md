---
layout: post
title: "Leetcode - Algorithm - Binary Watch "
date: 2017-11-19 01:01:10
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["bit manipulation","backtracking"]
level: "easy"
description: >
---

### 题目
A binary watch has 4 LEDs on the top which represent the hours (0-11), and the 6 LEDs on the bottom represent the minutes (0-59).

Each LED represents a zero or one, with the least significant bit on the right.

![binary-watch](/images/leetcode/binary-watch.jpg)

For example, the above binary watch reads "3:25".

Given a non-negative integer n which represents the number of LEDs that are currently on, return all possible times the watch could represent.

Example:
```
Input: n = 1
Return: ["1:00", "2:00", "4:00", "8:00", "0:01", "0:02", "0:04", "0:08", "0:16", "0:32"]
```

Note:
* The order of output does not matter.
* The hour must not contain a leading zero, for example "01:00" is not valid, it should be "1:00".
* The minute must be consist of two digits and may contain a leading zero, for example "10:2" is not valid, it should be "10:02".

### 标准回溯算法

#### 代码
```java
class Solution {
    private static boolean[] hourFlags = new boolean[4];
    private static final int[] hour = new int[]{8,4,2,1};
    private static boolean[] minFlags = new boolean[6];
    private static final int[] min = new int[]{32,16,8,4,2,1};
    private static Set<String> set = new HashSet<>();

    public List<String> readBinaryWatch(int num) {
        init();
        dfs(num,0,0);
        return new ArrayList<String>(set);
    }
    private void init() {
        set.clear();
    }
    /** Standard Backtracking Algorithm */
    private void dfs(int remain, int hours, int mins) {
        if (remain == 0) {
            String time = convert(hours,mins);
            if (!time.isEmpty()) { set.add(time); }
        }
        for (int i = 0; i < hourFlags.length; i++) {
            if (!hourFlags[i]) {
                hourFlags[i] = true;
                dfs(remain-1,hours+hour[i],mins);
                hourFlags[i] = false;
            }
        }
        for (int i = 0; i < minFlags.length; i++) {
            if (!minFlags[i]) {
                minFlags[i] = true;
                dfs(remain-1,hours,mins+min[i]);
                minFlags[i] = false;
            }
        }
    }
    /** return "" when given a not-valide time */
    private String convert(int hours, int mins) {
        if (hours > 11 || mins > 59) { return ""; }
        String time = String.valueOf(hours) + ":";
        if (mins < 10) { time += "0"; }
        return time + String.valueOf(mins);
    }
}
```

#### 结果
通不过。
![binary-watch-1](/images/leetcode/binary-watch-1.png)


### 还是用回溯算法，但把“小时”和“分钟”两部分分开预先算好
比如`x`栈小时指示灯亮和`y`盏分钟指示灯亮的情况，预先算好，当拿到`n = x + y`的情况就可以拿来组合。当然预先算的步骤还是用回溯算法来做。

#### 代码
```java
class Solution {

    private static Map<Integer,Set<String>> hours= new HashMap<>();
    private static Map<Integer,Set<String>> mins = new HashMap<>();

    private static final int[] values = new int[]{1,2,4,8,16,32};
    private static boolean[] hourFlags = new boolean[4];
    private static boolean[] minFlags = new boolean[6];

    private static final void dfsHour(int remain, int sum, Set<String> set) {
        if (remain == 0 && sum < 12) { set.add(String.valueOf(sum)); }
        for (int i = 0; i < hourFlags.length; i++) {
            if (!hourFlags[i]) {
                hourFlags[i] = true;
                dfsHour(remain-1,sum+values[i],set);
                hourFlags[i] = false;
            }
        }
    }
    private static final void dfsMin(int remain, int sum, Set<String> set) {
        if (remain == 0 && sum < 60) {
            String prefix = (sum < 10)? "0" : "";
            set.add((prefix + String.valueOf(sum)));
        }
        for (int i = 0; i < minFlags.length; i++) {
            if (!minFlags[i]) {
                minFlags[i] = true;
                dfsMin(remain-1,sum+values[i],set);
                minFlags[i] = false;
            }
        }
    }
    private static final void init() {
        for (int i = 0; i <= hourFlags.length; i++) {
            Set<String> set = new HashSet<>();
            dfsHour(i,0,set);
            hours.put(i,set);
        }
        for (int i = 0; i <= minFlags.length; i++) {
            Set<String> set = new HashSet<>();
            dfsMin(i,0,set);
            mins.put(i,set);
        }
    }

    static { init(); }

    private static final String SPLITER = ":";

    public List<String> readBinaryWatch(int num) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 6; j++) {
                if (i + j == num) {
                    Set<String> hourCandidates = hours.get(i);
                    Set<String> minCandidates = mins.get(j);
                    for (String hc : hourCandidates) {
                        for (String mc : minCandidates) {
                            res.add(hc + SPLITER + mc);
                        }
                    }
                }
            }
        }
        return res;
    }
}
```

#### 结果
![binary-watch-2](/images/leetcode/binary-watch-2.png)


### 表驱动
因为可能出现的样本空间不大，完全可以硬编码。下面是所有可能出现的情况，
```
private static final String[][] hours = {
        {"0"},
        {"1", "2", "4", "8"},
        {"3", "5", "6", "9", "10"},
        {"7", "11"}
};
private static final String[][] mins = {
    {"00"},
    {"01", "02", "04", "08", "16", "32"},
    {"03", "05", "06", "09", "10", "12", "17", "18", "20", "24", "33", "34", "36", "40", "48"},
    {"07", "11", "13", "14", "19", "21", "22", "25", "26", "28", "35", "37", "38", "41", "42", "44", "49", "50", "52", "56"},
    {"15", "23", "27", "29", "30", "39", "43", "45", "46", "51", "53", "54", "57", "58"},
    {"31", "47", "55", "59"}
};
```

#### 代码
```java
class Solution {
    private static final String[][] hours = {
            {"0"},
            {"1", "2", "4", "8"},
            {"3", "5", "6", "9", "10"},
            {"7", "11"}
    };
    private static final String[][] mins = {
        {"00"},
        {"01", "02", "04", "08", "16", "32"},
        {"03", "05", "06", "09", "10", "12", "17", "18", "20", "24", "33", "34", "36", "40", "48"},
        {"07", "11", "13", "14", "19", "21", "22", "25", "26", "28", "35", "37", "38", "41", "42", "44", "49", "50", "52", "56"},
        {"15", "23", "27", "29", "30", "39", "43", "45", "46", "51", "53", "54", "57", "58"},
        {"31", "47", "55", "59"}
    };
    private static final String spliter = ":";

    public List<String> readBinaryWatch(int num) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < 4; i++) {       // 不可能4个小时指示灯都亮
            for (int j = 0; j < 6; j++) {   // 不可能6个分钟指示灯都亮
                if (i + j == num) {
                    for (String h : hours[i]) {
                        for (String m : mins[j]) {
                            res.add(h + spliter + m);
                        }
                    }
                }
            }
        }
        return res;
    }
}
```

#### 结果
![binary-watch-3](/images/leetcode/binary-watch-3.png)

### 从`0~11`然后`0~59`这样从最终结果的空间里遍历
思路反过来从结果出发遍历，然后用`Integer.bitcount()`计算`1`位的数量。因为那个表明显就是根据2进制数字的模式做的。这个方法很聪明。

#### 代码
```java
class Solution {
        public List<String> readBinaryWatch(int num) {
        List<String> res = new ArrayList<>();
        for (int h = 0; h < 12; h++) {
            for (int m = 0; m < 60; m++) {
                if (Integer.bitCount(h) + Integer.bitCount(m) == num) {
                    res.add(String.format("%d:%02d",h,m));
                }
            }
        }
        return res;
    }
}
```

#### 结果
![binary-watch-4](/images/leetcode/binary-watch-4.png)
