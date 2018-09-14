---
layout: post
title: "Leetcode - Algorithm - Asteroid Collision "
date: 2018-09-14 18:32:25
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list","stack"]
level: "medium"
description: >
---

### 题目
We are given an array `asteroids` of integers representing asteroids in a row.

For each asteroid, the absolute value represents its size, and the sign represents its direction (positive meaning right, negative meaning left). Each asteroid moves at the same speed.

Find out the state of the asteroids after all collisions. If two asteroids meet, the smaller one will explode. If both are the same size, both will explode. Two asteroids moving in the same direction will never meet.

Example 1:
```
Input:
asteroids = [5, 10, -5]
Output: [5, 10]
Explanation:
The 10 and -5 collide resulting in 10.  The 5 and 10 never collide.
```

Example 2:
```
Input:
asteroids = [8, -8]
Output: []
Explanation:
The 8 and -8 collide exploding each other.
```

Example 3:
```
Input:
asteroids = [10, 2, -5]
Output: [10]
Explanation:
The 2 and -5 collide resulting in -5.  The 10 and -5 collide resulting in 10.
```

Example 4:
```
Input:
asteroids = [-2, -1, 1, 2]
Output: [-2, -1, 1, 2]
Explanation:
The -2 and -1 are moving left, while the 1 and 2 are moving right.
Asteroids moving the same direction never meet, so no asteroids will meet each other.
```

Note:
* The length of asteroids will be at most 10000.
* Each asteroid will be a non-zero integer in the range [-1000, 1000]..

### 想象成一场向右移动的星球的冒险之旅
考虑下面这个例子，
```
[10, 7, -3, 5, -9, -11, 6, 7]
```
先只考虑向右移动的星球，如果没有向左移动的星球，一切都很和谐，它们一起愉快地向右移动，直到天荒地老。
```
       所以先有[10, 7]，他们很愉快地向右运动。
     | -->
[10, 7]
```
但如果中间夹杂着向左移动的星球，把他们想象成破坏者，它们的将打碎一切他们能打碎的所有向右移动的星球，
```
当遇到`-3`的时候，它尝试打败`7`，但失败了。
      <-- |
[10, 7 , -3]
```
又遇到`5`，继续一起愉快地向右运行，
```
         遇到"5"，一起愉快地向右运动。
        | -->
[10, 7, 5]
```
又遇到`-9`，它打败了`5`和`7`，但最终被`10`打败，
```
              遇到"-9"
            | -->
[10, 7, 5, -9]

  | --> "9"打败了"5"和"7"，但最终被"10"打败。现在只剩下"10"孤独地旅行
[10]
```
但是当遇到`-11`大魔王，连`10`也挡不住它，所以，`-11`大魔王可以自由地往左无限飞。
```
    "-11"自由了 <-- |     | --> "11"打败了"10"，没有向右旅行的星球了。
                [-11]   []
```

所以，最终可以抽象成两个`List`，一个是所有往右运动的星球`moveRight`，另一个是所有冲破障碍得到自由的大魔王左移星球`moveLeft`。所以当最后又遇到`6`和`7`之后，最终两个List如下，
```
左移"-11"自由了 <-- |     | --> 右移只剩下"6"和"7"
                [-11]   [6,7]
```

#### 代码
```java
class Solution {
    public int[] asteroidCollision(int[] asteroids) {
        LinkedList<Integer> moveRight = new LinkedList<>();
        List<Integer> moveLeft = new ArrayList<>();
        for (int i = 0; i < asteroids.length; i++) {
            int asteroid = asteroids[i];
            if (asteroid > 0) { // move to right
                moveRight.add(asteroid);
            } else { // move to left
                int opponent = 0;
                int absAsteroid = -asteroid;
                while (opponent < absAsteroid && !moveRight.isEmpty()) {
                    opponent = moveRight.pollLast();
                }
                if (opponent > absAsteroid) { // this asteroid to left explods
                    moveRight.add(opponent);
                } else if (opponent < absAsteroid) { // this asteroid distroy all previous right move asteroids and free now
                    moveLeft.add(asteroid);
                }
            }
        }
        int[] res = new int[moveLeft.size() + moveRight.size()];
        int resP = 0;
        for (Integer n : moveLeft) {
            res[resP++] = n;
        }
        for (Integer n : moveRight) {
            res[resP++] = n;
        }
        return res;
    }
}
```

#### 用一个`List`
具体实现上可以把`moveRight`和`moveLeft`合并成一个`List`，
```java
class Solution {
    public int[] asteroidCollision(int[] asteroids) {
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < asteroids.length; i++) {
            int asteroid = asteroids[i];
            if (asteroid > 0) { // keep each right move asteroid
                list.add(asteroid);
            } else { // move to left
                int opponent = 0;
                int absAsteroid = -asteroid;
                while (opponent < absAsteroid && !list.isEmpty() && list.getLast() > 0) {
                    opponent = list.pollLast();
                }
                if (opponent < absAsteroid) { // beats all previous right move asteroid
                    list.add(asteroid);
                } else if (opponent > absAsteroid) {
                    list.add(opponent); // one of previous right move asteroid beats current left move one
                }
            }
        }
        int[] arr = new int[list.size()];
        int arrP = 0;
        for (Integer n : list) {
            arr[arrP++] = n;
        }
        return arr;
    }
}
```

#### 结果
![asteroid-collision-1](/images/leetcode/asteroid-collision-1.png)
