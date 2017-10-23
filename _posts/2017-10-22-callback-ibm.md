---
layout: post
title: "About Callback (From IBM Developer Works)"
date: 2017-10-22 23:35:42
author: "Wei SHEN"
categories: ["java"]
tags: ["callback"]
description: >
---

From: <https://www.ibm.com/developerworks/cn/linux/l-callback/>

### 1 什么是回调

软件模块之间总是存在着一定的接口，从调用方式上，可以把他们分为三类：同步调用、回调和异步调用。同步调用是一种阻塞式调用，调用方要等待对方执行完毕才返回，它是一种单向调用；回调是一种双向调用模式，也就是说，被调用方在接口被调用时也会调用对方的接口；异步调用是一种类似消息或事件的机制，不过它的调用方向刚好相反，接口的服务在收到某种讯息或发生某种事件时，会主动通知客户方（即调用客户方的接口）。回调和异步调用的关系非常紧密，通常我们使用回调来实现异步消息的注册，通过异步调用来实现消息的通知。同步调用是三者当中最简单的，而回调又常常是异步调用的基础，因此，下面我们着重讨论回调机制在不同软件架构中的实现。

![image001](/images/callback-ibm/image001.gif)

对于不同类型的语言（如结构化语言和对象语言）、平台（Win32、JDK）或构架（CORBA、DCOM、WebService），客户和服务的交互除了同步方式以外，都需要具备一定的异步通知机制，让服务方（或接口提供方）在某些情况下能够主动通知客户，而回调是实现异步的一个最简捷的途径。

对于一般的结构化语言，可以通过回调函数来实现回调。回调函数也是一个函数或过程，不过它是一个由调用方自己实现，供被调用方使用的特殊函数。

在面向对象的语言中，回调则是通过接口或抽象类来实现的，我们把实现这种接口的类成为回调类，回调类的对象成为回调对象。对于象C++或Object Pascal这些兼容了过程特性的对象语言，不仅提供了回调对象、回调方法等特性，也能兼容过程语言的回调函数机制。

Windows平台的消息机制也可以看作是回调的一种应用，我们通过系统提供的接口注册消息处理函数（即回调函数），从而实现接收、处理消息的目的。由于Windows平台的API是用C语言来构建的，我们可以认为它也是回调函数的一个特例。

对于分布式组件代理体系CORBA，异步处理有多种方式，如回调、事件服务、通知服务等。事件服务和通知服务是CORBA用来处理异步消息的标准服务，他们主要负责消息的处理、派发、维护等工作。对一些简单的异步处理过程，我们可以通过回调机制来实现。

下面我们集中比较具有代表性的语言（C、Object Pascal）和架构（CORBA）来分析回调的实现方式、具体作用等。

### 2 过程语言中的回调（C）

#### 2.1 函数指针

回调在C语言中是通过函数指针来实现的,通过将回调函数的地址传给被调函数从而实现回调。因此，要实现回调，必须首先定义函数指针，请看下面的例子：

    void Func(char *s)；// 函数原型
    void (*pFunc) (char *);//函数指针

可以看出，函数的定义和函数指针的定义非常类似。

一般的化，为了简化函数指针类型的变量定义，提高程序的可读性，我们需要把函数指针类型自定义一下。

    typedef void(*pcb)(char *);

回调函数可以象普通函数一样被程序调用，但是只有它被当作参数传递给被调函数时才能称作回调函数。

被调函数的例子：

    void GetCallBack(pcb callback)
    {
    /*do something*/
    }
    用户在调用上面的函数时，需要自己实现一个pcb类型的回调函数：
    void fCallback(char *s)
    {
    /* do something */
    }
    然后，就可以直接把fCallback当作一个变量传递给GetCallBack,
    GetCallBack（fCallback）;

如果赋了不同的值给该参数，那么调用者将调用不同地址的函数。赋值可以发生在运行时，这样使你能实现动态绑定。

#### 2.2 参数传递规则

到目前为止，我们只讨论了函数指针及回调而没有去注意ANSI C/C++的编译器规范。许多编译器有几种调用规范。如在Visual C++中，可以在函数类型前加_cdecl，_stdcall或者_pascal来表示其调用规范（默认为_cdecl）。C++ Builder也支持_fastcall调用规范。调用规范影响编译器产生的给定函数名，参数传递的顺序（从右到左或从左到右），堆栈清理责任（调用者或者被调用者）以及参数传递机制（堆栈，CPU寄存器等）。

将调用规范看成是函数类型的一部分是很重要的；不能用不兼容的调用规范将地址赋值给函数指针。例如：

    // 被调用函数是以int为参数，以int为返回值
    __stdcall int callee(int);
    // 调用函数以函数指针为参数
    void caller( __cdecl int(*ptr)(int));
    // 在p中企图存储被调用函数地址的非法操作
    __cdecl int(*p)(int) = callee; // 出错

指针p和callee()的类型不兼容，因为它们有不同的调用规范。因此不能将被调用者的地址赋值给指针p，尽管两者有相同的返回值和参数列

#### 2.3 应用举例

C语言的标准库函数中很多地方就采用了回调函数来让用户定制处理过程。如常用的快速排序函数、二分搜索函数等。

快速排序函数原型：

    void qsort(void *base, size_t nelem, size_t width,
      int (_USERENTRY *fcmp)(const void *, const void *));
    二分搜索函数原型：
    void *bsearch(const void *key, const void *base, size_t nelem,
            size_t width, int (_USERENTRY *fcmp)(const void *, const void *));

其中fcmp就是一个回调函数的变量。

下面给出一个具体的例子：

    #include <stdio.h>
    #include <stdlib.h>
    int sort_function( const void *a, const void *b);
    int list[5] = { 54, 21, 11, 67, 22 };
    int main(void)
    {
       int  x;
       qsort((void *)list, 5, sizeof(list[0]), sort_function);
       for (x = 0; x < 5; x++)
          printf("%i\n", list[x]);
       return 0;
    }
    int sort_function( const void *a, const void *b)
    {
       return *(int*)a-*(int*)b;
    }

#### 2.4 面向对象语言中的回调（Delphi）

Dephi与C++一样，为了保持与过程语言Pascal的兼容性，它在引入面向对象机制的同时，保留了以前的结构化特性。因此，对回调的实现，也有两种截然不同的模式，一种是结构化的函数回调模式，一种是面向对象的接口模式。

**2.4.1 回调函数**

回调函数类型定义：

    type
       TCalcFunc=function (a:integer;b:integer):integer;

按照回调函数的格式自定义函数的实现，如

    function Add(a:integer;b:integer):integer
    begin
      result:=a+b;
    end;
    function Sub(a:integer;b:integer):integer
    begin
      result:=a-b;
    end;

回调的使用

    function Calc(calc:TcalcFunc;a:integer;b:integer):integer

下面，我们就可以在我们的程序里按照需要调用这两个函数了

    c:=calc(add,a,b);//c=a+b
    c:=calc(sub,a,b);//c=a-b

**2.4.2 回调对象**

什么叫回调对象呢，它具体用在哪些场合？首先，让我们把它与回调函数对比一下，回调函数是一个定义了函数的原型，函数体则交由第三方来实现的一种动态应用模式。要实现一个回调函数，我们必须明确知道几点：该函数需要那些参数，返回什么类型的值。同样，一个回调对象也是一个定义了对象接口，但是没有具体实现的抽象类（即接口）。要实现一个回调对象，我们必须知道：它需要实现哪些方法，每个方法中有哪些参数，该方法需要放回什么值。

因此，在回调对象这种应用模式中，我们会用到接口。接口可以理解成一个定义好了但是没有实现的类，它只能通过继承的方式被别的类实现。Delphi中的接口和COM接口类似，所有的接口都继承与IInterface（等同于IUnknow），并且要实现三个基本的方法QueryInterface, _AddRef, 和_Release。

  * 定义一个接口  


    type IShape=interface(IInterface)
            procedure Draw;
    end

  * 实现回调类  


    type TRect=class(TObject,IShape)
            protected
          function QueryInterface(const IID: TGUID; out Obj): HResult; stdcall;
          function _AddRef: Integer; stdcall;
    function _Release: Integer; stdcall;
        public
              procedure Draw;
    end;
    type TRound=class(TObject,IShape)
            protected
          function QueryInterface(const IID: TGUID; out Obj): HResult; stdcall;
          function _AddRef: Integer; stdcall;
    function _Release: Integer; stdcall;
        public
              procedure Draw;
    end;

  * 使用回调对象  


    procedure MyDraw(shape:IShape);
    var
    shape:IShape;
    begin
    shape.Draw;
    end;

如果传入的对象为TRect，那么画矩形；如果为TRound，那么就为圆形。用户也可以按照自己的意图来实现IShape接口，画出自己的图形：

    MyDraw(Trect.Create);
    MyDraw(Tround.Create);

**2.4.3 回调方法**

回调方法(Callback Method)可以看作是回调对象的一部分，Delphi对windows消息的封装就采用了回调方法这个概念。在有些场合，我们不需要按照给定的要求实现整个对象，而只要实现其中的一个方法就可以了，这是我们就会用到回调方法。

回调方法的定义如下：

    TNotifyEvent = procedure(Sender: TObject) of object;
    TMyEvent=procedure(Sender:Tobject;EventId:Integer) of object;

TNotifyEvent 是Delphi中最常用的回调方法，窗体、控件的很多事件，如单击事件、关闭事件等都是采用了TnotifyEvent。回调方法的变量一般通过事件属性的方式来定义，如TCustomForm的创建事件的定义：

    property OnCreate: TNotifyEvent read FOnCreate write FOnCreate stored IsForm;

我们通过给事件属性变量赋值就可以定制事件处理器。

用户定义对象（包含回调方法的对象）：

    type TCallback=Class
        procedure ClickFunc(sender:TObject);
    end;
    procedure Tcallback.ClickFunc(sender:TObject);
    begin
      showmessage('the caller is clicked!');
    end;

窗体对象：

    type TCustomFrm=class(TForm)
      public
            procedure RegisterClickFunc(cb:procedure(sender:Tobject) of object);
    end;
    procedure TcustomFrm..RegisterClickFunc(cb:TNotifyEvent);
    begin
      self.OnClick=cb;
    end;

使用方法：

    var
      frm:TcustomFrm;
    begin
      frm:=TcustomFrm.Create(Application);
      frm.RegisterClickFunc(Tcallback.Create().ClickFunc);
    end;

### 3 回调在分布式计算中的应用（CORBA）

#### 3.1 回调接口模型

CORBA的消息传递机制有很多种，比如回调接口、事件服务和通知服务等。回调接口的原理很简单，CORBA客户和服务器都具有双重角色，即充当服务器也是客户客户。

回调接口的反向调用与正向调用往往是同时进行的，如果服务端多次调用该回调接口，那么这个回调接口就变成异步接口了。因此，回调接口在CORBA中常常充当事件注册的用途，客户端调用该注册函数时，客户函数就是回调函数，在此后的调用中，由于不需要客户端的主动参与，该函数就是实现了一种异步机制。

从CORBA规范我们知道，一个CORBA接口在服务端和客户端有不同的表现形式，在客户端一般使用桩（Stub）文件，服务端则用到框架（Skeleton）文件，接口的规格采用IDL来定义。而回调函数的引入，使得服务端和客户端都需要实现一定的桩和框架。下面是回调接口的实现模型：

##### 3.1.1 范例

![image002](/images/callback-ibm/image002.gif)

下面给出了一个使用回调的接口文件，服务端需要实现Server接口的框架，客户端需要实现CallBack的框架：

    module cb
    {
            interface CallBack;
            interface Server;
    interface CallBack
    {
            void OnEvent(in long Source,in long msg);
    };
            interface Server
    {
            long RegisterCB(in CallBack cb);
                    void UnRegisterCB(in long hCb);
    };
    };

客户端首先通过同步方式调用服务端的接口RegistCB，用来注册回调接口CallBack。服务端收到该请求以后，就会保留该接口引用，如果发生某种事件需要向客户端通知的时候就通过该引用调用客户方的OnEvent函数，以便对方及时处理。

本文源码 [下载][6]。

   [6]: samplecode.rar

* * *
