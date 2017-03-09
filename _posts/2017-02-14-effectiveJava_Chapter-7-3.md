---
layout: post
title: "[Effective Java] Note: - Chapter-7-3: Add Documentation Comment for each API"
date: 2017-02-14
author: "Wei SHEN"
categories: ["java","effective java"]
tags: ["code style","api"]
description: >
  使用JavaDoc。为每个公有导出API: 类，接口，构造器，方法，域。都编写文档。
---

### 使用JavaDoc

### API
API包括所有`public`或者`protected`访问权限的：
1. 类
2. 接口
3. 构造器
4. 方法
5. 域

### 良好的文档应该包括
1. 做了什么
2. 前置条件：throws未受检异常，对应的是前置条件违例。
3. 后置条件：方法完成后，哪些条件必须满足。
4. 副作用：比如，在后台开了什么新线程。
5. 线程安全
6. `@param`
7. `@return`
8. `@throws`

另外注意，`{@code ...}`标签，表示其中内容讲义代码字体显示，而且避免受到`HTML`转意元字符的影响。`{@literal ...}`标签只提供屏蔽`HTML`转意字符干扰，不以代码字体显示。
