---
layout: post
title: "Some Naive Thoughts on the Control Flow"
date: 2017-04-21 00:47:15
author: "Wei SHEN"
categories: ["java"]
tags: ["control flow"]
description: >
---

### 按
这是目前为止，我对自己使用control flow的一些习惯的思考。记录一下。等以后有更深的理解之后，再来修改。

### 关于`for`和`while`

#### 明确已知迭代次数用`for`
像这种约定俗成的遍历数组，数组长度很明确的，当然就按习惯用`for`就好。虽然肯定也能改成`while`，但老老实实用`for`，方便自己和别人阅读。
```java
for (int i = 0; i < array.length; i++) {

}
```

#### 遍历数组或容器用`for`，或者`for each`
实现了`Iterable`接口的容器，用`for each`的语法遍历，代码可读性更好，这么用就是了，不要问为什么。
```java
List<String> strs = new ArrayList<>(Arrays.asList(new String[]{"Hello","World"}));
for (String str : strs) {
    // do something
}
```

#### 不明确迭代次数，只知道终止条件的用`while`
比如像二分查找中，我们不知道循环要执行多少次，只知道当下界下标`low`和上界下标`high`交叉的时候终止迭代，
```java
public int binarySearch() {
    // ... ommited code
    while (low <= high) {
        // binary parse
    }
}
```

#### `for`的优先级只是略微高于`while`
`for`循环稍微优先于`while`的理由很有限：**只不过是`for`把变量都收纳在`{}`域里了。这符合尽量缩短变量生命周期的规则。** 但`for`相对于`while`的好处目前来看也就仅此而已了。

绝对不是说，尽量用`for`代替`while`。**该用`while`的时候，就不应该用`for`来模拟`while`的行为**。下面这个例子是 **错误的！**
```java
// 错误示例。不要用for模拟while的行为。该用while就用while。
for (para = initValue; endCondition(); ) {
    // do something
}
```

### `while`的三种形式
1. 条件判断在循环开头的就是普通的`while`
2. 条件判断在循环结尾的就是`do-while`
3. 带退出条件的`while(true)`

#### 优先考虑带退出条件的`while(true)`
我更喜欢`while(true)`因为它更具有普遍性，无论是判断条件在开头的，还是在结尾的都可以写成`while(true)`并在中间放一个出口。
```java
while (true) {
    if (condition()) { break; }
}
```

#### 中间的退出点不能太多，最多3个
多了会乱。

#### 用`break`和`continue`迅速淘汰逻辑分支
用`break`和`continue`来淘汰某些逻辑分支必须迅速！最好是 **在循环的一开头就迅速淘汰一些干扰的逻辑分支**。好处是 **避免了多层套嵌的判断语句。**

到了逻辑流程腹地就不要到处都是`break`或者`continue`了。比如我下面这个`自底向上`的动态规划的代码，就是对逻辑分层，优先处理优先级更高的规则，再处理更普通的规则。
```java
public int uniquePathsWithObstacles(int[][] obstacleGrid) {
    int lineSize = obstacleGrid.length;
    if (lineSize == 0) { return 0; }
    int columnSize = obstacleGrid[0].length;
    if (columnSize == 0) { return 0; }
    int[][] memo = new int[lineSize][columnSize];
    for (int i = lineSize-1; i >= 0; i--) {
        for (int j = columnSize-1; j >= 0; j--) {
            if (obstacleGrid[i][j] == 1) { memo[i][j] = 0; continue; } // 优先级最高的规则，一旦此格是石头，就代表此路不通，之前所有的路径信息失效。
            if ( (i == lineSize-1) && (j == columnSize-1) ) { memo[i][j] = 1; continue; } // 特殊点：终点到自己的可行路径为1，这是所有动态规划计算的base case。（此时已经确定终点一定不是石头，因为所有是石头的情况已经全部淘汰返回。）
            if (i == lineSize-1) { memo[i][j] = memo[i][j+1]; continue; } // 次特殊情况，底边上的所有点。
            if (j == columnSize-1) { memo[i][j] = memo[i+1][j]; continue; } // 侧特殊情况，右侧边所有点。
            memo[i][j] = memo[i+1][j] + memo[i][j+1]; // 最普遍的情况，其他所有点。
        }
    }
    return memo[0][0];
}
```
上面的代码如果完整地用套嵌条件判断，会有好几层，影响代码可读性。
