---
layout: post
title: "Spring in Action - Chapter 3"
date: 2019-04-01 21:43:01
author: "Wei SHEN"
categories: ["spring"]
tags: ["jdbc"]
description: >
---

### 添加`Ingredient`数据库后的项目结构
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
│   │   │           │   └── JdbcIngredientRepository.class
│   │   │           └── web
│   │   │               ├── DesignTacoController.class
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
│   │                       │   └── JdbcIngredientRepository.class
│   │                       └── web
│   │                           ├── DesignTacoController.class
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
    │   │               │   └── JdbcIngredientRepository.java
    │   │               └── web
    │   │                   ├── DesignTacoController.java
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
    │           ├── home.html
    │           └── orderForm.html
    └── test
        └── java
```

### `order.addDesign()`函数缺失
```java
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    // code omitted
    ...    

    @M@PostMapping
    public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order) {
        if (errors.hasErrors()) {
            return "design";
        }
        Taco saved = designRepo.save(design);
        order.addDesign(saved); // 函数缺失
        return "redirect:/orders/current";
    }

    // code omitted
    ...

}
```
