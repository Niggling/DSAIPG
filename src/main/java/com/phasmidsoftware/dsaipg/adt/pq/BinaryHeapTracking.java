package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class BinaryHeapTracking<K> implements Iterable<K> {
    // 修改为 protected 以便子类访问
    protected final boolean max;
    protected final int first;
    protected final Comparator<K> comparator;
    protected final K[] binHeap;
    protected int last; // 当前堆中元素数
    protected final boolean floyd; // 是否使用 Floyd trick

    // 用于跟踪溢出（被丢弃）的元素中优先级最高的那个
    private K bestSpilled = null;

    @SuppressWarnings("unchecked")
    public BinaryHeapTracking(int n, int first, boolean max, Comparator<K> comparator, boolean floyd) {
        this.max = max;
        this.first = first;
        this.comparator = comparator;
        this.last = 0;
        this.binHeap = (K[]) new Object[n + first];
        this.floyd = floyd;
    }

    // 辅助构造器
    public BinaryHeapTracking(int n, boolean max, Comparator<K> comparator, boolean floyd) {
        this(n, 1, max, comparator, floyd);
    }

    public boolean isEmpty() {
        return last == 0;
    }

    public int size() {
        return last;
    }

    // 返回容量（不含保留位）
    public int capacity() {
        return binHeap.length - first;
    }

    /**
     * 插入新元素。如果堆已满，则先将溢出的元素记录下来（如果其优先级高于当前记录）。
     */
    public void give(K key) {
        if (last == capacity()) {
            // 溢出：根据原逻辑，溢出时直接 last--
            K spilled = binHeap[last + first - 1]; // 溢出位置上的元素
            // 更新 bestSpilled：假设 max 堆，比较数值大小；若为 min 堆则反过来
            if (bestSpilled == null || compare(spilled, bestSpilled) > 0) {
                bestSpilled = spilled;
            }
            last--; // “丢弃”末尾元素
        }
        binHeap[++last + first - 1] = key;
        swimUp(last + first - 1);
    }

    /**
     * 删除堆顶元素，并调整堆结构。根据 floyd 标志选择使用 sink 或 snake。
     */
    public K take() throws Exception {
        if (isEmpty()) throw new Exception("Priority queue is empty");
        K result = binHeap[first];
        swap(first, last-- + first - 1);
        if (floyd) snake(first);
        else sink(first);
        binHeap[last + first] = null; // 避免对象游离
        return result;
    }

    // 比较方法：对于 max 堆，如果 a 大于 b 则返回正数；min 堆则相反。
    protected int compare(K a, K b) {
        return (comparator.compare(a, b)) * (max ? 1 : -1);
    }

    void sink(int k) {
        doHeapify(k, (a, b) -> !unordered(a, b));
    }

    void snake(int k) {
        swimUp(doHeapify(k, (a, b) -> !unordered(a, b)));
    }

    int doHeapify(int k, BiPredicate<Integer, Integer> p) {
        int i = k;
        while (firstChild(i) <= last + first - 1) {
            int j = firstChild(i);
            if (j < last + first - 1 && unordered(j, j + 1)) j++;
            if (p.test(i, j)) break;
            swap(i, j);
            i = j;
        }
        return i;
    }

    void swimUp(int k) {
        int i = k;
        while (i > first && unordered(parent(i), i)) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    boolean unordered(int i, int j) {
        return (comparator.compare(binHeap[i], binHeap[j]) > 0) ^ max;
    }

    private void swap(int i, int j) {
        K tmp = binHeap[i];
        binHeap[i] = binHeap[j];
        binHeap[j] = tmp;
    }

    private int parent(int k) {
        return (k + 1 - first) / 2 + first - 1;
    }

    private int firstChild(int k) {
        return (k + 1 - first) * 2 + first - 1;
    }

    public K getBestSpilled() {
        return bestSpilled;
    }

    @Override
    public Iterator<K> iterator() {
        Collection<K> copy = new ArrayList<>(Arrays.asList(Arrays.copyOf(binHeap, last + first)));
        Iterator<K> result = copy.iterator();
        if (first > 0 && result.hasNext()) result.next();
        return result;
    }
}
