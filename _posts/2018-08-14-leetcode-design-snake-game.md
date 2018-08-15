---
layout: post
title: "Leetcode - Algorithm - Design Snake Game "
date: 2018-08-14 17:09:27
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["linked list"]
level: "medium"
description: >
---

### 题目

Design a Snake game that is played on a device with screen size = width x height. Play the game online if you are not familiar with the game.

The snake is initially positioned at the top left corner (0,0) with length = 1 unit.

You are given a list of food's positions in row-column order. When a snake eats the food, its length and the game's score both increase by 1.

Each food appears one by one on the screen. For example, the second food will not appear until the first food was eaten by the snake.

When a food does appear on the screen, it is guaranteed that it will not appear on a block occupied by the snake.

Example:
```
Given width = 3, height = 2, and food = [[1,2],[0,1]].

Snake snake = new Snake(width, height, food);

Initially the snake appears at position (0,0) and the food at (1,2).

|S| | |
| | |F|

snake.move("R"); -> Returns 0

| |S| |
| | |F|

snake.move("D"); -> Returns 0

| | | |
| |S|F|

snake.move("R"); -> Returns 1 (Snake eats the first food and right after that, the second food appears at (0,1) )

| |F| |
| |S|S|

snake.move("U"); -> Returns 1

| |F|S|
| | |S|

snake.move("L"); -> Returns 2 (Snake eats the second food)

| |S|S|
| | |S|

snake.move("U"); -> Returns -1 (Game over because snake collides with border)
```

### 贪吃蛇游戏
核心就是用`LinkedList`模拟贪吃蛇。

#### 代码
下面这个代码的优点是，
1. 子函数很短小并且低耦合。每个函数都负责很小的一件事。
2. 扩展性强。因为重要的抽象已经有了，未来增强功能只需要做局部组件更新。
3. 可读性强。`move()`函数基本和说话一样。

缺点是还是有点把问题复杂化了，
1. 食物因为被蛇遮挡暂时“挂起”是没有必要的。因为蛇咬到自己就会死，食物在那儿它也吃不到。
2. 重新定义`Position`数据结构不是必要的。二维的点可以用乘法转换成一维数组的点。为了用`List`或`Set`需要重写`equals()`和`hashCode()`有点浪费。

有时间可以在写一个简化版。

```java
class SnakeGame {

    /** Initialize your data structure here.
        @param width - screen width
        @param height - screen height
        @param food - A list of food positions
        E.g food = [[1,1], [1,0]] means the first food is positioned at [1,1], the second is at [1,0]. */
    public SnakeGame(int width, int height, int[][] food) {
        //测试很中二，行列定义是反的
        this.height = height;   //width代表行数
        this.width = width;     //height代表列数
        snake = new LinkedList<Position>();
        snake.add(new Position(0,0));
        this.food = food;
        this.nextFood = 0;
        dead = false;
        score = 0;
    }

    /** Moves the snake.
        @param direction - 'U' = Up, 'L' = Left, 'R' = Right, 'D' = Down
        @return The game's score after the move. Return -1 if game over.
        Game over when snake crosses the screen boundary or bites its body. */
    public int move(String direction) {
        if (dead) { return -1; }
        int[] next = nextPas(direction);
        if (die(next)) {
            dead = true;
            return -1;
        }
        if (findFood(next)) {
            eat();
            newFood();
            ++score;
        } else {
            moveOn(next);
        }
        return score;
    }


    /** ===================================== 【以下为私有】 ============================================*/


    private int width;               //地图宽度
    private int height;              //地图高度
    private Deque<Position> snake;   //模拟蛇
    private int score;               //成功吃掉多少个食物
    private int nextFood;            //指向food中的下一个食物。如果食物更新位置被蛇身占住，则暂时挂起（负数）。
    private int[][] food;            //食物列表
    private boolean dead;            //记录蛇是否已死

    //通过方向词，算出下一步要走的位置
    //测试行列定义是反的：
    //  width代表行数
    //  height代表列数
    private int[] nextPas(String direction) {
        Position head = snake.peekFirst();
        int[] headArray = new int[]{head.getWidth(), head.getHeight()};
        if (direction == null) {
            return headArray;
        }
        //测试很中二，行列定义是反的
        switch (direction) {
            case "U":
                headArray[0]--; break;  //往“上”: width-1
            case "D":
                headArray[0]++; break;  //往“下”: width+1
            case "L":
                headArray[1]--; break;  //往“左”: height-1
            case "R":
                headArray[1]++; break;  //往“右”: height+1
            default:
                break;
        }
        return headArray;
    }

    //判断这一步蛇会不会死。
    //两种情况会死：
    //  1. 超出屏幕
    //  2. 咬到自己身体
    private boolean die(int[] next) {
        if (next[0] < 0 || next[0] >= height || next[1] < 0 || next[1] >= width) {
            return true;
        } //出界
        Position nextPos = new Position(next[0],next[1]);
        if (snake.contains(nextPos) && !nextPos.equals(snake.peekLast())) {
            return true;
        } //咬到自己（下一步正好是自己的尾巴是允许的，因为位置正好会空出来）
        return false;
    }

    //判定本轮蛇是否吃到食物
    private boolean findFood(int[] next) {
        if (foodSuspended()) { return false; }
        return (nextFood < food.length &&       //先得有食物
                next[0] == food[nextFood][0]) && (next[1] == food[nextFood][1]);
    }

    //蛇吃掉食物，本轮长大一格
    private void eat() {
        snake.offerFirst(new Position(food[nextFood][0],food[nextFood][1]));
    }

    //蛇直接前进一格
    //如果下个新食物的位置被蛇占住了，每次moveOn()都重新调用newFood()试图更新食物
    private void moveOn(int[] next) {
        snake.offerFirst(new Position(next[0],next[1]));
        snake.pollLast();
        if (foodSuspended()) { //检查被挡住的食物能更新了吗
            newFood();
        }
    }

    //如果食物被蛇身挡住，食物指针为负数
    private boolean foodSuspended() {
        return nextFood < 0;
    }

    //放新食物。nextFood指针超出food数组末尾，说明食物放完。
    //如果下一个新食物的位置被蛇占据了，就暂时不更新下一个食物，并将nextFood取负数。
    private void newFood() {
        //如果食物因为被蛇身占了位置无法跟新，重新检查现在蛇身是否还挡住食物更新位置。
        if (foodSuspended()) {
            Position nextFoodPos = new Position(food[nextFood][0], food[nextFood][1]);
            if (!snake.contains(nextFoodPos) || nextFoodPos.equals(snake.peekLast())) {
                activateFood();
            }
        //正常更新食物
        } else {
            if (nextFood >= food.length) { return; } //食物吃完了
            nextFood++;
            if (nextFood < food.length && snake.contains(new Position(food[nextFood][0], food[nextFood][1]))) {
                suspendFood();
            }
        }
    }

    //挂起食物。将下个食物指针取反，并-1（保证是负数）
    private void suspendFood() {
        nextFood = -Math.abs(nextFood) - 1;
    }

    //取消食物挂起。先将指针+1，再取绝对值
    private void activateFood() {
        nextFood = Math.abs(nextFood+1);
    }

    //模拟地图上点位置
    private class Position {
        private int width;  //行数
        private int height; //列数
        private int hash;   //散列值
        private Position(int width, int height) {
            this.width = width;
            this.height = height;
            hash = 0;
        }
        private int getWidth() {
            return width;
        }
        private int getHeight() {
            return height;
        }
        public String toString() {
            return "[" + width + "," + height + "]";
        }
        //为了比较LinkedList里Position元素，必须重写equals()和hashCode()函数
        @Override
        public boolean equals(Object another) {
            return (this.width == ((Position)another).width) && (this.height == ((Position)another).height);
        }
        @Override
        public int hashCode() {
            if (hash != 0) { return hash; }
            return 31 * (width+1) + (height+1);
        }
    }
}

/**
 * Your SnakeGame object will be instantiated and called as such:
 * SnakeGame obj = new SnakeGame(width, height, food);
 * int param_1 = obj.move(direction);
 */
```

#### 结果
![design-snake-game-1](/images/leetcode/design-snake-game-1.png)


### 简化版
如上所述，主要做了2个优化，
1. 去掉`suspend`这个多余过程
2. 去掉`Position`这个多余数据结构

#### 代码
```java
class SnakeGame {

        //2-D: [row,col]
        //1-D: row * width + col + 1
        public SnakeGame(int width, int height, int[][] food) {
            this.width = width;     //width代表列数
            this.height = height;   //height代表行数
            snake = new LinkedList<Integer>();
            snake.add(1);
            this.food = food;
            this.nextFood = 0;
            dead = false;
            score = 0;
        }
        public int move(String direction) {
            if (dead) { return -1; }
            //定位下一步
            int head = snake.peekFirst();
            int row = (head - 1) / width;
            int col = (head - 1) % width;
            switch (direction) {
                case "U":
                    row--; break;
                case "D":
                    row++; break;
                case "L":
                    col--; break;
                case "R":
                    col++; break;
                default:
                    break;
            }
            int next = row * width + col + 1;
            //先排除死局
            int tail = snake.pollLast(); //刚好咬尾巴不会死
            if (row < 0 || row >= height || col < 0 || col >= width || snake.contains(next)) {
                dead = true;
                return -1;
            }
            //吃到食物
            if (nextFood < food.length && row == food[nextFood][0] && col == food[nextFood][1]) {
                nextFood++;
                score++;
                snake.offerLast(tail);  //吃到食物不用剪尾巴
            }
            snake.offerFirst(next);
            return score;
        }

        private int width;               //列数
        private int height;              //行数
        private Deque<Integer> snake;    //模拟蛇
        private int nextFood;            //指向food中的下一个食物坐标
        private int[][] food;            //食物坐标列表
        private int score;               //成功吃掉多少个食物
        private boolean dead;            //记录蛇是否已死

}
```

#### 结果
![design-snake-game-2](/images/leetcode/design-snake-game-2.png)
