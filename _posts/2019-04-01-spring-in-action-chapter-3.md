---
layout: post
title: "Spring in Action - Chapter 3"
date: 2019-04-01 21:43:01
author: "Wei SHEN"
categories: ["spring"]
tags: ["jdbc"]
description: >
---

### 本章项目最终效果
对本章的项目有些许改动，主页还是`localhost:8080/`，
![res1](/images/spring-in-action-demo-ch03/res1.png)

点击`Order your specific Taco!`后，进入选肉卷配料页面`localhost:8080/design`，
![res2](/images/spring-in-action-demo-ch03/res2.png)

选完第一个肉卷配料，点击提交，进入`localhost:8080/order/new`付款页面。不着急付款，点击`Design Another Taco`按钮，再次进入`localhost:8080/design`，继续制作第二个肉卷，
![res3](/images/spring-in-action-demo-ch03/res3.png)

选完配料，再次提交表单，再次进入`localhost:8080/order/new`页面。这次开始付款，
![res4](/images/spring-in-action-demo-ch03/res4.png)

填完付款信息，提交表单，
![res5](/images/spring-in-action-demo-ch03/res5.png)

最后为了直观地看到我们往数据库里写入的数据，`localhost:8080/finish`页面负责显示出这一单所有的肉卷配料信息，以及送货地址信息，
![res6](/images/spring-in-action-demo-ch03/res6.png)

### 需要制作4对`controller-view`
1. `HomeController.java`和`home.html`: 对应主页`localhost:8080/`
2. `DesignTacoController.java`和`design.html`: 对应`localhost:8080/design`
3. `OrderController.java`和`orderForm.html`: 对应`localhost:8080/orders/new`
4. `FinishOrderController.java`和`finish.html`: 对应`localhost:8080/finish`

### 需要储存的数据主体有3类
每一类数据分别用一个`repository`专门负责，
1. `IngredientRepository.java`以及`JdbcIngredientRepository.java`餐厅提供的肉卷配料表
2. `TacoRepository.java`以及`JdbcTacoRepository.java`: 单个肉卷的配料信息，以及肉卷名称信息
2. `OrderRepository.java`以及`JdbcOrderRepository.java`: 整个订单的信息（包括付款信息，以及配送地址等信息）

`Ingredient`,`Taco`,`Order`三者涉及的关系型数据库表结构如下，
![relations](/images/spring-in-action-demo-ch03/relations.png)

### 项目编译的后的文件结构如下
```
.
├── HELP.md
├── bin
│   ├── main
│   │   ├── application.properties
│   │   ├── com
│   │   │   └── ciaoshen
│   │   │       └── sia_ch03_taco
│   │   │           ├── Ingredient$Type.class
│   │   │           ├── Ingredient.class
│   │   │           ├── Order.class
│   │   │           ├── ServletInitializer.class
│   │   │           ├── SiaCh03TacoApplication.class
│   │   │           ├── Taco.class
│   │   │           ├── data
│   │   │           │   ├── IngredientRepository.class
│   │   │           │   ├── JdbcIngredientRepository.class
│   │   │           │   ├── JdbcOrderRepository.class
│   │   │           │   ├── JdbcTacoRepository.class
│   │   │           │   ├── OrderRepository.class
│   │   │           │   └── TacoRepository.class
│   │   │           └── web
│   │   │               ├── DesignTacoController.class
│   │   │               ├── FinishOrderController.class
│   │   │               ├── HomeController.class
│   │   │               ├── OrderController.class
│   │   │               └── WebConfig.class
│   │   ├── data.sql
│   │   ├── schema.sql
│   │   ├── static
│   │   │   └── images
│   │   │       └── TacoCloud.png
│   │   └── templates
│   │       ├── design.html
│   │       ├── finish.html
│   │       ├── home.html
│   │       └── orderForm.html
│   └── test
├── build
│   ├── classes
│   │   └── java
│   │       └── main
│   │           └── com
│   │               └── ciaoshen
│   │                   └── sia_ch03_taco
│   │                       ├── Ingredient$Type.class
│   │                       ├── Ingredient.class
│   │                       ├── Order.class
│   │                       ├── ServletInitializer.class
│   │                       ├── SiaCh03TacoApplication.class
│   │                       ├── Taco.class
│   │                       ├── data
│   │                       │   ├── IngredientRepository.class
│   │                       │   ├── JdbcIngredientRepository.class
│   │                       │   ├── JdbcOrderRepository.class
│   │                       │   ├── JdbcTacoRepository.class
│   │                       │   ├── OrderRepository.class
│   │                       │   └── TacoRepository.class
│   │                       └── web
│   │                           ├── DesignTacoController.class
│   │                           ├── FinishOrderController.class
│   │                           ├── HomeController.class
│   │                           ├── OrderController.class
│   │                           └── WebConfig.class
│   ├── libs
│   │   └── sia-ch03-taco-0.0.1-SNAPSHOT.war
│   ├── reports
│   │   └── tests
│   ├── resources
│   │   └── main
│   │       ├── application.properties
│   │       ├── data.sql
│   │       ├── schema.sql
│   │       ├── static
│   │       │   └── images
│   │       │       └── TacoCloud.png
│   │       └── templates
│   │           ├── design.html
│   │           ├── finish.html
│   │           ├── home.html
│   │           └── orderForm.html
│   ├── test-results
│   └── tmp
│       ├── bootWar
│       │   └── MANIFEST.MF
│       ├── compileJava
│       └── compileTestJava
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── ciaoshen
    │   │           └── sia_ch03_taco
    │   │               ├── Ingredient.java
    │   │               ├── Order.java
    │   │               ├── ServletInitializer.java
    │   │               ├── SiaCh03TacoApplication.java
    │   │               ├── Taco.java
    │   │               ├── data
    │   │               │   ├── IngredientRepository.java
    │   │               │   ├── JdbcIngredientRepository.java
    │   │               │   ├── JdbcOrderRepository.java
    │   │               │   ├── JdbcTacoRepository.java
    │   │               │   ├── OrderRepository.java
    │   │               │   └── TacoRepository.java
    │   │               └── web
    │   │                   ├── DesignTacoController.java
    │   │                   ├── FinishOrderController.java
    │   │                   ├── HomeController.java
    │   │                   ├── OrderController.java
    │   │                   └── WebConfig.java
    │   └── resources
    │       ├── application.properties
    │       ├── data.sql
    │       ├── schema.sql
    │       ├── static
    │       │   └── images
    │       │       └── TacoCloud.png
    │       └── templates
    │           ├── design.html
    │           ├── finish.html
    │           ├── home.html
    │           └── orderForm.html
    └── test
        └── java
```

### 书上的几处错误
第五版属于预印版，错误较多，书上代码经常编译不过，

#### `Taco@Ingredients`字段
P78页，`Taco.java`类中的`ingredients`字段，在第二章的类型为`List<String>`，到第三章，突然变成`List<Ingredient>`，
![error1](/images/spring-in-action-demo-ch03/error1.png)

在`JdbcTacoRepository.java`的`save()`函数里，使用的也是`Ingredient`而不是`String`，
![error1-2](/images/spring-in-action-demo-ch03/error1-2.png)

但问题是之前的`design.html`中表单采集的信息赋值给了`ingredient.id`字段，并且是`String`类型。所以这里突然变成`List<Ingredient>`编译通不过。
![error1-3](/images/spring-in-action-demo-ch03/error1-3.png)

所以现在还是将`Taco#ingredients`字段保持为`List<String>`，储存的是`Ingredient#id`信息。
![error1-4](/images/spring-in-action-demo-ch03/error1-4.png)

#### `JdbcTacoRepository#saveTacoInfo()`函数
`JdbcTacoRepository#saveTacoInfo()`函数用到了`org.springframework.jdbc.core.PreparedStatementCreator`，负责把`Taco`对象应用于SQL语句。然后再用`KeyHolder`类创建索引`id`，
![error2](/images/spring-in-action-demo-ch03/error2.png)

但`KeyHolder`类返回值一直为`null`，无法通过编译。
```
KeyHolder keyHolder = new GeneratedKeyHolder();
```

所以我这里借鉴了`JdbcOrderRepository`类的做法：用了`org.springframework.jdbc.core.simple.SimpleJdbcInsert` + `com.fasterxml.jackson.databind.ObjectMapper`的组合，来串流化`Taco`对象，
![error2-2](/images/spring-in-action-demo-ch03/error2-2.png)

#### `orderForm.html`的字段和`Order.java`匹配不上
`Order.java`中定义了`name`,`street`,`city`,`state`等字段，
![error3](/images/spring-in-action-demo-ch03/error3.png)

`orderForm.html`里表单关联的字段名也是`name`,`street`,`city`,`state`，
![error3-2](/images/spring-in-action-demo-ch03/error3-2.png)

但到了定义数据库表格的`schema.sql`里，变成了`deliveryName`,`deliveryStreet`等等，导致数据库储存失败。这里我统一把名字改成了`dName`和`dStreet`这种格式，
![error3-3](/images/spring-in-action-demo-ch03/error3-3.png)
![error3-4](/images/spring-in-action-demo-ch03/error3-4.png)
![error3-5](/images/spring-in-action-demo-ch03/error3-5.png)

#### `@Valid`验证错误提示无法显示
下图中的红色提示并没有出现，
![error4](/images/spring-in-action-demo-ch03/error4.png)

这里因为不影响大局，我没有修改。

### 具体代码
