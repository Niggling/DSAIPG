package com.phasmidsoftware.dsaipg.sort;

import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparator;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;

import java.util.Random;

public class BenchmarkInsertionSort {
    public static void main(String[] args) {
        int base = 1000;
        int trials = 10;
        System.out.println("Random:");
        for (int i = 0; i < 5; i++) {
            int n = base << i;
            Benchmark_Timer<Integer[]> timer = new Benchmark_Timer<>("InsertionSort", a -> InsertionSortComparator.sort(a));
            double time = timer.runFromSupplier(() -> generateRandomArray(n), trials);
            System.out.println("n=" + n + " time=" + time + " ms");
        }
        System.out.println("Ordered:");
        for (int i = 0; i < 5; i++) {
            int n = base << i;
            Benchmark_Timer<Integer[]> timer = new Benchmark_Timer<>("InsertionSort", a -> InsertionSortComparator.sort(a));
            double time = timer.runFromSupplier(() -> generateOrderedArray(n), trials);
            System.out.println("n=" + n + " time=" + time + " ms");
        }
        System.out.println("Partially Ordered:");
        for (int i = 0; i < 5; i++) {
            int n = base << i;
            Benchmark_Timer<Integer[]> timer = new Benchmark_Timer<>("InsertionSort", a -> InsertionSortComparator.sort(a));
            double time = timer.runFromSupplier(() -> generatePartiallyOrderedArray(n), trials);
            System.out.println("n=" + n + " time=" + time + " ms");
        }
        System.out.println("Reverse Ordered:");
        for (int i = 0; i < 5; i++) {
            int n = base << i;
            Benchmark_Timer<Integer[]> timer = new Benchmark_Timer<>("InsertionSort", a -> InsertionSortComparator.sort(a));
            double time = timer.runFromSupplier(() -> generateReverseOrderedArray(n), trials);
            System.out.println("n=" + n + " time=" + time + " ms");
        }
    }

    private static Integer[] generateRandomArray(int n) {
        Random rand = new Random();
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = rand.nextInt(n);
        }
        return arr;
    }

    private static Integer[] generateOrderedArray(int n) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }
        return arr;
    }

    private static Integer[] generateReverseOrderedArray(int n) {
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = n - i;
        }
        return arr;
    }

    private static Integer[] generatePartiallyOrderedArray(int n) {
        Integer[] arr = generateOrderedArray(n);
        Random rand = new Random();
        int swaps = n / 10;
        for (int i = 0; i < swaps; i++) {
            int idx1 = rand.nextInt(n);
            int idx2 = rand.nextInt(n);
            int temp = arr[idx1];
            arr[idx1] = arr[idx2];
            arr[idx2] = temp;
        }
        return arr;
    }
}
