/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

final class ParSort {

    /**
     * cutoff：如果待排序数组长度小于此值，则直接顺序排序。
     */
    public static int cutoff = 900000;

    /**
     * 对数组 array[from, to) 进行排序。
     * 如果子数组长度小于 cutoff，则采用顺序排序；否则使用递归并行排序。
     *
     * @param array 要排序的数组
     * @param from  起始索引（包含）
     * @param to    结束索引（不包含）
     */
    public static void sort(int[] array, int from, int to) {
        if (to - from < cutoff) {
            Arrays.sort(array, from, to);
        } else {
            // 初始递归深度：例如使用 commonPool 并行度，取对数计算一个合理的深度
            int defaultDepth = (int) (Math.log(ForkJoinPool.getCommonPoolParallelism()) / Math.log(2));
            // 调用递归排序方法
            int[] sorted = sortRecursive(array, from, to, defaultDepth);
            System.arraycopy(sorted, 0, array, from, sorted.length);
        }
    }

    /**
     * 递归排序 array[from, to)，并返回一个新的已排序数组。
     * 使用 depth 参数控制并行递归层数：当 depth <= 0 或数组大小小于 cutoff 时，转为顺序排序。
     *
     * @param array 要排序的数组
     * @param from  起始索引（包含）
     * @param to    结束索引（不包含）
     * @param depth 当前允许的最大并行递归层数
     * @return 新的已排序数组
     */
    static int[] sortRecursive(int[] array, int from, int to, int depth) {
        if ((to - from) < cutoff || depth <= 0) {
            // 当数组区间较小或达到递归深度限制时，直接复制子数组并进行顺序排序
            int[] result = Arrays.copyOfRange(array, from, to);
            Arrays.sort(result);
            return result;
        } else {
            int mid = (from + to) / 2;
            // 异步递归排序左半部分和右半部分，递归深度减 1
            CompletableFuture<int[]> leftFuture = CompletableFuture.supplyAsync(
                    () -> sortRecursive(array, from, mid, depth - 1)
            );
            CompletableFuture<int[]> rightFuture = CompletableFuture.supplyAsync(
                    () -> sortRecursive(array, mid, to, depth - 1)
            );
            // 合并两个排序结果
            return leftFuture.thenCombine(rightFuture, ParSort::doMerge).join();
        }
    }

    /**
     * 合并两个已排序的数组为一个新的已排序数组。
     *
     * @param xs1 第一个排序数组
     * @param xs2 第二个排序数组
     * @return 合并后的新数组
     */
    static int[] doMerge(int[] xs1, int[] xs2) {
        int[] result = new int[xs1.length + xs2.length];
        int i = 0, j = 0;
        for (int k = 0; k < result.length; k++) {
            if (i >= xs1.length) result[k] = xs2[j++];
            else if (j >= xs2.length) result[k] = xs1[i++];
            else if (xs2[j] < xs1[i]) result[k] = xs2[j++];
            else result[k] = xs1[i++];
        }
        return result;
    }

    /**
     * 异步排序 array[from, to) 的一个辅助方法，
     * 如果需要使用自定义线程池，可以在此处传入自己的 ForkJoinPool。
     *
     * @param array 要排序的数组
     * @param from  起始索引（包含）
     * @param to    结束索引（不包含）
     * @return 包含排序结果的 CompletableFuture
     */
    static CompletableFuture<int[]> asyncSort(int[] array, int from, int to) {
        // 如需自定义线程数，可使用如下方式：
        // ForkJoinPool myPool = new ForkJoinPool(customThreadCount);
        // return CompletableFuture.supplyAsync(() -> sortRecursive(array, from, to, defaultDepth), myPool);
        return CompletableFuture.supplyAsync(() -> sortRecursive(array, from, to,
                (int) (Math.log(ForkJoinPool.getCommonPoolParallelism()) / Math.log(2))
        ));
    }
}
