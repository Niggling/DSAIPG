package com.phasmidsoftware.dsaipg.adt.pq;

import com.phasmidsoftware.dsaipg.adt.pq.BinaryHeapTracking;
import com.phasmidsoftware.dsaipg.adt.pq.DHeapTracking;
import com.phasmidsoftware.dsaipg.adt.pq.FibonacciHeapTracking;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HeapBenchmarkUsingTimer {

    // Heap maximum capacity
    private static final int M = 4095;
    // Number of insertions and removals
    private static final int INSERTIONS = 16000;
    private static final int REMOVALS = 4000;
    private static final Random rnd = new Random();

    // Test for Binary Heap (for BinaryHeapTracking type)
    private static void runTestBinary(BinaryHeapTracking<Integer> heap) {
        // Insert INSERTIONS random numbers
        for (int i = 0; i < INSERTIONS; i++) {
            heap.give(rnd.nextInt(100000));
        }
        // Remove REMOVALS top elements
        for (int i = 0; i < REMOVALS; i++) {
            try {
                heap.take();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Test for 4-ary Heap (for DHeapTracking type)
    private static void runTestDHeap(DHeapTracking<Integer> heap) {
        for (int i = 0; i < INSERTIONS; i++) {
            heap.give(rnd.nextInt(100000));
        }
        for (int i = 0; i < REMOVALS; i++) {
            try {
                heap.take();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Test for Fibonacci Heap (for FibonacciHeapTracking type)
    private static void runTestFibonacci(FibonacciHeapTracking<Integer> heap) {
        for (int i = 0; i < INSERTIONS; i++) {
            heap.insert(rnd.nextInt(100000));
        }
        for (int i = 0; i < REMOVALS; i++) {
            heap.removeMax();
        }
    }

    public static void main(String[] args) {
        Comparator<Integer> comparator = Integer::compareTo;

        // Note: Each test returns a heap instance to query its bestSpilled field after the test.
        // Define Supplier and Consumer for Benchmark_Timer.
        int runs = 10; // Adjust the number of runs as needed

        // --- Binary Heap (No Floyd) ---
        Supplier<BinaryHeapTracking<Integer>> supplierBinaryNoFloyd =
                () -> new BinaryHeapTracking<>(M, true, comparator, false);
        Consumer<BinaryHeapTracking<Integer>> testBinary = heap -> runTestBinary(heap);
        Benchmark_Timer<BinaryHeapTracking<Integer>> timerBinaryNoFloyd =
                new Benchmark_Timer<>("Binary Heap (No Floyd)", testBinary);
        double timeBinaryNoFloyd = timerBinaryNoFloyd.runFromSupplier(supplierBinaryNoFloyd, runs);
        // Create a separate heap instance to get bestSpilled
        BinaryHeapTracking<Integer> binaryNoFloydInstance = supplierBinaryNoFloyd.get();
        runTestBinary(binaryNoFloydInstance);
        System.out.println("Binary Heap (No Floyd) average time: " + timeBinaryNoFloyd + " ms");
        System.out.println("Binary Heap (No Floyd) spilled: " + binaryNoFloydInstance.getBestSpilled());

        // --- Binary Heap (Floyd) ---
        Supplier<BinaryHeapTracking<Integer>> supplierBinaryFloyd =
                () -> new BinaryHeapTracking<>(M, true, comparator, true);
        Benchmark_Timer<BinaryHeapTracking<Integer>> timerBinaryFloyd =
                new Benchmark_Timer<>("Binary Heap (Floyd)", testBinary);
        double timeBinaryFloyd = timerBinaryFloyd.runFromSupplier(supplierBinaryFloyd, runs);
        BinaryHeapTracking<Integer> binaryFloydInstance = supplierBinaryFloyd.get();
        runTestBinary(binaryFloydInstance);
        System.out.println("Binary Heap (Floyd) average time: " + timeBinaryFloyd + " ms");
        System.out.println("Binary Heap (Floyd) spilled: " + binaryFloydInstance.getBestSpilled());

        // --- 4-ary Heap (No Floyd) ---
        Supplier<DHeapTracking<Integer>> supplierDHeapNoFloyd =
                () -> new DHeapTracking<>(M, 4, true, comparator, false);
        Consumer<DHeapTracking<Integer>> testDHeap = heap -> runTestDHeap(heap);
        Benchmark_Timer<DHeapTracking<Integer>> timerDHeapNoFloyd =
                new Benchmark_Timer<>("4-ary Heap (No Floyd)", testDHeap);
        double timeDHeapNoFloyd = timerDHeapNoFloyd.runFromSupplier(supplierDHeapNoFloyd, runs);
        DHeapTracking<Integer> dHeapNoFloydInstance = supplierDHeapNoFloyd.get();
        runTestDHeap(dHeapNoFloydInstance);
        System.out.println("4-ary Heap (No Floyd) average time: " + timeDHeapNoFloyd + " ms");
        System.out.println("4-ary Heap (No Floyd) spilled: " + dHeapNoFloydInstance.getBestSpilled());

        // --- 4-ary Heap (Floyd) ---
        Supplier<DHeapTracking<Integer>> supplierDHeapFloyd =
                () -> new DHeapTracking<>(M, 4, true, comparator, true);
        Benchmark_Timer<DHeapTracking<Integer>> timerDHeapFloyd =
                new Benchmark_Timer<>("4-ary Heap (Floyd)", testDHeap);
        double timeDHeapFloyd = timerDHeapFloyd.runFromSupplier(supplierDHeapFloyd, runs);
        DHeapTracking<Integer> dHeapFloydInstance = supplierDHeapFloyd.get();
        runTestDHeap(dHeapFloydInstance);
        System.out.println("4-ary Heap (Floyd) average time: " + timeDHeapFloyd + " ms");
        System.out.println("4-ary Heap (Floyd) spilled: " + dHeapFloydInstance.getBestSpilled());

        // --- Fibonacci Heap (Bonus) ---
        Supplier<FibonacciHeapTracking<Integer>> supplierFibonacci =
                () -> new FibonacciHeapTracking<>(M, true, comparator);
        Consumer<FibonacciHeapTracking<Integer>> testFibonacci = heap -> runTestFibonacci(heap);
        Benchmark_Timer<FibonacciHeapTracking<Integer>> timerFibonacci =
                new Benchmark_Timer<>("Fibonacci Heap", testFibonacci);
        double timeFibonacci = timerFibonacci.runFromSupplier(supplierFibonacci, runs);
        FibonacciHeapTracking<Integer> fibonacciInstance = supplierFibonacci.get();
        runTestFibonacci(fibonacciInstance);
        System.out.println("Fibonacci Heap average time: " + timeFibonacci + " ms");
        System.out.println("Fibonacci Heap spilled: " + fibonacciInstance.getBestSpilled());

    }
}
