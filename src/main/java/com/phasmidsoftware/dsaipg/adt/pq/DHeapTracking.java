package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;
import java.util.function.BiPredicate;

public class DHeapTracking<K> {
    protected final int d;           // 分叉数（此处 d=4）
    protected final boolean max;
    protected final Comparator<K> comparator;
    protected final K[] heap;
    protected int size;
    protected final int offset;      // 数组起始索引，一般为 1
    protected final boolean floyd;   // 是否使用 Floyd trick

    // 溢出跟踪字段
    private K bestSpilled = null;

    @SuppressWarnings("unchecked")
    public DHeapTracking(int capacity, int d, boolean max, Comparator<K> comparator, boolean floyd) {
        this.d = d;
        this.max = max;
        this.comparator = comparator;
        this.offset = 1;
        this.heap = (K[]) new Object[capacity + offset];
        this.size = 0;
        this.floyd = floyd;
    }

    public int capacity() {
        return heap.length - offset;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void give(K key) {
        if (size == capacity()) {
            // 当满时，溢出：丢弃最后一个元素
            K spilled = heap[size + offset - 1];
            if (bestSpilled == null || compare(spilled, bestSpilled) > 0) {
                bestSpilled = spilled;
            }
            size--; // 删除末尾元素
        }
        heap[++size + offset - 1] = key;
        swimUp(size + offset - 1);
    }

    public K take() throws Exception {
        if (isEmpty()) throw new Exception("Heap is empty");
        K top = heap[offset];
        heap[offset] = heap[size + offset - 1];
        heap[size + offset - 1] = null;
        size--;
        if (floyd) snake(offset);
        else sink(offset);
        return top;
    }

    protected int compare(K a, K b) {
        return comparator.compare(a, b) * (max ? 1 : -1);
    }

    // 判断是否需要交换
    private boolean unordered(int i, int j) {
        return compare(heap[i], heap[j]) < 0;
    }

    private void swap(int i, int j) {
        K tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    // 父节点索引：对于节点 i，父节点为 (i - offset - 1)/d + offset
    private int parent(int i) {
        return (i - offset - 1) / d + offset;
    }

    // 第一个子节点索引： d * (i - offset) + offset + 1
    private int firstChild(int i) {
        return d * (i - offset) + offset + 1;
    }

    private int doHeapify(int i) {
        while (firstChild(i) <= size + offset - 1) {
            int best = firstChild(i);
            // 遍历所有 d 个子节点
            for (int j = 1; j < d; j++) {
                int child = firstChild(i) + j;
                if (child <= size + offset - 1 && unordered(best, child)) {
                    best = child;
                }
            }
            if (compare(heap[i], heap[best]) >= 0) break;
            swap(i, best);
            i = best;
        }
        return i;
    }

    private void sink(int i) {
        doHeapify(i);
    }

    private void swimUp(int i) {
        while (i > offset && unordered(parent(i), i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    private void snake(int i) {
        int finalIndex = doHeapify(i);
        swimUp(finalIndex);
    }

    public K getBestSpilled() {
        return bestSpilled;
    }
}
