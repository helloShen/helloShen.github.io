---
layout: post
title: "About Union Find"
date: 2017-05-21 21:19:06
author: "Wei SHEN"
categories: ["algorithm"]
tags: ["union find"]
description: >
---

### `Union Find` 解决什么问题？
这题需要解决的核心问题是：**矩阵中，点的动态连通性。** 我有很多的节点（可以是任何事物的抽象），其中一些相邻的点（上下左右）是相互连通的。可以画出下面这张图，
![union-find-1](/images/leetcode/union-find-1.png)

对这样一个数据结构，我们想达到的效果是，能很快地查询出：任意两点是否是相互连通的（换句话说，是否属于同一个分组）。

#### 最朴素的 `Quick Find` 解法，$$O(n^2)$$ 复杂度
解决这个问题最直观的方法叫 **`Quick Find`**。
> 维护每个点所在分组的id。

```java
public class QuickFind {
    private int[] id;
}
```

```
[1,2,3,4,5,6,7,8,9]
```
基本思路就是：初始化的时候，给每个点一个唯一的`id`。然后不断地合并两个不同分组的点。合并的过程，就是比如把`5`号分组全部并入`3`号分组中，就把所有`id=5`的点，全改成`id=3`。
```
经过数次合并后, 2,4被并入3小组, 6,7被并入5小组：
[1,3,3,3,5,5,5,8,9]
```
```
现在要合并`3`和`5`，把5全改成3.
[1,3,3,3,3,3,3,8,9]
```

代码如下：
```java
public void union(int p, int q) {
    int groupPId = find(p);
    int groupQId = find(q);
    if (groupPId == groupQId) { return; }
    // 下面是核心动作: 把所有id=p的点，id改成q
    for (int i = 0; i < id.length; i++) {
        if (id[i] == groupPId) { id[i] = groupQId; }
    }
}
```

之所以叫`Quick Find`，就是这个方法，让`find`方法很快捷，只需要读取每个点对应的小组编号即可：
```java
public int find(int p) {
    return id[p];
}
```

但这个算法的复杂度，在最坏的情况下：$$O(n^2)$$。因为每次调用`union()`方法合并两个分组，都需要完整地遍历矩阵中所有的点。

#### 更好的 `Quick Union` 算法，复杂度 $$O(n^2)$$
更好的做法，
> 不是一定要给相同的分组相同的编号。而是以 **一棵树** 的结构保存一个分组。目的是：让所有属于同一分组的点都拥有同一个根节点。 每个节点中只要保存其某个父节点的编号即可。有点像一个链表。

![union-find-3](/images/leetcode/union-find-3.png)

这样做的好处是：合并两个分组的时候，不需要遍历所有节点，然后改变分组中所有节点的编号。 **只需要改变被吃掉的那个分组的根节点的id即可。**

这里最精妙的一个技巧在于：**怎么区分一个节点是不是根节点？** 在数组中，**如果一个节点的id编号和它的下标相等（id[i] = i），它就是根节点**。


`Union Find`的基本行为基于下面这组`API`:
* find(int p): 找到某个点所属小组的id。
* union(int p, int q): 把两个小组，合并成一个小组。
* connected(int p, int q): 判断两个点是否属于同一小组。

```java
private class UnionFind {
    private int[] board;
    private UnionFind(int size) {
        board = new int[size];
    }
    public int find(int p) {
        while (p != board[p]) {
            p = board[p];
        }
        return p;
    }
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) { return; }
        board[rootP] = board[rootQ];
    }
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }
}
```

但`Quick Union`的方法，最坏情况下，`union()`函数复杂度还是`O(n^2)`。因为`union()`找到树的根节点的快慢取决于树的深度。最坏情况下，树被合并成了一条直线。

![union-find-5](/images/leetcode/union-find-5.jpg)

#### 加权`Quick Union`算法，复杂度 $$O(n\log_{}{n})$$
为了让树被合并成一条直线的情况不在发生，加权`Quick Union`：
> 总是把较小的一棵树合并到较大的那棵树上。

![union-find-6](/images/leetcode/union-find-6.png)

复杂度结论：
> 对于N个节点，加权quick-union算法构造的森林中，任意节点的深度最多为$$\log_{}{N}$$


证明请参见 《算法》第四版P146。

#### 路径压缩
要实现路径压缩：
> 只需要在find()函数中，将路径上遇到的所有节点都直接链接到根节点上。这样可以让树彻底扁平化。

![union-find-7](/images/leetcode/union-find-7.png)

可以用迭代，将查询的节点直接链接到根节点，
```java
public int find(int p) {
    int cur = p;
    while (board[cur] != cur) {
        cur = board[cur];
    }
    board[p] = cur;
    return cur;
}
```
也可以用递归法，将一路上遇到的每一个节点的直接链接到根节点。
```java
public int find(int p) {
    int father = board[p];
    if (father == p) { return p; }
    board[p] = find(father);
}
```

#### 路径压缩后的加权`Quick Union`是最优算法，复杂度很接近但达不到 $$O(n)$$
记住结论：
> 路径压缩后的加权`Quick Union`是最优算法，但还达不到常数级别的复杂度。

![union-find-8](/images/leetcode/union-find-8.png)
