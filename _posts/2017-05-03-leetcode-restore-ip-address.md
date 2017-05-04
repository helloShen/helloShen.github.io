---
layout: post
title: "Leetcode - Algorithm - Restore Ip Address "
date: 2017-05-03 15:26:33
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["backtracking","string"]
level: "medium"
description: >
---

### 题目
Given a string containing only digits, restore it by returning all possible valid IP address combinations.

for example:
given `25525511135`,

return `["255.255.11.135", "255.255.111.35"]`. (order does not matter)

注意，这里`0`是不能折叠的，比如`100111`不能写成`1.001.1.1`或者`1.1.1.1`。`001`这种写法是不合法的。

### DFS(Depth First Search)递归

#### 代码
```java
public class Solution {
    public List<String> restoreIpAddresses(String s) {
        Set<String> res = new HashSet<>();
        dfs(s,0,1,"",0,res);
        dfs(s,0,2,"",0,res);
        dfs(s,0,3,"",0,res);
        return new ArrayList<String>(res);
    }
    public void dfs(String s, int lo, int hi, String temp, int numSection, Set<String> res) { // lo inclusive, hi exclusive
        int length = s.length();
        if (numSection == 4) {
            if (lo == length) { res.add(temp.substring(1)); }
            return;
        }
        if (lo >= length || hi > length) { return; }
        if (s.charAt(lo) == '0' && hi - lo > 1) { return; } // can't be "001"
        int section = Integer.parseInt(s.substring(lo,hi));
        if (section <= 255) {
            temp = temp + "." + section;
            dfs(s,hi,hi+1,temp,numSection+1,res);
            dfs(s,hi,hi+2,temp,numSection+1,res);
            dfs(s,hi,hi+3,temp,numSection+1,res);
        }
    }
}
```

可以把判断每个数字是不是合法的`0~255`的动作独立出来。
```java
public List<String> restoreIpAddresses(String s) {
    Set<String> res = new HashSet<>();
    dfs(s,0,1,"",0,res);
    dfs(s,0,2,"",0,res);
    dfs(s,0,3,"",0,res);
    return new ArrayList<String>(res);
}
public void dfs(String s, int lo, int hi, String ip, int count, Set<String> res) {
    int length = s.length();
    if (count == 4) {
        if (lo == length) { res.add(ip.substring(1)); }
        return;
    }
    if (lo >= length || hi > length) { return; }
    String section = s.substring(lo,hi);
    if (isValide(section)) {
        ip = ip + "." + section;
        dfs(s,hi,hi+1,ip,count+1,res);
        dfs(s,hi,hi+2,ip,count+1,res);
        dfs(s,hi,hi+3,ip,count+1,res);
    }
}
public boolean isValide(String s) {
    int length = s.length();
    if (s.charAt(0) == '0') { // can't be 001
        return length == 1;
    }
    return Integer.parseInt(s) < 256;
}
```

#### 结果
![restore-ip-address-1](/images/leetcode/restore-ip-address-1.png)


### 回溯
如果不是用一个`String`记录`IP`，换成`List`，要记得回退。

#### 代码
```java
public class Solution {
    public List<String> restoreIpAddresses(String s) {
        Set<String> res = new HashSet<>();
        List<Integer> ip = new ArrayList<>();
        backtracking(s,0,1,ip,res);
        backtracking(s,0,2,ip,res);
        backtracking(s,0,3,ip,res);
        return new ArrayList<String>(res);
    }
    public void backtracking(String s, int lo, int hi, List<Integer> ip, Set<String> res) { // lo inclusive, hi exclusive
        int length = s.length();
        if (ip.size() == 4) {
            if (lo == length) { res.add(toIp(ip)); }
            return;
        }
        if (lo >= length || hi > length) { return; }
        String section = s.substring(lo,hi);
        if (isValide(section)) {
            ip.add(section);
            backtracking(s,hi,hi+1,ip,res);
            backtracking(s,hi,hi+2,ip,res);
            backtracking(s,hi,hi+3,ip,res);
            ip.remove(ip.size()-1); // 回退
        }
    }
    public boolean isValide(String s) {
        int length = s.length();
        if (s.charAt(0) == '0') { // can't be 001
            return length == 1;
        }
        return Integer.parseInt(s) < 256;
    }
    public String toIp(List<Integer> ip) {
        StringBuilder sb = new StringBuilder();
        for (int i : ip) {
            sb.append("." + i);
        }
        return sb.toString().substring(1);
    }
}
```

#### 结果
![restore-ip-address-2](/images/leetcode/restore-ip-address-2.png)


### 三个指针
因为IP地址必须有四段。所以可以用三个指针将数字分成四段，然后判断每段的合法性。用一个简单的三层套嵌迭代就可以完成。

#### 代码
```java
public class Solution {
    public List<String> restoreIpAddresses(String s) {
        List<String> res = new ArrayList<>();
        int len = s.length();
        if (len < 4) { return res; }
        for (int i = 1; i < 4 && i < len-2; i++) {
            for (int j = i + 1; j < i+4 && j < len-1; j++) {
                for (int k = j + 1; k < j+4 && k < len; k++) {
                    String one = s.substring(0,i), two = s.substring(i,j), three = s.substring(j,k), four = s.substring(k,len);
                    if (isValide(one) && isValide(two) && isValide(three) && isValide(four)) {
                        res.add(one + "." + two + "." + three + "." + four);
                    }
                }
            }
        }
        return res;
    }
    public boolean isValide(String s) {
        int length = s.length();
        if (length > 3) { return false; }
        if (s.charAt(0) == '0') { return length == 1; }
        int num = Integer.parseInt(s);
        return num < 256;
    }
}
```

#### 结果
![restore-ip-address-3](/images/leetcode/restore-ip-address-3.png)

### 注意剪枝的话，效率还能提高
处理比如`00101010`，当`i`指向第二个`0`，`00`已经不合法，但这种情况下，程序还是会按部就班完成整个三层迭代。如果发现不合法，马上剪枝，跳出迭代，可以提高效率。

#### 代码
```java
public class Solution {
    public List<String> restoreIpAddresses(String s) {
        List<String> res = new ArrayList<>();
        int len = s.length();
        if (len < 4) { return res; }
        iLoop:
        for (int i = 1; i < 4 && i < len-2; i++) {
            jLoop:
            for (int j = i + 1; j < i+4 && j < len-1; j++) {
                kLoop:
                for (int k = j + 1; k < j+4 && k < len; k++) {
                    String one = s.substring(0,i), two = s.substring(i,j), three = s.substring(j,k), four = s.substring(k,len);
                    if (!isValide(one)) { break iLoop; } // 迅速失败，剪枝
                    if (!isValide(two)) { break jLoop; } // 迅速失败，剪枝
                    if (!isValide(three)) { break kLoop; } // 迅速失败，剪枝
                    if (!isValide(four)) { continue; } // 迅速失败，剪枝
                    res.add(one + "." + two + "." + three + "." + four);
                }
            }
        }
        return res;
    }
    public boolean isValide(String s) {
        int length = s.length();
        if (length > 3) { return false; }
        if (s.charAt(0) == '0') { return length == 1; }
        int num = Integer.parseInt(s);
        return num < 256;
    }
}
```


#### 结果
剪枝有效哦。
![restore-ip-address-4](/images/leetcode/restore-ip-address-4.png)
