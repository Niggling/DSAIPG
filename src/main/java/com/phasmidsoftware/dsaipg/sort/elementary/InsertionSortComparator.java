/*
 * Copyright (c) 2024. Robin Hillyard
 */
package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.Sort;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.Config_Benchmark;
import java.io.IOException;
import java.util.Comparator;
import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.getRunsConfig;

public class InsertionSortComparator<X> extends SortWithHelper<X> {

    public InsertionSortComparator(Helper<X> helper) {
        super(helper);
    }

    protected InsertionSortComparator(String description, Comparator<X> comparator, int N, int nRuns, Config config) {
        super(description, comparator, N, nRuns, config);
    }

    public InsertionSortComparator(Comparator<X> comparator, int N, int nRuns, Config config) {
        this(DESCRIPTION, comparator, N, nRuns, config);
    }

    public void sort(X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        for (int i = from + 1; i < to; i++) {
            for (int j = i; j > from; j--) {
                if (!helper.swapStableConditional(xs, j)) break;
            }
        }
    }

    public static final String DESCRIPTION = "Insertion sort";

    public static <T extends Comparable<T>> void sort(T[] ts) {
        try (InsertionSortComparator<T> sort = new InsertionSortComparator<>(DESCRIPTION, Comparable::compareTo, ts.length, 1, Config.load(InsertionSortComparator.class))) {
            sort.mutatingSort(ts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Sort<String> stringSorterCaseInsensitive(int n, Config config) {
        return new InsertionSortComparator<>(DESCRIPTION, String.CASE_INSENSITIVE_ORDER, n, getRunsConfig(config), config);
    }

    public static <T> long countInversions(T[] ts, Comparator<T> comparator) {
        final Config config = Config_Benchmark.setupConfigFixes();
        try (InsertionSortComparator<T> sorter = new InsertionSortComparator<>(comparator, ts.length, getRunsConfig(config), config)) {
            Helper<T> helper = sorter.getHelper();
            sorter.sort(ts, true);
            return helper.getFixes();
        }
    }
}
