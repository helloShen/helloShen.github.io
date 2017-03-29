---
layout: post
title: "Leetcode - Algorithm - Letter combination of a phone number "
date: 2017-03-29 13:04:01
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["leetcode"]
level: "medium"
description: >
---

### 题目
Given a digit string, return all possible letter combinations that the number could represent.
A mapping of digit to letters (just like on the telephone buttons) is given below.
![phone-keypad](/images/leetcode/phone-keypad.png)
```
Input:Digit string "23"
Output: ["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
```
Note:
Although the above answer is in lexicographical order, your answer could be in any order you want.

### 暴力解法
记录一个键盘数字和字母的映射表。组合字母的时候，遍历现有字符串，在每个字符串的末尾加上当前数字字母表中的每一个数字。

查到 `2 = "abc"`,
```
[a,b,c]
```
查到`3 = def`，删除`a`，加上`ad`,`ae`,`af`。
```
[ad,ae,af,b,c]
```
删除`b`，加上`bd`,`be`,`bf`。
```
[ad,ae,af,bd,be,bf,c]
```
删除`c`，加上`cd`,`ce`,`cf`。
```
[ad,ae,af,bd,be,bf,cd,ce,cf]
```

#### 代码
```java
public class Solution {
    private Map<Character,String> keypad = new HashMap<>();
    {
        keypad.put('1',"");
        keypad.put('2',"abc");
        keypad.put('3',"def");
        keypad.put('4',"ghi");
        keypad.put('5',"jkl");
        keypad.put('6',"mno");
        keypad.put('7',"pqrs");
        keypad.put('8',"tuv");
        keypad.put('9',"wxyz");
        keypad.put('0',"");
    }
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < digits.length(); i++) {
            String letters = keypad.get(digits.charAt(i));
            if (letters != null && letters.length() > 0) {
                ListIterator<String> ite = result.listIterator();
                while (ite.hasNext()) {
                    String old = ite.next();
                    ite.remove();
                    for (int j = 0; j < letters.length(); j++) {
                        ite.add(old + letters.substring(j,j+1));
                    }
                }
                if (result.size() == 0) {
                    for (int j = 0; j < letters.length(); j++) {
                        result.add(letters.substring(j,j+1));
                    }
                }
            }
        }
        return result;
    }
}
```

#### 结果
![phone-number-1](/images/leetcode/phone-number-1.png)


### 二维数组存放映射
不用`Map`，改成用二维数组存放手机数字键的字母映射。

#### 代码
```java
public class Solution {
        private String[][] letterArray = new String[][]{
        {"","","",""},
        {"","","",""},
        {"a","b","c",""},
        {"d","e","f",""},
        {"g","h","i",""},
        {"j","k","l",""},
        {"m","n","o",""},
        {"p","q","r","s"},
        {"t","u","v",""},
        {"w","x","y","z"}
    };
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < digits.length(); i++) {
            int digit = digits.charAt(i)-'0';
            if (result.size() == 0) {
                for (int j = 0; j < 4; j++) {
                    if (letterArray[digit][j] != "") {
                        result.add(letterArray[digit][j]);
                    }
                }
            } else {
                ListIterator<String> ite = result.listIterator();
                while (ite.hasNext()) {
                    String old = ite.next();
                    ite.remove();
                    for (int j = 0; j < 4; j++) {
                        if (letterArray[digit][j] != "") {
                            ite.add(old + letterArray[digit][j]);
                        }
                    }
                }
            }
        }
        return result;
    }
}
```

#### 结果
![phone-number-2](/images/leetcode/phone-number-2.png)


### 简洁版
在最初结果数组为空的时候，加入一个`""`，可以简化逻辑。

#### 代码
```java
public class Solution {
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.length() == 0) { return result; }
        String[] letterPad = new String[]{"","","abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"};
        result.add("");
        for (int i = 0; i < digits.length(); i++) {
            int digit = digits.charAt(i)-'0';
            String letters = letterPad[digit];
            ListIterator<String> ite = result.listIterator();
            while (ite.hasNext()) {
                String old = ite.next();
                ite.remove();
                for (int j = 0; j < letters.length(); j++) {
                    ite.add(old + letters.charAt(j));
                }
            }
        }
        return result;
    }
}
```

#### 也可以这样写
如果不用`ListIterator`，也可以用像下面这样`while(ans.peek().length()==i)`，只对特定元素做处理。这样做很`sexy`，但工作中不值得推荐。
```java
public List<String> letterCombinations(String digits) {
    LinkedList<String> ans = new LinkedList<String>();
    String[] mapping = new String[] {"0", "1", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
    ans.add("");
    for(int i =0; i<digits.length();i++){ // 这行非常帅
        int x = Character.getNumericValue(digits.charAt(i));
        while(ans.peek().length()==i){
            String t = ans.remove();
            for(char s : mapping[x].toCharArray())
                ans.add(t+s);
        }
    }
    return ans;
}
```

#### 结果
![phone-number-3](/images/leetcode/phone-number-3.png)
