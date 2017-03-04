---
layout: post
title: "Fly Weight Pattern"
date: 2017-02-03
author: "Wei SHEN"
categories: ["Java","Design_Pattern"]
tags: ["Fly_Weight"]
description: >
  享元模式对于学习容器很有帮助。因为它真正把数据和容器分离开来。这个享元框架，可以将任意外部传入的字符串二维数组(`String[][]`)转化成一个`Map`视图。
---

### 享元模式

享元模式对于学习容器很有帮助。因为它真正把数据和容器分离开来。这个享元框架，可以将任意外部传入的字符串二维数组(`String[][]`)转化成一个`Map`视图。

最重要的方法就是`entrySet()`，看它是怎么组织并返回一个`Set<Map.Entry<K,V>>`的。

```java
/**
 * Fly Weight Scafflod
 */
package com.ciaoshen.thinkinjava.newchapter17;
import java.util.*;

// 可以接受任意外部String二维数组作为DATA的数据源。
public class FlyWeightMapScaffold extends AbstractMap<String,String> {
    // 所谓享元，就是根据内部缓存的数组，自动向用户展示一个Map视图的工具。能很直观地看到，Map到底是个什么东西。
    // 这里所谓享元脚手架，就是内部数据虚位以待。等待外部数据的传入。可以将任意外部String[][]转化成Map视图。
    public final String[][] DATA;
    private int size;
    public FlyWeightMapScaffold(String[][] data, int num) {
        if (data.length == 0 || num <= 0) {
            throw new RuntimeException();
        }
        DATA = data;
        size = num;
        if (size > DATA.length) {
            size = DATA.length;
        }
    }
    public FlyWeightMapScaffold(String[][] data) {
        this(data,data.length);
    }
    public int size() {
        return size;
    }
    // 用来容纳<K,V>对的数据结构
    public class Pair implements Map.Entry<String,String> {
        private int index=0;
        public Pair(int num) {
            index = num;
        }
        public String getKey() {
            return DATA[index][0];
        }
        public String getValue() {
            return DATA[index][1];
        }
        public String setValue(String str) {
            throw new UnsupportedOperationException();
        }
        public boolean equals(Object o) {
            if (o == null || ! (o instanceof Pair)) {
                return false;
            }
            return DATA[index][0].equals((Pair)o);
        }
        public int hashCode() {
            return DATA[index][0].hashCode();
        }
    }
    /**
     * 享元最关键的部件。这个DataSet并不储存数据实体。只包含一个指针和一个返回数据引用的Map.Entry视窗。
     */
    public class DataSet extends AbstractSet<Map.Entry<String,String>> {
        private int index = 0; // 唯一指针
        private Pair viewWindow = new Pair(-1); //唯一view的窗口
        public Iterator<Map.Entry<String,String>> iterator() {
            return new Iterator<Map.Entry<String,String>>() {
                public boolean hasNext() {
                    return index < size;
                }
                public Map.Entry<String,String> next() {
                    viewWindow.index++;
                    index++;
                    return viewWindow;
                }
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        public int size() {
            return size;
        }
    }
    public Set<Map.Entry<String,String>> entrySet() {
        return new DataSet();
    }

    public static void main(String[] args) {
        String path = "/Users/Wei/java/com/ciaoshen/thinkinjava/newchapter17/FlyWeightMapScaffold.java";
        // 这里使用的外部数据，由我工具箱里的MyReader.calculWordFreq()方法产生。
        String[][] data = MyReader.calculWordFreq(MyReader.readFile(path));
        FlyWeightMapScaffold dataMap = new FlyWeightMapScaffold(data);
        Formatter f = new Formatter(System.out);
        // 打印整个词频表
        for(Map.Entry<String,String> entry : dataMap.entrySet()) {
            f.format("Word: %1$-20.20s Frequence: %2$-20.20s \n", entry.getKey(), entry.getValue());
        }
        // 只打印单词列表
        for (String word : dataMap.keySet()) {
            f.format("Word: %1$-20.20s \n", word);
        }
    }
}
```
