package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FibonacciHeapTracking<K> {

    private Node<K> maxNode;
    private int nNodes;
    private final Comparator<K> comparator;
    private final boolean max; // true 表示最大堆，false 表示最小堆
    private final int capacity;
    private K bestSpilled = null;

    public FibonacciHeapTracking(int capacity, boolean max, Comparator<K> comparator) {
        this.capacity = capacity;
        this.max = max;
        this.comparator = comparator;
        this.nNodes = 0;
        this.maxNode = null;
    }

    // 比较函数：对于 max 堆，如果 a 大于 b 则返回正数；否则取反
    private int compare(K a, K b) {
        return comparator.compare(a, b) * (max ? 1 : -1);
    }

    public boolean isEmpty() {
        return maxNode == null;
    }

    public int size() {
        return nNodes;
    }

    /**
     * 插入新元素。如果已达到容量，则不插入而记录该元素为 spilled 元素。
     */
    public void insert(K key) {
        if (nNodes == capacity) {
            if (bestSpilled == null || compare(key, bestSpilled) > 0) {
                bestSpilled = key;
            }
            return;
        }
        Node<K> node = new Node<>(key);
        maxNode = mergeLists(maxNode, node);
        nNodes++;
    }

    /**
     * 删除堆顶元素（对于 max 堆即最大值），并整理堆结构。
     */
    public K removeMax() {
        if (maxNode == null) return null;
        Node<K> removed = maxNode;
        if (removed.child != null) {
            // 将所有子节点加入到根链表中
            List<Node<K>> children = new ArrayList<>();
            Node<K> x = removed.child;
            int count = 0;
            // 使用 nNodes 作为安全上限，防止结构异常导致无限循环
            int maxIter = nNodes;
            do {
                children.add(x);
                x = x.right;
                count++;
                if (count > maxIter) {
                    System.err.println("Error: exceeded maximum iteration in child list. count = " + count);
                    break;
                }
            } while (x != removed.child);
            // 对于每个子节点，断开父节点关联并加入根链表
            for (Node<K> child : children) {
                child.parent = null;
                child.left = child;
                child.right = child;
                maxNode = mergeLists(maxNode, child);
            }
        }
        // 将 removed 从根链表中删除
        if (removed == removed.right) { // 如果只有一个节点
            maxNode = null;
        } else {
            removed.left.right = removed.right;
            removed.right.left = removed.left;
            maxNode = removed.right;
            consolidate();
        }
        nNodes--;
        return removed.key;
    }

    /**
     * 整理根链表，将具有相同 degree 的节点合并，更新 maxNode。
     */
    private void consolidate() {
        int arraySize = ((int) Math.floor(Math.log(nNodes) / Math.log(2))) + 2;
        @SuppressWarnings("unchecked")
        Node<K>[] A = (Node<K>[]) new Node[arraySize];
        for (int i = 0; i < arraySize; i++) {
            A[i] = null;
        }
        // 收集当前根链表中的所有节点，防止在合并过程中破坏链表结构
        List<Node<K>> rootList = new ArrayList<>();
        if (maxNode != null) {
            Node<K> x = maxNode;
            do {
                rootList.add(x);
                x = x.right;
            } while (x != maxNode);
        }
        // 合并相同 degree 的节点
        for (Node<K> w : rootList) {
            Node<K> x = w;
            int d = x.degree;
            while (A[d] != null) {
                Node<K> y = A[d];
                if (compare(x.key, y.key) < 0) {
                    Node<K> temp = x;
                    x = y;
                    y = temp;
                }
                heapLink(y, x);
                A[d] = null;
                d++;
            }
            A[d] = x;
        }
        // 重新构建根链表，并找出新的 maxNode
        maxNode = null;
        for (int i = 0; i < arraySize; i++) {
            if (A[i] != null) {
                A[i].left = A[i];
                A[i].right = A[i];
                maxNode = mergeLists(maxNode, A[i]);
            }
        }
    }

    /**
     * 将节点 y 从根链表中移除，并链接为 x 的子节点。
     */
    private void heapLink(Node<K> y, Node<K> x) {
        // 从根链表中删除 y
        y.left.right = y.right;
        y.right.left = y.left;
        // 使 y 成为 x 的子节点
        y.parent = x;
        y.left = y;
        y.right = y;
        x.child = mergeLists(x.child, y);
        x.degree++;
        y.mark = false;
    }

    /**
     * 合并两个循环双向链表 a 和 b，并返回 key 较大者作为根。
     */
    private Node<K> mergeLists(Node<K> a, Node<K> b) {
        if (a == null) return b;
        if (b == null) return a;
        Node<K> aRight = a.right;
        Node<K> bLeft = b.left;
        a.right = b;
        b.left = a;
        aRight.left = bLeft;
        bLeft.right = aRight;
        return (compare(a.key, b.key) >= 0) ? a : b;
    }

    public K getBestSpilled() {
        return bestSpilled;
    }

    /**
     * 内部节点类。构造时将左右指针初始化为指向自身，确保形成单节点循环链表。
     */
    private static class Node<K> {
        K key;
        int degree;
        Node<K> parent;
        Node<K> child;
        Node<K> left;
        Node<K> right;
        boolean mark;

        Node(K key) {
            this.key = key;
            this.degree = 0;
            this.parent = null;
            this.child = null;
            this.mark = false;
            this.left = this;
            this.right = this;
        }
    }
}
