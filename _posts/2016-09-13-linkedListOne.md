---
layout: post
title: "About the LinkedList"
date: 2016-09-13 14:24:21
author: "Wei SHEN"
categories: ["java","data structure"]
tags: ["container","linkedlist"]
description: >
  实现链表的时候，在前面加一个“桩节点”，可以简化逻辑结构。
---

### 头节点
实现链表的时候，一个很实用的实践是在链表前面加一个不储存数据的“头节点”。
![linkedlist](/images/tij4-17/linkedlist.png)

如上图，头节点不储存数据。从头节点的后一个元素开始才是链表的第一个元素。当链表为空的时候，链表只有一个头节点，而且next指针为空。

头节点作为经典设计，好处很明显：
1. 首先当链表为空的时候，还有地方存放首，尾，前，中，后等指针。
2. 如果没有头节点，对链表第一个元素和后续元素的操作是不一致的。但加个头节点，首元素和其他元素的插入，删除等操作步骤相同，统一化。减少控制流的逻辑分叉，提高代码质量，减少bug。

### 实践
3种不同的方法实现**“单向链表”**：
**版本1. 只有头节点：head。**
![linkedlist](/images/tij4-17/linkedlist.png)

**版本2. 有一个头节点head，一个尾节点tail。**
![linkedlist2](/images/tij4-17/linkedlist2.png)

**版本3. 没有头节点，没有尾节点。只有普通节点。**
![linkedlist3](/images/tij4-17/linkedlist3.png)


### 关于LinkedList头节点的总结：
1. 头节点是不储存元素信息的纯功能性节点。用来停放指针。
2. 单纯从功能上讲，单个头节点head就够了。不需要尾节点。
3. 没有head头节点的话，大部分操作对第一个元素和后续元素，实现都不同。导致逻辑复杂。容易出bug。编程目标是不出错，不是为了挑战脑力。
4. 不需要专门的桩子尾节点。但需要一个tail指针，指向末尾元素，方便从末尾插入新元素。
5. 双头节点版本：一个头节点head，一个尾节点tail。也没什么不好。反而逻辑清楚。
6. JDK里的官方LinkedList是没有头节点的版本。

### 以双头节点链表举例
1. 节点的数据结构很清楚：一部分放数据，一部分是指向下一个节点的指针。
2. 个人比较喜欢：前后两个固定的头节点head和尾节点tail。只站桩，无数据。
3. 其中，只有头节点是必须的。无头节点的话，对第一个元素的操作会和其他元素不同，代码复杂化。
4. 尾节点可以没有。可以让最后一个元素指向null。加尾节点是为了在末尾插入新元素更高效。不然就得从head遍历到最后一个元素，再插入。
3. head.next指向第一个节点，tail.next指向最后一个节点。当链表为空，head.next=tail。 tail.next=head。
4. cursor光标表示上一个next()方法返回的节点。
5. previous引用盯住cursor。表示cursor的前一个节点。为了remove()的时候能找到前一个节点的向后引用。
6. 每一次调用next()，只能用remove()方法删除一次当前节点。删除完，光标退回上一节点。
7. 只有两种情况，previous和cursor指向同一个节点：要么初始阶段还没有调用过next()。要么next()返回的上一个节点被remove()方法删掉。
8. previous和cursor永远不可能指向tail。


### JDK1.8 中LinkedList的实现
JDK里的官方LinkedList是没有头节点的版本。但实现写得不繁琐。

参见[**《JDK1.8源码分析之LinkedList》**](http://hovertree.com/h/bjaf/l9bqickc.htm)这篇文章。

### 代码
根据《Think in Java》书中练习的要求，所有增删修改的方法都没有在链表本体里，而是放在Iterator的下面。

##### 单头节点版本
1. 单向链表
2. 单头节点。只有前头节点head，无尾节点tail。
3. tail只是一个指向最后一个元素的指针。
4. 前中后2个光标：previous,cursor。cursor指向上一个next()返回的元素。previous指向cursor的前一个元素。

```java
package com.ciaoshen.thinkinjava.chapter17;
import java.util.*;

@SuppressWarnings(value={"unchecked", "rawtypes"})
public class SListV2<T>{

    private Node<T> head;
    private Node<T> tail;
    private int size=0;
    public SListV2(){
        head=new Node();
        tail=head;
    }

    private static class Node<T>{
        private T item;
        private Node<T> next;
        public Node(){item=null;next=null;}
        public Node(T t){item=t;next=null;}
        public String toString(){return item.toString();}
    }

    private class SListIterator{
        private Node<T> cursor=head;
        private Node<T> previous=cursor;
        private int index=0;
        //在cursor后面插入新元素
        public void add(T t){
            Node<T> n=new Node<T>(t);
            n.next=tail.next;
            tail.next=n;
            tail=n;
            size++;
        }
        //替换cursor位置元素
        public void set(T t){
            if(cursor==head){
                System.out.println("Not Begun!");
            }else{
                Node<T> n=new Node<T>(t);
                cursor.item=t;
            }
        }
        //从cursor位置开始往后删除
        public void remove(){
            if(cursor==previous){
                System.out.println("Can't remove!");
            }else{
                if(cursor==tail){
                    tail=previous;
                }
                previous.next=cursor.next;
                cursor=previous;
                index--;
                size--;
            }
        }
        //current还没到tail
        public boolean hasNext(){
            return cursor.next!=null;
        }
        //返回cursor的下一个元素
        public Node<T> next(){
            if(hasNext()){
                previous=cursor;
                cursor=cursor.next;
                index++;
                return cursor;
            }else{
                System.out.println("Reach the end!");
                return null;
            }
        }
        public int index(){
            return index;
        }
        public int size(){
            return size;
        }
        public void reset(){
            cursor=head;
            previous=cursor;
            index=0;
        }
    }

    public SListIterator iterator(){
        return new SListIterator();
    }
    public String toString(){
        StringBuilder sb=new StringBuilder();
        SListIterator ite=iterator();
        while(ite.hasNext()){
            sb.append("["+ite.next()+"]");
            if(ite.hasNext()){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     *  测试
     */
    public static void main(String[] args){
        String[] s="A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
        SListV2<String> list=new SListV2<String>();
        SListV2.SListIterator ite=list.iterator();
        for(int i=0;i<s.length;i++){
            ite.add(s[i]);
        }
        System.out.println(list);
        for(int i=0;i<ite.size();i++){
            System.out.println(ite.next());
        }
        ite.reset();
        int length=ite.size();
        for(int i=0;i<10;i++){
            Node<String> n=ite.next();
            ite.remove();
            ite.remove();
        }
        System.out.println(list);
    }
}
```

##### 双头节点版本
1. 单向链表
2. 双头节点。前头节点head，后尾节点tail。两个节点永远不存放元素。尾节点的next指向最后一个元素。空链表时指向head。
3. 两个光标：previous,cursor。主要是cursor。previous只负责盯住cursor，并在remove的时候提供前一节点的next指针。
```java
package com.ciaoshen.thinkinjava.chapter17;
import java.util.*;

@SuppressWarnings(value={"unchecked", "rawtypes"})
public class SListV3<T>{

    private Node<T> head;
    private Node<T> tail;
    private int size=0;
    public SListV3(){
        head=new Node();
        tail=new Node();
        head.next=tail;
        tail.next=head;
    }

    private static class Node<T>{
        private T item;
        private Node<T> next;
        public Node(){item=null;next=null;}
        public Node(T t){item=t;next=null;}
        public String toString(){return item.toString();}
    }

    private class SListIterator{
        private Node<T> cursor=head;
        private Node<T> previous=cursor;
        private int index=0;
        //在tail栓塞前插入新元素
        public void add(T t){
            Node<T> n=new Node<T>(t);
            tail.next.next=n;
            n.next=tail;
            tail.next=n;
            size++;
        }
        //替换当前cursor元素（若当前元素被删，也不能set。）
        public void set(T t){
            if(cursor==previous){
                System.out.println("Cannot set!");
            }else{
                cursor.item=t;
            }
        }
        //只删除上一次next()返回的元素。每次next()只能删除一次。
        public void remove(){
            if(cursor==previous){
                System.out.println("Can't remove!");
            }else{
                if(tail.next==cursor){
                    tail.next=previous;
                }
                previous.next=cursor.next;
                cursor=previous;
                index--;
                size--;
            }
        }
        //current还没到tail
        public boolean hasNext(){
            return cursor.next!=tail;
        }
        //返回cursor的下一个元素
        public Node<T> next(){
            if(hasNext()){
                previous=cursor;
                cursor=cursor.next;
                index++;
                return cursor;
            }else{
                System.out.println("Reach the end!");
                return null;
            }
        }
        public int index(){
            return index;
        }
        public int size(){
            return size;
        }
        public void reset(){
            cursor=head;
            previous=cursor;
            index=0;
        }
    }

    public SListIterator iterator(){
        return new SListIterator();
    }
    public String toString(){
        StringBuilder sb=new StringBuilder();
        SListIterator ite=iterator();
        while(ite.hasNext()){
            sb.append("["+ite.next()+"]");
            if(ite.hasNext()){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     *  测试
     */
    public static void main(String[] args){
        String[] s="A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
        SListV3<String> list=new SListV3<String>();
        SListV3.SListIterator ite=list.iterator();
        for(int i=0;i<s.length;i++){
            ite.add(s[i]);
        }
        System.out.println(list);
        for(int i=0;i<ite.size();i++){
            System.out.println(ite.next());
        }
        ite.reset();
        int length=ite.size();
        for(int i=0;i<10;i++){
            Node<String> n=ite.next();
            ite.remove();
            ite.remove();
        }
        System.out.println(list);
    }
}
```

##### 无头节点版本
1. 单向链表
2. 没有头节点
3. head和tail只是两个指针，指向第一个和最后一个元素。
4. 当链表为空：维持一个空节点。head和tail都指向这个空节点。只有一个元素的时候，head和tail也都指向唯一的这个节点。
5. 两个光标：previous,cursor。cursor指向上一个next()返回的元素。previous指向cursor的前一个元素。

```java
package com.ciaoshen.thinkinjava.chapter17;
import java.util.*;

@SuppressWarnings(value={"unchecked", "rawtypes"})
public class SListV4<T>{

    private Node<T> head;
    private Node<T> tail;
    private int size=0;
    public SListV4(){
        head=new Node();
        tail=head;
    }

    private static class Node<T>{
        private T item;
        private Node<T> next;
        public Node(){item=null;next=null;}
        public Node(T t){item=t;next=null;}
        public Node(T t, Node<T> n){item=t;next=n;}
        public String toString(){return item.toString();}
    }

    private class SListIterator{
        private Node<T> cursor=null;
        private Node<T> previous=null;
        private int index=-1;
        //在tail栓塞前插入新元素
        public void add(T t){
            //空链表
            if(head.item==null){
                head.item=t;
            }else{
                Node<T> n=new Node<T>(t);
                tail.next=n;
                tail=n;
            }
            size++;
        }
        //替换当前cursor元素（若当前元素被删，也不能set。）
        public void set(T t){
            if(cursor==previous){
                System.out.println("Cannot set!");
            }else{
                cursor.item=t;
            }
        }
        //只删除上一次next()返回的元素。每次next()只能删除一次。
        public void remove(){
            if(cursor==previous){
                System.out.println("Can't remove!");
            }else{
                if(cursor==head){
                    cursor.item=null;
                    cursor=null;
                } else {
                    previous.next=cursor.next;
                    cursor=previous;
                }
                index--;
                size--;
            }
        }
        //current还没到tail
        public boolean hasNext(){
            if(cursor==null){
                return (head.item==null && head.next==null)? false:true;
            }else{
                return (cursor.next==null)? false:true;
            }
        }
        //返回cursor的下一个元素
        public Node<T> next(){
            if(hasNext()){
                if(cursor==null && head.item==null){
                    cursor=head.next;
                    previous=head;
                }else if(cursor==null && head.item!=null){
                    cursor=head;
                }else{
                    previous=cursor;
                    cursor=cursor.next;
                }
                index++;
                return cursor;
            }else{
                System.out.println("Reach the end!");
                return null;
            }
        }
        public int index(){
            return index;
        }
        public int size(){
            return size;
        }
        public void reset(){
            cursor=previous=null;
            index=0;
        }
    }

    public SListIterator iterator(){
        return new SListIterator();
    }
    public String toString(){
        StringBuilder sb=new StringBuilder();
        SListIterator ite=iterator();
        while(ite.hasNext()){
            sb.append("["+ite.next()+"]");
            if(ite.hasNext()){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     *  测试
     */
    public static void main(String[] args){
        String[] s="A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");
        SListV4<String> list=new SListV4<String>();
        SListV4.SListIterator ite=list.iterator();
        for(int i=0;i<s.length;i++){
            ite.add(s[i]);
        }
        System.out.println(list);
        for(int i=0;i<ite.size();i++){
            System.out.println(ite.next());
        }
        ite.reset();
        for(int i=0;i<10;i++){
            Node<String> n=ite.next();
            ite.set("XXX");
            System.out.println(list);
            ite.remove();
        }
        System.out.println(list);
    }
}
```
