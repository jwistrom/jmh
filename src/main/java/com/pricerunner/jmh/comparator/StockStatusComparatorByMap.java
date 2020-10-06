package com.pricerunner.jmh.comparator;

import java.util.Comparator;
import java.util.Map;

import static com.pricerunner.jmh.comparator.StockStatus.IN_STOCK;
import static com.pricerunner.jmh.comparator.StockStatus.OUT_OF_STOCK;
import static com.pricerunner.jmh.comparator.StockStatus.PREORDER;
import static com.pricerunner.jmh.comparator.StockStatus.SPECIAL_ORDER;
import static com.pricerunner.jmh.comparator.StockStatus.UNKNOWN;

public class StockStatusComparatorByMap implements Comparator<StockStatus> {

    public static final StockStatusComparatorByMap INSTANCE = new StockStatusComparatorByMap();

    private static final Map<StockStatus, Integer> SORTING_ORDER = Map.of(
        IN_STOCK, 0,
        PREORDER, 1,
        SPECIAL_ORDER, 2,
        OUT_OF_STOCK, 3,
        UNKNOWN, 4
    );

    @Override
    public int compare(final StockStatus o1, final StockStatus o2) {
        if (o1 == o2) {
            return 0;
        }

        return Integer.compare(SORTING_ORDER.get(o1), SORTING_ORDER.get(o2));
    }
}
