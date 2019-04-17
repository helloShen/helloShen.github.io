---
layout: post
title: "Spring in Action 5th Edition - Chapter 2"
date: 2019-03-23 20:49:29
author: "Wei SHEN"
categories: ["spring"]
tags: ["web", "mvc"]
description: >
---

### 前言
注意本章书上有个严重的疏漏，P33页的`DesignTacoController.java`缺少`filterByType()`函数。我这里的版本补上了。

### 初始化本章Demo项目
使用在线初始化工具`https://start.spring.io`更快捷。基本配置如下图所示，
![initialize-1](/images/spring-in-action-demo-ch02/initialize-1.png)
![initialize-2](/images/spring-in-action-demo-ch02/initialize-2.png)

依赖的库除了沿袭上一章的`Web`和`Thymeleaf`外，又加了一个`Lombok`。主要为了简化像`Ingredient`这样的POJO类的代码。Lombok会自动生成像`equals()`,`hashCode()`和`toString()`这些常用基本方法。


生成之后，项目根目录为`ch02/sia-ch02-taco/`，项目结构如下，
```
.
└── sia-ch02-taco
    ├── HELP.md
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
        │   │           └── sia_ch02_taco
        │   │               ├── ServletInitializer.java
        │   │               └── SiaCh02TacoApplication.java
        │   └── resources
        │       ├── application.properties
        │       ├── static
        │       └── templates
        └── test
            └── java
                └── com
                    └── ciaoshen
                        └── sia_ch02_taco
                            └── SiaCh02TacoApplicationTests.java
```

### 基本流程
由3对主要`Controller-View`构成，
1. 主页`localhost:8080/`对应`home.html`模板。和书上例子稍微有点不同，书上的主页没有跳转到`localhost:8080/design`的功能，需要手动输出url访问。我在主页右下角添加一个链接，模拟给客户的点餐入口。
![sia-ch02-taco-home](/images/spring-in-action-demo-ch02/sia-ch02-taco-home.png)

2. 点餐选Taco的馅料页面，url为`localhost:8080/design`，对应`design.html`模板。客户勾选想要的馅料，填上姓名，点击提交订单按钮。填写信息符合要求后，进入点餐下一环节。不符合要求则重新填写。
![sia-ch02-taco-design](/images/spring-in-action-demo-ch02/sia-ch02-taco-design.png)

3. 付款页面，url为`localhost:8080/orders/new`，对应`orderForm.html`模板。客户填写银行卡信息，提交表单模拟下单。付款成功后返回主页。同样，不符合要求的付款信息，退回重新填写。
![sia-ch02-taco-order](/images/spring-in-action-demo-ch02/sia-ch02-taco-order.png)
![sia-ch02-taco-invalid-order](/images/spring-in-action-demo-ch02/sia-ch02-taco-invalid-order.png)

3个`Controllor`放在`com.ciaoshen.sia_ch02_taco.web`包，
1. `HomeController.java`: 主页控制器。
2. `DesignTacoController.java`: 肉卷配料点餐控制器。
3. `OrderController.java`: 付款页面控制器。

3个`View`的模板放在`./src/main/resources/templates/`目录下，
1. `home.html`: 主页。
2. `design.html`: 肉卷馅料选单页面。
3. `orderForm.html`: 银行付款页面。

其他就是封装数据的基本对象类，直接放在`com.ciaoshen.sia_ch02_taco`包，
1. `Taco.java`: 卷饼对象。里面主要包含一个配料列表`List<String>`。
2. `Order.java`: 订单对象。主要是一系列和银行卡有关的付款信息。
3. `Ingredient.java`: 每种配料的具体对象。是个枚举型，表示仅有的几种可选配料。

最后`SiaCh02TacoApplication.java`类是应用的主入口。最终我们的`./src/`目录看上去像这个样子，
```
└── src
    └── main
        ├── java
        │   └── com
        │       └── ciaoshen
        │           └── sia_ch02_taco
        │               ├── Ingredient.java
        │               ├── Order.java
        │               ├── ServletInitializer.java
        │               ├── SiaCh02TacoApplication.java
        │               ├── Taco.java
        │               └── web
        │                   ├── DesignTacoController.java
        │                   ├── HomeController.java
        │                   ├── OrderController.java
        │                   └── WebConfig.java
        └── resources
            ├── application.properties
            ├── static
            │   └── images
            │       └── TacoCloud.png
            └── templates
                ├── design.html
                ├── home.html
                └── orderForm.html
```

### 代码

#### 3个`Controller`
##### `HomeController.java`
```java
package com.ciaoshen.sia_ch02_taco.web;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import com.ciaoshen.sia_ch02_taco.Order;
import com.ciaoshen.sia_ch02_taco.Taco;

@Slf4j
@Controller
@RequestMapping("/")
public class HomeController {

    // 主页显示design.html
    @GetMapping
    public String designForm(Model model) {
        model.addAttribute("taco", new Taco());
        return "home";
    }

    // 从orderForm填完点餐单回到主页
    @PostMapping
    public String processOrder(@Valid Order order, Errors errors) {
        if (errors.hasErrors()) {
            return "redirect:/orders/new";
            // return "home";  // 假装表单合格，为了方便测试，否则填正确表单太麻烦。
        }
        log.info("Processing order: " + order);
        return "home";
    }

}
```

##### `DesignTacoController.java`
注意，这里补上了`filterByType()`函数。

```java
package com.ciaoshen.sia_ch02_taco.web;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import com.ciaoshen.sia_ch02_taco.Taco;
import com.ciaoshen.sia_ch02_taco.Ingredient;
import com.ciaoshen.sia_ch02_taco.Ingredient.Type;

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {
    @GetMapping
    public String showDesignForm(Model model) {
        List<Ingredient> ingredients = Arrays.asList(
            new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
            new Ingredient("COTO", "Corn Tortilla", Type.WRAP),
            new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
            new Ingredient("CARN", "Carnitas", Type.PROTEIN),
            new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),
            new Ingredient("LETC", "Lettuce", Type.VEGGIES),
            new Ingredient("CHED", "Cheddar", Type.CHEESE),
            new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),
            new Ingredient("SLSA", "Salsa", Type.SAUCE),
            new Ingredient("SRCR", "Sour Cream", Type.SAUCE)
        );
        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }
        model.addAttribute("design", new Taco());
        return "design";
    }

    /** 书上的错误： 书上没有列出这个函数。需要补上。 */
    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients.stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());

    }

}
```

##### `OrderController.java`
```java
package com.ciaoshen.sia_ch02_taco.web;

import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import com.ciaoshen.sia_ch02_taco.Order;
import com.ciaoshen.sia_ch02_taco.Taco;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {

    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors) {
        if (errors.hasErrors()) {
            /*
             * 会报错500：template can not extract "/design.html"
             * 因为使用design.html模板需要一个Taco对象。构建Taco对象比较复杂，
             * 在DesignTacoController已经实现了构建Taco对象。所以这里直接跳转到/design最简单。
             */
            // return "design";
            return "redirect:/design";
        }
        // Save the taco design...
        // We'll do this in chapter 3
        log.info("Processing design: " + design);
        return "redirect:/orders/new"; // 本章暂不涉及数据传递，所以只是简单的页面跳转。简单模拟点餐结束后完成整个点单和送货地址记录过程。
    }

    @GetMapping("/new")
    public String orderForm(Model model) {
        model.addAttribute("order", new Order());
        return "orderForm";
    }

}
```

#### 3个`View`
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
    <form method="POST" th:action="@{/orders}" th:object="${design}">
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
        <form method="POST" th:action="@{/}" th:object="${order}">
            <h1>Order your taco creations!</h1>
            <img th:src="@{/images/TacoCloud.png}"/>
            <a th:href="@{/design}" id="another">Design another taco</a><br/>
            <div th:if="${#fields.hasErrors()}">
                <span class="validationError">
                    Please correct the problems below and resubmit.
                </span>
            </div>

            <h3>Deliver my taco masterpieces to...</h3>
            <label for="name">Name: </label>
            <input type="text" th:field="*{name}"/>
            <span class="validationError"
                th:if="${#fields.hasErrors('name')}"
                th:errors="*{name}">Name is required</span>
            <br/>
            <label for="street">Street address: </label>
            <input type="text" th:field="*{street}"/>
            <br/>
            <label for="city">City: </label>
            <input type="text" th:field="*{city}"/>
            <span class="validationError"
                th:if="${#fields.hasErrors('city')}"
                th:errors="*{city}">City is required</span>
            <br/>
            <label for="state">State: </label>
            <input type="text" th:field="*{state}"/>
            <br/>
            <label for="zip">Zip code: </label>
            <input type="text" th:field="*{zip}"/>
            <span class="validationError"
                th:if="${#fields.hasErrors('zip')}"
                th:errors="*{zip}">Zip code is required</span>
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

#### 数据实体类
##### `Taco.java`
```java
package com.ciaoshen.sia_ch02_taco;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class Taco {
    @NotNull
    @Size(min=5, message="Name must be at least 5 characters long")
    private String name;
    @Size(min=1, message="You must choose at least 1 ingredient")
    private List<String> ingredients;
}
```

##### `Order.java`
```java
package com.ciaoshen.sia_ch02_taco;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.CreditCardNumber;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Order {
    @NotBlank(message="Name is required")
    private String name;
    @NotBlank(message="Street is required")
    private String street;
    @NotBlank(message="City is required")
    private String city;
    @NotBlank(message="State is required")
    private String state;
    @NotBlank(message="Zip code is required")
    private String zip;
    @CreditCardNumber(message="Not a valid credit card number")
    private String ccNumber;
    @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
    message="Must be formatted MM/YY")
    private String ccExpiration;
    @Digits(integer=3, fraction=0, message="Invalid CVV")
    private String ccCVV;
}
```

##### `Ingredient.java`
```java
package com.ciaoshen.sia_ch02_taco;

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

### 运行
在项目根目录下运行命令行，
```
gradle build
```

在`./build/classes/libs/`目录下得到一个`war`包`sia-ch02-taco-0.0.1-SNAPSHOT.war`。整个项目的结构看上去像下面这个样子，
```
.
├── HELP.md
├── bin
│   ├── main
│   │   ├── application.properties
│   │   ├── com
│   │   │   └── ciaoshen
│   │   │       └── sia_ch02_taco
│   │   │           ├── Ingredient$Type.class
│   │   │           ├── Ingredient.class
│   │   │           ├── Order.class
│   │   │           ├── ServletInitializer.class
│   │   │           ├── SiaCh02TacoApplication.class
│   │   │           ├── Taco.class
│   │   │           └── web
│   │   │               ├── DesignTacoController.class
│   │   │               ├── HomeController.class
│   │   │               ├── OrderController.class
│   │   │               └── WebConfig.class
│   │   ├── static
│   │   │   └── images
│   │   │       └── TacoCloud.png
│   │   └── templates
│   │       ├── design.html
│   │       ├── home.html
│   │       └── orderForm.html
│   └── test
│       └── com
│           └── ciaoshen
│               └── sia_ch02_taco
├── build
│   ├── classes
│   │   └── java
│   │       └── main
│   │           └── com
│   │               └── ciaoshen
│   │                   └── sia_ch02_taco
│   │                       ├── Ingredient$Type.class
│   │                       ├── Ingredient.class
│   │                       ├── Order.class
│   │                       ├── ServletInitializer.class
│   │                       ├── SiaCh02TacoApplication.class
│   │                       ├── Taco.class
│   │                       └── web
│   │                           ├── DesignTacoController.class
│   │                           ├── HomeController.class
│   │                           ├── OrderController.class
│   │                           └── WebConfig.class
│   ├── libs
│   │   └── sia-ch02-taco-0.0.1-SNAPSHOT.war
│   ├── reports
│   │   └── tests
│   ├── resources
│   │   └── main
│   │       ├── application.properties
│   │       ├── static
│   │       │   └── images
│   │       │       └── TacoCloud.png
│   │       └── templates
│   │           ├── design.html
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
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── ciaoshen
│   │   │           └── sia_ch02_taco
│   │   │               ├── Ingredient.java
│   │   │               ├── Order.java
│   │   │               ├── ServletInitializer.java
│   │   │               ├── SiaCh02TacoApplication.java
│   │   │               ├── Taco.java
│   │   │               └── web
│   │   │                   ├── DesignTacoController.java
│   │   │                   ├── HomeController.java
│   │   │                   ├── OrderController.java
│   │   │                   └── WebConfig.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── static
│   │       │   └── images
│   │       │       └── TacoCloud.png
│   │       └── templates
│   │           ├── design.html
│   │           ├── home.html
│   │           └── orderForm.html
│   └── test
│       └── java
└── temp
    └── com
        └── ciaoshen
            └── sia_ch02_taco
                └── SiaCh02TacoApplicationTests.java
```

最终运行输入命令行，
```
gradle bootRun
```
