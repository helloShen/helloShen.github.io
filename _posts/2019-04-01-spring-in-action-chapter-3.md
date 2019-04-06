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
前三组第二章就有，最后一组是我加的显示订单信息的页面。

### 3类数据访问对象（DAO）
`Ingredient`,`Taco`,`Order`三种数据主体涉及的关系型数据库表结构如下，
![relations](/images/spring-in-action-demo-ch03/relations.png)

对每一种数据主体的全部数据存取操作，被封装在一个`XXXRepository`接口类，定义数据存取操作函数，以及一个`JdbcXXXXRepositiory`具体实现类里，
1. `IngredientRepository.java`以及`JdbcIngredientRepository.java`: 肉卷的可选配料表
2. `TacoRepository.java`以及`JdbcTacoRepository.java`: 客户选择的单个肉卷的配料，以及肉卷名称信息
3. `OrderRepository.java`以及`JdbcOrderRepository.java`: 整个订单的信息（包括付款信息，以及配送地址等信息）

`schema.sql`创建数据库结构，
```sql
create table if not exists Ingredient (
    id varchar(4) not null,
    name varchar(25) not null,
    type varchar(10) not null
);

create table if not exists Taco (
    id identity,
    name varchar(50) not null,
    createdAt timestamp not null
);

create table if not exists Taco_Ingredients (
    taco bigint not null,
    ingredient varchar(4) not null
);

alter table Taco_Ingredients add foreign key (taco) references Taco(id);

alter table Taco_Ingredients add foreign key (ingredient) references Ingredient(id);

create table if not exists Taco_Order (
    id identity,
    dName varchar(50) not null,
    dStreet varchar(50) not null,
    dCity varchar(50) not null,
    dState varchar(2) not null,
    dZip varchar(10) not null,
    ccNumber varchar(16) not null,
    ccExpiration varchar(5) not null,
    ccCVV varchar(3) not null,
    placedAt timestamp not null
);

create table if not exists Taco_Order_Tacos (
    tacoOrder bigint not null,
    taco bigint not null
);

alter table Taco_Order_Tacos
add foreign key (tacoOrder) references Taco_Order(id);
alter table Taco_Order_Tacos
add foreign key (taco) references Taco(id);
```

店家提供的馅料表单在`data.sql`，
```sql
delete from Taco_Order_Tacos;
delete from Taco_Ingredients;
delete from Taco;
delete from Taco_Order;
delete from Ingredient;

insert into Ingredient (id, name, type) values ('FLTO', 'Flour Tortilla', 'WRAP');
insert into Ingredient (id, name, type) values ('COTO', 'Corn Tortilla', 'WRAP');
insert into Ingredient (id, name, type) values ('GRBF', 'Ground Beef', 'PROTEIN');
insert into Ingredient (id, name, type) values ('CARN', 'Carnitas', 'PROTEIN');
insert into Ingredient (id, name, type) values ('TMTO', 'Diced Tomatoes', 'VEGGIES');
insert into Ingredient (id, name, type) values ('LETC', 'Lettuce', 'VEGGIES');
insert into Ingredient (id, name, type) values ('CHED', 'Cheddar', 'CHEESE');
insert into Ingredient (id, name, type) values ('JACK', 'Monterrey Jack', 'CHEESE');
insert into Ingredient (id, name, type) values ('SLSA', 'Salsa', 'SAUCE');
insert into Ingredient (id, name, type) values ('SRCR', 'Sour Cream', 'SAUCE');
```

### Java对象到JDBC数据库的映射
一份订单包含很多信息，转化成一个`Order`POJO对象包含很多字段。
```java
@Data
public class Order {
    private Long id;
    private Date placedAt;
    private String dName;
    private String dStreet;
    private String dCity;
    private String dState;
    private String dZip;
    private String ccNumber;
    private String ccExpiration;
    private String ccCVV;

    private List<Taco> tacos = new ArrayList<>();

    public void addDesign(Taco design) {
        tacos.add(design);
    }
}
```

`JdbcOrderRepository`类虽然总体负责将`Order`对象存入数据库这件事，但它不是直接把`Order`对象转化成JDBC数据库能接受数据格式的那个部分。

书上这步转化主要靠2个类，
1. `com.fasterxml.jackson.databind.ObjectMapper`
2. `org.springframework.jdbc.core.simple.SimpleJdbcInsert`

首先和JDBC对接的`SimpleJdbcInsert#executeAndReturnKey(Map<String, Object> map)`函数，接受一个`Map<String, Object>`型参数。`ObjectMapper#convertValue()`就是负责把`Order`对象转化成`Map<String, Object>`键值对。

假设某个订单具体信息如下，
```java
public class Order {
    private Long id = 1;
    private Date placedAt = new Date();
    private String dName = "shen";
    private String dStreet = "1111 University Road";
    ...
    ...
}
```

经过`ObjectMapper#convertValue()`转化的`Map`结构就变成，
```
键           值
String      Object
------------------------
id          1
placedAt    "01/01/2019"
dName       "shen"
dStreet     "1111 University Road"
...
...
```

看下面代码，可以用`ObjectMapper`自动转化，也可以直接创建一个`HashMap`对象，手动添加每一个字段。
```java
private long saveOrderDetails(Order order) {
    @SuppressWarnings("unchecked")
    Map<String, Object> values = objectMapper.convertValue(order, Map.class);
    values.put("placedAt", order.getPlacedAt());
    long orderId = orderInserter.executeAndReturnKey(values).longValue();
    return orderId;
}
private void saveTacoToOrder(Taco taco, long orderId) {
    Map<String, Object> values = new HashMap<>();
    values.put("tacoOrder", orderId);
    values.put("taco", taco.getId());
    orderTacoInserter.execute(values);
}
```

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

但到了定义数据库表格的`schema.sql`里，变成了`deliveryName`,`deliveryStreet`等等，
![error3-6](/images/spring-in-action-demo-ch03/error3-6.png)

导致数据库储存失败。这里我统一把名字改成了`dName`和`dStreet`这种格式，
![error3-3](/images/spring-in-action-demo-ch03/error3-3.png)
![error3-4](/images/spring-in-action-demo-ch03/error3-4.png)
![error3-5](/images/spring-in-action-demo-ch03/error3-5.png)

#### `@Valid`验证错误提示无法显示
下图中的红色提示并没有出现，
![error4](/images/spring-in-action-demo-ch03/error4.png)

这里因为不影响大局，我没有修改。

### 具体代码

#### 4对`Controller`和`View`

##### `HomeController.java`
```java
package com.ciaoshen.sia_ch03_taco.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String processRequest() {
        return "home";
    }

}
```

##### `home.html`
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
xmlns:th="http://www.thymeleaf.org">

    <head>
        <title>Taco Cloud</title>
    </head>

    <body>
        <h1>Welcome to...</h1>
        <img th:src="@{/images/TacoCloud.png}"/> <!-- abs path = ./resources/static/images/TacoCloud.png -->
        <a th:href="@{/design}">Order your specific Taco!</a>
    </body>

</html>
```

##### `DesignTacoController.java`
```java
package com.ciaoshen.sia_ch03_taco.web;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

import com.ciaoshen.sia_ch03_taco.Taco;
import com.ciaoshen.sia_ch03_taco.Order;
import com.ciaoshen.sia_ch03_taco.Ingredient;
import com.ciaoshen.sia_ch03_taco.Ingredient.Type;

import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;

import com.ciaoshen.sia_ch03_taco.data.IngredientRepository;
import com.ciaoshen.sia_ch03_taco.data.JdbcIngredientRepository;
import com.ciaoshen.sia_ch03_taco.data.TacoRepository;
import com.ciaoshen.sia_ch03_taco.data.JdbcTacoRepository;
import com.ciaoshen.sia_ch03_taco.data.OrderRepository;
import com.ciaoshen.sia_ch03_taco.data.JdbcOrderRepository;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;
    private TacoRepository designRepo;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository designRepo) {
        this.ingredientRepo = ingredientRepo;
        this.designRepo = designRepo;
    }

    @ModelAttribute(name = "order")
    public Order order() {
        return new Order();
    }

    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    @GetMapping
    public String showDesignForm(Model model) {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredientRepo.findAll().forEach(i -> ingredients.add(i));

        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }
        model.addAttribute("design", new Taco());
        return "design";
    }

    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order) {
        if (errors.hasErrors()) {
            if (log.isInfoEnabled()) {
                log.info("errors = {}", errors.toString());
            }
            return "redirect:/design";
        }
        Taco saved = designRepo.save(design);
        order.addDesign(saved);
        return "redirect:/orders/new";
    }

    /** 书上的错误： 书上没有列出这个函数。需要补上。 */
    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients.stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());

    }

}
```

##### `design.html`
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Taco Cloud</title>
    <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
    <h1>Design your taco!</h1>
    <img th:src="@{/images/TacoCloud.png}"/>
    <form method="POST" th:action="@{/design}" th:object="${design}">
        <div class="grid">
            <div class="ingredient-group" id="wraps">
                <h3>Designate your wrap:</h3>
                <div th:each="ingredient : ${wrap}">
                    <input name="ingredients" type="checkbox" th:value="${ingredient.id}"/>
                    <span th:text="${ingredient.name}">INGREDIENT</span><br/>
                </div>
            </div>

            <div class="ingredient-group" id="proteins">
                <h3>Pick your protein:</h3>
                <div th:each="ingredient : ${protein}">
                    <input name="ingredients" type="checkbox" th:value="${ingredient.id}"/>
                    <span th:text="${ingredient.name}">INGREDIENT</span><br/>
                </div>
            </div>

            <div class="ingredient-group" id="cheeses">
                <h3>Choose your cheese:</h3>
                <div th:each="ingredient : ${cheese}">
                    <input name="ingredients" type="checkbox" th:value="${ingredient.id}"/>
                    <span th:text="${ingredient.name}">INGREDIENT</span><br/>
                </div>
            </div>

            <div class="ingredient-group" id="veggies">
                <h3>Determine your veggies:</h3>
                <div th:each="ingredient : ${veggies}">
                    <input name="ingredients" type="checkbox" th:value="${ingredient.id}"/>
                    <span th:text="${ingredient.name}">INGREDIENT</span><br/>
                </div>
            </div>

            <div class="ingredient-group" id="sauces">
                <h3>Select your sauce:</h3>
                <div th:each="ingredient : ${sauce}">
                    <input name="ingredients" type="checkbox" th:value="${ingredient.id}"/>
                    <span th:text="${ingredient.name}">INGREDIENT</span><br/>
                </div>
            </div>
        </div>

        <div>
            <h3>Name your taco creation:</h3>
            <input type="text" th:field="*{name}"/>
            <br/>
            <button>Submit your taco</button>
        </div>
    </form>
</body>
</html>
```

##### `OrderController.java`
```java
package com.ciaoshen.sia_ch03_taco.web;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import lombok.extern.slf4j.Slf4j;

import com.ciaoshen.sia_ch03_taco.Taco;
import com.ciaoshen.sia_ch03_taco.Order;
import com.ciaoshen.sia_ch03_taco.data.OrderRepository;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("order")
public class OrderController {

    private OrderRepository orderRepo;

    public OrderController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @PostMapping
    public String processOrder(@Valid Order order, Errors errors,
        SessionStatus sessionStatus) {
        if (errors.hasErrors()) {
            return "orderForm";
        }
        orderRepo.save(order);
        return "redirect:/orders/finish";
    }

    /** 这里书上是错的： 不能创建新new Order()，
     *  必须从session里调取DesignTacoController里创建的order对象 */
    @GetMapping("/new")
    public String orderForm(Model model, Order order) {
        model.addAttribute("order", order);
        return "orderForm";
    }

}
```

##### `orderForm.html`
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>Taco Cloud</title>
        <link rel="stylesheet" th:href="@{/styles.css}" />
    </head>

    <body>
        <form method="POST" th:action="@{/orders}" th:object="${order}">
            <h1>Order your taco creations!</h1>
            <img th:src="@{/images/TacoCloud.png}"/>
            <a th:href="@{/design}" id="another">Design another taco</a><br/>
            <div th:if="${#fields.hasErrors()}">
                <span class="validationError">
                    Please correct the problems below and resubmit.
                </span>
            </div>

            <!--原本这里数据名：name, street，但schema.sql里是deliveryName, deliveryStreet，
                会导致导入数据库失败。-->
            <!--数据名统一改成：dName, dStreet的形式。在schema.sql里也改。-->
            <h3>Deliver my taco masterpieces to...</h3>
            <label for="dName">Name: </label>
            <input type="text" th:field="*{dName}"/>
            <span class="validationError"
                th:if="${#fields.hasErrors('dName')}"
                th:errors="dName}">Name is required</span>
            <br/>
            <label for="dStreet">Street address: </label>
            <input type="text" th:field="*{dStreet}"/>
            <br/>
            <label for="dCity">City: </label>
            <input type="text" th:field="*{dCity}"/>
            <span class="validationError"
                th:if="${#fields.hasErrors('dCity')}"
                th:errors="dCity">City is required</span>
            <br/>
            <label for="dState">State: </label>
            <input type="text" th:field="*{dState}"/>
            <br/>
            <label for="dZip">Zip code: </label>
            <input type="text" th:field="*{dZip}"/>
            <span class="validationError"
                th:if="${#fields.hasErrors('dZip')}"
                th:errors="dZip">Zip code is required</span>
            <br/>

            <h3>Here's how I'll pay...</h3>
            <label for="ccNumber">Credit Card #: </label>
            <input type="text" th:field="*{ccNumber}"/>
            <span class="validationError"
                th:if="${#fields.hasErrors('ccNumber')}"
                th:errors="*{ccNumber}">Not a valid credit card number</span>
            <br/>
            <label for="ccExpiration">Expiration: </label>
            <input type="text" th:field="*{ccExpiration}"/>
            <span class="validationError"
                th:if="${#fields.hasErrors('ccExpiration')}"
                th:errors="*{ccExpiration}">Not a valid credit card number</span>
            <br/>
            <label for="ccCVV">CVV: </label>
            <input type="text" th:field="*{ccCVV}"/>
            <span class="validationError"
                th:if="${#fields.hasErrors('ccCVV')}"
                th:errors="*{ccCVV}">Invalid CVV</span>
            <br/>
            <input type="submit" value="Submit order"/>
        </form>
    </body>
</html>
```

##### `FinishOrderController.java`
```java
package com.ciaoshen.sia_ch03_taco.web;

import java.util.List;
import java.util.ArrayList;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import lombok.extern.slf4j.Slf4j;

import com.ciaoshen.sia_ch03_taco.Ingredient;
import com.ciaoshen.sia_ch03_taco.Order;
import com.ciaoshen.sia_ch03_taco.Taco;
import com.ciaoshen.sia_ch03_taco.data.TacoRepository;
import com.ciaoshen.sia_ch03_taco.data.JdbcTacoRepository;
import com.ciaoshen.sia_ch03_taco.data.OrderRepository;
import com.ciaoshen.sia_ch03_taco.data.IngredientRepository;
import com.ciaoshen.sia_ch03_taco.data.JdbcOrderRepository;

@Slf4j
@Controller
@RequestMapping("/orders/finish")
@SessionAttributes("order")
public class FinishOrderController {

    private IngredientRepository ingredientRepo;

    @Autowired
    public FinishOrderController(IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    @GetMapping
    public String showOrder(Model model, Order order, SessionStatus sessionStatus) {
        model.addAttribute("myTacos", order.getTacos());

        model.addAttribute("name", order.getDName());
        model.addAttribute("street", order.getDStreet());
        model.addAttribute("city", order.getDCity());
        model.addAttribute("state", order.getDState());
        model.addAttribute("zip", order.getDZip());
        sessionStatus.setComplete();
        return "finish";
    }

}
```

##### `finish.html`
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
xmlns:th="http://www.thymeleaf.org">

    <head>
        <title>Taco Cloud</title>
        <style>
            table {
                font-family: arial, sans-serif;
                border-collapse: collapse;
                width: 100%;
            }

            td, th {
                border: 1px solid #dddddd;
                text-align: left;
                padding: 8px;
            }

            tr:nth-child(even) {
                background-color: #dddddd;
            }
          </style>
    </head>

    <body>
        <h1>Your Taco will be delivered soon!</h1>
        <table th:each="taco : ${myTacos}">
            <tr>
                <td>Taco Name</td>
                <td th:text="${taco.name}">TACO-NAME</td>
            </tr>
            <tr th:each="ingredientId : ${taco.ingredients}">
                <td>Ingredient: </td>
                <td th:text="${ingredientId}">INGREDIENT-ID</td>
            </tr>
        </table>

        <h2>Your Infomations</h2>
        <table>
            <tr>
              <td>Name: </td>
              <td th:text="${name}">YOUR-NAME</td>
            </tr>
            <tr>
              <td>Street: </td>
              <td th:text="${street}">YOUR-STREET</td>
            </tr>
            <tr>
              <td>City: </td>
              <td th:text="${city}">YOUR-CITY</td>
            </tr>
            <tr>
              <td>State: </td>
              <td th:text="${state}">YOUR-STATE</td>
            </tr>
            <tr>
              <td>Zip: </td>
              <td th:text="${zip}">YOUR-ZIP</td>
            </tr>
        </table>
    <body>

</html>
```

#### 3对`Repository`接口和实现类

##### `IngredientRepository.java`
```java
package com.ciaoshen.sia_ch03_taco.data;

import com.ciaoshen.sia_ch03_taco.Ingredient;

public interface IngredientRepository {
    Iterable<Ingredient> findAll();
    Ingredient findOne(String id);
    Ingredient save(Ingredient ingredient);
}
```

##### `JdbcIngredientRepository.java`
```java
package com.ciaoshen.sia_ch03_taco.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.ciaoshen.sia_ch03_taco.Ingredient;

@Repository
public class JdbcIngredientRepository implements IngredientRepository {

    private JdbcTemplate jdbc;

    @Autowired
    public JdbcIngredientRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Iterable<Ingredient> findAll() {
        return jdbc.query("select id, name, type from Ingredient",
        this::mapRowToIngredient);
    }

    @Override
    public Ingredient findOne(String id) {
        return jdbc.queryForObject("select id, name, type from Ingredient where id=?",
        this::mapRowToIngredient, id);
    }

    private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
        return new Ingredient(
            rs.getString("id"),
            rs.getString("name"),
            Ingredient.Type.valueOf(rs.getString("type")));
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        jdbc.update("insert into Ingredient (id, name, type) values (?, ?, ?)",
            ingredient.getId(),
            ingredient.getName(),
            ingredient.getType().toString());
        return ingredient;
    }

}
```

##### `TacoRepository.java`
```java
package com.ciaoshen.sia_ch03_taco.data;

import com.ciaoshen.sia_ch03_taco.Taco;

public interface TacoRepository {
    Taco save(Taco design);
}
```

##### `JdbcTacoRepository.java`
```java
package com.ciaoshen.sia_ch03_taco.data;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

import com.ciaoshen.sia_ch03_taco.Ingredient;
import com.ciaoshen.sia_ch03_taco.Taco;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Repository
public class JdbcTacoRepository implements TacoRepository {

    private JdbcTemplate jdbc;
    private SimpleJdbcInsert tacoInserter;
    private ObjectMapper objectMapper;

    @Autowired
    public JdbcTacoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.tacoInserter = new SimpleJdbcInsert(jdbc)
            .withTableName("Taco")
            .usingGeneratedKeyColumns("id");
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Taco save(Taco taco) {
        long tacoId = saveTacoInfo(taco);
        taco.setId(tacoId);
        for (String ingredient : taco.getIngredients()) {
            saveIngredientToTaco(ingredient, tacoId);
        }
        return taco;
    }

    /** 作废！这里KeyHolder返回的null，导致design页面制作的Taco信息无法储存数据库 */
    // private long saveTacoInfo(Taco taco) {
    //     taco.setCreatedAt(new Date());
    //     PreparedStatementCreator psc = new PreparedStatementCreatorFactory(
    //         "insert into Taco (name, createdAt) values (?, ?)",
    //         Types.VARCHAR, Types.TIMESTAMP).newPreparedStatementCreator(
    //             Arrays.asList(taco.getName(),
    //             new Timestamp(taco.getCreatedAt().getTime())));
    //     KeyHolder keyHolder = new GeneratedKeyHolder();
    //     if (log.isInfoEnabled()) {
    //         log.info("New Taco Id = {}", keyHolder.getKey());
    //     }
    //     jdbc.update(psc, keyHolder);
    //     return keyHolder.getKey().longValue();
    // }

    /** 这个利用ObjectMapper的方案借鉴了JdbcOrderRepository.java */
    private long saveTacoInfo(Taco taco) {
        taco.setCreatedAt(new Date());
        @SuppressWarnings("unchecked")
        Map<String, Object> values = objectMapper.convertValue(taco, Map.class);
        values.put("createdAt", taco.getCreatedAt());
        long tacoId = tacoInserter.executeAndReturnKey(values).longValue();
        return tacoId;
    }

    private void saveIngredientToTaco(
        String ingredient, long tacoId) {
            jdbc.update("insert into Taco_Ingredients (taco, ingredient) " +
                        "values (?, ?)", tacoId, ingredient);
    }
}
```

##### `OrderRepository.java`
```java
package com.ciaoshen.sia_ch03_taco.data;

import com.ciaoshen.sia_ch03_taco.Order;

public interface OrderRepository {
    Order save(Order order);
}
```

##### `JdbcOrderRepository.java`
```java
package com.ciaoshen.sia_ch03_taco.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ciaoshen.sia_ch03_taco.Taco;
import com.ciaoshen.sia_ch03_taco.Order;

@Repository
public class JdbcOrderRepository implements OrderRepository {

    private SimpleJdbcInsert orderInserter;
    private SimpleJdbcInsert orderTacoInserter;
    private ObjectMapper objectMapper;

    @Autowired
    public JdbcOrderRepository(JdbcTemplate jdbc) {
        this.orderInserter = new SimpleJdbcInsert(jdbc)
            .withTableName("Taco_Order")
            .usingGeneratedKeyColumns("id");
        this.orderTacoInserter = new SimpleJdbcInsert(jdbc)
            .withTableName("Taco_Order_Tacos");
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Order save(Order order) {
        order.setPlacedAt(new Date());
        long orderId = saveOrderDetails(order);
        order.setId(orderId);
        List<Taco> tacos = order.getTacos();
        for (Taco taco : tacos) {
            saveTacoToOrder(taco, orderId);
        }
        return order;
    }
    private long saveOrderDetails(Order order) {
        @SuppressWarnings("unchecked")
        Map<String, Object> values = objectMapper.convertValue(order, Map.class);
        // 因为h2数据库org.h2.util.DateTimeUtils.parseDateValue()无法解析Date型日期
        // 因此必须手动添加
        values.put("placedAt", order.getPlacedAt());
        long orderId = orderInserter.executeAndReturnKey(values).longValue();
        return orderId;
    }
    private void saveTacoToOrder(Taco taco, long orderId) {
        Map<String, Object> values = new HashMap<>();
        values.put("tacoOrder", orderId);
        values.put("taco", taco.getId());
        orderTacoInserter.execute(values);
    }

}
```

#### `Ingredient`,`Taco`,`Order`三个实体类

##### `Ingredient.java`
```java
package com.ciaoshen.sia_ch03_taco;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Ingredient {
    private final String id;
    private final String name;
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
```

##### `Taco.java`
```java
package com.ciaoshen.sia_ch03_taco;

import java.util.List;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class Taco {
    // @NotNull
    private Long id;
    // @NotNull
    private Date createdAt;
    @NotNull
    @Size(min=5, message="Name must be at least 5 characters long")
    private String name;

    /**
     * 书上原来是Ingredient的列表，和第二章List<String>不符。
     * 而且design.html表单也是赋值的也是Ingredient.id字段，
     * 和这里Ingredient不符合。
     * 所以先维持String的设定，在finish.html需要完整Ingredient信息的时候，
     * 再在FinishOrderController.java里通过Ingredient.id字段到数据库里去找Ingredient对象。
     */
    // private List<Ingredient> ingredients;
    @Size(min=1, message="You must choose at least 1 ingredient")
    private List<String> ingredients;

}
```

##### `Order.java`
```java
package com.ciaoshen.sia_ch03_taco;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.CreditCardNumber;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class Order {
    // @NotBlank(message="ID is required")
    private Long id;
    // @NotBlank(message="ID is required")
    private Date placedAt;
    // @NotBlank(message="Name is required")
    private String dName;
    // @NotBlank(message="Street is required")
    private String dStreet;
    // @NotBlank(message="City is required")
    private String dCity;
    // @NotBlank(message="State is required")
    private String dState;
    // @NotBlank(message="Zip code is required")
    private String dZip;
    // @CreditCardNumber(message="Not a valid credit card number")
    private String ccNumber;
    // @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
    // message="Must be formatted MM/YY")
    private String ccExpiration;
    // @Digits(integer=3, fraction=0, message="Invalid CVV")
    private String ccCVV;

    // 书上缺失designs字段和addDesign()函数
    // @Size(min=1, message="Each order must contain at least 1 design of Taco")
    private List<Taco> tacos = new ArrayList<>();

    public void addDesign(Taco design) {
        tacos.add(design);
    }
}
```
