---
layout: post
title: "Adapter Pattern"
date: 2016-08-21 23:06:30
author: "Wei SHEN"
categories: ["Java","Design_Pattern"]
tags: ["Generics","Array","Adapter_Pattern"]
description: >
---

这要用Java泛型来实现确实比较坑爹。Java的泛型又不像C++或者Python支持“潜在类型(Latent Type)"。Java必须显式地指出接口。而且Number类也没有定义一个类似的add( )方法。

但硬要做的话，还是可以用“适配器”模式曲线救国一下。
```java
//接口
interface Addable<T extends Number> {public Double reduce();}

//适配器类
class AddableArrayAdapter<T extends Number> implements Addable<T>{
	//数组成员字段
	private T[] t;
	//构造器接受一个数组为参数
	public AddableArrayAdapter(T[] t){this.t=t;}
	//实现接口定义的reduce方法
	public Double reduce(){
		Double sum=new Double(0.0);
		for(int i=0;i<t.length;i++){
			sum+=t[i].doubleValue();
		}
		return sum;
	}
}
```

先定义一个边界为Number的泛化Addable接口。实现Addable接口必须实现一个返回值是Double型的reduce( )方法。

然后写一个实现Addable接口的适配器，这里叫AddableArrayAdapter。适配器以需要求和的数组T[] t为主要成员字段。reduce()方法负责为数组求和。具体就像 @李伟 说的，遍历数组元素，全转换成Double型再求和。最后返回一个Double型的和。选Double型是因为无论是取值范围还是精度，Double对其他Number的普适性比较好。

从用户的角度看，只要把需要求和的数组作为参数，传递给适配器的构造函数，包装出一个带有reduce()求和方法的对象。直接调用reduce()方法就可以求和。下面是简单的测试代码：
```java
/**
*  AtomicInteger, AtomicLong, BigDecimal, BigInteger,
*  Byte, Double, Float, Integer, Long, Short
*/
public class Test{
public static void main(String[] args){
//不同类型数组
Integer[] ia= new Integer[]{1,2,3,4,5,6,7,8,9,10};
Short[] sa= new Short[]{1,2,3,4,5,6,7,8,9,10};
Long[] la=new Long[]{1l,2l,3l,4l,5l,6l,7l,8l,9l,10l};
Float[] fa=new Float[]{1f,2f,3f,4f,5f,6f,7f,8f,9f,10f};
Double[] da=new Double[]{1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0};

//用适配器包装数组
AddableArrayAdapter<Integer> aai=new AddableArrayAdapter<Integer>(ia);
AddableArrayAdapter<Short> aas=new AddableArrayAdapter<Short>(sa);
AddableArrayAdapter<Long> aal=new AddableArrayAdapter<Long>(la);
AddableArrayAdapter<Float> aaf=new AddableArrayAdapter<Float>(fa);
AddableArrayAdapter<Double> aad=new AddableArrayAdapter<Double>(da);

//调用reduce()方法求和
Double sumI=aai.reduce();
Double sumS=aas.reduce();
Double sumL=aal.reduce();
Double sumF=aaf.reduce();
Double sumD=aad.reduce();

//输出
System.out.println(sumI);
System.out.println(sumS);
System.out.println(sumL);
System.out.println(sumF);
System.out.println(sumD);
}
}
```

Java泛型确实比较坑坑洼洼，只能从应用的层面弥补了。以上只是我的一个思路，欢迎纠正补充。
