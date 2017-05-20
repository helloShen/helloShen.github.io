---
layout: post
title: "Leetcode - Algorithm - Surrounded Regions (to be continued...)"
date: 2017-05-16 21:07:33
author: "Wei SHEN"
categories: ["algorithm","leetcode"]
tags: ["depth first search","array","union find"]
level: "medium"
description: >
---

 ### 题目
Given a 2D board containing 'X' and 'O' (the letter O), capture all regions surrounded by 'X'.

A region is captured by flipping all 'O's into 'X's in that surrounded region.

For example,
```
X X X X
X O O X
X X O X
X O X X
```
After running your function, the board should be:
```
X X X X
X X X X
X X X X
X O X X
```

### 笨办法，杀鸡用牛刀

很像围棋的规则。基本思路是：
> 以所有相邻的`O`为小组，如果小组中有一个`O`在最外一圈，则整个小组就没有被抓住。

```
X X X X
X O O X
X X O X
X O X X
```
假设还是上面这个矩阵，如果按部就班地按下面步骤走，肯定能得到解。
1. 找出所有`O`，并记录位置。
2. 把所有`O`按位置是否连在一起，分成两组`O`和`OOO`。
3. 对于任意一组`O`，其中只要有一个`O`在最外一圈，整个一组就是活的，逃跑了。
4. 反之，某一组`O`，如果没有`O`在最外一圈，整个一组被捕获。
5. 记录完所有被捕获的小组，在`board`上把所有捕获的`O`替换成`X`。

#### 代码
```java
public class Solution {
    /**
     * 主入口
     * 1. 找出所有`O`，并记录位置。
     * 2. 把所有`O`按位置是否连在一起，分成两组`O`和`OOO`。
     * 3. 对于任意一组`O`，其中只要有一个`O`在最外一圈，整个一组就是活的，逃跑了。
     * 4. 反之，某一组`O`，如果没有`O`在最外一圈，整个一组被捕获。
     * 5. 记录完所有被捕获的小组，在`board`上把所有捕获的`O`替换成`X`。
     */
    public void solve(char[][] board) {
        if (board.length == 0) { return; }
        List<List<Integer>> captured = new ArrayList<>();
        List<List<List<Integer>>> groups = groups(board);
        for (List<List<Integer>> group : groups) {
            if (!groupIsFree(board.length,board[0].length,group)) {
                captured.addAll(group);
            }
        }
        for (List<Integer> point : captured) {
            board[point.get(0)][point.get(1)] = 'X';
        }
    }
    /**
     * 找出所有连在一起的O小组。
     */
    public List<List<List<Integer>>> groups(char[][] board) {
        List<List<List<Integer>>> groups = new ArrayList<>();
        List<List<Integer>> points = points(board);
        while (!points.isEmpty()) {
            List<List<Integer>> group = new ArrayList<>();
            List<Integer> head = points.remove(0);
            trackGroup(points,head,group);
            groups.add(group);
            for (List<Integer> point : group) {
                points.remove(point);
            }
        }
        return groups;
    }
    /**
     * 筛选出所有的O
     */
    public List<List<Integer>> points(char[][] board) {
        List<List<Integer>> points = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 'O') {
                    points.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{i,j})));
                }
            }
        }
        return points;
    }
    /**
     * 给定board上的一个O，顺藤摸瓜，找出整个相邻O小组。
     */
    public void trackGroup(List<List<Integer>> points, List<Integer> head, List<List<Integer>> group) {
        if (group.contains(head)) { return; }
        group.add(new ArrayList<Integer>(Arrays.asList(new Integer[]{head.get(0),head.get(1)})));
        for (List<Integer> point : points) {
            if (areNeighbours(point.get(0),point.get(1),head.get(0),head.get(1))) {
                trackGroup(points,point,group);
            }
        }
    }
    /**
     * 判定两个点是否相邻。
     */
    public boolean areNeighbours (int rowA, int colA, int rowB, int colB) {
        if (rowA == rowB && Math.abs(colA - colB) == 1) { return true; }
        if (colA == colB && Math.abs(rowA-rowB) == 1) { return true; }
        return false;
    }
    /**
     * 判定整个相邻O小组是否逃脱。
     */
    public boolean groupIsFree(int height, int width, List<List<Integer>> group) {
        for (List<Integer> point : group) {
            if (pointIsFree(height,width,point.get(0),point.get(1))) { return true; }
        }
        return false;
    }
    /**
     * 判定某个点是否逃脱。
     */
    public boolean pointIsFree(int height, int width, int row, int col) {
        return row == 0 || row == height-1 || col == 0 || col == width-1;
    }
}
```

#### 结果
确实能解决问题，但遇到`O`比较多的矩阵，超时比较严重。
![surrounded-regions-1](/images/leetcode/surrounded-regions-1.png)


### 最外圈的`O`点是关键
这题关键在于讲故事的角度。换个角度看问题，可以转化成 **找和最外圈联通的`O`**. 因为：
> 只有和最外圈的`O`连通的`O`才是可以逃脱的。

```
X X X X             X X X X
X O O X     =>      X     X     +       O O
X X O X             X     X             X O
X O X X             X O X X
```

必须先找到最外一圈中的`O`点，然后以这些点为起点，顺藤摸瓜，找出一串相邻的`O`点。


#### 迭代版`dfs()`代码
```java
public class Solution {
    public void solve(char[][] board) {
        if (board.length < 3 || board[0].length < 3) { return; }
        int height = board.length;
        int width = board[0].length;
        // 找最外圈的`O`
        for (int i = 0; i < width-1; i++) { // top line
            if (board[0][i] == 'O') { board[0][i] = 'F'; dfs(board,1,i); }
        }
        for (int i = 0; i < height-1; i++) { // right col
            if (board[i][width-1] == 'O') { board[i][width-1] = 'F'; dfs(board,i,width-2); }
        }
        for (int i = width-1; i > 0; i--) { // buttom line
            if (board[height-1][i] == 'O') { board[height-1][i] = 'F'; dfs(board,height-2,i); }
        }
        for (int i = height-1; i > 0; i--) { // left col
            if (board[i][0] == 'O') { board[i][0] = 'F'; dfs(board,i,1); }
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 'O') { board[i][j] = 'X'; }
                if (board[i][j] == 'F') { board[i][j] = 'O'; }
            }
        }
    }
    // 顺藤摸瓜
    public void dfs(char[][] board, int row, int col) {
        List<Integer> buffer = new LinkedList<>();
        buffer.add(row); buffer.add(col);
        while (!buffer.isEmpty()) {
            int r = buffer.remove(0);
            int c = buffer.remove(0);
            if (r <= 0 || r >= board.length-1 || c <= 0 || c >= board[0].length-1) { continue; } // 注意，最外圈的点都跳过
            if (board[r][c] == 'O') {
                board[r][c] = 'F';
                buffer.add(r-1); buffer.add(c);
                buffer.add(r+1); buffer.add(c);
                buffer.add(r); buffer.add(c-1);
                buffer.add(r); buffer.add(c+1);
            }
        }
    }
}
```
这里有个需要注意的点：**在顺藤摸瓜用`dfs()`探索相邻`O`的时候，可以跳过所有最外圈的点。** 可以尽可能减小复杂度。

#### 结果
时间在`20ms`级别。还不够好。
![surrounded-regions-2](/images/leetcode/surrounded-regions-2.png)

#### 递归版`dfs()`
这里必须用迭代`dfs()`的原因是：**递归版的`dfs()`探索函数容易导致栈溢出。因为需要以整个`char[][]`数组作为函数的参数之一。**

如果在递归的时候跳过所有最外圈的点的话，可以侥幸通过。大多数情况可能是栈溢出的。但如果内存足够大的情况下，递归的做法速度快很多，基本在`5ms`级别。因为都是直接操作数组，不需要用容器。

```java
public class Solution {
    public void solve(char[][] board) {
        if (board.length < 3 || board[0].length < 3) { return; }
        int height = board.length;
        int width = board[0].length;
        for(int i=0;i<height;i++) {
            if(board[i][0]=='O') { board[i][0]='F'; dfs(board,i,1); }
            if(board[i][width-1]=='O') { board[i][width-1]='F'; dfs(board,i,width-2); }
        }
        for(int i=0;i<width;i++) {
            if(board[0][i]=='O') { board[0][i]='F'; dfs(board,1,i); }
            if(board[height-1][i]=='O') { board[height-1][i]='F'; dfs(board,height-2,i); }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (board[i][j] == 'O') { board[i][j] = 'X'; }
                if (board[i][j] == 'F') { board[i][j] = 'O'; }
            }
        }
    }
    public void dfs(char[][] board, int row, int col) {
        if(row>=board.length-1 || row<=0 || col>=board[0].length-1 || col<=0) { return; } // 外圈和越界都不允许
        if(board[row][col]=='O') {
            board[row][col]='F';
            dfs(board,row+1,col);
            dfs(board,row,col+1);
            dfs(board,row-1,col);
            dfs(board,row,col-1);
        }
    }
}
```

#### 结果
这基本是这道题最快的方法了。虽然容易栈溢出。
![surrounded-regions-3](/images/leetcode/surrounded-regions-3.png)


### `Union Find` 的解法
给出下面这样的矩阵，把所有相邻的`O`点看成一组，给`O`点分组，有一个专门的算法叫：**`Union Find`**。
```
X X X X
X O O X
X X O X
X O X X
```

`Union Find`的基本行为基于下面这组`API`:
* find(int p): 找到某个点所属小组的id。
* union(int p, int q): 把两个小组，合并成一个小组。
* connected(int p, int q): 判断两个点是否属于同一小组。

基本思路（待补充）。

#### 代码
下面的代码，完整地实现了一个朴素的`UnionFind`类型。这里的`UnionFind`是基于一个`Point[]`数组。`Point`是表示二维坐标的数据结构。
```java
public class Solution {
    /**
     * 为了比较两个Point的值，需要加equals()和hashCode()方法
     */
    private static class Point {
        private int row;
        private int col;
        private Point(int row, int col) { this.row = row; this.col = col; }
        public boolean equals(Object obj) {
            if (! (obj instanceof Point)) { return false; }
            Point p = (Point)obj;
            return row == p.row && col == p.col;
        }
        public int hashCode() {
            return (17 + row) * 31 + col;
        }
    }
    /**
     * 不需要换算，直接用Point做数据结构。
     * 默认值为null。
     */
    private static class UnionFind {
        private Point[][] board;
        private UnionFind(int height, int width) {
            board = new Point[height][width];
        }
        /**
         * 找到P点的根节点。注意，没有做路径压缩。
         */
        public Point find(Point p) {
            do {
                p = board[p.row][p.col];
            } while (!board[p.row][p.col].equals(p)); // 根节点指向自己
            return p;
        }
        /**
         * P是新点，接在老树Q后面
         */
        public void follow(Point p, Point q) {
            board[p.row][p.col] = new Point(q.row,q.col);
        }
        /**
         * 把P变成Q的子树。没有做深度平衡。
         */
        public void union(Point p, Point q) {
            Point rootP = find(p);
            Point rootQ = find(q);
            board[rootP.row][rootP.col] = new Point(rootQ.row,rootQ.col);
        }
        /**
         * 判断P点和Q点是否属于同一棵树
         */
        public boolean connected(Point p, Point q) {
            return find(p).equals(find(q));
        }
        /**
         * 创建一棵以p为根的新树
         */
        public void create(Point p) {
            board[p.row][p.col] = new Point(p.row,p.col);
        }
    }
    /**
     * 朴素的 Union Find 解法
     */
    public void solve(char[][] board) {
        if (board.length < 3 || board[0].length < 3) { return; }
        if (!checkOuterRing(board)) {
            allCaptured(board);
            return;
        }
        int height = board.length;
        int width = board[0].length;
        UnionFind uf = new UnionFind(height,width);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (board[i][j] == 'O') {
                    Point p = new Point(i,j);
                    if (i > 0 && board[i-1][j] == 'O' && j > 0 && board[i][j-1] == 'O') { // 需要union()两棵树
                        uf.follow(p,new Point(i-1,j)); // 先把当前点接到楼上节点后面
                        uf.union(p,new Point(i,j-1)); // 再union()两棵老树
                    } else if (i > 0 && board[i-1][j] == 'O') { // follow()楼上点
                        uf.follow(p,new Point(i-1,j));
                    } else if (j > 0 && board[i][j-1] == 'O') { // follow()左边点
                        uf.follow(p,new Point(i,j-1));
                    } else { // 建一棵新树
                        uf.create(p);
                    }
                }
            }
        }
        Set<Point> outerRing = getOuterRing(uf);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (board[i][j] == 'O' && !outerRing.contains(uf.find(uf.board[i][j]))) {
                    board[i][j] = 'X';
                }
            }
        }
    }
    public Set<Point> getOuterRing(UnionFind uf) {
        Set<Point> res = new HashSet<>();
        int height = uf.board.length;
        int width = uf.board[0].length;
        for (int i = 0; i < width-1; i++) { // top line
            if (uf.board[0][i] != null) { res.add(uf.find(uf.board[0][i])); }
        }
        for (int i = 0; i < height-1; i++) { // right col
            if (uf.board[i][width-1] != null) { res.add(uf.find(uf.board[i][width-1])); }
        }
        for (int i = width-1; i > 0; i--) { // buttom line
            if (uf.board[height-1][i] != null) { res.add(uf.find(uf.board[height-1][i])); }
        }
        for (int i = height-1; i > 0; i--) { // left col
            if (uf.board[i][0] != null) { res.add(uf.find(uf.board[i][0])); }
        }
        return res;
    }
    public boolean checkOuterRing(char[][] board) {
        int height = board.length;
        int width = board[0].length;
        for (int i = 0; i < width-1; i++) { // top line
            if (board[0][i] == 'O') { return true; }
        }
        for (int i = 0; i < height-1; i++) { // right col
            if (board[i][width-1] == 'O') { return true; }
        }
        for (int i = width-1; i > 0; i--) { // buttom line
            if (board[height-1][i] == 'O') { return true; }
        }
        for (int i = height-1; i > 0; i--) { // left col
            if (board[i][0] == 'O') { return true; }
        }
        return false;
    }
    public void allCaptured(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = 'X';
            }
        }
    }
}
```

#### 结果
朴素的`Union Find`是通不过的。主要是因为树的深度太深。
![surrounded-regions-4](/images/leetcode/surrounded-regions-4.png)

### 优化`UnionFind`
优化`UnionFind`主要有两个手段，
1. 深度平衡
2. 路径压缩

待补充这部分。

这里，深度平衡的效果不太明显。但路径压缩非常有效。使用了路径压缩的版本就能通过车测试。
```java
public class Solution {
    /**
     * 为了比较两个Point的值，需要加equals()和hashCode()方法
     */
    private static class Point {
        private int row;
        private int col;
        private Point(int row, int col) { this.row = row; this.col = col; }
        public boolean equals(Object obj) {
            if (! (obj instanceof Point)) { return false; }
            Point p = (Point)obj;
            return row == p.row && col == p.col;
        }
        public int hashCode() {
            return (17 + row) * 31 + col;
        }
    }
    /**
     * 优化的 Union Find
     * 即是深度平衡，又做了路径压缩。
     */
    private static class UnionFind {
        private Point[][] board;
        private int[][] sz;
        private UnionFind(int height, int width) {
            board = new Point[height][width];
            sz = new int[height][width];
        }
        /**
         * 找到P点的根节点
         */
        public Point find(Point p) {
            Point cur = p;
            do {
                cur = board[cur.row][cur.col];
            } while (!board[cur.row][cur.col].equals(cur)); // 根节点指向自己
            board[p.row][p.col] = cur; // 路径压缩
            return cur;
        }
        /**
         * P是新点，接在老树Q后面
         */
        public void follow(Point p, Point q) {
            board[p.row][p.col] = q;
            Point rootQ = find(q);
            sz[rootQ.row][rootQ.col]++;
        }
        /**
         * 把P变成Q的子树。用了深度平衡优化。
         */
        public void union(Point p, Point q) {
            Point rootP = find(p);
            Point rootQ = find(q);
            if (rootP.equals(rootQ)) { return; }
            int sizeP = sz[rootP.row][rootP.col];
            int sizeQ = sz[rootQ.row][rootQ.col];
            int sum = sizeP + sizeQ;
            // 深度平衡：把较小的树嫁接到较大的树的根节点，以减小树的深度。
            if (sizeP >= sizeQ) { // q树较小，把q树接到p树的根
                board[rootQ.row][rootQ.col] = rootP;
                sz[rootP.row][rootP.col] = sum;
            } else { // p树较小，把p树接到q树的根。
                board[rootP.row][rootP.col] = rootQ;
                sz[rootQ.row][rootQ.col] = sum;
            }
        }
        /**
         * 判断P点和Q点是否属于同一棵树
         */
        public boolean connected(Point p, Point q) {
            return find(p).equals(find(q));
        }
        /**
         * 创建一棵以p为根的新树
         */
        public void create(Point p) {
            board[p.row][p.col] = new Point(p.row,p.col);
            sz[p.row][p.col] = 1;
        }
    }
    /**
     * 可以灵活更换不同的UnionFind组件。
     */
    public void solve(char[][] board) {
        if (board.length < 3 || board[0].length < 3) { return; }
        if (!checkOuterO(board)) {
            allCaptured(board);
            return;
        }
        int height = board.length;
        int width = board[0].length;
        // 可以选用不同的UnionFind实现类。比较效率。
        UnionFind uf = new UnionFind(height,width);
        unionO(board,uf);
        Set<Point> outerO = getOuterO(board,uf);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (board[i][j] == 'O') {
                    if (!outerO.contains(uf.find(new Point(i,j)))) {
                        board[i][j] = 'X';
                    }
                }
            }
        }
    }
    public void unionO(char[][] board, UnionFind uf) {
        int height = board.length;
        int width = board[0].length;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (board[i][j] == 'O') {
                    Point p = new Point(i,j);
                    if (i > 0 && board[i-1][j] == 'O' && j > 0 && board[i][j-1] == 'O') { // 需要union()两棵树
                        uf.follow(p,new Point(i-1,j)); // 先把当前点接到楼上节点后面
                        uf.union(p,new Point(i,j-1)); // 再union()两棵老树
                    } else if (i > 0 && board[i-1][j] == 'O') { // follow()楼上点
                        uf.follow(p,new Point(i-1,j));
                    } else if (j > 0 && board[i][j-1] == 'O') { // follow()左边点
                        uf.follow(p,new Point(i,j-1));
                    } else { // 建一棵新树
                        uf.create(p);
                    }
                }
            }
        }
    }
    public Set<Point> getOuterO(char[][] board, UnionFind uf) {
        Set<Point> res = new HashSet<>();
        int height = board.length;
        int width = board[0].length;
        for (int i = 0; i < width-1; i++) { // top line
            if (board[0][i] == 'O') { res.add(uf.find(new Point(0,i))); }
        }
        for (int i = 0; i < height-1; i++) { // right col
            if (board[i][width-1] == 'O') { res.add(uf.find(new Point(i,width-1))); }
        }
        for (int i = width-1; i > 0; i--) { // buttom line
            if (board[height-1][i] == 'O') { res.add(uf.find(new Point(height-1,i))); }
        }
        for (int i = height-1; i > 0; i--) { // left col
            if (board[i][0] == 'O') { res.add(uf.find(new Point(i,0))); }
        }
        return res;
    }
    /**
     * 如果最外圈没有存活的O，就不用麻烦用Union Find了，直接全部填满X，然后返回。
     */
    public boolean checkOuterO(char[][] board) {
        int height = board.length;
        int width = board[0].length;
        for (int i = 0; i < width-1; i++) { // top line
            if (board[0][i] == 'O') { return true; }
        }
        for (int i = 0; i < height-1; i++) { // right col
            if (board[i][width-1] == 'O') { return true; }
        }
        for (int i = width-1; i > 0; i--) { // buttom line
            if (board[height-1][i] == 'O') { return true; }
        }
        for (int i = height-1; i > 0; i--) { // left col
            if (board[i][0] == 'O') { return true; }
        }
        return false;
    }
    /**
     * 直接填满X
     */
    public void allCaptured(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = 'X';
            }
        }
    }
}
```

#### 结果
![surrounded-regions-5](/images/leetcode/surrounded-regions-5.png)


### 不使用完整的`UnionFind`类
如果不定义一个完整的`UnionFind`。而是用一个`int[]`嵌入函数当中，用两个子程序`union()`和`find()`模拟`UnionFind`的行为，可以优化效率。

#### 代码
```java
public class Solution {
    /**
     * 不用数据结构UnionFind。直接用局部变量数组。
     */
    public void solve(char[][] board) {
        if (board.length < 3 || board[0].length < 3) { return; }
        List<Integer> outerO = collectOuterOWithInt(board);
        if (outerO.isEmpty()) { allCaptured(board); return; }
        int height = board.length;
        int width = board[0].length;
        // 用数组代替UnionFind类型
        int[] uf = new int[width*height];
        for (int i = 0; i < height; i++) { // Union Find 生成树
            for (int j = 0; j < width; j++) {
                if (board[i][j] == 'O') {
                    int index = i * width + j;
                    if (i > 0 && board[i-1][j] == 'O' && j > 0 && board[i][j-1] == 'O') { // 需要union()两棵树
                        uf[index] = index - width;
                        union(uf,index,index-1); // 再union()两棵老树
                    } else if (i > 0 && board[i-1][j] == 'O') { // follow()楼上点
                        uf[index] = index - width;
                    } else if (j > 0 && board[i][j-1] == 'O') { // follow()左边点
                        uf[index] = index - 1;
                    } else { // 建一棵新树
                        uf[index] = index;
                    }
                }
            }
        }
        Set<Integer> outerRoot = new HashSet<>();
        for (Integer p : outerO) { outerRoot.add(find(uf,p)); }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (board[i][j] == 'O') {
                    int index = i * width + j;
                    if (!outerRoot.contains(find(uf,index))) {
                        board[i][j] = 'X';
                    }
                }
            }
        }
    }
    public int find(int[] board, int p) {
        int cur = p;
        do {
            cur = board[cur];
        } while (board[cur] != cur);
        board[p] = cur; // 路径压缩
        return cur;
    }
    public void union(int[] board, int p, int q) {
        int rootP = find(board,p);
        int rootQ = find(board,q);
        if (rootP == rootQ) { return; } // 已经属于同一棵树
        board[rootP] = rootQ;
    }
    public List<Integer> collectOuterOWithInt(char[][] board) {
        int height = board.length;
        int width = board[0].length;
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < width-1; i++) { // top line
            if (board[0][i] == 'O') { res.add(i); }
        }
        for (int i = 0; i < height-1; i++) { // right col
            if (board[i][width-1] == 'O') { res.add(i*width+width-1); }
        }
        for (int i = width-1; i > 0; i--) { // buttom line
            if (board[height-1][i] == 'O') { res.add((height-1)*width+i); }
        }
        for (int i = height-1; i > 0; i--) { // left col
            if (board[i][0] == 'O') { res.add(i*width); }
        }
        return res;
    }
    /**
     * 直接填满X
     */
    public void allCaptured(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = 'X';
            }
        }
    }
}
```

#### 结果
![surrounded-regions-6](/images/leetcode/surrounded-regions-6.png)


### 用另外的一个数组`boolean[] isEdge`记录是否逃脱
这样就不用到最后做完`UnionFind`归并之后，再判断是否逃脱。记录工作在用`UnionFind`的过程中就完成了。
```java
public class Solution {
    // 数组拿出来，防止递归过深
    private int[] uf;
    private boolean[] isEdge;
    /**
     * 不用数据结构UnionFind。直接用局部变量数组。
     * 而且也不重新遍历外圈。在union的时候，统一处理外圈信息。
     */
    public void solve(char[][] board) {
        if (board.length < 3 || board[0].length < 3) { return; }
        int height = board.length;
        int width = board[0].length;
        int size = width * height;
        // 用数组代替UnionFind类型
        uf = new int[size];
        isEdge = new boolean[size];
        for (int row = 0; row < height; row++) { // Union Find 生成树
            for (int col = 0; col < width; col++) {
                int i = row * width + col;
                if (board[row][col] == 'O') {
                    uf[i] = i;
                    if ((row==0) || (row==height-1) || (col==0) || (col==width-1)) { isEdge[i] = true; } //标记是否外圈
                    if (row > 0 && board[row-1][col] == 'O') { follow(i,i-width); } // 简单follow()楼上
                    if (col > 0 && board[row][col-1] == 'O') { union(i,i-1); } // 左邻
                }
            }
        }
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int i = row * width + col;
                if (board[row][col] == 'O' && !isEdge[find(i)]) {
                    board[row][col] = 'X';
                }
            }
        }
    }
    // 递归版。优点是能将路过的所有点都压缩路径。
    public int find(int p) {
        if (uf[p] == p) { return p; }
        uf[p] = find(uf[p]);
        return uf[p];
    }
    public void follow(int p, int q) { // p点简单follow() q树
        uf[p] = uf[q];
        int rootQ = find(q);
        isEdge[rootQ] = isEdge[p] || isEdge[rootQ];
    }
    public void union(int p, int q) { // p树嫁接到q树
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) { return; } // 已经属于同一棵树
        uf[rootP] = rootQ;
        isEdge[rootQ] = isEdge[rootP] || isEdge[rootQ];
    }
}
```

#### 结果
至此基本就是`UnionFind`方法能达到的比较理想的速度了。基本在`10ms`级别。
![surrounded-regions-6](/images/leetcode/surrounded-regions-6.png)
