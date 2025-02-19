package com.phasmidsoftware.dsaipg.adt.pq;

import com.phasmidsoftware.dsaipg.adt.pq.BinaryHeapTracking;
import com.phasmidsoftware.dsaipg.adt.pq.DHeapTracking;
import com.phasmidsoftware.dsaipg.adt.pq.FibonacciHeapTracking;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;

public class HeapBenchmark {
    // 堆最大容量
    private static final int M = 4095;
    // 插入总数和删除数
    private static final int INSERTIONS = 16000;
    private static final int REMOVALS = 4000;
    private static final Random rnd = new Random();

    // 一个简单的 Benchmark_Timer 实现（你也可使用自己仓库中的版本）
    public static long benchmark(String desc, Runnable f) {
        long start = System.nanoTime();
        f.run();
        long end = System.nanoTime();
        long duration = end - start;
        System.out.println(desc + " 耗时 " + duration/1e6 + " 毫秒");
        return duration;
    }

    public static void main(String[] args) throws Exception {
        // 使用 Integer 的自然顺序，构造 max 堆
        Comparator<Integer> comparator = Integer::compareTo;
        // 注意：为了比较不同实现，我们传入不同的 floyd 标志

        // 1. 二叉堆，不使用 Floyd trick
        BinaryHeapTracking<Integer> binaryNoFloyd = new BinaryHeapTracking<>(M, true, comparator, false);
        long t1 = benchmark("Binary Heap (No Floyd)", () -> runTest(binaryNoFloyd));

        // 2. 二叉堆，使用 Floyd trick
        BinaryHeapTracking<Integer> binaryFloyd = new BinaryHeapTracking<>(M, true, comparator, true);
        long t2 = benchmark("Binary Heap (Floyd)", () -> runTest(binaryFloyd));

        // 3. 4-叉堆，不使用 Floyd trick
        DHeapTracking<Integer> d4NoFloyd = new DHeapTracking<>(M, 4, true, comparator, false);
        long t3 = benchmark("4-ary Heap (No Floyd)", () -> runTest(d4NoFloyd));

        // 4. 4-叉堆，使用 Floyd trick
        DHeapTracking<Integer> d4Floyd = new DHeapTracking<>(M, 4, true, comparator, true);
        long t4 = benchmark("4-ary Heap (Floyd)", () -> runTest(d4Floyd));

        // 5. Fibonacci Heap (Bonus)
        FibonacciHeapTracking<Integer> fibHeap = new FibonacciHeapTracking<>(M, true, comparator);
        long t5 = benchmark("Fibonacci Heap", () -> runTestFib(fibHeap));

        // 输出各实现溢出元素中优先级最高的记录
        System.out.println("Binary No Floyd spilled: " + binaryNoFloyd.getBestSpilled());
        System.out.println("Binary Floyd spilled: " + binaryFloyd.getBestSpilled());
        System.out.println("4-ary No Floyd spilled: " + d4NoFloyd.getBestSpilled());
        System.out.println("4-ary Floyd spilled: " + d4Floyd.getBestSpilled());
        System.out.println("Fibonacci spilled: " + fibHeap.getBestSpilled());

        // 此处可将 t1...t5 数据写出用于 log-log 绘图
    }

    // 针对 BinaryHeapTracking 和 DHeapTracking 共同的测试方法
    private static void runTest(Object heapObj) {
        // 使用 Consumer<Integer> 接口调用 give() 和 take() 方法
        // 为简化代码，这里假定 heapObj 分别有 give(Integer) 和 take() 方法
        try {
            // 先插入 INSERTIONS 个随机元素
            for (int i = 0; i < INSERTIONS; i++) {
                int val = rnd.nextInt(100000);
                if (heapObj instanceof BinaryHeapTracking) {
                    ((BinaryHeapTracking<Integer>) heapObj).give(val);
                } else if (heapObj instanceof DHeapTracking) {
                    ((DHeapTracking<Integer>) heapObj).give(val);
                }
            }
            // 删除 REMOVALS 个元素
            for (int i = 0; i < REMOVALS; i++) {
                if (heapObj instanceof BinaryHeapTracking) {
                    ((BinaryHeapTracking<Integer>) heapObj).take();
                } else if (heapObj instanceof DHeapTracking) {
                    ((DHeapTracking<Integer>) heapObj).take();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 针对 Fibonacci 堆的测试
    private static void runTestFib(FibonacciHeapTracking<Integer> fibHeap) {
        // 插入 INSERTIONS 个随机元素
        for (int i = 0; i < INSERTIONS; i++) {
            int val = rnd.nextInt(100000);
            fibHeap.insert(val);
        }
        // 删除 REMOVALS 个元素
        for (int i = 0; i < REMOVALS; i++) {
            fibHeap.removeMax();
        }
    }
}
